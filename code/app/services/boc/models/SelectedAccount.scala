package services.boc.models

import play.api.libs.json.{Json, OFormat}

object SelectedAccount {
  implicit val jsonFormat: OFormat[SelectedAccount] = Json.format[SelectedAccount]
}

case class SelectedAccount(accountId: String)
