import javax.json.*;
import javax.json.stream.*;

public class Audio extends Document
{
	private String classification;
	public static final int DUREE = 4*7;
	public static final double TARIF = 1.0;	

	public Audio(int[] nbDoc, String titre, String auteur, String annee, Localisation loc, Genre genre, String classification)
	{
		nbDoc[0] = nbDoc[0]+1;
		this.code = "A" + Integer.toString(nbDoc[0]);
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.classification = classification;

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public Audio()
	{
		this.code = "";
		this.titre = "";
		this.auteur = "";
		this.annee = "";

		this.loc = new Localisation();
		this.genre = Genre.AUTRE; // Par défaut

		this.classification = "";

		super.DUREE = this.DUREE;
		super.TARIF = this.TARIF;
	}

	public String getClassification()
	{
		return this.classification;
	}

	@Override
	// Renvoit les données pour un tableau
	public Object[] getTable()
	{
		Object[] tab = {this.code, this.titre, this.auteur, this.annee, this.emprunte, this.nbEmprunts, this.genre, this.classification};
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
		   .add("classification", this.classification)
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
			      	else if(parser.getString().equals("classification"))
			      	{
			      		parser.next();
			      		this.classification = parser.getString();
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