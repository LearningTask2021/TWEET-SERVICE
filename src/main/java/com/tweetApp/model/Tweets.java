package com.tweetApp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class Tweets {

	public Tweets(String tweetText) {
		// TODO Auto-generated constructor stub
		this.tweetText=tweetText;
	}
	@Id
	private String tweetId=new ObjectId().toString();
	private String tweetText;
	private String createdAt=LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	private int likes;
	private String parentTweetId;
	private List<Tweets> replies=new ArrayList<Tweets>();
	
	
	

}
