package com.github.rafaelsilvestri.simulation

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class SequenceMemcachedSimulation extends Simulation {

  val baseUrl: String = System.getProperty("baseurl", "http://localhost:8080")

  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(baseUrl)
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val scn1: ScenarioBuilder = scenario("Create sequence 'foo' with Memcached")
    .exec(http("Type foo")
      .post("/sequences/memcached/foo"))
    .pause(5)

  val scn2: ScenarioBuilder = scenario("Create sequence 'bar' with Memcached")
    .exec(http("Type bar")
      .post("/sequences/memcached/bar"))
    .pause(5)

  setUp(
    //scn1.inject(constantUsersPerSec(10).during(10)),
    //scn2.inject(constantUsersPerSec(10).during(10))
    scn1.inject(atOnceUsers(5)),
    scn2.inject(atOnceUsers(5))
  ).protocols(httpProtocol)

}