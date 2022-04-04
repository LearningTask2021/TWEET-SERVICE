package com.tweetApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
//import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.tweetApp.model.Tweets;
import com.tweetApp.model.Users;
import com.tweetApp.repository.UsersRepository;
import com.tweetApp.services.TweetsService;

import java.util.*;
import java.util.function.Supplier;

@SpringBootApplication
@EnableMongoRepositories
@EnableEurekaClient
public class TweetApplication {
	
	@Autowired
	public UsersRepository repo;
	@Autowired 
	public TweetsService service;

	public static void main(String[] args) {
		SpringApplication.run(TweetApplication.class, args);
	}
	
	@Bean
	public Supplier<List<Users>> getAllUsers(){
		System.out.println("Inside getAllUsers lambda function");
		return ()-> repo.findAll();
	}
	
	@Bean
	public Supplier<List<Tweets>> getAllTweets(){
		System.out.println("Inside getting all tweets lambda function");
		return ()->service.retrunAllTweets();
	}

}
