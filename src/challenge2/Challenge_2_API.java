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
 
 
public class Challenge_2_API {
	
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/haystack";
	
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validateneedle";
	
    public static void main(String[] args) {
    	
    	String token = "";
 
    	//Register to API
    	String registerJson = "{\"email\":\"adamsombonga@gmail.com\",\"github\":\"http://github.com/adams0619\"}";
    	System.out.println("Register JSON is: " + registerJson);
    	token = postInformation(registerJson, true, false); 	
    	System.out.println("Token is: " + token);	
   
    	//Challenge #2
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	System.out.println(tokenJson);
    	int needleIndex = Integer.parseInt(postInformation(tokenJson, false, false));
    	System.out.println("Needle index [Main Method]: " + needleIndex);
    	
    	//Post needle index to API
    	String validJson = "{\"token\":\"" + token + "\",\"needle\":\"" + needleIndex + "\"}";
    	System.out.println("Validate Json is " + validJson);
    	postInformation(validJson, false, true);     	
    
   }

    public static String postInformation(String postInfo, boolean regRun, boolean lastRun) {
    	
    	String finalOutput = "";
    	String needle;
    	String[] haystack = {null};
    	int finalNeedleIndex = 0;
//    	if (lastRun) {
//    		finalNeedleIndex = Integer.parseInt(postInfo);
//		}
    	
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
				    	boolean foundNeedle = false;
				    	for (int j = 0; j < haystack.length; j++) {
				    		if (haystack[j].equalsIgnoreCase(needle)) {
				    			System.out.println("Needle at index: " + j);
				    			foundNeedle = true;
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