import javax.json.*;
import javax.json.stream.*;

/* Mise en place de la Catégorie d'un client */
// Nombre limité de catégorie => Enum

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

	// Renvoie la carégorie en fonction du nom
	public CategorieClient setCategorie(String nom)
	{
		for(CategorieClient c : CategorieClient.values())
		{
			if(c.name().equals(nom) || c.getNom().equals(nom))
			{
				return c;
			}
		}

		// error
		return null;
	}

	// Renvoie la catégorie en fonction de l'id de l'énumération et modifie le codeReductionActif
	public CategorieClient setCategorie(int ordinal, Boolean codeActif)
	{

		CategorieClient cat = PARTICULIER;
		for(CategorieClient c : CategorieClient.values())
		{
			if(ordinal == c.ordinal())
			{
				cat = c;
				break;
			}
		}
		cat.setCodeActif(codeActif);

		return cat;
	}

	public void setCodeActif(Boolean actif)
	{
		this.codeReductionActif = actif;
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

	// Traduit un String en Boolean
	public Boolean fromStringToBool(String s)
	{
		if(s.equals("true"))
		{
			return true;
		}

		return false;
	}

	// Permet d'écrire la catégorie en tant qu'objet d'un autre objet
	public JsonObjectBuilder writeBuilder()
	{
		JsonObjectBuilder obj = Json.createObjectBuilder()
			.add("ordinal", this.ordinal())
		    .add("actif", this.codeReductionActif.toString())
		   ;

	   return obj;
	}

	// Renvoie la catégorie après lecture
	public CategorieClient read(JsonParser parser)
	{
		int ordinal = 0;
		Boolean actif = false;

		int count = 0; // compteur pour connaitre le nombre de variables lues

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
			      	if(parser.getString().equals("ordinal"))
			      	{
			      		parser.next();
			      		ordinal = parser.getInt();
			      		count ++;
			      	}
			      	else if(parser.getString().equals("actif"))
			      	{
			      		parser.next();
			      		actif = fromStringToBool(parser.getString());
			      		count ++;
			      	}
			      	else
			      	{
			      		// error
			      	}
			         break;

			         default:
			         break;
			   }

			   if(count >= 2)
			      		{
			      			break;
			      		}
			}

			return setCategorie(ordinal, actif);
	
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}

		return setCategorie(ordinal, actif);
	}
};