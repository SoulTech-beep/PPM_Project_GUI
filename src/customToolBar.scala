import Tools.Tools
import javafx.fxml.FXML
import javafx.scene.control.{Button, ChoiceBox, ComboBox, MenuButton, MenuItem, SplitMenuButton, ToolBar}
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.util.Callback


object Tools extends Enumeration {

  type Tools = String

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

  var selectedTool:Tools = Tools.pen

  val optionsHBox:HBox = new HBox()

  var toolsList:List[Button] = List()


  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={
    setPenButton()

    toolbar.getItems.add(optionsHBox)
    optionsHBox.setSpacing(10)
  }

  def setButton(name: String): Button = {
    val newButton: Button = new Button()

    toolsList = newButton :: toolsList

    newButton.setOnAction(event => {
      selectTool(name)
    })
    newButton
  }

  def setPenButton():Unit = {

    val penButton:Button = new Button()

    toolsList = penButton::toolsList

    penButton.setOnAction(event => {
        selectTool(Tools.pen)
    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/ball-point.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def selectTool(toolName: String): Unit = {
      if(toolName == Tools.pen){
        //Cor
        val dropDown = new MenuButton()

        val redColor = new MenuItem("Red")
        val blackColor = new MenuItem("Black")
        val blueColor = new MenuItem("Blue")

        def getCircle(color: Color):Circle = {
          val circle = new Circle()
          circle.setFill(color)
          circle.setRadius(10)
          circle
        }

        redColor.setGraphic(getCircle(Color.RED))
        blackColor.setGraphic(getCircle(Color.BLACK))
        blueColor.setGraphic(getCircle(Color.BLUE))

        redColor.setOnAction(p => dropDown.setGraphic(getCircle(Color.RED)))
        blueColor.setOnAction(p => dropDown.setGraphic(getCircle(Color.BLUE)))
        blackColor.setOnAction(p => dropDown.setGraphic(getCircle(Color.BLACK)))

        dropDown.setGraphic(getCircle(Color.RED))

        dropDown.getItems.addAll(redColor, blackColor, blueColor)

        optionsHBox.getChildren.clear()
        optionsHBox.getChildren.add(dropDown)



        //Largura
        //Opacidade
      }
  }

  def teste():Unit = {


  }


}

object customToolBar {



}
