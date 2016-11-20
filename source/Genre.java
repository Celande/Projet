import javax.json.*;
import javax.json.stream.*;

/* Mise en place du genre d'un document */
// Nombre limité de genre => Enum

public enum Genre
{
	// Donner tous les genres possibles
	AUTRE("Autre"), FANTASTIQUE("Fantastique"), HORREUR("Frisson & Tereur"), JEUNESSE("Jeunesse"), HISTORIQUE("Historique"), ROMANTIQUE("Romantique"), POLICIER("Policier"), SF("Science-Ficion"), THRILLER("Thriller");

	private String nom;
	private int nbEmprunts;

	Genre(String nom)
	{
		this.nom = nom;
		this.nbEmprunts = 0;
	}

	// Renvoie le genre en fonction de l'id de l'énumération et modifie le nbEmprunts
	Genre setGenre(int ordinal, int nb)
	{
		Genre ge = AUTRE;
		for(Genre g : Genre.values())
		{
			if(ordinal == g.ordinal())
			{
				ge = g;
				break;
			}
		}

		
		ge.setnbEmprunts(nb);

		return ge;
	}

	public void setnbEmprunts(int n)
	{
		this.nbEmprunts = n;
	}

	public String getNom()
	{
		return this.nom;
	}

	public String toString()
	{
		return this.nom;
	}

	// Permet d'écrire le genre en tant qu'objet d'un autre objet
	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("ordinal", this.ordinal())
		    //.add("nbEmprunts", this.nbEmprunts)
		   ;

	   return obj;
	}

	// Ajout d'emprunts
	public void addEmprunts(int n)
	{
		this.nbEmprunts += n;
	}

	// Renvoie le genre après lecture
	public Genre read(JsonParser parser)
	{
		int ordinal = 0;
		int nb = 0;

		int count = 0;

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
			      	if(parser.getString().equals("ordinal"))
			      	{
			      		parser.next();
			      		ordinal = parser.getInt();

			      		count ++;
			      	}
			      	/*
			      	else if(parser.getString().equals("nbEmprunts"))
			      	{
			      		parser.next();
			      		nb = parser.getInt();

			      		count ++;
			      	}
			      	*/
			      	else
			      	{
			      		// error
			      	}
			         break;

			         default:
			         break;
			   }

			   if(count >= 1)
			   {
			   		break;
			   }
			}

			return setGenre(ordinal, nb);
	
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}

		return setGenre(ordinal, nb);
	}
};