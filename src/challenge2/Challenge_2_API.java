package challenge2;

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
 * CODE2040 API Challenge #2
 * We’re going to send you a dictionary with two values and keys. The first value, needle, is a string. 
 * The second value, haystack, is an array of strings. You’re going to tell the API where the needle is
 * in the array.
 *  
 * @author Adams Ombonga
 * @version 1.0
 */
public class Challenge_2_API {
	//Class constants
	/**
	 * String variable containing our API Endpoint link 
	 */	
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	/**
	 * String variable containing our API Challenge link, which will return necessary info for the challenge
	 */	
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/haystack";
	
	/**
	 * String variable containing our API Challenge verify link, which will let us know if we passed the challenge
	 */
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validateneedle";
	
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
   
    	//Challenge #2 - Grab necessary data from JSON
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	System.out.println(tokenJson);
    	int needleIndex = Integer.parseInt(postInformation(tokenJson, false, false));
    	System.out.println("Needle index [Main Method]: " + needleIndex);
    	
    	//Post needle index to API for validation
    	String validJson = "{\"token\":\"" + token + "\",\"needle\":\"" + needleIndex + "\"}";
    	System.out.println("Validate Json is " + validJson);
    	postInformation(validJson, false, true);     	
    
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
    	
    	//Method Variables
    	String finalOutput = "";
    	String needle;
    	String[] haystack = {null};
    	
    	//Try-Catch block using a HTTP Url connection to POST/Retrieve information to the API
    	try {
    		URL targetUrl;    		
    		if (regRun) {
    			targetUrl = new URL(API_ENDPOINT);
    			System.out.println("Using registration URL");
    		} else if (!lastRun) { 
    			targetUrl = new URL(API_URL_STAGE_1);
    			System.out.println("Using Challenge URL");
    		} else {
    			targetUrl = new URL(API_URL_STAGE_2);
    			System.out.println("Using Validation URL");
    		} 
			HttpURLConnection httpConnection = (HttpURLConnection) targetUrl.openConnection();
			httpConnection.setDoOutput(true);
			httpConnection.setRequestMethod("POST");
			httpConnection.setRequestProperty("Content-Type", "x-json-stream/line delimited json");
	
			String input = postInfo;;
			 
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
			if (regRun || lastRun) {
				while ((output = responseBuffer.readLine()) != null) {
					System.out.println(output + "\n");
					Gson gson = new GsonBuilder().create();
					Result result = gson.fromJson(output, Result.class);
					finalOutput = result.getResult();
				}			
			} else {
				while ((output = responseBuffer.readLine()) != null) {
					System.out.println(output + "\n");
					Gson gson = new GsonBuilder().create();
					if (regRun) {
						Result result = gson.fromJson(output, Result.class);
						finalOutput = result.getResult();
					} else {
						NestedJson result = gson.fromJson(output, NestedJson.class);
						needle = result.getResult().getNeedle();
						System.out.println("needle: " + needle);
						haystack = result.getResult().getHaystack();
						int i = 0;
				    	while(i < haystack.length) {
				    		System.out.println("haystack: " + i + " " + haystack[i]);
				    		i++;
				    	}
				    	
				    	//Code to find needle in haystack
				    	for (int j = 0; j < haystack.length; j++) {
				    		if (haystack[j].equalsIgnoreCase(needle)) {
				    			System.out.println("Needle at index: " + j);
				    			finalOutput = "";
				    			finalOutput += j;
				    		}
				    	}
				    	
					}	
				}	
			}	

			//Disconnect from HTTP Request client
			httpConnection.disconnect();
		} catch (MalformedURLException e) {
//			e.printStackTrace();
			System.out.println("Malformed URL Error: " + e);
		} catch (IOException e) {
//			e.printStackTrace();
			System.out.println("IO Exception Error: " + e);
		}
    	return finalOutput;
    }
    
    
}   