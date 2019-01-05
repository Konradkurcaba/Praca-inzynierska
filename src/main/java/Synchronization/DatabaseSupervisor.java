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

import pl.kurcaba.FileServer;


public class DatabaseSupervisor {

	private String DATABASE_URL = "jdbc:h2:~/synch";
	private String CREATE_FILE_DATA_TABLE = "CREATE TABLE sync_file_data(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(255) NOT NULL,key VARCHAR(255) NOT NULL"
			+ ",size VARCHAR(255) NOT NULL,date VARCHAR(255) NOT NULL,file_server VARCHAR(255) NOT NULL,bucket_name VARCHAR(255),region VARCHAR(255))\r\n";
	private String CREATE_SYNC_INFO_TABLE = "CREATE TABLE sync_info_table(source_id INT NOT NULL ,dest_id INT NOT NULL"
			+ ",FOREIGN KEY (source_id) REFERENCES sync_file_data(id),FOREIGN KEY (dest_id) REFERENCES sync_file_data(id))\r\n";
	private String CREATE_S3_ACCOUNTS_TABLE = "CREATE TABLE s3_accounts(id INT PRIMARY KEY,path VARCHAR(255))";
	private String CREATE_DRIVE_ACCOUNTS_TABLE = "CREATE TABLE drive_accounts(id INT PRIMARY KEY,name VARCHAR(80))";
	private String CREATE_APP_CONFIG_TABLE = "CREATE TABLE app_config(sync_status BOOL NOT NULL"
			+ ",drive_default_account INT,s3_default_account INT,FOREIGN KEY (drive_default_account)\r\n" + 
			" REFERENCES drive_accounts(id),FOREIGN KEY(s3_default_account) REFERENCES s3_accounts(id))";
	
	private Connection connection;
	
	public DatabaseSupervisor() throws SQLException {
		connectToDatabase();
	}
	
	public void closeConnection() throws SQLException
	{
		connection.close();
	}
	
	public void saveSyncData(SyncFileData aSource, SyncFileData aTarget) throws SQLException
	{
		int sourceId = putFileData(aSource);
		int targetId = putFileData(aTarget);
		
		String query = "INSERT INTO sync_info_table VALUES (?,?)";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setInt(1, sourceId);
		prepStmt.setInt(2, targetId);
		prepStmt.executeUpdate();
	}
	
	public Map<SyncFileData,SyncFileData> getSyncMap() throws SQLException
	{
		Map<SyncFileData,SyncFileData> syncMap = new HashMap();
		String query = "SELECT * FROM sync_info_table";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
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
		
		deleteFile(sourceId);
		deleteFile(targetId);
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
	
	public List<String> getDriveAccounts() throws SQLException
	{
		String sql = "SELECT name FROM drive_accounts";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		List<String> accountList = new ArrayList();
		while(rs.next())
		{
			accountList.add(rs.getString(1));
		}
		return accountList;
	}
	
	public List<String> getS3Accounts()
	{
		return null;
	}
	
	private int getFileId(SyncFileData aFile) throws SQLException
	{
		String query;
		if(aFile.getFileServer() == FileServer.Amazon)
		{
			query = "SELECT id FROM sync_file_data WHERE key = ? AND bucket_name = ? AND region = ?";
		}else
		{
			query = "SELECT id FROM sync_file_data WHERE key = ? AND bucket_name IS NULL AND region IS NULL";
		}
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, aFile.getFileId());
		if(aFile.getFileServer() == FileServer.Amazon)
		{
			S3SyncFileData s3File = (S3SyncFileData) aFile;
			stmt.setString(2, s3File.getBucketName());
			stmt.setString(3, s3File.getRegion());
		}
		
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt(1);
		} else throw new NoSuchElementException("Row doesn't exist in database");
		
	}
	
	private void deleteFile(int fileId) throws SQLException
	{
		String sql ="DELETE FROM sync_file_data WHERE id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setInt(1, fileId);
		prepStmt.executeUpdate();
	}
	
	private boolean FileDataExist(SyncFileData aFileData) throws SQLException
	{
		String query = "SELECT * FROM sync_file_data WHERE key=?";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1,aFileData.getFileId());
		ResultSet rs = prepStmt.executeQuery();
		if(rs.next())
		{
			if(aFileData.getFileServer() != FileServer.Amazon)
			{
				return true;
			}else
			{
				String bucketName = rs.getString(6);	
				String region = rs.getString(7);
				S3SyncFileData s3Data = (S3SyncFileData) aFileData;
				return s3Data.getBucketName().equals(bucketName) && s3Data.getRegion().equals(region);
			}
		}else return false;
	}
	
	private int putFileData(SyncFileData aFileData) throws SQLException
	{

		String query = "INSERT INTO sync_file_data(name,key,size,date,file_server,bucket_name,region)"
				+ " VALUES ( ?,?,?,?,?,?,? )";
		PreparedStatement prepStmt = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
		prepStmt.setString(1, aFileData.getFileName());
		prepStmt.setString(2, aFileData.getFileId());
		prepStmt.setString(3, aFileData.getLastSize());
		prepStmt.setString(4, aFileData.getLastModifyDate());
		prepStmt.setString(5, aFileData.getFileServer().toString());
		
		if(aFileData.getFileServer() == FileServer.Amazon)
		{
			prepStmt.setString(6, ((S3SyncFileData)aFileData).getBucketName());
			prepStmt.setString(7, ((S3SyncFileData)aFileData).getRegion());
		}
		else
		{
			prepStmt.setNull(6, java.sql.Types.NULL );
			prepStmt.setNull(7, java.sql.Types.NULL );
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
		stmt.executeUpdate(CREATE_FILE_DATA_TABLE);
		stmt.executeUpdate(CREATE_SYNC_INFO_TABLE);
		stmt.executeUpdate(CREATE_S3_ACCOUNTS_TABLE);
		stmt.executeUpdate(CREATE_DRIVE_ACCOUNTS_TABLE);
		stmt.executeUpdate(CREATE_APP_CONFIG_TABLE);
		String configRecord = "INSERT INTO app_config VALUES (FALSE,NULL,NULL)";
		stmt.executeUpdate(configRecord);
		connection.close();
	}
	
	private SyncFileData getFileData(int aId) throws SQLException
	{
		String query = "SELECT * FROM sync_file_data WHERE id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setInt(1, aId);
		ResultSet rs = prepStmt.executeQuery();
		if(rs.next())
		{
			if(rs.getString(6).equals("Amazon"))
			{
				return new S3SyncFileData(rs.getString(2), rs.getString(3),rs.getString(4),rs.getString(6),rs.getString(7),rs.getString(8));
			}
			else
			{
				return new SyncFileData(rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6));
			}
		}else throw new NoSuchElementException("Row doesn't exist in database");
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
