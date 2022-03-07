// IllegalArgumentException is a custom made exception set as a sub class of Exception

@SuppressWarnings("serial")
public class IllegalArgumentException extends Exception{
	// empty constructor
	public IllegalArgumentException() {
		super("IllegalArgumentException");
	}
	
	// customized error message constructor
	public IllegalArgumentException(String string) {
		super(string);
	}
	
	// customized error message and chained exception constructor
	public IllegalArgumentException(String string, Throwable throwable) {
		super(string, throwable);
	}
	
	// throwable constructor
	public IllegalArgumentException(Throwable throwable) {
		super(throwable);
	}
}
