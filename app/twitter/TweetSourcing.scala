package twitter

import java.util.concurrent.{Executors, LinkedBlockingQueue}

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.{Constants, HttpHosts}
import com.twitter.hbc.httpclient.auth.OAuth1
import play.api.Logger

import scala.collection.JavaConversions._

object TweetSourcing {
  /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
  val msgQueue = new LinkedBlockingQueue[String](100000)

  /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
  val hosebirdHosts = new HttpHosts(Constants.STREAM_HOST)
  val hosebirdEndpoint = new StatusesFilterEndpoint()
  // Optional: set up some followings and track terms
//  val followings: Seq[java.lang.Long] = Seq(1234L, 566788L)
  val terms = Seq("#ENGvNZL")
//  hosebirdEndpoint.followings(followings)
  hosebirdEndpoint.trackTerms(terms)

  // These secrets should be read from a config file
  val hosebirdAuth = new OAuth1("", "", "", "")

  val builder = new ClientBuilder()
    .name("Hosebird-Client-01")                              // optional: mainly for the logs
    .hosts(hosebirdHosts)
    .authentication(hosebirdAuth)
    .endpoint(hosebirdEndpoint)
    .processor(new StringDelimitedProcessor(msgQueue))

  val hosebirdClient = builder.build()
  // Attempts to establish a connection.


  val queueChecker = new Runnable {
    override def run(): Unit = {
      while(!hosebirdClient.isDone) {
        var msg = msgQueue.poll()
        while(msg != null) {
          ActorDirectory.tweetActor match {
            case Some(actor) => actor ! Tweet(msg)
            case None =>
              Logger.error("Error, actor not initialized")
          }
          msg = msgQueue.poll()
        }
      }
    }
  }

  val threadPool = Executors.newSingleThreadExecutor()

  threadPool.submit(queueChecker)

}
