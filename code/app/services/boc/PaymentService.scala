package services.boc

import javax.inject.Inject
import play.api.libs.json.{JsObject, JsValue, Json}
import services.boc.contracts.{BOCService, PaymentServiceContract}
import services.boc.models.{BOCApiError, Subscription}
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

class PaymentService @Inject()(val boc: BOCWebClient, val db: DB)(
  implicit ec: ExecutionContext
) extends BOCService
    with LoggingSupport
    with PaymentServiceContract {
  override val bocEndpoint: String = "payments"

  /**
			* Create Payment
	  * curl --request POST \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
	  * --header 'subscriptionid: REPLACE_THIS_VALUE' \
	  * --header 'timestamp: REPLACE_THIS_VALUE' \
	  * --header 'tppid: REPLACE_THIS_VALUE' \
	  * --data '{"debtor":{"bankId":"7171851449008128","accountId":"5018870625119776"},"creditor":{"bankId":"1381956413554688","accountId":"4903323557837438","name":"Effie Diaz","address":"1966 Cudej Pike"},"transactionAmount":{"amount":12.06026091,"currency":"SHP","currencyRate":"SHP"},"endToEndId":"2978096080748544","paymentDetails":"wugidjakrumogmomotepcekeglabcihimaotfokrunlanubkeiganigaehluledfijuemoutuhohacfihmorir","terminalId":"1373765151752192","branch":"kekinpehwontim","RUB":{"voCode":"wohuruwidisovezcemerluj","BIK":"jugustulowonetnuehanadesamocipde","INN":"itofabubbejfoji","correspondentAccount":"5499146955150014"},"executionDate":"1/3/2077","valueDate":"11/7/2115","attachments":[{"fileData":"pegpuz","fileName":"Edwin Floyd"}]}'
			*
			*/
  override def create(token: String, subscriptionId: String, payment: JsObject)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint)
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .post(payment)
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }

  /**
			* Get Account Payments
	  * curl --request GET \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/accounts/REPLACE_ACCOUNTID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE&endDate=REPLACE_THIS_VALUE&maxCount=REPLACE_THIS_VALUE&startDate=REPLACE_THIS_VALUE' \
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
  override def getAccountPayments(
    token: String,
    subscriptionId: String,
    accountId: String
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsValue]] = {
    boc
      .call(bocEndpoint, Some(s"accounts/$accountId"))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .get()
      .map { response =>
        boc.unwrap(response)
      }
  }

  /**
			* Check availability of funds in a Account
	  * curl --request POST \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/fundAvailability?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
	  * --header 'subscriptionid: REPLACE_THIS_VALUE' \
	  * --header 'timestamp: REPLACE_THIS_VALUE' \
	  * --header 'tppid: REPLACE_THIS_VALUE' \
	  * --data '{"bankId":"8288742053773312","accountId":"4026150247403251","transaction":{"amount":27.87628209,"currency":"IMP","currencyRate":"MGA"}}'
			*/
  override def hasAvailableFundsInAccount(
    token: String,
    subscriptionId: String,
    accountInfo: JsObject
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint, Some("fundAvailability"))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .post(accountInfo)
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }

  /**
			* Retrieve the details of payment by payment ID
	  * curl --request GET \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/REPLACE_PAYMENTID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
  override def get(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint, Some(paymentId))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .get()
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }

  /**
			* Cancel a payment which is authorized
	  * curl --request DELETE \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/REPLACE_PAYMENTID?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
  override def cancel(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint, Some(paymentId))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .delete()
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }

  /**
			* Authorise a Payment
	  * curl --request POST \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/REPLACE_PAYMENTID/authorize?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
	  * --header 'subscriptionid: REPLACE_THIS_VALUE' \
	  * --header 'timestamp: REPLACE_THIS_VALUE' \
	  * --header 'tppid: REPLACE_THIS_VALUE' \
	  * --data '{"transactionTime":"13:00","authCode":"wotu"}'
			*/
  override def authorise(
    token: String,
    subscriptionId: String,
    paymentId: String,
    transactionTime: String,
    authCode: String
  )(implicit ec: ExecutionContext): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint, Some(s"$paymentId/authorize"))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .post(
        Json.obj("transactionTime" -> transactionTime, "authCode" -> authCode)
      )
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }

  /**
			* Get the Status of a Payment by payment ID
	  * curl --request GET \
	  * --url 'https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/v1/payments/REPLACE_PAYMENTID/status?client_id=REPLACE_THIS_KEY&client_id=REPLACE_THIS_VALUE&client_secret=REPLACE_THIS_KEY&client_secret=REPLACE_THIS_VALUE' \
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
  override def status(token: String, subscriptionId: String, paymentId: String)(
    implicit ec: ExecutionContext
  ): Future[Either[BOCApiError, JsObject]] = {
    boc
      .call(bocEndpoint, Some(s"$paymentId/status"))
      .addHttpHeaders(
        boc.parameters.JOURNEY_ID,
        boc.parameters.TIMESTAMP,
        boc.parameters.ORIGIN_USER_ID,
        boc.parameters.TPP_ID,
        boc.parameters.APP_NAME,
        boc.parameters.API_DEBUG_TRANS_ID,
        boc.parameters.CONTENT_TYPE_JSON,
        boc.parameters.authorization(token),
        boc.parameters.subscription(subscriptionId),
      )
      .get()
      .map { response =>
        boc.unwrapAs[JsObject](response)
      }
  }
}
