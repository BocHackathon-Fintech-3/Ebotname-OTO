package services.boc.contracts

import play.api.libs.json.{JsObject, JsValue}
import services.boc.models.BOCApiError

import scala.concurrent.{ExecutionContext, Future}

trait PaymentServiceContract { self: BOCService =>

  override val bocEndpoint: String = "payments"

  /**
			* Create Payment
			*
			*/
  def create(token: String, subscriptionId: String, payment: JsObject)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]]

  /**
			* Get Account Payments
			*/
  def getAccountPayments(
    token: String,
    subscriptionId: String,
    accountId: String
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsValue]]

  /**
			* Check availability of funds in a Account
	  *
			*/
  def hasAvailableFundsInAccount(
    token: String,
    subscriptionId: String,
    accountInfo: JsObject
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsObject]]

  /**
			* Retrieve the details of payment by payment ID
			*/
  def get(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]]

  /**
			* Cancel a payment which is authorized
			*/
  def cancel(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]]

  /**
			* Authorise a Payment
			*/
  def authorise(
    token: String,
    subscriptionId: String,
    paymentId: String,
    transactionTime: String,
    authCode: String
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsObject]]

  /**
			*Get the Status of a Payment by payment ID
			*/
  def status(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]]
}
