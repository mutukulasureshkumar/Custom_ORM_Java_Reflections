package com.java.miniORM.DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author ${Suresh M Kumar}
 *
 * Aug 27, 2017
 */
public class DBConnection {
	
	private static final String DBDRIVER="oracle.jdbc.driver.OracleDriver";
	private static final String DBURL="jdbc:oracle:thin:@<server>:1521:<db_instance>";
	private static final String USERNAME="<user_name>"; 
	private static final String PASSWORD="<password>"; 
	private static Connection connection=null;
	
	private DBConnection(){}
	
	public static Connection getConnection(){
		try{
			if(connection==null){
				Class.forName(DBDRIVER);
				connection=DriverManager.getConnection(DBURL,USERNAME,PASSWORD);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return connection;
	}
	
	public static void closeConnection(){
		try{
			if(connection!=null)
				connection.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
