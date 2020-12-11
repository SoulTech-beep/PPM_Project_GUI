package app

import javafx.beans.property.ObjectProperty
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.{Node, Scene}
import javafx.scene.control.{Button, TextField, ToggleButton, ToggleGroup}
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.{Modality, Stage, WindowEvent}
import logicMC.Auxiliary.{getSpacer, getStyledHBox}
import logicMC.PageSize.PageSize
import logicMC.PageStyle.PageStyle
import logicMC.{Auxiliary, PageSize, PageStyle, Section}


class WhiteboardCreate() {

  var appState:(Section, Section) = (null, null)
  var pagePicker = new PagePicker()
  var pane:Node = _

  var whiteboardNameTextField: TextField = _


  def initialize(pane:Node, updateVisualState: Section => Unit ): Unit = {
    this.pane = pane
    whiteboardNameTextField = Auxiliary.setUpPopupTextField("Whiteboard name")
    val section = Auxiliary.setUpPopupSection(whiteboardNameTextField)()

    val mainVBox = pagePicker.initialize()

    mainVBox.getChildren.add(mainVBox.getChildren.size()-1, section)

    val scene= new Scene(mainVBox)
    scene.getStylesheets.add("testStyle.css")

    Auxiliary.blurBackground(0, 30, 1000, pane)

    val stage:Stage = new Stage()
    stage.setScene(scene)
    stage.initModality(Modality.APPLICATION_MODAL)
    stage.show()
    stage.setTitle("Add Page")
    stage.getIcons.add(new Image("images/addIcon.png"))
    stage.setResizable(false)



    stage.setOnCloseRequest(_ => {
      if (!whiteboardNameTextField.getText.isBlank) {
        FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, pagePicker.selectedColor.get(), pagePicker.selectedSize._1, pagePicker.selectedSize._2, whiteboardNameTextField.getText, pagePicker.selectedStyle)
      }
      Auxiliary.blurBackground(30, 0, 500, pane)
      updateVisualState(FxApp.app_state._2)
    })


  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

}
