package services.boc.models

import play.api.libs.json.Json

object Balance {
  implicit val jsonFormat = Json.format[Balance]
}

case class Balance(amount: Float, balanceType: String)
