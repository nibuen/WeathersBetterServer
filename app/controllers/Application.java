package controllers;

import java.util.ArrayList;
import java.util.List;

import org.geojson.LngLatAlt;

import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.index;
import actors.ClientConnectionProtocol;
import actors.ClientConnectionProtocol.User;
import actors.ClientConnectionProtocol.WeatherLocation;
import actors.ClientConnectionProtocol.WeatherLocations;
import actors.WeatherActor;
import play.mvc.BodyParser;

import com.fasterxml.jackson.databind.JsonNode;

import exceptions.ExternalWeatherApiException;

public class Application extends Controller {

	/**
	 * The return of the main page. Currently just used for live debugging.
	 * 
	 * @return
	 * @throws ExternalWeatherApiException
	 */
	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	/**
	 * Gives the weather of an area around a specific long/lat.
	 * 
	 * @return If successful JSON response with a list of
	 *         {@link ClientConnectionProtocol.WeatherLocations}.
	 */
	@BodyParser.Of(BodyParser.Json.class)
	public static Result weather() {
		Result result = Results
				.internalServerError("error retrieving weather data.");

		User user = Json.fromJson(request().body().asJson(),
				ClientConnectionProtocol.User.class);

		LngLatAlt coordinates = user.getPosition().getCoordinates();
		JsonNode jsonNode = WeatherActor.getWeather(coordinates.getLatitude(),
				coordinates.getLongitude(), 30);

		try {
			WeatherLocations weatherLocations = weatherLocations(jsonNode);
			result = ok(Json.toJson(weatherLocations));
		} catch (ExternalWeatherApiException e) {
			result = Results.internalServerError(e.getMessage());
		}

		return result;
	}

	private static WeatherLocations weatherLocations(JsonNode jsonNode)
			throws ExternalWeatherApiException {
		JsonNode messageStatusNode = jsonNode.get("message");

		if (messageStatusNode == null) {
			throw new ExternalWeatherApiException("unkown error");
		}
		if (!messageStatusNode.asText().equals("accurate")) {
			throw new ExternalWeatherApiException(messageStatusNode.asText());
		}

		List<WeatherLocation> weatherLocations = new ArrayList<WeatherLocation>();

		for (JsonNode itemNode : jsonNode.get("list")) {
			JsonNode mainNode = itemNode.get("main");

			WeatherLocation weatherLocation = new WeatherLocation(itemNode.get(
					"name").asText(), kelvinToFahrenheit(mainNode.get("temp")
					.asDouble()), kelvinToFahrenheit(mainNode.get("temp_min")
					.asDouble()), kelvinToFahrenheit(mainNode.get("temp_max")
					.asDouble()));

			weatherLocations.add(weatherLocation);
		}

		return new WeatherLocations(weatherLocations);
	}

	private static double kelvinToFahrenheit(double kelvin) {
		return (kelvin - 273.15) * 1.8000 + 32.00;
	}

}
