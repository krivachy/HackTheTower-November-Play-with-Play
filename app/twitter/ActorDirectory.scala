package twitter

import akka.actor.ActorRef

object ActorDirectory {
  var tweetActor: Option[ActorRef] = None
}
