public class Livre extends Document
{
	private int nbPages;
	private static int DUREE = 6*7;
	private static double TARIF = 0.5;

	public Livre(String code, String titre, String auteur, String annee, Localisation loc, Genre genre, int nbPages)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.nbPages = nbPages;
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
};