package com.tweetApp.kafka;

import java.io.IOException;

import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetApp.model.Tweets;
import com.tweetApp.model.Users;
import com.tweetApp.repository.UsersRepository;
import com.tweetApp.services.TweetsService;

@Service
public class kafkaConsumer {

	
		private final Logger logger = LoggerFactory.getLogger(kafkaConsumer.class);
		private ObjectMapper mapper = new ObjectMapper();
		@Autowired
		private UsersRepository repo;

//	    @KafkaListener(topics = "Tweets", groupId = "group_id")
//	    public void consume(String message) throws IOException {
//	        logger.info(String.format("#### -> Consumed message -> %s", message));
//	    }
	    @KafkaListener(topics = "Tweets", groupId = "group_id")
		public void consume(String message) throws Exception, JsonProcessingException {
	    	logger.info("inside consumer for registering user");
			Users user = this.mapper.readValue(message, Users.class);
			logger.info(user.toString());
			try {
			if(repo.findById(user.getUserId()).isPresent()) {
				logger.info("user already exists");
				throw new Exception("UserID already exists");
				}
				else {
				Users newUser=repo.save(user);
				logger.info(newUser.toString());
				//return newUser;
				}
			}
			catch(Exception e) {
				logger.info(e.getMessage());
				throw e;
			}
			// this.repo.save(user);
			//logger.info("user saved successfully!");
			//logger.info("User added{}", userSaved);
		}

}
