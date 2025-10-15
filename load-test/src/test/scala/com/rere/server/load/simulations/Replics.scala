package com.rere.server.load.simulations

import com.rere.server.load.Endpoints._
import com.rere.server.load.Feeders
import com.rere.server.load.model.AuthModes
import io.gatling.core.Predef.scenario

class Replics extends BaseSimulation {

  /**
   * Sets up a replic quota.
   */
  private val setupQuotaScenario = scenario("Create quota")
    .exec(postServerConfig)

  /**
   * Scenario that creates replics and downloads their info and content.
   */
  private val createReplicScenario = scenario("Create replic and view")
    .exec(getOwnQuotaReport(AuthModes.Basic))
    .feed(Feeders.mediaModes)
    .feed(Feeders.expirationsInFuture)
    .exec(postReplic)
    .exec(getSpecificReplic)
    .exec(getReplicContent)

  /**
   * Scenario that gets all replics with various filters and sorters enabled.
   */
  private val getAllReplicsScenario = scenario("Get all replics")
    .feed(Feeders.replicStates)
    .feed(Feeders.replicSorts)
    .feed(Feeders.sortDirections)
    .exec(getAllReplics)


  addScenario(createReplicScenario, setupQuotaScenario)
  addScenario(getAllReplicsScenario)

  setup()

}
