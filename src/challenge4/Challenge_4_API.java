package challenge4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import challenge1.Result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
/**
 * CODE2040 API Challenge #4
 * The last challenge is a little different. You’re going to work with dates and times. 
 * The API will again give you a dictionary. The value for datestamp is a string, formatted 
 * as an ISO 8601 datestamp. The value for interval is a number of seconds. You’re going to 
 * add the interval to the date, then return the resulting date to the API. POST your token here:
 *  
 * @author Adams Ombonga
 * @version 1.0
 */
public class Challenge_4_API {
	
	//Class constants	
	/**
	 * String variable containing our API Endpoint link 
	 */	
	private static String API_ENDPOINT = "http://challenge.code2040.org/api/register";
	
	/**
	 * String variable containing our API Challenge link, which will return necessary info for the challenge
	 */	
	private static String API_URL_STAGE_1 = "http://challenge.code2040.org/api/time";
	
	/**
	 * String variable containing our API Challenge verify link, which will let us know if we passed the challenge
	 */
	private static String API_URL_STAGE_2 = "http://challenge.code2040.org/api/validatetime";
	
	/**
	 * DateFormat variable containing the date format in ISO_8601
	 */
	private static DateFormat ISO_8601_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
	
	/**
	 * Main method used to create a JSON that we pass to our postInformaton method which will either retrieve
	 * or send to the API.
	 * 
	 * @param args, not used
	 */
    public static void main(String[] args) {
    	//Set TimeZone for ISO8601 to 0 or no-offset with UTC
        ISO_8601_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        //Token Variable
    	String token = "";
    	
    	//Register to API
    	String registerJson = "{\"email\":\"adamsombonga@gmail.com\",\"github\":\"http://github.com/adams0619\"}";
    	System.out.println("Register JSON is: " + registerJson);
    	token = postInformation(registerJson, true, false); 	
    	System.out.println("Token is: " + token);
   
    	//Challenge #4
    	String tokenJson = "{\"token\":\"" + token + "\"}";
    	String finalTimeStamp = postInformation(tokenJson, false, false);
    	System.out.println("Final Time Stamp is:  " + finalTimeStamp);
    	//Create JSON String with token + datestamp
    	String timeJson = "{\"token\":\"" + token + "\",\"datestamp\":\"" + finalTimeStamp + "\"}";
    	System.out.println("Time JSON: " + timeJson);

    	//Post new TimeStamp String to API
    	postInformation(timeJson, false, true);
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
    	String datestamp;
    	int interval;
    	
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
						datestamp = result.getResult().getDateStamp();
						interval = result.getResult().getInterval();
						//Call method to add interval to datestamp
						finalOutput = appendISO_8601(datestamp, interval);				    	
					}	
				}	
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
     * Method used to return the datestamp with the appended time interval in ISO 8601 Format using the 
     * passed datestamp String and the passed interval in seconds.
     * 
     * @param datestamp, date from API in ISO 8601 format
     * @param interval, seconds to append to the date
     * @return outDate, final date in ISO 8601 format with the seconds added to the datestamp 
     */
    public static String appendISO_8601(String datestamp, long interval) {
    	long finalTimeMilli = 0;
//    	System.out.println("Interval is:    " + interval);
		interval = interval * 1000;
//		System.out.println("NewInterval is: " + interval);
		String outDate = "";
		try {
			//Append TimeZone designation to date stamp
			datestamp = datestamp.substring(0, datestamp.length() - 1) + "UTC";
			System.out.println("New Datestamp: " + datestamp);
			
			//Create a date that will be parsed
			Date parsedDate;
			
			//Convert dateStamp to millisecond
			parsedDate = ISO_8601_FORMAT.parse(datestamp);
			finalTimeMilli = parsedDate.getTime();
//			System.out.println("Parsed date in milli: " + finalTimeMilli);
			finalTimeMilli += interval;
//			System.out.println("Final date in milli:  " + finalTimeMilli);

			//Convert milliseconds to ISO_8601 Date String
			Date finalDate = new Date(finalTimeMilli);
			outDate = ISO_8601_FORMAT.format(finalDate);
			outDate = outDate.replace(outDate.substring(outDate.length() - 5), "Z");
//			System.out.println("Final formatted datestamp: " + outDate.substring(outDate.length() - 5));
//			System.out.println(outDate);
//			System.out.println(datestamp);
		} catch (ParseException e) {
//			e.printStackTrace();
			System.out.println("Error parsing datestamp: " + e);
		}
		return outDate;
    }    
}   