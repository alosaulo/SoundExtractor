package View;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Control.MenuManager;
import Control.SilenceInfo;

import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import sun.swing.table.DefaultTableCellHeaderRenderer;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.Panel;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import java.awt.Color;

public class MainMenu {
	
	
	private JFrame frmSoundExtractor;
	private JTextField TxtthresholdInSec;
	private JTextField TxtMaxDB;
	private MenuManager Menu;
	private JTable tblSilence;
	private ArrayList<SilenceInfo> silence;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainMenu window = new MainMenu();
					window.frmSoundExtractor.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainMenu() {
		Menu = new MenuManager();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmSoundExtractor = new JFrame();
		frmSoundExtractor.setResizable(false);
		frmSoundExtractor.setTitle("Sound Extractor");
		frmSoundExtractor.setBounds(100, 100, 529, 423);
		frmSoundExtractor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		TxtthresholdInSec = new JTextField();
		TxtthresholdInSec.setLocation(212, 130);
		TxtthresholdInSec.setSize(125, 25);
		TxtthresholdInSec.setText("1");
		TxtthresholdInSec.setEditable(false);
		TxtthresholdInSec.setColumns(10);
		
		TxtMaxDB = new JTextField();
		TxtMaxDB.setLocation(212, 202);
		TxtMaxDB.setSize(125, 25);
		TxtMaxDB.setText("8000");
		TxtMaxDB.setEditable(false);
		TxtMaxDB.setColumns(10);
		
		JButton btnExportXml = new JButton("Export XML");
		btnExportXml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.CreateXML(silence); 
			}
		});
		btnExportXml.setLocation(388, 238);
		btnExportXml.setSize(125, 25);
		btnExportXml.setEnabled(false);
		
		JButton btnFindSilence = new JButton("Find Silence");
		btnFindSilence.setLocation(212, 238);
		btnFindSilence.setSize(125, 25);
		btnFindSilence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					silence = Menu.CheckSilence(TxtMaxDB.getText(), TxtthresholdInSec.getText());
					PopulateTable(silence);
					btnExportXml.setEnabled(true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					 JOptionPane.showMessageDialog(new JFrame(), "Ocorreu um erro inesperado.", "Dialog",
						        JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnFindSilence.setEnabled(false);
		
		JButton btnExtractAudio = new JButton("Extract Audio");
		btnExtractAudio.setLocation(388, 202);
		btnExtractAudio.setSize(125, 25);
		btnExtractAudio.setEnabled(false);
		btnExtractAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Menu.ExtractAudio();
					btnFindSilence.setEnabled(true);
					TxtMaxDB.setEditable(true);
					TxtthresholdInSec.setEditable(true);
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					 JOptionPane.showMessageDialog(new JFrame(), "Ocorreu um erro inesperado.", "Dialog",
						        JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		frmSoundExtractor.getContentPane().setLayout(null);
		
		JButton btnOpen = new JButton("Open");
		btnOpen.setLocation(388, 166);
		btnOpen.setSize(125, 25);
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Menu.OpenFile();
				btnExtractAudio.setEnabled(true);
			}
		});
		frmSoundExtractor.getContentPane().add(btnOpen);
		
		JLabel lblNewLabel = new JLabel("Secs Threshold");
		lblNewLabel.setLocation(212, 94);
		lblNewLabel.setSize(125, 25);
		frmSoundExtractor.getContentPane().add(lblNewLabel);
		frmSoundExtractor.getContentPane().add(TxtthresholdInSec);
		
		JLabel lblMaxDB = new JLabel("Max dB");
		lblMaxDB.setLocation(212, 166);
		lblMaxDB.setSize(125, 25);
		frmSoundExtractor.getContentPane().add(lblMaxDB);
		frmSoundExtractor.getContentPane().add(TxtMaxDB);
		frmSoundExtractor.getContentPane().add(btnExportXml);
		frmSoundExtractor.getContentPane().add(btnFindSilence);
		frmSoundExtractor.getContentPane().add(btnExtractAudio);
		

		
		tblSilence = new JTable();
		tblSilence.setShowVerticalLines(false);
		tblSilence.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Start", "End", "Duration"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tblSilence.getColumnModel().getColumn(0).setResizable(false);
		tblSilence.getColumnModel().getColumn(1).setResizable(false);
		tblSilence.getColumnModel().getColumn(2).setResizable(false);
		tblSilence.setFillsViewportHeight(true);
		tblSilence.setBounds(10, 11, 190, 372);
		tblSilence.addMouseListener(new MouseAdapter(){
		    public void mouseClicked(MouseEvent evnt) {
		        if (evnt.getClickCount() == 1) {
		        	Menu.container.GetWaveformPanels().get(0).drawTheBeach(silence.get(tblSilence.getSelectedRow()));
		         }
		     }
		});
		
		//frmSoundExtractor.getContentPane().add(tblSilence);
		
		JScrollPane scroll = new JScrollPane();
		scroll.setBounds(10, 11, 190, 372);
		scroll.setViewportView(tblSilence);
		frmSoundExtractor.getContentPane().add(scroll);
		
	}
	
	public void PopulateTable(ArrayList<SilenceInfo> silence){
		DefaultTableModel tableSilence = (DefaultTableModel)tblSilence.getModel();
		
		while(tableSilence.getRowCount() > 0){
			tableSilence.removeRow(0);
		}
		for(SilenceInfo s : silence){
			tableSilence.addRow(new Object[] {s.GetStart(), s.GetEnd(), s.GetDuration()});
		}
	}
}
