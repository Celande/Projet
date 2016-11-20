import javax.swing.*;
import java.awt.*;
import java.lang.*;
import java.io.*;
import java.awt.event.*;
import java.text.*;
import javax.swing.table.*;

/* Modèle de table personnalisé */
// Des cases cochées ou non à la place des Booléen
// Plus rapide au vu du nombre de tableaux

class CustomTableModel extends AbstractTableModel
{
  private Object[][] data;
  private String[] titre;

  public CustomTableModel(Object[][] data, String[] titre)
  {
    this.data = data;
    this.titre = titre;
  }
  public int getColumnCount() 
  {
    return this.titre.length;
  }
  public int getRowCount() 
  {
    return this.data.length;
  }
  public Object getValueAt(int ligne, int colonne) 
  {
    return this.data[ligne][colonne];
  }  

  public String getColumnName(int colonne) 
  {
    return this.titre[colonne];
  }
  public Class getColumnClass(int colonne)
  {
    return this.data[0][colonne].getClass();
  }   
  public boolean isCellEditable(int ligne, int colonne)
  {
    if(getValueAt(0, colonne) instanceof JButton)
      return false;

    return true; 
  }
};