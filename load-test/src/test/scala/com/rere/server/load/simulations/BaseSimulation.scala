package com.rere.server.load.simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.collection.mutable


/**
 * Base simulation class that provides shared scenarios.
 */
abstract class BaseSimulation extends Simulation {

  /**
   * The usual protocol that connects to a localhost-running instance.
   */
  protected val httpProtocol: HttpProtocolBuilder = http
    .baseUrl(System.getProperty("baseUrl"))
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")

  private val scenarios: mutable.Buffer[(ScenarioBuilder, ScenarioBuilder)] = mutable.Buffer()

  /**
   * Adds a new scenario.
   *
   * @param test The actual test scenario
   * @param init An optional scenario to be added before the test.
   */
  protected def addScenario(test: ScenarioBuilder, init: ScenarioBuilder): Unit = {
    scenarios.append((test, init))
  }

  /**
   * Adds a new scenario.
   *
   * @param test The test scenario
   */
  protected def addScenario(test: ScenarioBuilder): Unit = addScenario(test, null)

  /**
   * Delegates te setup and configures the scenarios.
   */
  protected def setup(): Unit = {
    val popBuilders = mutable.Buffer[PopulationBuilder]()
    for ((test, init) <- scenarios) {
      if (init != null) {
        popBuilders += configureInitialScenario(init)
          .andThen(configureTestScenario(test))
      } else {
        popBuilders += configureTestScenario(test)
      }
    }

    setUp(popBuilders.toArray: _*)
      .protocols(httpProtocol)
  }

  protected def configureInitialScenario(scenario: ScenarioBuilder): PopulationBuilder = {
    scenario.inject(atOnceUsers(1))
  }

  // Currently only uses a capacity-test approach.
  protected def configureTestScenario(scenario: ScenarioBuilder): PopulationBuilder = {
    scenario.inject(
      incrementUsersPerSec(1)
        .times(13)
        .eachLevelLasting(10)
        .startingFrom(1)
    )
  }

}
