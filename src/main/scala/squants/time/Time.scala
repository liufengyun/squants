/*                                                                      *\
** Squants                                                              **
**                                                                      **
** Scala Quantities and Units of Measure Library and DSL                **
** (c) 2013-2014, Gary Keorkunian                                       **
**                                                                      **
\*                                                                      */

package squants.time

import scala.language.implicitConversions
import squants._
import scala.Some
import scala.concurrent.duration.Duration

/**
 * Represents a quantity of Time
 *
 * @author  garyKeorkunian
 * @since   0.1
 *
 * @param value value in [[squants.time.Milliseconds]]
 */
final class Time private (val value: Double) extends Quantity[Time]
    with BaseQuantity {

  def baseUnit = Seconds
  def valueUnit = Time.valueUnit

  def millis = toMilliseconds.toLong

  // TODO - Remove once TimeDerivative pairing of Length -> Velocity is fixed
  def *(that: Velocity) = that * this

  def *[A <: squants.Quantity[A] with squants.time.TimeIntegral[_]](that: TimeDerivative[A]): A = that * this

  def toMicroseconds = to(Microseconds)
  def toMilliseconds = to(Milliseconds)
  def toSeconds = to(Seconds)
  def toMinutes = to(Minutes)
  def toHours = to(Hours)
  def toDays = to(Days)
}

object Time extends QuantityCompanion[Time] {
  val MillisecondsPerNanosecond = 1d / 1000000d
  val MillisecondsPerMicrosecond = 1d / 1000d
  val MillisecondsPerSecond = 1000d
  val MillisecondsPerMinute = MillisecondsPerSecond * 60d
  val MillisecondsPerHour = MillisecondsPerMinute * 60d
  val MillisecondsPerDay = MillisecondsPerHour * 24d
  val SecondsPerMinutes = 60d
  val SecondsPerHour = SecondsPerMinutes * 60d
  val SecondsPerDay = SecondsPerHour * 24

  private[time] def apply[A](n: A)(implicit num: Numeric[A]) = new Time(num.toDouble(n))
  def apply(s: String) = parseString(s)

  def name = "Time"
  def valueUnit = Milliseconds
  def units = Set(Microseconds, Milliseconds, Seconds, Minutes, Hours, Days)
}

trait TimeUnit extends BaseQuantityUnit[Time] with UnitMultiplier {
  def dimensionSymbol = "T"
  def apply[A](n: A)(implicit num: Numeric[A]) = Time(convertFrom(n))
  def unapply(t: Time) = Some(convertTo(t.value))
}

object Microseconds extends TimeUnit {
  val multiplier = Milliseconds.multiplier / 1000d
  val symbol = "µs"
}

object Milliseconds extends TimeUnit with ValueUnit {
  val symbol = "ms"
}

object Seconds extends TimeUnit with BaseUnit {
  val multiplier = Milliseconds.multiplier * 1000d
  val symbol = "s"
}

object Minutes extends TimeUnit {
  val multiplier = Seconds.multiplier * 60d
  val symbol = "m"
}

object Hours extends TimeUnit {
  val multiplier = Minutes.multiplier * 60d
  val symbol = "h"
}

object Days extends TimeUnit {
  val multiplier = Hours.multiplier * 24d
  val symbol = "d"
}

object TimeConversions {
  lazy val microsecond = Microseconds(1)
  lazy val millisecond = Milliseconds(1)
  lazy val second = Seconds(1)
  lazy val minute = Minutes(1)
  lazy val halfHour = Minutes(30)
  lazy val hour = Hours(1)
  lazy val day = Days(1)

  implicit class TimeConversions[A](n: A)(implicit num: Numeric[A]) {
    def microseconds = Microseconds(n)
    def milliseconds = Milliseconds(n)
    def seconds = Seconds(n)
    def minutes = Minutes(n)
    def hours = Hours(n)
    def days = Days(n)
  }

  implicit class TimeStringConversions(s: String) {
    def toTime = Time(s)
  }

  implicit object TimeNumeric extends AbstractQuantityNumeric[Time](Time.valueUnit)

  implicit def timeToScalaDuration(time: Time) = Duration(time.toString)
  implicit def scalaDurationToTime(duration: Duration) = Milliseconds(duration.toMillis)
}

