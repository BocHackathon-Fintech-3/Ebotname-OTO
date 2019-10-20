package services.boc.contracts

import services.boc.models.{BOCApiError, Subscription}

import scala.concurrent.{ExecutionContext, Future}

trait SubscriptionServiceContract { self: BOCService =>

  override val bocEndpoint: String = "subscriptions"

  /**
    * Get subscription for TPP based on subscriptionId in header
    */
  def get(subscriptionId: String, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]]

  /**
    * Update subscription details based on subscriptionId
    */
  def update(subscription: Subscription, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]]

  /**
    * Retrieve subscriptions for customer account based on accountId in path parameter
    */
  def getSubscriptionAccount(
    subscriptionId: String,
    token: String,
    accountId: String
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, Subscription]]

  /**
    * Revoke subscription based on subscriptionId
    */
  def delete(subscriptionId: String, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]]

  /**
    * Create Subscription after user authentication
    */
  def create(token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]]
}
