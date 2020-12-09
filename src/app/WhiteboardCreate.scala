package app

import app.PageStyle.PageStyle
import javafx.beans.property.ObjectProperty
import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Button, TextField, ToggleButton, ToggleGroup}
import javafx.scene.input.KeyCode
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}
import logicMC.Auxiliary.{getSpacer, getStyledHBox}
import logicMC.PageSize.PageSize
import logicMC.{Auxiliary, PageSize, Section}


object PageStyle extends Enumeration {

  type PageStyle = String

  val DOTTED = "DOTTED"
  val SQUARED = "SQUARED"
  val LINED = "LINED"
  val SIMPLE = "SIMPLE"
}

class WhiteboardCreate() {

  @FXML
  private var whiteboardNameTextField: TextField = _

  @FXML
  private var colorVBox:VBox = _

  @FXML
  private var sizeVBox:VBox = _

  @FXML
  private var pageVBox:VBox = _

  @FXML
  private var mainVBox :VBox = _

  @FXML
  private var whiteboardNameVBox:VBox = _

  var selectedSizeGroup: ToggleGroup = new ToggleGroup()
  var selectedSize:PageSize = PageSize.A4

  var selectedStyle:PageStyle = PageStyle.SIMPLE
  var selectedStyleButtons:List[Pane] = List()

  var selectedColor : ObjectProperty[Color] = null

  var section: Section = _

  var appState: (Section, Section) = _

  var createButton: Button = _

  @Override
  def initialize():Unit = {
    colorVBox.getChildren.add(getColorPicker)
    sizeVBox.getChildren.add(getSizePicker())

    pageVBox.getChildren.add(getStylePicker())

    whiteboardNameTextField.getStyleClass.add("customTextField")
    whiteboardNameTextField.setOnKeyPressed(p => {
      if(p.getCode == KeyCode.ENTER){
        onCreateClicked()
      }
    })

    createButton = setCreateButton()
    mainVBox.getChildren.add(createButton)
  }

  def horizontalLine(width: Double, height: Double, pane: Pane): Unit = {
    Auxiliary.horizontalLine(width, height, pane, 10)
  }

  def verticalLines(width: Double, height: Double, pane: Pane): Unit = {
   Auxiliary.verticalLines(width, height, pane, 10)
  }

  def dottedPage(width: Double, height: Double, pane: Pane): Unit = {
    Auxiliary.dottedPage(width, height, pane, 10)
  }

  def getSquaredLines(width: Double, height: Double, pane: Pane):Unit = {
    verticalLines(width, height, pane)
    horizontalLine(width, height, pane)
  }


  def getColorPicker: HBox ={
    val colorPicker = Auxiliary.getColorPicker()

    selectedColor = colorPicker._2

    colorPicker._1
  }

  def setStyleToggle(style: PageStyle,function:(Double, Double, Pane) => Unit, setDefault:Boolean = false):Pane = {
    val pane = new Pane()

    selectedStyleButtons = pane :: selectedStyleButtons

    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)))
    resetBorder(pane)

    pane.setPrefSize(50, 50)
    function(50, 50, pane)

    def select():Unit = {
      selectedStyle = style
      pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1.5))))
      pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);")
    }

    def resetBorder(paneToReset:Pane):Unit = {
      paneToReset.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)))
    }

    if(setDefault){
      select()
    }

    pane.setOnMouseClicked(_ =>{
     select()

      selectedStyleButtons.foreach(f => {
        if(f != pane) {
          resetBorder(f)
          f.setStyle("")
        }
      })
    })

    Auxiliary.setScaleAnimation(pane)

    pane
  }

  def getStylePicker():HBox = {
    val hBox = getStyledHBox()

    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SIMPLE,(_:Double,_:Double,_:Pane)=>(), setDefault = true), getSpacer, setStyleToggle(PageStyle.DOTTED,dottedPage))
    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SQUARED,getSquaredLines), getSpacer)
    hBox.getChildren.addAll(setStyleToggle(PageStyle.LINED,horizontalLine),getSpacer)

    hBox
  }

  def setSize(text:String,pageSize: PageSize , setDefault: Boolean = false):ToggleButton = {
    val toggleButton = new ToggleButton(text)
    toggleButton.setPadding(new Insets(5,20,5,20))
    toggleButton.setToggleGroup(selectedSizeGroup)
    toggleButton.setSelected(setDefault)

    toggleButton.setOnAction(_ => {
      selectedSize = pageSize
    })


    toggleButton.getStyleClass.add("start-stop")
    toggleButton.setStyle("-fx-background-radius: 25px;")

    toggleButton
  }

  def getSizePicker():HBox = {
    val hBox = getStyledHBox()

    hBox.getChildren.addAll(getSpacer, setSize("A4", pageSize= PageSize.A4, setDefault = true))
    hBox.getChildren.addAll(getSpacer, setSize("A3", PageSize.A3), getSpacer)

    hBox
  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

  def onCreateClicked(): Unit = {
    if(!whiteboardNameTextField.getText.isBlank){
      FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, selectedColor.get(), selectedSize._1, selectedSize._2, whiteboardNameTextField.getText, selectedStyle)
    }
    val stage = createButton.getScene.getWindow.asInstanceOf[Stage]
    stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))

  }

  def setCreateButton():Button = {
    val button = new Button()

    VBox.setMargin(button, new Insets(0, 10, 10, 10))

    button.setText("Create")
    button.setFont(Auxiliary.getFont(16))

    val style = "-fx-background-radius:15px; -fx-text-fill: white;"

    button.setStyle(style + "-fx-background-color:#55efc4;")

    button.setOnMouseEntered(_ =>{
      button.setStyle(style + "-fx-background-color:#00b894;")
    })

    button.setOnMouseExited(_ => {
      button.setStyle(style + "-fx-background-color:#55efc4;")
    })

    button.setMaxWidth(Double.MaxValue)
    button.setPrefHeight(35)

    button.setOnAction(_ => {
      onCreateClicked()
    })

    button
  }

}
