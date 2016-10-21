import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.table.*;

// javac -Xlint Mediatheque.java

public class Mediatheque implements ActionListener
{
	private String nom;

	private ArrayList<Localisation> locList;
	private ArrayList<Genre> genreList;
	private ArrayList<Document> docList;
	private ArrayList<FicheEmprunt> ficheList;
	private ArrayList<Client> clientList;
	private ArrayList<CategorieClient> catList;

	JFrame frame;
	JTable table;
	JTextField searchTextField;
	TableRowSorter<TableModel> sorter;

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

		/** Recherche **/
		JPanel searchPanel = new JPanel(new FlowLayout());
		searchTextField = new JTextField(20);
		JButton searchButton = new JButton("OK");
		searchButton.setActionCommand("search");
		searchButton.addActionListener(this);
		searchPanel.add(searchTextField);
		searchPanel.add(searchButton);

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

		//table = new JTable(data, title);
		DefaultTableModel tableModel = new DefaultTableModel(data, title);
		table = new JTable(tableModel);
		sorter = new TableRowSorter<TableModel>(table.getModel());
		table.setRowSorter(sorter);

		List<RowSorter.SortKey> sortKeys = new ArrayList<>(25); // Pourquoi 25 ?
		for(int i=0; i<title.length; i++)
		{
			sortKeys.add(new RowSorter.SortKey(i, SortOrder.ASCENDING));
		}
		sorter.setSortKeys(sortKeys);

		frame.add(searchPanel, BorderLayout.NORTH);
		frame.getContentPane().add(new JScrollPane(table), BorderLayout.CENTER);

		frame.setMinimumSize(new Dimension(640,480));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("search"))
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