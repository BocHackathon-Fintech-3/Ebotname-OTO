package services.boc.models

import play.api.libs.json.{JsObject, Json, OFormat}

object Subscription {
  implicit val jsonFormat: OFormat[Subscription] = Json.format[Subscription]

  def empty: JsObject = Json.obj(
    "accounts" -> Json.obj(
      "transactionHistory" -> true,
      "balance" -> true,
      "details" -> true,
      "checkFundsAvailability" -> true
    ),
    "payments" -> Json
      .obj("limit" -> 99999999, "currency" -> "EUR", "amount" -> 999999999)
  )
}

case class Subscription(subscriptionId: String,
                        status: String,
                        description: String,
                        selectedAccounts: Seq[SelectedAccount] = Seq.empty,
                        accounts: AccountSettings,
                        payments: PaymentSettings,
                        duration: Option[SubscriptionDuration] = None) {
  def isActive: Boolean = status == "ACTV"
}
