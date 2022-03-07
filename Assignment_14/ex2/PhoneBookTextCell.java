// Custom ListView cell factory that displays a row of text
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

// represents an individual cell within the contact list
public class PhoneBookTextCell extends ListCell<String> {
	private VBox vbox = new VBox(8.0); // 8 points of gap between controls
	private Label nameLabel = new Label();
	
	// constructor configures VBox and two Labels
	public PhoneBookTextCell() {
		vbox.setAlignment(Pos.CENTER); // center VBox contents horizontally
		nameLabel.setWrapText(true); // wrap if text too wide to fit in label
		nameLabel.setTextAlignment(TextAlignment.CENTER); // center text
		
		vbox.getChildren().add(nameLabel); // attach to VBox
		
		setPrefWidth(USE_PREF_SIZE); // use preferred size for cell width
	}
	
	// called to configure each custom ListView cell
	@Override
	protected void updateItem(String item, boolean empty) {
		// required to ensure that cell displays properly
		super.updateItem(item, empty);
		
		if (empty || item == null) {
			setGraphic(null); // don't display anything
		}
		else {
			nameLabel.setText(item.toString()); // configure Label's text
			setGraphic(vbox); // attach custom layout to ListView cell
		}
	}
}