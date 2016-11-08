import javax.json.*;
import javax.json.stream.*;

public enum Genre
{
	// Donner tous les genres possibles
	FANTASTIQUE("Fantastique"), HORREUR("Frisson & Tereur"), JEUNESSE("Jeunesse"), HISTORIQUE("Historique"), ROMANTIQUE("Romantique"), POLICIER("Policier"), SF("Science-Ficion"), THRILLER("Thriller");

	private String nom;
	private int nbEmprunts;

	Genre(String nom)
	{
		this.nom = nom;
		this.nbEmprunts = 0;
	}

	Genre(String nom, int nbEmprunts)
	{
		this.nom = nom;
		this.nbEmprunts = nbEmprunts;
	}

	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("nom", nom)
		   //.add("nbEmprunts", nbEmprunts)
		   //.build();
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
			      	if(parser.getString().equals("nom"))
			      	{
			      		parser.next();
			      		nom = parser.getString();
			      	}
			      	else if(parser.getString().equals("nbEmprunts"))
			      	{
			      		parser.next();
			      		nbEmprunts = parser.getInt();
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

/*
Romans & Fictions
	Aventure & Action
Classiques
Erotique
Espionnage
Fantastique
Frisson & Terreur 	Guerre
Jeunesse
Historique
Littérature sentimentale
Policier
Régional/Terroir 	Roman
Science-Fiction
Nouvelles
Anthologies
Fantasy
Thriller
Western
*/