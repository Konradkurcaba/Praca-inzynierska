package Synchronization;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.amazonaws.regions.Regions;

import AmazonS3.AmazonAccountInfo;
import pl.kurcaba.ApplicationConfig;
import pl.kurcaba.ApplicationConfiguration;
import pl.kurcaba.FileServer;

public class DatabaseSupervisor {

	private String DATABASE_URL = "jdbc:h2:~/synch;AUTO_SERVER=TRUE";
	private String CREATE_FILE_DATA_TABLE = "CREATE TABLE sync_file_data(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255) NOT NULL,key VARCHAR(255) NOT NULL"
			+ ",size VARCHAR(255) NOT NULL,date VARCHAR(255) NOT NULL,bucket_name VARCHAR(255),account_name VARCHAR(60)"
			+ ",FOREIGN KEY (account_name) REFERENCES accounts(name))";
	private String CREATE_ACCOUNTS_TABLE = "CREATE TABLE accounts(name VARCHAR(60) PRIMARY KEY,"
			+ "file_server VARCHAR(60) NOT NULL,access_key VARCHAR(60),secret_key VARCHAR(60),region VARCHAR(60))";
	private String CREATE_SYNC_INFO_TABLE = "CREATE TABLE sync_info_table(source_id INT NOT NULL,dest_id INT NOT NULL"
			+ ",FOREIGN KEY (source_id) REFERENCES sync_file_data(id),FOREIGN KEY (dest_id) REFERENCES sync_file_data(id))";
	private String CREATE_APP_CONFIG_TABLE = "CREATE TABLE app_config(id INT AUTO_INCREMENT PRIMARY KEY"
			+ ",drive_default_account VARCHAR(60),s3_default_account VARCHAR(60),FOREIGN KEY (drive_default_account)\r\n" + 
			" REFERENCES accounts(name),FOREIGN KEY(s3_default_account) REFERENCES accounts(name))";
	
	private Connection connection;
	
	public DatabaseSupervisor() throws SQLException {
		connectToDatabase();
	}
	
	public void closeConnection() throws SQLException
	{
		connection.close();
	}
	
	public ApplicationConfiguration getAppConfig() throws SQLException
	{
		String sql = "SELECT drive_default_account,s3_default_account FROM app_config";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if(rs.next())
		{
			String googleAccountName = rs.getString(1);
			String amazonAccountName = rs.getString(2);
			AmazonAccountInfo amazonAccount = null;
			if(amazonAccountName != null)
			{
				try
				{
				amazonAccount = getAmazonAccountByName(amazonAccountName);
				}catch(NoSuchElementException ex)
				{
					amazonAccount = null;
				}
			}
			ApplicationConfiguration config = new ApplicationConfiguration(googleAccountName,amazonAccount);
			
			return config;
		}
		else return null;
		
	}
	
	public void saveSyncData(SyncFileData aSource, SyncFileData aTarget) throws SQLException
	{
		int sourceId = putFileData(aSource);
		int targetId = putFileData(aTarget);	
		
		String query = "INSERT INTO sync_info_table (source_id,dest_id) VALUES (?,?)";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setInt(1, sourceId);
		prepStmt.setInt(2, targetId);
		prepStmt.executeUpdate();
	}
	
	public Map<SyncFileData,SyncFileData> getSyncMap(String aGoogleAccount,String aAmazonAccount) throws SQLException
	{
		Map<SyncFileData,SyncFileData> syncMap = new HashMap();
		String query = "SELECT source_id,dest_id\r\n" + 
				"FROM SYNC_INFO_TABLE\r\n" + 
				"LEFT JOIN SYNC_FILE_DATA AS source ON source_id = source.id \r\n" + 
				"LEFT JOIN SYNC_FILE_DATA AS dest ON dest_id = dest.id\r\n" + 
				"WHERE  (source.account_name= ? OR source.account_name= ? OR source.account_name IS NULL ) "
				+ "AND ( dest.account_name= ? OR  dest.account_name= ? OR dest.account_name IS NULL )";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1, aGoogleAccount);
		prepStmt.setString(2, aAmazonAccount);
		prepStmt.setString(3, aAmazonAccount);
		prepStmt.setString(4, aGoogleAccount);
		ResultSet rs = prepStmt.executeQuery();
		while(rs.next())
		{
			SyncFileData sourceData = getFileData(rs.getInt(1));
			SyncFileData destData = getFileData(rs.getInt(2));
			syncMap.put(sourceData, destData);
		}
		return syncMap;	
	}
	
	public void removeSyncData(SyncFileData aSource,SyncFileData aTarget) throws SQLException
	{
		int sourceId = getFileId(aSource);
		int targetId = getFileId(aTarget);
		
		String sql = "DELETE FROM sync_info_table WHERE source_id = ? AND dest_id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setInt(1, sourceId);
		prepStmt.setInt(2, targetId);
		prepStmt.executeUpdate();
		try
		{
			deleteFile(sourceId);
		}catch(Exception ex)
		{
			System.out.println("Cannot delete file from database");
		}
		try
		{
			deleteFile(targetId);
		}catch(Exception ex)
		{
			System.out.println("Cannot delete file from database");
		}
		
	}
	
	public void updateFileKey(String aOldKey,String aNewKey) throws SQLException
	{
		String sql = "UPDATE sync_file_data SET key= ? WHERE key= ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1, aNewKey);
		prepStmt.setString(2, aOldKey);
		prepStmt.executeUpdate();
	}
	
	public boolean checkWhetherFileIsSyncTarget(SyncFileData aFile) throws SQLException
	{
		try 
		{
			int Id = getFileId(aFile);
			String sql = "SELECT * FROM sync_info_table where dest_id = ? ";
			PreparedStatement prepStmt = connection.prepareStatement(sql);
			prepStmt.setInt(1,Id);
			ResultSet rs = prepStmt.executeQuery();
			if(rs.next()) return true;
			else return false;
		}catch(NoSuchElementException ex)
		{
			return false;
		}
	}
	
	public void cleanDatabase() throws SQLException
	{
		String sql = "DROP ALL OBJECTS";
		Statement st = connection.createStatement();
		st.executeUpdate(sql);
		createTables();
	}
	
	public List<String> getDriveAccounts() throws SQLException
	{
		String sql = "SELECT name FROM accounts WHERE file_server = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, FileServer.GoogleDrive.toString());
		ResultSet rs = stmt.executeQuery();
		List<String> accountList = new ArrayList();
		while(rs.next())
		{
			accountList.add(rs.getString(1));
		}
		return accountList;
	}
	
	public void putGoogleAccount(String aAccountName) throws SQLException
	{
		String sql = "INSERT INTO accounts(name,file_server) VALUES (?,?)";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1, aAccountName);
		prepStmt.setString(2, FileServer.GoogleDrive.toString());
		prepStmt.executeUpdate();
	}
	
	public void putAmazonAccount(AmazonAccountInfo aAmazonAccount) throws SQLException
	{
		String sql = "INSERT INTO accounts(name,file_server,access_key,secret_key,region) VALUES(?,?,?,?,?)";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1,aAmazonAccount.getAccountName() );
		prepStmt.setString(2, FileServer.AmazonS3.toString());
		prepStmt.setString(3, aAmazonAccount.getAccessKey());
		prepStmt.setString(4, aAmazonAccount.getSecretKey());
		prepStmt.setString(5, aAmazonAccount.getRegion().getName());
		prepStmt.executeUpdate();
	}
	
	public void deleteGoogleAccount(String aAccountName) throws SQLException
	{
		String sql = "DELETE FROM accounts WHERE name = ? AND file_server = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1, aAccountName);
		prepStmt.setString(2, FileServer.GoogleDrive.toString());
		prepStmt.executeUpdate();
	}
	
	public void updateDefaultDriveAccount(String aGoogleAlias) throws SQLException
	{
		String sql = "UPDATE app_config SET drive_default_account = ? WHERE id=1";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1,aGoogleAlias);
		prepStmt.executeUpdate();
	}
	
	public void updateDefaultAmazonAccount(AmazonAccountInfo aAccount) throws SQLException
	{
		String sql = "UPDATE app_config SET s3_default_account = ? WHERE id=1";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1,aAccount.getAccountName());
		prepStmt.executeUpdate();
	}

	public List<AmazonAccountInfo> getS3Accounts() throws SQLException
	{
		List<AmazonAccountInfo> accounts = new ArrayList<>();
		String sql = "SELECT name,access_key,secret_key,region FROM accounts WHERE file_server = '?'";
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);
		while(rs.next())
		{
			String name = rs.getString(1);
			String access_key = rs.getString(2);
			String secret_key = rs.getString(3);
			Regions region = Regions.fromName(rs.getString(4));
			accounts.add(new AmazonAccountInfo(name, access_key, secret_key,region));
		}
		return accounts;
	}
	
	private int getFileId(SyncFileData aFile) throws SQLException
	{
		String query;
		if(aFile.getFileServer() == FileServer.AmazonS3)
		{
			query = "SELECT id FROM sync_file_data WHERE key = ? AND account_name = ? AND bucket_name = ? ";
		}else if(aFile.getFileServer() == FileServer.Komputer)
		{
			query = "SELECT id FROM sync_file_data WHERE key = ? AND account_name IS NULL AND bucket_name IS NULL ";
		}
		else
		{
			query = "SELECT id FROM sync_file_data WHERE key = ? AND account_name = ? AND bucket_name IS NULL";
		}
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, aFile.getFileId());
		if(aFile.getFileServer() != FileServer.Komputer) stmt.setString(2, aFile.getAccountName());
		if(aFile.getFileServer() == FileServer.AmazonS3)
		{
			S3SyncFileData s3File = (S3SyncFileData) aFile;
			stmt.setString(3, s3File.getBucketName());
		}
		
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt(1);
		} else throw new NoSuchElementException("Row doesn't exist in database");
		
	}
	
	private AmazonAccountInfo getAmazonAccountByName(String aName) throws SQLException
	{
		String sql = "SELECT name,access_key,secret_key,region FROM accounts WHERE name = ? AND file_server = ? ";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setString(1, aName);
		prepStmt.setString(2, FileServer.AmazonS3.toString());
		ResultSet rs = prepStmt.executeQuery();
		if(rs.next())
		{
			Regions region = Regions.fromName(rs.getString(4).toLowerCase());
			return new AmazonAccountInfo(rs.getString(1), rs.getString(2), rs.getString(3),region);
		}
		else throw new NoSuchElementException("Row with given id doesn't exist");
	}
	
	private void deleteFile(int fileId) throws SQLException
	{
		String sql ="DELETE FROM sync_file_data WHERE id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setInt(1, fileId);
		prepStmt.executeUpdate();
	}
	
	private int putFileData(SyncFileData aFileData) throws SQLException
	{

		String query = "INSERT INTO sync_file_data(name,key,size,date,account_name,bucket_name)"
				+ " VALUES ( ?,?,?,?,?,? )";
		PreparedStatement prepStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
		prepStmt.setString(1, aFileData.getFileName());
		prepStmt.setString(2, aFileData.getFileId());
		prepStmt.setString(3, aFileData.getLastSize());
		prepStmt.setString(4, aFileData.getLastModifyDate());
		prepStmt.setString(5, aFileData.getAccountName());
		
		if(aFileData.getFileServer() == FileServer.AmazonS3)
		{
			prepStmt.setString(6, ((S3SyncFileData)aFileData).getBucketName());
		}
		else
		{
			prepStmt.setNull(6, java.sql.Types.NULL );
		}
		prepStmt.executeUpdate();
		
		ResultSet rs = prepStmt.getGeneratedKeys();
		boolean insertSuccessful = rs.next();
		if(insertSuccessful)
		{
			int id = rs.getInt(1);
			return id;
		}else throw new SQLException("Insert into table failed");
	}
	
	private void createTables() throws SQLException
	{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(CREATE_ACCOUNTS_TABLE);
		stmt.executeUpdate(CREATE_FILE_DATA_TABLE);
		stmt.executeUpdate(CREATE_SYNC_INFO_TABLE);
		stmt.executeUpdate(CREATE_APP_CONFIG_TABLE);
		String configRecord = "INSERT INTO app_config VALUES (1,NULL,NULL)";
		stmt.executeUpdate(configRecord);
	}
	
	private SyncFileData getFileData(int aId) throws SQLException
	{
		String query = "SELECT sync_file_data.name,key,size,date,bucket_name,account_name FROM sync_file_data  WHERE id = ?;";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setInt(1, aId);
		ResultSet rs = prepStmt.executeQuery();
		if(rs.next())
		{
			String fileName = rs.getString(1);
			String fileKey = rs.getString(2);
			String size = rs.getString(3);
			String date = rs.getString(4);
			String bucketName = rs.getString(5);
			String conectedAccountName = rs.getString(6);
			
			if(conectedAccountName == null)
			{
				return new SyncFileData(fileName,fileKey,size,date,FileServer.Komputer,conectedAccountName);
			}else
			{
				String getFileServerQuery = "SELECT file_server FROM accounts WHERE name = ?";
				prepStmt = connection.prepareStatement(getFileServerQuery);
				prepStmt.setString(1, conectedAccountName);
				rs = prepStmt.executeQuery();
				FileServer server = null;
				if(rs.next())
				{
					server = FileServer.valueOf(rs.getString(1));
				}else throw new NoSuchElementException("Row doesn't exist");
			
				if(server.equals(FileServer.AmazonS3))
				{
					return new S3SyncFileData(fileName,fileKey,size,date,bucketName,conectedAccountName);
				}
				else
				{
					return new SyncFileData(fileName,fileKey,size,date,server,conectedAccountName);
				}
			}
		}
		else throw new NoSuchElementException("Row doesn't exist in database");
	}
	
	private final void connectToDatabase() throws SQLException
	{
		connection = DriverManager.getConnection(DATABASE_URL);
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = dbm.getTables(null,null,"SYNC_FILE_DATA",null);
		boolean tableExist = tables.next();
		if(!tableExist)
		{
			 createTables();
		}
	}
	
	
	
	
}
