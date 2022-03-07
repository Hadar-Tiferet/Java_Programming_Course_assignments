// File: PayrollSystem.java
// Employee information management program.

public class PayrollSystem {
	// defined bonus for employees who celebrate a birthday this month
	private static final double BIRTHDAYBONUS = 200;
	
	public static void main(String[] args) {
		// create an Employee array of specific sub-class employees 
		Employee[] employees = {
				new SalariedEmployee("John", "Smith", "111-11-1111",
						17, 02, 1964, 800.00),
				new HourlyEmployee("Karen", "Price", "222-22-2222",
						20, 11, 1985, 16.75, 40),
				new CommissionEmployee("Sue", "Jones", "333-33-3333",
						31, 12, 1995, 10000, .06),
				new BasePlusCommissionEmployee("Bob", "Lewis", "444-44-4444",
						03, 03, 1974, 5000, .04, 300),
				new PieceWorkerEmployee("David", "Goldfinger", "555-55-5555",
						13, 06, 1990, 12.25, 43),
				new PieceWorkerEmployee("Natalie", "Decker", "666-66-6666",
						06, 11, 1963, 27.43, 15)
		};
		
		System.out.printf("Employees processed polymorphically:%n%n");
		
		// generically process each element in array employees
		for (Employee currentEmployee : employees) {
			System.out.println(currentEmployee); // invokes toString
			
			// if an employee has their birthday in the current month
			// indicate and add a bonus to their earnings
			if (currentEmployee.hasBirthDay()) {
				System.out.printf("Earned $%,.2f , including a birth day bonus%n",
						currentEmployee.earnings() + BIRTHDAYBONUS);
			}
			else {
				System.out.printf("Earned $%,.2f%n", currentEmployee.earnings());
			}
			
			System.out.println();
		}
	}
}