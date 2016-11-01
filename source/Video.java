public class Video extends Document
{
	private int dureeFilm; // en min
	private String mentionsLegales;
	private static int DUREE = 2*7;
	private static double TARIF = 1.5;	

	public Video(String code, String titre, String auteur, String annee, String code, String titre, String auteur, String annee, Localisation loc, Genre genre, int dureeFilm, String mentionsLegales)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;

		this.dureeFilm = dureeFilm;
		this.mentionsLegales = mentionsLegales;
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

	public String getMentionsLegales()
	{
		return this.mentionsLegales;
	}
};