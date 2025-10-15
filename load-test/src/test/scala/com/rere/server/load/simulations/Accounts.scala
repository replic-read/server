package com.rere.server.load.simulations

import com.rere.server.load.Endpoints.{getAllAccountsFull, getAllAccountsPartial, signup}
import com.rere.server.load.Feeders
import io.gatling.core.Predef.scenario

class Accounts extends BaseSimulation {

  // Needed so we create accounts that wen can get in the latter two scenarios.
  private val createAccountsScenario = scenario("Create accounts")
    .exec(signup)

  /**
   * Scenario that gets all accounts.
   */
  private val getAllAccountsPartialScenario = scenario("Get all partial accounts")
    .feed(Feeders.accountSorts)
    .feed(Feeders.accountState)
    .feed(Feeders.sortDirections)
    .exec(getAllAccountsPartial)

  /**
   * Scenario that gets all accounts.
   */
  private val getAllAccountsFullScenario = scenario("Get all full accounts")
    .feed(Feeders.accountSorts)
    .feed(Feeders.accountState)
    .feed(Feeders.sortDirections)
    .exec(getAllAccountsFull)

  addScenario(createAccountsScenario)
  addScenario(getAllAccountsPartialScenario)
  addScenario(getAllAccountsFullScenario)

  setup()

}
