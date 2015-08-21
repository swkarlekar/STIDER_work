//Written by Sweta Karlekar, July 2015, Thomas Jefferson High School

import java.util.ArrayList;


public abstract class Entity 
{
	private String name = ""; 
	private String id = "";
	private ArrayList<Relationship> inconnections = new ArrayList<Relationship>();
	private ArrayList<Relationship> outconnections = new ArrayList<Relationship>();
	private ArrayList<String> tags = new ArrayList<String>();
	public String getName()
	{
		return name;
	}
	public String getId()
	{
		return id; 
	}
	public void setName(String x)
	{
		name = x; 
	}
	public void setId(String y)
	{
		id = y;
	}
	public int getRelationshipDistances()
	{
		int comparison = 0;
		ArrayList<Relationship> relations = new ArrayList<Relationship>(inconnections);
		relations.addAll(outconnections);
		for(Relationship r1 : relations)
		{
			for(Relationship r2: relations)
			{
				comparison += r1.compareTo(r2);
			}
		}
		return comparison;
	}
	public int getWeight()
	{
			//tweak formula to produce best results
		//System.out.println("getNumAllConnections " + getNumAllConnections()*40);
		//System.out.println("getRelationshipDistances " + getRelationshipDistances()/35);
		return ((getNumAllConnections()*40) + getRelationshipDistances()/35)/2;
	}
	public void addIncomingConnection(Relationship s)
	{
		inconnections.add(s);
	}
	public void setIncomingConnections(ArrayList<Relationship> c)
	{
		inconnections = c;
	}
	public ArrayList<Relationship> getOutgoingConnections()
	{
		return outconnections;
	}
	public int getNumOutgoingConnections()
	{
		return outconnections.size();
	}
	public int getNumIncomingConnections()
	{
		return inconnections.size();
	}
	public int getNumAllConnections()
	{
		return outconnections.size() + inconnections.size();
	}
	public void addOutgoingConnection(Relationship s)
	{
		outconnections.add(s);
	}
	public void setOutgoingConnections(ArrayList<Relationship> c)
	{
		outconnections = c;
	}
	public ArrayList<Relationship> getIncomingConnections()
	{
		return inconnections;
	}
	public String toString()
	{
		return " [" + name + " id:" + id + "]";
	}
	public void addTag(String x)
	{
		tags.add(x.toLowerCase());
	}
	public ArrayList<String> getTags()
	{
		return tags;
	}
	public boolean equals(Entity e)
	{
		return this.getId() == e.getId();
	}
	abstract public int compareTo(Entity x);
}
