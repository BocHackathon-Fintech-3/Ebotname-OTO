package services.boc

import akka.Done
import javax.inject.Inject
import play.api.cache.AsyncCacheApi
import services.boc.contracts.StorageContract
import services.boc.models.{Account, CallbackResult, Subscription}

import scala.concurrent.{ExecutionContext, Future}

class DB @Inject()(cacheApi: AsyncCacheApi)(implicit ec: ExecutionContext)
  extends DBService(cacheApi)
    with StorageContract {

  override val resource: String = "application"

  override def getRedirectUrl: Future[String] =
    cacheApi.get[String]("redirect_url").map {
      _.getOrElse(throw new Exception("Invalid Redirect Url"))
    }

  override def setRedirectUrl(url: String): Future[Done] =
    cacheApi.set("redirect_url", url)

  override def setSubscriptionId(id: String): Future[Done] =
    cacheApi.set("subscriptions.id", id)

  override def getSubscriptionId: Future[String] =
    cacheApi.get[String]("subscriptions.id").map {
      _.getOrElse("invalid_subscription_id")
    }

  override def setApplicationToken(token: String): Future[Done] =
    cacheApi.set("application.token", token)

  override def getApplicationToken: Future[Option[String]] =
    cacheApi.get[String]("application.token")

  override def setActiveSubscription(
                                      subscription: Subscription
                                    ): Future[Done] = {
    cacheApi.set("subscriptions.active", subscription)
  }

  override def getActiveSubscription: Future[Option[Subscription]] = {
    cacheApi.get[Subscription]("subscriptions.active")
  }

  override def getSelectedAccounts: Future[Seq[Account]] = cacheApi.get[Seq[Account]]("accounts.selected").map {
    _.getOrElse(throw new Exception("No Selected Account"))
  }

  override def setSelectedAccounts(accounts: Seq[Account]): Future[Done] = cacheApi.set("accounts.selected", accounts)

  override def getCallbackResult: Future[CallbackResult] = {
    cacheApi.get[CallbackResult]("application.callback").map {
      _.getOrElse(throw new Exception("No Callback Result Found"))
    }
  }

  override def setCallbackResult(result: CallbackResult): Future[Done] = cacheApi.set("application.callback", result)
}
