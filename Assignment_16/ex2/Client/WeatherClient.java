// Main application class that loads and displays the weather client GUI.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WeatherClient extends Application{
	@Override
	public void start(Stage stage) throws Exception {
		// loads WeatherClient.fxml and configures the WeatherClientController
		Parent root = FXMLLoader.load(getClass().getResource("WeatherClient.fxml"));
		Scene scene = new Scene(root); // attach scene graph to scene
		stage.setTitle("Chat Client"); // displayed in window's title bar
		stage.setScene(scene); // attach scene to stage
		stage.show(); // display the stage
	}
	
	// application execution starts here
	public static void main(String[] args) {
		launch(args); // create a weather client object and call its start method
	}
}