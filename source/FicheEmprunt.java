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

	private Client client;
	private Document doc;

	public SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");

	public FicheEmprunt(Client c, Document d)
	{
		Calendar cal = Calendar.getInstance();

		dateEmprunt = new Date();
		dateEmprunt = cal.getTime();

		dateRappel = new Date();
        cal.setTime(dateEmprunt);
        cal.add(Calendar.DATE, doc.DUREE-1);
		dateRappel = cal.getTime();

		dateLimite = new Date();
		cal.setTime(dateRappel);
		cal.add(Calendar.DATE, 1);
		dateLimite = cal.getTime();

		this.client = c;
		this.doc = d;

		tarif = doc.TARIF;

		depasse = false;
	}

	public void update()
	{
		Calendar cal = Calendar.getInstance();

		if(cal.getTime().after(dateLimite))
		{
			depasse = true;
		}
		else
		{
			depasse = false;
		}

		//tarif = doc.TARIF;
		if(depasse)
		{
			//tarif = tarif + (cal.getTime() - dateLimite.getTime())*doc.TARIF;
		}
	}

	public JsonObject write()
	{
		JsonObject obj = Json.createObjectBuilder()
			.add("dateEmprunt", dateFormat.format(dateEmprunt))
			.add("dateLimite", dateFormat.format(dateLimite))
			.add("dateRappel", dateFormat.format(dateRappel))
		   .add("depasse", depasse)
		   .add("tarif", String.valueOf(tarif))
		   	.add("client", client.writeBuilder()) // JsonObjectBuilder
		   	.add("doc", doc.writeBuilder()) // JsonObjectBuilder
		   .build();
	   return obj;
	}

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
			         //System.out.println(event.toString());
			         break;
			      case KEY_NAME:
			      	if(parser.getString().equals("dateEmprunt"))
			      	{
			      		parser.next();
			      		dateEmprunt = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("dateLimite"))
			      	{
			      		parser.next();
			      		dateLimite = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("dateRappel"))
			      	{
			      		parser.next();
			      		dateEmprunt = dateFormat.parse(parser.getString());
			      	}
			      	else if(parser.getString().equals("depasse"))
			      	{
			      		parser.next();
			      		depasse = fromStringToBool(parser.getString());
			      	}
			      	else if(parser.getString().equals("tarif"))
			      	{
			      		parser.next();
			      		tarif = Double.parseDouble(parser.getString());
			      	}
			      	else if(parser.getString().equals("client"))
			      	{

			      	}
			      	else if(parser.getString().equals("doc"))
			      	{

			      	}
			      	else
			      	{
			      		// error
			      	}
			         /*
			         System.out.print(event.toString() + " " +
			                          parser.getString() + " - ");
			                          */
			         break;
			         /*
			      case VALUE_STRING:
			      case VALUE_NUMBER:
			         System.out.println(event.toString() + " " +
			                            parser.getString());
			         break;
			         */
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

	public Boolean fromStringToBool(String s)
	{
		if(s.equals("true"))
		{
			return true;
		}

		return false;
	}
};