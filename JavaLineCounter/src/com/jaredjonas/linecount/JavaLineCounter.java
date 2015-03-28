package com.jaredjonas.linecount;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class JavaLineCounter implements ActionListener {
	private JFrame window;
	private JProgressBar progressBar;
	private JLabel statusBar;
	private JavaFileTable canvas;
	private JTextPane textPane;
	private JScrollPane scrollPane;
	private JFileChooser chooser;
	
	private List<JavaFile> files = new LinkedList<JavaFile>();
	private File folder;
	
	private JCheckBoxMenuItem chckbxmntmShowPackage;
	private JRadioButtonMenuItem rdbtnmntmSortByName;
	private JRadioButtonMenuItem rdbtnmntmSortByLines;
	private JRadioButtonMenuItem rdbtnmntmSortByCharacters;
	private JCheckBoxMenuItem chckbxmntmSortDescending;
	
	private JMenuItem mntmOpen;
	private JMenuItem mntmReload;
	private JMenuItem mntmChangeExtensions;
	private JCheckBoxMenuItem chckbxmntmIgnoreComments;
	private JCheckBoxMenuItem chckbxmntmIgnoreWhitespace;
	private String extensions = ".java, .cs, .cpp, .c, .py";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Launch the application in a thread
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JavaLineCounter prog = new JavaLineCounter();
					prog.window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JavaLineCounter() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// Set system look and feel
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Get file chooser ready
		
		chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		// Create the window
		
		window = new JFrame();
		window.setResizable(false);
		window.setTitle("Java File Line Counter");
		window.setBounds(100, 100, 640, 300);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().setLayout(null);
		
		// Create the progress bar
		
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setBounds(416, 247, 208, 14);
		window.getContentPane().add(progressBar);
		
		// Create the menu bar
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 634, 21);
		window.getContentPane().add(menuBar);
		
		// Add the file menu
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		// Add buttons to menu
		
		mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(this);
		mnFile.add(mntmOpen);
		
		mntmReload = new JMenuItem("Reload");
		mntmReload.addActionListener(this);
		mnFile.add(mntmReload);
		mnFile.addSeparator();
		
		mntmChangeExtensions = new JMenuItem("Change Extensions");
		mntmChangeExtensions.addActionListener(this);
		mnFile.add(mntmChangeExtensions);
		
		chckbxmntmIgnoreWhitespace = new JCheckBoxMenuItem("Ignore Whitespace");
		mnFile.add(chckbxmntmIgnoreWhitespace);
		
		chckbxmntmIgnoreComments = new JCheckBoxMenuItem("Ignore Comments");
		mnFile.add(chckbxmntmIgnoreComments);
		
		JMenu mnSort = new JMenu("Sort");
		menuBar.add(mnSort);
		
		rdbtnmntmSortByName = new JRadioButtonMenuItem("Sort by name");
		rdbtnmntmSortByName.addActionListener(this);
		mnSort.add(rdbtnmntmSortByName);
		
		rdbtnmntmSortByLines = new JRadioButtonMenuItem("Sort by lines");
		rdbtnmntmSortByLines.addActionListener(this);
		mnSort.add(rdbtnmntmSortByLines);
		
		rdbtnmntmSortByCharacters = new JRadioButtonMenuItem("Sort by characters");
		rdbtnmntmSortByCharacters.addActionListener(this);
		mnSort.add(rdbtnmntmSortByCharacters);
		mnSort.addSeparator();
		
		chckbxmntmSortDescending = new JCheckBoxMenuItem("Sort descending");
		chckbxmntmSortDescending.addActionListener(this);
		mnSort.add(chckbxmntmSortDescending);
		
		JMenu mnOptions = new JMenu("Options");
		menuBar.add(mnOptions);
		
		chckbxmntmShowPackage = new JCheckBoxMenuItem("Show package");
		chckbxmntmShowPackage.addActionListener(this);
		mnOptions.add(chckbxmntmShowPackage);
		
		// Create the status label
		
		statusBar = new JLabel("Created by Jared Jonas 2012");
		statusBar.setForeground(Color.BLACK);
		statusBar.setHorizontalAlignment(SwingConstants.LEFT);
		statusBar.setBounds(10, 247, 396, 14);
		window.getContentPane().add(statusBar);
		
		// Create the scrolling pane with the table
		
		scrollPane = new JScrollPane();
		scrollPane.setIgnoreRepaint(true);
		scrollPane.setBounds(10, 32, 396, 204);
		window.getContentPane().add(scrollPane);
		scrollPane.getVerticalScrollBar().setUnitIncrement(12);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(12);
		
		// Create the custom table
		
		canvas = new JavaFileTable(this);
		canvas.setIgnoreRepaint(true);
		canvas.setBackground(Color.LIGHT_GRAY);
		scrollPane.setViewportView(canvas);
		
		// Create the result textual pane
		
		textPane = new JTextPane();
		textPane.setBackground(SystemColor.control);
		textPane.setEditable(false);
		textPane.setBounds(416, 32, 208, 204);
		window.getContentPane().add(textPane);
	}
	
	/**
	 * Starts a new thread, and loads all of the files
	 * with the extension of .java into the table,
	 * sorting them alphabetically.  It also sets all
	 * of the stuff in the text view.
	 * 
	 * @param f    The directory being loaded
	 * @see #sortTable(int, boolean, boolean)
	 */
	public void loadProgram(final File f) {
		new Thread(new Runnable() { // Create the thread
			public void run() {
				folder = f.getParentFile();
				
				// Start the line counting thread
				
				LineCountingThread thread = new LineCountingThread(f, extensions, chckbxmntmIgnoreWhitespace.getState(), chckbxmntmIgnoreComments.getState());
				
				progressBar.setVisible(true);
				
				while ( !thread.isDone() ) { // Adjust percent
					int p = Math.round(thread.getPercent());
					statusBar.setText("Loading " + p + "% done.");
					progressBar.setValue(p);
				}
				
				// Set files and sort
				
				files = thread.getFiles();
				sortTable(canvas.getSortingType(), canvas.getReversed(), canvas.getShowsFullPath());
				
				// Set the text view
				
				StringBuffer sb = new StringBuffer();
				sb.append("Number of classes: ");
				sb.append(files.size());
				sb.append("\nTotal lines: ");
				sb.append(thread.getTotalLines());
				sb.append("\nTotal characters: ");
				sb.append(thread.getTotalCharacters());
				sb.append("\nAverage lines: ");
				sb.append(thread.getAverageLines());
				sb.append("\nAverage characters: ");
				sb.append(thread.getAverageCharacters());
				textPane.setText(sb.toString());
				
				// Clean up
				
				statusBar.setText("Created by Jared Jonas 2012");
				progressBar.setValue(0);
				progressBar.setVisible(false);
				scrollPane.getHorizontalScrollBar().setValue(0);
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		}).start();
	}
	
	/**
	 * Sorts the table in the GUI, taking the type, order, and name
	 * into consideration.  The type of sorting are as follows:
	 *     0 - Sort by name
	 *     1 - Sort by lines
	 *     2 - Sort by characters
	 * 
	 * This function is threaded and accesses the status label and
	 * the progress bar, which are reset after the thread terminates.
	 * In addition, this function runs the setMenuButtons() function.
	 * 
	 * @param type    The type of sorting
	 * @param reverse    Whether to reverse the order
	 * @param show    Whether to take packages into account
	 * @see #setMenuButtons()
	 */
	public void sortTable(final int type, final boolean reverse, final boolean show) {
		new Thread(new Runnable() { // Create a thread
			public void run() {
				// Duplicate the list to avoid any synchronization errors with the table
				
				List<JavaFile> tempFiles = new LinkedList<JavaFile>();
				for ( JavaFile jf : files ) {
					tempFiles.add(jf);
				}
				
				// Create the sorting thread
				
				JavaFileSortingThread thread = new JavaFileSortingThread(tempFiles, type, reverse, show);;
				progressBar.setVisible(true);
				
				while ( !thread.isDone() ) { // Adjust the percent
					int p = Math.round(thread.getPercent());
					statusBar.setText("Sorting " + p + "% done.");
					progressBar.setValue(p);
				}
				
				// Notify the canvas and set values

				canvas.setSortingType(type);
				canvas.setReversed(reverse);
				canvas.setShowsFullPath(show);
				
				files = tempFiles;
				canvas.loadFiles(files);
				
				// Set the menu items accordingly
				
				setMenuButtons();
				
				// Clean up
				
				statusBar.setText("Created by Jared Jonas 2012");
				progressBar.setValue(0);
				progressBar.setVisible(false);
				
				System.gc();
			}
		}).start();
	}
	
	/**
	 * Finds the width of the scroll pane inside of the
	 * GUI and returns it, optionally taking the vertical
	 * scroll bar width into account.
	 * 
	 * @param b    Whether to include the scroll bar width
	 * @return The width of the scroll pane
	 */
	public int getViewportWidth(boolean b) {
		int w = scrollPane.getWidth() - scrollPane.getInsets().left - scrollPane.getInsets().right;
		
		if ( b ) {
			w -= 17;
		}
		
		return ( w );
	}
	
	/**
	 * Finds the height of the scroll pane inside of the
	 * GUI and returns it.
	 * 
	 * @return The height of the scroll pane
	 */
	public int getViewportHeight() {
		int h = scrollPane.getHeight() - scrollPane.getInsets().top - scrollPane.getInsets().bottom;
		
		return ( h );
	}
	
	/**
	 * Finds the y coordinate of the scroll pane inside of the
	 * GUI and returns it.
	 * 
	 * @return The y position of the scroll pane
	 */
	public int getViewportY() {
		return ( scrollPane.getViewport().getViewPosition().y );
	}
	
	/**
	 * Synchronizes the canvas' sorting options with the options specified in the menu.
	 */
	public void setMenuButtons() {
		chckbxmntmShowPackage.setSelected(canvas.getShowsFullPath());
		chckbxmntmSortDescending.setSelected(canvas.getReversed());
		
		switch ( canvas.getSortingType() ) {
		case 0: 
			rdbtnmntmSortByName.setSelected(true);
			rdbtnmntmSortByLines.setSelected(false);
			rdbtnmntmSortByCharacters.setSelected(false);
			break;
		case 1: 
			rdbtnmntmSortByName.setSelected(false);
			rdbtnmntmSortByLines.setSelected(true);
			rdbtnmntmSortByCharacters.setSelected(false);
			break;
		case 2: 
			rdbtnmntmSortByName.setSelected(false);
			rdbtnmntmSortByLines.setSelected(false);
			rdbtnmntmSortByCharacters.setSelected(true);
			break;
		}
	}

	/**
	 *  @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if ( o.equals(chckbxmntmShowPackage) ) { // Options - Show package
			sortTable(canvas.getSortingType(), canvas.getReversed(), !canvas.getShowsFullPath());
		} else if ( o.equals(chckbxmntmSortDescending) ) { // Sort - Sort descending
			sortTable(canvas.getSortingType(), !canvas.getReversed(), canvas.getShowsFullPath());
		} else if ( o.equals(rdbtnmntmSortByName) ) { // Sort - Sort by name
			sortTable(0, canvas.getReversed(), canvas.getShowsFullPath());
		} else if ( o.equals(rdbtnmntmSortByLines) ) { // Sort - Sort by lines
			sortTable(1, canvas.getReversed(), canvas.getShowsFullPath());
		} else if ( o.equals(rdbtnmntmSortByCharacters) ) { // Sort - Sort by characters
			sortTable(2, canvas.getReversed(), canvas.getShowsFullPath());
		} else if ( o.equals(mntmOpen) ) { // File - Open
			chooser.setCurrentDirectory(folder); // Open up dialog
			int ret = chooser.showOpenDialog(window);
			
			if ( ret == JFileChooser.APPROVE_OPTION ) { // If everything is OK
				File f = chooser.getSelectedFile();
				
				if ( f.exists() && f.isDirectory() ) {
					loadProgram(f); // Load it!
				}
			}
		} else if ( o.equals(mntmReload) ) { // File - Reload
			File f = chooser.getSelectedFile();
			
			if ( f != null && f.exists() && f.isDirectory() ) {
				loadProgram(f); // Load it!
			}
		} else if ( o.equals(mntmChangeExtensions) ) { // File - Reload
			Object ret = JOptionPane.showInputDialog(window, "Current extensions: ", "Title", JOptionPane.PLAIN_MESSAGE, null, null, extensions);
			
			if ( ret != null ) {
				extensions = (String) ret;
			}
		}
	}
}
