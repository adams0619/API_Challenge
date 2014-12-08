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
 
 
public class API {
	
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/getstring";
	
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validatestring";
	
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

    public static String postInformation(String postInfo, boolean regRun, boolean lastRun) {
    	
    	String finalOutput = "";
   	
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
    
    public static String reverseMethod(String word) {
    	String revWord = "";
    	for (int i = 0; i < word.length(); i++) { 
    		revWord = word.charAt(i) + revWord;
    	}
    	return revWord;
    }
    
}   