import akka.actor.ActorSystem
import akka.actor.Props
import akka.io.IO
import spray.can.Http
import com.typesafe.scalalogging.slf4j.LazyLogging

object Boot extends App with LazyLogging {
  try {
    implicit val system = ActorSystem()

    import system.dispatcher

    val service = system.actorOf(Props(classOf[RestActor]), "rest-api")

    IO(Http) ! Http.Bind(service, interface = "0.0.0.0", port = 8080)
  } catch {
    case e: Throwable => logger.error("Error while booting", e)
  }
}
