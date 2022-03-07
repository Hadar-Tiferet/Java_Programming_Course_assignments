// Student.java
// information about a student with a comparable ID.
public class Student implements Comparable<Student>{
	// instance variables
	String firstName;
	String lastName;
	String id;
	String birthYear;
	
	// constructor
	public Student(String firstName, String lastName, String id, String birthYear) {
		// check input using regular expressions
		try {
			validateInput(firstName.trim(), "[a-zA-Z]+", 
				"First name must contain only one or more letters");
			validateInput(lastName.trim(), "[a-zA-Z]+(['-][a-zA-Z]+)*", 
				"Last name must contain only one or more letters");
			validateInput(id.trim(), "\\d{9}", 
				"ID must contain exactly 9 digits");
			validateInput(birthYear.trim(), "\\d{4}", 
				"Birth year must contain exactly 4 digits");
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		// all input was validated without throwing an exception, continue
		this.firstName = firstName.trim();
		this.lastName = lastName.trim();
		this.id = id.trim();
		this.birthYear = birthYear.trim();
	}
	
	// get methods
	public String getFirstName() {return firstName;}
	
	public String getLastName() {return lastName;}
	
	public String getId() {return id;}
	
	public String getBirthYear() {return birthYear;}
	
	// print method for an object of class Student
	@Override
	public String toString() {
		return String.format("Student: %s %s%nID: %s birth year: %s",
				getFirstName(), getLastName(), getId(), getBirthYear());
	}
	
	// implementation of compareTo of the comparable interface
	@Override
	public int compareTo(Student other) {
		// return the value of a lexicographical comparison between the IDs of both objects
		// positive integer if the current object is larger, 
		// negative if smaller and zero if the objects are equal
		return getId().compareTo(other.getId());
	}
	
	// class method used to validate given input before processing it
	private static void validateInput(String input, String regularExpression, String errorMessage) 
			throws IllegalArgumentException {
		if (!input.matches(regularExpression)) {
			throw new IllegalArgumentException(errorMessage);
		}
	}
}


