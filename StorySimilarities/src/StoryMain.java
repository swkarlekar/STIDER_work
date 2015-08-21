//Written by Sweta Karlekar, July 2015, Thomas Jefferson High School

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;


public class StoryMain {
	public static Entity a, b, c, d, e, f, g, h, i, j, k;
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	public static Relationship first, second, third, fourth, fifth, sixth, seventh, eighth, ninth, tenth, eleventh, twevelth, thirteenth, fourteenth;
	public static ArrayList<Story> stories = new ArrayList<Story>();
	
	public static void main(String[] args) {
		createEntities();
		createRelationships();
		createStories();
		//System.out.print(findMostConnectedEntity());
		//System.out.print(a.getAllConnections());
		//System.out.println(stories.get(0));
		//System.out.println(stories.get(0).getEntities());
		compareStories();
	}
	public static void createEntities()
	{
		a = new Person("Sweta Karlekar", "1");
		a.addTag("female");
		a.addTag("high school");
		a.addTag("Thomas Jefferson High School");
		
		b = new Person("Esha Karlekar", "2");
		b.addTag("female");
		b.addTag("middle school");
		
		c = new Person("Disha Jain", "3");
		c.addTag("high school");
		c.addTag("Thomas Jefferson High School");

		d = new Person("David Zhao", "4");
		d.addTag("male");
		d.addTag("high school");
		d.addTag("Thomas Jefferson High School");

		e = new Person("Jackie Woodwell", "5");
		e.addTag("male");
		e.addTag("Geospatial Research Center");
		
		f = new Person("Sujata Karlekar", "6");
		f.addTag("female");
		f.addTag("INOVA Physical Therapy");
		
		g = new Person("Kevin Glazer", "7");
		g.addTag("male");
		g.addTag("thomas jefferson high school");
		g.addTag("principal");
		g.addTag("math teacher");
		
		h = new Person("Christine Conklin", "8");
		h.addTag("thomas jefferson high school");
		h.addTag("female");
		h.addTag("latin teacher");
		h.addTag("Rubik's Cube");
		
		i = new Person("Teresa Li", "9");
		i.addTag("thomas jefferson high school");
		i.addTag("female");
		i.addTag("Geospatial research center");
		
		j = new Organization("Sweta Karlekar Foundation", "10");
		j.addTag("charity");
		
		k = new Item("apple", "11");
		k.addTag("fruit");
		
		entities.add(a);
		entities.add(b);
		entities.add(c);
		entities.add(d);
		entities.add(e);
		entities.add(f);
		entities.add(g);
		entities.add(h);
		entities.add(i);
		entities.add(j);
		entities.add(k);
	}
	public static void createRelationships()
	{
		first = new Relationship(a, b, "is sisters with", 40.400, 3.7167, "1997-04-16 11:15:00");
		second = new Relationship(b, c, "met", 40.400, 3.7167, "1997-04-16 11:15:00");
		third = new Relationship(c, d, "works with", 40.400, 3.7167, "1997-04-16 11:15:00");
		fourth = new Relationship(d, e, "eats with", 40.400, 3.7167, "1997-04-16 11:15:00");
		fifth = new Relationship(c, d, "works with", 40.400, 3.7167, "1997-04-16 11:15:00");
		sixth = new Relationship(d, e, "eats with", 40.400, 3.7167, "1997-04-16 11:15:00");
		seventh = new Relationship(e, f, "swam with", 40.400, 3.7167, "1997-04-16 11:15:00");
		eighth = new Relationship(f, h, "went swimming with", 40.400, 3.7167, "1997-04-16 11:15:00");
		ninth = new Relationship(h, j, "went to school with", 40.400, 3.7167, "1997-04-16 11:15:00");
		tenth = new Relationship(j, k, "buys", 40.400, 3.7167, "1997-04-16 11:15:00");
		eleventh = new Relationship(a, k, "eats", 40.400, 3.7167, "1997-04-16 11:15:00");
		twevelth = new Relationship(k, b, "was bought by", 40.400, 3.7167, "1997-04-16 11:15:00");
		thirteenth = new Relationship(b, i, "eats lunch with", 40.400, 3.7167, "1997-04-16 11:15:00");
		fourteenth = new Relationship(i, h, "eats breakfast with", 40.400, 3.7167, "1997-04-16 11:15:00");
	}
	public static void createStories()
	{
		Story story1 = new Story(first, "1s");
		story1.addRelationship(second);
		story1.addRelationship(third);
		story1.addRelationship(fourth);
		
		
		Story story2 = new Story(fifth, "2s");
		story2.addRelationship(sixth);
		
		
		Story story3 = new Story(seventh, "3s");
		story3.addRelationship(eighth);
		story3.addRelationship(ninth);
		story3.addRelationship(tenth);
		
		Story story4 = new Story(eleventh, "4s");
		story4.addRelationship(twevelth);
		story4.addRelationship(thirteenth);
		story4.addRelationship(fourteenth);
		
		stories.add(story1);
		stories.add(story2);
		stories.add(story3);
		stories.add(story4);
	}
	public static Entity findMostConnectedEntity()
	{
		Entity maxE = entities.get(0);
		int max = 0;
		for(Entity e: entities)
		{
			if(max < e.getWeight())
			{
				max = e.getWeight();
				maxE = e;
			}
		}
		return maxE;
	}
	public static void compareStories()
	{
		Story toCompare = stories.get(0);
		TreeMap<Story, Integer> comparison = new TreeMap<Story, Integer>();
		for(Story s : stories)
		{
			comparison.put(s, toCompare.compareTo(s));
		}
		Iterator<Entry<Story, Integer>> iter = comparison.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Story, Integer> entry = iter.next();
            System.out.print(entry.getKey());
            //System.out.print("=");
            //System.out.print(entry.getValue());
            if (iter.hasNext()) {
                System.out.println();
            }
        }
		
	}

}
