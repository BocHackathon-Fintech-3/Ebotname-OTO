package services.dialogflow.models

import play.api.libs.json.{
  Format,
  Json,
  JsResult,
  JsString,
  JsSuccess,
  JsValue,
  OFormat
}

object Session {
  implicit val jsonFormat = new Format[Session] {
    override def writes(o: Session): JsValue = JsString(o.toString)

    override def reads(json: JsValue): JsResult[Session] = {
      val parts = json.as[String].split("/")
      val name = parts(4)
      val project = parts(1)
      JsSuccess(Session(name, project))
    }
  }

  def fromContext(ctx: Context): Session = {
    val parts = ctx.name.split("/")
    val name = parts(4)
    val project = parts(1)
    Session(name, project)
  }
  def fromString(str: String): Session = {
    val parts = str.split("/")
    val name = parts(4)
    val project = parts(1)
    Session(name, project)
  }

}

case class Session(name: String, project: String) {
  override def toString: String = s"projects/$project/agent/sessions/$name"
}
