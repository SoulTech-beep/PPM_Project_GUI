import ToolType.ToolType
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle

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

  val optionsHBox:HBox = new HBox()

  var currentPen:Pen = null
  var selectedTool:ToolType = null

  var buttonList:List[Button] = List()
  var penList:List[Pen] = List()


  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={

    setPenButton("images/marker.png",1, Color.YELLOW, 5, 0.5)
    setPenButton("images/ball-point.png",0, Color.BLACK, 1, 1)

    toolbar.getItems.add(optionsHBox)
    optionsHBox.setSpacing(10)
  }


  def setPenButton(imageLocation: String, id: Int, color: Color, width: Double, opacity:Double):Unit = {

    val penButton:Button = new Button()

    val pen:Pen = Pen(id, color, new SimpleDoubleProperty(width), new SimpleDoubleProperty(opacity))
    penList = pen::penList

    buttonList = penButton::buttonList

    penButton.setOnAction(event => {
      selectTool(ToolType.pen, id)
      currentPen = penList.find(p => p.id == pen.id).get
      println("selected pen: " + currentPen + " width: " + currentPen.width)
    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image(imageLocation))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def selectTool(toolName: String, id: Int): Unit = {

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
        changePenColor(id, colorPicker.getValue)

        //pen = pen.changeColor(colorPicker.getValue)
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
        changePenColor(id, Color.RED)
        //pen = pen.changeColor(Color.RED)
      })

      blueColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLUE))
        changePenColor(id, Color.BLUE)
        //pen = pen.changeColor(Color.BLUE)
      })

      blackColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLACK))
        changePenColor(id, Color.BLACK)
        //pen = pen.changeColor(Color.BLACK)
      })

      dropDown.setGraphic(getCircle(penList(penList.indexWhere(p => p.id==id)).color))

      dropDown.getItems.addAll(setColors, colorPickerMenu)

      optionsHBox.getChildren.clear()
      optionsHBox.getChildren.addAll(dropDown, getSliderMenu("images/width.png", id, false), getSliderMenu("images/opacity.png",id, true))
      //Opacidade
    }
  }

  def changePenColor(id:Int, color: Color):Unit = {
    val index = penList.indexWhere(p => p.id == id)
    val newPen = penList(index).changeColor(color)
    penList = penList.updated(index, newPen)
  }

  def getSliderMenu(imageLocation: String, id:Int, opacity: Boolean):MenuButton = {
    val menu = new MenuButton()

    val index = penList.indexWhere(p => p.id == id)

    val slider = new Slider(0,
      if(opacity) 1 else 10,
      if(opacity) penList(index).opacity.get() else penList(index).width.get()
    )

    slider.setSnapToTicks(true)
    slider.setMajorTickUnit(if(opacity) 0.1 else 1)
    slider.setShowTickLabels(true)

    slider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        if(opacity) {

          val newPen  = penList(index).changeOpacity(t1.doubleValue())
          penList = penList.updated(index, newPen)

        } else {
          val newPen  = penList(index).changeWidth(t1.doubleValue())
          penList = penList.updated(index, newPen)
        }

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
