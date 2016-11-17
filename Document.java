import javax.json.*;
import javax.json.stream.*;

abstract class Document
{
	protected String code;
	protected String titre;
	protected String auteur;
	protected String annee;
	protected Boolean empruntable = false;
	public Boolean emprunte = false; //
	protected int nbEmprunts = 0;

	protected Localisation loc;
	protected Genre genre;

	public static int DUREE;
	public static double TARIF;

	public Document()
	{

	}

/*
	public Document(String code, String titre, String auteur, String annee, Localisation loc, Genre genre)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;
	}
	*/

	public JsonObject write() // done to be override
	{
		return Json.createObjectBuilder().build();
	}

	public JsonObjectBuilder writeBuilder() // done to be override
	{
		return Json.createObjectBuilder();
	}

	public void read(JsonParser parser) // done to be override
	{

	}

	public Boolean fromStringToBool(String s)
	{
		if(s.equals("true"))
		{
			return true;
		}

		return false;
	}

	public Localisation getLoc()
	{
		return loc;
	}

	public Genre getGenre()
	{
		return genre;
	}

	public String getTitre()
	{
		return titre;
	}
};