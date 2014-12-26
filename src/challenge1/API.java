package challenge1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import challenge1.Result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
/**
 * CODE2040 API Challenge #1
 * This specific challenge involves connecting to an API and receiving a unique token used for getting/verifying challenges
 * 
 * You’re going to reverse a string. That is, if the API says “cupcake,” you’re going to send back “ekacpuc.”
 * POST a JSON dictionary with the key token and your previous token value to API end-point:
 * the getstring endpoint will return a string that your c
 * ode should then reverse, as in the example above
 * Once that string is reversed, send it back to us. Then post your JSON to the 
 * @author Adams Ombonga
 * @version	1.0
 *
 */
public class API {
	//Class constants
	/**
	 * String variable containing our API Endpoint link 
	 */
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	/**
	 * String variable containing our API Challenge link, which will return necessary info for the challenge
	 */
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/getstring";
	
	/**
	 * String variable containing our API Challenge verify link, which will let us know if we passed the challenge
	 */
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validatestring";
	
	/**
	 * Main method used to create JSON that we pass and also call our postInformaton method which will either retrieve
	 * or send to the API.
	 * 
	 * @param args, not used
	 */
    public static void main(String[] args) {
    	
    	String token = "";
 
    	//Register to API
    	String registerJson = "{\"email\":\"adamsombonga@gmail.com\",\"github\":\"http://github.com/adams0619\"}";
    	System.out.println("Register JSON is: " + registerJson);
    	token = postInformation(registerJson, true, false); 	
    	System.out.println("Token is: " + token);
   
    	//Challenge #1
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	String word = postInformation(tokenJson, false, false);
    	System.out.println("Word is:  " + word);
    	String revWord = reverseMethod(word);
    	String wordJson = "{\"token\":\"" + token + "\",\"string\":\"" + revWord + "\"}";
    	System.out.println("Reversd word JSON: " + wordJson);
    	//Post Reveresed String to API
    	postInformation(wordJson, false, true);    
    }
    
    /**
     * This methods uses the passed parameters to determine how the passed JSON is posted to its corresponding API link
     * using a HttpUrlConnection. The returned JSON is de-serialized using GSON and information is processed and and 
     * returned as a String
     * 
     * @param postInfo, JSON that will be posted to the API
     * @param regRun, boolean used to determine if this is the first the the method was called
     * @param lastRun, boolean used to determine if this is the last time the method will be called
     * @return finalOutPut, String containing the returned JSON information, or processed JSON information
     */
    public static String postInformation(String postInfo, boolean regRun, boolean lastRun) {
    	
    	String finalOutput = "";
    	//Try-Catch block using a HTTP Url connection to POST/Retrieve information to the API
    	try {
    		URL targetUrl;
    		if (regRun) {
    			targetUrl = new URL(API_ENDPOINT);
    		} else if (!lastRun) { 
    			targetUrl = new URL(API_URL_STAGE_1);
    		} else {
    			targetUrl = new URL(API_URL_STAGE_2);
    		} 
			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "x-json-stream/line delimited json");
	
			String input = postInfo;
	
			OutputStream outputStream = httpConnection.getOutputStream();
			outputStream.write(input.getBytes());
			outputStream.flush();
	
			if (httpConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
					+ httpConnection.getResponseCode());
			}
	
			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
					(httpConnection.getInputStream())));
	
			String output; 
			System.out.println("Output from Server:\n");
			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output + "\n");
				Gson gson = new GsonBuilder().create();
				Result result = gson.fromJson(output, Result.class);
				finalOutput = result.getResult();
			}	


			httpConnection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			 e.printStackTrace();
		}
    	
    	return finalOutput;
    }
    
    /**
     * This method uses a for loop to reverse the passed string
     * 
     * @param word, String containing the word passed to thus method
     * @return revWord, String containing the reversed word
     */
    public static String reverseMethod(String word) {
    	String revWord = "";
    	for (int i = 0; i < word.length(); i++) { 
    		revWord = word.charAt(i) + revWord;
    	}
    	return revWord;
    }
    
}   