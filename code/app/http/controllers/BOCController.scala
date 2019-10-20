package http.controllers

import http.api.ApiController
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsObject, Json, JsValue}
import play.api.mvc._
import services.boc.{BOCApi, DB}
import services.boc.exceptions.InvalidAuthCodeException
import services.boc.models.{Account, BOCApiError, CallbackResult}
import utils.DialogFlowAuth._

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  * Auth api controller for Play Scala
  */
class BOCController @Inject()(bocApi: BOCApi, db: DB, cc: ControllerComponents)(
  implicit ec: ExecutionContext
) extends AbstractController(cc)
    with ApiController
    with I18nSupport {

  /**
    * Invoke the Login Flow for Bank of Cyprus
    *
    * @return
    */
  def login: Action[AnyContent] = Action.async { implicit request =>
    val redirectUrl =
      request
        .getQueryString("redirect_uri")
        .getOrElse(bocApi.subscription.boc.getSuccessUrl)
    val state =
      request.getQueryString("state").getOrElse("badState")
    bocApi.authorizeApplication(redirectUrl, state)
  }

  /**
    * The Return Url of
    */
  def callback: Action[AnyContent] = Action.async { implicit request =>
    val codeFromRequest = request
      .getQueryString("code")
      .getOrElse(throw new InvalidAuthCodeException)
    val stateFromGoogle = request.getQueryString("state").getOrElse("")
    bocApi.handleBocCallback(codeFromRequest).map {
      case Right(result: CallbackResult) =>
        redirectToSuccessUrl(
          result.redirectUrl,
          result.appToken,
          stateFromGoogle
        )
      case Left(_) => BadRequest
    }
  }

  def success: Action[Unit] = ApiAction { implicit request =>
    db.getCallbackResult.flatMap { result =>
      for {
        accountBalances: Seq[Option[Account]] <- Future
          .sequence(result.selectedAccounts.map { acc =>
            bocApi.accounts
              .balance(
                result.appToken,
                result.subscription.subscriptionId,
                acc.accountId
              )
              .map { _.getOrElse(Json.arr()).as[Seq[Account]].headOption }
          })
        accountStatement: Seq[JsValue] <- Future
          .sequence(result.selectedAccounts.map { acc =>
            bocApi.accounts
              .statement(
                result.appToken,
                result.subscription.subscriptionId,
                acc.accountId
              )
              .map { _.getOrElse(Json.arr()) }
          })
        accounts: Seq[Account] = accountBalances.flatten
        resultItem = result.copy(selectedAccounts = accounts)
        updateDb <- db.setCallbackResult(resultItem)
        respond <- ok(accountStatement)
      } yield (respond)

    }

  }
}
