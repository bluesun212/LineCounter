package com.jaredjonas.linecount;

import java.util.Comparator;

public class JavaFileComparator implements Comparator<JavaFile> {
	private int sort;
	private boolean order;
	private boolean showPath;
	
	// The constructor
	
	public JavaFileComparator(int sortType, boolean reverse, boolean show) {
		sort = sortType;
		order = reverse;
		showPath = show;
	}

	/**
	 * Compares two java files based on the sorting
	 * type, order, and the name.
	 * 
	 * @param arg0    The first java file
	 * @param arg1    The second java file
	 * @return the numerical comparison of the two files
	 * @see Comparator#compare(Object, Object)
	 */
	@Override
	public int compare(JavaFile arg0, JavaFile arg1) {
		// Check for nulls
		
		if ( arg0 == null && arg1 != null ) {
			return ( adjustOrder(-1) );
		} else if ( arg0 != null && arg1 == null ) {
			return ( adjustOrder(1) );
		} else if ( arg0 == null && arg1 == null ) {
			return ( adjustOrder(0) );
		}
		
		// If sorting by name
		
		if ( sort == 0 ) {
			String s = getName(arg0);
			String s2 = getName(arg1);
			return ( adjustOrder(s.compareToIgnoreCase(s2)) );
		}
		
		// Sorting by lines
		
		int i = arg0.getLines();
		int j = arg1.getLines();
		
		if ( sort == 2 ) {
			i = arg0.getCharacters();
			j = arg1.getCharacters();
		}
		
		if ( i > j ) {
			return ( adjustOrder(1) );
		} else if ( i < j ) {
			return ( adjustOrder(-1) );
		} else {
			return ( adjustOrder(0) );
		}
	}
	
	// Private stuff
	
	private int adjustOrder(int i) {
		if ( order ) {
			return ( -i );
		}
		
		return ( i );
	}
	
	private String getName(JavaFile jf) {
		if ( showPath ) {
			return ( jf.getFullName() );
		}
		
		return ( jf.getName() );
	}
}
