package de.jotschi.vertx;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.jotschi.vertx.openapi.OpenAPIGenerator;
import de.jotschi.vertx.router.ApiRouter;
import de.jotschi.vertx.router.impl.ApiRouterImpl;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

public class OpenAPITest {
	@Test
	public void testRouting() throws JsonProcessingException {
		ApiRouter api = createAPI();
		assertEquals("The root router", api.description());

		String yaml = OpenAPIGenerator.gen(api);
		System.out.println(yaml);
	}

	private ApiRouter createAPI() {
		ApiRouter root = new ApiRouterImpl(Vertx.vertx());
		root.description("The root router");
		root.route("/root1").method(HttpMethod.POST)
			.exampleRequest("application/json", "{ \"value\": \"The example request\"}", "The required request")
			.exampleResponse(OK, "text/plain", "The example response", "Regular response of this endpoint")
			.exampleResponse(BAD_REQUEST, "application/json", new JsonObject().put("test", "The example response"),
				"Regular response of this endpoint")
			.consumes("application/json")
			.produces("application/json");

		ApiRouter level1 = new ApiRouterImpl(Vertx.vertx());
		level1.route("/anotherRoute").method(HttpMethod.POST).consumes("application/json");

		ApiRouter level2 = new ApiRouterImpl(Vertx.vertx());
		level2.route("/onLevel3").description("Route on level 3").method(HttpMethod.POST).consumes("application/json");

		root.mountSubRouter("/test", level1);
		level1.mountSubRouter("/level2", level2);
		return root;
	}
}