import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.table.*;
import java.io.File;
import java.util.Iterator;
import java.util.Collections;
import java.util.Calendar;
import java.util.Date;
import javax.swing.event.*;
import javax.imageio.ImageIO;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// VERIFIER NBPAGES ET DUREE SONT BIEN DES NOMBRES (PATTERN ?)


import javax.json.*;
import javax.json.stream.*;

public class Mediatheque
{
	/* Attributs principaux */
	private String nom;

	private ArrayList<Localisation> locList;
	private ArrayList<Genre> genreList;
	private ArrayList<Document> docList;
	private ArrayList<FicheEmprunt> ficheList;
	private ArrayList<Client> clientList;
	private ArrayList<CategorieClient> catList;

	/* Aujourd'hui */
	Date today;

	

	/* Nombre de clients par catégorie */
	int[] nbClient;

	/* Nombre de document par catégorie */
	int[] nbDoc;

	Boolean searchInEmprunt = false;
	Boolean searchInRenew = false;

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");
	public static final SimpleDateFormat yearFormat = new SimpleDateFormat("y");

	public Mediatheque()
	{
		
		// Initialisation du nom
		nom = new String("La Médiathèque des Magnolias");
		// Intialisation de la date
		try
		{
			Calendar cal = Calendar.getInstance();
			today = new Date();
			today = dateFormat.parse(dateFormat.format(cal.getTime()));
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "Date invalide.", "Erreur", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}

		// Initialisation des listes
		locList = new ArrayList<Localisation>();
		genreList = new ArrayList<Genre>();
		docList = new ArrayList<Document>();
		ficheList = new ArrayList<FicheEmprunt>();
		clientList = new ArrayList<Client>();
		catList = new ArrayList<CategorieClient>();

		// Lancement de la fenêtre
		new Fenetre();
		
	}	

	
	public static void main(String[] args)
	{
		new Mediatheque();

	}

	

	public void initMediatheque(String nom)
	{
		try
		{
			// Lecture de la mémoire
			read(nom);

		// Initialisation de nbClient
			int i = 0;
			for(CategorieClient c : CategorieClient.values()) // nombre total de catégories
			{
				i++;
			}
			nbClient = new int[i];
			// Initialisation à 0
			for(int j=0; j<i; j++)
			{
				nbClient[j] = 0;
			}
			// Une case pour chaque catégorie de clients avec le nombre actuel correspondant
			for(Client c : clientList)
			{
				nbClient[c.getCategorie().ordinal()] = nbClient[c.getCategorie().ordinal()] + 1;
			}

			/* Initialisation de nbDoc */
			nbDoc = new int[3];
			// Initialisation à 0
			for(int j=0; j<3; j++)
			{
				nbDoc[j] = 0;
			}
			// Une case pour chaque type de document avec le nombre actuel correspondant
			for(Document d : docList)
			{
				if(d instanceof Audio)
				{
					nbDoc[0] = nbDoc[0]+1;
				}
				else if(d instanceof Livre)
				{
					nbDoc[1] = nbDoc[1]+1;
				}
				else // Video
				{
					nbDoc[2] = nbDoc[2]+1;
				} 
			}

			/* Initialisation de genreList */
			for(Document d : docList)
			{
				addGenre(d.getGenre());
			}
		}
		catch(NoSaveException ex)
		{
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}
	}



	// Ajout d'un client en vérifiant les doublons
	public void addClient(Client cl)
	{
		if(!alreadyInClientList(cl))
		{
			clientList.add(cl);
		}
	}

	// Ajout d'une localisation en vérifiant les doublons
	public void addLoc(Localisation loc)
	{
		Boolean alreadyIn = false;

		for(Localisation l : locList)
		{
			if(loc.getSalle().equals(l.getSalle()))
			{
				if(loc.getRayon().equals(l.getRayon())) // déjà dans la liste
				{
					alreadyIn = true;
					break;
				}
			}
		}

		if(!alreadyIn)
		{
			locList.add(loc);
		}
	}

	// Ajout d'un genre en vérifiant les doublons
	public void addGenre(Genre ge)
	{
		Boolean alreadyIn = false;
		for(Genre g : genreList)
		{
			if(g.ordinal() == ge.ordinal()) // déjà dans la liste
			{
				alreadyIn = true;
				break;
			}
		}

		if(!alreadyIn) // Ajout que si pas dans la liste
		{
			genreList.add(ge);
		}
	}

	//Ajout d'un document
	public void addDocument(String type, String titre, String auteur, String annee, String localisation, String genre, int nb, String str) throws AlreadyInDocException
	{
		// Détermine la localisation à partir du String
		String[] s = localisation.split(",");
		Localisation loc = new Localisation(s[0], s[1]);

		// Détermine le genre à partir du String
		Genre g = Genre.AUTRE;
		g = g.setGenre(genre);
		addGenre(g); // Ajout à la liste des genres

		// Audio
		if(type.equals("a"))
		{
			Audio a = new Audio(nbDoc, titre, auteur, annee, loc, g, str);
			if(!alreadyIndocList(a)) // Déjà dans la liste ?
			{
				docList.add(a);
			}
			else
			{
				throw new AlreadyInDocException("L'audio est déjà dans la médiathèque : " + a);
			}
		}
		// Livre
		else if(type.equals("l"))
		{
			
			Livre l = new Livre(nbDoc, titre, auteur, annee, loc, g, nb);
			if(!alreadyIndocList(l)) // Déjà dans la liste ?
			{
				docList.add(l);
			}
			else
			{		
				throw new AlreadyInDocException("Le livre est déjà dans la médiathèque : " + l);

			}
			
		}
		// Vidéo
		else if(type.equals("v"))
		{
			
			Video v = new Video(nbDoc, titre, auteur, annee, loc, g, nb);
			if(!alreadyIndocList(v)) // Déjà dans la liste ?
			{
				docList.add(v);
			}
			else
			{
				throw new AlreadyInDocException("La vidéo est déjà dans la médiathèque : " + v);

			}
			
		}
		
	}

	

	// Retourne le document de la liste qui correspond au code
	public Document getDocFromCode(String code)
	{
		for(int i=0 ; i<docList.size() ; i++)
		{
			if(code.equals(docList.get(i).getCode()))
			{
				return docList.get(i);
			}
		}

		return null;
	}


	// Vérifie si le client n'est pas déjà dans la liste des clients
	public Boolean alreadyInClientList(Client c)
	{
		for(Client x : clientList)
		{
			// Même catégorie, nom, prénom et adresse
			if(x.getNomCategorie().equals(c.getNomCategorie()) && x.getNom().equals(c.getNom()) && x.getPrenom().equals(c.getPrenom()) && x.getAdresse().equals(c.getAdresse()))
			{
				return true;
			}
		}

		return false;
	}

	
	//Vérifie si le document n'est pas déjà dans la liste des documents
	public Boolean alreadyIndocList(Document d)
	{
		for(Document x : docList)
		{
			// Même titre, même auteur, même années
			if(x.getTitre().equals(d.getTitre()) && x.getAuteur().equals(d.getAuteur()) && x.getAnnee().equals(d.getAnnee()))
			{
				return true;
			}
		}

		
		return false;
	}

	// Première lettre majuscule, reste minuscule
	public String firstUpperCase(String s)
	{
		StringBuilder str = new StringBuilder();
		// String non vide
		if(!s.isEmpty())
		{
			// Première lettre en majuscule
			str.append(Character.toUpperCase(s.charAt(0)));

			for(int i=1; i<s.length(); i++)
			{
				// Si le précédent est un espace, un trait d'union ou une apostrophe
				if(s.charAt(i-1) == ' ' || s.charAt(i-1) == '-' || s.charAt(i-1) == '\'')
				{
					// Majuscule
					str.append(Character.toUpperCase(s.charAt(i)));
				}
				else
				{
					// Minuscule
					str.append(Character.toLowerCase(s.charAt(i)));
				}
			}
		}
		return str.toString();
	}	

	// Vérification de la forme de la classification
	public String checkClassification(String c)  throws InvalidClassificationException
	{
		// 3 chiffres max
		if(c.matches("[0-9]{1,3}"))
		{
			return c;
		}
		// Que des lettres
		else if(c.matches("[a-zA-Z]+")) 
		{
			return c;
		}

		throw new InvalidClassificationException("Classification invalide : " + c + "(voir le dossier doc)");
	}

	// Vérification du nombre de pages
	public int checkNbPages(String page) throws InvalidNbPagesException
	{
		try
		{
			// Convertie String en int
			int n = Integer.parseInt(page);

			// Vérifie le nombre
			if(n > 0)
			{
				return n;
			}

			// Inférieur à 0
			throw new InvalidNbPagesException("Nombre de pages invalide : " + n);
		}
		catch(Exception e) // Ne peut pas être convertie en int
		{
			e.printStackTrace();
			throw new InvalidNbPagesException("Nombre de pages invalide : " + page);

		}
	}

	// Vérification de la forme de l'année
	public String checkAnnee(String annee) throws InvalidYearException
	{
		Boolean onlyNumbers = false;

        // AVJC
		if(annee.startsWith("-"))
		{
        	// 3 chiffres max
			if(annee.matches(".[0-9]{1,3}"))
			{
				onlyNumbers = true;
			}
			else
			{
				throw new InvalidYearException("Année AVJC invalide : " + annee);
			}
		}

        // 4 chiffres max
		else if(annee.matches("[0-9]{1,4}"))
		{
			onlyNumbers = true;
		}

		// Que des chiffres
		if(onlyNumbers)
		{

			try
			{
				int year = Integer.parseInt(annee);
				int n = Integer.parseInt(yearFormat.format(today));

				// Année supérieure à l'année en cours
				if(year > n)
				{
					throw new InvalidYearException("Année invalide : " + annee);
				}

				return annee;
			}
			catch(Exception e)
			{
				throw new InvalidYearException("Année invalide : " + annee);
			}
		}

		throw new InvalidYearException("Année invalide : " + annee);
	}

	// Ecriture de la mémoire de la médiathèque
	public void write()
	{
		try
		{
			// Effacement du fichier
			PrintWriter writer = new PrintWriter("./resource/memory/" + this.nom.replace(' ', '_') + ".json");
			writer.print("");
			writer.close();


			StringWriter stWriter;
			JsonWriter jsonWriter;

			String jsonData = new String();

			// Ouverture du fichier correspondant à la médiathèque
			FileWriter file = new FileWriter("./resource/memory/" + this.nom.replace(' ', '_') + ".json");

			stWriter = new StringWriter();

			// Ecriture du nom et de la date
			JsonObject obj = Json.createObjectBuilder()
			.add("nom", nom)
			.add("today", dateFormat.format(today))
			.build()
			;
			stWriter = new StringWriter();
			jsonWriter = Json.createWriter(stWriter);
			jsonWriter.writeObject(obj);
			jsonWriter.close();

			jsonData = stWriter.toString();

			file.write(jsonData);
			file.write(System.getProperty("line.separator")); // Passe une ligne
			file.write(System.getProperty("line.separator"));

			// Localasition
			file.write("Localisation :");
			file.write(System.getProperty("line.separator"));
			// Ecriture de la liste
			for(Localisation l : locList)
			{
				stWriter = new StringWriter();
				jsonWriter = Json.createWriter(stWriter);
				jsonWriter.writeObject(l.write());
				jsonWriter.close();

				jsonData = stWriter.toString();

				file.write(jsonData);
				file.write(System.getProperty( "line.separator" ));
			}
			file.write(System.getProperty( "line.separator" ));

			// Document
			file.write("Document :");
			file.write(System.getProperty( "line.separator" ));

			// Audio
			file.write("Audio :");
			file.write(System.getProperty( "line.separator" ));
			// Ecriture de la liste
			for(Document a : docList)
			{
				if (a instanceof Audio)
				{
					stWriter = new StringWriter();
					jsonWriter = Json.createWriter(stWriter);
					jsonWriter.writeObject(a.write());
					jsonWriter.close();

					jsonData = stWriter.toString();

					file.write(jsonData);
					file.write(System.getProperty( "line.separator" ));
				}
			}

			// Livre
			file.write("Livre :");
			file.write(System.getProperty( "line.separator" ));
			// Ecriture de la liste
			for(Document l : docList)
			{
				if (l instanceof Livre)
				{
					stWriter = new StringWriter();
					jsonWriter = Json.createWriter(stWriter);
					jsonWriter.writeObject(l.write());
					jsonWriter.close();

					jsonData = stWriter.toString();

					file.write(jsonData);
					file.write(System.getProperty( "line.separator" ));
				}
			}

			// Video
			file.write("Video :");
			file.write(System.getProperty( "line.separator" ));
			// Ecriture de la liste
			for(Document v : docList)
			{
				if (v instanceof Video)
				{
					stWriter = new StringWriter();
					jsonWriter = Json.createWriter(stWriter);
					jsonWriter.writeObject(v.write());
					jsonWriter.close();

					jsonData = stWriter.toString();
					file.write(jsonData);
					file.write(System.getProperty( "line.separator" ));
				}
			}
			file.write(System.getProperty( "line.separator" ));

			// Client
			file.write("Client :");
			file.write(System.getProperty( "line.separator" ));
			// Ecriture de la liste
			for(Client c : clientList)
			{

				stWriter = new StringWriter();
				jsonWriter = Json.createWriter(stWriter);
				jsonWriter.writeObject(c.write());
				jsonWriter.close();

				jsonData = stWriter.toString();

				file.write(jsonData);
				file.write(System.getProperty( "line.separator" ));
			}
			file.write(System.getProperty( "line.separator" ));

			// Fiche Emprunt
			file.write("Fiche Emprunt :");
			file.write(System.getProperty( "line.separator" ));
			// Ecriture de la liste
			for(FicheEmprunt f : ficheList)
			{
				stWriter = new StringWriter();
				jsonWriter = Json.createWriter(stWriter);
				jsonWriter.writeObject(f.write());
				jsonWriter.close();

				jsonData = stWriter.toString();

				file.write(jsonData);
				file.write(System.getProperty( "line.separator" ));
			}

			file.write(System.getProperty( "line.separator" ));

			file.flush();
			file.close();
			System.out.println("Sauvegarde des données.");
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			JOptionPane.showMessageDialog(null, "Impossible de sauvegarder.", "Erreur", JOptionPane.WARNING_MESSAGE);
			System.exit(-1);
		}
	}

	// Lecture de la médiathèque en fonction de son nom
	public void read(String nom) throws NoSaveException
	{

		try {

			// Nettoyage des listes
			locList.clear();
			docList.clear();
			clientList.clear();
			ficheList.clear();
			genreList.clear();


			// Ouverture du fichier
			BufferedReader reader = new BufferedReader(new FileReader("./resource/memory/" + this.nom.replace(' ', '_')+ ".json"));

			JsonParser parser;

			String jsonData = new String();

			// Lecture des nom et jour
			if((jsonData = reader.readLine()) != null)
			{
				parser = Json.createParser(new StringReader(jsonData));
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
						if(parser.getString().equals("nom"))
						{
							parser.next();
							this.nom = firstUpperCase(parser.getString());
						}
						else if(parser.getString().equals("today"))
						{
							parser.next();
							this.today = dateFormat.parse(parser.getString());
						}
						else
						{
							throw new NoSaveException("Mediatheque - Fichier de sauvegarde corrompu.");
						}
						break;
						default:
						break;
					}
				}

			}
			
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (nom et jour).");
			}

			jsonData = reader.readLine(); // Passe une ligne

			jsonData = reader.readLine(); // Passe une ligne

			// Localisation
			if(jsonData.equals("Localisation :"))
			{
				jsonData = reader.readLine();

				Localisation l;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.isEmpty())
				{
					parser = Json.createParser(new StringReader(jsonData));
					l = new Localisation();
					l.read(parser);
					addLoc(l);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (localisation).");
			}

			// Document
			jsonData = reader.readLine();

			// Audio
			jsonData = reader.readLine();
			if(jsonData.equals("Audio :"))
			{
				jsonData = reader.readLine();
				Audio a;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.equals("Livre :"))
				{
					parser = Json.createParser(new StringReader(jsonData));
					a = new Audio();
					a.read(parser);
					docList.add(a);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (audio).");
			}
			
			// Livre
			if(jsonData.equals("Livre :"))
			{
				jsonData = reader.readLine();
				Livre l;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.equals("Video :"))
				{
					parser = Json.createParser(new StringReader(jsonData));
					l = new Livre();
					l.read(parser);
					docList.add(l);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (livre).");
			}

			// Video
			if(jsonData.equals("Video :"))
			{
				jsonData = reader.readLine();
				Video v;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.isEmpty())
				{

					parser = Json.createParser(new StringReader(jsonData));
					v = new Video();
					v.read(parser);
					docList.add(v);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (video).");
			}

			// Client
			jsonData = reader.readLine();
			if(jsonData.equals("Client :"))
			{
				jsonData = reader.readLine();
				Client c;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.isEmpty())
				{
					parser = Json.createParser(new StringReader(jsonData));
					c = new Client();
					c.read(parser);
					addClient(c);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (client).");
			}

			// Fiche Emprunt
			jsonData = reader.readLine();
			if(jsonData.equals("Fiche Emprunt :"))
			{
				jsonData = reader.readLine();
				FicheEmprunt f;
				// Parcourt le fichier et remplie la liste
				while(!jsonData.isEmpty())
				{
					parser = Json.createParser(new StringReader(jsonData));
					f = new FicheEmprunt();
					f.read(parser);
					ficheList.add(f);
					jsonData = reader.readLine();
				}
			}
			else
			{
				throw new NoSaveException("Fichier de sauvegarde invalide (fiche emprunt).");
			}
		}

		catch(NoSaveException ne)
		{
			throw ne;
		}
		catch (NullPointerException ex)
		{
			ex.printStackTrace();
			throw new NoSaveException("Fichier de sauvegarde vide.");
		}
		catch(FileNotFoundException fn)
		{
			fn.printStackTrace();
			throw new NoSaveException("Aucun fichier de sauvegarde trouvé.");
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}


	


	public class Fenetre extends JFrame implements ActionListener
	{
		/* Recherche */
	// Client
		JTextField searchNomInClientTextField;
		JTextField searchPrenomInClientTextField;
	// Document
		JTextField searchTitreInDocTextField;
		JTextField searchAuteurInDocTextField;
	// Fiche
		JTextField searchDocInFicheTextField;
		JTextField searchClientInFicheTextField;

		/* Paneaux */
		JPanel cards;
		JPanel showClientPanel;
		JPanel showDocPanel;
		JPanel createPanel;
		JPanel pageCards;
		JPanel choicePanel;
		JPanel locDocPanel;
		JPanel genreDocPanel;
		JPanel rappelPanel;
		JPanel renewPanel;
		JPanel deletePanel;
		JPanel menuPanel;
		JPanel addDocPanel;
		JPanel docCards;
		JPanel empruntPanel;
		JPanel empruntDoc;
		JPanel renduPanel;
		JPanel reinscrirePanel;

		/* Tables */
		JXTable clientTable;
		JXTable audioTable;
		JXTable livreTable;
		JXTable videoTable;

		JXTable ficheTable;

		/* Ajout du client */
		JTextField nomTextField;
		JTextField prenomTextField;
		JTextField adresseTextField;
		JTextField emailTextField;
		JComboBox<String> categorieCombo;

		/* Ajout de document */
	// audio
		JTextField titreAudioTextField;
		JTextField auteurAudioTextField;
		JTextField anneeAudioTextField;
		JComboBox<String> locAudioCombo;
		JComboBox<String> genreAudioCombo;
		JTextField classTextField;
	//livre
		JTextField titreLivreTextField;
		JTextField auteurLivreTextField;
		JTextField anneeLivreTextField;
		JComboBox<String> locLivreCombo;
		JComboBox<String> genreLivreCombo;
		JTextField nbPagesTextField;
	// video
		JTextField titreVideoTextField;
		JTextField auteurVideoTextField;
		JTextField anneeVideoTextField;
		JComboBox<String> locVideoCombo;
		JComboBox<String> genreVideoCombo;
		JTextField dureeTextField;

		/* String pour le choix du JPanel à montrer */
		static final String MENU_SIGNAL = "menu";
		static final String SHOW_CLIENT_SIGNAL = "show_client";
		static final String CREATE_CLIENT_SIGNAL = "create_client";
		static final String ADD_CLIENT_SIGNAL = "add_client";
		static final String CHOICE_DOC_SIGNAL = "choice_doc";
		static final String LOC_DOC_SIGNAL = "loc_doc";
		static final String GENRE_DOC_SIGNAL = "genre_doc";
		static final String SHOW_DOC_SIGNAL = "show_doc";
		static final String UPDATE_SIGNAL = "update";
		static final String SEARCH_CLIENT_SIGNAL = "search_client";
		static final String SEARCH_DOC_SIGNAL = "search_doc";
		static final String SEARCH_FICHE_SIGNAL = "search_fiche";
		static final String ADD_DOC_SIGNAL = "add_doc";
		static final String ADD_AUDIO_SIGNAL = "add_audio";
		static final String ADD_LIVRE_SIGNAL = "add_livre";
		static final String ADD_VIDEO_SIGNAL = "add_video";
		static final String CREATE_AUDIO_SIGNAL = "create_audio";
		static final String CREATE_LIVRE_SIGNAL = "create_livre";
		static final String CREATE_VIDEO_SIGNAL = "create_video";
		static final String EMPRUNT_SIGNAL = "emprunt";
		static final String CREATE_FICHE_SIGNAL = "add_emprunt";
		static final String CHOIX_DOC_SIGNAL = "choix_doc";
		static final String CHOIX_CLIENT_SIGNAL = "choix_client";
		static final String RENDU_SIGNAL = "rendu";
		static final String DEL_EMPRUNT_SIGNAL = "del_emprunt";
		static final String RENEW_SIGNAL = "renew";
		static final String REINSCRIRE_SIGNAL = "reinscrire";


		/* Taille de la fenêtre */
		static final int FRAME_WIDTH = 1000;
		static final int FRAME_HEIGHT = 800;
		static final int MSG_WIDTH = (FRAME_WIDTH/3) - 10;
		static final int MSG_HEIGHT = (FRAME_HEIGHT/3) - 10;

		/* Code des livres à emprunter */
		String audioCode;
		String livreCode;
		String videoCode;

		public Fenetre()
		{
		// Initialisation de la mediathèque
			initMediatheque(nom);

		// Initialisation de la frame
			this.setLayout(new BorderLayout());

		// Permet de changer de page sans modifier la fenêtre
			cards = new JPanel();
			cards.setLayout(new CardLayout());

		// Mise à jour des messages
			updateFiche();
			updateClient();

		// Initialisation des pages
			initPanel();

			this.add(cards, BorderLayout.CENTER);

			((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); 

			this.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			this.pack();
			this.setLocationRelativeTo(null);
			this.setVisible(true);
		}

		public void initPanel()
		{
			renewPanel = new JPanel();
			renewPanel.setLayout(new BoxLayout(renewPanel, BoxLayout.Y_AXIS));
			renewPanel.setBorder(BorderFactory.createTitledBorder("Réinscription"));
			renewPanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
			renewPanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
			JButton renewButton = new JButton("Renouveler");
			renewButton.setActionCommand(RENEW_SIGNAL);
			renewButton.addActionListener(this);
			renewPanel.add(renewButton);


			deletePanel = new JPanel();
			deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));
			deletePanel.setBorder(BorderFactory.createTitledBorder("Suppression"));
			deletePanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
			deletePanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));

			rappelPanel = new JPanel();
			rappelPanel.setLayout(new BoxLayout(rappelPanel, BoxLayout.Y_AXIS));
			rappelPanel.setBorder(BorderFactory.createTitledBorder("Emprunt"));
			rappelPanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
			rappelPanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));

		// Initialisation des pages
			showClientPanel = showClientList("", "");
			cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
			menuPanel = menuScreen();
			cards.add(MENU_SIGNAL, menuPanel);
			choicePanel = choiceDocList();
			cards.add(CHOICE_DOC_SIGNAL, choicePanel);
			addDocPanel = addChosenDoc();
			cards.add(ADD_DOC_SIGNAL, addDocPanel);
			empruntPanel = empruntDoc("", "", "", "");
			cards.add(EMPRUNT_SIGNAL, empruntPanel);

			renduPanel = renduDoc("", "");
			cards.add(RENDU_SIGNAL, renduPanel);

			reinscrirePanel = reinscrire("", "");
			cards.add(RENEW_SIGNAL, reinscrirePanel);


			cards.revalidate();
			cards.repaint();
		}

	// Menu
		public JPanel menuScreen() // Ajouter des icones aux boutons
		{
		// Page du menu
			JPanel startPanel = new JPanel();
			startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));

			JPanel introPanel = new JPanel();
			introPanel.setLayout(new FlowLayout());
			JLabel label = new JLabel("Bienvenue à " + nom);
			label.setFont(new Font("Serif", Font.PLAIN, 40));
			introPanel.add(label);
			JLabel timeLabel = new JLabel(dateFormat.format(today));
			timeLabel.setFont(new Font("Serif", Font.PLAIN, 14));
			introPanel.add(timeLabel);

			JButton updateButton = new JButton("Jour suivant");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/time_50.png"));
				updateButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);

			}
			updateButton.setActionCommand(UPDATE_SIGNAL);
			updateButton.addActionListener(this);
			introPanel.add(updateButton);


			JPanel addPanel = new JPanel(new FlowLayout());

			JButton addClientButton = new JButton("Ajouter un client");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/add_client4.png"));
				addClientButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			addClientButton.setActionCommand(ADD_CLIENT_SIGNAL);
			addClientButton.addActionListener(this);
			addPanel.add(addClientButton);

			JButton addDocButton = new JButton("Ajouter un Document");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/add_livre_cd_img_50.png"));
				addDocButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			addDocButton.setActionCommand(ADD_DOC_SIGNAL);
			addDocButton.addActionListener(this);
			addPanel.add(addDocButton);

			JPanel empPanel = new JPanel(new FlowLayout());

			JButton empruntButton = new JButton("Emprunter un document");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/livre_cd_emprunt_50.png"));
				empruntButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			empruntButton.setActionCommand(EMPRUNT_SIGNAL);
			empruntButton.addActionListener(this);
			empPanel.add(empruntButton);

			JButton rendButton = new JButton("Rendre un document");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/livre_cd_rendu_50.png"));
				rendButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			rendButton.setActionCommand(RENDU_SIGNAL);
			rendButton.addActionListener(this);
			empPanel.add(rendButton);

			JPanel showPanel = new JPanel(new FlowLayout());

			JButton showClientButton = new JButton("Consulter la liste des clients");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/search_client_50.png"));
				showClientButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			showClientButton.setActionCommand(SHOW_CLIENT_SIGNAL);
			showClientButton.addActionListener(this);
			showPanel.add(showClientButton);

			JButton showDocButton = new JButton("Consulter la liste des documents");
			try 
			{
				Image img = ImageIO.read(getClass().getResource("./pictures/search_livre_cd_img_50.png"));
				showDocButton.setIcon(new ImageIcon(img));
			} catch (IOException ex) 
			{
				JOptionPane.showMessageDialog(null, "Image introuvable", "Erreur", JOptionPane.WARNING_MESSAGE);
			}
			showDocButton.setActionCommand(CHOICE_DOC_SIGNAL);
			showDocButton.addActionListener(this);
			showPanel.add(showDocButton);

			JPanel msgPanel = new JPanel(new FlowLayout());
			msgPanel.setBorder(BorderFactory.createTitledBorder("E-mails envoyés pour :"));
			msgPanel.add(new JScrollPane(rappelPanel));
			msgPanel.add(new JScrollPane(renewPanel));
			msgPanel.add(new JScrollPane(deletePanel));

			startPanel.add(introPanel);
			startPanel.add(addPanel);
			startPanel.add(empPanel);
			startPanel.add(showPanel);
			startPanel.add(msgPanel);

		// refresh showclient

			cards.remove(showClientPanel);
			showClientPanel = showClientList("", "");
			cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
			cards.revalidate();
			cards.repaint();



			return startPanel;
		}

	// Page de réinscription
		public JPanel reinscrire(String nom, String prenom)
		{
			JPanel clientPanel = new JPanel();
			clientPanel.setLayout(new BorderLayout());

			JPanel mePanel = new JPanel(new FlowLayout());
			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);
			mePanel.add(menuButton);

			JPanel okPanel = new JPanel(new FlowLayout());
			JButton okButton = new JButton("OK");
			okButton.setActionCommand(REINSCRIRE_SIGNAL);
			okButton.addActionListener(this);
			okPanel.add(okButton);

			clientPanel.add(mePanel, BorderLayout.PAGE_START);
			clientPanel.add(showClientList(nom, prenom), BorderLayout.CENTER);
			clientPanel.add(okPanel, BorderLayout.PAGE_END);

			return clientPanel;
		}


	// Page de création d'un client
		public JPanel createClient()
		{

			JPanel panel = new JPanel();
			SpringLayout layout = new SpringLayout();
			panel.setLayout(layout);
			panel.setPreferredSize(new Dimension(FRAME_WIDTH/2, FRAME_HEIGHT/2));
			panel.setBorder(BorderFactory.createTitledBorder("Ajout d'un client"));

			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);

			JLabel labNom = new JLabel("Nom : ");
			nomTextField = new JTextField(20);

			JLabel labPrenom = new JLabel("Prenom : ");
			prenomTextField = new JTextField(20);

			JLabel labAdresse = new JLabel("Adresse : ");
			adresseTextField = new JTextField(20);

			JLabel labEmail = new JLabel("E-Mail : ");
			emailTextField = new JTextField(20);

			JLabel labCombo = new JLabel("Catégorie : ");
			categorieCombo = new JComboBox<String>();
			categorieCombo.setPreferredSize(new Dimension(100, 20));
			for(CategorieClient c : CategorieClient.values())
			{
				categorieCombo.addItem(c.getNom());
			}

			JButton okButton = new JButton("OK");
			okButton.setActionCommand(CREATE_CLIENT_SIGNAL);
			okButton.addActionListener(this);
			this.getRootPane().setDefaultButton(okButton);

			GroupLayout lay = new GroupLayout(panel);
			lay.setAutoCreateGaps(true);
			lay.setAutoCreateContainerGaps(true);


			lay.setHorizontalGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(menuButton)
				.addGroup(lay.createSequentialGroup()
					.addComponent(labNom)
					.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nomTextField)
						)
					)
				.addGroup(lay.createSequentialGroup()
					.addComponent(labPrenom)
					.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(prenomTextField)
						)
					)
				.addGroup(lay.createSequentialGroup()
					.addComponent(labAdresse)
					.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(adresseTextField)
						)
					)
				.addGroup(lay.createSequentialGroup()
					.addComponent(labEmail)
					.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(emailTextField)
						)
					)
				.addGroup(lay.createSequentialGroup()
					.addComponent(labCombo)
					.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(categorieCombo)
						)
					)
				.addComponent(okButton)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING))
				);

			lay.linkSize(SwingConstants.HORIZONTAL, labNom, labPrenom, labAdresse, labEmail, labCombo);
			lay.linkSize(SwingConstants.HORIZONTAL, nomTextField, prenomTextField, adresseTextField, emailTextField, categorieCombo);

			lay.setVerticalGroup(lay.createSequentialGroup()
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(menuButton)
					)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(labNom)
					.addComponent(nomTextField)
					)

				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(labPrenom)
					.addComponent(prenomTextField)
					)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(labAdresse)
					.addComponent(adresseTextField)
					)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(labEmail)
					.addComponent(emailTextField)
					)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(labCombo)
					.addComponent(categorieCombo)
					)
				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(okButton)
					)



				.addGroup(lay.createParallelGroup(GroupLayout.Alignment.LEADING))
				);


			panel.setLayout(lay);


			return panel;
		}

	// Page de la liste des clients en fonction d'une recherche (String)
		public JPanel showClientList(String nom, String prenom)
		{
			JPanel showClientPanel = new JPanel();
			showClientPanel.setLayout(new BorderLayout());
			showClientPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

			/** Recherche **/
			JPanel searchPanel = new JPanel(new FlowLayout());
			searchNomInClientTextField = new JTextField(20);
			searchPrenomInClientTextField = new JTextField(20);
			JButton searchButton = new JButton("OK");
			searchButton.setActionCommand(SEARCH_CLIENT_SIGNAL);
			searchButton.addActionListener(this);
			searchPanel.add(new JLabel("Nom :"));
			searchPanel.add(searchNomInClientTextField);
			searchPanel.add(new JLabel("Prénom :"));
			searchPanel.add(searchPrenomInClientTextField);
			searchPanel.add(searchButton);

			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);
			searchPanel.add(menuButton);

			this.getRootPane().setDefaultButton(searchButton);

			/** Tableau **/

		// Données du tableau
			Object[][] data = new Object[clientList.size()][];
			for(int i=0; i<clientList.size(); i++)
			{
				data[i] = clientList.get(i).getTable();
			}

		// Titre des colonnes
			String title[] = {"Matricule", "Catégorie", "Nom", "Prénom", "Adresse", "E-Mail", "Inscription", "Renouvellement", "Emprunts effectués", "Emprunts dépassés", "Emprunts en cours"};

			clientTable = new JXTable(new CustomTableModel(data, title));
		if(!nom.isEmpty() && !prenom.isEmpty()) // Si on a fait une recherche
		{
			String regexNom = /*"(?i)^" +*/ nom;
			String regexPrenom = /*"(?i)^" +*/ prenom;

			int colNom = clientTable.getColumn("Nom").getModelIndex();
			int colPrenom = clientTable.getColumn("Prénom").getModelIndex();

			FilterPipeline fp = new FilterPipeline(new Filter[]{ new PatternFilter(regexNom , Pattern.CASE_INSENSITIVE, colNom), new PatternFilter(regexPrenom , Pattern.CASE_INSENSITIVE, colPrenom) });

			clientTable.setFilters(fp);
		}
		else if(!nom.isEmpty())
		{
			String regexNom = /*"(?i)^" +*/ nom;

			int colNom = clientTable.getColumn("Nom").getModelIndex();

			FilterPipeline fp = new FilterPipeline(new Filter[]{ new PatternFilter(regexNom , Pattern.CASE_INSENSITIVE, colNom)});

			clientTable.setFilters(fp);
		}
		else if(!prenom.isEmpty())
		{
			String regexPrenom = /*"(?i)^" +*/ prenom;

			int colPrenom = clientTable.getColumn("Prénom").getModelIndex();

			FilterPipeline fp = new FilterPipeline(new Filter[]{new PatternFilter(regexPrenom , Pattern.CASE_INSENSITIVE, colPrenom) });

			clientTable.setFilters(fp);
		}

		showClientPanel.add(searchPanel, BorderLayout.NORTH);
		showClientPanel.add(new JScrollPane(clientTable), BorderLayout.CENTER);

		return showClientPanel;
	}

	// Page du choix du type du document à ajouter
	public JPanel addChosenDoc()
	{
		JPanel addDocPanel = new JPanel();
		addDocPanel.setLayout(new BorderLayout());

		docCards = new JPanel();
		docCards.setLayout(new CardLayout());

		JPanel buttonPanel = new JPanel(new FlowLayout());
		JButton menuButton = new JButton("Menu");
		menuButton.setActionCommand(MENU_SIGNAL);
		menuButton.addActionListener(this);
		JButton audioButton = new JButton("Ajouter un audio");
		audioButton.setActionCommand(ADD_AUDIO_SIGNAL);
		audioButton.addActionListener(this);
		JButton livreButton = new JButton("Ajouter un livre");
		livreButton.setActionCommand(ADD_LIVRE_SIGNAL);
		livreButton.addActionListener(this);
		JButton videoButton = new JButton("Ajouter une vidéo");
		videoButton.setActionCommand(ADD_VIDEO_SIGNAL);
		videoButton.addActionListener(this);

		buttonPanel.add(menuButton);
		buttonPanel.add(audioButton);
		buttonPanel.add(livreButton);
		buttonPanel.add(videoButton);

		docCards.add(ADD_AUDIO_SIGNAL, chooseAudio());
		docCards.add(ADD_LIVRE_SIGNAL, chooseLivre());
		docCards.add(ADD_VIDEO_SIGNAL, chooseVideo());

		addDocPanel.add(buttonPanel, BorderLayout.PAGE_START);
		addDocPanel.add(docCards, BorderLayout.CENTER);

		return addDocPanel;


	}

	// Page d'ajout 'un audio'
	public JPanel chooseAudio()
	{
		JPanel audioPanel = new JPanel();

		JLabel audioLab = new JLabel("Ajouter un audio");
		audioLab.setFont(new Font("Serif", Font.PLAIN, 40));

		JLabel titreLab = new JLabel("Titre :");
		titreAudioTextField = new JTextField();

		JLabel auteurLab = new JLabel("Auteur :");
		auteurAudioTextField = new JTextField();

		JLabel anneeLab = new JLabel("Année :");
		anneeAudioTextField = new JTextField();

		JLabel locLab = new JLabel("Localisation :");
		locAudioCombo = new JComboBox<String>();

		for(Localisation l : locList)
		{
			locAudioCombo.addItem(l.getSalle() + "," + l.getRayon());
		}

		JLabel genreLab = new JLabel("Genre :");
		genreAudioCombo = new JComboBox<String>();

		for(Genre g : Genre.values())
		{
			genreAudioCombo.addItem(g.getNom());
		}

		JLabel classLab = new JLabel("Classification :");
		classTextField = new JTextField();

		JButton button = new JButton("OK");
		button.setActionCommand(CREATE_AUDIO_SIGNAL);
		button.addActionListener(this);

		GroupLayout l = new GroupLayout(audioPanel);
		l.setAutoCreateGaps(true);
		l.setAutoCreateContainerGaps(true);


		l.setHorizontalGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(audioLab)
			.addGroup(l.createSequentialGroup()
				.addComponent(titreLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(titreAudioTextField)
					)
				)
			.addGroup(l.createSequentialGroup()
				.addComponent(auteurLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(auteurAudioTextField)
					)
				)
			.addGroup(l.createSequentialGroup()
				.addComponent(anneeLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(anneeAudioTextField)
					)
				)
			.addGroup(l.createSequentialGroup()
				.addComponent(locLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(locAudioCombo)
					)
				)
			.addGroup(l.createSequentialGroup()
				.addComponent(genreLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(genreAudioCombo)
					)
				)
			.addGroup(l.createSequentialGroup()
				.addComponent(classLab)
				.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(classTextField)
					)
				)
			.addComponent(button)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING))
			);

		l.linkSize(SwingConstants.HORIZONTAL, titreLab, auteurLab, anneeLab, locLab, genreLab, classLab);
		l.linkSize(SwingConstants.HORIZONTAL, titreAudioTextField, auteurAudioTextField, anneeAudioTextField, locAudioCombo, genreAudioCombo, classTextField);

		l.setVerticalGroup(l.createSequentialGroup()
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(audioLab)
				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(titreLab)
				.addComponent(titreAudioTextField)
				)

			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(auteurLab)
				.addComponent(auteurAudioTextField)
				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(anneeLab)
				.addComponent(anneeAudioTextField)
				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(locLab)
				.addComponent(locAudioCombo)
				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(genreLab)
				.addComponent(genreAudioCombo)
				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(classLab)
				.addComponent(classTextField)

				)
			.addGroup(l.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(button)
				)



			.addGroup(l.createParallelGroup(GroupLayout.Alignment.LEADING))
			);


		audioPanel.setLayout(l);

		return audioPanel;
	}

	
	// Page d'ajout d'un livre
	public JPanel chooseLivre()
	{
		JPanel livrePanel = new JPanel();

		JLabel livreLab = new JLabel("Ajouter un livre");
		livreLab.setFont(new Font("Serif", Font.PLAIN, 40));

		JLabel titreLab = new JLabel("Titre :");
		titreLivreTextField = new JTextField();

		JLabel auteurLab = new JLabel("Auteur :");
		auteurLivreTextField = new JTextField();

		JLabel anneeLab = new JLabel("Année :");
		anneeLivreTextField = new JTextField();

		JLabel locLab = new JLabel("Localisation :");
		locLivreCombo = new JComboBox<String>();

		for(Localisation l : locList)
		{
			locLivreCombo.addItem(l.getSalle() + "," + l.getRayon());
		}

		JLabel genreLab = new JLabel("Genre :");
		genreLivreCombo = new JComboBox<String>();

		for(Genre g : Genre.values())
		{
			genreLivreCombo.addItem(g.getNom());
		}

		JLabel nbPagesLab = new JLabel("Nombre de pages :");
		nbPagesTextField = new JTextField();

		JButton button = new JButton("OK");
		button.setActionCommand(CREATE_LIVRE_SIGNAL);
		button.addActionListener(this);

		GroupLayout layout = new GroupLayout(livrePanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);


		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(livreLab)
			.addGroup(layout.createSequentialGroup()
				.addComponent(titreLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(titreLivreTextField)
					)
				)
			.addGroup(layout.createSequentialGroup()
				.addComponent(auteurLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(auteurLivreTextField)
					)
				)
			.addGroup(layout.createSequentialGroup()
				.addComponent(anneeLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(anneeLivreTextField)
					)
				)
			.addGroup(layout.createSequentialGroup()
				.addComponent(locLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(locLivreCombo)
					)
				)
			.addGroup(layout.createSequentialGroup()
				.addComponent(genreLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(genreLivreCombo)
					)
				)
			.addGroup(layout.createSequentialGroup()
				.addComponent(nbPagesLab)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(nbPagesTextField)
					)
				)
			.addComponent(button)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING))
			);

		layout.linkSize(SwingConstants.HORIZONTAL, titreLab, auteurLab, anneeLab, locLab, genreLab, nbPagesLab);
		layout.linkSize(SwingConstants.HORIZONTAL, titreLivreTextField, auteurLivreTextField, anneeLivreTextField, locLivreCombo, genreLivreCombo, nbPagesTextField);

		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(livreLab)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(titreLab)
				.addComponent(titreLivreTextField)
				)

			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(auteurLab)
				.addComponent(auteurLivreTextField)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(anneeLab)
				.addComponent(anneeLivreTextField)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(locLab)
				.addComponent(locLivreCombo)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(genreLab)
				.addComponent(genreLivreCombo)
				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(nbPagesLab)
				.addComponent(nbPagesTextField)

				)
			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(button)
				)



			.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING))
			);

		livrePanel.setLayout(layout);

		return livrePanel;
	}

	// Page d'ajout d'une vidéo
	public JPanel chooseVideo()
	{
		JPanel videoPanel = new JPanel();

		JLabel videoLab = new JLabel("Ajouter une vidéo");
		videoLab.setFont(new Font("Serif", Font.PLAIN, 40));

		JLabel titreLab = new JLabel("Titre :");
		titreVideoTextField = new JTextField();

		JLabel auteurLab = new JLabel("Réalisateur :");
		auteurVideoTextField = new JTextField();

		JLabel anneeLab = new JLabel("Année :");
		anneeVideoTextField = new JTextField();

		JLabel locLab = new JLabel("Localisation :");
		locVideoCombo = new JComboBox<String>();

		for(Localisation l : locList)
		{
			locVideoCombo.addItem(l.getSalle() + "," + l.getRayon());
		}

		JLabel genreLab = new JLabel("Genre :");
		genreVideoCombo = new JComboBox<String>();
		for(Genre g : Genre.values())
		{
			genreVideoCombo.addItem(g.getNom());
		}

		JLabel dureeLab = new JLabel("Durée (min) :");
		dureeTextField = new JTextField();

		JButton button = new JButton("OK");
		button.setActionCommand(CREATE_VIDEO_SIGNAL);
		button.addActionListener(this);

		GroupLayout layo = new GroupLayout(videoPanel);
		layo.setAutoCreateGaps(true);
		layo.setAutoCreateContainerGaps(true);


		layo.setHorizontalGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(videoLab)
			.addGroup(layo.createSequentialGroup()
				.addComponent(titreLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(titreVideoTextField)
					)
				)
			.addGroup(layo.createSequentialGroup()
				.addComponent(auteurLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(auteurVideoTextField)
					)
				)
			.addGroup(layo.createSequentialGroup()
				.addComponent(anneeLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(anneeVideoTextField)
					)
				)
			.addGroup(layo.createSequentialGroup()
				.addComponent(locLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(locVideoCombo)
					)
				)
			.addGroup(layo.createSequentialGroup()
				.addComponent(genreLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(genreVideoCombo)
					)
				)
			.addGroup(layo.createSequentialGroup()
				.addComponent(dureeLab)
				.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(dureeTextField)
					)
				)
			.addComponent(button)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING))
			);

		layo.linkSize(SwingConstants.HORIZONTAL, titreLab, auteurLab, anneeLab, locLab, genreLab, dureeLab);
		layo.linkSize(SwingConstants.HORIZONTAL, titreVideoTextField, auteurVideoTextField, anneeVideoTextField, locVideoCombo, genreVideoCombo, dureeTextField);

		layo.setVerticalGroup(layo.createSequentialGroup()
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(videoLab)
				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(titreLab)
				.addComponent(titreVideoTextField)
				)

			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(auteurLab)
				.addComponent(auteurVideoTextField)
				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(anneeLab)
				.addComponent(anneeVideoTextField)
				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(locLab)
				.addComponent(locVideoCombo)
				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(genreLab)
				.addComponent(genreVideoCombo)
				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(dureeLab)
				.addComponent(dureeTextField)

				)
			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(button)
				)



			.addGroup(layo.createParallelGroup(GroupLayout.Alignment.LEADING))
			);


		videoPanel.setLayout(layo);

		return videoPanel;
	}

	// Page de choix de l'affichage des documents
	public JPanel choiceDocList()
	{
		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout());

		JButton menuButton = new JButton("Menu");
		menuButton.setActionCommand(MENU_SIGNAL);
		menuButton.addActionListener(this);

		JButton locButton = new JButton("Par localisation");
		locButton.setActionCommand(LOC_DOC_SIGNAL);
		locButton.addActionListener(this);

		JButton genreButton = new JButton("Par genre");
		genreButton.setActionCommand(GENRE_DOC_SIGNAL);
		genreButton.addActionListener(this);

		JButton allButton = new JButton("Tous");
		allButton.setActionCommand(SHOW_DOC_SIGNAL);
		allButton.addActionListener(this);

		buttonPanel.add(menuButton);
		buttonPanel.add(locButton);
		buttonPanel.add(genreButton);
		buttonPanel.add(allButton);

		pageCards = new JPanel();
		pageCards.setLayout(new CardLayout());
		locDocPanel = locDocList();
		pageCards.add(LOC_DOC_SIGNAL, locDocPanel);
		genreDocPanel = genreDocList();
		pageCards.add(GENRE_DOC_SIGNAL, genreDocPanel);
		showDocPanel = showDocList("", "");
		pageCards.add(SHOW_DOC_SIGNAL, showDocPanel);

		choicePanel.add(buttonPanel, BorderLayout.PAGE_START);
		choicePanel.add(pageCards, BorderLayout.CENTER);

		return choicePanel;
	}

	// Affichage des documents par localisation
	public JPanel locDocList()
	{
		JPanel locDocPanel = new JPanel();
		locDocPanel.setLayout(new BorderLayout());
		locDocPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		JPanel salleAllPanel = new JPanel();
		salleAllPanel.setLayout(new BoxLayout(salleAllPanel, BoxLayout.Y_AXIS));

		JPanel sallePanel;
		JPanel rayPanel;

		ArrayList<JPanel> sallePanelList = new ArrayList<JPanel>();
		ArrayList<JPanel> rayPanelList = new ArrayList<JPanel>();

		/** Tableau **/
		// Un tableau pour chaque loc
		Object[][] data;
		String audioTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Classification"};
		String livreTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Nombre de Pages"};
		String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film"};

		// liste des salles
		ArrayList<String> salleList = new ArrayList<String>();
		for(Localisation l : locList)
		{
			if(!salleList.contains(l.getSalle()))
			{
				salleList.add(l.getSalle());
			}
		}
		// tri
		Collections.sort(salleList);

		// Par salle
		ArrayList<String> rayList = new ArrayList<String>();
		ArrayList<Document> audioList = new ArrayList<Document>();
		ArrayList<Document> livreList = new ArrayList<Document>();
		ArrayList<Document> videoList = new ArrayList<Document>();
		for(String s : salleList)
		{
			sallePanel = new JPanel();
			sallePanel.setLayout(new BoxLayout(sallePanel, BoxLayout.Y_AXIS));

			rayList.clear();
			for(Localisation l : locList)
			{
				if(l.getSalle().equals(s) && !rayList.contains(l.getRayon()))
				{
					rayList.add(l.getRayon());
				}
			}
			// tri
			Collections.sort(rayList);

			rayPanelList.clear();

			// Par rayon
			for(String r : rayList)
			{
				rayPanel = new JPanel();
				rayPanel.setLayout(new BoxLayout(rayPanel, BoxLayout.Y_AXIS));
				JLabel rayonLabel = new JLabel(r);
				rayonLabel.setFont(new Font("Serif", Font.PLAIN, 30));
				rayPanel.add(rayonLabel);

				// liste des doc
				audioList.clear();
				livreList.clear();
				videoList.clear();
				for(Document d : docList)
				{
					if(d.getLoc().getSalle().equals(s) && d.getLoc().getRayon().equals(r))
					{
						if(d instanceof Audio)
						{
							audioList.add(d);
						}
						else if(d instanceof Livre)
						{
							livreList.add(d);
						}
						else // Video
						{
							videoList.add(d);
						}
					}
				}

				// tables
				// audio
				if(!audioList.isEmpty())
				{
					data = new Object[audioList.size()][];
					for(int i=0; i<audioList.size(); i++)
					{
						data[i] = audioList.get(i).getTable();
					}

					audioTable = new JXTable(new CustomTableModel(data, audioTitle));

					rayPanel.add(new JLabel("Audio"));
					rayPanel.add(audioTable.getTableHeader());
					rayPanel.add(audioTable);
				}


				// livre
				if(!livreList.isEmpty())
				{
					data = new Object[livreList.size()][];
					for(int i=0; i<livreList.size(); i++)
					{
						data[i] = livreList.get(i).getTable();
					}

					livreTable = new JXTable(new CustomTableModel(data, livreTitle));

					rayPanel.add(new JLabel("Livre"));
					rayPanel.add(livreTable.getTableHeader());
					rayPanel.add(livreTable);
				}
				

				// video
				if(!videoList.isEmpty())
				{
					data = new Object[videoList.size()][];
					for(int i=0; i<videoList.size(); i++)
					{
						data[i] = videoList.get(i).getTable();
					}

					videoTable = new JXTable(new CustomTableModel(data, videoTitle));

					rayPanel.add(new JLabel("Video"));
					rayPanel.add(videoTable.getTableHeader());
					rayPanel.add(videoTable);
				}

				if(!audioList.isEmpty() || !livreList.isEmpty() || !videoList.isEmpty())
				{
					rayPanelList.add(rayPanel);
				}
			}

			// sallePanel
			JLabel salleLabel = new JLabel(s);
			salleLabel.setFont(new Font("Serif", Font.PLAIN, 40));
			sallePanel.add(salleLabel);
			for(JPanel r : rayPanelList)
			{
				sallePanel.add(r);
			}

			sallePanelList.add(sallePanel);

		}

		for(JPanel s : sallePanelList)
		{
			salleAllPanel.add(s);
		}

		locDocPanel.add(new JScrollPane(salleAllPanel), BorderLayout.CENTER);

		return locDocPanel;
	}

	// Affichage des documents par genre
	public JPanel genreDocList()
	{
		JPanel genreDocPanel = new JPanel();
		genreDocPanel.setLayout(new BorderLayout());
		genreDocPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		JPanel genreAllPanel = new JPanel();
		genreAllPanel.setLayout(new BoxLayout(genreAllPanel, BoxLayout.Y_AXIS));

		JPanel genrePanel;

		ArrayList<JPanel> genrePanelList = new ArrayList<JPanel>();

		/** Tableau **/
		// Un tableau pour chaque genre
		Object[][] data;
		String audioTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Classification"};
		String livreTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Nombre de Pages"};
		String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film"};

		// tri
		Collections.sort(genreList);

		// Par genre
		ArrayList<Document> audioList = new ArrayList<Document>();
		ArrayList<Document> livreList = new ArrayList<Document>();
		ArrayList<Document> videoList = new ArrayList<Document>();

		for(Genre g : genreList)
		{
			genrePanel = new JPanel();
			genrePanel.setLayout(new BoxLayout(genrePanel, BoxLayout.Y_AXIS));
			JLabel genreLabel = new JLabel(g.getNom());
			genreLabel.setFont(new Font("Serif", Font.PLAIN, 40));
			genrePanel.add(genreLabel);

			// liste des doc
			audioList.clear();
			livreList.clear();
			videoList.clear();

			for(Document d : docList)
			{
				if(d.getGenre().ordinal() == g.ordinal())
				{
					if(d instanceof Audio)
					{
						audioList.add(d);
					}
					else if(d instanceof Livre)
					{
						livreList.add(d);
					}
						else // Video
						{
							videoList.add(d);
						}
					}
				}

				// tables
				// audio
				if(!audioList.isEmpty())
				{
					data = new Object[audioList.size()][];
					for(int i=0; i<audioList.size(); i++)
					{
						data[i] = audioList.get(i).getTable();
					}

					audioTable = new JXTable(new CustomTableModel(data, audioTitle));

					genrePanel.add(new JLabel("Audio"));
					genrePanel.add(audioTable.getTableHeader());
					genrePanel.add(audioTable);
				}


				// livre
				if(!livreList.isEmpty())
				{
					data = new Object[livreList.size()][];
					for(int i=0; i<livreList.size(); i++)
					{
						data[i] = livreList.get(i).getTable();
					}

					livreTable = new JXTable(new CustomTableModel(data, livreTitle));

					genrePanel.add(new JLabel("Livre"));
					genrePanel.add(livreTable.getTableHeader());
					genrePanel.add(livreTable);
				}
				

				// video
				if(!videoList.isEmpty())
				{
					data = new Object[videoList.size()][];
					for(int i=0; i<videoList.size(); i++)
					{
						data[i] = videoList.get(i).getTable();
					}

					videoTable = new JXTable(new CustomTableModel(data, videoTitle));

					genrePanel.add(new JLabel("Video"));
					genrePanel.add(videoTable.getTableHeader());
					genrePanel.add(videoTable);
				}

				if(!audioList.isEmpty() || !livreList.isEmpty() || !videoList.isEmpty())
				{
					genrePanelList.add(genrePanel);
				}
			}



			for(JPanel p : genrePanelList)
			{
				genreAllPanel.add(p);
			}

			genreDocPanel.add(new JScrollPane(genreAllPanel), BorderLayout.CENTER);

			return genreDocPanel;
		}

	// Page de la liste des documents en fonction d'une recherche (String)
		public JPanel showDocList(String titre, String auteur)
		{
			JPanel showPanel = new JPanel();
			showPanel.setLayout(new BorderLayout());
			showPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

			/** Recherche **/
			JPanel searchPanel = new JPanel(new FlowLayout());
			searchTitreInDocTextField = new JTextField(20);
			searchAuteurInDocTextField = new JTextField(20);

			JButton searchButton = new JButton("OK");
			searchButton.setActionCommand(SEARCH_DOC_SIGNAL);
			searchButton.addActionListener(this);
			
			searchPanel.add(new JLabel("Titre :"));
			searchPanel.add(searchTitreInDocTextField);
			searchPanel.add(new JLabel("Auteur :"));
			searchPanel.add(searchAuteurInDocTextField);
			searchPanel.add(searchButton);

			Object[][] data;
			String audioTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Classification"};
			String livreTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Nombre de Pages"};
			String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film"};

			audioTable = new JXTable();
			livreTable = new JXTable();
			videoTable = new JXTable();

			ArrayList<Document> audioList = new ArrayList<Document>();
			ArrayList<Document> livreList = new ArrayList<Document>();
			ArrayList<Document> videoList = new ArrayList<Document>();

			JPanel showDocListPanel = new JPanel();
			showDocListPanel.setLayout(new BoxLayout(showDocListPanel, BoxLayout.Y_AXIS));

			/** Tableau **/

			for(Document d : docList)
			{
				if(d instanceof Audio)
				{
					audioList.add(d);
				}
				else if(d instanceof Livre)
				{
					livreList.add(d);
				}
						else // Video
						{
							videoList.add(d);
						}
					}


				// tables
				// audio
					if(!audioList.isEmpty())
					{
						data = new Object[audioList.size()][];
						for(int i=0; i<audioList.size(); i++)
						{
							data[i] = audioList.get(i).getTable();
						}

						audioTable = new JXTable(new CustomTableModel(data, audioTitle));

						showDocListPanel.add(new JLabel("Audio"));
						showDocListPanel.add(audioTable.getTableHeader());
						showDocListPanel.add(audioTable);
					}


				// livre
					if(!livreList.isEmpty())
					{
						data = new Object[livreList.size()][];
						for(int i=0; i<livreList.size(); i++)
						{
							data[i] = livreList.get(i).getTable();
						}

						livreTable = new JXTable(new CustomTableModel(data, livreTitle));

						showDocListPanel.add(new JLabel("Livre"));
						showDocListPanel.add(livreTable.getTableHeader());
						showDocListPanel.add(livreTable);
					}


				// video
					if(!videoList.isEmpty())
					{
						data = new Object[videoList.size()][];
						for(int i=0; i<videoList.size(); i++)
						{
							data[i] = videoList.get(i).getTable();
						}

						videoTable = new JXTable(new CustomTableModel(data, videoTitle));

						showDocListPanel.add(new JLabel("Video"));
						showDocListPanel.add(videoTable.getTableHeader());
						showDocListPanel.add(videoTable);
					}
		// Titre des colonnes

		if(!titre.isEmpty() && !auteur.isEmpty()) // Si on a fait une recherche
		{
			String regexTitre = /*"(?i)^" +*/ titre;
			String regexAuteur = /*"(?i)^" +*/ auteur;

			int colTitre = audioTable.getColumn("Titre").getModelIndex();
			int colAuteur = audioTable.getColumn("Auteur").getModelIndex();

			System.out.println("Mediatheque - showDocList - colTitre = " + colTitre + " regexTitre = " + regexTitre);
			System.out.println("Mediatheque - showDocList - colAuteur = " + colAuteur + " regexAuteur = " + regexAuteur);

			FilterPipeline audioFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre), new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });
			FilterPipeline livreFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre), new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });
			FilterPipeline videoFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre), new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });

			if(!audioList.isEmpty())
			{
				audioTable.setFilters(audioFp);
			}
			if(!livreList.isEmpty())
			{
				livreTable.setFilters(livreFp);
			}
			if(!videoList.isEmpty())
			{
				videoTable.setFilters(videoFp);
			}
		}
		else if(!titre.isEmpty())
		{
			String regexTitre = /*"(?i)^" +*/ titre;

			int colTitre = audioTable.getColumn("Titre").getModelIndex();

			System.out.println("Mediatheque - showDocList - colTitre = " + colTitre + " regexTitre = " + regexTitre);

			FilterPipeline audioFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre)});
			FilterPipeline livreFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre)});
			FilterPipeline videoFp = new FilterPipeline(new Filter[]{ new PatternFilter(regexTitre , Pattern.CASE_INSENSITIVE, colTitre)});

			if(!audioList.isEmpty())
			{
				audioTable.setFilters(audioFp);
			}
			if(!livreList.isEmpty())
			{
				livreTable.setFilters(livreFp);
			}
			if(!videoList.isEmpty())
			{
				videoTable.setFilters(videoFp);
			}		}
			else if(!auteur.isEmpty())
			{
				String regexAuteur = /*"(?i)^" +*/ auteur;

				int colAuteur = audioTable.getColumn("Auteur").getModelIndex();

				System.out.println("Mediatheque - showDocList - colAuteur = " + colAuteur + " regexAuteur = " + regexAuteur);

				FilterPipeline audioFp = new FilterPipeline(new Filter[]{new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });
				FilterPipeline livreFp = new FilterPipeline(new Filter[]{new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });
				FilterPipeline videoFp = new FilterPipeline(new Filter[]{new PatternFilter(regexAuteur , Pattern.CASE_INSENSITIVE, colAuteur) });

				if(!audioList.isEmpty())
				{
					audioTable.setFilters(audioFp);
				}
				if(!livreList.isEmpty())
				{
					livreTable.setFilters(livreFp);
				}
				if(!videoList.isEmpty())
				{
					videoTable.setFilters(videoFp);
				}
			}

			showPanel.add(searchPanel, BorderLayout.NORTH);
			showPanel.add(new JScrollPane(showDocListPanel), BorderLayout.CENTER);

			return showPanel;
		}

	// Page d'emprunt d'un document
		public JPanel empruntDoc(String titre, String auteur, String nom, String prenom)
		{
		// Choix du document
			empruntDoc = new JPanel();
			empruntDoc.setLayout(new CardLayout());

			JPanel menuPanel = new JPanel(new FlowLayout());
			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);
			menuPanel.add(menuButton);

			JPanel choixDoc = new JPanel();
			choixDoc.setLayout(new BorderLayout());

			choixDoc.add(menuPanel, BorderLayout.PAGE_START);
			choixDoc.add(new JScrollPane(showDocList(titre, auteur)), BorderLayout.CENTER);

			JPanel buttonPanel = new JPanel(new FlowLayout());

			JButton okButton = new JButton("OK");
			okButton.setActionCommand(CHOIX_DOC_SIGNAL);
			okButton.addActionListener(this);

			buttonPanel.add(okButton);

			choixDoc.add(buttonPanel, BorderLayout.PAGE_END);

			empruntDoc.add(CHOIX_DOC_SIGNAL, choixDoc);

			JPanel choixClient = new JPanel();
			choixClient.setLayout(new BorderLayout());
			choixClient.add(showClientList(nom, prenom), BorderLayout.CENTER);

			JPanel button2Panel = new JPanel(new FlowLayout());

			JButton oKButton = new JButton("OK");
			oKButton.setPreferredSize(new Dimension(100, 20));
			oKButton.setActionCommand(CREATE_FICHE_SIGNAL);
			oKButton.addActionListener(this);

			button2Panel.add(oKButton);

			choixClient.add(button2Panel, BorderLayout.PAGE_END);


			empruntDoc.add(CHOIX_CLIENT_SIGNAL, choixClient);


			if(!titre.isEmpty() || !auteur.isEmpty())
			{

				((CardLayout)empruntDoc.getLayout()).show(empruntDoc, CHOIX_DOC_SIGNAL);
			}
			else if(!nom.isEmpty() || !prenom.isEmpty())
			{

				((CardLayout)empruntDoc.getLayout()).show(empruntDoc, CHOIX_CLIENT_SIGNAL);
			}
			else
			{

				((CardLayout)empruntDoc.getLayout()).show(empruntDoc, CHOIX_DOC_SIGNAL);

			}

			return empruntDoc;
		}

	// Page de rendu de document
		public JPanel renduDoc(String doc, String client)
		{
			JPanel fichePanel = new JPanel();
			fichePanel.setLayout(new BorderLayout());

			/** Recherche **/
			JPanel searchPanel = new JPanel(new FlowLayout());
			searchDocInFicheTextField = new JTextField(20);
			searchClientInFicheTextField = new JTextField(20);

			JButton searchButton = new JButton("OK");
			searchButton.setActionCommand(SEARCH_FICHE_SIGNAL);
			searchButton.addActionListener(this);

			searchPanel.add(new JLabel("Document : "));
			searchPanel.add(searchDocInFicheTextField);
			searchPanel.add(new JLabel("Client : "));
			searchPanel.add(searchClientInFicheTextField);
			searchPanel.add(searchButton);

			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);
			searchPanel.add(menuButton);

			JPanel okPanel = new JPanel(new FlowLayout());

			JButton okButton = new JButton("OK");
			okButton.setActionCommand(DEL_EMPRUNT_SIGNAL);
			okButton.addActionListener(this);

			okPanel.add(okButton);


			this.getRootPane().setDefaultButton(searchButton);

			/** Tableau **/

		// Données du tableau
			Object[][] data = new Object[ficheList.size()][];
			for(int i=0; i<ficheList.size(); i++)
			{
				data[i] = ficheList.get(i).getTable(docList, clientList);
			}

		// Titre des colonnes
			String title[] = {"Document", "Client", "Date d'emprunt", "Date limite", "Dépassé", "Tarif"};

			ficheTable = new JXTable(new CustomTableModel(data, title));
		if(!doc.isEmpty() && !client.isEmpty()) // Si on a fait une recherche
		{
			String regexDoc = /*"(?i)^" +*/ doc;
			String regexClient = /*"(?i)^" +*/ client;

			int colDoc = ficheTable.getColumn("Document").getModelIndex();
			int colClient = ficheTable.getColumn("Client").getModelIndex();

			System.out.println("Mediatheque - renduDoc - regexDoc = " + regexDoc);
			System.out.println("Mediatheque - renduDoc - regexClient = " + regexClient);

			FilterPipeline fp = new FilterPipeline(new Filter[]{ new PatternFilter(regexDoc , Pattern.CASE_INSENSITIVE, colDoc), new PatternFilter(regexClient , Pattern.CASE_INSENSITIVE, colClient) });

			ficheTable.setFilters(fp);
		}
		else if(!doc.isEmpty())
		{
			String regexDoc = /*"(?i)^" +*/ doc;

			int colDoc = ficheTable.getColumn("Document").getModelIndex();

			System.out.println("Mediatheque - renduDoc - regexDoc = " + regexDoc);

			FilterPipeline fp = new FilterPipeline(new Filter[]{ new PatternFilter(regexDoc , Pattern.CASE_INSENSITIVE, colDoc)});

			ficheTable.setFilters(fp);
		}
		else if(!client.isEmpty())
		{
			String regexClient = /*"(?i)^" +*/ client;

			int colClient = ficheTable.getColumn("Client").getModelIndex();

			System.out.println("Mediatheque - renduDoc - regexClient = " + regexClient);

			FilterPipeline fp = new FilterPipeline(new Filter[]{new PatternFilter(regexClient , Pattern.CASE_INSENSITIVE, colClient) });

			ficheTable.setFilters(fp);
		}

		fichePanel.add(searchPanel, BorderLayout.NORTH);
		fichePanel.add(new JScrollPane(ficheTable), BorderLayout.CENTER);
		fichePanel.add(okPanel, BorderLayout.PAGE_END);

		return fichePanel;
	}

	// Actions pour tous les boutons
	public void actionPerformed(ActionEvent e) 
	{
		// Menu
		if(e.getActionCommand().equals(MENU_SIGNAL)) 
		{
			((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); // Page menu
		}
		// Mise à jour de la médiathèque
		else if(e.getActionCommand().equals(UPDATE_SIGNAL)) 
		{
			update();
		}
		// Recherche de client
		else if(e.getActionCommand().equals(SEARCH_CLIENT_SIGNAL)) 
		{
			
			String nom = searchNomInClientTextField.getText();
			String prenom = searchPrenomInClientTextField.getText();

			// Mise à jour de l'affichage de la liste des clients en fonction de la recherche
			
			if(searchInEmprunt) // EN cours d'empruny
			{
				cards.remove(empruntPanel);
				empruntPanel = empruntDoc("", "", nom, prenom);
				cards.add(EMPRUNT_SIGNAL, empruntPanel);
				cards.revalidate();
				cards.repaint();

				((CardLayout) cards.getLayout()).show(cards,EMPRUNT_SIGNAL); // Page d'emprunt
			}
			else if(searchInRenew) // En cours de réinscription
			{
				cards.remove(renewPanel);
				renewPanel = reinscrire(nom, prenom);
				cards.add(RENEW_SIGNAL, empruntPanel);
				cards.revalidate();
				cards.repaint();

				((CardLayout) cards.getLayout()).show(cards,RENEW_SIGNAL); // Page de réinscription
			}
			else
			{
				cards.remove(showClientPanel);
				showClientPanel = showClientList(nom, prenom);
				cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
				cards.revalidate();
				cards.repaint();

				((CardLayout) cards.getLayout()).show(cards,SHOW_CLIENT_SIGNAL); // Page de liste des clients
			}
		}
		// Recherche de document
		else if(e.getActionCommand().equals(SEARCH_DOC_SIGNAL)) 
		{
			
			String titre = searchTitreInDocTextField.getText();
			String auteur = searchAuteurInDocTextField.getText();

			// Mise à jour de l'affichage de la liste des clients en fonction de la recherche
			
			if(searchInEmprunt) // En cours d'emprunt
			{
				cards.remove(empruntPanel);
				empruntPanel = empruntDoc(titre, auteur, "", "");
				cards.add(EMPRUNT_SIGNAL, empruntPanel);
				cards.revalidate();
				cards.repaint();

				((CardLayout) cards.getLayout()).show(cards,EMPRUNT_SIGNAL); // Page d'emprunt
			}
			else
			{
				pageCards.remove(showDocPanel);
				showDocPanel = showDocList(titre, auteur);
				pageCards.add(SHOW_DOC_SIGNAL, showDocPanel);
				pageCards.revalidate();
				pageCards.repaint();

				((CardLayout) pageCards.getLayout()).show(pageCards,SHOW_DOC_SIGNAL); // Page de liste de clients
			}
		}
		// Recherche de fiche
		else if(e.getActionCommand().equals(SEARCH_FICHE_SIGNAL)) 
		{
			String doc = searchDocInFicheTextField.getText();
			String client = searchClientInFicheTextField.getText();

			cards.remove(renduPanel);
			renduPanel = renduDoc(doc, client);
			cards.add(RENDU_SIGNAL, renduPanel);
			cards.revalidate();
			cards.repaint();

			((CardLayout) cards.getLayout()).show(cards,RENDU_SIGNAL); // Page de rendu de document
		}
		// Ajout d'un client
		else if(e.getActionCommand().equals(ADD_CLIENT_SIGNAL)) 
		{
			createPanel = createClient();
			cards.add(CREATE_CLIENT_SIGNAL, createPanel);

			((CardLayout) cards.getLayout()).show(cards,CREATE_CLIENT_SIGNAL); // Page de création d'un client
		}
		// Affichage des clientss
		else if(e.getActionCommand().equals(SHOW_CLIENT_SIGNAL)) 
		{
			searchInEmprunt = false;
			searchInRenew = false;

			// Relise à zéro des champs
			cards.remove(showClientPanel);
			showClientPanel = showClientList("","");
			cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
			cards.revalidate();
			cards.repaint();

			((CardLayout) cards.getLayout()).show(cards,SHOW_CLIENT_SIGNAL); // Page de la liste des clients
		}
		// Ajout d'un document
		else if(e.getActionCommand().equals(ADD_DOC_SIGNAL)) 
		{
			((CardLayout) cards.getLayout()).show(cards, ADD_DOC_SIGNAL); // Page d'ajout d'un document
		}
		// Ajout d'un document audio
		else if(e.getActionCommand().equals(ADD_AUDIO_SIGNAL)) 
		{
			((CardLayout) docCards.getLayout()).show(docCards,ADD_AUDIO_SIGNAL); // Page d'ajout d'un audio
		}
		// Ajout d'un livre
		else if(e.getActionCommand().equals(ADD_LIVRE_SIGNAL)) 
		{
			((CardLayout) docCards.getLayout()).show(docCards,ADD_LIVRE_SIGNAL); // Page d'ajout d'un livre
		}
		// Ajout d'une vidéo
		else if(e.getActionCommand().equals(ADD_VIDEO_SIGNAL)) 
		{
			((CardLayout) docCards.getLayout()).show(docCards,ADD_VIDEO_SIGNAL); // Page d'ajout d'une vidéo
		}
		// Création d'un document audio
		else if(e.getActionCommand().equals(CREATE_AUDIO_SIGNAL)) 
		{
			// Tous les champs remplis
			if(!titreAudioTextField.getText().isEmpty() && !auteurAudioTextField.getText().isEmpty() && !anneeAudioTextField.getText().isEmpty() && !classTextField.getText().isEmpty())
			{
				try
				{
					addDocument("a", new String(titreAudioTextField.getText()), new String (auteurAudioTextField.getText()), new String(checkAnnee(anneeAudioTextField.getText())), new String(locAudioCombo.getSelectedItem().toString()), new String(genreAudioCombo.getSelectedItem().toString()), 0, new String(checkClassification(classTextField.getText())));


				// Mise à jour de l'affichage
					cards.remove(choicePanel);
					choicePanel = choiceDocList();
					cards.add(CHOICE_DOC_SIGNAL, choicePanel);

					cards.remove(empruntPanel);
					empruntPanel = empruntDoc("", "", "", "");
					cards.add(EMPRUNT_SIGNAL, empruntPanel);

					cards.revalidate();
					cards.repaint();

				// Notification
					JOptionPane.showMessageDialog(null, firstUpperCase(titreAudioTextField.getText()) + " ajouté", "Ajout d'un audio", JOptionPane.INFORMATION_MESSAGE);

					cards.remove(addDocPanel);
					addDocPanel = addChosenDoc();
					cards.add(ADD_DOC_SIGNAL, addDocPanel);

					System.out.println("Ajout d'un audio.");

				write(); // Sauvegarde

				((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); // Page menu
			}
			catch(InvalidYearException in)
			{
				JOptionPane.showMessageDialog(null, in.getMessage(), "Attention", JOptionPane.INFORMATION_MESSAGE);

			}
			catch(InvalidClassificationException cl)
			{
				JOptionPane.showMessageDialog(null, cl.getMessage(), "Attention", JOptionPane.INFORMATION_MESSAGE);

			}
			catch(AlreadyInDocException d)
			{
				JOptionPane.showMessageDialog(null, d.getMessage(), "Attention", JOptionPane.INFORMATION_MESSAGE);

			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Attention", JOptionPane.INFORMATION_MESSAGE);

		}
	}
		// Création d'un livre
	else if(e.getActionCommand().equals(CREATE_LIVRE_SIGNAL))
	{
		// Tous les champs remplis
		if(!titreLivreTextField.getText().isEmpty() && !auteurLivreTextField.getText().isEmpty() && !anneeLivreTextField.getText().isEmpty() && !nbPagesTextField.getText().isEmpty())
		{
			try
			{
				addDocument("l", new String(titreLivreTextField.getText()), new String (auteurLivreTextField.getText()), new String(checkAnnee(anneeLivreTextField.getText())), new String(locLivreCombo.getSelectedItem().toString()), new String(genreLivreCombo.getSelectedItem().toString()), checkNbPages(new String(nbPagesTextField.getText())), "");

				// Mise à jour de l'affichage
				cards.remove(choicePanel);
				choicePanel = choiceDocList();
				cards.add(CHOICE_DOC_SIGNAL, choicePanel);

				cards.remove(empruntPanel);
				empruntPanel = empruntDoc("", "", "", "");
				cards.add(EMPRUNT_SIGNAL, empruntPanel);

				cards.revalidate();
				cards.repaint();

				// Notification
				JOptionPane.showMessageDialog(null, firstUpperCase(titreLivreTextField.getText()) + " ajouté", "Ajout d'un livre", JOptionPane.INFORMATION_MESSAGE);

				cards.remove(addDocPanel);
				addDocPanel = addChosenDoc();
				cards.add(ADD_DOC_SIGNAL, addDocPanel);

				System.out.println("Ajout d'un livre.");

				write(); // Sauvegarde

				((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
			}
			catch(InvalidYearException in)
			{
				JOptionPane.showMessageDialog(null, in.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);

			}
			catch(InvalidNbPagesException p)
			{
				JOptionPane.showMessageDialog(null, p.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre pour le nombre de pages", "Attention", JOptionPane.WARNING_MESSAGE);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Attention", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	else if(e.getActionCommand().equals(CREATE_VIDEO_SIGNAL))
	{
				// Tous les champs sont remplis
		if(!titreVideoTextField.getText().isEmpty() && !auteurVideoTextField.getText().isEmpty() && !anneeVideoTextField.getText().isEmpty() && !dureeTextField.getText().isEmpty())
		{
			try
			{
				addDocument("v", new String(titreVideoTextField.getText()), new String (auteurVideoTextField.getText()), new String(checkAnnee(anneeVideoTextField.getText())), new String(locVideoCombo.getSelectedItem().toString()), new String(genreVideoCombo.getSelectedItem().toString()), Integer.parseInt(new String(dureeTextField.getText())), "");

				// Mise à jour de l'affichage
				cards.remove(choicePanel);
				choicePanel = choiceDocList();
				cards.add(CHOICE_DOC_SIGNAL, choicePanel);

				cards.remove(empruntPanel);
				empruntPanel = empruntDoc("", "", "", "");
				cards.add(EMPRUNT_SIGNAL, empruntPanel);

				cards.revalidate();
				cards.repaint();

				// Notification
				JOptionPane.showMessageDialog(null, firstUpperCase(titreVideoTextField.getText()) + " ajouté", "Ajout d'une vidéo", JOptionPane.INFORMATION_MESSAGE);

				cards.remove(addDocPanel);
				addDocPanel = addChosenDoc();
				cards.add(ADD_DOC_SIGNAL, addDocPanel);

				System.out.println("Ajout d'une vidéo.");

				write(); // Sauvegarde

				((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); // Page menu
			}
			catch(InvalidYearException in)
			{
				JOptionPane.showMessageDialog(null, in.getMessage(), "Attention", JOptionPane.WARNING_MESSAGE);

			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, "Veuillez entrer un nombre pour la durée du film", "Attention", JOptionPane.WARNING_MESSAGE);
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Attention", JOptionPane.INFORMATION_MESSAGE);

		}
	}
	// Choix de l'affichage des documents
	else if(e.getActionCommand().equals(CHOICE_DOC_SIGNAL))
	{
			// Mise à jour de l'affichage 
		cards.remove(choicePanel);
		choicePanel = choiceDocList();
		cards.add(CHOICE_DOC_SIGNAL, choicePanel);
		cards.revalidate();
		cards.repaint();

		((CardLayout) cards.getLayout()).show(cards,CHOICE_DOC_SIGNAL);
	}
		// Affichage de la liste de tous les document
	else if(e.getActionCommand().equals(SHOW_DOC_SIGNAL))
	{
			searchInEmprunt = false; // Pas dans emprunt

			((CardLayout) pageCards.getLayout()).show(pageCards,SHOW_DOC_SIGNAL);
		}
		// Affichage des documents par localisation
		else if(e.getActionCommand().equals(LOC_DOC_SIGNAL)) 
		{
			((CardLayout) pageCards.getLayout()).show(pageCards,LOC_DOC_SIGNAL);
		}
		// Affichage des documents par genre
		else if(e.getActionCommand().equals(GENRE_DOC_SIGNAL)) 
		{
			((CardLayout) pageCards.getLayout()).show(pageCards,GENRE_DOC_SIGNAL);
		}
		// Création d'un client
		else if(e.getActionCommand().equals(CREATE_CLIENT_SIGNAL)) 
		{
			// Tous les champs remplis
			if(!nomTextField.getText().isEmpty() && !prenomTextField.getText().isEmpty() && !adresseTextField.getText().isEmpty() && !emailTextField.getText().isEmpty())
			{

				CategorieClient cat = CategorieClient.ETUDIANT;
				cat = cat.setCategorie(categorieCombo.getSelectedItem().toString());

				Client c = new Client(cat, new String(nomTextField.getText()), new String(prenomTextField.getText()), new String(adresseTextField.getText()), new String(emailTextField.getText()), nbClient);
				
				// Déjà dans la liste ?
				if(!alreadyInClientList(c))
				{
					clientList.add(c);

					// Mise à jour de l'affichage de la liste des clients
					cards.remove(showClientPanel);
					showClientPanel = showClientList("", "");
					cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
					cards.remove(createPanel);
					cards.revalidate();
					cards.repaint();

					// Sauvegarde
					System.out.println("Ajout d'un client.");
					write();

					((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Le client existe déjà.", "Attention", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			else 
			{
				JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs.", "Attention", JOptionPane.INFORMATION_MESSAGE);
			}

		}
		else if(e.getActionCommand().equals(RENEW_SIGNAL))
		{
			
			// Mise à jour de l'affichage
			searchInRenew = true;
			cards.remove(reinscrirePanel);
			reinscrirePanel = reinscrire("","");
			cards.add(RENEW_SIGNAL, reinscrirePanel);			

			((CardLayout) cards.getLayout()).show(cards,RENEW_SIGNAL);
		}
		else if(e.getActionCommand().equals(REINSCRIRE_SIGNAL))
		{
			int clientSelectedRow = clientTable.getSelectedRow();
			if(clientSelectedRow >= 0) // Si ligne sélectionnée
			{

				clientList.get(clientSelectedRow).reinscription(today);

				// MIse à jour de l'affichage
				cards.remove(showClientPanel);
				showClientPanel = showClientList("", "");
				cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
				cards.revalidate();
				cards.repaint();

				System.out.println("Réinscription d'un client.");

				write(); // Sauvegarde

				JOptionPane.showMessageDialog(null, "Renouvellement de l'abonnement.\nCotisation dûe : " + clientList.get(clientSelectedRow).getCotisation(), "Validation du renouvellement", JOptionPane.INFORMATION_MESSAGE);

				((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Veuillez sélectionner une ligne.", "Attention", JOptionPane.INFORMATION_MESSAGE);
			}
		}
	// Emprunt d'un document
		else if(e.getActionCommand().equals(EMPRUNT_SIGNAL))
		{
		searchInEmprunt = true; // Mode emprunt

		// Mise à jour de l'affichage
		cards.remove(empruntPanel);
		empruntPanel = empruntDoc("", "", "", "");
		cards.add(EMPRUNT_SIGNAL, empruntPanel);

		((CardLayout) cards.getLayout()).show(cards,EMPRUNT_SIGNAL); // Page emprunt
	}
	// Choix du document lors de l'emprunt
	else if(e.getActionCommand().equals(CHOIX_DOC_SIGNAL))
	{
		// Récupération de la ligne sélectionnée
		int audioSelectedRow = audioTable.getSelectedRow();
		int livreSelectedRow = livreTable.getSelectedRow();
		int videoSelectedRow = videoTable.getSelectedRow();

		audioCode = "";
		livreCode = "";
		videoCode = "";

		// Audio
		if(audioSelectedRow >= 0) // Si ligne sélectionner
		{
			int j = 0;
			// Parcours de la liste
			for(int i=0; i<docList.size(); i++)
			{
				if(docList.get(i) instanceof Audio)
				{
					// Recherche du document corresponddant
					if(audioSelectedRow == j)
					{

						// Si pas emprunté
						if(!docList.get(i).emprunte)
						{
							//docList.get(i).addEmprunt();
							audioCode = docList.get(i).getCode(); // Récupération du code

							livreCode = "";
							videoCode = "";

							((CardLayout) empruntDoc.getLayout()).show(empruntDoc,CHOIX_CLIENT_SIGNAL);
							break;
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Veuillez choisir un document actuellement disponible.", "Emprunt de document", JOptionPane.INFORMATION_MESSAGE);

						}
					}
					j++;
				}
			}
		}
		// Livre
		else if(livreSelectedRow >= 0) // Si ligne sélectionner
		{
			int j=0;
			// Parcours de la liste
			for(int i=0; i<docList.size(); i++)
			{
				if(docList.get(i) instanceof Livre)
				{
					// Recherche du document corresponddant
					if(livreSelectedRow == j)
					{

						// Si pas emprunté
						if(!docList.get(i).emprunte)
						{
							//docList.get(i).addEmprunt();
							livreCode = docList.get(i).getCode(); // Récupération du code

							audioCode = "";
							videoCode = "";

							((CardLayout) empruntDoc.getLayout()).show(empruntDoc,CHOIX_CLIENT_SIGNAL);
							break;
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Veuillez choisir un document actuellement disponible.", "Emprunt de document", JOptionPane.INFORMATION_MESSAGE);

						}
					}
					j++;
				}
			}
		}
		// Video
		else if(videoSelectedRow >= 0) // Si ligne sélectionner
		{
			int j=0;
			// Parcours de la liste
			for(int i=0; i<docList.size(); i++)
			{
				if(docList.get(i) instanceof Video)
				{
					// Recherche du document corresponddant
					if(videoSelectedRow == j)
					{
						// Si pas emprunté
						if(!docList.get(i).emprunte)
						{
							//docList.get(i).addEmprunt();
							videoCode = docList.get(i).getCode(); // Récupération du code

							livreCode = "";
							audioCode = "";

							((CardLayout) empruntDoc.getLayout()).show(empruntDoc,CHOIX_CLIENT_SIGNAL);
							break;
						}
						else
						{
							JOptionPane.showMessageDialog(null, "Veuillez choisir un document actuellement disponible.", "Emprunt de document", JOptionPane.INFORMATION_MESSAGE);

						}
					}
					j++;
				}
			}

		}
		else 
		{
			JOptionPane.showMessageDialog(null, "Veuillez sélectionnner une ligne.", "Attention", JOptionPane.WARNING_MESSAGE);

		}

	}
	// Création de la fiche
	else if(e.getActionCommand().equals(CREATE_FICHE_SIGNAL))
	{
		int clientSelectedRow = clientTable.getSelectedRow();

		if(clientSelectedRow >= 0) // Si ligne sélectionnée
		{
			// recherche client
			// client.setemprunt
			// remplacement dans la liste
			// recherche doc
			// d.setemprunt
			// remplacement dans la liste
			// création de la fiche emprunt
			// ajout de la fiche dans la liste
			// dialog
			Client c;
			Document d = null;
			// Emprunt max ?
			if(clientList.get(clientSelectedRow).getNbEmpruntsEnCours() < clientList.get(clientSelectedRow).getNbEmpruntsMax())
			{
				clientList.get(clientSelectedRow).addEmprunt(); // Ajout d'emprunt pour le client
				c = clientList.get(clientSelectedRow);

				// Audio
				if(!audioCode.isEmpty())
				{
					getDocFromCode(audioCode).addEmprunt(); // Ajout d'emprunt pour le document
					d = getDocFromCode(audioCode);
					ficheList.add(new FicheEmprunt(c, d)); // Création de la fiche
				}
				// Livre
				else if(!livreCode.isEmpty())
				{
					getDocFromCode(livreCode).addEmprunt(); // Ajout d'emprunt pour le document
					d = getDocFromCode(livreCode);
					ficheList.add(new FicheEmprunt(c, d)); // Création de la fiche
				}
				// Vidéo
				else if(!videoCode.isEmpty())
				{
					getDocFromCode(videoCode).addEmprunt(); // Ajout d'emprunt pour le document
					d = getDocFromCode(videoCode);
					ficheList.add(new FicheEmprunt(c, d)); // Création de la fiche
				}

				// Notification
				JOptionPane.showMessageDialog(null, "Le document " + d + " a été emprunté par " + c + ".", "Emprunt de document", JOptionPane.INFORMATION_MESSAGE);

				System.out.println("Emprunt d'un document.");

				write(); // Sauvegarde

				
				// Mise à jour de l'affichage
				((CardLayout)empruntDoc.getLayout()).show(empruntDoc, CHOIX_DOC_SIGNAL);

				((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "Le nombre d'emprunt maximum a été atteint.", "Attention", JOptionPane.WARNING_MESSAGE);

			}

			
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Veuillez sélectionnner une ligne.", "Attention", JOptionPane.WARNING_MESSAGE);

		}
	}
	// Rendu d'un document
	else if(e.getActionCommand().equals(RENDU_SIGNAL))
	{
		// Mise à jour de l'affichage
		cards.remove(renduPanel);
		renduPanel = renduDoc("", "");
		cards.add(RENDU_SIGNAL, renduPanel);
		cards.revalidate();
		cards.repaint();

		((CardLayout) cards.getLayout()).show(cards,RENDU_SIGNAL); // Page de rendu
	}
	// Suppression d'une fiche
	else if(e.getActionCommand().equals(DEL_EMPRUNT_SIGNAL))
	{
		int ficheSelectedRow = ficheTable.getSelectedRow();

		if(ficheSelectedRow >= 0) // Si sélection d'une ligne
		{

			// Mise à jour des emprunts
			ficheList.get(ficheSelectedRow).getClient(clientList).rmEmprunt();
			ficheList.get(ficheSelectedRow).getDoc(docList).rmEmprunt();
			ficheList.remove(ficheSelectedRow); // Fiche supprimée

			// Notification
			JOptionPane.showMessageDialog(null, "Le document a été rendu.", "Rendu de document", JOptionPane.INFORMATION_MESSAGE);


			System.out.println("Rendu d'un document.");

			write(); // Sauvegarde

			((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
		}
		else
		{
			JOptionPane.showMessageDialog(null, "Veuillez sélectionnner une ligne.", "Attention", JOptionPane.INFORMATION_MESSAGE);

		}
	}
	

}

	// Mise à jour de la médiathèque
public void update()
{
		// On avance d'un jour
	Calendar cal = Calendar.getInstance();
	cal.setTime(today);
	cal.add(Calendar.DATE, 1);
	today = cal.getTime();

		// Mise à jour des fiches et des clients
	updateFiche();
	updateClient();

	write();

	System.out.println("Mise à jour de la médiathèque");

		// Mise à jour de la page de manu
	cards.remove(menuPanel);
	menuPanel = menuScreen();
	cards.add(MENU_SIGNAL, menuPanel);

	cards.revalidate();
	cards.repaint();

		((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); // Page Menu

	}

	// Mise à jour des fiches
	public void updateFiche()
	{
		rappelPanel = new JPanel();
		rappelPanel.setLayout(new BoxLayout(rappelPanel, BoxLayout.Y_AXIS));
		rappelPanel.setBorder(BorderFactory.createTitledBorder("Emprunt"));
		rappelPanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
		rappelPanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));

		for(int i=0; i<ficheList.size(); i++)
		{
			// Mise à jour de la fiche
			if(ficheList.get(i).update(today)) // emprunt depasse
			{
				ficheList.get(i).getClient(clientList).addEmpruntDepasse();
			}

			// Vérifie les rappels
			if(ficheList.get(i).giveRappel(today))
			{
				Document d = ficheList.get(i).getDoc(docList);
				Client c = ficheList.get(i).getClient(clientList);
				rappelPanel.add(new JLabel("- " + d.getTitre() + " de " + d.getAuteur() + " emprunté par " + c.getPrenom() + " " + c.getNom().toUpperCase()));
			}
		}
	}

	// Mise à jour des clients
	public void updateClient()
	{
		renewPanel = new JPanel();
		renewPanel.setLayout(new BoxLayout(renewPanel, BoxLayout.Y_AXIS));
		renewPanel.setBorder(BorderFactory.createTitledBorder("Réinscription"));
		renewPanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
		renewPanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));

		JButton renewButton = new JButton("Renouveler");
		renewButton.setActionCommand(RENEW_SIGNAL);
		renewButton.addActionListener(this);

		renewPanel.add(renewButton);

		deletePanel = new JPanel();
		deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.Y_AXIS));
		deletePanel.setBorder(BorderFactory.createTitledBorder("Suppression"));
		deletePanel.setMinimumSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));
		deletePanel.setPreferredSize(new Dimension(MSG_WIDTH,MSG_HEIGHT));

		int i = 0;
		while(i < clientList.size())
		{
			// Suppression des clients s'ils ne se sont pas réinscrit après 3 mois
			if(clientList.get(i).delete(today))
			{
				deletePanel.add(new JLabel("- " + clientList.get(i).getPrenom() + " " + clientList.get(i).getNom()));
				clientList.remove(i);
			}
			// Envoie des rappels de réinscription
			else if(clientList.get(i).haveToRenew(today))
			{
				renewPanel.add(new JLabel("- " + clientList.get(i).getPrenom() + " " + clientList.get(i).getNom()));
				i++;
			}
			else
			{
				i++;
			}
		}
	}
};


};