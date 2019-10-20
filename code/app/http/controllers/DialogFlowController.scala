package http.controllers

import http.api.{ApiController, ApiError}
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, JsValue}
import play.api.mvc.{AbstractController, Action, ControllerComponents}
import services.boc.DB
import services.dialogflow.{AssistantResponse, BotService}
import services.dialogflow.models.DialogFlowConversation

import scala.concurrent.ExecutionContext

/**
  *
  * DialogFlow api controller for Play Scala
  */
class DialogFlowController @Inject()(
  db: DB,
  bot: BotService,
  cc: ControllerComponents
)(implicit ec: ExecutionContext)
    extends AbstractController(cc)
    with ApiController
    with I18nSupport {

  /**
    * Get a DialogFlow Response
    *
    * @return
    */
  def handle: Action[JsValue] = ApiActionWithBody { implicit request =>
    readFromRequest[DialogFlowConversation] { conv =>
      println(conv.toString())
      db.getCallbackResult
      conv.action match {
        case Some("account.select") =>
          ok(
            AssistantResponse.accounts()
//            AssistantResponse.fulfillmentList(
//              "hey there",
//              Seq(
//                AssistantResponse.getListItem("abc", "Super", "description"),
//                AssistantResponse.getListItem("abcd", "Super", "description")
//              )
//            )
          ).recover {
            case e => ApiError.errorBadRequest(e.getMessage)
          }
        case _ => ok(Json.obj())
      }
    }
  }

}
