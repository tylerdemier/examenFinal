package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import astronomy.Constellation;
import database.DBException;
import database.DBManager;

/**
 * Esta clase contiene la ventana principal de la aplicación.
 *
 */
public class AstronomicViewer extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private DBManager dbManager; // gestor de base de datos
	
	private ConstellationListModel constellationListModel; // modelo del JList de constelaciones
	private JList<Constellation> constellationJList; // JList de constelaciones
	private ConstellationInfoPanel constellationInfoPanel; // panel de información
	private JLabel totalStarsInfo; // label con información del número de estrellas totales
	
	private JPanel panelContenedorTabla = new JPanel();
	private TablaMax tm;
	
	

	public AstronomicViewer() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(720, 480);
		setTitle("Visor astron�mico");
		
		addWindowListener(this);
		
		try {
			dbManager = new DBManager();
			dbManager.open();
			tm = new TablaMax(dbManager);
			panelContenedorTabla.setVisible(true);
			panelContenedorTabla.setBorder(new LineBorder(Color.LIGHT_GRAY));
		} catch (DBException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		// se prepara la UI
		prepareMenuBar();
		prepareConstellationList();
		prepareMainPanel();
		
		// se carga la lista de constelaciones
		loadConstellations();
		
		
		setVisible(true);
	}

	private void prepareMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		mainPanel.add(panelContenedorTabla);
		
		constellationInfoPanel = new ConstellationInfoPanel();
		mainPanel.add(constellationInfoPanel, BorderLayout.NORTH);
		
		add(mainPanel, BorderLayout.CENTER);
	}

	private void prepareMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu fileJMenu = new JMenu("Fichero");
		menuBar.add(fileJMenu);
		
		JMenuItem exitItem = new JMenuItem("Salir");
		fileJMenu.add(exitItem);
		
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		
	}
	
	private void exit() {
		try {
			dbManager.close();
			System.exit(0);
		} catch (DBException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void prepareConstellationList() {
		constellationListModel = new ConstellationListModel();
		constellationJList = new JList<Constellation>(constellationListModel);
		constellationJList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
		JScrollPane constellationScrollPane = new JScrollPane(constellationJList);
		
		JPanel constellationPanel = new JPanel();
		constellationPanel.setLayout(new BorderLayout());
		JPanel centeringPanel = new JPanel(new GridBagLayout());
		centeringPanel.add(new JLabel("Constelaciones"));
		constellationPanel.add(centeringPanel, BorderLayout.NORTH);
		constellationPanel.add(constellationScrollPane, BorderLayout.CENTER);
		
		JPanel totalStarsPanel = new JPanel();
		JLabel totalStarsLabel = new JLabel("Estrellas: ");
		totalStarsInfo = new JLabel();
		totalStarsPanel.add(totalStarsLabel);
		totalStarsPanel.add(totalStarsInfo);
		
		constellationPanel.add(totalStarsPanel, BorderLayout.SOUTH);
		
		add(constellationPanel, BorderLayout.WEST);
		
		constellationJList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				updateUI();
			}
		});
	}
	
	private void updateUI() {
		if (constellationJList.getSelectedIndex() != -1) {
			Constellation constellation = constellationJList.getSelectedValue();
			
			JPanel panel = new JPanel();
			panel = tm.panelTabla(constellation.getName());
			panelContenedorTabla.removeAll();
			panelContenedorTabla.add(panel);
			repaint();
			validate();
			
			
			constellationInfoPanel.setConstellation(constellation);
		} else {
			constellationInfoPanel.clear();
		}
	}
	
	private void loadConstellations() {
		try {
			int total = 0;
			
			for (Constellation constellation : dbManager.getConstellations()) {
				constellationListModel.addElement(constellation);
				total += constellation.getStars();
			}
			
			totalStarsInfo.setText(String.valueOf(total));
		} catch (DBException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				new AstronomicViewer();
			}
		
		});
	}
	
	@Override
	public void windowClosing(WindowEvent arg0) {
		exit();		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}
