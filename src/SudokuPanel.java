
import java.awt.Color;
import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

public class SudokuPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
        	g.setColor(Color.red);
            super.paintComponent(g);
            g.setColor(Color.BLACK);

            int xOffset = 50;
            int yOffset = 50;
            int letOffset = 25;
        
        	for(int j = 0; j<=sBoard[0].length; j++){
          		if(j%3==0){
          			((Graphics2D) g).setStroke(new BasicStroke(5));
          		}
          		else{
          			((Graphics2D) g).setStroke(new BasicStroke(1));
          			g.setColor(Color.BLACK);
          		}
        	   	g.drawLine(0, j * xOffset, (sBoard.length) * xOffset, j * xOffset);
    		}
        	
          	for(int j = 0; j<=sBoard[0].length; j++){
          		if(j%3==0){
          			((Graphics2D) g).setStroke(new BasicStroke(5));
          		}
          		else{
          			((Graphics2D) g).setStroke(new BasicStroke(1));
          			g.setColor(Color.BLACK);
          		}
        	   	g.drawLine( j * xOffset, 0, j * xOffset, (sBoard.length) * xOffset);
    		}
        
         	g.setFont(new Font("TimesRoman", Font.PLAIN, 24)); 
          	
        	for(int i = 0; i<sBoard.length; i++){
        		for(int j = 0; j<sBoard[i].length; j++){
        			//System.out.println("drawing");
        			
        			g.drawString(sBoard[i][j],(j+1)*yOffset-30,(1+i)*xOffset-20);
        		}
        	}
        }
        
        String[][] sBoard;
        SudokuPanel(int w, int h) {
        	this.setPreferredSize(new Dimension(500,500));
        	this.setMinimumSize(new Dimension(500,500));
        	sBoard = new String[w][h];
        	for(int i = 0; i<sBoard.length; i++){
        		for(int j = 0; j<sBoard[i].length; j++){
        			sBoard[i][j] = new String(" ");
        		}
        	}
        }

        public void displayBoard(int[][] in){
        	for(int i = 0; i<sBoard.length; i++){
        		for(int j = 0; j<sBoard[i].length; j++){
        			sBoard[i][j] = in[i][j] + "";
        		}
        	}
        	this.repaint();
        }
    }