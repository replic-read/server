package com.rere.server.load.simulations

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

/**
 * BAse simulation class that provides shared scenarios.
 */
abstract class BaseSimulation extends Simulation {

  /**
   * The usual protocol that connects to a localhost-running instance.
   */
  protected val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

}
