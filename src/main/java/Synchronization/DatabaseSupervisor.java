package Synchronization;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import pl.kurcaba.FileServer;


public class DatabaseSupervisor {

	private String DATABASE_URL = "jdbc:h2:~/synch";
	private String CREATE_FILE_DATA_TABLE = "CREATE TABLE sync_file_data(id INT PRIMARY KEY,key VARCHAR(255) NOT NULL"
			+ ",size VARCHAR(255) NOT NULL,date VARCHAR(255) NOT NULL,file_server VARCHAR(255) NOT NULL,bucket_name VARCHAR(255),region VARCHAR(255))\r\n";
	private String CREATE_SYNC_INFO_TABLE = "CREATE TABLE sync_info_table(source_id INT NOT NULL ,dest_id INT NOT NULL"
			+ ",FOREIGN KEY (source_id) REFERENCES sync_file_data(id),FOREIGN KEY (dest_id) REFERENCES sync_file_data(id))\r\n";
	
	private Connection connection;
	
	public DatabaseSupervisor() throws SQLException {
		connectToDatabase();
	}
	
	public void saveSyncData(SyncFileData aSource, SyncFileData aTarget) throws SQLException
	{
		
		if(!FileDataExist(aSource))
		{
			putFileData(aSource);
		}
		if(!FileDataExist(aTarget))
		{
			putFileData(aTarget);
		}
		
		String query = "INSERT INTO sync_info_table VALUES (?,?)";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1, aSource.getFileId());
		prepStmt.setString(2, aTarget.getFileId());
		prepStmt.executeQuery();
		
	}
	
	public Map<SyncFileData,SyncFileData> getSyncMap() throws SQLException
	{
		Map<SyncFileData,SyncFileData> syncMap = new HashMap();
		String query = "SELECT * FROM sync_info_table";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next())
		{
			SyncFileData sourceData = getFileData(rs.getString(1));
			SyncFileData destData = getFileData(rs.getString(2));
			syncMap.put(sourceData, destData);
		}
		return syncMap;	
	}
	
	public void removeSyncData(SyncFileData aSource,SyncFileData aTarget) throws SQLException
	{
		
		int sourceId = getFileId(aSource);
		int targetId = getFileId(aTarget);
		
		String sql = "DELETE FROM sync_info_table WHERE source_id = ? AND target_id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setInt(1, sourceId);
		prepStmt.setInt(2, targetId);
		prepStmt.executeQuery();
		deleteFile(sourceId);
		deleteFile(targetId);
		
	}
	
	private void deleteFile(int fileId) throws SQLException
	{
		String sql ="DELETE FROM sync_file_data WHERE id = ?";
		PreparedStatement prepStmt = connection.prepareStatement(sql);
		prepStmt.setInt(1, fileId);
		prepStmt.executeQuery();
	}
	
	
	private int getFileId(SyncFileData aFile) throws SQLException
	{
		String query = "SELECT id FROM sync_file_data WHERE key = ? AND bucket_name = ? AND region = ?";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setString(1, aFile.getFileId());
		if(aFile.getFileServer() == FileServer.Amazon)
		{
			S3SyncFileData s3File = (S3SyncFileData) aFile;
			stmt.setString(2, s3File.getBucketName());
			stmt.setString(3, s3File.getRegion());
		}else
		{
			stmt.setNull(2, java.sql.Types.NULL);
			stmt.setNull(3, java.sql.Types.NULL);
		}
		ResultSet rs = stmt.executeQuery();
		if(rs.next())
		{
			return rs.getInt(1);
		} else throw new NoSuchElementException("Row doesn't exist in database");
		
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
	
	private void putFileData(SyncFileData aFileData) throws SQLException
	{

		String query = "INSERT INTO sync_file_data VALUES ( ?,?,?,?,?,?,? )";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1, aFileData.getFileId());
		prepStmt.setString(2, aFileData.getLastSize());
		prepStmt.setString(3, aFileData.getLastModifyDate());
		prepStmt.setString(4, aFileData.getFileServer().toString());
		
		if(aFileData.getFileServer() == FileServer.Amazon)
		{
			prepStmt.setString(5, ((S3SyncFileData)aFileData).getBucketName());
			prepStmt.setString(6, ((S3SyncFileData)aFileData).getRegion());
		}
		prepStmt.executeQuery();
	}
	
	private void createTables() throws SQLException
	{
		Statement stmt = connection.createStatement();
		stmt.executeUpdate(CREATE_FILE_DATA_TABLE);
		stmt.executeUpdate(CREATE_SYNC_INFO_TABLE);
		connection.close();
	}
	
	private SyncFileData getFileData(String aId) throws SQLException
	{
		String query = "SELECT * FROM sync_file_data WHERE key = ?";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1, aId);
		ResultSet rs = prepStmt.executeQuery();
		if(rs.next())
		{
			if(rs.getString(5).equals("Amazon"))
			{
				return new S3SyncFileData(rs.getString(2), rs.getString(3),rs.getString(4),rs.getString(6),rs.getString(7));
			}
			else
			{
				return new SyncFileData(rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5));
			}
		}else throw new NoSuchElementException("Row doesn't exist in database");
	}
	
	private final void connectToDatabase() throws SQLException
	{
		connection = DriverManager.getConnection(DATABASE_URL);
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = dbm.getTables(null,null,"sync_file_data",null);
		boolean tableExist = tables.next();
		if(!tableExist)
		{
			 createTables();
		}
	}
	
	
}
