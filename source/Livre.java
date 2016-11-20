import javax.json.*;
import javax.json.stream.*;

public class Livre extends Document
{
	private int nbPages;
	public static final int DUREE = 6*7;
	public static final double TARIF = 0.5;

	public Livre(String code, String titre, String auteur, String annee, Localisation loc, Genre genre, int nbPages)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.nbPages = nbPages;

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public Livre()
	{
		this.code = "";
		this.titre = "";
		this.auteur = "";
		this.annee = "";

		this.loc = new Localisation();
		this.genre = Genre.AUTRE; // Par défaut

		this.nbPages = 0;

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public int getNbPages()
	{
		return this.nbPages;
	}

	@Override
	// Renvoit les données pour un tableau
	public Object[] getTable()
	{
		Object[] tab = {this.code, this.titre, this.auteur, this.annee, this.emprunte,this. nbEmprunts, this.genre, this.nbPages};
		return tab;
	}

	@Override
	// Renvoit un objet décrivant les attributs du document
	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("code", this.code)
		   .add("titre", this.titre)
		   .add("auteur", this.auteur)
		   .add("annee", this.annee)
		   .add("empruntable", this.empruntable.toString())
		   .add("emprunte", this.emprunte.toString())
		   .add("nbEmprunts", this.nbEmprunts)
		   .add("nbPages", this.nbPages)
		   .add("loc", this.loc.writeBuilder())
		   .add("genre", this.genre.writeBuilder())
		   .build()
		   ;

	   return obj;
	}

	@Override
	// Lit les attributs du document
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
			      		this.code = parser.getString();
			      	}
			      	else if(parser.getString().equals("titre"))
			      	{
			      		parser.next();
			      		this.titre = parser.getString();
			      	}
			      	else if(parser.getString().equals("auteur"))
			      	{
			      		parser.next();
			      		this.auteur = parser.getString();
			      	}
			      	else if(parser.getString().equals("annee"))
			      	{
			      		parser.next();
			      		this.annee = parser.getString();
			      	}
			      	else if(parser.getString().equals("empruntable"))
			      	{
			      		parser.next();
			      		this.empruntable = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("emprunte"))
			      	{
			      		parser.next();
			      		this.emprunte = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("nbEmprunts"))
			      	{
			      		parser.next();
			      		this.nbEmprunts = parser.getInt();
			      	}
			      	else if(parser.getString().equals("nbPages"))
			      	{
			      		parser.next();
			      		this.nbPages = parser.getInt();
			      	}
			      	else if(parser.getString().equals("loc"))
			      	{
			      		parser.next();
			      		this.loc.read(parser);
			      	}
			      	else if(parser.getString().equals("genre"))
			      	{
			      		parser.next();
			      		this.genre = genre.read(parser);
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