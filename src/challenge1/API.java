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
	
    public static void main(String[] args) {
    	
    	String token = "";
 
    	//Register to API
    	String registerJson = "{\"email\":\"adamsombonga@gmail.com\",\"github\":\"http://github.com/adams0619\"}";
    	token = postInformation(registerJson, true); 	
   
    	//Challenge #1
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	String word = postInformation(tokenJson, false);
    	String revWord = reverseMethod(word);
    	String wordJson = "{\"token\":\"" + token + "\",\"result\":\"" + revWord + "\"}";
//    	System.out.println(wordJson);
    	postInformation(wordJson, false);
    	
    
   }

    public static String postInformation(String postInfo, boolean regRun) {
    	
    	String finalOutput = "";
   	
    	try {
    		URL targetUrl;
    		if (regRun) {
    			targetUrl = new URL(API_ENDPOINT);
    		} else {
    			targetUrl = new URL(API_URL_STAGE_1);
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
//				System.out.println(output);
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

//    	System.out.println("Word is:  " + finalOutput);
    	return finalOutput;
    }
    
    public static String reverseMethod(String word) {
    	String revWord = "";
    	for (int i = 0; i < word.length(); i++) { 
    		revWord = word.charAt(i) + revWord;
    	}
//    	System.out.println(revWord);
    	return revWord;
    }
    
}   