package twitter

import akka.actor.{Actor, ActorRef}

class TwitterWebsocketActor(connectedClient: ActorRef) extends Actor {

  ActorDirectory.tweetActor.foreach(actor => actor ! RegisterMe(context.self))

  override def receive: Receive = {
    case Tweet(tweetText) =>
      connectedClient ! tweetText
  }

  override def postStop(): Unit = {
    ActorDirectory.tweetActor.foreach(actor => actor ! UnRegisterMe(context.self))
  }
}
