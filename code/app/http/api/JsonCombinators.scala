package http.api

import java.util.Date

import play.api.libs.json.{Reads, Writes}

/*
* Set of every Writes[A] and Reads[A] for render and parse JSON objects
*/
object JsonCombinators {

  implicit val dateWrites: Writes[Date] = Writes.dateWrites("dd-MM-yyyy HH:mm:ss")
  implicit val dateReads: Reads[Date] = Reads.dateReads("dd-MM-yyyy HH:mm:ss")

}
