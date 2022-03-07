// File: BirthDate.java
// contains a birth date as an extension of class Date
import java.util.Calendar;

// class BirthDate extends class Date
public class BirthDate extends Date {
	// no additional instance variables
	
	// BirthDate constructor, uses the superclass constructor
	public BirthDate(int day, int month, int year) {
		// the super uses a month/day/year format
		super(month, day, year);
	}
	
	// returns true if Birthday takes place this month
	public boolean isBirthMonth() {
		// month representation starting at 0, including undecimber
		int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
		// change current month to fit a Georgian calendar, starting at 1 and ending in 12
		currentMonth = (currentMonth == 12 ? currentMonth : ++currentMonth);
		return (currentMonth == getMonth());
	}
}
