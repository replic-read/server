package com.rere.server.load

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

/**
 * Collection of the endpoints used in scenarios.
 */
object Endpoints {

  /**
   * Request to POST /auth/login/.
   */
  val login: HttpRequestBuilder = http("Login with credentials")
    .post("/auth/login/")
    .body(ElFileBody("bodies/login.json"))
    .check(status is 200)
    .check(jmesPath("access_token").saveAs("accessToken"))

  /**
   * Request to POST /auth/signup/.
   */
  val signup: HttpRequestBuilder = http("Signup")
    .post("/auth/signup/")
    .body(ElFileBody("bodies/signup.json"))
    .queryParam("send_email", false)
    .check(status is 200)

  /**
   * Request to POST /auth/logout/.
   */
  val logout: HttpRequestBuilder = http("Logout from all devices")
    .post("/auth/logout/")
    .header("Authorization", "Bearer #{accessToken}")
    .queryParam("all", true)
    .check(status is 200)

}
