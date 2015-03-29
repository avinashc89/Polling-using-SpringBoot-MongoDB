package com.tool.app.controller;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.tool.app.bean.Moderator;
import com.tool.app.bean.Moderator.ModValidator;
import com.tool.app.db.MongoDatabase;



@RestController
@RequestMapping("/api/v3")
public class Vote2Controller {
	

	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static int moderatorSeqId = 10000;
	private static long pollSeqId = 345546880; 
	
	/**
	 * to create moderator : POST
	 * @param mod
	 * @param result
	 * @return
	 * @throws UnknownHostException 
	*/
	@RequestMapping(method = RequestMethod.POST, value="/moderators",produces = "application/json")
	public ResponseEntity<String> createModerator(@Validated({ModValidator.class}) @RequestBody Moderator mod, BindingResult result) throws UnknownHostException
	{
		
		System.out.println("came hwew"+mod.getEmail());
		if(result.hasErrors())
		{
			return new ResponseEntity<String>(callError(result),HttpStatus.BAD_REQUEST);
		}
		mod.setId(moderatorSeqId);
		mod.setCreated_at(dateFormatter.format(new Date()));
		mod.setPollList(null);
		
		DB db = MongoDatabase.getMongoDBInstance();
		DBCollection voteCollection = db.getCollection("Vote");
		voteCollection.save((DBObject) JSON.parse(mod.toString()));
		 
		moderatorSeqId++;
		return new ResponseEntity<String>(mod.toString(),HttpStatus.CREATED);
	}
		
	
	/**
	 * to get moderator details : GET
	 * @param moderatorId
	 * @return
	 * @throws UnknownHostException 
	 */
	@RequestMapping(method = RequestMethod.GET, value="/moderators/{moderatorId}")
	public ResponseEntity<String> getModeratorDetails(@PathVariable int moderatorId) throws UnknownHostException
	{
		DB db = MongoDatabase.getMongoDBInstance();
		DBCollection voteCollection = db.getCollection("Vote");
		
		
		BasicDBObject allQuery = new BasicDBObject();
		BasicDBObject fields = new BasicDBObject();
		fields.put("id", moderatorId);
		fields.put("name", moderatorId);
		fields.put("email", moderatorId);
		fields.put("password", moderatorId);
		fields.put("created_at", moderatorId);
		
	 
		  DBCursor cursor = voteCollection.find(allQuery, fields);
		  while (cursor.hasNext()) {
			System.out.println(cursor.next());
		  }
		  
		  
		
			return new ResponseEntity<String>("hi",HttpStatus.OK);
		//else
		//	return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);

	}


	/**
	 * to create error strings
	 * @param result
	 * @return
	 */
	private String callError(BindingResult result) {
		StringBuilder errorMsg = new StringBuilder();
		for (ObjectError err: result.getAllErrors()){
			errorMsg.append(err.getDefaultMessage());
		}
		return errorMsg.toString();

	}

}
