package controllers

import java.io.{BufferedOutputStream, FileOutputStream}
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang3.ObjectUtils.Null
import play.api._
import play.api.data.Form
import scala.concurrent.{Await, Future}

import play.api.libs.json._
import play.api.mvc.AnyContent
import play.api.libs.ws.{WS, WSRequest, WSClient}
import play.api.mvc._
import play.api.Play.current
import scala.concurrent.{Future, Await}
import scala.collection.immutable.ListMap
import scala.concurrent.duration._
import play.api.libs.ws.WS
import play.api.libs.functional.syntax._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Failure, Success}
import scala.concurrent._
import ExecutionContext.Implicits.global

case class parentobj(name: String,actualid: String,VFrequency: String,Strength: String,EFrequency: String)
case class childobj(actualid: String,name: String,VFrequency: String,Strength: String,EFrequency: String)

class Application extends Controller {

  def options(path: String) = Action {
    Ok("")
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def display3 = Action {
    Ok(views.html.ved())
  }

  val url = "http://40.124.54.95:8082/"
  val url2 = "http://192.150.23.156:8082/"


  def getVertices3 = Action.async(BodyParsers.parse.json) { implicit request =>
    val tt = request.body

    val item = (tt \ "news").get.toString().replaceAll("^\"|\"$", "")
    println(item)


    val future = WS.url(url + "Rest_API_Jersey/sigmoid/index/" + item).get().map {
      response => {
        val temp = response.json
        temp
      }
    }

    future.map(
      t => Ok(t)
    )
  }


  def getIntialData = Action.async(BodyParsers.parse.json)(implicit request => {

    val t = (request.body \ "input").get.toString().replaceAll("^\"|\"$", "")
    val future = WS.url(url + "Rest_API_Jersey/sigmoid/data/" + t + ".csv").get().map(response => {
      response.body
    })

    future.map(itr =>
      Ok(itr.toString)
    )

  })

  def getData = Action.async(BodyParsers.parse.json)(implicit request=> {

    val gotdata = request.body
    val dropdown = (gotdata \ "inputtimeseries").get.toString().replaceAll("^\"|\"$", "")
    val value = (gotdata \ "value").get.toString().replaceAll("^\"|\"$", "")
    val gradient = (gotdata \ "gradient").get.toString().replaceAll("^\"|\"$", "")
    val time = (gotdata \ "time").get.toString().replaceAll("^\"|\"$", "")
    val priority = (gotdata \ "priority").get.toString().replaceAll("^\"|\"$", "")

    val future = WS.url(url + "Rest_API_Jersey/sigmoid/patterntable/"+ dropdown + ".csv," + value + "," + gradient + "," + time + "," + priority).get().map(response => {
      response.body
    })

    future.map(t => {
      Ok(t.toString())
    })

    })

  def addFeature = Action.async(BodyParsers.parse.json)(implicit request => {

    val feature = (request.body \ "feature" ).get.toString().replaceAll("^\"|\"$", "")
    val valuefeature = (request.body \ "value").get.toString().replaceAll("^\"|\"$", "")

    val future = WS.url(url + "Rest_API_Jersey/sigmoid/addfeature/"+ feature +"," + valuefeature).get().map(response => {
      //println("body is " + response.body)
      response.body
    })

    future.map(r => {Ok(r.toString)})
  })

  def createGraph = Action {
    val future = WS.url(url + "Rest_API_Jersey/sigmoid/creategraph").get().map(response => {
      response.body
    })

    Ok("")
  }

  def getNeighbourGraph = Action.async(BodyParsers.parse.json)(implicit request => {

    val featurename = (request.body \ "featurename").get.toString().replaceAll("^\"|\"$", "");

    val future = WS.url(url + "Rest_API_Jersey/sigmoid/featurechildren/" + featurename).get().map(response => {
      //println("body is " + response.body)
      response.body
    })

    future.map(temp => {Ok(temp.toString)})
  })

}