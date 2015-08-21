//Written by Sweta Karlekar, August 2015, Thomas Jefferson High School

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.swing.JOptionPane;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

 
 //test tweet id: 327473909412814850
public class RetweetLookup {
 
	public static void main(String[] args) throws Exception {
		
		JSONArray retweeters = getRetweetersArray(executeCall(authenticateUser(getAPICallTweets())));
		JSONArray userInfo = getUserInfoArray(executeCall(authenticateUser(getAPICallUsers(JSONArrayToString(retweeters)))));
		printUsers(userInfo);
	}
	public static HttpGet authenticateUser(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException
	{
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
                "DBds2BSKBzppbfkYAX3eksQdC",
                "RbUUE8j6xBUZGuoOloUovUWJDGnTaaIXUWyFNXx6Bw5KmEjKYy"); //Consumer Key, Secret Consumer Key
        consumer.setTokenWithSecret("2417738450-nVhqorvMwDEoYlVR00rAMprM3KEBCN7bJXrV7fW", "nCopndrYkpaU48LE0qbpgZQiZe3eEHaXxqZm1MVyqKKd0"); //Access Token, Secret Access Token
        HttpGet request = new HttpGet(url);
        consumer.sign(request);
        return request;
	}
	public static void printUsers(JSONArray users)
	{
		try{
			File outfile = new File("twitterUsersOutput.txt");
		    FileWriter fwriter = new FileWriter(outfile);
		    PrintWriter pwriter = new PrintWriter(fwriter);
		    //JSONArray parameters = users.getJSONObject(0).names();
		    //pwriter.println(JSONArrayToString(parameters));

			for(int i = 0; i < users.size(); i++)
			{
				pwriter.println(users.getJSONObject(i));//.toJSONArray(parameters));			
				pwriter.println();
			}
			//System.out.println(users.getJSONObject(0).names().size());
			System.out.println("Done printing.");
		    System.out.println(outfile.getCanonicalPath());
		    pwriter.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		
	}
	public static String getAPICallTweets()
	{
		String tweetID = JOptionPane.showInputDialog(null, "Please enter tweet/status id:");
		String url = "https://api.twitter.com/1.1/statuses/retweeters/ids.json?id=" + tweetID + "&count=100&stringify_ids=true";
		return url;
	}
	public static String getAPICallUsers(String userIDs)
	{
		String url = "https://api.twitter.com/1.1/users/lookup.json?user_id=" + userIDs;
		return url;
	}
	public static HttpResponse executeCall(HttpGet request) throws ClientProtocolException, IOException
	{
		 HttpClient client = new DefaultHttpClient();
	     HttpResponse response = client.execute(request);
	     return response;
	}
	public static void printResponse(HttpResponse response) throws UnsupportedOperationException, IOException
	{
		String jsontext = IOUtils.toString(response.getEntity().getContent());
        System.out.println(jsontext);
	}
	public static JSONArray getRetweetersArray(HttpResponse response) throws UnsupportedOperationException, IOException
	{
		String jsontext = IOUtils.toString(response.getEntity().getContent());
		JSONObject json = (JSONObject)JSONSerializer.toJSON(jsontext);
        JSONArray IDarray = json.getJSONArray("ids");
        return IDarray;
	}
	public static JSONArray getUserInfoArray(HttpResponse response) throws UnsupportedOperationException, IOException
	{
		String jsontext = IOUtils.toString(response.getEntity().getContent());
		JSONArray json = (JSONArray)JSONSerializer.toJSON(jsontext);
        return json;
	}
	public static String JSONArrayToString(JSONArray users)
	{
		String usercompilation = "";
		for(int i = 0; i < users.size(); i++)
		{
			usercompilation += users.getString(i) + ",";
		}
		return usercompilation.substring(0, usercompilation.length());
	}
	public static void printStatus(HttpResponse response)
	{
		int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());
	}
}
/*
String url = getAPICallTweets();
HttpGet request = authenticateUser(url);
HttpResponse response = executeCall(request);
printStatus(response);

// printResponse(response); //don't print response and then get the response array
String csvusers = "";
try 
{

    File outfile = new File("twitterUsersOutput.csv");
    FileWriter fwriter = new FileWriter(outfile);
    PrintWriter pwriter = new PrintWriter(fwriter);
    JSONArray users = getResponseArray(response);
    pwriter.println(JSONArrayToString(users));
    csvusers = JSONArrayToString(users);
    pwriter.close();
    System.out.println(outfile.getCanonicalPath());

    String userURL = getAPICallUsers(csvusers);
	HttpGet request2 = authenticateUser(userURL);
	HttpResponse response2 = executeCall(request2);
    printStatus(response2);
    //printResponse(response2);
    String jsontext2 = IOUtils.toString(response2.getEntity().getContent());
	JSONArray json2 = (JSONArray)JSONSerializer.toJSON(jsontext2);
    System.out.println("finished the try catch loop");
    System.out.println(JSONArrayToString(json2));
}
catch(IOException e)
{
	System.out.println(e);
}
*/