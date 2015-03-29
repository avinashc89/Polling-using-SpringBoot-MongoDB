package com.tool.app.db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDatabase {
	
	private static DB db ;
	
	
	private MongoDatabase()
	{
		
	}
	
	@SuppressWarnings("deprecation")
	public static DB getMongoDBInstance() throws UnknownHostException
	{
		
		if(db == null){
			MongoClient mongo = new MongoClient( "hostname" , port );
			db = mongo.getDB("dbname");
			db.authenticate("uname", "pwd".toCharArray());
		}
		return db;
		
	}

}
