public enum CategorieClient
{
	ETUDIANT("Etudiant", 5, 10, 5, 5), PARTICULIER("Particulier", 2, 20, 10, 10), ENTREPRISE("Entreprise", 10, 100, 50, 50); // Donner toutes les catégories possibles

	private String nom;
	private int nbEmpruntsMax;
	private double cotisation;
	private double coefTarif;
	private double coefDuree;
	private Boolean codeReductionActif = false;

	CategorieClient(String nom, int nb, double cot, double tarif, double duree)
	{
		this.nom = nom;
		this.nbEmpruntsMax = nb;
		this.cotisation = cot;
		this.coefTarif = tarif;
		this.coefDuree = duree;
	}

	public void setCategorie(String nom)
	{
		for(CategorieClient c : CategorieClient.values())
		{
			if(c.getNom().equals(nom))
			{
				
				// Copie des informations de c pour les renvoyer

				this.nom = c.getNom();
				this.nbEmpruntsMax = c.getNbEmpruntsMax();
				this.cotisation = c.getCotisation();
				this.coefTarif = c.getCoefTarif();
				this.coefDuree = c.getCoefDuree();

				break;			}
		}
	}

	public String getNom()
	{
		return this.nom;
	}

	public int getNbEmpruntsMax()
	{
		return this.nbEmpruntsMax;
	}

	public double getCotisation()
	{
		return this.cotisation;
	}

	public double getCoefTarif()
	{
		return this.coefTarif;
	}

	public double getCoefDuree()
	{
		return this.coefDuree;
	}

	public String toString()
	{
		return this.nom; 
	}
};