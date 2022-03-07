// File: Answer.java
// represents an answer to a multiple choice question

public class Answer {
	private final String answer;
	private final boolean state;
	
	// constructor
	public Answer(String answer, boolean state) {
		this.answer = answer;
		this.state = state;
	}
	
	public String toString() {return answer;}
	
	public boolean isTrue() {return state;}
		
	
}
