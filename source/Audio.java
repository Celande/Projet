public class Audio extends Document
{
	private String classification;
	private static int DUREE = 4*7;
	private static double TARIF = 1.0;	

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

	public String getClassification
	{
		return this.classification;
	}
};