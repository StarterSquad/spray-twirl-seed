import akka.actor.Actor
import twirl.TwirlService

class RestActor extends Actor with TwirlService {
  def actorRefFactory = context
  def receive = runRoute(twirlRoute)
}