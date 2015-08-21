import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;


public class TwitterService {
	public static String twitterConsumerKey = "DBds2BSKBzppbfkYAX3eksQdC";
	public static String twitterConsumerSecretKey = "RbUUE8j6xBUZGuoOloUovUWJDGnTaaIXUWyFNXx6Bw5KmEjKYy";
	public static String twitterAccessToken = "2417738450-nVhqorvMwDEoYlVR00rAMprM3KEBCN7bJXrV7fW";
	public static String twitterAccessTokenSecret = "nCopndrYkpaU48LE0qbpgZQiZe3eEHaXxqZm1MVyqKKd0";
	
	 public   List<Status> getTweets(String keyword, String startDate, String endDate, int numberOfTweets) {

         boolean result = false;
         Twitter twitter = new TwitterFactory().getInstance();
         twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecretKey);
     AccessToken oathAccessToken = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);
     twitter.setOAuthAccessToken(oathAccessToken);

     List<Status> tweets = null;
     StringBuilder sb2 = new StringBuilder();
         keyword = keyword.toLowerCase().trim();
         Query query = new Query(keyword.toLowerCase().trim());
         query.setCount(numberOfTweets);
         query.setSince(startDate);
         query.setUntil(endDate);
         query.setLang("en");

         QueryResult queryResult = null;
         try {
                 queryResult = twitter.search(query);
         } catch (TwitterException e) {
                 e.printStackTrace();
         }

         if (queryResult != null && queryResult.getCount() >0) {
                 tweets = queryResult.getTweets();
         }

         if (tweets != null && tweets.size() >0) {

                 sb2.append("keyword: " + keyword + " ---> " + tweets.size() + "\n");
         }


     System.out.println(sb2.toString());
     return tweets;
 }

}
