package integration

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.http.FileMimeTypes
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._
import play.api.test.CSRFTokenHelper._
import play.api.test.Helpers._
import play.api.test._

import scala.concurrent.ExecutionContext

/**
 * $model;format="Camel"$ form controller specs
 */
class $model;format="Camel"$ControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // Provide stubs for components based off Helpers.stubControllerComponents()
  class StubComponents(cc:ControllerComponents = stubControllerComponents()) extends MessagesControllerComponents {
    override val parsers: PlayBodyParsers = cc.parsers
    override val messagesApi: MessagesApi = cc.messagesApi
    override val langs: Langs = cc.langs
    override val fileMimeTypes: FileMimeTypes = cc.fileMimeTypes
    override val executionContext: ExecutionContext = cc.executionContext
    override val actionBuilder: ActionBuilder[Request, AnyContent] = cc.actionBuilder
    override val messagesActionBuilder: MessagesActionBuilder = new DefaultMessagesActionBuilderImpl(parsers.default, messagesApi)(executionContext)
  }

  "$model;format="Camel"$Controller GET" should {

    "render the index page from a new instance of controller" in {
      val controller = new $model;format="Camel"$Controller(new StubComponents())
      val request = FakeRequest().withCSRFToken
      val home = controller.index.apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "render the index page from the application" in {
      val controller = inject[$model;format="Camel"$Controller]
      val request = FakeRequest().withCSRFToken
      val home = controller.index.apply(request)

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }

    "render the index page from the router" in {
      val request = CSRFTokenHelper.addCSRFToken(FakeRequest(GET, "/$model;format="normalize"$"))
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("application/json")
    }
  }

  "$model;format="Camel"$Controller POST" should {
    "process form" in {
      val request = {
        FakeRequest(POST, "/$model;format="normalize"$").withJsonBody(Json.obj("name"->"value"))
      }
      val home = route(app, request).get

      status(home) mustBe 201
    }
  }

}
