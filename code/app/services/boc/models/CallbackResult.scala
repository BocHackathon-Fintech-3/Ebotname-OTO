package services.boc.models

import play.api.libs.json.Json

object CallbackResult {
  implicit val jsonFormat = Json.format[CallbackResult]
}

case class CallbackResult(redirectUrl: String, appToken: String, userToken: String, subscription: Subscription, selectedAccounts: Seq[Account])
