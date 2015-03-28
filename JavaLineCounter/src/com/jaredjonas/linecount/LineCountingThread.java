package com.jaredjonas.linecount;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LineCountingThread implements Runnable {
	private File directory;
	private boolean whitespace;
	private boolean comments;
	private ArrayList<String> extensions;
	
	private float percent = 0f;
	private boolean done = false;
	
	private List<JavaFile> javaFiles = new LinkedList<JavaFile>();
	private int totalLines = 0;
	private int totalChars = 0;
	private int avgLines = 0;
	private int avgChars = 0;
	
	// Constructor
	
	public LineCountingThread(File f, String exts, boolean ws, boolean cm) {
		directory = f;
		whitespace = ws;
		comments = cm;
		
		extensions = new ArrayList<String>();
		for ( String s : exts.replace(" ","").split(",") ) {
			extensions.add(s.toLowerCase());
		}
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Creates a list of JavaFiles from the given directory
	 * specified in the constructor, and tallies the statistics.
	 * 
	 * @see JavaFile
	 * @see Runnable#run()
	 */
	public void run() {
		List<File> files = getAllJavaFilesInSubdirectories(directory);
		
		if ( files == null ) {
			done = true;
			percent = 0;
			
			return;
		}
		
		int size = files.size();
		int num = 0;
		
		// Add all files to list and calculate values
			
		if ( files != null ) {
			for ( File f2 : files ) {
				JavaFile jf = getJavaFile(f2);
				
				if ( jf != null ) {
					javaFiles.add(jf);
					
					totalLines += jf.getLines();
					totalChars += jf.getCharacters();
				}
				
				// Update percent
				
				num++;
				percent = Math.min((float)num / size * 100f, 100f);
			}
		}
		
		// Calculate averages
		
		avgLines = Math.round((float)totalLines / size);
		avgChars = Math.round((float)totalChars / size);
		
		// Close up
		
		done = true;
		percent = 0;
	}
	
	// User functions
	
	/**
	 * @return Whether the thread is done.
	 */
	public boolean isDone() {
		return ( done );
	}
	
	/**
	 * @return The percent completed
	 */
	public float getPercent() {
		return ( percent );
	}
	
	/**
	 * @return The total amount of lines counted
	 */
	public int getTotalLines() {
		return ( totalLines );
	}
	
	/**
	 * @return The total amount of characters counted
	 */
	public int getTotalCharacters() {
		return ( totalChars );
	}
	
	/**
	 * @return The average amount of lines per class
	 */
	public int getAverageLines() {
		return ( avgLines );
	}
	
	/**
	 * @return The average amount of characters per class
	 */
	public int getAverageCharacters() {
		return ( avgChars );
	}
	
	/**
	 * @return The list of files it has generated
	 */
	public List<JavaFile> getFiles() {
		return ( javaFiles );
	}
	
	// Utility functions
	
	private JavaFile getJavaFile(File f) {
		BufferedReader br = null;
		FileReader fr = null;
		
		try {
			int lines = 0;
			int chr = 0;
			String pack = null;
			boolean isInComment = false;
			
			fr = new FileReader(f);
			br = new BufferedReader(fr);
			
			// Go over all of the lines
			
			boolean eof = false;
			while ( !eof ) {
				String line = br.readLine();
						
				if ( line != null ) {
					// Get the package name
					
					if ( whitespace ) {
						line = line.trim();
					}
					
					if ( pack == null || comments ) {
						boolean singleQuote = false;
						boolean doubleQuote = false;
						char c2 = 0;
						
						StringBuilder sb = new StringBuilder();
						for ( int i = 0; i < line.length(); i++ ) {
							char c = line.charAt(i);
							boolean skip = false;
							
							if ( !doubleQuote && c == '\'' && c2 != '\\' ) {
								singleQuote = !singleQuote;
							}
							
							if ( !singleQuote && c == '"' && c2 != '\\' ) {
								doubleQuote = !doubleQuote;
							}
							
							if ( c == '/' ) {
								if ( !singleQuote && !doubleQuote ) {
									if ( i < line.length() - 1 && line.charAt(i + 1) == '/' ) {
										break; // End of line comment
									}
									
									if ( i < line.length() - 1 && line.charAt(i + 1) == '*' ) {
										isInComment = true;
									}
										
									if ( c2 == '*' ) {
										isInComment = false;
										skip = true;
									}
								}
							}
							
							c2 = c;
							if ( !isInComment && !skip ) {
								sb.append(c);
							}
						}
						
						if ( comments ) {
							line = sb.toString();
						}
						
						if ( pack == null ) {
							String newLine = sb.toString();
							
							if ( newLine.trim().startsWith("package ") ) {
								pack = line.substring(line.indexOf(" ") + 1, line.indexOf(";"));
							}
						}
					}
					
					// Increment stats
					
					int len = line.length();
					if ( len > 0 || !whitespace ) {
						chr += len;
						lines++;
					}
				} else {
					eof = true;
				}
			}
			
			// Close and return
			
			fr.close();
			br.close();
			if ( pack != null ) {
				return ( new JavaFile(f, lines, chr, pack) );
			} else {
				return ( new JavaFile(f, lines, chr) );
			}
		} catch ( IOException e ) { // Error
			return ( null );
		} finally {
			// Clean up if there are any problems
			
			if ( fr != null ) {
				try {
					fr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if ( br != null ) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private List<File> getAllJavaFilesInSubdirectories(File root) {
		// Handle any bad states
		
		if ( root == null ) {
			throw new NullPointerException();
		}
		
		if ( !root.isDirectory() ) {
			return ( null );
		}
		
		// Setup lists
		
		LinkedList<File> files = new LinkedList<File>();
		File[] fileArray = root.listFiles();
		
		// Iterate through current directory
		
		for ( File f : fileArray ) {
			if ( f.isDirectory() ) { // Iterate through new directory
				List<File> tempList = getAllJavaFilesInSubdirectories(f);
				
				for ( File f2 : tempList ) { // Add returned files to list
					if ( hasExt(f2) ) {
						files.add(f2);
					}
				}
			} else {
				if ( hasExt(f) ) {
					files.add(f); // Add file to list regularly
				}
			}
		}
		
		return ( files );
	}
	
	private boolean hasExt(File f) {
		if ( f == null || f.isDirectory() ) {
			return ( false ); // For files that have no extension
		}
		
		String name = f.getName();
		String ext = "";
		if ( name.contains(".") ) {
			ext = name.substring(name.lastIndexOf(".")).toLowerCase();
		}
				
		return ( extensions.contains(ext) );
	}
}