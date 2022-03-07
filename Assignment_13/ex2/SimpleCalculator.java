/*
 * Author: Hadar Tiferet
 * File: SimpleCalculator.java
 */

// Main application class that loads and displays the Simple calculator GUI.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimpleCalculator extends Application{
	@Override
	public void start(Stage stage) throws Exception {
		// loads SimpleCalculator.fxml and configures the SimpleCalculatorController
		Parent root = FXMLLoader.load(getClass().getResource("SimpleCalculator.fxml"));
		Scene scene = new Scene(root); // attach scene graph to scene
		stage.setTitle("Simple Calculator"); // displayed in window's title bar
		stage.setScene(scene); // attach scene to stage
		stage.show(); // display the stage
	}
	
	// application execution starts here
	public static void main(String[] args) {
		launch(args); // create an Exam object and call its start method
	}
}