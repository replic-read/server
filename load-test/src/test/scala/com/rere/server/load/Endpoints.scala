package com.rere.server.load

import com.rere.server.load.model.AuthModes
import com.rere.server.load.model.AuthModes.{Access, AuthMode, Basic}
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
    .check(jsonPath("$.access_token").saveAs("accessToken"))
    .check(jsonPath("$.refresh_token").saveAs("refreshToken"))

  /**
   * Request to POST /auth/signup/.
   */
  val signup: HttpRequestBuilder = http("Signup")
    .post("/auth/signup/")
    .body(ElFileBody("bodies/signup.json"))
    .queryParam("send_email", false)
    .check(status is 200)
    .check(jsonPath("$.access_token").saveAs("accessToken"))
    .check(jsonPath("$.refresh_token").saveAs("refreshToken"))

  /**
   * Request to POST /auth/logout/.
   */
  val logout: HttpRequestBuilder = withAuth(AuthModes.Access, http("Logout from all devices")
    .post("/auth/logout/")
    .queryParam("all", true)
    .check(status is 200))

  /**
   * Request to GET /me/.
   */
  val getOwnData: HttpRequestBuilder = withAuth(AuthModes.Access, http("Get account data")
    .get("/me/")
    .check(status is 200))
  /**
   * Request to POST /auth/refresh/.
   */
  val refresh: HttpRequestBuilder = withAuth(AuthModes.None, http("Authenticate with refresh token")
    .post("/auth/refresh/")
    .body(ElFileBody("bodies/refresh.json"))
    .check(status is 200)
    .check(jsonPath("$.access_token").saveAs("accessToken"))
    .check(jsonPath("$.refresh_token").saveAs("refreshToken")))
  /**
   * Request to POST/replics/
   */
  val postReplic: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Create a replic")
    .post("/replics/")
    .header("Content-Type", "multipart/form-data")
    .bodyPart(ElFileBodyPart("request_body", "parts/create_replic.json")
      .contentType("application/json"))
    .bodyPart(ElFileBodyPart("file", "content/replic_content.html")
      .contentType("text/html")
      .fileName("content.html"))
    .check(status is 200)
    .check(jsonPath("$.id").saveAs("replicId")))
  /**
   * Request to GET /replics/{id}/content/
   */
  val getReplicContent: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get replic content")
    .get("/replics/#{replicId}/content/")
    .header("Accept", "text/html")
    .check(status is 200))
  /**
   * Request to GET /replics/
   */
  val getSpecificReplic: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get replic info")
    .get("/replics/")
    .queryParam("replic_id", "#{replicId}")
    .check(status is 200)
    .check(jsonPath("$").count is "1"))
  /**
   * Request to GET /replics/.
   */
  val getAllReplics: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get all replics")
    .get("/replics/")
    .queryParam("filter", "#{replicState}")
    .queryParam("filter", "#{replicState}")
    .queryParam("sort", "#{replicSort}")
    .queryParam("direction", "#{sortDirection}")
    .queryParam("query", "#{randomAlphanumeric(1)}"))
  /**
   * Request to GET /accounts/partial.
   */
  val getAllAccountsPartial: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get all accounts partial")
    .get("/accounts/partial/")
    .queryParam("sort", "#{accountSort}")
    .queryParam("direction", "#{sortDirection}")
    .queryParam("query", "#{randomAlphanumeric(1)}"))
  /**
   * Request to GET /accounts/full/.
   */
  val getAllAccountsFull: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get all accounts full")
    .get("/accounts/full/")
    .queryParam("filter", "#{accountState}")
    .queryParam("filter", "#{accountState}")
    .queryParam("sort", "#{accountSort}")
    .queryParam("direction", "#{sortDirection}")
    .queryParam("query", "#{randomAlphanumeric(1)}"))
  /**
   * Request to GET /reports/.
   */
  val getAllReports: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get all reports")
    .get("/reports/")
    .queryParam("sort", "#{reportSort}")
    .queryParam("direction", "#{sortDirection}")
    .queryParam("query", "#{randomAlphanumeric(1)}"))
  /**
   * Request to POST /reports/.
   */
  val postReport: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Create a report")
    .post("/reports/")
    .queryParam("replic_id", "#{replicId}")
    .body(ElFileBody("bodies/create_report.json"))
    .check(status is 200)
    .check(jsonPath("$.id").saveAs("reportId")))
  /**
   * Request to GET /reports/.
   */
  val getReport: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Get a report")
    .get("/reports/")
    .queryParam("report_id", "#{reportId}")
    .check(status is 200))
  /**
   * Request to PUT /report/{id}/.
   */
  val changeReportState: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Change report state")
    .put("/reports/#{reportId}/")
    .queryParam("state", "#{reportState}")
    .check(status is 200))
  /**
   * Request to POST /server-config/.
   */
  val postServerConfig: HttpRequestBuilder = withAuth(AuthModes.Basic, http("Set server config")
    .put("/server-config/")
    .body(StringBody(
      """
        |{
        |  "create_replic_group": "all",
        |  "access_replic_group": "all",
        |  "create_report_group": "all",
        |  "maximum_expiration_period": "P1Y3D",
        |  "replic_limit_period": "P1M3D",
        |  "replic_limit_count": 3,
        |  "allow_signup": true
        |}
        |""".stripMargin))
    .check(status is 200))

  /**
   * Request to GET /me/quota/.
   */
  def getOwnQuotaReport(authMode: AuthMode): HttpRequestBuilder = withAuth(authMode, http("Get quota report")
    .get("/me/quota/")
    .check(status is 200))

  private def withAuth(authMode: AuthMode, http: HttpRequestBuilder): HttpRequestBuilder =
    authMode match {
      case AuthModes.None => http
      case Access => http.header("Authorization", "Bearer #{accessToken}")
      case Basic => http.basicAuth("load-test-user-#{randomUuid()}", "#{randomUuid()}")
    }

}
