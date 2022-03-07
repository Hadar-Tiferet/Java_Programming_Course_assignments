/*
 * Author: Hadar Tiferet
 * File: SimpleCalculatorController.java
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class SimpleCalculatorController {

	private enum Operator {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, EQUALS;
	}
	
	// toggle groups
	@FXML private ToggleGroup digitToggleGroup;
	@FXML private ToggleGroup operatorToggleGroup;
    
	// digit buttons
	@FXML private ToggleButton zeroToggleButton;
	@FXML private ToggleButton oneToggleButton;
	@FXML private ToggleButton twoToggleButton;
	@FXML private ToggleButton threeToggleButton;
	@FXML private ToggleButton fourToggleButton;
	@FXML private ToggleButton fiveToggleButton;
	@FXML private ToggleButton sixToggleButton;
	@FXML private ToggleButton sevenToggleButton;
	@FXML private ToggleButton eightToggleButton;
	@FXML private ToggleButton nineToggleButton;
	
    // operator buttons
	@FXML private ToggleButton addToggleButton;
	@FXML private ToggleButton subtractToggleButton;
	@FXML private ToggleButton multiplyToggleButton;
    @FXML private ToggleButton divideToggleButton;
    @FXML private ToggleButton equalsToggleButton;
    
    // sign button
    @FXML private Button signButton;
    
    // decimal button
    @FXML private Button dotButton;
    
    // screen text field
    @FXML private TextField screenTextField;
    
    // instance variables
    BigDecimal storedValue = new BigDecimal(0);
    BigDecimal inputValue = new BigDecimal(0);
    Operator currentOperation;
    boolean isAfterOperation = true;
    int decimalCount = 0; // the number of digits after the decimal point in inputValue
    
    
    public void initialize() {
    	// set user data
    	
    	zeroToggleButton.setUserData(new BigDecimal(0));
    	oneToggleButton.setUserData(new BigDecimal(1));
    	twoToggleButton.setUserData(new BigDecimal(2));
    	threeToggleButton.setUserData(new BigDecimal(3));
    	fourToggleButton.setUserData(new BigDecimal(4));
    	fiveToggleButton.setUserData(new BigDecimal(5));
    	sixToggleButton.setUserData(new BigDecimal(6));
    	sevenToggleButton.setUserData(new BigDecimal(7));
    	eightToggleButton.setUserData(new BigDecimal(8));
    	nineToggleButton.setUserData(new BigDecimal(9));
    	
    	addToggleButton.setUserData(Operator.ADD);
    	subtractToggleButton.setUserData(Operator.SUBTRACT);
    	multiplyToggleButton.setUserData(Operator.MULTIPLY);
    	divideToggleButton.setUserData(Operator.DIVIDE);
    	equalsToggleButton.setUserData(Operator.EQUALS);
    	currentOperation = Operator.EQUALS;
    }
    
    // handles additions of digits to the input value
    @FXML
    void digitToggleButtonPressed(ActionEvent event) {
    	BigDecimal addition = (BigDecimal)digitToggleGroup.getSelectedToggle().getUserData();
    	
    	if (isAfterOperation) { // an operation was the last action performed, get a new input number
    		inputValue = new BigDecimal(0);
    		isAfterOperation = false;
    	}
    	
    	// add a new digit to inputValue
    	if (decimalCount == 0) { // new digit is added after the decimal point
    		inputValue = inputValue.scaleByPowerOfTen(1);
    	}
    	else { // number includes a decimal point
    		addition = addition.scaleByPowerOfTen(-decimalCount);
    		decimalCount++;
    	}
    	
    	inputValue = inputValue.add(addition);
    	setFormattedString(inputValue);
    	((ToggleButton)digitToggleGroup.getSelectedToggle()).setSelected(false);
    }

    // signal a decimal point for the current value stored in inputValue
    @FXML
    void dotButtonPressed(ActionEvent event) {
    	if (decimalCount == 0) {
    		decimalCount++;
    	}
    }
    
    // handles requests for operations
    @FXML
    void operatorToggleButtonPressed(ActionEvent event) {
    	if (!isAfterOperation) { 
    		// last action was not an operation - execute the current pending operation before setting up the next
    		executeLastOperation();
    		isAfterOperation = true;
    	}
    	
    	setFormattedString(storedValue);
    	decimalCount = 0; // after an operation request - reset new input to zero digits after the decimal point
    	// set the next operator to be executed
    	currentOperation = (Operator)operatorToggleGroup.getSelectedToggle().getUserData();
    	((ToggleButton)operatorToggleGroup.getSelectedToggle()).setSelected(false);
    }

    // change the sign for the input value
    // will use the value in storedValue if no new input has been given yet after an operation
    @FXML
    void signButtonPressed(ActionEvent event) {
    	if (isAfterOperation) { // an operation was the last action performed
    		inputValue = storedValue;
    		isAfterOperation = false;
    	}
    	inputValue = inputValue.negate();
    	setFormattedString(inputValue);
    }
    
    // clear inputValue
    @FXML
    void clearButtonPressed(ActionEvent event) {
    	decimalCount = 0;
    	inputValue = new BigDecimal(0);
    	setFormattedString(inputValue);
    }
    
    // show the value currently stored by the calculator
    @FXML
    void memoryButtonPressed(ActionEvent event) {
    	setFormattedString(storedValue);
    }
    
    // execute the last specified arithmetic operation
    void executeLastOperation() {
    	try {
    		switch (currentOperation) {
    		case EQUALS:
    			storedValue = inputValue.stripTrailingZeros();
    			break;
    		case ADD:
    			storedValue = storedValue.add(inputValue).stripTrailingZeros();
    			break;
    		case SUBTRACT:
    			storedValue = storedValue.subtract(inputValue).stripTrailingZeros();
    			break;
    		case MULTIPLY:
    			storedValue = storedValue.multiply(inputValue).stripTrailingZeros();
    			break;
    		case DIVIDE:
    			// rounding mode for division to prevent non terminal decimal exceptions 
    			storedValue = storedValue.divide(inputValue, 20, RoundingMode.HALF_EVEN).stripTrailingZeros();
    			break;
    		}
    	} catch (ArithmeticException e) {
    		// in case of an illegal operation, like division by zero - reset the stored number to zero
    		storedValue = new BigDecimal(0);
    	}
    }
    
    // set screenTextField to show the currently requested number
    void setFormattedString(BigDecimal number) {
    	screenTextField.setText(number.toPlainString());
    }

}