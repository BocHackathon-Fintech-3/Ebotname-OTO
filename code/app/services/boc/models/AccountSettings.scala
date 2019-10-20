package services.boc.models

import play.api.libs.json.{Json, OFormat}

object AccountSettings {
  implicit val jsonFormat: OFormat[AccountSettings] = Json.format[AccountSettings]
}

case class AccountSettings(transactionHistory: Boolean = true, balance: Boolean = true, details: Boolean = true, checkFundsAvailability: Boolean = true) {}
