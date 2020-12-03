import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.{Bounds, Insets, Pos}
import javafx.scene.control.{Button, ContextMenu, MenuItem, ScrollPane}
import javafx.scene.input.MouseButton
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{Polyline, Rectangle}

class whiteboardScroller {

  val pages:List[Pane] = List()
  val toolbar:customToolBar = new customToolBar()


}

object whiteboardScroller{

  def createPage(backgroundColor: Color, width : Double, height: Double, toolBar: customToolBar):Pane = {
    val page = new Pane()
    page.setPrefSize(width, height)
    page.setMaxSize(width, height)
    page.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)))

    var camadas:List[Polyline] = List()
    var currentLayer = new Polyline()

    page.setOnMousePressed(event => {

      currentLayer = new Polyline()
      val tempCurrentLayer = currentLayer

      currentLayer.setOnContextMenuRequested(click => {
        val delete = new MenuItem("Delete")
        val contextMenu = new ContextMenu(delete)

        delete.setOnAction(action => {
          camadas = camadas.filter(p=>p!=currentLayer)
          page.getChildren.remove(tempCurrentLayer)
        })

        contextMenu.show(currentLayer, click.getScreenX, click.getScreenY)

      })

      page.getChildren.add(currentLayer)

      currentLayer.setStrokeWidth(toolBar.currentPen.width.get())
      currentLayer.setOpacity(toolBar.currentPen.opacity.get())
      currentLayer.setSmooth(true)
      currentLayer.setStroke(Color.RED)
      currentLayer.getPoints.add(event.getX)
      currentLayer.getPoints.add(event.getY)

    })

    page.setOnMouseDragged(event => {
      if(event.getX < page.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < page.getHeight) {
        currentLayer.getPoints.add(event.getX)
        currentLayer.getPoints.add(event.getY)
      }
    })

    page
  }

  def getCanvas(toolBar:customToolBar):ScrollPane = {

    var currentLayer = new Polyline()
    var selecting = false
    var  dragBox = new Rectangle(0, 0, 0, 0);
    var camadas = List(currentLayer)

    /////



    //////7

    val canvas = new ScrollPane()
    val pages = new VBox()

    val page1 = new Pane()
    val page2 = new Pane()
    val page3 = whiteboardScroller.createPage(Color.GREENYELLOW, 600, 600, toolBar)

    val pag = List(page3,page1, page2)

    page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    page2.setBackground(new Background(new BackgroundFill(Color.web("#2d3436"), CornerRadii.EMPTY, Insets.EMPTY)));

    page1.setPrefSize(1200, 800)
    page2.setPrefSize(1200, 800)

    pages.setMaxWidth(1200)


    var selectButton = new Button("Selecionar")
    selectButton.setOnMouseClicked(p => selecting = !selecting)

    pages.getChildren.addAll(page3, selectButton,page1,page2)


    pages.setSpacing(80)
    pages.setAlignment(Pos.CENTER)

    canvas.viewportBoundsProperty().addListener(new ChangeListener[Bounds] {
      override def changed(observableValue: ObservableValue[_ <: Bounds], t: Bounds, t1: Bounds): Unit = {
        if(t1.getMaxX > 1200) {
          pag.foreach(p => p.setTranslateX(t1.getCenterX - 1200/2))
        }
        //TODO when viewport is small and we use the windows fullscreen, then it gets messed up!

      }
    })

    canvas.setContent(pages)


    canvas.setOnMouseClicked(event => {
      //Left click zooms, right click unzooms
      if(event.isControlDown && event.getButton == MouseButton.PRIMARY){
        canvas.setScaleX( canvas.getScaleX + canvas.getScaleX*0.1)

      }else if(event.isControlDown && event.getButton == MouseButton.SECONDARY){
        canvas.setScaleX( canvas.getScaleX - canvas.getScaleX*0.1)
      }

    })

    canvas.scaleYProperty().bind(canvas.scaleXProperty())

    canvas
  }


}


