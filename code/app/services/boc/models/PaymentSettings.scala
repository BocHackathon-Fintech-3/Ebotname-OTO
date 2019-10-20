package services.boc.models

import play.api.libs.json.{Json, OFormat}

object PaymentSettings {
  implicit val jsonFormat: OFormat[PaymentSettings] = Json.format[PaymentSettings]
}

case class PaymentSettings(limit: Int = 99999999, currency: String = "EUR", amount: Int = 99999999)
