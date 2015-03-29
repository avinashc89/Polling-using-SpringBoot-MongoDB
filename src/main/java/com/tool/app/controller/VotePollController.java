package com.tool.app.controller;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.validation.Valid;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.tool.app.bean.Moderator;
import com.tool.app.bean.Moderator.EmailValidator;
import com.tool.app.bean.Moderator.ModValidator;
import com.tool.app.bean.Poll;
import com.tool.app.db.SpringMongoConfig;


@RestController
@RequestMapping("/api/v1")
public class VotePollController {
	
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	private static int moderatorSeqId = 10000;
	private static long pollSeqId = 345546880; 

	static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	static MongoOperations mongoOperation =  (MongoOperations) ctx.getBean("mongoTemplate");
	
	
	
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
		
		if(result.hasErrors())
		{
			return new ResponseEntity<String>(callError(result),HttpStatus.BAD_REQUEST);
		}
		mod.setId(moderatorSeqId);
		mod.setCreated_at(dateFormatter.format(new Date()));
		mod.setPollList(null);
		
		mongoOperation.save(mod, "Vote");
		 
		moderatorSeqId++;
		return new ResponseEntity<String>(mod.toString(),HttpStatus.CREATED);
	}
	
	
	/**
	 * to get moderator details : GET
	 * @param moderatorId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value="/moderators/{moderatorId}")
	public ResponseEntity<Moderator> getModeratorDetails(@PathVariable int moderatorId)
	{
		
		Query query1 = new Query();
		query1.fields().exclude("pollList");
		query1.addCriteria(Criteria.where("id").is(moderatorId));
		
		Moderator mod = mongoOperation.findOne(query1, Moderator.class,"Vote");

		
		if(mod!= null)
			return new ResponseEntity<Moderator>(mod,HttpStatus.OK);
		else
			return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);

	} 	
	
	

	/**
	 * to update moderator details : PUT
	 * @param mod
	 * @param moderator_id
	 * @param result
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value="/moderators/{moderator_id}" ,produces = "application/json")
	public ResponseEntity<String> updateModeratorDetails(@Validated({ EmailValidator.class }) @RequestBody Moderator mod, BindingResult result, @PathVariable int moderator_id )
	{
		if(result.hasErrors())
		{
			return new ResponseEntity<String>(callError(result),HttpStatus.BAD_REQUEST);
		}
		else
		{
			Query query1 = new Query();
			query1.addCriteria(Criteria.where("id").is(moderator_id));
			Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
			
			if(curMod!=null)
			{
				curMod.setEmail(mod.getEmail());
				curMod.setPassword(mod.getPassword());
				mongoOperation.save(curMod);
				return new ResponseEntity<String>(curMod.toString(),HttpStatus.OK);
			}
			else
				return new ResponseEntity<String>("Moderator Id not found!!!",HttpStatus.NOT_FOUND);
		}
	}


	/**
	 * to create Poll : POST
	 * @param poll
	 * @param moderator_id
	 * @param result
	 * @return
	 */
	
	@RequestMapping(method = RequestMethod.POST, value="/moderators/{moderator_id}/polls" ,produces = "application/json")
	public ResponseEntity<String> createPoll(@Valid @RequestBody Poll poll, BindingResult result, @PathVariable int moderator_id)
	{
		if(result.hasErrors())
		{
			return new ResponseEntity<String>(callError(result),HttpStatus.BAD_REQUEST);
		}
		else
		{
			Query query1 = new Query();
			query1.addCriteria(Criteria.where("id").is(moderator_id));
			Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");

			if(curMod!=null)
			{
				poll.setId(Long.toString(pollSeqId, 36).toUpperCase());
				int choiceLen = poll.getChoice().length;
				int[] results = new int[choiceLen];
				poll.setResults(results);
				
				curMod.getPollList().add(poll);
				mongoOperation.save(curMod);
				
				pollSeqId++;
				
				poll.setResults(null);
				
				return new ResponseEntity<String>(poll.toString(),HttpStatus.OK);
			}
			else{
				return new ResponseEntity<String>("Given Moderator Id is not found",HttpStatus.BAD_REQUEST);
			}
		}

	}
	
	

	/**
	 * to get poll details with results : GET
	 * @param moderator_id
	 * @param poll_id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value="/moderators/{moderator_id}/polls/{poll_id}")
	public ResponseEntity<Poll> getPollDetailsWithResult(@PathVariable int moderator_id,@PathVariable String poll_id)
	{
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("pollList._id").is(poll_id).and("id").is(moderator_id));
		query1.fields().include("pollList");
		Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
		
		if(curMod!=null)
		{
			Poll pollDetails =null;
			for(Poll p: curMod.getPollList())
				if(p.getId().equalsIgnoreCase(poll_id)){
					pollDetails = p;
					break;
				}
			
			return new ResponseEntity<Poll>(pollDetails,HttpStatus.OK);
		}
		else
			return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);

	}
	
	/**
	 * to get poll details without results : GET
	 * @param poll_id
	 * @return
	 */
	@JsonView(Poll.ViewPoll.class)
	@RequestMapping(method = RequestMethod.GET, value="/polls/{poll_id}")
	public ResponseEntity<Poll> getPollDetailsWithoutResults(@PathVariable String poll_id)
	{
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("pollList._id").is(poll_id));
		query1.fields().include("pollList");
		Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
		
		if(curMod!=null)
		{
			Poll pollDetails =null;
			for(Poll p: curMod.getPollList())
				if(p.getId().equalsIgnoreCase(poll_id)){
					pollDetails = p;
					break;
				}
			
			return new ResponseEntity<Poll>(pollDetails,HttpStatus.OK);
		}
		else
			return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);

	}
	
	
	/**
	 * to get all polls for a moderator : GET
	 * @param moderator_id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value="/moderators/{moderator_id}/polls")
	public ResponseEntity<ArrayList<Poll>> listAllPoll(@PathVariable int moderator_id)
	{
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("id").is(moderator_id));
		Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
		
		if(curMod != null)
		{
			return new ResponseEntity<ArrayList<Poll>>(curMod.getPollList(),HttpStatus.OK);
		}
		else
			return new ResponseEntity<ArrayList<Poll>>(HttpStatus.NOT_FOUND);
	}

	
	/**
	 * to delete given poll : DELETE
	 * @param moderator_id
	 * @param poll_id
	 * @return
	 */
	@RequestMapping(method = RequestMethod.DELETE, value="/moderators/{moderator_id}/polls/{poll_id}")
	public ResponseEntity<Poll> deletePoll(@PathVariable int moderator_id,@PathVariable String poll_id)
	{
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("pollList._id").is(poll_id).and("id").is(moderator_id));
		Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
		
		
		if(curMod != null)
		{
			ArrayList<Poll> deletePollList = new ArrayList<Poll>();
			
			for(Poll p: curMod.getPollList())
				if(p.getId().equalsIgnoreCase(poll_id)){
					deletePollList.add(p);
					break;
				}
			
			curMod.getPollList().removeAll(deletePollList);
			
			mongoOperation.save(curMod, "Vote");
		}
		else
			return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
		
		return new ResponseEntity<Poll>(HttpStatus.NO_CONTENT);
	}
	
	
	
	/**
	 * to update poll result : PUT
	 * @param poll_id
	 * @param choice
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, value="/polls/{poll_id}",produces = "application/json")
	public ResponseEntity<String> updatePoll(@PathVariable String poll_id, @RequestParam int choice)
	{
		Query query1 = new Query();
		query1.addCriteria(Criteria.where("pollList._id").is(poll_id));
		Moderator curMod = mongoOperation.findOne(query1, Moderator.class,"Vote");
		
		if(curMod !=null)
		{
			for(Poll p: curMod.getPollList())
			{
				if(p.getId().equalsIgnoreCase(poll_id))
				{
					int[] result = p.getResults();
					if(choice < result.length ){
						result[choice]++;
						p.setResults(result);
						break;
					}
					else
						return new ResponseEntity<String>("Choice index is invalid!!!",HttpStatus.BAD_REQUEST);
					
				}
			}
			mongoOperation.save(curMod, "Vote");
			return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		}
		else
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

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
	
	
	