package com.rere.server.load

import io.gatling.core.Predef.{configuration, defaultJsonParsers, jsonFile}
import io.gatling.core.feeder.{Feeder, FeederBuilder, FileBasedFeederBuilder}

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Contains misc feeders.
 */
object Feeders {

  /**
   * A feeder containing the media modes.
   */
  val mediaModes: FeederBuilder = jsonFile("data/media_modes.json")
    .random

  /**
   * A feeder containing the replic sort modes.
   */
  val replicSorts: FeederBuilder = jsonFile("data/replic_sorts.json")
    .random

  /**
   * A feeder containing the replic states.
   */
  val replicStates: FeederBuilder = jsonFile("data/replic_states.json")
    .random

  /**
   * A feeder containing the sort directions.
   */
  val sortDirections: FeederBuilder = jsonFile("data/sort_directions.json")
    .random

  /**
   * A feeder containing the various states a report can be changed into.
   */
  val reportStates: FileBasedFeederBuilder[Any] = jsonFile("data/report_states.json")
    .random

  /**
   * A feeder containing the various report sort modes.
   */
  val reportSorts: FileBasedFeederBuilder[Any] = jsonFile("data/report_sorts.json")
    .random

  /**
   * A feeder containing the various states an account can be in.
   */
  val accountState: FileBasedFeederBuilder[Any] = jsonFile("data/account_states.json")
    .random

  /**
   * A feeder containing the various account sort modes.
   */
  val accountSorts: FileBasedFeederBuilder[Any] = jsonFile("data/account_sorts.json")
    .random

  /**
   * An infinite feeder that always returns the current timestamp as an expiration.
   */
  val expirationsInFuture: Iterator[Map[String, String]] = Iterator.continually {
    Map("expiration" -> Instant.now().plus(1, ChronoUnit.DAYS).toString)
  }

}
