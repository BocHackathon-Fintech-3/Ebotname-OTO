package services.dialogflow.models

import java.util.UUID

import play.api.libs.json.{JsObject, Json, OFormat}

object Context {
  implicit val jsonFormat: OFormat[Context] = Json.format[Context]

  protected val sessionContextKey: String = "sessionvars"
  def createSession(session: Session,
                    params: JsObject,
                    lifespanCount: Int = 50): Context = {
    Context(
      s"projects/${session.project}/agent/sessions/${session.name}/contexts/$sessionContextKey",
      Some(lifespanCount),
      Some(params)
    )
  }

  def create(session: Session,
             contextName: String,
             lifeSpan: Option[Int] = Some(1)): Context = {
    Context(
      s"projects/${session.project}/agent/sessions/${session.name}/contexts/$contextName",
      lifeSpan
    )
  }

}

case class Context(name: String,
                   lifespanCount: Option[Int] = None,
                   parameters: Option[JsObject] = None) {

  val session: Session = Session.fromString(name)
  val getContextName: String = name.split("/").last
  val hasIntent: Boolean = getContextName.contains("-") && lifespanCount
    .contains(1)
  val getEventName: String = s"$getContextName-event"
  val getContextFallbackEvent: String = s"$getContextName-fallback-event"

  val isSessionContext: Boolean = getContextName == Context.sessionContextKey

  def suggestedLocations: Seq[String] =
    parameters
      .flatMap { j =>
        (j \ "suggested_locations").asOpt[Seq[String]]
      }
      .getOrElse(Seq.empty)

  def appendSuggestedLocations(locations: Seq[String]): Context =
    appendParameters(Json.obj("suggested_locations" -> locations))

  def appendParameters(params: JsObject): Context =
    copy(parameters = Some(parameters.getOrElse(Json.obj()) ++ params))

  def getParameters: JsObject =
    parameters
      .map { p =>
        val items = p.value.filterKeys(k => !k.contains("."))
        JsObject(items)
      }
      .getOrElse(Json.obj())
}
