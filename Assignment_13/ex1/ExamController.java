/*
 * Author: Hadar Tiferet
 * File: ExamController.java
 */
// javafx operations
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
// IO operations
import java.util.Scanner;
import java.io.File;
// utilities
import java.security.SecureRandom;
import java.util.ArrayList;
// exception handling
import java.util.NoSuchElementException;
import java.io.FileNotFoundException;
import java.lang.IllegalStateException;
import java.lang.SecurityException;

public class ExamController {

    @FXML private TextArea textArea;    
    @FXML private VBox answerAreaVBox;    
    @FXML private RadioButton radioButton1;       
    @FXML private RadioButton radioButton2;    
    @FXML private RadioButton radioButton3;    
    @FXML private RadioButton radioButton4;
    @FXML private ToggleGroup answerToggleGroup;
    @FXML private Button submitAnswerButton;    
    @FXML private Label inputRequestLabel;  
    @FXML private Button nextButton;
    @FXML private Button restartExamButton;
    @FXML private Label informationLabel;
    
    // instance variables
    private final int ENTRIES_PER_QUESTION = 5; // specification for each question (1 question and 4 answers)
    private int totalQuestions; // the total amount of questions successfully read from file
    private int currentPage;
    private int score; // the number of right answers given
    
    // ArrayList of a class specifically made to contain a question in the form of
    // one question String and 4 answers with truth values for each one
    private ArrayList<MultipleChoiceQuestion> questions = new ArrayList<MultipleChoiceQuestion>();
    
    // initialize controller
    public void initialize() {
    	getQuestionsFromFile(new File("exam.txt"));
    	
    	totalQuestions = questions.size(); // set the number of questions found in exam.txt
    	
    	currentPage = -1; // opening screen
    	score = 0;
    	answerAreaVBox.setVisible(false);
    	answerAreaVBox.setDisable(true);
    	nextButton.setDisable(false);
    	informationLabel.setText("Please click Next to start the exam");
    	textArea.setText(String.format("%s%n%s%n%s%n%s%n%s%n%s", "Welcome to the exam.", 
    			"Each question has 4 possible answers and only one correct answer.",
    			"After selecting an answer - click Submit Answer to progress.",
    			"You can move to the next page after submitting an answer by clicking Next.",
    			"At any time, you can choose to reset the exam by clicking Reset Exam.",
    			"The page after the last question will include your score."));
    }
    
    // progress to the next page, if includes a question - lock progression until an answer is submitted
    @FXML
    void nextButtonPressed(ActionEvent event) {
    	currentPage++; // move to the next page index
    	
    	if (currentPage >= totalQuestions) {
    		// ending screen
    		// disable the answerVBox
    		answerAreaVBox.setVisible(false);
        	answerAreaVBox.setDisable(true);
        	informationLabel.setText("Exam finished");
        	textArea.setText(String.format("%s%n%s %d %s %d %s%n%s %.2f%%%n%s",
        			"The exam is done.", "You've answered correctly on:", score, "out of:",
        			totalQuestions, "questions.", "Your total score is:",
        			((double)score * 100 / totalQuestions),
        			"If you'd like to start the exam again - click Reset Exam."));
    	}
    	else {
    		if (currentPage <= 0) {
    			// transition from starting screen to questions
    			// enable the answers area
    			answerAreaVBox.setVisible(true);
            	answerAreaVBox.setDisable(false);
            	
    		}
    		informationLabel.setText(String.format("Question %d out of %d", currentPage + 1, totalQuestions));
    		// set up the current question and answers
    		textArea.setText(questions.get(currentPage).getQuestion());
    		setRandomAnswers(currentPage);
    		inputRequestLabel.setText("Please select an answer and then click Submit Answer");
    		submitAnswerButton.setDisable(false); // enable answer submission
    		// set the first answer to be highlighted for each new question
    		radioButton1.setSelected(true); 
    	}
    	
    	// disable the Next button
    	nextButton.setDisable(true);
    	
    }

    // reset the state of the exam to the start (without having to read from file again)
    @FXML
    void restartExamButtonPressed(ActionEvent event) {
    	currentPage = -1; // opening screen
    	score = 0;
    	answerAreaVBox.setVisible(false);
    	answerAreaVBox.setDisable(true);
    	nextButton.setDisable(false);
    	informationLabel.setText("Please click Next to start the exam");
    	textArea.setText(String.format("%s%n%s%n%s%n%s%n%s%n%s", "Welcome to the exam.", 
    			"Each question has 4 possible answers and only one correct answer.",
    			"After selecting an answer - click Submit Answer to progress.",
    			"You can move to the next page after submitting an answer by clicking Next.",
    			"At any time, you can choose to reset the exam by clicking Reset Exam.",
    			"The page after the last question will include your score."));
    }

    // lock down an answer, display a message regarding the answer and enable progressing to the next page
    @FXML
    void submitAnswerButtonPressed(ActionEvent event) {
    	if ((boolean)answerToggleGroup.getSelectedToggle().getUserData()) {
    		// the answer was correct
    		score++;
    		inputRequestLabel.setText("Correct answer! please click Next to move to the next page");
    	}
    	else {
    		inputRequestLabel.setText("Incorrect answer. please click Next to move to the next page");
    	}
    	
    	submitAnswerButton.setDisable(true); // prevent multiple submissions to the same question
    	nextButton.setDisable(false); // an answer was accepted, enable moving to the next page
    }
    
    private void getQuestionsFromFile(File fileName) {
    	// attempt to read questions in the format of 5 non empty lines per question from a given file
    	// write each question into ArrayList questions
    	try(Scanner input = new Scanner(fileName)) { // close Scanner upon exit
    		
    		int currentEntry = 0; // counts the number of entry since a new question was configured
    		String[] entries = new String[ENTRIES_PER_QUESTION]; // string array for question initialization
    		
    		// read from file until end of file is reached
    		while (input.hasNextLine()) {
    			entries[currentEntry] = input.nextLine();
    			currentEntry++;
    			
    			if (currentEntry == ENTRIES_PER_QUESTION) {
    				// enough entries to create a new question
    				questions.add(new MultipleChoiceQuestion(
    						entries[0], entries[1], entries[2],
    						entries[3], entries[4]));
    				
    				// reset entries to accept a new question
    				currentEntry = 0;
    			}
    		}
    		
    		if (currentEntry != 0) {
    			// the number of entries is not divisible by 5
    			throw new NoSuchElementException();
    		}
    	} catch (FileNotFoundException | IllegalStateException | SecurityException e) {
    		System.err.printf("%nFile: %s could not be opened%n", fileName);
    		e.printStackTrace();
    		System.exit(1);
    	} catch (NoSuchElementException e) {
    		System.err.printf("%nFile: %s does not follow to the format of 5 entries per question%n", fileName);
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
    
    // for a given question - set a random answer for each of the radio buttons and store the truth value
    private void setRandomAnswers(int questionNumber) {
    	SecureRandom random = new SecureRandom();
    	ArrayList<Answer> answers = questions.get(questionNumber).getAnswersList();
    	
    	// set data and text for each radio button
    	int index = random.nextInt(answers.size());
    	radioButton1.setText(answers.get(index).toString()); // set the text to be displayed
    	radioButton1.setUserData(answers.get(index).isTrue()); // set the state of the answer (true or false)
    	answers.remove(index); // remove the answer that was selected from the list
    	
    	index = random.nextInt(answers.size());
    	radioButton2.setText(answers.get(index).toString()); // set the text to be displayed
    	radioButton2.setUserData(answers.get(index).isTrue()); // set the state of the answer (true or false)
    	answers.remove(index); // remove the answer that was selected from the list
    	
    	index = random.nextInt(answers.size());
    	radioButton3.setText(answers.get(index).toString()); // set the text to be displayed
    	radioButton3.setUserData(answers.get(index).isTrue()); // set the state of the answer (true or false)
    	answers.remove(index); // remove the answer that was selected from the list
    	
    	// only one item is left
    	radioButton4.setText(answers.get(0).toString()); // set the text to be displayed
    	radioButton4.setUserData(answers.get(0).isTrue()); // set the state of the answer (true or false)
    	answers.clear(); // clear the list (not necessary)
    }
}