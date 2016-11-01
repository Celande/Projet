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

	public Document(String code, String titre, String auteur, String annee, Localisation loc, Genre genre)
	{
		this.code = code;
		this.titre = titre;
		this.auteur = auteur;
		this.annee = annee;

		this.loc = loc;
		this.genre = genre;
	}
};