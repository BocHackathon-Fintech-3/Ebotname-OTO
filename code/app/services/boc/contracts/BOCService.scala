package services.boc.contracts

import services.boc.{BOCWebClient, DB}
import utils.LoggingSupport

import scala.concurrent.{ExecutionContext, Future}

trait BOCService { self: LoggingSupport =>
  val boc: BOCWebClient
  val db: DB
  val bocEndpoint: String
}
