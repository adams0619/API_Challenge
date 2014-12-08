package challenge3;
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
 * CODE2040 API Challenge #3
 * In this challenge, the API is going to give you another dictionary. The first value, prefix, is a string. 
 * The second value, array, is an array of strings. Your job is to return an array containing only the strings 
 * that do not start with that prefix.
 *  
 * @author Adams Ombonga
 * @version 1.0
 */
public class Challenge_3_API {
	
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/prefix";
	
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validateprefix";
	
    public static void main(String[] args) {
    	
    	String token = "";
 
    	//Register to API
    	String registerJson = "{\"email\":\"adamsombonga@gmail.com\",\"github\":\"http://github.com/adams0619\"}";
    	System.out.println("Register JSON is: " + registerJson);
    	token = postInformation(registerJson, true, false); 	
    	System.out.println("Token is: " + token);	
   
    	//Challenge #3 execution
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	System.out.println(tokenJson);
    	String prefixString = postInformation(tokenJson, false, false);
    	prefixString = "[" + prefixString + "]";
     	System.out.println("Prefix array returned to main method: " + prefixString);
    	String[] prefixArray = prefixString.split("'");
     	System.out.println("Prefix Array length is: " + prefixArray.length);
    	System.out.println(prefixArray[prefixArray.length - 1]);
     	
    	//Add prefixArray Strings to validation JSON
    	//Validate prefixArray with API
    	String validJson = "{\"token\":\"" + token + "\",\"array\":"; 
    	for (int i =0;i < prefixArray.length; i++) {
    		validJson += prefixArray[i];
    	}
    	validJson += "}";
    	System.out.println("Validation JSON is " + validJson);
    	postInformation(validJson, false, true);     	
    
   }

    public static String postInformation(String postInfo, boolean regRun, boolean lastRun) {
    	
    	//Method Variables
    	String finalOutput = "";
    	String prefix;
    	String[] array = {null};
    	
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
						prefix = result.getResult().getNeedle();
						System.out.println("Prefix is: " + prefix);
						array = result.getResult().getHaystack();
						int i = 0;
				    	while(i < array.length) {
				    		System.out.println("array: " + i + " " + array[i]);
				    		i++;
				    	}
				    	
				    	//Code to find save array values without prefix
		    			finalOutput = "";
		    			System.out.println("\nReturned Array Length is: "  + array.length + "\n");
				    	for (int j = 0; j <= array.length - 1; j++) {
				    		if (array[j].contains(prefix)) {
//				    			System.out.println("Array value NOT added to String");
//				    			System.out.println("Array Value is "  + array[j]);
				    		} else {
//				    			System.out.println("Array value ADDED to String");
//				    			System.out.println("Array Value is "  + array[j]);
				    				finalOutput += "\"" + array[j] + "\"" + ",' ";
				    		}
				    	}
		    			finalOutput = finalOutput.substring(0, finalOutput.length() - 3);
//				    	System.out.println("Array is: "  + finalOutput);
				    	
					}	
				}	
			}	

			//Disconnect from HTTP Request client
			httpConnection.disconnect();
		//Catch & print any HTTP errors	
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