package controllers

import java.io.File

import javax.inject.Inject
import models.Label
import play.api.data.Form
import play.api.mvc._
import services.LabelWriter

import scala.concurrent.ExecutionContext.Implicits.global

class LabelsController @Inject()(messagesAction: MessagesActionBuilder, components: ControllerComponents) extends AbstractController(components) {
  import LabelForm._

  private val postUrl = routes.LabelsController.createLabels()
  private val exampleLabels = LabelForm.form.fill(
    Data("label 1!;line 2 of label 1;line 3 of label 1;line 4 of label 1\nlabel 2!;line 2 of label 2;line 3 of label 2;line 4 of label 2")
  )

  def labels: Action[AnyContent] = messagesAction { implicit request: MessagesRequestHeader =>
    Ok(views.html.labels(exampleLabels, postUrl))
  }

  def createLabels: Action[AnyContent] = messagesAction { implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[Data] =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.labels(formWithErrors, postUrl))
    }

    val successFunction = { data: Data =>
      // We do not allow quoted records or line separators
      val lines = data.labels.split("\r?\n")
      val labels = lines.map(line => {
        val cols = line.split(";")
        // Pad with empty strings if less than four elements
        val colsPadded = cols ++ Array.fill(4 - math.min(cols.length, 4))("")
        Label(colsPadded(0), colsPadded(1), colsPadded(2), colsPadded(3))
      })
      val file = File.createTempFile("labels", ".pdf")
      file.deleteOnExit()

      LabelWriter.createLabels(labels, file)

      Ok.sendFile(
        content = file,
        inline = false,
        fileName = _ => Some("labels.pdf"),
        onClose = () => file.delete()
      )
    }

    val formValidationResult = form.bindFromRequest
    formValidationResult.fold(errorFunction, successFunction)
  }
}
