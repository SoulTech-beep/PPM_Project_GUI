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

  var selectedPen:Pen = null

  val optionsHBox:HBox = new HBox()

  var buttonList:List[Button] = List()
  var penList:List[Pen] = List()

  var pen: Pen = Pen(0,Color.BLACK, new SimpleDoubleProperty(1.0), new SimpleDoubleProperty(1))
  var marker:Pen = Pen(1, Color.YELLOW, new SimpleDoubleProperty(5),new SimpleDoubleProperty(0.5))

  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={
    penList = pen :: penList
    penList = marker :: penList

    setPenButton("images/marker.png",1)
    setPenButton("images/ball-point.png",0)

    toolbar.getItems.add(optionsHBox)
    optionsHBox.setSpacing(10)
  }


  def setPenButton(imageLocation: String, id: Int):Unit = {

    val penButton:Button = new Button()

    buttonList = penButton::buttonList

    penButton.setOnAction(event => {
      selectTool(ToolType.pen, id)
    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image(imageLocation))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def selectTool(toolName: ToolType, id: Int): Unit = {

    if(toolName == ToolType.pen){
      selectedPen = penList.find(p => p.id == id).get

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
        penList.foreach(p1 => if(p1.id == id)  {
          penList = penList.updated(penList.indexOf(p1), p1.changeColor(colorPicker.getValue))
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
        penList.foreach(p1 => if(p1.id == id)  {
          penList = penList.updated(penList.indexOf(p1), p1.changeColor(Color.RED))
        })
      })

      blueColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLUE))
        penList.foreach(p1 => if(p1.id == id)  {
          penList = penList.updated(penList.indexOf(p1), p1.changeColor(Color.BLUE))
        })
      })

      blackColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLACK))
        penList.foreach(p1 => if(p1.id == id)  {
          penList = penList.updated(penList.indexOf(p1), p1.changeColor(Color.BLACK))
        })
      })

      dropDown.getItems.addAll(setColors, colorPickerMenu)

      optionsHBox.getChildren.clear()

      penList.foreach(p => if(p.id == id)  {

        dropDown.setGraphic(getCircle(p.color))

        val slOpacity: Slider = getSliderMenu(p.opacity.get(), (0,1), 0.1)
        val slWidth: Slider = getSliderMenu(p.width.get(), (1,15))

        slOpacity.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p1 => if(p1.id == id)  {
              penList = penList.updated(penList.indexOf(p1), p1.changeOpacity(t1.doubleValue()))
            })
          }
        })

        slWidth.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p2 => if(p2.id == id)  {
              penList = penList.updated(penList.indexOf(p2), p2.changeWidth(t1.doubleValue()))
            })
          }
        })

        return optionsHBox.getChildren.addAll(dropDown, toMenuItem(slWidth,"images/width.png"), toMenuItem(slOpacity, "images/opacity.png"))
      })


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