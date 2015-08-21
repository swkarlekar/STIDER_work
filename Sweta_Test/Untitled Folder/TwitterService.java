package mil.army.usace.tec.strider.graph.services;
// -XX:MaxPermSize=512m
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import mil.army.usace.tec.strider.graph.util.DatabaseHelp;
import mil.army.usace.tec.strider.graph.util.Help;
import mil.army.usace.tec.strider.graph.util.Helper;
import mil.army.usace.tec.strider.graph.util.KeyGenerator;
import mil.army.usace.tec.strider.graph.util.OAuth;
import mil.army.usace.tec.strider.model.DocumentResponse;
import mil.army.usace.tec.strider.model.Tweet;
import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.UserMentionEntity;
import twitter4j.auth.AccessToken;

@Service("twitterservice")
public class TwitterService {	
	@Value("${environment}")  private String environment;
	
	@Value("${twitter.consumer.key}")  private String twitterConsumerKey;
	@Value("${twitter.consumer.secret.key}") private String twitterConsumerSecretKey;
	@Value("${twitter.access.token}") private String twitterAccessToken;
	@Value("${twitter.access.token.secret}") private String twitterAccessTokenSecret;
	
	@Value("${twitter.storage.folder}") private String twitterStorageFolder;
	@Value("${twitter.storage.folder.prod}") private String twitterStorageFolderProd;	
	
	@Value("${twitter.archive.folder}") private String twitterArchiveFolder;
	@Value("${twitter.archive.folder.prod}") private String twitterArchiveFolderProd;
	
	@Value("${twitter.base.url}") private String twitterBaseUrl;
	@Value("${oauth.consumer.key}") private String oauth_consumer_key;
	
	@Value("${oauth.consumer.secret.key}") private String oauth_consumer_secret_key;
	@Value("${oauth.token.secret.key}") private String oauth_token_secret_key;
	
	@Value("${oauth.signature.method}") private String oauth_signature_method;
	@Value("${oauth.nonce}") private String oauth_nonce;
	@Value("${oauth.token}") private String oauth_token;
	@Value("${oauth.version}") private String oauth_version;
	final static Charset ENCODING = StandardCharsets.UTF_8;
	
	public TwitterService() {		
	}

	public  String persistEntity(String entityName, int entityType,
			int entityDataSource, JdbcTemplate jdbc) {
		String entityId = null;
		// before inserting a new entity, we must first check if it already
		// exists. If it does, we do not insert again.
		Connection conn = null;
		try {
			entityName = "#allfixed";
			conn = jdbc.getDataSource().getConnection();
			conn.setAutoCommit(false);
			String commandText = "{?=call find_entities_by_name(?)}";
			CallableStatement stmt = conn.prepareCall(commandText);
			stmt.setNull(1, Types.OTHER);
			stmt.registerOutParameter(1, Types.OTHER);
			stmt.setString(2, entityName);
			stmt.execute();
			ResultSet rs = (ResultSet) stmt.getObject(1);
			conn.close();
			while (rs.next()) {
				String myEntityId = rs.getString("entity_id");
			//	System.out.println(rs.getString("entity_id") + "-->"
			//			+ rs.getString("entity_name") + "-->"
			//			+ rs.getString("entity_type") + "-->"
			//			+ rs.getDouble("entity_datasource"));
				return myEntityId;
			}
			// the entity does not already exist, will insert it into the db
			String sql = "select insert_entity(?,?,?,?)";
			entityId = Help.generateId();
			jdbc.queryForInt(sql, entityId, entityName, entityType,
					entityDataSource);
		} catch (DataAccessException e) {
			e.printStackTrace();
		} catch (SQLException se) {
			se.printStackTrace();
		}
		return entityId;
	}
	//-----------------------

	

	public boolean prepareToFetchTweets(String keywords, String startDate, String endDate, int numberOfTweets) {
		
		 if (environment.equals("prod")) {
			 twitterStorageFolder = twitterStorageFolderProd;
		 }
		
		boolean success = false;
		//String storageFolder = "G:\\crawlStorageFolder\\Files";
		ArrayList<String> queryWords = Help.readQueryWords(keywords);
		
		for (int z=0;z<queryWords.size();z++) {	
		String word = queryWords.get(z).trim().toLowerCase();
		List<Status> tweets = getTweets(word, startDate, endDate, numberOfTweets);
		success = DatabaseHelp.saveJSONTweets(tweets, twitterStorageFolder, word);
		}
		return success;
	}
    /************************************************/
	/* 6.1 big data*/
	/***********************************************/
	public DocumentResponse getStreamingTweets(String keywords, int numberOfTweets) {
		 if (environment.equals("prod")) {
			 twitterArchiveFolder = twitterArchiveFolderProd;
		 }

		String oauth_timestamp = null;
		
	     final SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");
	     sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	     Long l = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();
	  //   Date date = Calendar.getInstance().getTime();
		 oauth_timestamp = String.valueOf(l/1000);
	
		int z = 0;
	
		String HTTPMethod = "POST";
		String twitterKey = KeyGenerator.generate(twitterBaseUrl, oauth_consumer_key, oauth_nonce, 
				oauth_signature_method, oauth_timestamp, oauth_token, oauth_version, keywords,
				HTTPMethod, oauth_consumer_secret_key, oauth_token_secret_key);
		
		HttpClient client = new HttpClient();
		PostMethod postMethod = new PostMethod(twitterBaseUrl);
		StringBuffer header = new StringBuffer();
		String cleanKeywords = keywords.replace(",", "-");
		cleanKeywords = cleanKeywords.replace(" ", "");
		cleanKeywords = cleanKeywords.replace("'", "");
		
		String path;
		if (cleanKeywords.length()<40) path =  twitterArchiveFolder + File.separator + numberOfTweets + "-" + cleanKeywords;
		else path = twitterArchiveFolder + File.separator + numberOfTweets + "-" + cleanKeywords.substring(0,40) ;

		header.append("OAuth ");
		header.append(OAuth.percentEncode("oauth_consumer_key") + "=\"" + OAuth.percentEncode(oauth_consumer_key) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_nonce") + "=\"" + OAuth.percentEncode(oauth_nonce) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_signature") + "=\"" + OAuth.percentEncode(twitterKey) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_signature_method") + "=\"" + OAuth.percentEncode(oauth_signature_method) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_timestamp") + "=\"" + OAuth.percentEncode(oauth_timestamp) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_token") + "=\"" + OAuth.percentEncode(oauth_token) + "\"" + ",");
		header.append(OAuth.percentEncode("oauth_version") + "=\"" + OAuth.percentEncode(oauth_version) + "\"");
		
		postMethod.setRequestHeader("Authorization", header.toString());
		
		postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		postMethod.setParameter("track", keywords.trim());
		postMethod.setParameter("lang","en");
		InputStream is;
		DocumentResponse dr;
		try {
			 z = client.executeMethod(postMethod);
			is = postMethod.getResponseBodyAsStream();
			int count = 0;
			dr = Helper.convertStreamToString(is, path, numberOfTweets);
			System.out.println("going back");
			
			//is.close();
			//return  "success";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();	
			dr = new DocumentResponse("error","error","error",0, false);
			
		}		
		
		return dr;
	}
		
		

	
	public   List<Status> getTweets(String keyword, String startDate, String endDate, int numberOfTweets) {
		

		boolean result = false;
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(twitterConsumerKey, twitterConsumerSecretKey);
	    AccessToken oathAccessToken = new AccessToken(twitterAccessToken, twitterAccessTokenSecret);	 
	    twitter.setOAuthAccessToken(oathAccessToken);	
	 

	//    ArrayList<String> queryWords = Help.readQueryWords(keyword);
	  //  String word = null;
	    List<Status> tweets = null;
	    StringBuilder sb2 = new StringBuilder();
	 //   for (int m=0;m<queryWords.size();m++) {
	    //	word = queryWords.get(m).trim();
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
	    		//result = Help.saveRawTweets(tweets, storageFolder, word);
	    		//result = Help.saveJSONTweets(tweets, storageFolder, word);
	    	//	result = Help.saveRawTweets(tweets, storageFolder, word);
	    	}
	//    }
	    
	   // StatusJSONImpl a= new StatusJSONImpl();
	    System.out.println(sb2.toString());
	    return tweets;
		//return result;
	}

	
	private String printUserMentionEntities(UserMentionEntity[] userMentionEntities){
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t");
		for(UserMentionEntity entity: userMentionEntities){
			sb.append(entity.getScreenName()).append(",").append(entity.getName());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private String printHashtagEntities(HashtagEntity[] hashtagEntities){
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t");
		for(HashtagEntity entity: hashtagEntities){
			sb.append(entity.getText());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	private String printMediaEntities(MediaEntity[] mediaEntities){
		StringBuilder sb = new StringBuilder();
		sb.append("\t\t");
		for(MediaEntity entity: mediaEntities){
			sb.append(entity.getURL());
			sb.append(" ");
		}
		return sb.toString();
	}
	
	public boolean generateImages(String text) {
		boolean result = false;
				//String text = "I love donuts with ice cream on top and in the middle in Paris. But when I go to New York or San Francisco, I prefer lasagna.";
		        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		        Graphics2D g2d = img.createGraphics();
		        Font font = new Font("Arial", Font.PLAIN, 48);
		        g2d.setFont(font);
		        FontMetrics fm = g2d.getFontMetrics();
		        int width = fm.stringWidth(text);
		        int height = fm.getHeight();
		        g2d.dispose();

		        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		        g2d = img.createGraphics();
		        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		        g2d.setFont(font);
		        fm = g2d.getFontMetrics();
		        g2d.setColor(Color.BLACK);
		        g2d.drawString(text, 0, fm.getAscent());
		        g2d.dispose();
		        try {
		            ImageIO.write(img, "png", new File("G:/images/Text.png"));
		            result = true;
		        } catch (IOException ex) {
		            ex.printStackTrace();
		        }
		        return result;
	}
	

	
	public String saveSimpleTwitterJSON (String filePath) {
		 if (environment.equals("prod")) {
			 twitterStorageFolder = twitterStorageFolderProd;
		 }
		
		boolean success = false;
		String fileName = "error";
		try{
			Path path = Paths.get(filePath);
			BufferedReader reader = Files.newBufferedReader(path, ENCODING);
			fileName = twitterStorageFolder + File.separator + path.getFileName() + ".simple";
			FileWriter fstream = new FileWriter(fileName, true);
			BufferedWriter out = new BufferedWriter(fstream);
	        String line;
	        while ((line = reader.readLine()) != null) {        	
	        		if (line.length() > 0 && !line.equals("") && !line.equals(" "))	{
	        			Tweet tweet = Helper.createTweet(line);
	        			JSONObject oneTweet = new JSONObject();
	        			oneTweet.element("tweetText", tweet.getText());
	        			oneTweet.element("tweetLat", tweet.getLat());
	        			oneTweet.element("tweetLon", tweet.getLon());
	        			oneTweet.element("tweetUserLat", tweet.getUserLat());
	        			oneTweet.element("tweetUserLon", tweet.getUserLat());
	        			oneTweet.element("tweetPublishedDate", tweet.getCreatedAt());
	        			oneTweet.element("tweetScreenName", tweet.getScreenName());
	        			oneTweet.element("tweetUserLocation", tweet.getUserLocation());
	        			oneTweet.element("tweetPlaceName", tweet.getPlaceName());
	        			oneTweet.element("tweetId", tweet.getId());	
	        			oneTweet.element("tweetLanguage", tweet.getTweetLanguage());	
	        			out.write(oneTweet.toString() + "\n");
	        		}        	
	        	}
	        	success = true;
	        	out.close();
	    		    		
			} catch (Exception e) {
				e.printStackTrace();		
								  }
		return fileName;
	}
	
	
	
	
	
}