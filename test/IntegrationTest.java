import static controllers.routes.ref.Application;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.callAction;
import static play.test.Helpers.running;
import static play.test.Helpers.status;
import static play.test.Helpers.testServer;

import org.geojson.Point;
import org.junit.Test;

import play.libs.Json;
import play.mvc.Http.Status;
import play.mvc.Result;
import play.test.FakeRequest;
import play.test.Helpers;

import com.boarbeard.weathersbetter.ClientConnectionProtocol;
import com.boarbeard.weathersbetter.ClientConnectionProtocol.WeatherLocations;
import com.fasterxml.jackson.databind.JsonNode;

public class IntegrationTest {

	@Test
	public void testValidPostWeather() {
		running(testServer(3333), new Runnable() {
			public void run() {
				JsonNode jsonNode = Json
						.toJson(new ClientConnectionProtocol.User(new Point(
								-115, 36)));

				FakeRequest fakeRequest = new FakeRequest()
						.withJsonBody(jsonNode);
				Result result = callAction(Application.weather(), fakeRequest);

				int responseCode = status(result);
				assertThat(responseCode).isEqualTo(OK);

				JsonNode bodyNode = Json.parse(Helpers.contentAsString(result));
				WeatherLocations weatherLocations = Json.fromJson(bodyNode,
						WeatherLocations.class);

				assertThat(weatherLocations.getWeatherLocations()).isNotEmpty();
			}
		});
	}

	@Test
	public void testinvalidLatLongPostWeather() {
		running(testServer(3333), new Runnable() {
			public void run() {

				// Use an unearthly coordinate
				JsonNode jsonNode = Json
						.toJson(new ClientConnectionProtocol.User(new Point(
								200, -200)));

				FakeRequest fakeRequest = new FakeRequest()
						.withJsonBody(jsonNode);
				Result result = callAction(Application.weather(), fakeRequest);

				int responseCode = status(result);
				assertThat(responseCode)
						.isEqualTo(Status.INTERNAL_SERVER_ERROR);
			}
		});
	}
}
