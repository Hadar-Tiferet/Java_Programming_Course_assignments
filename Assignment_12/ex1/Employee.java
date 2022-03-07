// File: Employee.java
// Employee abstract superclass.

public abstract class Employee {
	private final String firstName;
	private final String lastName;
	private final String socialSecurityNumber;
	private final BirthDate birthDate;
	// hidden since the assignment specified handling the bonus in the test class
	/*
	private final double birthDayBonus = 200; // earning bonus for an employee in their birthday month
	*/
	
	// constructor
	public Employee(String firstName, String lastName, 
			String socialSecurityNumber, int birthDay, int birthMonth, int birthYear) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.socialSecurityNumber = socialSecurityNumber;
		// build birthDate using the class constructor
		this.birthDate = new BirthDate(birthDay, birthMonth, birthYear);
	}
	
	// return first name
	public String getFirstName() {return firstName;}
	
	// return last name
	public String getLastName() {return lastName;}
	
	// return social security number
	public String getSocialSecurityNumber() {return socialSecurityNumber;}
	
	// return birth date
	public String getBirthDate() {return birthDate.toString();}
	
	// hidden since the assignment specified handling the bonus in the test class
	// return birthday bonus amount
	/*
	public double getBirthDayBonus() {return birthDayBonus;}
	*/
	
	// return true if employee has a birthday this month, for convenience
	public boolean hasBirthDay() {return birthDate.isBirthMonth();}
	
	// return String representation of Employee object
	@Override
	public String toString() {
		return String.format("%s %s%nsocial security number: %s%nbirth date: %s", 
				getFirstName(), getLastName(), getSocialSecurityNumber(), getBirthDate());
	}
	
	// abstract method to be overridden by concrete subclasses
	public abstract double earnings(); // no implementation here
	
	// method created to calculate the added bonuses to each employee directly
	// hidden since the assignment specified handling the bonus in the test class
	/*
	public double earningsPlusBonus() {
		return (hasBirthDay() ? earnings() + getBirthDayBonus() : earnings());
	}
	*/
}