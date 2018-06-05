package com.example.hello.impl

import java.time.LocalDateTime

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import com.typesafe.scalalogging._
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

class LagompiEntity
  extends PersistentEntity
    with LazyLogging {

  override type Command = LagompiCommand[_]
  override type Event = LagompiEvent
  override type State = LagompiState

  override def initialState: LagompiState = LagompiState("Hello", LocalDateTime.now.toString)

  override def behavior: Behavior = {
    case LagompiState(message, _) => Actions().onReadOnlyCommand[L, Double] {
      case (L(n), ctx, state) =>
        val l = math.pow(-1, n.toDouble) / (2 * n + 1)
        logger.info(s"Service - Leibniz($n) = $l")
        ctx.reply(l)
    }
  }
}

case class LagompiState(message: String, timestamp: String)

object LagompiState {
  implicit val format: Format[LagompiState] = Json.format
}

sealed trait LagompiEvent extends AggregateEvent[LagompiEvent] {
  def aggregateTag = LagompiEvent.Tag
}

object LagompiEvent {
  val Tag = AggregateEventTag[LagompiEvent]
}

sealed trait LagompiCommand[R] extends ReplyType[R]

case class L(n: Long) extends LagompiCommand[Double]

object L {

  implicit val format: Format[L] = Json.format
}

object LagompiSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[L],
    JsonSerializer[LagompiState]
  )
}
