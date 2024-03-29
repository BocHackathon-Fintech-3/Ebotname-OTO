package http.api


import org.joda.time.DateTime
import play.api.i18n.{Lang, MessagesImpl, MessagesProvider}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Request, RequestHeader, WrappedRequest}
import http.api.Api._
import scala.util.Try

/*
* Wrapped Request with additional information for the API (headers: Api Key, Date, Auth-Token, ...)
*/
trait ApiRequestHeader[R <: RequestHeader] {
  val request: R
  val apiKeyOpt: Option[String] = request.headers.get(HEADER_API_KEY)
  val dateOptTry: Option[Try[DateTime]] = request.headers.get(HEADER_DATE).map { dateStr =>
    Try(parseHeaderDate(dateStr))
  }

  val dateOpt: Option[DateTime] = dateOptTry.filter(_.isSuccess).map(_.get)
  val tokenOpt: Option[String] = request.headers.get(HEADER_AUTH_TOKEN)
  val requestedDomain: String = request.headers.get("X-DOMAIN").getOrElse(request.domain)

  def dateOrNow: DateTime = dateOpt.getOrElse(new DateTime())

  //  def remoteAddress: String = request.remoteAddress
  def method: String = request.method

  //  def uri: String = request.uri
  def maybeBody: Option[String] = None

  def requestUri: String = request.uri


  def modelFilters: Seq[ModelFilter] = ModelFilter.fromRequest(parameters, requestedDomain)

  def parameters: Map[String, String] = request.queryString.map { case (k, v) => k -> v.mkString }
}

case class ApiRequestHeaderImpl(request: RequestHeader) extends ApiRequestHeader[RequestHeader]

/*
* ApiRequestHeader for requests that don't require authentication
*/
class ApiRequest[A](val request: Request[A],val messagesApi: play.api.i18n.MessagesApi) extends WrappedRequest[A](request) with ApiRequestHeader[Request[A]] {
  //  override def remoteAddress = request.remoteAddress
  override def method: String = request.method

  //  override def uri = request.uri
  override def maybeBody: Option[String] = request.body match {
    case body: JsValue => Some(Json.prettyPrint(body))
    case body: String => if (body.length > 0) Some(body) else None
    case body => Some(body.toString)
  }

  lazy val lang: Lang = acceptLanguages.headOption.getOrElse {
    Lang("en")
  }
  implicit def messagesProvider()(implicit messageApi:play.api.i18n.MessagesApi): MessagesProvider = {
    MessagesImpl(lang, messageApi)
  }
}

object ApiRequest {
  def apply[A](request: Request[A],messagesApi: play.api.i18n.MessagesApi): ApiRequest[A] = new ApiRequest[A](request,messagesApi)
}

/*
* ApiRequest for authenticated requests
*/
case class SecuredApiRequest[A](override val request: Request[A],override val messagesApi: play.api.i18n.MessagesApi, date: DateTime, token: String, userId: Long) extends ApiRequest[A](request,messagesApi)

