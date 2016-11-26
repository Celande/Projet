import javax.json.*;
import javax.json.stream.*;

/* Mise en place di document */
// Ne sera jamais utilisé tel quel => Abstract

abstract class Document
{
	protected String code;
	protected String titre;
	protected String auteur;
	protected String annee;
	protected Boolean empruntable = true;
	public Boolean emprunte = false; //
	protected int nbEmprunts = 0;

	protected Localisation loc;
	protected Genre genre;

	public int DUREE;
	public double TARIF;

	public Document()
	{
		this.code = "";
		this.titre = "";
		this.auteur = "";
		this.annee = "";

		this.loc = new Localisation();
		this.genre = Genre.HORREUR;
	}

	public String getCode()
	{
		return this.code;
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

	public String getAuteur()
	{
		return auteur;
	}

	public String getAnnee()
	{
		return this.annee;
	}

	public int getNbEmprunts()
	{
		return this.nbEmprunts;
	}

	// Renvoit les données pour un tableau
	// Fait pour être surchargé par les classes filles
	public Object[] getTable()
	{
		Object[] tab = {this.code, this.titre, this.auteur, this.annee, this.emprunte, this.nbEmprunts, this.genre};
		return tab;
	}

	public String toString()
	{
		return this.titre;
	}

	public void addEmprunt()
	{
		this.empruntable = false;
		this.emprunte = true;
		this.nbEmprunts ++;
	}

	public void rmEmprunt()
	{
		this.empruntable = true;
		this.emprunte = false;
	}

	public Boolean fromStringToBool(String s)
	{
		if(s.equals("true"))
		{
			return true;
		}

		return false;
	}

	// Renvoit un objet décrivant les attributs du document
	// Fait pour être surchargé par les classes filles
	public JsonObject write()
	{
		return Json.createObjectBuilder().build();
	}

	// Lit les attributs du document
	// Fait pour être surchargé par les classes filles
	public void read(JsonParser parser) throws Exception
	{

	}
};