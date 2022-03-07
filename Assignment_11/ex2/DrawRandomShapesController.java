/*
 * Author: Hadar Tiferet
 * File: DrawRandomShapesController.java
 */

import java.security.SecureRandom;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// The class controls the creation of a set of randomly generated shapes upon the click of a button
public class DrawRandomShapesController {
	int numberOfShapes = 10; // the number of shapes to be created randomly
	int minSize = 10; // minimum of pixels for a shape to be comfortably visible on screen
	int minScreenToShapeRatio = 4; // the minimum ratio between the size of the screen and a given shape
	
	// random number generator
	static final SecureRandom randomNumbers = new SecureRandom();
	final int RGB_RANGE = 256;
	
	// constants that represent the possible shape types
	static final int LINE = 0;
	static final int RECTANGLE = 1;
	static final int OVAL = 2;
	
    @FXML
    private Canvas canvas;
    
    @FXML
    void DrawRandomShapesButtonPressed(ActionEvent event) {
    	// get the GraphicsContext, which is used to draw on the Canvas
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	// clear the canvas before drawing random shapes
    	gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    	for (int i = 0; i < numberOfShapes; i++) {
    		drawRandomShape(gc);
    	}
    }
    
    // drawRandomShape is a separate method that handles the choice and creation of a random shape, for readability
    void drawRandomShape(GraphicsContext gc) {
    	// set position and size parameters for the chosen shape
    	int startingPointX = getRandomRange((int)canvas.getWidth(), 0);
    	int startingPointY = getRandomRange((int)canvas.getHeight(), 0);
    	int width = getRandomRange((int)(canvas.getWidth() / minScreenToShapeRatio), minSize);
    	int height = getRandomRange((int)(canvas.getHeight() / minScreenToShapeRatio), minSize);
    	// set color of the chosen shape
    	gc.setFill(Color.rgb(getRandomRange(RGB_RANGE, 0), getRandomRange(RGB_RANGE, 0), getRandomRange(RGB_RANGE, 0)));
    	// choose a shape type
    	int shape = getRandomRange(3 , 0); // get a random number between the 3 possible shape types
    	
    	switch (shape) {
    	case LINE: // draw a straight line
    		gc.setStroke(gc.getFill());
    		gc.strokeLine(startingPointX, startingPointY, width, height);
    		break;
    	case RECTANGLE: // draw a color filled Rectangle
    		gc.fillRect(startingPointX, startingPointY, width, height);
    		break;
    	case OVAL: // draw a color filled Oval
    		gc.fillOval(startingPointX, startingPointY, width, height);
    		break;
    	}
    }
    
    // Incorporate an offset to the randomNumbers.nextInt method
    int getRandomRange(int max, int offset) {
    	if (max < offset) {
    		return offset;
    	}
    	else {
    		return offset + randomNumbers.nextInt(max - offset);
    	}
    }
}
