package com.tool.app.bean;

import java.util.ArrayList;





import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Document(collection = "Vote")
public class Moderator  {


	private int id;
	
	//@NotNull(groups = {ModValidator.class},message="name field must not be null or empty!!")
	@NotBlank(groups = {ModValidator.class},message="name field must not be null or empty!!")
	private String name;
	
	//@NotNull(groups = { EmailValidator.class, ModValidator.class}, message="email field must not be null or empty!!")
	@NotBlank(groups = { EmailValidator.class, ModValidator.class}, message="email field must not be null or empty!!")
	private String email;
	
	//@NotNull(groups = { EmailValidator.class,ModValidator.class }, message="password field must not be null or empty!!")
	@NotBlank(groups = { EmailValidator.class,ModValidator.class }, message="password field must not be null or empty!!")
	private String password;
			
	
	private String created_at;
	
	@JsonInclude(Include.NON_EMPTY)
	private ArrayList<Poll> pollList = new ArrayList<Poll>();
	
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	
	public ArrayList<Poll> getPollList() {
		return pollList;
	}
	public void setPollList(ArrayList<Poll> pollList) {
		this.pollList = pollList;
	}

	public interface EmailValidator {};
	public interface ModValidator {};
	
	
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
