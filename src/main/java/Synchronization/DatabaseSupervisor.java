package Synchronization;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pl.kurcaba.FileServer;


public class DatabaseSupervisor {

	private String DATABASE_URL = "jdbc:h2:~/synch";
	private String CREATE_FILE_DATA_TABLE = "CREATE TABLE sync_file_data(key VARCHAR(255) PRIMARY KEY"
			+ ",size VARCHAR(255) NOT NULL,date VARCHAR(255) NOT NULL,file_server VARCHAR(255) NOT NULL,additional_info VARCHAR(255))\r\n";
	private String CREATE_SYNC_INFO_TABLE = "CREATE TABLE sync_info_table(source_key VARCHAR(255) NOT NULL ,dest_key VARCHAR(255) NOT NULL"
			+ ",FOREIGN KEY (source_key) REFERENCES sync_file_data(key),FOREIGN KEY (dest_key) REFERENCES sync_file_data(key))\r\n";
	
	private Connection connection;
	
	public void connectToDatabase() throws SQLException
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
	
	public void saveFileData(SyncFileData aSource, SyncFileData aTarget) throws SQLException
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
		prepStmt.
		
	}
	
	private boolean FileDataExist(SyncFileData aFileData) throws SQLException
	{
		String query = "SELECT * FROM sync_file_data WHERE key=?";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1,aFileData.getFileId());
		ResultSet rs = prepStmt.executeQuery();
		boolean recordExist = rs.next();
		return recordExist;
	}
	
	private void putFileData(SyncFileData aFileData) throws SQLException
	{

		String query = "INSERT INTO sync_file_data VALUES ( ?,?,?,?,? )";
		PreparedStatement prepStmt = connection.prepareStatement(query);
		prepStmt.setString(1, aFileData.getFileId());
		prepStmt.setString(2, aFileData.getLastSize());
		prepStmt.setString(3, aFileData.getLastModifyDate());
		prepStmt.setString(4, aFileData.getFileServer().toString());
		
		if(aFileData.getFileServer() == FileServer.Amazon)
		{
			prepStmt.setString(5, ((S3SyncFileData)aFileData).getBucketName());
		}else
		{
			prepStmt.setString(5, "");
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
	
	
}
