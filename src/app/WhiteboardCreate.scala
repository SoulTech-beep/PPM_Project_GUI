package app

import javafx.geometry.Insets
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import javafx.scene.{Node, Scene}
import javafx.stage.{Modality, Stage}
import logicMC.Auxiliary.{setUpPopupLabel, setUpPopupStage}
import logicMC.{Auxiliary, Section}

class WhiteboardCreate() {

  var appState:(Section, Section) = (null, null)
  var pagePicker = new PagePicker()
  var pane:Node = _

  var whiteboardNameTextField: TextField = _

  def initialize(pane:Node, updateVisualState: Section => Unit ): Unit = {

    this.pane = pane

    whiteboardNameTextField = Auxiliary.setUpPopupTextField("")
    VBox.setMargin(whiteboardNameTextField, new Insets(10, 10, 10, 10))

    val nameFieldLabel = setUpPopupLabel("Whiteboard name")

    val section = Auxiliary.setUpPopupSection(nameFieldLabel, whiteboardNameTextField)(" -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")

    VBox.setMargin(section, new Insets(0,10,10,10))

    val mainVBox = pagePicker.initialize()

    mainVBox.getChildren.add(mainVBox.getChildren.size()-1, section)

    val scene= new Scene(mainVBox)
    scene.getStylesheets.add("testStyle.css")

    Auxiliary.blurBackground(0, 30, 1000, pane)

    val stage:Stage = setUpPopupStage("Add Page")
    stage.setScene(scene)
    stage.show()

    stage.getIcons.add(new Image("images/addIcon.png"))
    stage.setResizable(false)

    stage.setOnCloseRequest(_ => {
      if (!whiteboardNameTextField.getText.isBlank) {
        FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, pagePicker.selectedColor.get(), pagePicker.selectedSize._1, pagePicker.selectedSize._2, whiteboardNameTextField.getText, pagePicker.selectedStyle)
        updateVisualState(FxApp.app_state._2)
      }
      Auxiliary.blurBackground(30, 0, 500, pane)
    })


  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

}
