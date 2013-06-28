package twirl

import spray.routing.HttpService
import spray.httpx.TwirlSupport
import domain.User

trait TwirlService extends HttpService with TwirlSupport {

  val twirlRoute = pathPrefix("") {
    complete {html.index.render(User("Sherlock Holmes"))}
  }

}
