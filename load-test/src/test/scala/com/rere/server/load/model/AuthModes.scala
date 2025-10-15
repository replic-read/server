package com.rere.server.load.model

/**
 * Auth modes that define what kind of authentication a request will use.
 */
object AuthModes extends Enumeration {
  type AuthMode = Value

  /**
   * No authentication.
   */
  val None: AuthMode = Value

  /**
   * Authentication with an access-token.
   */
  val Access: AuthMode = Value

  /**
   * Authentication via basic-auth.
   */
  val Basic: AuthMode = Value

}
