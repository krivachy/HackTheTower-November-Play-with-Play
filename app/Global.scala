import akka.actor.{ActorSystem, Props}
import play.api._
import twitter.{ActorDirectory, TweetSourcing, TwitterActor}

object Global extends GlobalSettings {

  val actorSystem = ActorSystem()

  override def onStart(app: Application) {
    Logger.info("Application has started")
    TweetSourcing.hosebirdClient.connect()
    Logger.info("Connected to Twitter")
    val props = Props[TwitterActor]
    ActorDirectory.tweetActor = Some(actorSystem.actorOf(props))
  }

  override def onStop(app: Application) {
    Logger.info("Closing Twitter connection")
    TweetSourcing.hosebirdClient.stop()
    Logger.info("Application shutdown...")
  }


}
