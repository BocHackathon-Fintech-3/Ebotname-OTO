package utils

import play.api.Logger

trait LoggingSupport {
  private val slf4jLogger: Logger = Logger(getClass)

  @inline
  def error(msg: => String): Unit = { if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg) }

  @inline
  def error(msg: => String, t: Throwable): Unit = { if (slf4jLogger.isErrorEnabled) slf4jLogger.error(msg, t) }

  @inline
  def warn(msg: => String): Unit = { if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg) }

  @inline
  def warn(msg: => String, t: Throwable): Unit = { if (slf4jLogger.isWarnEnabled) slf4jLogger.warn(msg, t) }

  @inline
  def info(msg: => String): Unit = { if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg) }

  @inline
  def info(msg: => String, t: Throwable): Unit = { if (slf4jLogger.isInfoEnabled) slf4jLogger.info(msg, t) }

  @inline
  def debug(msg: => String): Unit = { if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg) }

  @inline
  def debug(msg: => String, t: Throwable): Unit = { if (slf4jLogger.isDebugEnabled) slf4jLogger.debug(msg, t) }

  @inline
  def trace(msg: => String): Unit = { if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg) }

  @inline
  def trace(msg: => String, t: Throwable): Unit = { if (slf4jLogger.isTraceEnabled) slf4jLogger.trace(msg, t) }
}
