/*
 * Author: Hadar Tiferet
 * File: GameOfLifeController.java
 */

/*
 *  Note: I've made another version of this which uses a class of squares to store 
 *  the information about each individual square in the matrix, but since the assignment
 *  specifies to define the matrix within the controller class - I'm sending this solution instead
 *  I can send the second solution as well if I misunderstood the instruction above
 */

import java.security.SecureRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

//The class controls the creation and maintenance of a game of life simulation based on button clicks
public class GameOfLifeController {
	
	// random number generator
	static final SecureRandom randomNumbers = new SecureRandom();
	
	int squaresPerRow = 10; // defined number of squares per row in the matrix
	int squaresPerColumn = 10; // defined number of squares per column in the matrix
	int generationCount = 0; // the current generation in the simulation
	
	// arrays to manage the life squares from within the controller class, as instructed
	boolean[][] isAlive = new boolean[squaresPerRow][squaresPerColumn];
	int[][] adjacentSquares= new int[squaresPerRow][squaresPerColumn];
	
	
    @FXML
    private Text textBox;

    @FXML
    private Canvas canvas;

    // method for resetting the simulation without reopening the program
    @FXML
    void ResetSimulationButtonPressed(ActionEvent event) {
    	// get the GraphicsContext, which is used to draw on the Canvas
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	// clear the canvas
    	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    	resetGenerationCount(); // reset the generation count
    	textBox.setText(currentGeneration()); // display the current generation count
    	
    }
    
    // method for generating the next generation within the game
    @FXML
    void SimulateNextGenerationButtonPressed(ActionEvent event) {
    	// get the GraphicsContext, which is used to draw on the Canvas
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	gc.setFill(Color.GRAY);
    	
    	if (generationCount == 0) { // first generation, randomize matrix
    		randomizeIsAlive();
    	}
    	else { // not first generation, set isAlive state based on the rules
    		resetAdjacentSquares(); // clear last generation adjacent squares info
    		
    		calculateAdjacentSquares(); // update the number of adjacent live squares for each square
    		
    		updateIsAlive(); // update the life status of each square in the current generation
    	}
    	
    	// simulate the generation
    	// clear the canvas
    	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    	
    	nextGenerationCount(); // add +1 to the generation count
    	textBox.setText(currentGeneration()); // display the current generation count
    	paintCurrentGeneration(gc); // paint the matrix according to the life status of each square
    }
    
    // update adjacent squares for each live square in the matrix
    private void calculateAdjacentSquares() {
    	for (int i = 0; i < squaresPerRow; i++) {
    		for (int j = 0; j < squaresPerColumn; j++) {
    			if (isAlive[i][j]) {
    				updateAdjacents(i, j);
    			}
    		}
    	}
    }
    
    // inform adjacent squares of a presence of a live square
    private void updateAdjacents(int x, int y) {
    	for (int i = x - 1; i <= x + 1; i++) {
    		if (i >= 0 && i < squaresPerRow) { // check if row is within the matrix
    			for (int j = y - 1; j <= y + 1; j++) {
        			if (j >= 0 && j < squaresPerColumn) { // check if column is within the matrix
        				// current square is not the updating square
        				if ((i != x) || (j != y)) {
        					adjacentSquares[i][j]++;
        				}
        			}
        		}
    		}
    	}
    }
    
    // change the life status of a square based on the rules
    private void updateIsAlive() {
    	for (int i = 0; i < squaresPerRow; i++) {
    		for (int j = 0; j < squaresPerColumn; j++) {
    			if (isAlive[i][j]) { // square contains life currently
    				if (adjacentSquares[i][j] >= 4 || adjacentSquares[i][j] <= 1) {
    					// death from loneliness or over population
    					isAlive[i][j] = false;
    				}
    			}
    			else { // square does not contain life
    				if (adjacentSquares[i][j] == 3) { // three live neighboring squares
    					isAlive[i][j] = true;
    				}
    			}
    		}
    	}
    }
    
    // paint the simulation based on current life status for each square
    private void paintCurrentGeneration(GraphicsContext gc) {
    	double squareWidth = canvas.getWidth() / squaresPerRow;
    	double squareHeight = canvas.getHeight() / squaresPerColumn;
    	
    	for (int i = 0; i < squaresPerRow; i++) {
    		for (int j = 0; j < squaresPerColumn; j++) {
    			// paint square borders
    			gc.strokeRect(squareWidth * (i % squaresPerRow),
    					squareHeight * (j % squaresPerColumn), squareWidth, squareHeight);
    			if (isAlive[i][j]) { // square contains life in the current generation
    				gc.fillRect(squareWidth * (i % squaresPerRow),
        					squareHeight * (j % squaresPerColumn), squareWidth, squareHeight);
    			}
    		}
    	}
    }
    
    // reset adjacentSquares values
    private void resetAdjacentSquares() {
    	for (int i = 0; i < squaresPerRow; i++) {
    		for (int j = 0; j < squaresPerColumn; j++) {
    			adjacentSquares[i][j] = 0;
    		}
    	}
    }
    
    // set random state values for each square
    private void randomizeIsAlive() {
    	for (int i = 0; i < squaresPerRow; i++) {
    		for (int j = 0; j < squaresPerColumn; j++) {
    			isAlive[i][j] = randomNumbers.nextBoolean();
    		}
    	}
    }
    
    
    // reset generation count
    private void resetGenerationCount() {generationCount = 0;}
    
    // count the next generation
    private void nextGenerationCount() {++generationCount;}
    
    // return a string stating the current generation count
    private String currentGeneration() {
    	return String.format("Current generation: %d", generationCount);
    }

}
