// Based on Fig. 8.7: Date.java
// Date class declaration.

public class Date {
	private int month; // 1-12
	private int day; // 1-31 based on month
	private int year; // any year
	
	private static final int[] daysPerMonth = 
		{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	
	// constructor: confirm proper value for month and day given the year
	public Date(int month, int day, int year) {
		// check if month in range
		if (month <= 0 || month > 12) {
			throw new IllegalArgumentException(
					"month (" + month + ") must be 1-12");
		}
		
		// check if day in range for month
		if (day <= 0 || (day > daysPerMonth[month] && !(month == 2 && day == 29))) {
			throw new IllegalArgumentException("day (" + day +
					") out-of-range for the specified month and year");
		}
		
		// check for leap year if month is 2 and day is 29
		if (month == 2 && day == 29 && !(year % 400 == 0 || 
				(year % 4 == 0 && year % 100 != 0))) {
			throw new IllegalArgumentException("day (" + day + 
					") out-of-range for the specified month and year");
		}
		
		this.month = month;
		this.day = day;
		this.year = year;
	}
	
	// get method for month
	public int getMonth() {
		return month;
	}
	
	// return String representation of a birthday date in form of day/month/year
	@Override
	public String toString() {
		return String.format("%02d/%02d/%04d", day, month, year);
	}
}