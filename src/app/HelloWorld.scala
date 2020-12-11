package app

import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.image.Image
import javafx.scene.{Parent, Scene}
import javafx.stage.Stage
import logicMC._

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
          new Whiteboard(0, Colors.c1, PageSize.A3, List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Colors.c2, PageSize.A4, List(), "Notes", PageStyle.LINED)
        )
        ),
        Section("0.1.1.2", "Linear Algebra", List(), List(
          new Whiteboard(0, Colors.c1, PageSize.A3, List(), "Test revision exercises", PageStyle.SIMPLE),
          new Whiteboard(1, Colors.c3, PageSize.A3, List(), "Exercises", PageStyle.DOTTED)
        )
        ),
      ), List()),

      Section("0.1.2", "2nd Year", List(
        Section("0.1.2.1", "PCD", List(), List(
          new Whiteboard(0, Colors.c2, PageSize.A4, List(), "Summary", PageStyle.SQUARED),
          new Whiteboard(1, Colors.c3, PageSize.A4, List(), "Notes", PageStyle.DOTTED)
        )
        ),
        Section("0.1.2.2", "PR", List(), List(
          new Whiteboard(0, Colors.c3, PageSize.A3, List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Colors.c2, PageSize.A4, List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),

      Section("0.1.3", "3rd Year", List(
        Section("0.1.3.1", "PPM", List(), List(
          new Whiteboard(0, Colors.c1, PageSize.A4, List(), "Summary", PageStyle.DOTTED),
          new Whiteboard(1, Colors.c2, PageSize.A3, List(), "Notes", PageStyle.DOTTED)
        )
        ),
        Section("0.1.3.2", "IA", List(), List(
          new Whiteboard(0, Colors.c3, PageSize.A3, List(), "Summary", PageStyle.SQUARED),
          new Whiteboard(1, Colors.c2, PageSize.A4, List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),

    ), List()),
    Section("0.2", "Personal", List(
      Section("0.2.1", "Games", List(
        Section("0.2.1.1", "Minecraft", List(), List(
          new Whiteboard(0, Colors.c1, PageSize.A3, List(), "Notes", PageStyle.LINED),
          new Whiteboard(1, Colors.c2, PageSize.A3, List(), "Tuturial_Of_Something", PageStyle.SIMPLE)
        )
        ),
        Section("0.2.1.2", "League of Legends", List(), List(
          new Whiteboard(0, Colors.c2, PageSize.A4, List(), "Notes", PageStyle.DOTTED)
        )
        ),
      ), List()),
      Section("0.2.2", "Images", List(
        Section("0.2.2.1", "2019", List(), List(
          new Whiteboard(0, Colors.c3, PageSize.A3, List(), "Notes", PageStyle.SIMPLE),
          new Whiteboard(1, Colors.c3, PageSize.A4, List(), "Summer on Algarve", PageStyle.SQUARED)
        )
        ),
        Section("0.2.2.2", "2020", List(), List(
          new Whiteboard(0, Colors.c1,PageSize.A3, List(), "My Sweet Home", PageStyle.LINED),
        )
        ),
      ), List()),
      Section("0.2.3", "Videos", List(
        Section("0.2.3.1", "Series", List(), List(
          new Whiteboard(0, Colors.c3, PageSize.A3, List(), "Game of Thrones S01E01", PageStyle.SIMPLE),
          new Whiteboard(1, Colors.c1, PageSize.A4, List(), "Game of Thrones S01E02", PageStyle.DOTTED)
        )
        ),
        Section("0.2.3.2", "Movies", List(), List(
          new Whiteboard(0, Colors.c1, PageSize.A3, List(), "Home Alone", PageStyle.DOTTED),
        )
        ),
      ), List()),
    ), List())


  ),List())

  var app_state: (Section, Section) = (originalSection, originalSection)

  def main(args: Array[String]): Unit = {
    Application.launch(classOf[HelloWorld], args: _*)

  }


}