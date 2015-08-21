//Written by Sweta Karlekar, July 2015, Thomas Jefferson High School


import java.util.ArrayList;


public class Story implements Comparable<Story> {
	private Relationship head; 
	private int length = 1;
	private String id = "";
	private int strength = 0;
	public Story(Relationship x, String dd)
	{
		head = x; 
		id = dd;
	}
	public String getId()
	{
		return id;
	}
	public int getLength()
	{
		return length;
	}
	public int getStrength()
	{
		Relationship pointer = head;
		strength += head.getSource().getWeight();
		while(pointer != null)
		{
			strength += pointer.getTarget().getWeight();
			pointer = pointer.getNextRelationship();
		}
		return strength;
	}
	public void addRelationship(Relationship r)
	{
		Relationship pointer = head;
		while(pointer.getNextRelationship() != null)
		{
			pointer = pointer.getNextRelationship();
		}
		pointer.addRelationship(r);
		length += 1;
	}
	//make a delete relationship method
	public String toString()
	{
		Relationship pointer = head;
		String printout = "";
		while(pointer != null)
		{
			printout += pointer.toString() + "\n";
			pointer = pointer.getNextRelationship();
		}
		return printout + " " + getStrength();
	}
	public ArrayList<Relationship> getRelationships()
	{
		Relationship pointer = head;
		ArrayList<Relationship> allrelations = new ArrayList<Relationship>();
		while(pointer != null)
		{
			allrelations.add(pointer);
			pointer = pointer.getNextRelationship();
		}
		return allrelations;
	}
	public ArrayList<Entity> getEntities()
	{
		Relationship pointer = head;
		ArrayList<Entity> entities = new ArrayList<Entity>();
		entities.add(head.getSource());
		while(pointer != null)
		{
			entities.add(pointer.getTarget());
			pointer = pointer.getNextRelationship();
		}
		return entities;
	}
	public int compareTo(Story x)
	{
		ArrayList<Relationship> myrel = getRelationships();
		ArrayList<Relationship> xrel = x.getRelationships();
		int compare = 0;
		for(Relationship a : myrel)
		{
			for(Relationship b : xrel)
			{
				compare += a.compareTo(b);
			}
		}
		return compare;
	}

}
