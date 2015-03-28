package com.jaredjonas.linecount;

import java.io.File;

public class JavaFile {
	private File file;
	private int numberOfLines;
	private int numberOfChars;
	
	private String name;
	private String fullName;
	
	// Constructors
	
	public JavaFile(File f, int lines, int chr) {
		file = f;
		numberOfLines = lines;
		numberOfChars = chr;
		
		name = f.getName();
		fullName = name;
	}
	
	public JavaFile(File f, int lines, int chr, String pack) {
		this(f, lines, chr);
		fullName = new StringBuilder().append(pack).append(".").append(fullName).toString();
	}
	
	// For data encapsulation
	
	public File getFile() {
		return ( file );
	}
	
	public int getLines() {
		return ( numberOfLines );
	}
	
	public int getCharacters() {
		return ( numberOfChars );
	}
	
	public String getName() {
		return ( name );
	}
	
	public String getFullName() {
		return ( fullName );
	}
}
