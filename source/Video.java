import javax.json.*;
import javax.json.stream.*;

public class Video extends Document
{
	private int dureeFilm; // en min
	public static final int DUREE = 2*7;
	public static final double TARIF = 1.5;	

	public Video(int[] nbDoc, String titre, String auteur, String annee, Localisation loc, Genre genre, int dureeFilm)
	{
		nbDoc[2] = nbDoc[2]+1;
		this.code = "V" + Integer.toString(nbDoc[2]);
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.dureeFilm = dureeFilm;

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public Video()
	{
		this.code = "";
		this.titre = "";
		this.auteur = "";
		this.annee = "";

		this.loc = new Localisation();
		this.genre = Genre.AUTRE; // Par défaut

		this.dureeFilm = 0;

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public int getDuree()
	{
		return this.DUREE;
	}

	public double getTarif()
	{
		return this.TARIF;
	}

	public int getDureeFilm()
	{
		return this.dureeFilm;
	}

	@Override
	// Renvoit les données pour un tableau
	public Object[] getTable()
	{
		Object[] tab = {this.code, this.titre, this.auteur, this.annee, this.emprunte, this.nbEmprunts, this.genre, this.dureeFilm};
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
		   .add("dureeFilm", this.dureeFilm)
		   .add("loc", this.loc.writeBuilder())
		   .add("genre", this.genre.writeBuilder())
		   .build()
		   ;

	   return obj;
	}

	@Override
	// Lit les attributs du document
	public void read(JsonParser parser) throws Exception
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
			      	else if(parser.getString().equals("dureeFilm"))
			      	{
			      		parser.next();
			      		this.dureeFilm = parser.getInt();
			      	}
			    
			         break;

			         default:
			         break;
			   }
			}
	
		}
		catch(Exception e)
		{
			throw e;
		}
	}
};