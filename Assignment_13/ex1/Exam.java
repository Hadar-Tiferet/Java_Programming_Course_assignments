/*
 * Author: Hadar Tiferet
 * File: Exam.java
 */

// Main application class that loads and displays the Exam GUI.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Exam extends Application{
	@Override
	public void start(Stage stage) throws Exception {
		// loads Exam.fxml and configures the ExamController
		Parent root = FXMLLoader.load(getClass().getResource("Exam.fxml"));
		Scene scene = new Scene(root); // attach scene graph to scene
		stage.setTitle("Exam"); // displayed in window's title bar
		stage.setScene(scene); // attach scene to stage
		stage.show(); // display the stage
	}
	
	// application execution starts here
	public static void main(String[] args) {
		launch(args); // create an Exam object and call its start method
	}
}
