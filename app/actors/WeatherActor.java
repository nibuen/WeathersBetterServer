package actors;

import play.libs.F.Promise;
import play.libs.ws.WS;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Basic actor used to retrieve weather. Currently
 * "http://api.openweathermap.org" is only API being used. Designed that would
 * be easy to replace with an Akka Actor if ever needed.
 *
 */
public class WeatherActor {

	/**
	 * Timeout in milliseconds for the weather API.
	 */
	private static final int WEATHER_API_TIMEOUT_MILLIS = 10000;

	private WeatherActor() {
	}

	public static JsonNode getWeather(double longitude, double latitude,
			int count) {
		String jsonCall = "http://api.openweathermap.org/data/2.5/find?lat="
				+ longitude + "&lon=" + latitude + "&cnt=" + count;

		Promise<JsonNode> jsonPromise = WS.url(jsonCall).get()
				.map(response -> {
					return response.asJson();
				});

		return jsonPromise.get(WEATHER_API_TIMEOUT_MILLIS);
	}

}
