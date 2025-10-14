package com.rere.server.load.simulations

import com.rere.server.load.Endpoints.signup
import io.gatling.core.Predef._

class AuthenticationSimulation extends BaseSimulation {

  /**
   * Scenario for creating new accounts.
   */
  private val accountCreateScenario = scenario("Create accounts")
    .exec(signup)

  setUp(accountCreateScenario.inject(atOnceUsers(1000)))
    .protocols(httpProtocol)

}
