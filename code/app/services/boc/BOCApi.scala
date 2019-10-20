package services.boc

import http.api.ApiError
import javax.inject.Inject
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import services.boc.exceptions.{InvalidApplicationTokenException, InvalidSubscriptionException, InvalidUserTokenException}
import services.boc.models.{Account, BOCApiError, CallbackResult, Subscription}
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

class BOCApi @Inject()(
                        db: DB,
                        auth: AuthorizationService,
                        val subscription: SubscriptionService,
                        val payments: PaymentService,
                        val accounts: AccountService
                      )(implicit ec: ExecutionContext)
  extends LoggingSupport {

  /**
    *
    */
  def authorizeApplication(redirectUrl: String,
                           state: String): Future[Result] = {
    for {
      saveRedirectUrl <- db.setRedirectUrl(redirectUrl)
      tokenResponse <- auth.getApplicationToken
      subscriptionResponse <- tokenResponse match {
        case Right(token) => {
          info("creating Subscription")
          this.subscription.create(token)
        }
        case Left(err) => Future(Left(err))
      }
      redirectUserToBOC: Result <- subscriptionResponse match {
        case Right(subscription) => {
          val redirectResponse = Future(
            auth.redirectToAuthorizationUrl(subscription.subscriptionId, state)
          )
          db.setSubscriptionId(subscription.subscriptionId)

          redirectResponse
        }
        case Left(err) =>
          Future.failed(err.throwable)
      }
    } yield (redirectUserToBOC)
  }

  /**
    * handle the callback from Bank of Cyprus
    */
  def handleBocCallback[T](codeFromRequest: String): Future[Either[BOCApiError, CallbackResult]] = {
    for {
      appToken <- db.getApplicationToken.map {
        _.getOrElse(throw new InvalidApplicationTokenException)
      }
      subscriptionIdInProgress <- db.getSubscriptionId
      userToken <- auth.exchangeCodeForUserToken(codeFromRequest).map {
        _.toOption.getOrElse(throw new InvalidUserTokenException)
      }
      subscriptionInProgress: Subscription <- subscription
        .get(subscriptionIdInProgress, appToken)
        .map {
          _.getOrElse(throw new InvalidSubscriptionException)
        }
      activeSubscription <- subscription
        .update(subscriptionInProgress, userToken)
        .map {
          _.toOption.getOrElse(throw new InvalidSubscriptionException)
        }
      _ <- db.setActiveSubscription(activeSubscription)
      selectedAccounts: Either[BOCApiError, JsValue] <- accounts.get(
        appToken,
        activeSubscription.subscriptionId
      )
      redirectUrl: String <- db.getRedirectUrl
      respond: Either[BOCApiError, CallbackResult] <- Future(selectedAccounts.map { selectedAccountResponse =>

        val selectedAccountItems = selectedAccountResponse.as[Seq[Account]]

        val result = CallbackResult(redirectUrl, appToken, userToken, activeSubscription, selectedAccountItems)
        db.setCallbackResult(result)
        result
      })


    } yield (respond)
  }

}
