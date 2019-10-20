package utils

import play.api.mvc.Result
import play.api.mvc.Results.Redirect

object DialogFlowAuth {


  /**
    *url#access_token=4/P7q7W91&token_type=Bearer&expires_in=3600
    */
  def redirectToSuccessUrl(url: String, token: String, state: String): Result =
    Redirect(
      s"$url#access_token=$token&token_type=Bearer&expires_in=3600&state=${state}"
    )

  /**
    *url#error=access_denied
    */
  def redirectToFailedUrl(url: String): Result =
    Redirect(s"$url#error=access_denied")
}
