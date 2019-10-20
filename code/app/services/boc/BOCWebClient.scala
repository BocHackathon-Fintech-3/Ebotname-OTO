package services.boc

import java.util.UUID

import javax.inject.{Inject, Singleton}
import org.joda.time.DateTime
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}
import play.api.Configuration
import play.api.libs.json.{JsValue, Json, Reads}
import play.api.libs.ws.ahc.AhcCurlRequestLogger
import play.api.mvc.Result
import play.api.mvc.Results.Redirect
import services.boc.models.BOCApiError
import utils.LoggingSupport

import scala.util.Try

@Singleton
class BOCWebClient @Inject()(ws: WSClient, config: Configuration)
  extends LoggingSupport {

  val base_url: String = config.get[String]("boc.base_url")
  val api_version: String = config.get[String]("boc.api_version")
  val authorize_url: String = config.get[String]("boc.authorize_url")
  val redirect_url: String = config.get[String]("boc.redirect_url")
  val app_url: String = config.get[String]("boc.app_url")
  val success_url: String = config.get[String]("boc.success_url")
  val client_id: String = config.get[String]("boc.client_id")
  val client_secret: String = config.get[String]("boc.client_secret")
  val getAppName: String = config.get[String]("boc.app_name")
  val parameters = new {
    lazy val APP_NAME: (String, String) = "app_name" -> getAppName
    lazy val CLIENT_ID: (String, String) = "client_id" -> client_id
    lazy val CLIENT_SECRET: (String, String) = "client_secret" -> client_secret
    lazy val ACCEPT_JSON: (String, String) = "accept" -> "application/json"
    lazy val CONTENT_TYPE_JSON
    : (String, String) = "content-type" -> "application/json"
    lazy val CONTENT_TYPE_URL_ENCODED
    : (String, String) = "content-type" -> "application/x-www-form-urlencoded"
    lazy val JOURNEY_ID: (String, String) = "journeyId" -> UUID
      .randomUUID()
      .toString
    lazy val TIMESTAMP
    : (String, String) = "timeStamp" -> DateTime.now().getMillis.toString
    lazy val TPP_ID: (String, String) = "tppId" -> "singpaymentdata"
    lazy val API_DEBUG_TRANS_ID
    : (String, String) = "APIm-Debug-Trans-Id" -> true.toString
    lazy val ORIGIN_USER_ID: (String, String) = "originUserId" -> UUID
      .randomUUID()
      .toString
    lazy val LANG: (String, String) = "lang" -> "en"
    lazy val CORRELATION_ID: (String, String) = "correlationid" -> UUID
      .randomUUID()
      .toString

    def authorization(token: String): (String, String) =
      "Authorization" -> s"Bearer $token"

    def subscription(subscriptionId: String): (String, String) =
      "subscriptionId" -> subscriptionId

  }

  def getReturnUrl = s"$app_url/$redirect_url"

  def getSuccessUrl = s"$app_url/$success_url"

  /**
    * Makes a Call to BOC for Authentication no version passed
    */
  def callForAuth(endpoint: String): WSRequest =
    request(endpoint, None, withVersion = false).addHttpHeaders(
      parameters.CONTENT_TYPE_URL_ENCODED,
      parameters.ACCEPT_JSON
    )

  def request(endpoint: String,
              id: Option[String],
              withVersion: Boolean = true): WSRequest = {
    val bocEndpoint =
      if (withVersion) s"$base_url/$api_version/$endpoint"
      else s"$base_url/$endpoint"

    val bocUrl = id
      .map { identifier =>
        s"$bocEndpoint/$identifier"
      }
      .getOrElse(bocEndpoint)
    debug(s"Calling $bocEndpoint")
    ws.url(bocUrl)
      .withRequestFilter(AhcCurlRequestLogger())
  }

  /**
    * Makes a Call to BOC Endpoints
    *
    * @param endpoint String
    * @param id       Option[String]
    * @return Either[BOCApiError, JsValue]
    */
  def call(endpoint: String, id: Option[String] = None): WSRequest = {

    request(endpoint, id)
      .addQueryStringParameters(parameters.CLIENT_ID, parameters.CLIENT_SECRET)
  }

  /**
    * Unwraps the BOC Response Checks whether the response is Json and then if it has Error
    */
  def unwrapAs[T](
                   response: WSResponse
                 )(implicit reads: Reads[T]): Either[BOCApiError, T] = {
    unwrap(response).map { value =>
      value.as[T]
    }
  }

  /**
    * Unwraps the BOC Response Checks whether the response is Json and then if it has Error
    *
    * @param response WSResponse
    * @return Either[BOCApiError, JsValue]
    */
  def unwrap(response: WSResponse): Either[BOCApiError, JsValue] = {
    if (response.status < 400) {
      Try(response.json).toEither match {
        case Right(v) => Right(v)
        case Left(_) => Right(Json.obj())
      }
    } else {
      Try(response.json).toEither match {
        case Right(v) => Left(v.as[BOCApiError])
        case Left(_) =>
          Left(BOCApiError(response.status, response.statusText, response.body))
      }
    }
  }

}
