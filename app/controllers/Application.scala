package controllers

import javax.inject._

import akka.NotUsed
import akka.actor.{ActorRef, ActorSystem}
import akka.cluster.pubsub.{DistributedPubSub, DistributedPubSubMediator}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, _}
import play.api.mvc._

@Singleton
class Application @Inject() (system: ActorSystem) extends Controller {

  val mediator = DistributedPubSub(system).mediator

  def socket = WebSocket.accept[String, String] { request =>
    new Chat(mediator, "chat").flow
  }
}

class Chat(mediator: ActorRef, topic: String) {

  def flow: Flow[String, String, NotUsed] =
    Flow.fromSinkAndSource(
      publishToMediatorSink,
      sourceFrom)

  private def publishToMediatorSink[In]: Sink[In, NotUsed] =
    Flow[In].map(DistributedPubSubMediator.Publish(topic, _)) to
      Sink.actorRef[DistributedPubSubMediator.Publish](mediator, ())

  private def sourceFrom[Out]: Source[Out, ActorRef] =
    Source
      .actorRef[Out](5, OverflowStrategy.fail)
      .mapMaterializedValue { ref => mediator ! DistributedPubSubMediator.Subscribe(topic, ref); ref }


}

