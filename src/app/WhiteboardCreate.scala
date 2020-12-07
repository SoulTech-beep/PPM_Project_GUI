package app

import app.PageSize.PageSize
import app.PageStyle.PageStyle
import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Button, TextField, ToggleButton, ToggleGroup}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Line}
import javafx.stage.{Stage, WindowEvent}
import logicMC.Section


object PageStyle extends Enumeration {

  type PageStyle = String

  val DOTTED = "DOTTED"
  val SQUARED = "SQUARED"
  val LINED = "LINED"
  val SIMPLE = "SIMPLE"
}

object PageSize extends Enumeration {

  type PageSize = (Int, Int)

  val A4: (Int, Int) = (210, 297)
  val A3: (Int, Int) = (297, 420)
}

class WhiteboardCreate() {

  @FXML
  private var whiteboardNameTextField: TextField = _

  @FXML
  private var createButton: Button = _

  @FXML
  private var colorVBox:VBox = _

  @FXML
  private var sizeVBox:VBox = _

  @FXML
  private var pageVBox:VBox = _

  var selectedSizeGroup: ToggleGroup = new ToggleGroup()
  var selectedSize:PageSize = PageSize.A4

  var selectedStyle:PageStyle = PageStyle.SIMPLE
  var selectedStyleButtons:List[Pane] = List()

  var selectedColorShapes:List[Circle] = List()
  var selectedColor : Color = Color.WHITE

  var section: Section = _

  var appState: (Section, Section) = _

  @Override
  def initialize():Unit = {
    colorVBox.getChildren.add(getColorPicker())
    sizeVBox.getChildren.add(getSizePicker())

    pageVBox.getChildren.add(getStylePicker())

    whiteboardNameTextField.getStyleClass.add("customTextField")


  }

  def horizontalLine(width: Double, height: Double, pane: Pane): Unit = {
    val j = (10 to height.toInt - 10) by 10

    j.foreach(h => {
      val line = new Line()

      line.setStartX(5)
      line.setEndX(width-5)

      line.setStartY(h)
      line.setEndY(h)

      line.setStrokeWidth(2)
      line.setFill(Color.LIGHTGRAY)
      line.setStroke(Color.LIGHTGRAY)

      pane.getChildren.add(0, line)
    })
  }

  def verticalLines(width: Double, height: Double, pane: Pane): Unit = {
    val i = (10 to width.toInt - 10) by 10

    i.foreach(w => {
      val line = new Line()

      line.setStartX(w)
      line.setEndX(w)

      line.setStartY(5)
      line.setEndY(height-5)

      line.setStrokeWidth(2)
      line.setFill(Color.LIGHTGRAY)
      line.setStroke(Color.LIGHTGRAY)

      pane.getChildren.add(0, line)
    })
  }

  def getSquaredLines(width: Double, height: Double, pane: Pane):Unit = {
    verticalLines(width, height, pane)
    horizontalLine(width, height, pane)
  }

  def getSpacer:HBox = {
    val spacer = new HBox()
    HBox.setHgrow(spacer, Priority.ALWAYS)

    spacer
  }

  def setColorToggle(color:Color, setDefault: Boolean = false):Circle = {

    val graphic = new Circle()
    graphic.setFill(color)
    graphic.setRadius(15)

    selectedColorShapes = graphic :: selectedColorShapes

    graphic.setStroke(Color.BLACK)
    graphic.setStrokeWidth(1.0)

    if(setDefault){
      selectedColor = color
      graphic.setStrokeWidth(1.5)
      graphic.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);")
    }

    graphic.setOnMouseClicked(_ =>{
      selectedColor = color
      graphic.setStrokeWidth(1.5)
      graphic.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);")

      selectedColorShapes.foreach(f => {
        if(f != graphic) {
          f.setStrokeWidth(1)
          f.setStyle("")
        }
      })
    })

    graphic.setOnMouseEntered(_ => {
      graphic.setScaleX(1.1)
      graphic.setScaleY(1.1)
    })

    graphic.setOnMouseExited(_ => {
      graphic.setScaleX(1)
      graphic.setScaleY(1)
    })

    graphic
  }

  def getColorPicker(): HBox ={
    val hBox = new HBox()

    hBox.getChildren.addAll(getSpacer, setColorToggle(Color.WHITE,setDefault = true))
    hBox.getChildren.addAll(getSpacer, setColorToggle(Color.web("#ffeaa7")))
    hBox.getChildren.addAll(getSpacer, setColorToggle(Color.web("#636e72")), getSpacer)

    hBox.setAlignment(Pos.CENTER)
    hBox.setPadding(new Insets(10, 0, 10, 0))

    hBox
  }

  def dottedPage(width: Double, height: Double, pane: Pane): Unit = {
    val j = (10 to height.toInt - 10) by 10
    val i = (10 to width.toInt - 10) by 10

    j.foreach(h => {
      i.foreach(w => {
        val circle = new Circle()

        circle.setCenterX(w)
        circle.setCenterY(h)

        circle.setRadius(1.5)
        circle.setFill(Color.LIGHTGRAY)
        circle.setStroke(Color.LIGHTGRAY)

        pane.getChildren.add(0, circle)
      })
    })

  }

  def setStyleToggle(style: PageStyle,function:(Double, Double, Pane) => Unit, setDefault:Boolean = false):Pane = {
    val pane = new Pane()

    selectedStyleButtons = pane :: selectedStyleButtons

    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)))
    pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)))

    pane.setPrefSize(50, 50)
    function(50, 50, pane)

    if(setDefault){
      selectedStyle = style
      pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1.5))))
      pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);")
    }

    pane.setOnMouseClicked(_ =>{
      selectedStyle = style
      pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1.5))))
      pane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);")

      selectedStyleButtons.foreach(f => {
        if(f != pane) {
          f.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)))
          f.setStyle("")
        }
      })
    })

    pane.setOnMouseEntered(_ => {
      pane.setScaleX(1.1)
      pane.setScaleY(1.1)
    })

    pane.setOnMouseExited(_ => {
      pane.setScaleX(1)
      pane.setScaleY(1)
    })

    pane
  }

  def getStylePicker():HBox = {
    val hBox = new HBox()

    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SIMPLE,(_:Double,_:Double,_:Pane)=>(), setDefault = true), getSpacer, setStyleToggle(PageStyle.DOTTED,dottedPage))
    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SQUARED,getSquaredLines), getSpacer)
    hBox.getChildren.addAll(setStyleToggle(PageStyle.LINED,horizontalLine),getSpacer)

    hBox.setAlignment(Pos.CENTER)
    hBox.setPadding(new Insets(10, 0, 10, 0))

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
    val hBox = new HBox()

    hBox.getChildren.addAll(getSpacer, setSize("A4", pageSize= PageSize.A4, setDefault = true))
    hBox.getChildren.addAll(getSpacer, setSize("A3", PageSize.A3), getSpacer)

    hBox.setAlignment(Pos.CENTER)
    hBox.setPadding(new Insets(10, 0, 10, 0))

    hBox
  }

  def setState(appStateController: (Section, Section)): Unit = {
    appState = appStateController
  }

  def onCreateClicked(): Unit = {
    FxApp.app_state = Section.addWhiteboardWithValues(appState._1, appState._2, selectedColor, selectedSize._1, selectedSize._2, whiteboardNameTextField.getText, selectedStyle)
    val stage = createButton.getScene.getWindow.asInstanceOf[Stage]
    stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
  }

}
