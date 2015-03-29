package com.tool.app.bean;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;



public class Poll {
	
	@JsonView(Poll.ViewPoll.class)
	private String id;
	

	@JsonView(Poll.ViewPoll.class)
	//@NotNull(message="question field must not be null or empty!!")
	@NotBlank(message="question field must not be null or empty!!")
	private String question;
	
	
	@JsonView(Poll.ViewPoll.class)
	//@NotNull(message="started_at field must not be null or empty!!")
	@NotBlank(message="started_at field must not be null or empty!!")
	private String started_at;
	

	@JsonView(Poll.ViewPoll.class)
	//@NotNull(message="expired_at field must not be null or empty!!")
	@NotBlank(message="expired_at field must not be null or empty!!")
	private String expired_at;
	

	@JsonView(Poll.ViewPoll.class)
	@NotNull(message="choice field must not be null or empty!!")
	@Size(min=2, message="choice field must have atleast 2 values!!")
	private String[] choice;
	
	@JsonInclude(Include.NON_EMPTY)
	private int[] results;
	
	
	public interface ViewPoll{};
	
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	
	public String[] getChoice() {
		return choice;
	}
	public void setChoice(String[] choice) {
		this.choice = choice;
	}
	public int[] getResults() {
		return results;
	}
	public void setResults(int[] results) {
		this.results = results;
	}
	public String getStarted_at() {
		return started_at;
	}
	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}
	public String getExpired_at() {
		return expired_at;
	}
	public void setExpired_at(String expired_at) {
		this.expired_at = expired_at;
	}

	@Override
	public String toString()
	{
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
