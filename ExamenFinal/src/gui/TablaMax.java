package gui;

import java.awt.Dimension; 
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import astronomy.Constellation;
import astronomy.Star;
import database.DBException;
import database.DBManager;

public class TablaMax{


	private static HashMap<String, ArrayList<Star>> hashConstelaciones = new HashMap<>();


	public TablaMax(DBManager db) {

		try {

			List<Constellation> listaConstelaciones = db.getConstellations();
			List<ArrayList<Star>> estrellas = new ArrayList<ArrayList<Star>>();

			for (Constellation constellation : listaConstelaciones) {
				List<Star> estrellasPorConstelacion = db.getStars(constellation);
				estrellas.add((ArrayList<Star>) estrellasPorConstelacion);
			}

			for (ArrayList<Star> linkedList : estrellas) {
				for (Star estrella : linkedList) {

					if(hashConstelaciones.containsKey(estrella.getConstellation().getName())) {
						ArrayList<Star> array = new ArrayList<>();
						array = hashConstelaciones.get(estrella.getConstellation().getName());
						array.add(estrella);
						hashConstelaciones.replace(estrella.getConstellation().getName(), array);
					} else {
						ArrayList<Star> nuevo = new ArrayList<>();
						nuevo.add(estrella);
						hashConstelaciones.put(estrella.getConstellation().getName(), nuevo);
					}

				}
			}

		} catch (DBException e) {
			e.printStackTrace();
		}

	}


	public JPanel panelTabla(String c) {

		JPanel panel = new JPanel();

		if(hashConstelaciones.containsKey(c)) {

			DefaultTableModel modelo = new DefaultTableModel(new Object[] { "CONSTELACION", "NOMBRE", "RA", "DEC",
					"DISTANCIA", "MAGNITUD", "LUMINOSIDAD", "TIPO_ESPECTRAL" }, 0);
			JTable tabla = new JTable(modelo);
			JScrollPane scroll = new JScrollPane(tabla);
			
			for (Star estrella : hashConstelaciones.get(c)) {
				Object[] coso = {estrella.getConstellation(), estrella.getName(), estrella.getRa(), estrella.getDec(), estrella.getDec(), 
				estrella.getDistance(), estrella.getMagnitude(), estrella.getLuminosity(), estrella.getSpectralType() };

				modelo.addRow(coso);
			}
			
			panel.add(scroll);
			panel.setVisible(true);
		} 

		return panel;

	}

}
