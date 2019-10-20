package http.api


import org.joda.time.DateTime
import play.api.i18n.{I18nSupport, Lang, MessagesImpl, MessagesProvider}
import play.api.libs.json._
import play.api.mvc._
import http.api.ApiError._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions
import Api.Sorting._


/*
* Controller for the API
*/
trait ApiController extends AbstractController with I18nSupport {


  //  implicit val messagesApi: MessagesApi
  implicit val messagesProvider: MessagesProvider = {
    MessagesImpl(Lang("en"), messagesApi)
  }
  implicit val lang: Lang = Lang("en")
  //////////////////////////////////////////////////////////////////////
  // Implicit transformation utilities

  implicit def objectToJson[T](o: T)(implicit tjs: Writes[T]): JsValue = Json.toJson(o)

  implicit def result2FutureResult(r: ApiResult): Future[ApiResult] = Future.successful(r)

  //////////////////////////////////////////////////////////////////////
  // Custom Actions

  def ApiAction(action: ApiRequest[Unit] => Future[ApiResult]): Action[Unit] = ApiActionWithParser(parse.empty)(action)

  def ApiActionWithBody(action: ApiRequest[JsValue] => Future[ApiResult]): Action[JsValue] = ApiActionWithParser(parse.json)(action)

  // Creates an Action checking that the Request has all the common necessary headers with their correct values (Date)
  private def ApiActionCommon[A](parser: BodyParser[A])(action: (ApiRequest[A], DateTime) => Future[ApiResult]): Action[A] = Action.async(parser) { implicit request =>
    implicit val apiRequest: ApiRequest[A] = ApiRequest(request, messagesApi)

    val futureApiResult: Future[ApiResult] = apiRequest.dateOptTry match {
      case None => action(apiRequest, new DateTime())
      case Some(Failure(_)) => errorDateMalformed
      case Some(Success(date)) => action(apiRequest, date)
    }

    futureApiResult.map {
      case error: ApiError => error.saveLog.toResult
      case response: ApiResponse => response.toResult
    }
  }

  // Basic Api Action
  private def ApiActionWithParser[A](parser: BodyParser[A])(action: ApiRequest[A] => Future[ApiResult]) = ApiActionCommon(parser) { (apiRequest, _) =>
    action(apiRequest)
  }


  //////////////////////////////////////////////////////////////////////
  // Auxiliar methods to create ApiResults from writable JSON objects

  def ok[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.ok(obj, headers: _*))

  def ok[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.ok(obj, headers: _*))

  private def itemOrError[A](opt: Option[A], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): ApiResult = opt match {
    case Some(i) => ApiResponse.ok(i, headers: _*)
    case None => ApiError.errorItemNotFound
  }

  def maybeItem[A](opt: Option[A], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): Future[ApiResult] = Future.successful(itemOrError(opt, headers: _*))

  def maybeItem[A](futOpt: Future[Option[A]], headers: (String, String)*)(implicit w: Writes[A], req: RequestHeader): Future[ApiResult] = futOpt.map(opt => itemOrError(opt, headers: _*))

  def created[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.created(obj, headers: _*))

  def created[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.created(obj, headers: _*))

  def created(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.created(headers: _*))

  def accepted[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.accepted(obj, headers: _*))

  def accepted[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.accepted(obj, headers: _*))

  def accepted(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.accepted(headers: _*))

  def noContent(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.noContent(headers: _*))

  //////////////////////////////////////////////////////////////////////
  // More auxiliary methods

  // Reads an object from an ApiRequest[JsValue] handling a possible malformed error
  def readFromRequest[T](f: T => Future[ApiResult])(implicit request: ApiRequest[JsValue], rds: Reads[T], req: RequestHeader): Future[ApiResult] = {
    request.body.validate[T].fold(
      (errors: Seq[(JsPath, Seq[JsonValidationError])]) => errorBodyMalformed(errors),
      readValue => f(readValue)
    )
  }

  /*
  * Process a "sort" URL GET param with a specific format. Returns the corresponding description as a list of pairs field-order,
  * where field is the field to sort by, and order indicates if the sorting has an ascended or descendent order.
  * The input format is a string with a list of sorting fields separated by commas and with preference order. Each field has a
  * sign that indicates if the sorting has an ascended or descendent order.
  * Example: "-done,order,+id"  Seq(("done", DESC), ("priority", ASC), ("id", ASC))   where ASC=false and DESC=true
  *
  * Params:
  *  - sortBy: optional String with the input sorting description.
  *  - allowedFields: a list of available allowed fields to sort.
  *  - default: String with the default input sorting description.
  *  - name: the name of the param.
  */
  def processSortByParam(sortBy: Option[String], allowedFields: Seq[String], default: String, name: String = "sort")(implicit req: RequestHeader): Either[ApiError, Seq[(String, Boolean)]] = {
    val signedFieldPattern = """([+-]?)(\w+)""".r
    val fieldsWithOrder = signedFieldPattern.findAllIn(sortBy.getOrElse(default)).toList.map {
      case signedFieldPattern("-", field) => (field, DESC)
      case signedFieldPattern(_, field) => (field, ASC)
    }
    // Checks if every field is within the available allowed ones
    if ((fieldsWithOrder.map(_._1) diff allowedFields).isEmpty)
      Right(fieldsWithOrder)
    else
      Left(errorParamMalformed(name))
  }

}
