package services.boc.contracts

import play.api.libs.json.JsValue
import services.boc.models.BOCApiError

import scala.concurrent.Future

trait AccountServiceContract { self: BOCService =>

  override val bocEndpoint: String = "accounts"

  def details(token: String,
              subscriptionId: String,
              accountId: String): Future[Either[BOCApiError, JsValue]]

  def balance(token: String,
              subscriptionId: String,
              accountId: String): Future[Either[BOCApiError, JsValue]]

  def statement(token: String,
                subscriptionId: String,
                accountId: String): Future[Either[BOCApiError, JsValue]]

  /**
    * accept
    *
    * Authorization
    * customerId
    * originSourceId
    * originChannelId
    * originDeptId
    * originUserId
    * originEmployeeId
    * originTerminalId
    * journeyId
    * timeStamp
    * correlationId
    * tppId
    * subscriptionId
    * lang
    * onlineAccessFlag
    */
  def get(token: String,
          subscriptionId: String): Future[Either[BOCApiError, JsValue]]

  def healthCheck(token: String,
                  subscriptionId: String): Future[Either[BOCApiError, JsValue]]
}
