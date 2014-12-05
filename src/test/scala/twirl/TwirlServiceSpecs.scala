package twirl

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest

trait MySpecsTest extends Specs2RouteTest {
  def actorRefFactory = system
}

class TwirlServiceSpecs extends Specification with MySpecsTest with TwirlService {

  "The twirl service " should {
    "return something from the root" in {
      Get() ~> twirlRoute ~> check {
        responseAs[String] must contain("Hello Sherlock Holmes")
      }
    }
  }

}
