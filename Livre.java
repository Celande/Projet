import javax.json.*;
import javax.json.stream.*;

public class Livre extends Document
{
	private int nbPages;
	public static int DUREE = 6*7;
	public static double TARIF = 0.5;

	public Livre(String code, String titre, String auteur, String annee, Localisation loc, Genre genre, int nbPages)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.nbPages = nbPages;
		
		Scanner sc = new Scanner(System.in);
		code = sc.next();
		sc.nextLine();
		titre = sc.next();
		sc.nextLine();
		auteur = sc.next();
		sc.nextLine();
		annee = sc.next();
		sc.nextLine();
		loc = sc.next();
		sc.nextLine();
		genre = sc.next();
		sc.nextLine();
		nbPages = sc.nextInt();
		sc.nextLine();
	}

	public int getDuree()
	{
		return this.DUREE;
	}

	public double getTarif()
	{
		return this.TARIF;
	}

	public int getNbPages()
	{
		return this.nbPages;
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
		   .add("nbPages", nbPages)
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
		   .add("nbPages", nbPages)
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
			      	else if(parser.getString().equals("nbPages"))
			      	{
			      		parser.next();
			      		nbPages = parser.getInt();
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
