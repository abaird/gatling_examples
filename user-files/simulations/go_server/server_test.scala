package goServer

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class GoServer extends Simulation {

  object Json {

    val json_simple = exec(http("json")
       .get("/json"))

    val json_1k = exec(http("json")
       .get("/json1k"))

    val json_10k = exec(http("json")
       .get("/json10k"))
  }

  val httpConf = http
    .baseURL("http://localhost:8080")
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn1 = scenario("load_test").exec(Json.json_simple)

  val scn2 = scenario("load_test").exec(Json.json_1k) 

  val scn3 = scenario("load_test").exec(Json.json_10k) 

  setUp(
    scn2.inject(atOnceUsers(10))
  ).protocols(httpConf)
}

class LoadTest extends Simulation {
  setUp(
    scenario("load_test")
    .exec(http("json")
    .get("http://localhost:8080/json"))
    .inject(atOnceUsers(10))
  ).protocols(http)
}
