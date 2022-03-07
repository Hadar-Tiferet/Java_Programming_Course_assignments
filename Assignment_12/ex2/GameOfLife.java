/*
 * Author: Hadar Tiferet
 * File: GameOfLife.java
 */

// Main application class that loads and displays the GameOfLife GUI.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class GameOfLife extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		// loads GameOfLife.fxml and configures the GameOfLifeController
		Parent root = FXMLLoader.load(getClass().getResource("GameOfLife.fxml"));
		Scene scene = new Scene(root); // attach scene graph to scene
		stage.setTitle("John Conway's Game Of Life"); // displayed in window's title bar
		stage.setScene(scene); // attach scene to stage
		stage.show(); // display the stage
	}
	
	// application execution starts here
	public static void main(String[] args) {
		launch(args); // create a GameOfLife object and call its start method
	}
}