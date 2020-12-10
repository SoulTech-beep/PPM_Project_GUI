package app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.paint.Color
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

  val originalSection = new Section("0", "My Files", List(
    Section("0.1", "University", List(
      Section("0.1.1", "1st Year", List(
        Section("0.1.1.1", "IP", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
        Section("0.1.1.2", "Linear Algebra", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),

      Section("0.1.2", "2nd Year", List(
        Section("0.1.2.1", "PCD", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
        Section("0.1.2.2", "PR", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),

      Section("0.1.3", "3rd Year", List(
        Section("0.1.3.1", "PPM", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
        Section("0.1.3.2", "IA", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),

    ), List()),
    Section("0.2", "Personal", List(
      Section("0.2.1", "Games", List(
        Section("0.2.1.1", "Minecraft", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Tuturial_Of_Something", PageStyle.DOTTED)
        )
        ),
        Section("0.2.1.2", "League of Legends", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),
      Section("0.2.2", "Images", List(
        Section("0.2.2.1", "2019", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Trip to France", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Summer on Algarve", PageStyle.DOTTED)
        )
        ),
        Section("0.2.2.2", "2020", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "My Sweet Home", PageStyle.DOTTED),
        )
        ),
      ), List()),
      Section("0.2.3", "Videos", List(
        Section("0.2.3.1", "Series", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Game of Thrones S01E01", PageStyle.DOTTED),
          new Whiteboard(1, Color.WHITE, (210*5,297*5), List(), "Game of Thrones S01E02", PageStyle.DOTTED)
        )
        ),
        Section("0.2.3.2", "Movies", List(), List(
          new Whiteboard(0, Color.WHITE, (210*5,297*5), List(), "Home Alone", PageStyle.DOTTED),
        )
        ),
      ), List()),
    ), List())


  ),List())

  var colorStyle:ColorStyle = new ColorStyle()


  var app_state: (Section, Section) = (originalSection, originalSection)

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HelloWorld], args: _*)

  }


}