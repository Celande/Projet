import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;

import javax.json.*;
import javax.json.stream.*;

public class Client
{
	private CategorieClient categorie;
	private String nom;
	private String prenom;
	private String adresse;
	private String email;
	private Date dateInscription;
	private Date dateRenouvellement;
	private int nbEmpruntsEffectues;
	private int nbEmpruntsDepasses;
	private int nbEmpruntsEnCours; // 3 max

	private String matricule;

	public SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");

	public Client()
	{
		this.categorie = CategorieClient.PARTICULIER; // Par défaut
		this.nom = "";
		this.prenom = "";
		this.adresse = "";
		this.email = "";

		try
		{
			Calendar cal = Calendar.getInstance();
			this.dateInscription = new Date();
			this.dateInscription = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours
			this.dateRenouvellement = new Date();
			this.dateRenouvellement = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		this.nbEmpruntsEffectues = 0;
		this.nbEmpruntsDepasses = 0;
		this.nbEmpruntsEnCours = 0;

		this.matricule = "";

	}

	public Client(CategorieClient cat, String nom, String prenom, String adresse, String email, int[] nbClient)
	{
		this.categorie = cat;
		this.nom = firstUpperCase(nom);
		this.prenom = firstUpperCase(prenom);
		this.adresse = firstUpperCase(adresse);
		this.email = email;

		try
		{
			Calendar cal = Calendar.getInstance();
			this.dateInscription = new Date();
			this.dateInscription = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours
			this.dateRenouvellement = new Date();
			this.dateRenouvellement = this.dateInscription;
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		this.nbEmpruntsEffectues = 0;
		this.nbEmpruntsDepasses = 0;
		this.nbEmpruntsEnCours = 0;

		nbClient[categorie.ordinal()] = nbClient[categorie.ordinal()] + 1;
		this.matricule = categorie.name().substring(0,2) + Integer.toString(nbClient[categorie.ordinal()]);

	}

	public CategorieClient getCategorie()
	{
		return this.categorie;
	}

	public String getNom()
	{
		return this.nom;
	}

	public String getPrenom()
	{
		return this.prenom;
	}

	public String getAdresse()
	{
		return this.adresse;
	}

	public String getEmail()
	{
		return this.email;
	}

	public String getNomCategorie()
	{
		return this.categorie.getNom();
	}

	public String getMatricule()
	{
		return this.matricule;
	}

	public double getCotisation()
	{
		return this.categorie.getCotisation();
	}

	public int getNbEmpruntsMax()
	{
		return this.categorie.getNbEmpruntsMax();
	}

	public int getNbEmpruntsEnCours()
	{
		return this.nbEmpruntsEnCours;
	}

		// Renvoit les données pour un tableau
	public Object[] getTable()
	{
		Object[] tab = {this.matricule, this.categorie, this.nom, this.prenom, this.adresse, this.email, dateFormat.format(this.dateInscription), dateFormat.format(this.dateRenouvellement), this.nbEmpruntsEffectues, this.nbEmpruntsDepasses, this.nbEmpruntsEnCours};

		return tab;
	}

	public String toString()
	{
		return this.nom.toUpperCase() + " " + this.prenom;
	}

	// +1 au nombres d'emprunts effectués et en cours
	public void addEmprunt()
	{
		this.nbEmpruntsEffectues ++;
		this.nbEmpruntsEnCours ++;
	}

	// -1 au nombre d'emprunt en cours
	public void rmEmprunt()
	{
		this.nbEmpruntsEnCours --;
	}

	public String firstUpperCase(String s)
	{
		StringBuilder str = new StringBuilder();
		if(!s.isEmpty())
		{
			str.append(Character.toUpperCase(s.charAt(0)));
			for(int i=1; i<s.length(); i++)
			{
				if(s.charAt(i-1) == ' ' || s.charAt(i-1) == '-' || s.charAt(i-1) == '\'')
				{
					str.append(Character.toUpperCase(s.charAt(i)));
				}
				else
				{
					str.append(Character.toLowerCase(s.charAt(i)));
				}
			}
		}
		return str.toString();
	}

	// Ajout de 1 au nombre d'emprunts dépassés
	public void addEmpruntDepasse()
	{
		this.nbEmpruntsDepasses ++;
	}

		// Nécessité de se réinscrire
	public Boolean haveToRenew(Date today)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.dateRenouvellement);
		cal.add(Calendar.YEAR, 1); // date de renouvellement + 1 an
		// Si le renouvellement date de plsu d'un an
		if(today.equals(cal.getTime()) || today.after(cal.getTime()))
		{
			return true;
		}

		return false;
	}

	// Réinscription
	public void reinscription(Date today)
	{
		//Calendar cal = Calendar.getInstance();
		// mise à jour de la date de renouvellement
		try
		{
			this.dateRenouvellement = today; // Pour être sûr que les calculs prennent en comptent les jours
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	// Supression du client
	public Boolean delete(Date today)
	{
		Calendar max = Calendar.getInstance();
		max.setTime(this.dateRenouvellement);
		max.add(Calendar.YEAR, 1);
		max.add(Calendar.MONTH, 3); // On a 3 mois pour se réinscrire

		// Si on ne se réinscrit pas dans les 3 mois
		if(today.equals(max.getTime()) || today.after(max.getTime()))
		{
			return true;
		}

		return false;
	}

	// Renvoit un objet décrivant les attributs du client
	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
		.add("categorie", this.categorie.writeBuilder())
		.add("nom", this.nom)
		.add("prenom", this.prenom)
		.add("adresse", this.adresse)
		.add("email", this.email)
		.add("dateInscription", dateFormat.format(this.dateInscription))
		.add("dateRenouvellement", dateFormat.format(this.dateRenouvellement))
		.add("nbEmpruntsEffectues", this.nbEmpruntsEffectues)
		.add("nbEmpruntsDepasses", this.nbEmpruntsDepasses)
		.add("nbEmpruntsEnCours", this.nbEmpruntsEnCours)
		.add("matricule", this.matricule)
		.build()
		;

		return obj;

	}

	// Lit les attributs du client
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
					if(parser.getString().equals("categorie"))
					{
						parser.next();
						this.categorie = this.categorie.read(parser);
					}
					else if(parser.getString().equals("nom"))
					{
						parser.next();
						this.nom = firstUpperCase(parser.getString());
					}
					else if(parser.getString().equals("prenom"))
					{
						parser.next();
						this.prenom = firstUpperCase(parser.getString());
					}
					else if(parser.getString().equals("adresse"))
					{
						parser.next();
						this.adresse = firstUpperCase(parser.getString());
					}
					else if(parser.getString().equals("email"))
					{
						parser.next();
						this.email = parser.getString();
					}
					else if(parser.getString().equals("dateInscription"))
					{
						parser.next();
						this.dateInscription = dateFormat.parse(parser.getString());
					}
					else if(parser.getString().equals("dateRenouvellement"))
					{
						parser.next();
						this.dateRenouvellement = dateFormat.parse(parser.getString());
					}
					else if(parser.getString().equals("nbEmpruntsEffectues"))
					{
						parser.next();
						this.nbEmpruntsEffectues = parser.getInt();
					}
					else if(parser.getString().equals("nbEmpruntsDepasses"))
					{
						parser.next();
						this.nbEmpruntsDepasses = parser.getInt();
					}
					else if(parser.getString().equals("nbEmpruntsEnCours"))
					{
						parser.next();
						this.nbEmpruntsEnCours = parser.getInt();
					}
					else if(parser.getString().equals("matricule"))
					{
						parser.next();
						this.matricule = parser.getString();
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