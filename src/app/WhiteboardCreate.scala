package app

import app.PageSize.{A3, A4, PageSize}
import app.PageStyle.PageStyle
import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import javafx.stage.{Stage, WindowEvent}
import logicMC.Section


object PageStyle extends Enumeration {

  type PageStyle = String

  val DOTTED = "DOTTED"
  val SQUARED = "SQUARED"
  val LINED = "LINED"
}

object PageSize extends Enumeration {

  type PageSize = (Int, Int)

  val A4 = (210, 297)
  val A3 = (297, 420)
}

class WhiteboardCreate() {

  @FXML
  private var a4Button: Button = _

  @FXML
  private var a3Button: Button = _

  @FXML
  private var whiteboardNameTextField: TextField = _

  @FXML
  private var createButton: Button = _

  var selectedSize:PageSize = PageSize.A4
  var section: Section = _
  var color: String = "White"
  var selectedStyle: PageStyle = PageStyle.LINED

  var appState: (Section, Section) = _

  def onA4Clicked: Unit = {
    selectedSize = A4
  }

  def onA3Clicked: Unit = {
    selectedSize = A3
  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

  def onCreateClicked: Unit = {
   FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, color, selectedSize._1, selectedSize._2, whiteboardNameTextField.getText, selectedStyle)
 ///sjssjs
    val stage = createButton.getScene.getWindow.asInstanceOf[Stage]
    stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
  }


}
