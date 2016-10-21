import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;

// javac -Xlint Mediatheque.java

public class Mediatheque
{
	private String nom;

	private ArrayList<Localisation> locList;
	private ArrayList<Genre> genreList;
	private ArrayList<Document> docList;
	private ArrayList<FicheEmprunt> ficheList;
	private ArrayList<Client> clientList;
	private ArrayList<CategorieClient> catList;

	JFrame frame;

	private void test()
	{
		addClient("a");
		addClient("a");
		addClient("b");
		for(Client c : clientList)
		{
			System.out.println(c);
		}
		showClientList();
	}

	public static void main(String[] args)
	{
		new Mediatheque();
	}

	public Mediatheque()
	{
		locList = new ArrayList<Localisation>();
		genreList = new ArrayList<Genre>();
		docList = new ArrayList<Document>();
		ficheList = new ArrayList<FicheEmprunt>();
		clientList = new ArrayList<Client>();
		catList = new ArrayList<CategorieClient>();

		test();
	}	

	public void addClient()
	{
		System.out.println("Ajout d'un nouveau client");
		Client c = new Client(true);
		if(!clientList.contains(c)) // Vérifier que ça fonctionne (atributs) meme sans pointeurs
		{
			clientList.add(c);
		}
		else
		{
			System.err.println("Le client est déjà dans la base de données : ");
			System.err.println(c);
		}
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
			System.err.println("Le client est déjà dans la base de données : ");
			System.err.println(c);
		}
	}

	public void showClientList()
	{
		frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setTitle("Liste des clients");

		JPanel searchPanel = new JPanel(new FlowLayout());
		JTextField searchTextField = new JTextField();
		JButton searchButton = new JButton("OK");
		searchPanel.add(searchTextField);
		searchPanel.add(searchButton);

		// Données du tableau
		Object[][] data = new Object[clientList.size()][];
		for(int i=0; i<clientList.size(); i++)
		{
			data[i] = clientList.get(i).getTable();
		}

		// Titre des colonnes
		String title[] = {"Catégorie", "Nom", "Prénom", "Adresse", "Inscription", "Renouvellement", "Emprunts effectués", "Emprunts dépassés", "Emprunts en cours", "Code de Réduction"};

		JTable table = new JTable(data, title);

		frame.getContentPane().add(new JScrollPane(table));

		frame.setMinimumSize(new Dimension(640,480));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

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
};