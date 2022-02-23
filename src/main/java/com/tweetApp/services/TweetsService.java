package com.tweetApp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoSocketOpenException;
import com.tweetApp.model.Tweets;
import com.tweetApp.model.Users;
import com.tweetApp.repository.UsersRepository;

import io.jsonwebtoken.lang.Collections;

@Service
public class TweetsService {
	
	@Autowired
	private UsersRepository usersRepository;
	
	public Users registerNewUser(Users user) throws Exception {
		if(usersRepository.findById(user.getUserId()).isPresent()) {
		throw new Exception("UserID already exists");
		}
		else {
		Users newUser=usersRepository.save(user);
		System.out.println(newUser);
		return newUser;
		}
		
		
	}
	
	public Users loginUser(String userId,String password) {
		Users user=usersRepository.findByUserIdAndPassword(userId,password).get();
		System.out.println(user);
		return user;
	}
	
	public Users resetPassword(String userId,String password)throws Exception {
		Optional<Users> user=usersRepository.findByUserId(userId);
		if(user.isPresent()) {
		user.get().setPassword(password);
		usersRepository.save(user.get());
		return user.get();
		}
		else {
			throw new Exception("User does not exist");
		}
	}
	
	public List<Tweets> retrunAllTweets(){
		List listOfTweets = new ArrayList<>();
        usersRepository.findAll().forEach(
        		user->{
        			if(!user.getTweets().isEmpty()) {
        				user.getTweets().forEach(
        						t->{
        							if(t.getTweetText()!=null)
        							listOfTweets.add(t);
        						});
        			
        			}
        		});
        return listOfTweets;
	}
	
	public List<Users> retrunAllUsers(){
		List listOfUsers = new ArrayList<>();
        usersRepository.findUsersAndExcludePassword().forEach(listOfUsers::add);
        return listOfUsers;
	}
	
	public List<Tweets> returnTweetsOfUSer(String userId){
		Users user = usersRepository.findByUserId(userId).get();
		List<Tweets> tweets=user.getTweets();
		return tweets;	
	}
	
	public List<Users> returnUsersContainingName(String userId)throws Exception{
		List<Users> users=this.retrunAllUsers();
		List<Users> result= users.stream()
                .filter(x ->x.getUserId().indexOf(userId)>=0)
                .collect(Collectors.toList());
		if(result.isEmpty())
			throw new Exception("No user found containing given name");
		else {
			return result;
		}
	    
	}
	
	public String PostATweet(Tweets tweet,String userId) throws Exception{
		if(tweet.getTweetText()==null) {
			throw new Exception("Please provide some text to post the tweet.empty tweets cannot be posted!");
		}
		else {
			if(tweet.getTweetText().length()>144) {
				throw new Exception("Tweet cannot exceed 144 characters.");
			}
		Optional<Users> user=usersRepository.findByUserId(userId);
		if(user.isPresent()) {
    	List<Tweets> tweets=user.get().getTweets();
    	tweets.add(tweet);
    	Users updatedUser=usersRepository.save(user.get());
    	return "Posted the tweet successfully!";
		}
		else {
			throw new Exception("No user found with the given Id");
		}
		}
	}
	
	public String deleteATweet(String userId,String tweetId) throws Exception{
		System.out.println(userId);
		System.out.println(tweetId);
		Optional<Users> u=usersRepository.findByUserId(userId);
		Optional<Users> tweetedUser=usersRepository.findByTweetsTweetId(tweetId);
		System.out.println(tweetedUser.get().getUserId());
		if(tweetedUser.isPresent()) {
			System.out.println("got user with give nu serid");
			if(tweetedUser.get().getUserId().equals(userId)) {
				System.out.println("userid matched with tweeted persons userid");
			Users user=u.get();
			List<Tweets> tweets=user.getTweets();
			List<Tweets> updated=tweets.stream().filter(t->!(t.getTweetId().equals(tweetId))).collect(Collectors.toList());
			updated.forEach(t->System.out.println(t.getTweetId()));
			user.setTweets(updated);
			Users updatedUser=usersRepository.save(user);
			return "Tweet deleted succcessfully!";
			}
		else {
			throw new Exception("Invalid UserId or TweetId!");
		}
		}
		throw new Exception("Cannot delete Tweet!");
		
	}
	
	public String replyATweet(String userId,String parentTweetId,Tweets reply) throws Exception{
		if(reply.getTweetText()==null) {
			throw new Exception("Please provide some text to update the tweet.Empty tweets cannot be posted!");
		}
		else {
			if(reply.getTweetText().length()>144) {
				throw new Exception("Tweet cannot exceed 144 characters.");
			}
		System.out.println("Inside tweets method!");
		reply.setParentTweetId(parentTweetId);
     	Users user=usersRepository.findByUserId(userId).get();
     	List<Tweets> tweets=user.getTweets();
     	tweets.add(reply);
     	Optional<Users> u1=usersRepository.findByTweetsTweetId(parentTweetId);
     	if(u1.isPresent()) {
     		Users user1=u1.get();
     	System.out.println(user1);
     	List<Tweets> tweets1=user1.getTweets();
     	tweets1.forEach(t->{
     	if(t.getTweetId().contentEquals(parentTweetId)) {
     		reply.setTweetText("@"+userId+":-"+reply.getTweetText().toString());
     		System.out.println(reply.getTweetText());
     		t.getReplies().add(reply);
     	}
     	});
     	System.out.println(tweets1);
     	user1.setTweets(tweets1);
     	usersRepository.save(user1);
     	Users updatedUser=usersRepository.save(user);
     	return "Posted the reply!";
     	}
     	else {
     	return "";
     	}
		}
	}
	
	public String updateATweet(String userId,String tweetId,Tweets tweet)throws Exception {
		if(tweet.getTweetText()==null) {
			throw new Exception("Please provide some text to update the tweet.Empty tweets cannot be posted!");
		}
		else {
			if(tweet.getTweetText().length()>144) {
				throw new Exception("Tweet cannot exceed 144 characters.");
			}
		List<Users> allusers=usersRepository.findAll();
//		allusers.forEach(
//				u->{
//					u.getTweets().forEach(
//							t->{
//								t.getReplies().forEach(
//										r->{
//											if(r.getParentTweetId().equals(tweetId)) {
//												System.out.println("Inside updating reply");
//												r=tweet;
//												System.out.println(r.getTweetText());
//												usersRepository.save(u);
//											}
//										}
//										);
//							}
//							);
//				}
//				);
//		
		Users user=usersRepository.findByUserId(userId).get();
		List<Tweets> tweets=user.getTweets();
		//tweets.removeIf(t -> t.getTweetId().equals(tweetId);
		for (int i = 0; i < tweets.size(); i++) {
					if(tweets.get(i).getTweetId().equals(tweetId)) {
						System.out.println("Inside updating tweeet");
						tweets.remove(tweets.get(i));
						tweets.add(tweet);
					}
		}
		//tweets.add(tweet);
		Users updatedUser=usersRepository.save(user);
		
		return "Updated tweet succcesfully";
		}
	}
	
	public List<Tweets> likeATweet(String userId,String tweetId) {
		Users user=usersRepository.findByUserId(userId).get();
		List<Tweets> tweets=user.getTweets();
		Users user1=usersRepository.findByTweetsTweetId(tweetId).get();
		if(user1!=null) {
			
		tweets.forEach(t->{
			if(t.getTweetId().contentEquals(tweetId)) {
				t.setLikes(t.getLikes()+1);
				System.out.println(t.getLikes());
			}
		});
		}
		else {
			return null;
		}
		user.setTweets(tweets);
		usersRepository.save(user);
		return user.getTweets();
	}
}
