package controllers

import play.api.data.Form
import play.api.data.Forms._

object LabelForm {
  case class Data(labels: String)

  val form = Form(
    mapping(
      "labels" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )
}
