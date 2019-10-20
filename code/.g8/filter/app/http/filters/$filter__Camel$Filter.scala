package http.filters

import javax.inject.Inject
import akka.stream.Materializer
import play.api.Logger
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

class $filter;format="Camel"$Filter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    // Handle Before Request
    // https://www.playframework.com/documentation/2.6.x/ScalaHttpFilters
    val startTime = System.currentTimeMillis

    nextFilter(requestHeader).map { result =>
      // Handle Request After
     result
    }
  }
}