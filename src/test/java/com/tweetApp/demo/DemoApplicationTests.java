package com.tweetApp.demo;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
//import org.junit.runner.RunWith;
//import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = TweetApplication.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {TweetsController.class})
//@WebMvcTest
class DemoApplicationTests {

	@Autowired
		private  MockMvc mockMvc;
		private static int size=10;
		
		@Test
		public String existentUserCanGetTokenAndAuthentication() throws Exception {
		    String username = "tweetUser";
		    String password = "dummy";

		    String body="{ \"username\":\"" + username + "\", \"password\":\"" + password + "\"}";
        
		    MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/authenticate")
		    		.accept(MediaType.APPLICATION_JSON)
		            .contentType(MediaType.APPLICATION_JSON)
		            .content(body))
		            .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();

		    String response = result.getResponse().getContentAsString();
		    System.out.println(response);
		    response = response.replace("{\"token\":\"", "");
		    String token = response.replace("\"}", "");
		    System.out.println(token);
		    return token;

		}
		
		public ResultActions buildAndTestURL(String url) throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			ResultActions res=mockMvc.perform(MockMvcRequestBuilders.get(url).accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token));
			//.andExpect(MockMvcResultMatchers.status().isCreated())
			//.andExpect(jsonPath("$.message").value("ToDo doesn´t exist"))
			//.andDo(print());
			return res;
		}

		@Test
		public void verifyAllUsersList() throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			 mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/users/all")
		        .header("Authorization", "Bearer " + token))
		        .andExpect(MockMvcResultMatchers.status().isOk())
		        .andExpect(jsonPath("$", hasSize(20))).andDo(print());
		}
		
		@Test
		public void verifyTweetsByUserId() throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/sample1").accept(MediaType.APPLICATION_JSON)
			 .header("Authorization", "Bearer " + token))
			.andExpect(jsonPath("$", hasSize(9)))
			.andDo(print());
		}
		@Test
		public void verifyWrongUserId() throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/sample6").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			//.andExpect(jsonPath("$.message").value("ToDo doesn´t exist"))
			.andDo(print());
		}
		@Test
		public void verifyLogin() throws Exception {
			ResultActions res=buildAndTestURL("/api/v1.0/tweets/login?userId=sample2&password=sample22");
			res.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(print());
		}
		@Test
		public void verifyInvalidLogin() throws Exception {
			//Invalid password
			ResultActions res=buildAndTestURL("/api/v1.0/tweets/login?userId=sample2&password=sample23");
			res.andExpect(MockMvcResultMatchers.status().isUnauthorized())
			.andDo(print());
			//Invalid UserID and passsword
			ResultActions res1=buildAndTestURL("/api/v1.0/tweets/login?userId=sample7&password=sample22");
			res1.andExpect(MockMvcResultMatchers.status().isUnauthorized())
			.andDo(print());
		}
		@Test
		public void verifyLikeATweet() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/tweets/sample2/like/61cb1805dca3627e7260941d").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andDo(print());
		}
		//wrong userid for a like
		@Test
		public void verifyLikeATweetInvalidUserID() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/tweets/sample22/like/61cb1805dca3627e7260941d").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andDo(print());
		}
		//Wrong tweetId for a like
		@Test
		public void verifyLikeATweetInvalidTweetID() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/tweets/sample2/like/61cb1805dca3627e726094").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andDo(print());
		}
		@Test
		public void verifyRegister() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String userId="x"+Math.random();
			DemoApplicationTests.size+=1;
			System.out.println(size);
			String body="{\"firstName\":\"yyy\",\"lastName\":\"yyy\",\"userId\":\""+userId+"\",\"password\":\"yyy\",\"email\":\"yyy@gmail.com\",\"contactNumber\":\"9999999999\"}";
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/tweets/register")
			    		.accept(MediaType.APPLICATION_JSON)
			    		.header("Authorization", "Bearer " + token)
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(body))
			            .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
		}
		@Test
		public void verifyRegisterDuplicateUserId() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String body="{\"firstName\":\"yyy\",\"lastName\":\"yyy\",\"userId\":\"yyy\",\"password\":\"yyy\",\"email\":\"yyy@gmail.com\",\"contactNumber\":\"9999999999\"}";
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/tweets/register")
			    		.accept(MediaType.APPLICATION_JSON)
			    		.header("Authorization", "Bearer " + token)
			            .contentType(MediaType.APPLICATION_JSON)
			            .content(body))
			            .andExpect(MockMvcResultMatchers.status().isInternalServerError())
			            .andExpect(content().string("UserID already exists"))
			            .andReturn();
		}
		@Test
		public void verifyForgotPassword() throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			//String body="{\"firstName\":\"yyy\",\"lastName\":\"yyy\",\"userId\":\"yyy\",\"password\":\"yyy\",\"email\":\"yyy@gmail.com\",\"contactNumber\":\"9999999999\"}";
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/yyy/forgot")
			    		.accept(MediaType.APPLICATION_JSON)
			    		.header("Authorization", "Bearer " + token)
			            .contentType(MediaType.TEXT_PLAIN)
			            .content("modifiedByTest"))
			            .andExpect(MockMvcResultMatchers.status().isCreated())
			            .andReturn();
		}
		@Test
		public void verifyForgotPasswordInvalidUserId() throws Exception {
			String token=existentUserCanGetTokenAndAuthentication();
			//String body="{\"firstName\":\"yyy\",\"lastName\":\"yyy\",\"userId\":\"yyy\",\"password\":\"yyy\",\"email\":\"yyy@gmail.com\",\"contactNumber\":\"9999999999\"}";
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/yyy23/forgot")
			    		.accept(MediaType.APPLICATION_JSON)
			    		.header("Authorization", "Bearer " + token)
			            .contentType(MediaType.TEXT_PLAIN)
			            .content("modifiedByTest"))
			            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
			            .andExpect(content().string("User does not exist"))
			            .andReturn();
		}
		@Test
		public void verifyGetAllTWeets() throws Exception{
		String token=existentUserCanGetTokenAndAuthentication();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/all").accept(MediaType.APPLICATION_JSON)
				.header("Authorization", "Bearer " + token))
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(jsonPath("$", hasSize(3)))
		.andDo(print());
		}
		@Test
		public void verifyGetByUserName()throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/user/search/sample1").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andDo(print());
		}
		@Test
		public void verifyGetByUserNameContaining()throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/user/search/sample").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(jsonPath("$", hasSize(3)))
			.andDo(print());
		}
		@Test
		public void verifyGetByUserNameContainingInvalidUserName()throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.get("/api/v1.0/tweets/user/search/cts").accept(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + token))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andExpect(content().string("No user found containing given name"))
			.andDo(print());
		}
		@Test
		public void verifypostATweet() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String body="{\"tweetText\":\"hi!Thisisxxx.GreattojoinTweetApp\",\"createdAt\":\"2021-12-28\"}";
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/tweets/xxx/add").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .content(body))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(content().string("Posted the tweet successfully!"))
			.andDo(print());
		}
		@Test
		public void verifypostATweetInvalidUserId() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String body="{\"tweetText\":\"hi!Thisisxxx.GreattojoinTweetApp\",\"createdAt\":\"2021-12-28\"}";
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/tweets/xxx23/add").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
            .content(body))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andExpect(content().string("No user found with the given Id"))
			.andDo(print());
		}
		@Test
		public void verifydeleteATweet() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/tweets/sample1/delete/61cc5d49a854a705dcfe24da").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andDo(print());
		}
		@Test
		public void verifydeleteATweetInvalidTweetId() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/tweets/xxx/delete/61fa731702757d33a9b0461d").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andExpect(content().string("Invalid UserId or TweetId!"))
			.andDo(print());
		}
		@Test
		public void verifydeleteATweetInvalidUserId() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/tweets/xxx23/delete/61fa73d8db05e170d2f43914").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andExpect(content().string("Cannot delete Tweet!"))
			.andDo(print());
		}
		@Test
		public void verifydeleteATweetNonMatchingUserIdAndTweetId() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1.0/tweets/sample1/delete/61fa73d8db05e170d2f43914").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON))
			.andExpect(MockMvcResultMatchers.status().isInternalServerError())
			.andExpect(content().string("Cannot delete Tweet!"))
			.andDo(print());
		}
		@Test
		public void verifyupdateATweet() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String body="{\"tweetText\":\"hi!Thisisxxx.Ijustupdatedmytweet\",\"createdAt\":\"2021-12-28\"}";
			mockMvc.perform(MockMvcRequestBuilders.put("/api/v1.0/tweets/xxx/update/61fa73d8db05e170d2f43914").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(content().string("Updated tweet succcesfully"))
			.andDo(print());
		}
		@Test
		public void verifyReplyATweet() throws Exception{
			String token=existentUserCanGetTokenAndAuthentication();
			String body="{\"tweetText\":\"hi!this is sample1.Thanks for joining tweetApp\",\"createdAt\":\"2021-12-28\"}";
			mockMvc.perform(MockMvcRequestBuilders.post("/api/v1.0/tweets/sample1/reply/61fa73d8db05e170d2f43914").accept(MediaType.APPLICATION_JSON)
			.header("Authorization", "Bearer " + token)
			.contentType(MediaType.APPLICATION_JSON)
			.content(body))
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(content().string("Posted the reply!"))
			.andDo(print());
		}

}

