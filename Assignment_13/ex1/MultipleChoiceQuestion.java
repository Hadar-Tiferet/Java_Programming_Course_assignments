// File: MultipleChoiceQuestion.java
// contains a question and 4 answers in an array, where the first answer is the correct one.
import java.util.ArrayList;

public class MultipleChoiceQuestion {
	private final String question;
	private final Answer rightAnswer;
	private final Answer wrongAnswer1;
	private final Answer wrongAnswer2;
	private final Answer wrongAnswer3;
	
	
	// constructor
	public MultipleChoiceQuestion(String question, String rightAnswer,
			String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
		this.question = question;
		this.rightAnswer = new Answer(rightAnswer, true);
		this.wrongAnswer1 = new Answer(wrongAnswer1, false);
		this.wrongAnswer2 = new Answer(wrongAnswer2, false);
		this.wrongAnswer3 = new Answer(wrongAnswer3, false);
	}
	
	// get methods
	
	public String getQuestion() {return question;}
	
	// return an array list containing the answer to the current question
	public ArrayList<Answer> getAnswersList() {
		ArrayList<Answer> answers = new ArrayList<Answer>(4); // there are 4 answers in total
		answers.add(rightAnswer);
		answers.add(wrongAnswer1);
		answers.add(wrongAnswer2);
		answers.add(wrongAnswer3);
		return answers;
	}
}
