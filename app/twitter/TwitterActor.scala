package twitter

import akka.actor.Actor
import play.api.Logger
import play.api.libs.json.{JsString, Json}

import scala.util.{Failure, Success, Try}

case class Tweet(content: String)

class TwitterActor extends Actor {
  override def receive: Receive = {
    case Tweet(jsonString) =>
      val json = Try(Json.parse(jsonString))
      json match {
        case Success(goodJson) =>
          Logger.info((goodJson \ "text").as[JsString].value)
        case Failure(exception) =>

      }
  }
}
