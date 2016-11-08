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

/*
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
*/
/*
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import java.io.StringWriter;
import javax.json.JsonWriter;
*/

import javax.json.*;
import javax.json.stream.*;

// JSON MEMORY : http://docs.oracle.com/javaee/7/tutorial/jsonp003.htm#BABHAHIA
// AND NEXT PAGE

// javac -Xlint Mediatheque.java

// javac -cp ".:./resource/lib/json-simple-1.1.1.jar" -d ./build ./source/Mediatheque.java
// java -cp ".:./resource/lib/json-simple-1.1.1.jar" ./build/Mediatheque

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
		//addClient();
		//showClientList();
		
		addClient("a");
		addClient("a");
		addClient("b");

		for(Client c : clientList)
		{
			if(!catList.contains(c.getCat()))
			{
				catList.add(c.getCat());
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
			/*
			if(!locList.contains(d.getLoc()))
			{
				locList.add(d.getLoc());
			}
			*/

			if(!genreList.contains(d.getGenre()))
			{
				genreList.add(d.getGenre());
			}
		}

		ficheList.add(new FicheEmprunt(clientList.get(0), docList.get(0))); // vérifié si le client et le document existe
		ficheList.add(new FicheEmprunt(clientList.get(1), docList.get(4)));
		
		/*
		for(Client c : clientList)
		{
			System.out.println(c.write());
		}
		*/
		write();
		//read();
		//showClientList();
	}

	public static void main(String[] args)
	{
		new Mediatheque();
	}

	public Mediatheque()
	{
		nom = new String("Machin");

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

	public void write()
	{
		try
		{
			StringWriter stWriter;
			JsonWriter jsonWriter;

			FileWriter file = new FileWriter("./resource/memory/" + this.nom + ".json");
			file.write("");

			stWriter = new StringWriter();

				file.write(this.nom);
				System.out.println("Successfully Copied JSON Object to File...");
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

				String jsonData = stWriter.toString();
				System.out.println(jsonData);

				file.write(jsonData);
				System.out.println("Successfully Copied JSON Object to File...");
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

					String jsonData = stWriter.toString();
					System.out.println(jsonData);

					file.write(jsonData);
					System.out.println("Successfully Copied JSON Object to File...");
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

					String jsonData = stWriter.toString();
					System.out.println(jsonData);

					file.write(jsonData);
					System.out.println("Successfully Copied JSON Object to File...");
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

					String jsonData = stWriter.toString();
					System.out.println(jsonData);

					file.write(jsonData);
					System.out.println("Successfully Copied JSON Object to File...");
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

				String jsonData = stWriter.toString();
				System.out.println(jsonData);

				file.write(jsonData);
				System.out.println("Successfully Copied JSON Object to File...");
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

				String jsonData = stWriter.toString();
				System.out.println(jsonData);

				file.write(jsonData);
				System.out.println("Successfully Copied JSON Object to File...");
				file.write(System.getProperty( "line.separator" ));
			}

			file.flush();
				file.close();
			//System.out.println("\nJSONString Object: " + obj.toJSONString());
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}
	}

	public void read()
	{
		//JSONParser parser = new JSONParser();

		try {

			/*
			Object obj = parser.parse(new FileReader("./resource/memory/" + "Machin.json"));

			JSONObject jsonObject = (JSONObject) obj;

			this.nom = (String) jsonObject.get("nom");
			System.out.println(nom);

			// loop array
			clientList.clear();
			JSONArray clients = (JSONArray) jsonObject.get("clients");
			Iterator<String> iterator = clients.iterator();
			while (iterator.hasNext()) 
			{
				//clientList.add(Client.read(iterator.next()));
				System.out.println(iterator.next());
			}
			*/

			clientList.clear();

			BufferedReader reader = new BufferedReader(new FileReader("./resource/memory/Machin.json"));

			JsonParser parser;

			String jsonData = new String();

			Client c;

			// Nom
			if(jsonData = reader.readLine() != null)
			{
				this.nom = jsonData;
			}
			else
			{
				// error
			}

			// Localisation
			do
			{
				jsonData = reader.readLine();
			}while(!jsonData.equals("Localisation :"));

			while(jsonData)
			// DOcument
			// Audio
			// Livre
			// Video
			// Client
			// Fiche Emprunt

			while ((jsonData = reader.readLine()) != null)
			{
				//System.out.println("Line = " + jsonData);
				parser = Json.createParser(new StringReader(jsonData));
				c = new Client();
				c.read(parser);
				clientList.add(c);
				System.out.println(c);
				/*
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
				         System.out.println(event.toString());
				         break;
				      case KEY_NAME:
				         System.out.print(event.toString() + " " +
				                          parser.getString() + " - ");
				         break;
				      case VALUE_STRING:
				      case VALUE_NUMBER:
				         System.out.println(event.toString() + " " +
				                            parser.getString());
				         break;
				   }
				}
				*/
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
};