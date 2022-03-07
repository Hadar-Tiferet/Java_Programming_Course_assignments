// File: PieceWorkerEmployee.java
// PieceWorkerEmployee extends class employee, represents an employee with earnings per item created

public class PieceWorkerEmployee extends Employee{
	private double pricePerPiece; // price per piece created
	private int numberOfPieces; // number of pieces created this week
	
	// constructor
	public PieceWorkerEmployee(String firstName, String lastName, 
			String socialSecurityNumber, int birthDay, int birthMonth,
			int birthYear, double pricePerPiece, int numberOfPieces) {
		super(firstName, lastName, socialSecurityNumber, birthDay, birthMonth, birthYear);
		
		if (pricePerPiece <= 0.0) { // validate price per piece
			throw new IllegalArgumentException("Price per piece must be > 0.0");
		}
		
		if (numberOfPieces < 0) { // validate number of pieces created
			throw new IllegalArgumentException(
					"Number of pieces created must be >= 0");
		}
		
		this.pricePerPiece = pricePerPiece;
		this.numberOfPieces = numberOfPieces;
	}
	
	// set price per piece created
	public void setPricePerItem(double pricePerPiece) {
		if (pricePerPiece <= 0.0) { // validate price per piece
			throw new IllegalArgumentException("Price per piece must be > 0.0");
		}
		
		this.pricePerPiece = pricePerPiece;
	}
	
	// return price per piece created
	public double getPricePerPiece() {return pricePerPiece;}
	
	// set number of pieces created this week
	public void setNumberOfItems(int numberOfPieces) {
		if (numberOfPieces < 0) { // validate number of pieces created
			throw new IllegalArgumentException(
					"Number of pieces created must be >= 0");
		}
		
		this.numberOfPieces = numberOfPieces;
	}
	
	// return number of pieces created this week
	public int getNumberOfPieces() {return numberOfPieces;}
	
	// calculate earnings; override abstract method earnings in Employee
	@Override
	public double earnings() {
		return getPricePerPiece() * getNumberOfPieces();
	}
	
	// return String representation of PieceWorkerEmployee object
	@Override
	public String toString() {
		return String.format("piece worker employee: %s%n%s: $%,.2f; %s: %,d", 
				super.toString(), "price per piece", getPricePerPiece(),
				"number of pieces created", getNumberOfPieces());
	}
}
