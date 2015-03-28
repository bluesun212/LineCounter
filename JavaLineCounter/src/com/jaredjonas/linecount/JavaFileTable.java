package com.jaredjonas.linecount;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JPanel;

public class JavaFileTable extends JPanel implements MouseListener {
	private static final long serialVersionUID = -1475511772038352868L;
	private static List<JavaFile> list = null;
	
	private boolean order = false;
	private int sort = 0;
	private boolean showPath = false;
	
	private final Font FONT = new Font("Tahoma", Font.PLAIN, 11);
	private final Color HEADING_COLOR = new Color(224, 224, 224);
	
	private int nameWidth = 232;
	private int height = getFM().getHeight();
	
	private JavaLineCounter parent;
	
	public JavaFileTable(JavaLineCounter jlk) {
		super();
		
		parent = jlk;
		addMouseListener(this);
		setFont(FONT);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if ( list == null || list.isEmpty() ) {
			return;
		}
		
		// Draw background
		
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getScreenWidth() + 1, getScreenHeight() + 1);
		g.setColor(Color.BLACK);
		
		// Draw entries
		
		int min = Math.max(0, parent.getViewportY() / height - 1);
		int max = Math.min(getListSize() + 1, (parent.getViewportY() + parent.getViewportHeight()) / height + 1);
		
		for ( int i = min; i < max; i++ ) {
			int y = ((i + 1) * height);
			g.drawLine(0, y, getScreenWidth(), y); // Horizontal
			
			if ( i == 0 ) { // Draw column titles
				g.setColor(HEADING_COLOR);
				g.fillRect(0, 0, getScreenWidth() + 1, height);
				g.setColor(Color.BLACK);
				
				String[] titles = {"File name", "Lines", "Characters"};
				int[] widths = {nameWidth, 80, 80};
				titles[sort] += " ";
				int w = 0;
				
				for ( int j = 0; j < titles.length; j++ ) {
					int x = w + (widths[j] / 2);
					int cw = getFM().stringWidth(titles[j]) / 2;
					
					g.drawString(titles[j], x - cw, height - 3);
					w += widths[j];
					
					if ( j == sort ) { // arrows
						for ( int k = 0; k < 4; k++ ) {
							if ( order ) {
								g.drawLine(x + cw + k, height - 8 + k, x + cw + (7 - k), height - 8 + k);
							} else {
								g.drawLine(x + cw + k, height - 6 - k, x + cw + (7 - k), height - 6 - k);
							}
						}
					}
				}
			} else { // Draw the data
				JavaFile jf = list.get(i - 1);
				String name = getName(jf);
				
				g.drawString(name, 3, y - 3);
				g.drawString("" + jf.getLines(), nameWidth + 3, y - 3);
				g.drawString("" + jf.getCharacters(), nameWidth + 80 + 3, y - 3);
			}
		}
		
		// Draw vertical lines
		
		g.drawLine(nameWidth, parent.getViewportY(), nameWidth, getScreenHeight());
		g.drawLine(nameWidth + 80, parent.getViewportY(), nameWidth + 80, getScreenHeight());
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// Exit if empty
		
		if ( list == null || list.isEmpty() ) {
			return;
		}
		
		// Check for top line clicks
		
		if ( arg0.getY() < height ) {
			int[] widths = {nameWidth, 80, 80};
			
			// Find the clicked column
			
			int w = 0;
			for ( int i = 0; i < widths.length; i++ ) {
				if ( arg0.getX() >= w && arg0.getX() <= w + widths[i] ) {
					boolean reverse = false;
					if ( sort == i ) {
						reverse = !order;
					}
					
					parent.sortTable(i, reverse, showPath);
					break;
				}
				
				w += widths[i];
			}
		}
	}
	
	// User functions
	
	public void loadFiles(List<JavaFile> files) {
		list = files;
		
		if ( files == null || files.isEmpty() ) {
			return;
		}
		
		nameWidth = parent.getViewportWidth(getScreenHeight() > parent.getViewportHeight()) - 160;
		
		for ( int i = 0; i < getListSize(); i++ ) {
			String name = getName(files.get(i));
			int w = getFM().stringWidth(name) + 6;
			
			if ( w > nameWidth ) {
				nameWidth = w;
			}
		}
		
		Dimension d = new Dimension(getScreenWidth(), getScreenHeight());
		setPreferredSize(d);
		revalidate();
		repaint();
	}
	
	public void setReversed(boolean b) {
		order = b;
	}
	
	public void setSortingType(int i) {
		sort = i;
	}
	
	public void setShowsFullPath(boolean b) {
		showPath = b;
	}
	
	public boolean getReversed() {
		return ( order );
	}
	
	public int getSortingType() {
		return ( sort );
	}
	
	public boolean getShowsFullPath() {
		return ( showPath );
	}
	
	public int getListSize() {
		return ( list.size() );
	}
	
	public int getScreenWidth() {
		return ( nameWidth + 160 );
	}
	
	public int getScreenHeight() {
		return ( height * (getListSize() + 1) );
	}
	
	private FontMetrics getFM() {
		return ( getFontMetrics(FONT) ); 
	}
	
	private String getName(JavaFile jf) {
		if ( showPath ) {
			return ( jf.getFullName() );
		}
		
		return ( jf.getName() );
	}
	
	// Unused methods

	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
	public void mousePressed(MouseEvent arg0) { }
	public void mouseReleased(MouseEvent arg0) { }
}
