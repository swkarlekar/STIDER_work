import org.apache.commons.lang3; 
public class Person implements Entity
{	
	private String name; 
	private String id; 
	private String firstname;
	private String lastname;
	public Person(String x, String y)
	{
		name = x; 
		id = y;
	}
	public Person(String x1, String x2, String y)
	{
		firstname = x1;
		lastname = x2;
		name = firstname + " " + lastname;
		id = y;
	}
	public String getName()
	{
		return name; 
	}
	public String getFirstName()
	{
		if(firstname!=null)
			return firstname;
		return name.split(" ")[0];
	}
	public String getLastName()
	{
		if(lastname!=null)
			return lastname;
		return name.split(" ")[1];
	}
	public String getId()
	{
		return id;
	}
	public void setName(String x)
	{
		name = x;
		firstname = name.split(" ")[0];
		lastname = name.split(" ")[1];
	}
	public void setFirstName(String x)
	{
		firstname = x;
		name = firstname+" "+lastname;
	}
	public void setLastName(String x)
	{
		lastname = x;
		name = firstname+ " " + lastname;
	}
	public void setId(String y)
	{
		id = y;
	}
	public String toString()
	{
		return name + " " + id; 
	}
	public int compareTo(Person a)
	{
		return 2;
	}

}

/*
attributes of a person: 
salary
occupation
gender
sex
*/
