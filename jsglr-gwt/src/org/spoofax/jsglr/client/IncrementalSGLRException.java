package org.spoofax.jsglr.client;

/**
 * An exception thrown if some input cannot be incrementally parsed.
 * Likely, it can be parsed non-incrementally instead.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class IncrementalSGLRException extends Exception {

	private static final long serialVersionUID = 3965028076577690983L;

	private static final String DEFAULT_MESSAGE = "Incremental parsing failed";
	
	public IncrementalSGLRException() {
		super(DEFAULT_MESSAGE);
	}

	public IncrementalSGLRException(String message) {
		super(message);
	}

	public IncrementalSGLRException(Throwable cause) {
		super(DEFAULT_MESSAGE, cause);
	}

	public IncrementalSGLRException(String message, Throwable cause) {
		super(message, cause);
	}

}
