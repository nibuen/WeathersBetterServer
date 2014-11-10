package exceptions;

/**
 * Thrown if issue with any external weather API.
 */
public class ExternalWeatherApiException extends Exception {

	public ExternalWeatherApiException(String message) {
		super(message);
	}

}
