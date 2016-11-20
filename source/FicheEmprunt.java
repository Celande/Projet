import java.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;

import javax.json.*;
import javax.json.stream.*;

public class FicheEmprunt
{
	private Date dateEmprunt;
	private Date dateLimite;
	private Date dateRappel;
	public Boolean depasse; //
	public double tarif;	//

	private String codeClient;
	private String codeDoc;
	private double docTarif;

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");

	public FicheEmprunt (Client c, Document d)
	{
		Calendar cal = Calendar.getInstance();

		try
		{
		// "aujourd'hui"
		this.dateEmprunt = new Date();
		this.dateEmprunt = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours

		this.dateLimite = new Date();
		cal.setTime(this.dateEmprunt);
		cal.add(Calendar.DATE, d.DUREE); // date limite en fonction de la durée du document
		this.dateLimite = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours

		this.dateRappel = new Date();
        cal.setTime(this.dateLimite);
        cal.add(Calendar.DATE, -1); // rappel un jour avant la date limite
		this.dateRappel = dateFormat.parse(dateFormat.format(cal.getTime())); // Pour être sûr que les calculs prennent en comptent les jours

	}
	catch(Exception e)
	{
		System.err.println(e.getMessage());
		System.exit(-1);
	}
		
		this.codeClient = c.getMatricule();
		this.codeDoc = d.getCode();

		this.tarif = d.TARIF;
		this.docTarif = d.TARIF;

		this.depasse = false;
	}

	public FicheEmprunt()
	{
		this.dateEmprunt = new Date();

		this.dateRappel = new Date();

		this.dateLimite = new Date();

		this.codeDoc = "";
		this.codeClient = "";

		this.tarif = 0;
		this.docTarif = 0;

		this.depasse = false;
	}

	// Retourne le document en fonction d'une liste
	public Document getDoc(ArrayList<Document> list)
	{
		for(Document d : list)
		{
			if(this.codeDoc.equals(d.getCode())) // Vérifie si le code du document correspond
			{
				return d;
			}
		}

		// error
		return null;
	}

	public String getCodeDoc()
	{
		return this.codeDoc;
	}

	// Retourne le client en fonction d'une liste
	public Client getClient(ArrayList<Client> list)
	{
		for(Client c : list)
		{
			if(this.codeClient.equals(c.getMatricule())) // Vérifie si le matricule du client correspond
			{
				return c;
			}
		}

		//error
		return null;
	}

	public String toString()
	{
		return "Emprunt de " + this.codeDoc + " par " + this.codeClient;
	}

	public Boolean fromStringToBool(String s)
	{
		if(s.equals("true"))
		{
			return true;
		}

		return false;
	}

	// Mise à jour de la fiche d'emprunt
	// Renvoie si le dépassement date d'aujourd'hui ou non
	public Boolean update(Date today)
	{
		Boolean justPassed = false; // Vérifie si le dépassement est le jour même ou non

		if(!this.depasse) // Vérifie si il n'y a pas déjà dépassement
		{
			if(today.after(dateLimite)) // Vérifie s'il y a  dépassement
			{
				this.depasse = true;
				justPassed = true;
			}
		}

		if(this.depasse)
		{
			this.tarif = this.tarif + docTarif/2; // Augement le tarif dû
		}

		return justPassed;
	}

	// Nécessité d'envoyer un rappel
	public Boolean giveRappel(Date today) // Verifier si c'est la date ou à la seconde pres
	{
		// Envoie un rappel chaque jour à partir de dateRappel
		if(today.equals(this.dateRappel) || today.after(this.dateRappel))
		{
			return true;
		}

		return false;
	}

	// Permet d'écrire la fiche
	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("dateEmprunt", this.dateFormat.format(dateEmprunt))
			.add("dateLimite", this.dateFormat.format(dateLimite))
			.add("dateRappel", this.dateFormat.format(dateRappel))
		   .add("depasse", this.depasse.toString())
		   .add("tarif", String.valueOf(this.tarif))
		   	.add("codeClient", this.codeClient)
		   	.add("codeDoc", this.codeDoc)
		   .build();
	   return obj;
	}

	// Li les attributs de la fiche
	public void read(JsonParser parser)
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
			      	if(parser.getString().equals("dateEmprunt"))
			      	{
			      		parser.next();
			      		this.dateEmprunt = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("dateLimite"))
			      	{
			      		parser.next();
			      		this.dateLimite = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("dateRappel"))
			      	{
			      		parser.next();
			      		this.dateRappel = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("depasse"))
			      	{
			      		parser.next();
			      		this.depasse = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("tarif"))
			      	{
			      		parser.next();
			      		this.tarif = Double.parseDouble(parser.getString());
			      	}
			      	else if(parser.getString().equals("codeClient"))
			      	{
			      		parser.next();
			      		this.codeClient = parser.getString();
			      	}
			      	else if(parser.getString().equals("codeDoc"))
			      	{
			      		parser.next();
			      		this.codeDoc = parser.getString();
			      	}
			      	else
			      	{
			      		// error
			      	}
			         break;
			         default:
			         break;
			   }
			}
	
		}
		catch(Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}
	}
};