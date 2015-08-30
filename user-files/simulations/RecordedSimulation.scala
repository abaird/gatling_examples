
import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	val httpProtocol = http
		.baseURL("http://computer-database.gatling.io")
		.inferHtmlResources()

	val headers_1 = Map("Accept" -> "image/png,image/*;q=0.8,*/*;q=0.5")

    val uri1 = "http://computer-database.gatling.io"

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.get("/")
			.resources(http("request_1")
			.get(uri1 + "/favicon.ico")
			.headers(headers_1)
			.check(status.is(404)),
            http("request_2")
			.get(uri1 + "/favicon.ico")
			.check(status.is(404))))

	setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
}