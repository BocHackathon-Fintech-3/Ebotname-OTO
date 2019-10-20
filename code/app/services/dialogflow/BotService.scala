package services.dialogflow

import javax.inject.Inject
import play.api.libs.json.{JsObject, Json}
import services.boc.BOCApi
import services.dialogflow.models.DialogFlowConversation

import scala.concurrent.{ExecutionContext, Future}

class BotService @Inject()(bocApi: BOCApi)(implicit ec: ExecutionContext) {

  def listen(conversation: DialogFlowConversation): Future[JsObject] = {
    Future(Json.obj())
  }
}
