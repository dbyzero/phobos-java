package phobos.exception;

/**
 * Exception throw in case of a unwanted value appears in a switch block
 * @author Cyril ALFARO <cyril@dbyzero.com>
 */

public class UnmanagedSwitchCaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param message
	 */
	public UnmanagedSwitchCaseException(String message) {
		super(message);
	}

}
