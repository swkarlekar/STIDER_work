//Written by Sweta Karlekar, July 2015, Thomas Jefferson High School


import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
//you can add a weight to each relationship to increase story strength

public class Relationship {
	private Entity source;
	private Entity target; 
	private double latitude;
	private double longitude;
	private Date time;
	private String address; 
	private String identifier;
	private Relationship nextRelation;
	private static final int R = 6371;
	private ArrayList<String> tags = new ArrayList<String>();
	public Relationship(Entity so, Entity tar, String nature, double lat, double lon, String timestamp)
	{
		source = so; 
		source.addOutgoingConnection(this);
		target = tar;
		target.addIncomingConnection(this);
		identifier = nature;
		latitude = lat;
		longitude = lon; 
		Integer year = Integer.parseInt(timestamp.substring(0, timestamp.indexOf("-")));
		timestamp = timestamp.substring(timestamp.indexOf("-")+1);
		Integer month = Integer.parseInt(timestamp.substring(0, timestamp.indexOf("-")));
		timestamp = timestamp.substring(timestamp.indexOf("-")+1);
		Integer day = Integer.parseInt(timestamp.substring(0, timestamp.indexOf(" ")));
		timestamp = timestamp.substring(timestamp.indexOf(" ")+1);
		Integer hour = Integer.parseInt(timestamp.substring(0, timestamp.indexOf(":")));
		timestamp = timestamp.substring(timestamp.indexOf(":")+1);
		Integer minute = Integer.parseInt(timestamp.substring(0, timestamp.indexOf(":")));
		timestamp = timestamp.substring(timestamp.indexOf(":")+1);
		time = new Date(year, month, day, hour, minute);
	}
	public Relationship(Entity so, Entity tar, String nature, double lat, double lon, String timestamp, String add)
	{
		this(so, tar, nature, lat, lon, timestamp);
		address = add; 
	}
	public Entity getSource()
	{
		return source;
	}
	public void setSource(Entity e)
	{
		source = e;
	}
	public Entity getTarget()
	{
		return target;
	}
	public void setTarget(Entity e)
	{
		target = e;
	}
	public double getLatitude()
	{
		return latitude;
	}
	public void setLatitude(double l)
	{
		latitude = l;
	}
	public double getLongitude()
	{
		return longitude;
	}
	public void setLongitude(double l)
	{
		longitude = l;
	}
	public String getNatureOf()
	{
		return identifier;
	}
	public void setNatureOf(String l)
	{
		identifier = l;
	}
	public Date getDate()
	{
		return time;
	}
	public void setDate(Date d)
	{
		time = d;
	}
	public String getAddress()
	{
		return address;
	}
	public void setAddress(String a)
	{
		address = a;
	}
	public void addRelationship(Relationship re)
	{
		if(target.equals(re.getSource()))
		{
			nextRelation = re;
			//nextRelation.getSource().addIncomingConnection();
		}
		else
			System.out.println("Relationship [" + this.toString() + re.toString() + "] was not added. Target did not match source.");
	}
	public Relationship getNextRelationship()
	{
		return nextRelation;
	}
	public String toString()
	{
		return source.getName() + " --> " + identifier + " --> " + target.getName(); //+ " at " + time + " at " + latitude + "° N, "
				//+ longitude + " ° W";
	}
	public ArrayList<String> getTags()
	{
		return tags;
	}
	public double findDistance(double l1, double l2, double l3, double l4)
	{
		double latdistance = Math.toRadians(l3-l1);
		double londistance = Math.toRadians(l4-l2);
		double a = Math.pow(Math.sin(latdistance/2), 2) + Math.cos(Math.toRadians(l1)) * Math.cos(Math.toRadians(l3)) * Math.pow(Math.sin(londistance/2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		return R * c;
	}
	public int findElapsedTime(Relationship b)
	{
		return (int)(this.getDate().getTime() - b.getDate().getTime());
	}
	public int compareTo(Relationship r)
	{
		double distance = StringUtils.getLevenshteinDistance(identifier, r.getNatureOf());
		int naturedist = (int)(1.0/(distance+1)*75);
		int locdistance = (int)(1.0/(this.findDistance(latitude, longitude, r.getLatitude(), r.getLongitude())+1)*30);
		int tempdistance = (int)(1.0/(this.findElapsedTime(r)+1)*15);
		int source2source = source.compareTo(r.getSource())+ source.compareTo(r.getTarget())+ target.compareTo(r.getTarget())+ target.compareTo(r.getSource());
		return (int)(naturedist + locdistance + tempdistance + (source2source)/4.88);
	}

}
