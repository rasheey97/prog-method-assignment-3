

/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;
	
	private static final double GRAVITY = 3.0;
	
	private static final int DELAY = 50;
	
	private RandomGenerator rgen = RandomGenerator.getInstance(); 
	private double vx, vy;
	private double vySTART = +3.0;
	private double vxSTART = 0.0;
	

	
	private GRect paddle;
	
	private GOval ball;
	

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		addActionListeners();
		JButton but = new JButton("Score");
		add(but, SOUTH);
		for(int i=0; i < NTURNS; i++) {
			buildGame();
			playGame();
			if(brickCounter == 0) {
				ball.setVisible(false);
				printWinner();
				break;
			}
			if(brickCounter > 0) {
				removeAll();
			}
		}
		if(brickCounter > 0) {
			printGameOver();
		}
	}
	
	private void buildGame() {
		
		setSize(WIDTH, HEIGHT);
		buildBricks(0, BRICK_Y_OFFSET);
		buildPaddle();
		buildBall();
	}
	//drawing all the bricks necessary for the game
	private void buildBricks(double ax, double ay) {
		Color color= null;
		/*need to have several columns in each row
		 * so there need to be two 'for' loops, 
		 * one 'for' loop for the rows and one 'for' loop for the columns.
		 */ 
		for (int row = 0; row <NBRICK_ROWS; row++) {
		for (int column =0; column < NBRICKS_PER_ROW; column++){
			/* To get the y coordinate of the starting height:
			 * 	start at the given length from the top for the first row,
			 * 	then add a brick height and a brick separation for each of the following rows
			 */
			
	double x = ax + (BRICK_WIDTH + BRICK_SEP) * row;
	/* To get the x coordinate for the starting width:
	 * 	start at the center width, 
	 * 	subtract half of the bricks (width) in the row,  
	 * 	subtract half of the separations (width) between the bricks in the row,
	 * now you're at where the first brick should be, 
	 * so for the starting point of the next bricks in the column, you need to: 
	 * 	add a brick width 
	 * 	add a separation width
	 */
	double y = ay + (BRICK_HEIGHT + BRICK_SEP) * column;
		
	GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
	brick.setFilled(true);
	add(brick);
			if(column<2){
				brick.setColor(Color.RED);
			} else if (column <=3) {
				brick.setColor(Color.ORANGE);
			} else if (column<=5){
				brick.setColor(Color.YELLOW);
			} else if (column<=7){
				brick.setColor(Color.GREEN);
			} else {
				brick.setColor(Color.CYAN);
			}
				
			
		}
		}
			
	}

	//paddle set-up
	private void buildPaddle() {
		setSize(WIDTH, HEIGHT);
		//starting the paddle in the middle of the screen
		double x = (WIDTH -PADDLE_WIDTH)/2;
		//the paddle height stays consistent throughout the game
		//need to make sure to subtract the PADDLE_HEIGHT, 
		//since the rectangle gets drawn from the top left corner
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		add(paddle);
		addMouseListeners();
		
	}
	//making the mouse track the paddle
	public void mouseMoved(MouseEvent e){
		/* The mouse tracks the middle point of the paddle. 
		 * If the middle point of the paddle is between half paddle width of the screen
		 * and half a paddle width before the end of the screen, 
		 * the x location of the paddle is set at where the mouse is minus half a paddle's width, 
		 * and the height remains the same
		 */
		
		double y = (getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT);
		if (e.getX() >= 0 && e.getX() < (getWidth() - PADDLE_WIDTH)){
		paddle.setLocation(e.getX(), y);
		} else if (e.getX() > WIDTH) {
			paddle.setLocation(WIDTH, y);
		}
		
		
	}
	
	//ball set-up
	private void buildBall() {
		ball = new GOval(getWidth()/2 - BALL_RADIUS, getHeight()/2 - BALL_RADIUS, 10, 10);
		ball.setFilled(true);
		add(ball);
	}
	
	
	private void playGame() {
		waitForClick();
		getBallVelocity();
		while (true) {
			moveBall();
			if (ball.getY() >= getHeight()) {
				break;
			}
			if (brickCounter == 0) {
				break;
			}
		}
	
			
	}
	
	private void getBallVelocity () {
		vy = 5.0;
		vx = rgen.nextDouble(1.0, 3.0);     
		if (rgen.nextBoolean(0.5)) {
			vx = -vx;
		}
		
	}

	private void moveBall() {
		ball.move(vx, vy);
		//check for walls
		//need to get vx and vy at the point closest to 0 or the other edge
		
		if ((ball.getX() -vx <= 0 && vx < 0) || (ball.getX() + vx >= (getWidth() - BALL_RADIUS))) {
			vx = -vx;
		}
		//We don't need to check for the bottom wall, since the ball can fall through the wall at that point
		
		if ((ball.getY() - vy <= 0 && vy < 0 )) {
			vy = -vy;
		}
		
		//check for other objects
		GObject collider = getCollidingObject();
		if (collider == paddle) {
			
			/* We need to make sure that the ball only bounces off the top part of the paddle  
			 * and also that it doesn't "stick" to it if different sides of the ball hit the paddle quickly and get the ball "stuck" on the paddle.
			 * I ran "println ("vx: " + vx + ", vy: " + vy + ", ballX: " + ball.getX() + ", ballY: " +ball.getY());"
			 * and found that the ball.getY() changes by 4 every time, instead of 1,
			 * so it never reaches exactly the the height at which the ball hits the paddle (paddle height + ball height), 
			 * therefore, I estimate the point to be greater or equal to the height at which the ball hits the paddle, 
			 * but less than the height where the ball hits the paddle minus 4. 
			 */
			
			if (ball.getY() >= getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT - BALL_RADIUS*2 && ball.getY() < getHeight() - PADDLE_HEIGHT) {
				vy = -vy;
			}
		}
		
		//since we lay down a row of bricks, the last brick in the brick wall is assigned the value brick.
		//so we narrow it down by saying that the collier does not equal to a paddle or null, 
		//so all that is left is the brick
		else if (collider != null) {
			remove(collider);
			brickCounter--;
			vy = -vy;
			
		}
		pause(DELAY);
		
		
		
	}
	
	private GObject getCollidingObject() {
		if((getElementAt(ball.getX(), ball.getY())) != null) {
	         return getElementAt(ball.getX(), ball.getY());
	      }
		else if (getElementAt( (ball.getX() + BALL_RADIUS*2), ball.getY()) != null ){
	         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY());
	      }
		else if(getElementAt(ball.getX(), (ball.getY() + BALL_RADIUS*2)) != null ){
	         return getElementAt(ball.getX(), ball.getY() + BALL_RADIUS*2);
	      }
		else if(getElementAt((ball.getX() + BALL_RADIUS*2), (ball.getY() + BALL_RADIUS*2)) != null ){
	         return getElementAt(ball.getX() + BALL_RADIUS*2, ball.getY() + BALL_RADIUS*2);
	      }
		
		else{
			return null;
		}
	}
	
	private void printGameOver() {
		GLabel gameOver = new GLabel ("Game Over", getWidth()/2, getHeight()/2);
		gameOver.move(-gameOver.getWidth()/2, -gameOver.getHeight());
		gameOver.setColor(Color.RED);
		add (gameOver);
	}
	
	private int brickCounter = 100;
	
	private void printWinner() {
		GLabel Winner = new GLabel ("Winner!!", getWidth()/2, getHeight()/2);
		Winner.move(-Winner.getWidth()/2 +vx, -Winner.getHeight());
		Winner.setColor(Color.RED);
		add (Winner);
	}
	
	

}
