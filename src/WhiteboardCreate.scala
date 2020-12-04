import javafx.fxml.FXML
import javafx.scene.control.{Button, TextField}
import javafx.stage.{Stage, WindowEvent}
import logicMC.{Section, Whiteboard}

import java.awt.Window

class WhiteboardCreate() {

  @FXML
  private var a4Button: Button = _

  @FXML
  private var a3Button: Button = _

  @FXML
  private var whiteboardNameTextField: TextField = _

  @FXML
  private var createButton: Button = _

  var selectedSize: String = "A4"
  var section: Section = _
  var color: String = "White"

  var appState: (Section, Section) = _

  def onA4Clicked: Unit = {
    selectedSize = "A4"
  }

  def onA3Clicked: Unit = {
    selectedSize = "A3"
  }

  def getSize: (Double, Double) = {
    if(selectedSize== "A4") (210, 297)
    else (297, 420)
  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

  def onCreateClicked: Unit = {
   FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, color, getSize._1, getSize._2, whiteboardNameTextField.getText)
 ///sjssjs
    val stage = createButton.getScene.getWindow.asInstanceOf[Stage]
    stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
  }


}
