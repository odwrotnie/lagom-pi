package com.example.hello.impl

import java.time.LocalDateTime

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag, PersistentEntity}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}
import play.api.libs.json.{Format, Json}

import scala.collection.immutable.Seq

import com.typesafe.scalalogging._

class LagompiEntity
  extends PersistentEntity
  with LazyLogging {

  override type Command = LagompiCommand[_]
  override type Event = LagompiEvent
  override type State = LagompiState

  override def initialState: LagompiState = LagompiState("Hello", LocalDateTime.now.toString)

  override def behavior: Behavior = {
    case LagompiState(message, _) => Actions().onCommand[UseGreetingMessage, Done] {

      case (UseGreetingMessage(newMessage), ctx, state) =>
        // In response to this command, we want to first persist it as a
        // GreetingMessageChanged event
        ctx.thenPersist(
          GreetingMessageChanged(newMessage)
        ) { _ =>
          // Then once the event is successfully persisted, we respond with done.
          ctx.reply(Done)
        }

    }.onReadOnlyCommand[L, Double] {
      case (L(n), ctx, state) =>
        val l = math.pow(-1, n.toDouble) / (2 * n + 1)
        logger.info(s"Service - Leibniz($n) = $l")
        ctx.reply(l)

    }.onEvent {

      // Event handler for the GreetingMessageChanged event
      case (GreetingMessageChanged(newMessage), state) =>
        // We simply update the current state to use the greeting message from
        // the event.
        LagompiState(newMessage, LocalDateTime.now().toString)

    }
  }
}

/**
  * The current state held by the persistent entity.
  */
case class LagompiState(message: String, timestamp: String)

object LagompiState {
  /**
    * Format for the hello state.
    *
    * Persisted entities get snapshotted every configured number of events. This
    * means the state gets stored to the database, so that when the entity gets
    * loaded, you don't need to replay all the events, just the ones since the
    * snapshot. Hence, a JSON format needs to be declared so that it can be
    * serialized and deserialized when storing to and from the database.
    */
  implicit val format: Format[LagompiState] = Json.format
}

/**
  * This interface defines all the events that the LagompiEntity supports.
  */
sealed trait LagompiEvent extends AggregateEvent[LagompiEvent] {
  def aggregateTag = LagompiEvent.Tag
}

object LagompiEvent {
  val Tag = AggregateEventTag[LagompiEvent]
}

/**
  * An event that represents a change in greeting message.
  */
case class GreetingMessageChanged(message: String) extends LagompiEvent

object GreetingMessageChanged {

  /**
    * Format for the greeting message changed event.
    *
    * Events get stored and loaded from the database, hence a JSON format
    * needs to be declared so that they can be serialized and deserialized.
    */
  implicit val format: Format[GreetingMessageChanged] = Json.format
}

/**
  * This interface defines all the commands that the HelloWorld entity supports.
  */
sealed trait LagompiCommand[R] extends ReplyType[R]

/**
  * A command to switch the greeting message.
  *
  * It has a reply type of [[Done]], which is sent back to the caller
  * when all the events emitted by this command are successfully persisted.
  */
case class UseGreetingMessage(message: String) extends LagompiCommand[Done]

object UseGreetingMessage {

  /**
    * Format for the use greeting message command.
    *
    * Persistent entities get sharded across the cluster. This means commands
    * may be sent over the network to the node where the entity lives if the
    * entity is not on the same node that the command was issued from. To do
    * that, a JSON format needs to be declared so the command can be serialized
    * and deserialized.
    */
  implicit val format: Format[UseGreetingMessage] = Json.format
}

case class L(n: Long) extends LagompiCommand[Double]

object L {

  implicit val format: Format[L] = Json.format
}

object LagompiSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: Seq[JsonSerializer[_]] = Seq(
    JsonSerializer[UseGreetingMessage],
    JsonSerializer[L],
    JsonSerializer[GreetingMessageChanged],
    JsonSerializer[LagompiState]
  )
}
