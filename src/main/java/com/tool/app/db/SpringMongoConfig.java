package com.tool.app.db;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.MongoClient;
 
/**
 * Spring MongoDB configuration file
 * 
 */
@Configuration
public class SpringMongoConfig{
 
	public @Bean
	MongoTemplate mongoTemplate() throws Exception {
 
		UserCredentials credentials = new UserCredentials("usrename", "password");
		MongoTemplate mongoTemplate = 
			new MongoTemplate(new MongoClient("host" , port),"dbname",credentials);
		mongoTemplate.remove(new Query(), "collectionName");
		return mongoTemplate;
 
	}
 
}