package services.boc

import akka.Done
import play.api.cache.AsyncCacheApi
import play.api.libs.json.Writes

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag

abstract class DBService(cache: AsyncCacheApi)(implicit ec: ExecutionContext) {
  val underlying: AsyncCacheApi = cache

  def resource: String

  def get[T: ClassTag](key: String): Future[Option[T]] =
    cache.get[T](getResource(Some(key)))

  def getOr[T: ClassTag](key: String, action: String => Future[T]): Future[T] =
    cache.get[T](key).flatMap {
      case Some(r) => Future(r)
      case None => action(key)
    }

  def insert[T](value: T)(implicit writes: Writes[T]): Future[Done] =
    cache.set(getResource(), value)

  private def getResource(id: Option[String] = None): String =
    id.map { identifier =>
      s"$resource.$identifier"
    }
      .getOrElse(resource)

  def update[T](key: Option[String] = None,
                value: T)(implicit writes: Writes[T]): Future[Done] =
    cache.getOrElseUpdate(getResource(key))(cache.set(getResource(key), value))

  def delete[T](key: Option[String] = None)(
    implicit writes: Writes[T]
  ): Future[Done] = cache.remove(getResource(key))
}
