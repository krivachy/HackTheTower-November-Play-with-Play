package twitter

import akka.actor.{Actor, ActorRef}
import play.api.Logger
import play.api.libs.json.{JsString, Json}

import scala.util.{Failure, Success, Try}

case class Tweet(content: String)
case class RegisterMe(actorRef: ActorRef)
case class UnRegisterMe(actorRef: ActorRef)

class TwitterActor extends Actor {

  private var registeredActors = Set.empty[ActorRef]

  override def receive: Receive = {
    case Tweet(jsonString) =>
      val json = Try(Json.parse(jsonString))
      json match {
        case Success(goodJson) =>
          val tweetText = Tweet((goodJson \ "text").as[JsString].value)
          Logger.info(tweetText.toString)
          registeredActors.foreach {
            actor => actor ! tweetText
          }
        case Failure(exception) =>
          Logger.error("Failed to parse tweet JSON", exception)
      }
    case RegisterMe(actorToRegister) => registeredActors += actorToRegister
    case UnRegisterMe(actorToRemove) => registeredActors -= actorToRemove
  }
}
