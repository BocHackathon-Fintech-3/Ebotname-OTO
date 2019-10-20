package services.boc.models

import play.api.libs.json._
import services.boc.exceptions.BocException
import utils.LoggingSupport


object BOCApiError {
  implicit val jsonFormat = new OFormat[BOCApiError] {
    override def writes(o: BOCApiError): JsObject = Json.obj("code" -> o.code, "status" -> o.status, "message" -> o.description)

    override def reads(json: JsValue): JsResult[BOCApiError] = {
      val code = (json \ "error" \ "code").as[String].toInt
      val descriptionA = (json \ "error" \ "description").as[String]
      val descriptionB = (json \ "error" \ "additionalDetails" \ 0 \ "description").as[String]
      val status = (json \ "error" \ "additionalDetails" \ 0 \ "status").as[String]

      JsSuccess(BOCApiError(code, status, s"$descriptionA, $descriptionB"))
    }
  }


}

case class BOCApiError(code: Int, status: String, description: String) extends LoggingSupport {
  error(description)

  def throwable = throw new BocException(this)
}
