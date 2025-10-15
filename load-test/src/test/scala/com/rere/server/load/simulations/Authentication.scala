package com.rere.server.load.simulations

import com.rere.server.load.Endpoints._
import com.rere.server.load.model.AuthModes
import io.gatling.core.Predef._

class Authentication extends BaseSimulation {

  private val setupQuotaScenario = scenario("Create quota")
    .exec(postServerConfig)

  /**
   * Scenario for creating new accounts.
   */
  private val accountCreateScenario = scenario("Create account and logout")
    .exec(signup)
    .exec(getOwnData)
    .exec(getOwnQuotaReport(AuthModes.Access))
    .exec(logout)

  /**
   * Scenario for refreshing after logging out.
   */
  private val refreshScenario = scenario("Use refresh token to reauthenticate")
    .exec(signup)
    .exec(refresh)

  addScenario(accountCreateScenario, setupQuotaScenario)
  addScenario(refreshScenario)

  setup()

}
