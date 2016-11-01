public enum Genre
{
	// Donner tous les genres possibles
	FANTASTIQUE("Fantastique"), HORREUR("Frisson & Tereur"), JEUNESSE("Jeunesse"), HISTORIQUE("Historique"), ROMANTIQUE("Romantique"), POLICIER("Policier"), SF("Science-Ficion"), THRILLER("Thriller");

	private String nom;
	private int nbEmprunts;

	Genre(String nom)
	{
		this.nom = nom;
		this.nbEmprunts = 0;
	}

	Genre(String nom, int nbEmprunts)
	{
		this.nom = nom;
		this.nbEmprunts = nbEmprunts;
	}
};

/*
Romans & Fictions
	Aventure & Action
Classiques
Erotique
Espionnage
Fantastique
Frisson & Terreur 	Guerre
Jeunesse
Historique
Littérature sentimentale
Policier
Régional/Terroir 	Roman
Science-Fiction
Nouvelles
Anthologies
Fantasy
Thriller
Western
*/