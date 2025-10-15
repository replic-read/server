package com.rere.server.load.simulations

import com.rere.server.load.Endpoints.{changeReportState, getAllReports, getReport, postReplic, postReport}
import com.rere.server.load.Feeders
import io.gatling.commons.validation.SuccessWrapper
import io.gatling.core.Predef.scenario

import java.util.UUID

class Reports extends BaseSimulation {

  /**
   * Scenario for initializing the replic for which will be created reports.
   */
  private val createReplicInit = scenario("Create initial replic")
    .feed(Feeders.mediaModes)
    .feed(Feeders.expirationsInFuture)
    .exec(postReplic)
    .exec { s =>
      replicId = UUID.fromString(s("replicId").as[String])
      s.success
    }
  /**
   * Scenario that creates a report and then gets it.
   */
  private val createReportScenario = scenario("Create report and review")
    .exec { s => s.set("replicId", replicId.toString).success }
    .exec(postReport)
    .exec(getReport)
    .feed(Feeders.reportStates)
    .exec(changeReportState)
  private val getAllReportsScenario = scenario("Get all reports")
    .feed(Feeders.reportSorts)
    .feed(Feeders.sortDirections)
    .exec(getAllReports)
  private var replicId: UUID = _

  addScenario(createReportScenario, createReplicInit)

  addScenario(getAllReportsScenario)

  setup()


}
