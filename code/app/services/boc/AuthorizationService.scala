package services.boc

import javax.inject.Inject
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import services.boc.contracts.{AuthorizationServiceContract, BOCService}
import services.boc.models.BOCApiError
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

class AuthorizationService @Inject()(val boc: BOCWebClient, val db: DB)(
  implicit ec: ExecutionContext
) extends BOCService
  with LoggingSupport
  with AuthorizationServiceContract {
  override val bocEndpoint: String = "oauth2/token"

  /**
    * Redirect to Authorization Endpoint
    *
    * @param subscriptionId String
    * @param state          String
    * @return
    */
  def redirectToAuthorizationUrl(subscriptionId: String,
                                 state: String): Result = {
    Redirect(
      s"${boc.base_url}/${boc.authorize_url}",
      Map(
        "response_type" -> Seq("code"),
        "redirect_uri" -> Seq(boc.getReturnUrl),
        "scope" -> Seq("UserOAuth2Security"),
        "client_id" -> Seq(boc.client_id),
        "subscriptionid" -> Seq(subscriptionId),
        "state" -> Seq(state)
      )
    )
  }


  /**
    * Get Application Token
    */
  def getApplicationToken()(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, String]] = {
    boc
      .callForAuth(bocEndpoint)
      .post(
        Map(
          "grant_type" -> Seq("client_credentials"),
          "client_id" -> Seq(boc.client_id),
          "client_secret" -> Seq(boc.client_secret),
          "scope" -> Seq("TPPOAuth2Security"),
        )
      )
      .map(boc.unwrap).map {
      _.map { value =>

        val token = (value \ "access_token").as[String]

        db.setApplicationToken(token)
        token
      }
    }
  }

  /**
    * Exchange Access Code for User Token
    */
  def exchangeCodeForUserToken(
                                code: String
                              )(implicit ec: ExecutionContext): Future[Either[BOCApiError, String]] = {
    this.boc
      .callForAuth(bocEndpoint)
      .post(
        Map(
          "grant_type" -> Seq("authorization_code"),
          "client_id" -> Seq(boc.client_id),
          "client_secret" -> Seq(boc.client_secret),
          "scope" -> Seq("TPPOAuth2Security"),
          "code" -> Seq(code),
        )
      )
      .map { response =>
        boc.unwrap(response) match {
          case Right(value) => {
            val token = (value \ "access_token").as[String]

            db.setApplicationToken(token)

            Right(token)
          }
          case Left(err) => Left(err)
        }
      }
  }
}
