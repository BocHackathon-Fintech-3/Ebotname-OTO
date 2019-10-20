package http.api

import play.api.libs.json.{Json, OFormat}
import play.api.libs.json._
import scala.util.Try

case class ModelFilter(key: String,
                       value: String,
                       operator: String,
                       values: Option[Seq[String]] = None) {}

object ModelFilter {
  implicit val format = Json.format[ModelFilter]
  val ExpectedQueryVariable = "q"
  val FilteredQueriesDelimiter = '|'
  val FilteredQueriesParametersDelimiter = ';'
  val Excluded = List("page", "size", "sort")

  val FilterWithoutOperator = 2
  val FilterWithOperator = 3

  private val allowedOperators = Map(
    "eq" -> "=",
    "lt" -> "<",
    "gt" -> ">",
    "le" -> "<=",
    "ge" -> ">=",
    "*" -> "%",
  )

  /**
    * Strategy for extracting multiple values i.e. array -> [...]
    * from query requestParameters
    *
    */
  def extractValue(param: String): Option[Seq[String]] = {
    param match {
      case x if x.startsWith("[") && param.endsWith("]") =>
        Try(param.filterNot("[]".toSet).split(',').toSeq.filterNot(_.isEmpty)).toOption
      case _ => None
    }
  }

  val FilterName = 0
  val FilterOperator = 1
  val FilterWithOperatorValues = 2

  /**
    *
    */
  def extractModelFilter(value: String): Option[ModelFilter] = {
    val filterElement = value.split(FilteredQueriesParametersDelimiter)

    filterElement.length match {
      case FilterWithoutOperator =>
        Some(ModelFilter(filterElement(FilterName), filterElement(FilterOperator), "=", extractValue(filterElement(FilterOperator))))
      case elementLength if elementLength == FilterWithOperator && allowedOperators.isDefinedAt(filterElement(FilterOperator)) =>
        val operator = allowedOperators.getOrElse(filterElement(FilterOperator), "=")
        val value = filterElement(FilterWithOperatorValues)
        val values = extractValue(value)
        Some(ModelFilter(filterElement(FilterName), value, operator, values))
      case _ => None
    }
  }

  /**
    * Expects to find query variable [[ModelFilter.ExpectedQueryVariable]] in request requestParameters
    * separated by delimiter (;) [[ModelFilter.FilteredQueriesParametersDelimiter]]
    * Receives requestParameters in the following format:
    * {{{QueryVariable=(VariableName)(Delimiter}}}
    *
    */
  def fromRequest(requestParameters: Map[String, String], requestDomain: String): Seq[ModelFilter] = {
    if (requestParameters.isDefinedAt(ExpectedQueryVariable)) {
      requestParameters(ExpectedQueryVariable)
        .split(FilteredQueriesDelimiter)
        .map(p => extractModelFilter(p))
        .toSeq
        .flatten
    } else {
      Seq.empty
    }
  }

}
