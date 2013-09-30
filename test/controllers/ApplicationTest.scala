package controllers

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._

class ApplicationTest extends FunSuite with ShouldMatchers {

    test("[GET] / route shoud produce index page") { new WithApplication {
        val result = route(FakeRequest("GET", "/")).get
        status(result) should be (OK)
        contentType(result) should be(Some("text/html"))
        contentAsString(result) should include("Code Elevator: From the ground to the highest level")
    }}
}