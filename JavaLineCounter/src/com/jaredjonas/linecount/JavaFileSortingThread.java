package com.jaredjonas.linecount;

import java.util.List;
import java.util.PriorityQueue;

public class JavaFileSortingThread implements Runnable {
	private int sort;
	private boolean order;
	private boolean showPath;
	private List<JavaFile> files;
	
	private boolean done = false;
	private float percent = 0;
	
	public JavaFileSortingThread(List<JavaFile> list, int sortType, boolean reverse, boolean show) {
		files = list;
		sort = sortType;
		order = reverse;
		showPath = show;
		
		// Create the thread
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * Sorts the list of files given from the constructor.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		int size = files.size();
		
		// Run elements through the comparator
		
		JavaFileComparator comparator = new JavaFileComparator(sort, order, showPath);
		PriorityQueue<JavaFile> queue = new PriorityQueue<JavaFile>(10, comparator);
		
		for ( int i = 0 ; i < files.size() ; i++ ) {
	    	queue.add(files.get(i));
	    	percent = i / size * 50f;
	    }
		
		// Add them back into the list
		
		files.clear();
		
		while ( queue.size() > 0 ) {
	    	files.add(queue.remove());
	    	percent = 50f + ((size - queue.size()) / size * 50f);
	    }
		
		// Done
		
		done = true;
	}
	
	/**
	 * @return Whether the thread is done
	 */
	public boolean isDone() {
		return ( done );
	}
	
	/**
	 * @return The percent the thread has completed
	 */
	public float getPercent() {
		return ( percent );
	}
}
