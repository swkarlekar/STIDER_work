//Written by Sweta Karlekar, July 2015, Thomas Jefferson High School

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.*;

 
public class Person extends Entity{

	
	private String name; 
	private String id; 
	private int numconnections;
	private ArrayList<String> tags = new ArrayList<String>(); 
	public Person(String x, String y)
	{
		super.setName(x);
		String[] array = x.split(" ");
		for(int i = 0; i < array.length; i++)
			super.addTag(array[i]); //adds name to tags to look for similarities 
		super.setId(y);
		//super.setIncomingConnections(0);
		//super.setOutgoingConnections(0);
	}
	public int compareTo(Entity a) {
		int tagsims = (int)(CollectionUtils.intersection(this.getTags(),a.getTags()).size() * 1);
		double distance = StringUtils.getLevenshteinDistance(this.getName(), a.getName());
		int weighteddistance = (int)(1.0/(distance+1)*240);
		return weighteddistance+tagsims*40;
	}
}
