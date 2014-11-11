package controllers;

import java.util.ArrayList;
import java.util.List;

import models.ClientConnectionProtocol;
import models.ClientConnectionProtocol.User;
import models.ClientConnectionProtocol.WeatherLocation;
import models.ClientConnectionProtocol.WeatherLocations;

import org.geojson.LngLatAlt;
import org.geojson.Point;

import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.index;
import actors.WeatherActor;

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
			JsonNode coordNode = itemNode.get("coord");

			String name = itemNode.get("name").asText();
			double temp = kelvinToFahrenheit(mainNode.get("temp").asDouble());
			double minTemp = kelvinToFahrenheit(mainNode.get("temp_min")
					.asDouble());
			double maxTemp = kelvinToFahrenheit(mainNode.get("temp_max")
					.asDouble());
			Point point = new Point(coordNode.get("lon").asDouble(), coordNode
					.get("lat").asDouble());

			WeatherLocation weatherLocation = new WeatherLocation(name, temp,
					minTemp, maxTemp, point);

			weatherLocations.add(weatherLocation);
		}

		return new WeatherLocations(weatherLocations);
	}

	private static double kelvinToFahrenheit(double kelvin) {
		return (kelvin - 273.15) * 1.8000 + 32.00;
	}

}
