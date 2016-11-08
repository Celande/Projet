import javax.json.*;
import javax.json.stream.*;

public class Audio extends Document
{
	private String classification;
	public static int DUREE = 4*7;
	public static double TARIF = 1.0;	

	public Audio(String code, String titre, String auteur, String annee, Localisation loc, Genre genre, String classification)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.classification = classification;
	}
	

	public int getDuree()
	{
		return this.DUREE;
	}

	public double getTarif()
	{
		return this.TARIF;
	}

	public String getClassification()
	{
		return this.classification;
	}

	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("code", code)
		   .add("titre", titre)
		   .add("auteur", auteur)
		   .add("annee", annee)
		   .add("empruntable", empruntable.toString())
		   .add("emprunte", emprunte.toString())
		   .add("nbEmprunts", nbEmprunts)
		   .add("classification", classification)
		   // localisation
		   .add("loc", loc.writeBuilder())
		   // genre
		   .add("genre", genre.writeBuilder())
		   .build()
		   ;

	   return obj;
	}

	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("code", code)
		   .add("titre", titre)
		   .add("auteur", auteur)
		   .add("annee", annee)
		   .add("empruntable", empruntable.toString())
		   .add("emprunte", emprunte.toString())
		   .add("nbEmprunts", nbEmprunts)
		   .add("classification", classification)
		   // localisation
		   .add("loc", loc.writeBuilder())
		   // genre
		   .add("genre", genre.writeBuilder())
		   //.build()
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
			      	if(parser.getString().equals("code"))
			      	{
			      		parser.next();
			      		code = parser.getString();
			      	}
			      	else if(parser.getString().equals("titre"))
			      	{
			      		parser.next();
			      		titre = parser.getString();
			      	}
			      	else if(parser.getString().equals("auteur"))
			      	{
			      		parser.next();
			      		auteur = parser.getString();
			      	}
			      	else if(parser.getString().equals("annee"))
			      	{
			      		parser.next();
			      		annee = parser.getString();
			      	}
			      	else if(parser.getString().equals("empruntable"))
			      	{
			      		parser.next();
			      		empruntable = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("emprunte"))
			      	{
			      		parser.next();
			      		emprunte = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("nbEmprunts"))
			      	{
			      		parser.next();
			      		nbEmprunts = parser.getInt();
			      	}
			      	// localisation
			      	else if(parser.getString().equals("loc"))
			      	{
			      		parser.next();
			      		loc.read(parser);
			      	}
			      	// genre
			      	else if(parser.getString().equals("genre"))
			      	{
			      		parser.next();
			      		genre.read(parser);
			      	}
			      	else if(parser.getString().equals("classification"))
			      	{
			      		parser.next();
			      		classification = parser.getString();
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