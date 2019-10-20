package data.models

import play.api.libs.json.{Json, OFormat}

object $model;format="Camel"$ {
  implicit val jsonFormat: OFormat[$model;format="Camel"$] = Json.format[$model;format="Camel"$]
}

case class $model;format="Camel"$(name: String) {}
