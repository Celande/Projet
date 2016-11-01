import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

//import javax.json.*;

public class Client implements ActionListener
{
	private CategorieClient categorie;
	private String nom;
	private String prenom;
	private String adresse;
	private Date dateInsription;
	private Date dateRenouvellement;
	private int nbEmpruntsEffectues;
	private int nbEmpruntsDepasses;
	private int nbEmpruntsEnCours; // 3 max
	private int codeReduction;

	SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/y");

	JDialog frame;
	JTextField nomTextField;
	JTextField prenomTextField;
	JTextField adresseTextField;
	JComboBox categorieCombo;
	Boolean closeFrame = false;

	public Client()
	{
		this.nbEmpruntsEffectues = 0;
		this.nbEmpruntsDepasses = 0;
		this.nbEmpruntsEnCours = 0;
		this.codeReduction = 0;

		Scanner scan = new Scanner(System.in);
		System.out.print("Nom : ");
		this.nom = new String(scan.next());

		scan.nextLine();
		System.out.print("Prénom : ");
		this.prenom = new String(scan.next());

		scan.nextLine();
		System.out.print("Adresse : ");
		this.adresse = new String(scan.nextLine());

		Calendar calendar = Calendar.getInstance();
		this.dateInsription = new Date();
		this.dateInsription = calendar.getTime();// calendar : date d'aujurd'hui
		this.dateRenouvellement = new Date();
		this.dateRenouvellement = this.dateInsription;

		//scan.nextLine();
		System.out.println("A quelle catégorie appartient le client ?");
		int count = 0;
		for(CategorieClient c : CategorieClient.values()) // chaque element de CategorieClient
		{
			System.out.println(c.ordinal() + ". " + c);
			count ++;
		}

		int choice = -1;
		do
		{
			try
			{
				System.out.print("Entrez le nombre correspondant : ");
				choice = scan.nextInt();
			}
			catch(Exception e)
			{
				System.out.println("Veuillez enter un nombre.");
				scan.nextLine();
				choice = -1;
			}

		}while(choice<0 || choice>count); // Vérifier que choice est entre 0 et max enum

		for(CategorieClient c : CategorieClient.values()) // chaque element de CategorieClient
		{
			if(c.ordinal() == choice)
			{
				this.categorie = c;
				break;
			}
		}

	}

	Client(Boolean lol)
	{
		this.nbEmpruntsEffectues = 0;
		this.nbEmpruntsDepasses = 0;
		this.nbEmpruntsEnCours = 0;
		this.codeReduction = 0;

		//frame = new JDialog("Nouveau client");
		frame = new JDialog();
		frame.setTitle("Nouveau Client");
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
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		frame.getRootPane().setDefaultButton(okButton);

		panel.add(p1);
		panel.add(p2);
		panel.add(p3);
		panel.add(p4);
		panel.add(okButton);

		frame.add(panel);

		frame.setMinimumSize(new Dimension(300, 300));
		frame.pack();
		frame.setModal(true);
		frame.setVisible(true);
	}

	Client(String x) // Generation de client pour les tests
	{
		categorie = CategorieClient.PARTICULIER;
		nom = x;
		prenom = x;
		adresse = x;

		Calendar calendar = Calendar.getInstance();
		this.dateInsription = new Date();
		this.dateInsription = calendar.getTime();
		this.dateRenouvellement = new Date();
		this.dateRenouvellement = this.dateInsription;

		this.nbEmpruntsEffectues = 0;
		this.nbEmpruntsDepasses = 0;
		this.nbEmpruntsEnCours = 0;
		this.codeReduction = 0;
	}

	public void actionPerformed(ActionEvent e) 
	{
		if(e.getActionCommand().equals("ok"))
		{
			this.nom = new String(nomTextField.getText());
			this.prenom = new String(prenomTextField.getText());
			this.adresse = new String(adresseTextField.getText());

			this.categorie = CategorieClient.ETUDIANT;
			this.categorie.setCategorie(categorieCombo.getSelectedItem().toString());

			Calendar calendar = Calendar.getInstance();
			this.dateInsription = new Date();
			this.dateInsription = calendar.getTime();
			this.dateRenouvellement = new Date();
			this.dateRenouvellement = this.dateInsription;

			frame.setVisible(false);
			frame.dispose();
		}
	}

	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode()==KeyEvent.VK_ENTER)
		{
			this.nom = new String(nomTextField.getText());
			this.prenom = new String(prenomTextField.getText());
			this.adresse = new String(adresseTextField.getText());

			this.categorie = CategorieClient.ETUDIANT;
			this.categorie.setCategorie(categorieCombo.getSelectedItem().toString());

			Calendar calendar = Calendar.getInstance();
			this.dateInsription = new Date();
			this.dateInsription = calendar.getTime();
			this.dateRenouvellement = new Date();
			this.dateRenouvellement = this.dateInsription;

			frame.setVisible(false);
			frame.dispose();
		}

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

	public String getNomCategorie()
	{
		return this.categorie.getNom();
	}

	public String toString()
	{
		return "Client : " + nom.toUpperCase() + " " + prenom + "\nAdresse : " + adresse + "\nCategorie : " + categorie.getNom() + "\nDate d'inscription : " + dateFormat.format(dateInsription);
	}

	public Object[] getTable()
	{
		Object[] tab = {categorie, nom, prenom, adresse, dateFormat.format(dateInsription), dateFormat.format(dateRenouvellement), nbEmpruntsEffectues, nbEmpruntsEffectues, nbEmpruntsEnCours, codeReduction};

		return tab;
	}

	public JSONObject write()
	{
		JSONObject obj = new JSONObject();

		try
		{
			obj.put("categorie", categorie.getNom());
			obj.put("nom", nom);
			obj.put("prenom", prenom);
			obj.put("adresse", adresse);
			obj.put("date_inscription", dateFormat.format(dateInsription));
			obj.put("date_renouvellement", dateFormat.format(dateRenouvellement));
			obj.put("nb_effectues", new Integer(nbEmpruntsEffectues));
			obj.put("nb_depasses", new Integer(nbEmpruntsDepasses));
			obj.put("nb_encours", new Integer(nbEmpruntsEnCours));
			obj.put("code", new Integer(codeReduction));

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);

			return obj;

			//String jsonText = out.toString();
			//System.out.println(jsonText);
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		return obj;
	}

	public void read(JSONObject obj)
	{
		JSONParser parser = new JSONParser();

		try 
		{
			/*
			Object obj = parser.parse(new FileReader("c:\\test.json"));

			JSONObject jsonObject = (JSONObject) obj;

			String name = (String) jsonObject.get("name");
			System.out.println(name);

			long age = (Long) jsonObject.get("age");
			System.out.println(age);

			// loop array
			JSONArray msg = (JSONArray) jsonObject.get("messages");
			Iterator<String> iterator = msg.iterator();
			while (iterator.hasNext()) {
				System.out.println(iterator.next());
				

			this.nom = obj.get("nom");
			}*/

			this.nom = (String) obj.get("nom");

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
};