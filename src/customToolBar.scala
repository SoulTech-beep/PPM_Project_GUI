import ToolType.ToolType
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import logicMC.Pen


object ToolType extends Enumeration {

  type ToolType = String

  val pen:String = "PEN"
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

  var selectedTool:ToolType = ToolType.pen

  val optionsHBox:HBox = new HBox()

  var buttonList:List[Button] = List()
  var pensList:List[Pen] = List()

  var pen: Pen = Pen(0,Color.BLACK, 1, 1)
  var marker:Pen = Pen(1, Color.YELLOW, 5, 0.5)


  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={
    setPenButton("images/marker.png")
    setPenButton("images/ball-point.png")

    toolbar.getItems.add(optionsHBox)
    optionsHBox.setSpacing(10)
  }

  def setButton(name: String): Button = {
    val newButton: Button = new Button()

    buttonList = newButton :: buttonList

    newButton.setOnAction(event => {
      selectTool(name)
    })
    newButton
  }

  def setPenButton(imageLocation: String):Unit = {

    val penButton:Button = new Button()

    buttonList = penButton::buttonList

    penButton.setOnAction(event => {
        selectTool(ToolType.pen)
    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image(imageLocation))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def selectTool(toolName: String): Unit = {

      if(toolName == ToolType.pen){
        //Cor
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
          pen = pen.changeColor(colorPicker.getValue)
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
          pen = pen.changeColor(Color.RED)
        })

        blueColor.setOnAction(p => {
          dropDown.setGraphic(getCircle(Color.BLUE))
          pen = pen.changeColor(Color.BLUE)
        })

        blackColor.setOnAction(p => {
          dropDown.setGraphic(getCircle(Color.BLACK))
          pen = pen.changeColor(Color.BLACK)
        })

        dropDown.setGraphic(getCircle(Color.RED))

        dropDown.getItems.addAll(setColors, colorPickerMenu)


        optionsHBox.getChildren.clear()
        optionsHBox.getChildren.addAll(dropDown, getSliderMenu("images/width.png", pen.id), getSliderMenu("images/opacity.png", pen.id))
        //Opacidade
      }
  }

  def getSliderMenu(imageLocation: String, id:Int):MenuButton = {
    val menu = new MenuButton()
    val slider = new Slider(0,10, 5)
    slider.setSnapToTicks(true)
    slider.setMajorTickUnit(1)
    slider.setShowTickLabels(true)

   slider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {

      }

    })

    val opacityMenuItem = new CustomMenuItem()
    opacityMenuItem.setContent(slider)
    opacityMenuItem.setHideOnClick(false)

    val icon = new ImageView(new Image(imageLocation))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    menu.setGraphic(icon)
    menu.getItems.add(opacityMenuItem)

    menu
  }


}

object customToolBar {



}
