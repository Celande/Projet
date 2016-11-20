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

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.*;
import java.util.regex.Pattern;


import javax.json.*;
import javax.json.stream.*;

public class Mediatheque implements ActionListener
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

	/* GUI */
	JFrame frame;
	JXTable clientTable;
	JTextField searchClientTextField;
	JTextField searchDocTextField;
	TableRowSorter<TableModel> sorter;
	JPanel cards;
	JPanel showClientPanel;
	JPanel showDocPanel;
	JPanel createPanel;
	JPanel choicePanel;
	JPanel locDocPanel;
	JPanel genreDocPanel;
	JPanel rappelPanel;
	JPanel renewPanel;
	JPanel menuPanel;

	/* Ajout du client */
	JTextField nomTextField;
	JTextField prenomTextField;
	JTextField adresseTextField;
	JComboBox categorieCombo;

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

	/* Taille de la fenêtre */
	static final int FRAME_WIDTH = 640;
	static final int FRAME_HEIGHT = 480;

	/* Nombre de clients par catégorie */
	int[] nbClient;

	public SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");

	public Mediatheque()
	{
		nom = new String("Machin");
		Calendar cal = Calendar.getInstance();
		today = new Date();
		today = cal.getTime();

		locList = new ArrayList<Localisation>();
		genreList = new ArrayList<Genre>();
		docList = new ArrayList<Document>();
		ficheList = new ArrayList<FicheEmprunt>();
		clientList = new ArrayList<Client>();
		catList = new ArrayList<CategorieClient>();

		writeTest();
		test();
	}	

	private void writeTest()
	{
		addClient("a");
		addClient("a");
		addClient("b");

		for(Client c : clientList)
		{
			if(!catList.contains(c.getCategorie()))
			{
				catList.add(c.getCategorie());
			}
		}		
		
		locList.add(new Localisation("s1", "r1"));
		locList.add(new Localisation("s1", "r2"));
		locList.add(new Localisation("s1", "r3"));
		locList.add(new Localisation("s2", "r1"));
		locList.add(new Localisation("s2", "r2"));
		locList.add(new Localisation("s2", "r3"));
		locList.add(new Localisation("s1", "r1"));

		

		// vérifie que la localisation existe
		docList.add(new Audio("a1", "a1", "a1", "2016", new Localisation("s1", "r1"), Genre.FANTASTIQUE, "a1"));
		docList.add(new Audio("a2", "a2", "a2", "2016", new Localisation("s1", "r1"), Genre.FANTASTIQUE, "a2"));
		docList.add(new Livre("l1", "l1", "l1", "2016", new Localisation("s1", "r3"), Genre.HORREUR, 1));
		docList.add(new Livre("l2", "l2", "l2", "2016", new Localisation("s2", "r1"), Genre.HORREUR, 2));
		docList.add(new Video("v1", "v1", "v1", "2016", new Localisation("s2", "r2"), Genre.JEUNESSE, 1, "v1"));
		docList.add(new Video("v2", "v2", "v2", "2016", new Localisation("s2", "r3"), Genre.JEUNESSE, 2, "v2"));

		for(Document d : docList)
		{
			if(!genreList.contains(d.getGenre()))
			{
				genreList.add(d.getGenre());
			}
		}

		ficheList.add(new FicheEmprunt(clientList.get(0), docList.get(0))); // vérifié si le client et le document existe
		ficheList.add(new FicheEmprunt(clientList.get(1), docList.get(4)));

		write();
	}

	private void test()
	{			
		
		// Initialisation de la mediathèque
		initMediatheque("Machin");

		// Initialisation de la frame
		frame = new JFrame();
		frame.setLayout(new BorderLayout());

		// Permet de changer de page sans modifier la fenêtre
		cards = new JPanel();
		cards.setLayout(new CardLayout());

		// Initialisation des pages
		showClientPanel = showClientList("");
		cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
		showDocPanel = showDocList("");
		cards.add(SHOW_DOC_SIGNAL, showDocPanel);
		menuPanel = startScreen();
		cards.add(MENU_SIGNAL, menuPanel);
		choicePanel = choiceDocList();
		cards.add(CHOICE_DOC_SIGNAL, choicePanel);
		locDocPanel = locDocList();
		cards.add(LOC_DOC_SIGNAL, locDocPanel);
		genreDocPanel = genreDocList();
		cards.add(GENRE_DOC_SIGNAL, genreDocPanel);

		cards.revalidate();
		cards.repaint();

		frame.add(cards, BorderLayout.CENTER);

		((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL); 

		frame.setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		
	}

	public static void main(String[] args)
	{
		new Mediatheque();
	}

	

	public void initMediatheque(String nom)
	{
		// Lecture de la mémoire
		read(nom);

		// Initialisation des genres
		initGenreList();

		// Initialisation de nbClient
		int i = 0;
		for(CategorieClient c : CategorieClient.values()) // nombre total de catégories
		{
			i++;
		}
		nbClient = new int[i];
		for(Client c : clientList)
		{
			nbClient[c.getCategorie().ordinal()] = nbClient[c.getCategorie().ordinal()] + 1;
		}

	}

	// Menu
	public JPanel startScreen() // Ajouter des icones aux boutons
	{
		// Page du menu
		JPanel startPanel = new JPanel();
		startPanel.setLayout(new BoxLayout(startPanel, BoxLayout.Y_AXIS));

		JPanel introPanel = new JPanel();
		introPanel.setLayout(new FlowLayout());
		introPanel.add(new JLabel("Bienvenue à " + this.nom));
		introPanel.add(new JLabel(dateFormat.format(today)));

		JButton updateButton = new JButton("Jour suivant");
		updateButton.setActionCommand(UPDATE_SIGNAL);
		updateButton.addActionListener(this);
		introPanel.add(updateButton);

		
		JPanel addPanel = new JPanel(new FlowLayout());

		JButton addClientButton = new JButton("Ajouter un client");
		addClientButton.setActionCommand(ADD_CLIENT_SIGNAL);
		addClientButton.addActionListener(this);
		addPanel.add(addClientButton);

		JButton addDocButton = new JButton("Ajouter un Document");
		addDocButton.setActionCommand("add_doc");
		addDocButton.addActionListener(this);
		addPanel.add(addDocButton);

		JPanel empPanel = new JPanel(new FlowLayout());

		JButton empruntButton = new JButton("Emprunter un document");
		empruntButton.setActionCommand("emprunt");
		empruntButton.addActionListener(this);
		empPanel.add(empruntButton);

		JButton rendButton = new JButton("Rendre un document");
		rendButton.setActionCommand("rendu");
		rendButton.addActionListener(this);
		empPanel.add(rendButton);

		JPanel showPanel = new JPanel(new FlowLayout());

		JButton showClientButton = new JButton("Consulter la liste des clients");
		showClientButton.setActionCommand(SHOW_CLIENT_SIGNAL);
		showClientButton.addActionListener(this);
		showPanel.add(showClientButton);

		JButton showDocButton = new JButton("Consulter la liste des documents");
		showDocButton.setActionCommand(CHOICE_DOC_SIGNAL);
		showDocButton.addActionListener(this);
		showPanel.add(showDocButton);

		startPanel.add(introPanel);
		startPanel.add(addPanel);
		startPanel.add(empPanel);
		startPanel.add(showPanel);

		// refresh showclient

		cards.remove(showClientPanel);
		showClientPanel = showClientList("");
		cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
		cards.revalidate();
		cards.repaint();



		return startPanel;
	}

	// Ajout d'un client dans la liste via GUI
	public void addClient()
	{
		System.out.println("Ajout d'un nouveau client");
		createPanel = createClient();
		cards.add(CREATE_CLIENT_SIGNAL, createPanel);
		((CardLayout) cards.getLayout()).show(cards,CREATE_CLIENT_SIGNAL);
	}

	public void addClient(String x) // Ajout de clients pour les tests
	{
		System.out.println("Ajout d'un nouveau client");
		Client c = new Client(x);
		if(!alreadyInClientList(c)) // Vérifier que ça fonctionne (atributs) meme sans pointeurs
		{
			clientList.add(c);
		}
		else
		{
			System.err.println("Le client " + c.getNom().toUpperCase() + " " + c.getPrenom() + " est déjà dans la base de données.");
			//System.err.println(c);
		}
	}

	// Page de création d'un client
	public JPanel createClient()
	{

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		JLabel labNom = new JLabel("Nom : ");
		nomTextField = new JTextField(20);
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
		p1.add(labNom);
		p1.add(nomTextField);
		
		JLabel labPrenom = new JLabel("Prenom : ");
		prenomTextField = new JTextField(20);
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
		p2.add(labPrenom);
		p2.add(prenomTextField);

		JLabel labAdresse = new JLabel("Adresse : ");
		adresseTextField = new JTextField(20);
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3, BoxLayout.LINE_AXIS));
		p3.add(labAdresse);
		p3.add(adresseTextField);

		JLabel labCombo = new JLabel("Catégorie : ");
		categorieCombo = new JComboBox();
		categorieCombo.setPreferredSize(new Dimension(100, 20));
		for(CategorieClient c : CategorieClient.values())
		{
			categorieCombo.addItem(c.getNom());
		}
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4, BoxLayout.LINE_AXIS));
		p4.add(labCombo);
		p4.add(categorieCombo);

		JButton okButton = new JButton("OK");
		okButton.setActionCommand(CREATE_CLIENT_SIGNAL);
		okButton.addActionListener(this);
		frame.getRootPane().setDefaultButton(okButton);

		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		panel.add(okButton);

		return panel;
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

	// Initialisation de genreList
	public void initGenreList()
	{
		// Ajout des genres
		for(Document d : docList)
		{
			addGenre(d.getGenre());
		}

		// Comptabilisation du nombre d'emprunts
		Genre g;
		// On ajoute le nombre d'emprunt pour chaque document appartenant au genre
		for(int i = 0; i<genreList.size(); i++)
		{
			for(Document d : docList)
			{
				if(d.getGenre().ordinal() == genreList.get(i).ordinal())
				{
					g = genreList.get(i);
					g.addEmprunts(d.getNbEmprunts());
					genreList.set(i, g);
				}
			}
		}
	}


	// Page de la liste des clients en fonction d'une recherche (String)
	public JPanel showClientList(String text)
	{
		JPanel showClientPanel = new JPanel();
		showClientPanel.setLayout(new BorderLayout());
		showClientPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		/** Recherche **/
		JPanel searchPanel = new JPanel(new FlowLayout());
		searchClientTextField = new JTextField(20);
		JButton searchButton = new JButton("OK");
		searchButton.setActionCommand(SEARCH_CLIENT_SIGNAL);
		searchButton.addActionListener(this);
		searchPanel.add(searchClientTextField);
		searchPanel.add(searchButton);

		JButton menuButton = new JButton("Menu");
		menuButton.setActionCommand(MENU_SIGNAL);
		menuButton.addActionListener(this);
		searchPanel.add(menuButton);

		frame.getRootPane().setDefaultButton(searchButton);

		/** Tableau **/

		// Données du tableau
		Object[][] data = new Object[clientList.size()][];
		for(int i=0; i<clientList.size(); i++)
		{
			data[i] = clientList.get(i).getTable();
		}

		// Titre des colonnes
		String title[] = {"Catégorie", "Nom", "Prénom", "Adresse", "Inscription", "Renouvellement", "Emprunts effectués", "Emprunts dépassés", "Emprunts en cours", "Code de Réduction"};

		clientTable = new JXTable(new CustomTableModel(data, title));
		if(!text.isEmpty()) // Si on a fait une recherche
		{
			Filter[] filterArray = { new PatternFilter("(.*" + text + ".*)", 0, 1), new PatternFilter("(.*" + text + ".*)", 0, 2), new PatternFilter("(.*" + text + ".*)", 0, 3) };
			FilterPipeline filters = new FilterPipeline(filterArray);

			clientTable.setFilters(filters);
		}

		showClientPanel.add(searchPanel, BorderLayout.NORTH);
		showClientPanel.add(new JScrollPane(clientTable), BorderLayout.CENTER);

		return showClientPanel;
	}

	// Page de choix de l'affichage des documents
	public JPanel choiceDocList()
	{
		JPanel choicePanel = new JPanel();
		choicePanel.setLayout(new BorderLayout());

		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		JButton locButton = new JButton("Par localisation");
		locButton.setActionCommand(LOC_DOC_SIGNAL);
		locButton.addActionListener(this);

		JButton genreButton = new JButton("Par genre");
		genreButton.setActionCommand(GENRE_DOC_SIGNAL);
		genreButton.addActionListener(this);

		JButton allButton = new JButton("Tous");
		allButton.setActionCommand(SHOW_DOC_SIGNAL);
		allButton.addActionListener(this);

		p.add(locButton);
		p.add(genreButton);
		p.add(allButton);

		choicePanel.add(p, BorderLayout.CENTER);

		return choicePanel;
	}

	public void updateDocListPanel()
	{

	}

	// Affichage des documents par localisation
	public JPanel locDocList()
	{
		JPanel locDocPanel = new JPanel();
		locDocPanel.setLayout(new BorderLayout());
		locDocPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

		JPanel searchPanel = new JPanel(new FlowLayout());

		JButton menuButton = new JButton("Menu");
		menuButton.setActionCommand(MENU_SIGNAL);
		menuButton.addActionListener(this);
		searchPanel.add(menuButton);

		locDocPanel.add(searchPanel, BorderLayout.PAGE_START);

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
		String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film", "Mentions légales"};
		JXTable audioTable;
		JXTable livreTable;
		JXTable videoTable;

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
				rayPanel.add(new JLabel(r));

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
			sallePanel.add(new JLabel(s));
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

		JPanel searchPanel = new JPanel(new FlowLayout());

		JButton menuButton = new JButton("Menu");
		menuButton.setActionCommand(MENU_SIGNAL);
		menuButton.addActionListener(this);
		searchPanel.add(menuButton);

		genreDocPanel.add(searchPanel, BorderLayout.PAGE_START);

		JPanel genreAllPanel = new JPanel();
		genreAllPanel.setLayout(new BoxLayout(genreAllPanel, BoxLayout.Y_AXIS));

		JPanel genrePanel;

		ArrayList<JPanel> genrePanelList = new ArrayList<JPanel>();

		/** Tableau **/
		// Un tableau pour chaque genre
		Object[][] data;
		String audioTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Classification"};
		String livreTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Nombre de Pages"};
		String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film", "Mentions légales"};
		JXTable audioTable;
		JXTable livreTable;
		JXTable videoTable;

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
			genrePanel.add(new JLabel(g.getNom()));

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
		public JPanel showDocList(String text)
		{
			JPanel showDocPanel = new JPanel();
			showDocPanel.setLayout(new BorderLayout());
			showDocPanel.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));

			/** Recherche **/
			JPanel searchPanel = new JPanel(new FlowLayout());
			searchDocTextField = new JTextField(20);
			JButton searchButton = new JButton("OK");
			searchButton.setActionCommand(SEARCH_DOC_SIGNAL);
			searchButton.addActionListener(this);
			searchPanel.add(searchDocTextField);
			searchPanel.add(searchButton);

			JButton menuButton = new JButton("Menu");
			menuButton.setActionCommand(MENU_SIGNAL);
			menuButton.addActionListener(this);
			searchPanel.add(menuButton);

			//frame.getRootPane().setDefaultButton(searchButton);

			Object[][] data;
			String audioTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Classification"};
			String livreTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Nombre de Pages"};
			String videoTitle[] = {"Code", "Titre", "Auteur", "Année", "Emprunte", "Nombre d'emprunts", "Genre", "Durée du film", "Mentions légales"};
			JXTable audioTable = new JXTable();
			JXTable livreTable = new JXTable();
			JXTable videoTable = new JXTable();

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

		if(!text.isEmpty()) // Si on a fait une recherche
		{
			// Tableau des filtres
			Filter[] audioFilterArray = { new PatternFilter("(.*" + text + ".*)", 0, 1), new PatternFilter("(.*" + text + ".*)", 0, 2), new PatternFilter("(.*" + text + ".*)", 0, 3) };
			Filter[] livreFilterArray = { new PatternFilter("(.*" + text + ".*)", 0, 1), new PatternFilter("(.*" + text + ".*)", 0, 2), new PatternFilter("(.*" + text + ".*)", 0, 3) };
			Filter[] videoFilterArray = { new PatternFilter("(.*" + text + ".*)", 0, 1), new PatternFilter("(.*" + text + ".*)", 0, 2), new PatternFilter("(.*" + text + ".*)", 0, 3) };

			FilterPipeline audioFilters = new FilterPipeline(audioFilterArray);
			FilterPipeline livreFilters = new FilterPipeline(livreFilterArray);
			FilterPipeline videoFilters = new FilterPipeline(videoFilterArray);

			// On filtre les tables si elles ne sont pas vides
			if(!audioList.isEmpty())
			{
				audioTable.setFilters(audioFilters);
			}
			if(!livreList.isEmpty())
			{
				livreTable.setFilters(livreFilters);
			}
			if(!videoList.isEmpty())
			{
				videoTable.setFilters(videoFilters);
			}
		}
	

	showDocPanel.add(searchPanel, BorderLayout.NORTH);
	showDocPanel.add(new JScrollPane(showDocListPanel), BorderLayout.CENTER);

	return showDocPanel;
}

	// Actions pour tous les boutons
public void actionPerformed(ActionEvent e) 
{
		if(e.getActionCommand().equals(MENU_SIGNAL)) // Menu
		{
			//startScreen();
			((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
		}
		else if(e.getActionCommand().equals(UPDATE_SIGNAL)) // Mise à jour de la médiathèque
		{
			update();
		}
		else if(e.getActionCommand().equals(SEARCH_CLIENT_SIGNAL)) // Recherche de client
		{
			
			String text = firstUpperCase(searchClientTextField.getText());

			// Mise à jour de l'affichage de la liste des clients en fonction de la recherche
			cards.remove(showClientPanel);
			showClientPanel = showClientList(text);
			cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
			cards.revalidate();
			cards.repaint();
			((CardLayout) cards.getLayout()).show(cards,SHOW_CLIENT_SIGNAL);
		}
		else if(e.getActionCommand().equals(SEARCH_DOC_SIGNAL)) // Recherche de document
		{
			
			String text = firstUpperCase(searchDocTextField.getText());

			// Mise à jour de l'affichage de la liste des clients en fonction de la recherche
			cards.remove(showDocPanel);
			showDocPanel = showDocList(text);
			cards.add(SHOW_DOC_SIGNAL, showDocPanel);
			cards.revalidate();
			cards.repaint();
			((CardLayout) cards.getLayout()).show(cards,SHOW_DOC_SIGNAL);
		}
		else if(e.getActionCommand().equals(ADD_CLIENT_SIGNAL)) // Ajout d'un client
		{
			addClient();
		}
		else if(e.getActionCommand().equals(SHOW_CLIENT_SIGNAL)) // Affichage des clients
		{
			((CardLayout) cards.getLayout()).show(cards,SHOW_CLIENT_SIGNAL);
		}
		else if(e.getActionCommand().equals(SHOW_DOC_SIGNAL)) // Affichage des clients
		{
			((CardLayout) cards.getLayout()).show(cards,SHOW_DOC_SIGNAL);
		}
		else if(e.getActionCommand().equals(CHOICE_DOC_SIGNAL)) // Page du choix de l'affichage des documents
		{
			((CardLayout) cards.getLayout()).show(cards,CHOICE_DOC_SIGNAL);
		}
		else if(e.getActionCommand().equals(LOC_DOC_SIGNAL)) // Affichage des documents par localisation
		{
			((CardLayout) cards.getLayout()).show(cards,LOC_DOC_SIGNAL);
		}
		else if(e.getActionCommand().equals(GENRE_DOC_SIGNAL)) // Affichage des documents par genre
		{
			((CardLayout) cards.getLayout()).show(cards,GENRE_DOC_SIGNAL);
		}
		else if(e.getActionCommand().equals(CREATE_CLIENT_SIGNAL)) // Création d'un client
		{
			if(!nomTextField.getText().isEmpty() && !prenomTextField.getText().isEmpty() && !adresseTextField.getText().isEmpty())
			{

				CategorieClient cat = CategorieClient.ETUDIANT;
				cat = cat.setCategorie(categorieCombo.getSelectedItem().toString());

				Client c = new Client(cat, new String(nomTextField.getText()), new String(prenomTextField.getText()), new String(adresseTextField.getText()), nbClient);
				if(!alreadyInClientList(c)) // Vérifier que ça fonctionne (atributs) meme sans pointeurs
				{
					clientList.add(c);

					// Mise à jour de l'affichage de la liste des clients
					cards.remove(showClientPanel);
					showClientPanel = showClientList("");
					cards.add(SHOW_CLIENT_SIGNAL, showClientPanel);
					cards.remove(createPanel);
					cards.revalidate();
					cards.repaint();

					// Sauvegarde
					write();

					((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);
				}
				else
				{
					// error
					System.err.println("Le client est déjà dans la base de données.");
					//System.err.println(c);
				}
			}
			else 
			{
				//error
				//remplir tous les champs
				//System.out.println("Vous devez remplir tous les champs");
			}

		}
	}

	
/*
	//@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			String text = searchTextField.getText();
			if (text.trim().length() == 0) 
			{
				sorter.setRowFilter(null);
			} 
			else 
			{
				sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, 1, 2, 3));
			}
		}

	}
	*/


	// Vérifie si le client n'est pas déjà dans la liste des clients
	public Boolean alreadyInClientList(Client c)
	{
		for(Client x : clientList)
		{
			if(x.getNomCategorie().equals(c.getNomCategorie()) && x.getNom().equals(c.getNom()) && x.getPrenom().equals(c.getPrenom()) && x.getAdresse().equals(c.getAdresse()))
			{
				return true;
			}
		}

		return false;
	}

	// Lecture de la mémoire de la médiathèque
	public void write()
	{
		try
		{
			// Effacement du fichier
			PrintWriter writer = new PrintWriter("./resource/memory/" + this.nom + ".json");
			writer.print("");
			writer.close();


			StringWriter stWriter;
			JsonWriter jsonWriter;

			String jsonData = new String();

			// Ouverture du fichier correspondant à la médiathèque
			FileWriter file = new FileWriter("./resource/memory/" + this.nom + ".json");

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
			file.write(System.getProperty("line.separator"));
			file.write(System.getProperty("line.separator"));

			// Localasition
			file.write("Localisation :");
			file.write(System.getProperty("line.separator"));
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
			System.out.println("write - fin");
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	// Lecture de la médiathèque en fonction de son nom
	public void read(String nom)
	{

		try {

			// Nettoyage des listes
			locList.clear();
			docList.clear();
			clientList.clear();
			ficheList.clear();
			genreList.clear();


			// Ouverture du fichier
			BufferedReader reader = new BufferedReader(new FileReader("./resource/memory/" + nom + ".json"));

			JsonParser parser;

			String jsonData = new String();

			// Pas de fichier
			if(reader.equals("")) // a tester
			{
				// error
				System.err.println("Fichier vide");
				System.exit(-1);
			}

			// Nom et jour
			if((jsonData = reader.readLine()) != null)
			{
				parser = Json.createParser(new StringReader(jsonData));
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
			else
			{
				// error
				//System.out.println("Mediatheque - read - 5");
			}
			jsonData = reader.readLine();

			jsonData = reader.readLine();

			// Localisation
			if(jsonData.equals("Localisation :"))
			{
				jsonData = reader.readLine();

				Localisation l;
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
				// error
				System.err.println("Erreur read - Localisation");
				System.exit(-1);
			}

			// Document
			jsonData = reader.readLine();

			// Audio
			jsonData = reader.readLine();
			if(jsonData.equals("Audio :"))
			{
				jsonData = reader.readLine();
				Audio a;
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
				// error
			}
			
			// Livre
			if(jsonData.equals("Livre :"))
			{
				jsonData = reader.readLine();
				Livre l;
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
				// error
			}

			// Video
			if(jsonData.equals("Video :"))
			{
				jsonData = reader.readLine();
				Video v;
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
				// error
			}

			// Client
			jsonData = reader.readLine();
			if(jsonData.equals("Client :"))
			{
				jsonData = reader.readLine();
				Client c;
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
				// error
			}

			// Fiche Emprunt
			jsonData = reader.readLine();
			if(jsonData.equals("Fiche Emprunt :"))
			{
				jsonData = reader.readLine();
				FicheEmprunt f;
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
				// error
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	// Mose à jour de la médiathèque
	public void update()
	{
		// On avance d'un jour
		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, 1);
		today = cal.getTime();

		updateFiche();
		updateClient();

		// Mise à jour de la page de manu
		cards.remove(menuPanel);
		menuPanel = startScreen();
		cards.add(MENU_SIGNAL, menuPanel);
		((CardLayout) cards.getLayout()).show(cards,MENU_SIGNAL);

	}

	// Mise à jour des fiches
	public void updateFiche()
	{
		rappelPanel = new JPanel();
		rappelPanel.setLayout(new BoxLayout(rappelPanel, BoxLayout.Y_AXIS));
		for(int i=0; i<ficheList.size(); i++)
		{
			// Mise à jour de la fiche
			if(ficheList.get(i).update()) // emprunt depasse
			{
				ficheList.get(i).getClient(clientList).addEmpruntDepasse();
			}

			// Vérifie les rappels
			if(ficheList.get(i).giveRappel())
			{
				Document d = ficheList.get(i).getDoc(docList);
				Client c = ficheList.get(i).getClient(clientList);
				rappelPanel.add(new JLabel(d.getTitre() + " de " + d.getAuteur() + " emprunté par " + c.getPrenom() + " " + c.getNom().toUpperCase()));
			}
		}
	}

	// Mise à jour des clients
	public void updateClient()
	{
		renewPanel = new JPanel();
		renewPanel.setLayout(new BoxLayout(renewPanel, BoxLayout.Y_AXIS));
		int i = 0;
		while(i < clientList.size())
		{
			// Suppression des clients s'ils ne se sont pas réinscrit après 3 mois
			if(clientList.get(i).delete())
			{
				clientList.remove(i);
			}
			// Envoie des rappels de réinscription
			else if(clientList.get(i).haveToRenew())
			{
				renewPanel.add(new JLabel(clientList.get(i).getPrenom() + " " + clientList.get(i).getNom()));
				i++;
			}
			else
			{
				i++;
			}
		}
	}

	// Première lettre majuscule, reste minuscule
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
	
};