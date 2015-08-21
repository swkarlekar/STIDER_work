//Written by Sweta Karlekar, August 2015, Thomas Jefferson High School

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import javax.swing.JFrame;
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

import java.net.URLEncoder;

 
 //test tweet id: 327473909412814850
public class TweetLookUp {

	public static void callTwitter(String query, String geocode, String language, String resultType, String count, String until, String outputFile) throws Exception
	{
		JSONArray retweeters = getStatusArray(executeCall(authenticateUser(getAPICallTweets(query, geocode, language, resultType, count, until))));
		printUsers(retweeters, outputFile);
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
	public static void printUsers(JSONArray users, String outputFile)
	{
		try{
			File outfile = new File(outputFile);
		    FileWriter fwriter = new FileWriter(outfile);
		    PrintWriter pwriter = new PrintWriter(fwriter);

			for(int i = 0; i < users.size(); i++)
			{
				pwriter.println(users.getJSONObject(i));			
				pwriter.println();
			}
			System.out.println("Done printing.");
		    System.out.println(outfile.getCanonicalPath());
		    pwriter.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
		
	}
	public static String getAPICallTweets(String query, String geocode, String language, String resultType, String count, String until) throws UnsupportedEncodingException
	{
		query = URLEncoder.encode(query, "UTF-8");
		String url = "https://api.twitter.com/1.1/search/tweets.json?q=" + query;
		if(!geocode.isEmpty()){
			url += "&geocode="+geocode;
		}
		if(!language.isEmpty()){
			url += "&lang="+language;
		}
		if(!resultType.isEmpty()){
			url += "&result_type=" + resultType;
		}
		if(!count.isEmpty()){
			url += "&count=" + count; 
		}
		if(!until.isEmpty()){
			url += "&until=" + until;
		}
		System.out.println(url);
		return url;
	}
	public static JSONArray getStatusArray(HttpResponse response) throws UnsupportedOperationException, IOException
	{
		String jsontext = IOUtils.toString(response.getEntity().getContent());
		JSONObject json = (JSONObject)JSONSerializer.toJSON(jsontext);
        JSONArray IDarray = json.getJSONArray("statuses");
        return IDarray;
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
	public static void printStatus(HttpResponse response)
	{
		int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());
	}
}
