import javax.json.*;
import javax.json.stream.*;

public class Localisation
{
	private String salle;
	private String rayon;

	public Localisation(String salle, String rayon)
	{
		this.salle = salle;
		this.rayon = rayon;
	}
	/*	

	public String getSalle()
	{
		return salle;
	}

	public String getRayon()
	{
		return rayon;
	}
	*/

	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("salle", salle)
		   .add("rayon", rayon)
		   //.build();
		   ;

	   return obj;
	}

	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("salle", salle)
		   .add("rayon", rayon)
		   .build();
		   ;

	   return obj;
	}

	public void read(JsonParser parser)
	{
		try{
			while (parser.hasNext()) 
			{
			   JsonParser.Event event = parser.next();
			   switch(event) 
			   {
			      case START_ARRAY:
			      case END_ARRAY:
			      case START_OBJECT:
			      case END_OBJECT:
			      case VALUE_FALSE:
			      case VALUE_NULL:
			      case VALUE_TRUE:
			         break;
			      case KEY_NAME:
			      	if(parser.getString().equals("salle"))
			      	{
			      		parser.next();
			      		salle = parser.getString();
			      	}
			      	else if(parser.getString().equals("rayon"))
			      	{
			      		parser.next();
			      		rayon = parser.getString();
			      	}
			      	else
			      	{
			      		// error
			      	}
			         break;

			         default:
			         break;
			   }
			}
	
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}
	}
};