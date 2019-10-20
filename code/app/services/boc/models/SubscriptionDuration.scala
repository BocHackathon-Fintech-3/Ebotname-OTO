package services.boc.models

import play.api.libs.json.Json

object SubscriptionDuration {
  implicit val jsonFormat = Json.format[SubscriptionDuration]
}

case class SubscriptionDuration(startDate: String, endDate: String)
