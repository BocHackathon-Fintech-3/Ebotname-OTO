package services.boc.contracts

import play.api.mvc.Result
import services.boc.models.BOCApiError

import scala.concurrent.{ExecutionContext, Future}

trait AuthorizationServiceContract { self: BOCService =>

  def redirectToAuthorizationUrl(subscriptionId: String, state: String): Result

  def getApplicationToken()(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, String]]

  def exchangeCodeForUserToken(code: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, String]]
}
