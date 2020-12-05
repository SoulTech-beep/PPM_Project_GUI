package app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import logicMC.{Section, Whiteboard}

class HelloWorld extends Application {

  override def start(primaryStage: Stage): Unit = {

    primaryStage.setTitle("ChestNut")

    val fxmlLoader = new FXMLLoader(getClass.getResource("Controller.fxml"))

    val mainViewRoot: Parent = fxmlLoader.load()

    val scene = new Scene(mainViewRoot)
    scene.getStylesheets.add("contextcolor.css")

    primaryStage.setMinWidth(900)
    primaryStage.setMinHeight(200)

    primaryStage.getIcons.add(new Image("images/icon.png"))

    primaryStage.setScene(scene)
    primaryStage.show()

  }

}
object FxApp {

  val originalSection = new Section("0", "Macaco", List(
    Section("0.1", "António", List(
      Section("0.1.1", "Laranja", List(
        Section("0.1.1.1", "Henrique", List(
          Section("0.1.1.1.1", "Tiago", List(), List(new Whiteboard(0, "Pink", (20,20), List(), "AR", PageStyle.DOTTED)))
        ), List())
      ), List()),
      Section("0.1.2", "Sapo", List(), List())
    ), List()),
    Section("0.2", "Miguel", List(), List())
  ), List(new Whiteboard(0, "Blue", (20,20), List(), "IA", PageStyle.LINED)))

  var app_state = (originalSection, originalSection)

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HelloWorld], args: _*)
  }


}