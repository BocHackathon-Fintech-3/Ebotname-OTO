package services.boc.contracts

import akka.Done
import services.boc.models.{Account, CallbackResult, Subscription}

import scala.concurrent.Future

trait StorageContract {

  def getRedirectUrl: Future[String]

  def setRedirectUrl(url: String): Future[Done]

  def setSubscriptionId(id: String): Future[Done]

  def getSubscriptionId: Future[String]

  def setApplicationToken(token: String): Future[Done]

  def getApplicationToken: Future[Option[String]]

  def setActiveSubscription(subscription: Subscription): Future[Done]

  def getActiveSubscription: Future[Option[Subscription]]

  def getSelectedAccounts: Future[Seq[Account]]

  def setSelectedAccounts(subscriptions: Seq[Account]): Future[Done]

  def getCallbackResult: Future[CallbackResult]

  def setCallbackResult(result: CallbackResult): Future[Done]

}
