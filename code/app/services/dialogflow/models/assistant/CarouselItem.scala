package services.dialogflow.models.assistant
import play.api.libs.json.{Json, OFormat}
object Info {
  implicit val jsonFormat = Json.format[Info]
}

object Image {
  implicit val jsonFormat = Json.format[Image]
}

object CarouselItem {
  implicit val jsonFormat: OFormat[CarouselItem] = Json.format[CarouselItem]
}

case class Info(key: String)
case class Image(imageUri: String, accessibilityText: String)
case class CarouselItem(info: Info, title: String, image: Image)
