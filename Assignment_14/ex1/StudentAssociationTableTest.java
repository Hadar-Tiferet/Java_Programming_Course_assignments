// test program for an association table using Students as keys and Strings as values
import java.util.Iterator;
import java.security.SecureRandom;

public class StudentAssociationTableTest {
	public static void main(String[] args) {
		// create an array of Student objects
		Student[] studentArray = {
				new Student("John", "Smith", "111111111", "1984"),
				new Student("Karen", "Price", "222222222", "1991"),
				new Student("Sue", "Jones", "333333333", "1998")
		};
		
		// create an array of phone numbers as Strings
		String[] phoneNumberArray = {
				"052-123-4567", "052-987-6543",
				"054-345-6789"
		};
		
		// create an Association table using the declared arrays
		// test the empty constructor
		AssociationTable<Student, String> studentPhoneTable = new AssociationTable<>();
		// initialize the Association table within a try catch segment to handle the possible exception
		try {
			studentPhoneTable = new AssociationTable<Student, String>(studentArray, phoneNumberArray);
			System.out.printf("Table after initialization:%n");
			printStudentTable(studentPhoneTable);
			
			// create new Student and String objects to add to the table
			Student newStudent = new Student("Amanda", "Ross", "222222226", "2000");
			String newPhone = "053-159-8741";
		
			// add a new entry to the table
			studentPhoneTable.add(newStudent, newPhone);
			System.out.printf("Table after adding a new student:%n");
			printStudentTable(studentPhoneTable);
		
			// update a phone number
			studentPhoneTable.add(newStudent, "052-987-6544");
			System.out.printf("Table after updating a student's phone number:%n");
			printStudentTable(studentPhoneTable);
			
			// choose a random entry in studentArray
			SecureRandom randomNumber = new SecureRandom();
			int randomEntry = randomNumber.nextInt(studentArray.length);
			// remove a randomly selected student from the table
			studentPhoneTable.remove(studentArray[randomEntry]);
		
			System.out.printf("After deleting a student from the table:%n");
			// print the content of the association table
			printStudentTable(studentPhoneTable);
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.exit(2);
		}
		catch (NullPointerException e) {
			e.printStackTrace();
			System.err.printf("%nAssociation table does not accept null keys");
			System.exit(3);
		}
		catch (ClassCastException e) {
			e.printStackTrace();
			System.err.printf("%nkeys must be comparable and of the same class");
			System.exit(4);
		}
	}
	
	// use an iterator to print the association table's contents
	private static void printStudentTable(AssociationTable<Student, String> table) {
		Iterator<Student> tableIterator = table.keyIterator();
		System.out.printf("Student phone table:%n");
		
		while (tableIterator.hasNext()) {
			Student currentStudent = tableIterator.next();
			// print the current table entry using an implicit call to Student object's toString method
			// and the phone string corresponding to the student key in the table 
			System.out.printf("%n%s%nPhone number: %s%n", 
					currentStudent, table.get(currentStudent));
		}
		System.out.println();
	}
}
