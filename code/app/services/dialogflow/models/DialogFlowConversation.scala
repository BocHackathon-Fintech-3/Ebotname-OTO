package services.dialogflow.models

import play.api.libs.json._

object DialogFlowConversation {
  implicit val jsonReads = new Reads[DialogFlowConversation] {
    override def reads(json: JsValue): JsResult[DialogFlowConversation] = {
      val queryResult = (json \ "queryResult").as[JsObject]
      val message = (queryResult \ "queryText").asOpt[String]
      val action = (queryResult \ "action").asOpt[String]
      val params = (queryResult \ "parameters").as[JsObject]
      val raw = json.as[JsObject]
      val contexts = (queryResult \ "outputContexts").asOpt[Seq[Context]]

      JsSuccess(DialogFlowConversation(message, action, params, raw))
    }
  }
}
case class DialogFlowConversation(message: Option[String] = None,
                                  action: Option[String] = None,
                                  params: JsObject = Json.obj(),
                                  raw: JsObject = Json.obj()) {}
