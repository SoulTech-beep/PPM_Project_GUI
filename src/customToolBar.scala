import ToolType.ToolType
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import logicMC.Eraser

object ToolType extends Enumeration {

  type ToolType = String

  val pen:String = "PEN"
  val marker:String = "MARKER"
  val eraser:String = "ERASER"
  val selector:String = "SELECTOR"
  val geometricShape:String = "GEOMETRIC_SHAPE"
  val text:String="TEXT"
  val image:String="IMAGE"
  val video:String = "VIDEO"
  val pdf:String = "PDF"
}

class customToolBar {

  @FXML
  var toolbar: ToolBar = _

  var selectedPen:Pen = null
  var selectedTool:ToolType = null

  val optionsHBox:HBox = new HBox()

  var buttonList:List[Button] = List()
  var penList:List[(Pen,ToolType)] = List()
  var eraserFinal:Eraser = new Eraser(new SimpleDoubleProperty(50))


  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={

    toolbar.getItems.clear()

    val penTool: Pen = Pen(0,Color.BLACK, new SimpleDoubleProperty(1), new SimpleDoubleProperty(1))
    val markerTool:Pen = Pen(1, Color.YELLOW, new SimpleDoubleProperty(5),new SimpleDoubleProperty(0.5))

    penList = (penTool, ToolType.pen) :: penList
    penList = (markerTool, ToolType.marker) :: penList

    selectedTool = ToolType.pen
    selectedPen = penTool

    setShapeButton()
    setEraserButton()

    getSeparator()

    //TODO we gotta add some separatores here!

    setPenButton("images/marker.png", ToolType.marker)
    setPenButton("images/ball-point.png", ToolType.pen)

    toolbar.getItems.add(optionsHBox)
    optionsHBox.setSpacing(10)
  }

  def getSeparator():Unit = {
    val separator = new Separator()
    separator.getStylesheets.add("separator.css")
    separator.setId("my-separator")
    separator.setPrefHeight(20)

    toolbar.getItems.add(0, separator)
  }

  def setShapeButton():Unit = {
    val shapeButton:Button = new Button()

    buttonList = shapeButton :: buttonList

    shapeButton.setOnAction(event => {
      selectTool(ToolType.eraser)
    })

    shapeButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/geometric.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    shapeButton.setGraphic(icon)

    toolbar.getItems.add(0,shapeButton)

  }

  def setEraserButton(): Unit = {

    val eraserButton:Button = new Button()

    buttonList = eraserButton::buttonList

    eraserButton.setOnAction(event => {
      selectTool(ToolType.eraser)
    })

    eraserButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/eraser.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    eraserButton.setGraphic(icon)

    toolbar.getItems.add(0,eraserButton)

  }

  def setPenButton(imageLocation: String, toolType: ToolType):Unit = {

    val penButton:Button = new Button()

    buttonList = penButton::buttonList

    penButton.setOnAction(event => {
      selectTool(toolType)
    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image(imageLocation))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def selectTool(toolName: ToolType): Unit = {

    optionsHBox.getChildren.clear()

    if(toolName == ToolType.pen || toolName == ToolType.marker){
      selectedTool = toolName
      selectedPen = penList.find(p => p._2 == toolName).get._1

      val dropDown = new MenuButton()

      val redColor = new Button()
      val blackColor = new Button()
      val blueColor = new Button()

      val setColors = new CustomMenuItem()
      val colorPickerMenu = new CustomMenuItem()

      val hboxColors = new HBox()
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor)
      hboxColors.setAlignment(Pos.CENTER)
      hboxColors.setSpacing(10)

      setColors.setContent(hboxColors)
      setColors.setHideOnClick(false)

      //TODO can't select custom colors!
      val colorPicker = new ColorPicker()
      colorPicker.setOnAction(a => {
        dropDown.setGraphic(getCircle(colorPicker.getValue))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(colorPicker.getValue), p1._2))
        })
      })
      //colorPicker.getStyleClass.add("button")

      colorPickerMenu.setContent(colorPicker)
      colorPickerMenu.setHideOnClick(false)


      def getCircle(color: Color):Circle = {
        val circle = new Circle()
        circle.setFill(color)
        circle.setRadius(10)

        circle
      }

      redColor.setGraphic(getCircle(Color.RED))
      blackColor.setGraphic(getCircle(Color.BLACK))
      blueColor.setGraphic(getCircle(Color.BLUE))

      redColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.RED))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.RED), p1._2))
        })
      })

      blueColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLUE))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.BLUE), p1._2))
        })
      })

      blackColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLACK))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.BLACK), p1._2))
        })
      })

      dropDown.getItems.addAll(setColors, colorPickerMenu)

      penList.foreach(p => if(p._2 == toolName)  {

        dropDown.setGraphic(getCircle(p._1.color))

        val slOpacity: Slider = getSliderMenu(p._1.opacity.get(), (0,1), 0.1)
        val slWidth: Slider = getSliderMenu(p._1.width.get(), (1,15))

        slOpacity.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p1 => if(p1._2 == toolName)  {
              penList = penList.updated(penList.indexOf(p1), (p1._1.changeOpacity(t1.doubleValue()), p1._2))
            })
          }
        })

        slWidth.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p2 => if(p2._2 == toolName)  {
              penList = penList.updated(penList.indexOf(p2), (p2._1.changeWidth(t1.doubleValue()), p2._2))
            })
          }
        })

        return optionsHBox.getChildren.addAll(dropDown, toMenuItem(slWidth,"images/width.png"), toMenuItem(slOpacity, "images/opacity.png"))
      })

    } else if(toolName == ToolType.eraser) {
      selectedTool = toolName
      val eraserRadius: Slider = getSliderMenu(eraserFinal.radius.get(), (30,100), 30)

      eraserRadius.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          eraserFinal = eraserFinal.changeRadius(t1.doubleValue())
        }
      })

      return optionsHBox.getChildren.add(toMenuItem(eraserRadius,"images/width.png"))

    }


  }



  def getSliderMenu(initial: Double, range:(Int,Int), majorTickUnit: Double = 2):Slider = {
    val slider = new Slider(range._1,range._2, initial)
    slider.setSnapToTicks(true)
    slider.setMajorTickUnit(majorTickUnit)
    slider.setShowTickLabels(true)
    slider
  }

  def toMenuItem(n: Node, imageLocation: String): MenuButton = {
    val menu: MenuButton = new MenuButton()
    val menuItem = new CustomMenuItem()
    menuItem.setContent(n)
    menuItem.setHideOnClick(false)

    val icon = new ImageView(new Image(imageLocation))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    menu.setGraphic(icon)
    menu.getItems.add(menuItem)

    menu
  }


}

object customToolBar {



}