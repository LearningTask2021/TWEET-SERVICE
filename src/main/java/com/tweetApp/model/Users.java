package com.tweetApp.model;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
@Document(collection = "Users")
	public class Users
	{
	
	
	private String firstName;
	private String lastName;
	@Id
	private String userId;
	private String password;
	private String email;
	private String contactNumber;
	private List<Tweets> tweets=new ArrayList<Tweets>();
	
	

}
