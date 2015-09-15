package pizza

import scala.concurrent.duration._
import scala.util.Random

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.core.validation._
import scala.concurrent.forkjoin.ThreadLocalRandom

class Pizzerias extends Simulation {

	val httpProtocol = http
		.baseURL("http://pizza-dev.elasticbeanstalk.com")
		.inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.ico"""), WhiteList())

  val uri1 = "http://pizza-dev.elasticbeanstalk.com"

	val scn = scenario("Pizzerrias list all")
		.exec(http("request all")
			.get("/api/v1/pizzerias"))
		.exec(http("pizza place 1")
			.get("/api/v1/pizzerias/1"))
		.exec(http("pizza place 2")
			.get("/api/v1/pizzerias/2"))

	setUp(scn.inject(atOnceUsers(10))).protocols(httpProtocol)
}

class Pizzerias2 extends Simulation {

  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val scnA = scenario("Pizza Place 1")
    .exec(http("get Pizza Place 1")
      .get("/api/v1/pizzerias/1"))

  val scnB = scenario("Pizza Place 2")
    .exec(http("get Pizza Place 2")
      .get("/api/v1/pizzerias/2"))

  setUp(scnA.inject(constantUsersPerSec(10) during(30 seconds)),
        scnB.inject(constantUsersPerSec(10) during(30 seconds))).protocols(httpProtocol)
}

class PizzeriasRandom extends Simulation {

  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val feeder = Iterator.continually(Map("id" ->
    (Random.nextInt(133)))) 

  val scnA = scenario("Pizza Place Random")
    .feed(feeder)
    //.exec((session) => 
  //     {println(session("id").as[String])
   //     session})
    .exec(http("get Pizza Place random")
      .get("/api/v1/pizzerias/${id}"))
  
  setUp(scnA.inject(rampUsersPerSec(1) to 10 during(10 seconds),
                    constantUsersPerSec(10) during(60 seconds))).protocols(httpProtocol)
        
}

class PizzeriasDual extends Simulation {

  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val feeder = Iterator.continually(Map("id" ->
    (Random.nextInt(133)))) 

  val scnR = scenario("Pizza Place Random")
    .feed(feeder)
    .exec(http("get Pizza Place 1")
      .get("/api/v1/pizzerias/${id}"))

  val scnAll = scenario("Pizza Place List")
    .exec(http("get all Pizza Places")
      .get("/api/v1/pizzerias"))

  setUp(scnR.inject(constantUsersPerSec(2) during(60 seconds)),
        scnAll.inject(constantUsersPerSec(2) during(60 seconds))).protocols(httpProtocol)
}

class PizzeriasQuery extends Simulation {
  
  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val scn = scenario("Pizza Place Searches")
    .exec(http("search for pizza place")
      .get("/api/v1/properties/search")
      .queryParam("city", "Oklahoma City"))

  setUp(scn.inject(constantUsersPerSec(2) 
                   during(10 seconds)))
    .protocols(httpProtocol)
}

class PizzeriasQueryWithCSV extends Simulation {
  
  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val searches = csv("pizza_search.csv").circular

  val scn = scenario("Pizza Place Searches")
    .feed(searches)
    .exec(http("search for pizza place")
      .get("/api/v1/properties/search")
      .queryParam("${property}","${value}"))

  setUp(scn.inject(constantUsersPerSec(2) 
    during(10 seconds)))
    .protocols(httpProtocol)
}

class PizzeriasQueryWithCheck extends Simulation {
  
  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val searches = csv("pizza_search.csv").circular

  val scn = scenario("Pizza Place Searches")
    .feed(searches)
    .exec(http("search for pizza place")
      .get("/api/v1/properties/search")
      .queryParam("${property}","${value}")
      .check(bodyString.not("[]")))

  setUp(scn.inject(constantUsersPerSec(2) 
    during(10 seconds)))
    .protocols(httpProtocol)
}

class PizzeriasJsonQuery extends Simulation {

  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val searches = jsonUrl("http://pizza-dev.elasticbeanstalk.com/api/v1/pizzerias")

  val scn = scenario("Pizza Place JSON Searches")
    .feed(searches)
    .exec(http("search for pizza stuff by json")
      .get("/api/v1/properties/search?city=${properties.city}")
      .check(bodyString.not("[]")))

  setUp(scn.inject(constantUsersPerSec(20) during(2 seconds))).protocols(httpProtocol)
}

class PizzeriasJsonQueryAdvanced extends Simulation {
  // This example only chooses 1 property for the entire simulation

  val httpProtocol = http
    .baseURL("http://pizza-dev.elasticbeanstalk.com")

  val searches = jsonUrl("http://pizza-dev.elasticbeanstalk.com/api/v1/pizzerias")

  val attr = Array("city","pizzeria","website","address")

  val property = Random.shuffle(attr.toList).head

  val scn = scenario("Pizza Place JSON Searches")
    .feed(searches)
    .exec(http("search for pizza stuff by json")
      .get(s"/api/v1/properties/search?$property=$${properties.$property}")
      .check(bodyString.not("[]")))

  setUp(scn.inject(constantUsersPerSec(20) during(2 seconds))).protocols(httpProtocol)
}



