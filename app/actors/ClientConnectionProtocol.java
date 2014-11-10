package actors;

import java.util.List;

import org.geojson.Point;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * TODO this will become a common model shared between server and client.
 *
 */
public abstract class ClientConnectionProtocol {

	/**
	 * Events to/from the client side
	 */
	@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME, property = "event")
	@JsonSubTypes({
			@JsonSubTypes.Type(value = User.class, name = "user"),
			@JsonSubTypes.Type(value = WeatherLocation.class, name = "weather-location"),
			@JsonSubTypes.Type(value = WeatherLocations.class, name = "weather-locations"), })
	public static abstract class ClientEvent {
		private ClientEvent() {
		}
	}

	/**
	 * Event sent from the client when they have moved
	 */
	public static class User extends ClientEvent {
		private final Point position;

		@JsonCreator
		public User(@JsonProperty("position") Point position) {
			this.position = position;
		}

		public Point getPosition() {
			return position;
		}
	}

	/**
	 * Event sent from the client when they have moved
	 */
	public static class WeatherLocations extends ClientEvent {
		private final List<WeatherLocation> weatherLocations;

		@JsonCreator
		public WeatherLocations(
				@JsonProperty("weatherLocations") List<WeatherLocation> weatherLocations) {
			this.weatherLocations = weatherLocations;
		}

		public List<WeatherLocation> getWeatherLocations() {
			return weatherLocations;
		}
	}

	/**
	 * Event sent from the client that gives a weather location for an area.
	 */
	public static class WeatherLocation extends ClientEvent {
		private final String name;
		private final double temp;
		private final double minTemp;
		private final double maxTemp;

		@JsonCreator
		public WeatherLocation(@JsonProperty("name") String name,
				@JsonProperty("temp") double temp,
				@JsonProperty("minTemp") double minTemp,
				@JsonProperty("maxTemp") double maxTemp) {
			this.name = name;
			this.temp = temp;
			this.minTemp = minTemp;
			this.maxTemp = maxTemp;
		}

		public String getName() {
			return name;
		}

		public double getTemp() {
			return temp;
		}

		public double getMinTemp() {
			return minTemp;
		}

		public double getMaxTemp() {
			return maxTemp;
		}

	}

}
