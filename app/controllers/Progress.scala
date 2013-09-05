package controllers

import play.api.mvc._


import play.api.libs.iteratee._


import play.api.libs.json.{JsString, JsNumber, Json, JsValue}

/**
 *
 * This the minimal implementation to use a channel to convey progress update to the
 *
 * User: Kayrnt
 * Date: 26/08/13
 * Time: 22:01
 */
object Progress extends Controller {

  /**
   * @return the websocket we use then to transmit progress
   */
  def process = WebSocket.using[JsValue] {
    request => start

  }

  /**
   * Creates the channel and start the processing
   */
  def start: (Iteratee[JsValue, _], Enumerator[JsValue]) = {
    //Concurrent.broadcast is the easy way to link an enumerator to a channel for later use
    val (progressEnumerator, progressChannel) = Concurrent.broadcast[JsValue]
    //we don't really care what the client sent in this sample but you might want to do something
    //if so use a proper iteratee
    val iteratee = Iteratee.skipToEof[JsValue].mapDone {
      _ => println("Disconnected")
    }
    new Thread(new Runnable {
      def run() {
        updateProgressTest(progressChannel)
      }
    }).start

    (iteratee, progressEnumerator)
  }


  def updateProgressTest(progressChannel: Concurrent.Channel[JsValue]) = {
    new Thread(new Runnable {
      def run() {
        var progress = 0
        while (progress < 100) {
          notifyProgress(Message("In progress", progress), progressChannel);
          Thread.sleep(1000)
          progress += 10
        }
        notifyProgress(Message("Done", progress + 5), progressChannel);
      }
    }).run
  }

  def notifyProgress(msg: Message, channel: Concurrent.Channel[JsValue]) {
    val json: JsValue = Json.toJson(
      Map(
        "value" -> JsNumber(msg.percent),
        "text" -> JsString(msg.message)
      )
    )
    channel.push(json)
  }

}

case class Enum(enumerator: Enumerator[JsValue])

case class Percent(percent: Int)

case class Message(message: String, percent: Int)

case class Initialisation(state: Boolean)

