import javax.json.*;
import javax.json.stream.*;

/* Mise en place de la localisation d'un document */

public class Localisation
{
	private String salle;
	private String rayon;

	public Localisation()
	{
		this.salle = "";
		this.rayon = "";
	}

	public Localisation(String salle, String rayon)
	{
		this.salle = salle;
		this.rayon = rayon;
	}
		

	public String getSalle()
	{
		return this.salle;
	}

	public String getRayon()
	{
		return this.rayon;
	}
	

	public String toString()
	{
		return "salle : " + this.salle + " rayon : " + this.rayon;
	}

	// Permet d'écrire la localisation en tant qu'objet d'un autre objet
	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("salle", this.salle)
		   .add("rayon", this.rayon)
		   ;

	   return obj;
	}

	// Permet d'écrire la localisation
	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("salle", this.salle)
		   .add("rayon", this.rayon)
		   .build();
		   ;

	   return obj;
	}

	// Lit les attributs de la localisation
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
			      		this.salle = parser.getString();
			      	}
			      	else if(parser.getString().equals("rayon"))
			      	{
			      		parser.next();
			      		this.rayon = parser.getString();
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