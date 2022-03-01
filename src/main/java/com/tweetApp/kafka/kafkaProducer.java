package com.tweetApp.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.Producer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tweetApp.model.Tweets;
import com.tweetApp.model.Users;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class kafkaProducer {

		    private static final Logger logger = LoggerFactory.getLogger(Producer.class);
		    private static final NewTopic TOPIC = adviceTopic();
		    
		    private ObjectMapper mapper = new ObjectMapper();

		    @Autowired
		    private KafkaTemplate<String, String> kafkaTemplate;

		    public void sendMessage(String message) {
		        logger.info(String.format("#### -> Producing message -> %s", message));
		        this.kafkaTemplate.send(TOPIC.name(), message);
		    }
		    
		    @Bean
		    public static NewTopic adviceTopic() {
		    	return new NewTopic("Tweets",3,(short)1);
		    }
		    public void registerKafkaProducer(Users user) throws JsonProcessingException {
		    	logger.info("inside kafka producer for registering user");
		    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);
			this.kafkaTemplate.send("Tweets", json);
		    }
		    
		   

}
