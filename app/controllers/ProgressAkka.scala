package controllers

import play.api.mvc._
import play.api.libs.json.{JsString, JsNumber, Json, JsValue}
import akka.actor._
import scala.concurrent.duration._

import play.api.libs.iteratee._

import akka.util.Timeout

import play.api.libs.concurrent.Execution.Implicits._

/**
 * User: Kayrnt
 * Date: 27/08/13
 * Time: 23:01
 */
object ProgressAkka extends Controller {

  implicit val timeout = Timeout(1 second)
  implicit val system = ActorSystem("progress")

  /**
   * We initialise the progression the right channel
   * In fact it might already try to send to another channel and we "replace it"
   * "Init" might not be right word in some case
   * Since we warn the actor it will be done asynchronously
   * @param progress actor that monitor the job for our client
   * @param progressChannel the channel we use to output the progression
   */
  def startAkka(progress: ActorRef, progressChannel: Concurrent.Channel[JsValue]) = {
    progress ! Init(progressChannel)
  }

  /**
   * @return the websocket we use then to transmit progress
   */
  def process = WebSocket.using[JsValue] {
    request => {
      //Concurrent.broadcast is the easy way to link an enumerator to a channel for later use
      val (progressEnumerator, progressChannel) = Concurrent.broadcast[JsValue]
      //we don't really care what the client sent in this sample but you might want to do something
      //if so use a proper iteratee
      val iteratee = Iteratee.skipToEof[JsValue].mapDone {
        _ => println("Disconnected")
      }

      //we generate a UUID to bind the processing to our client using the session
      val (session, generatedUuid) = Application.uuid(request)
      //quite a dirty way to name the actor but simple to use
      val actorPath: ActorPath = system / generatedUuid;
      val actorSelection = system.actorSelection(actorPath);
      //3 seconds might be a bit long for local resolution but
      val future = actorSelection.resolveOne(3 seconds);
      //in case we don't find it, we probably didn't create it (in fact, it's the most common case because we might not have already started the job)
      future.onFailure {
        case t => println(t.getMessage)
          startAkka(system.actorOf(Props[ProgressActor], generatedUuid), progressChannel)
      }
      //in case we found it
      future.map {
        actor => startAkka(actor, progressChannel)
      }

      (iteratee, progressEnumerator)
    }

  }

}

/**
 * Actor dedicated to monitor the progression and push it the Websocket
 */
class ProgressActor extends Actor {

  var progressChannel: Concurrent.Channel[JsValue] = null
  var processActor: ActorRef = null;
  var currentProgress = 0
  val system = ActorSystem("process")


  /**
   * helper method to create messages
   * @param percent of completion of our task
   */
  def createMessageTest(percent: Int) = {
    percent compare 100 match {
      case -1 => self ! Message("In progress", percent)
      case 0 => self ! Message("Done", percent)
      case 1 => println("Something is wrong !")
    }
  }

  def receive = {
    case percent: Int => {
      currentProgress = percent;
      createMessageTest(percent)
    }

    case Message(text, percent) => {
      val json: JsValue = Json.toJson(
        Map(
          "value" -> JsNumber(percent),
          "text" -> JsString(text)
        )
      )
      progressChannel.push(json)
    }

    case Init(channel) => {
      this.progressChannel = channel
      val started = processActor != null
      if (processActor == null) {
        processActor = system.actorOf(Props[ProcessActor])
        //we just tell anything to our processing actor to let him know
        //that this actor is asking for the job
        //in real case we would pass real data like an id or something at least
        processActor ! 0
      }
      else createMessageTest(currentProgress)
    }


  }
}

/**
 * Actor dedicated to processing
 * You may want to do the real stuff here
 */
class ProcessActor extends Actor {

  def receive = {
    case _ => {
      var percent = 0
      while (percent < 100) {
        sender ! percent
        Thread.sleep(1000)
        percent += 10
      }
      sender ! percent
    }
  }

}

case class Init(channel: Concurrent.Channel[JsValue])