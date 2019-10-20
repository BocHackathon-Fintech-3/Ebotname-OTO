package services.boc

import java.util.UUID

import javax.inject.Inject
import play.api.libs.json.{JsObject, Json}
import services.boc.contracts.{BOCService, SubscriptionServiceContract}
import services.boc.exceptions.InvalidSubscriptionException
import services.boc.models.{BOCApiError, Subscription}
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

class SubscriptionService @Inject()(val boc: BOCWebClient, val db: DB)(
  implicit ec: ExecutionContext
) extends BOCService
  with LoggingSupport
  with SubscriptionServiceContract {
  override val bocEndpoint: String = "subscriptions"

  /**
    * Get subscription for TPP based on subscriptionId in header
    * curl --request GET \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/subscriptions/REPLACE_SUBSCRIPTIONID?client_id=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_KEY' \
    * --header 'accept: application/json' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
    * --header 'originchannelid: REPLACE_THIS_VALUE' \
    * --header 'origindeptid: REPLACE_THIS_VALUE' \
    * --header 'originemployeeid: REPLACE_THIS_VALUE' \
    * --header 'originsourceid: REPLACE_THIS_VALUE' \
    * --header 'originterminalid: REPLACE_THIS_VALUE' \
    * --header 'originuserid: REPLACE_THIS_VALUE' \
    * --header 'timestamp: REPLACE_THIS_VALUE' \
    * --header 'tppid: REPLACE_THIS_VALUE'
    */
  override def get(subscriptionId: String, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]] = {
    boc
      .call(bocEndpoint, Some(subscriptionId))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
      )
      .get()
      .map { response =>
        boc.unwrapAs[Seq[Subscription]](response).map {
          _.headOption.getOrElse(throw new InvalidSubscriptionException)
        }

      }
  }

  /**
    * Update subscription details based on subscriptionId
    * curl --request PATCH \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/subscriptions/REPLACE_SUBSCRIPTIONID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
    * --header 'accept: application/json' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'content-type: application/json' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
    * --header 'originchannelid: REPLACE_THIS_VALUE' \
    * --header 'origindeptid: REPLACE_THIS_VALUE' \
    * --header 'originemployeeid: REPLACE_THIS_VALUE' \
    * --header 'originsourceid: REPLACE_THIS_VALUE' \
    * --header 'originterminalid: REPLACE_THIS_VALUE' \
    * --header 'originuserid: REPLACE_THIS_VALUE' \
    * --header 'timestamp: REPLACE_THIS_VALUE' \
    * --header 'tppid: REPLACE_THIS_VALUE' \
    * --data '{"selectedAccounts":[{"accountId":"6291102326830394"}],"accounts":{"transactionHistory":"true","balance":"false","details":"false","checkFundsAvailability":"false"},"payments":{"limit":6.66859089,"currency":"LBP","amount":26.43389797}}'
    */
  override def update(subscription: Subscription, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]] = {
    boc
      .call(bocEndpoint, Some(subscription.subscriptionId))
      .addHttpHeaders(
        boc.parameters.ACCEPT_JSON,
        boc.parameters.CORRELATION_ID,
        boc.parameters.LANG,
        boc.parameters.authorization(token),
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.APP_NAME,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.TPP_ID,
      )
      .patch(Json.toJsObject(subscription))
      .map { response =>
        boc.unwrapAs[Subscription](response)
      }
  }

  /**
    * Retrieve subscriptions for customer account based on accountId in path parameter
    * curl --request GET \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/subscriptions/accounts/REPLACE_ACCOUNTID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
    * --header 'accept: application/json' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
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
  override def getSubscriptionAccount(subscriptionId: String,
                                      token: String,
                                      accountId: String)(
                                       implicit ec: ExecutionContext
                                     ): Future[Either[BOCApiError, Subscription]] = {
    boc
      .call(bocEndpoint, Some(s"accounts/${accountId}"))
      .addHttpHeaders(
        boc.parameters.subscription(subscriptionId),
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
      )
      .get()
      .map { response =>
        boc.unwrapAs[Seq[Subscription]](response).map {
          _.headOption.getOrElse(throw new InvalidSubscriptionException())
        }
      }
  }

  /**
    * Revoke subscription based on subscriptionId
    * curl --request DELETE \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/subscriptions/REPLACE_SUBSCRIPTIONID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
    * --header 'accept: application/json' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
    * --header 'originchannelid: REPLACE_THIS_VALUE' \
    * --header 'origindeptid: REPLACE_THIS_VALUE' \
    * --header 'originemployeeid: REPLACE_THIS_VALUE' \
    * --header 'originsourceid: REPLACE_THIS_VALUE' \
    * --header 'originterminalid: REPLACE_THIS_VALUE' \
    * --header 'originuserid: REPLACE_THIS_VALUE' \
    * --header 'timestamp: REPLACE_THIS_VALUE' \
    * --header 'tppid: REPLACE_THIS_VALUE'
    */
  override def delete(subscriptionId: String, token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]] = {
    boc
      .call(bocEndpoint, Some(subscriptionId))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
      )
      .delete()
      .map { response =>
        boc.unwrapAs[Seq[Subscription]](response).map {
          _.headOption.getOrElse(throw new InvalidSubscriptionException())
        }
      }
  }

  /**
    * Create Subscription after user authentication
    * curl --request POST \
    * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/subscriptions?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
    * --header 'accept: application/json' \
    * --header 'app_name: REPLACE_THIS_VALUE' \
    * --header 'authorization: Bearer REPLACE_BEARER_TOKEN' \
    * --header 'content-type: application/json' \
    * --header 'correlationid: REPLACE_THIS_VALUE' \
    * --header 'journeyid: REPLACE_THIS_VALUE' \
    * --header 'lang: REPLACE_THIS_VALUE' \
    * --header 'originchannelid: REPLACE_THIS_VALUE' \
    * --header 'origindeptid: REPLACE_THIS_VALUE' \
    * --header 'originemployeeid: REPLACE_THIS_VALUE' \
    * --header 'originsourceid: REPLACE_THIS_VALUE' \
    * --header 'originterminalid: REPLACE_THIS_VALUE' \
    * --header 'originuserid: REPLACE_THIS_VALUE' \
    * --header 'timestamp: REPLACE_THIS_VALUE' \
    * --header 'tppid: REPLACE_THIS_VALUE' \
    * --data '{"selectedAccounts":[{"accountId":"3528975969078666"}],"accounts":{"transactionHistory":"true","balance":"false","details":"false","checkFundsAvailability":"false"},"payments":{"limit":87.92550252,"currency":"USD","amount":41.0253298}}'
    */
  override def create(token: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, Subscription]] = {
    for {
      subscription <- boc
        .call(bocEndpoint)
        .addHttpHeaders(
          boc.parameters.authorization(token),
          boc.parameters.CONTENT_TYPE_JSON,
          boc.parameters.API_DEBUG_TRANS_ID,
          boc.parameters.APP_NAME,
          boc.parameters.TPP_ID,
          boc.parameters.ORIGIN_USER_ID,
          boc.parameters.TIMESTAMP,
          boc.parameters.JOURNEY_ID
        )
        .post(Subscription.empty)
        .map { response =>
          boc.unwrapAs[Subscription](response)
        }
    } yield (subscription)

  }
}
