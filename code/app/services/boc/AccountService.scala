package services.boc

import java.util.UUID

import javax.inject.Inject
import org.joda.time.DateTime
import play.api.libs.json.{JsObject, JsValue, Json}
import services.boc.contracts.{AccountServiceContract, BOCService}
import services.boc.models.{BOCApiError, Subscription}
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

class AccountService @Inject()(val boc: BOCWebClient, val db: DB)(
  implicit ec: ExecutionContext
) extends BOCService
    with LoggingSupport
    with AccountServiceContract {

  /**
    * Retrieve account details
    *
    * curl --request GET \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/accounts?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
    * --header 'accept: application/json' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'customerid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
    * --header 'onlineaccessflag: REPLACE_THIS_VALUE' \
    * --header 'originchannelid: REPLACE_THIS_VALUE' \
    * --header 'origindeptid: REPLACE_THIS_VALUE' \
    * --header 'originemployeeid: REPLACE_THIS_VALUE' \
    * --header 'originsourceid: REPLACE_THIS_VALUE' \
    * --header 'originterminalid: REPLACE_THIS_VALUE' \
    * --header 'originuserid: REPLACE_THIS_VALUE' \
    * --header 'subscriptionid: REPLACE_THIS_VALUE' \
    * --header 'timestamp: REPLACE_THIS_VALUE' \
    * --header 'tppid: REPLACE_THIS_VALUE'
    */
  def details(token: String,
              subscriptionId: String,
              accountId: String): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint, Some(accountId))
      .addHttpHeaders(
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.TPP_ID,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.JOURNEY_ID,
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }

  /**
    *
    */
  def balance(token: String,
              subscriptionId: String,
              accountId: String): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint, Some(s"$accountId/balance"))
      .addHttpHeaders(
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.TPP_ID,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.JOURNEY_ID,
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }

  /**
    *
    */
  def statement(token: String,
                subscriptionId: String,
                accountId: String): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint, Some(s"$accountId/statement"))
      .addHttpHeaders(
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.TPP_ID,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.JOURNEY_ID,
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }

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
          subscriptionId: String): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint)
      .addHttpHeaders(
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.TPP_ID,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.JOURNEY_ID,
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }

  def healthCheck(
    token: String,
    subscriptionId: String
  ): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint, Some("healthCheck"))
      .addHttpHeaders(
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.TPP_ID,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.JOURNEY_ID,
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }
}
