import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.{Bounds, Insets, Pos}
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.control.{Button, ContextMenu, MenuItem, ScrollPane}
import javafx.scene.input.MouseButton
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{Path, Polyline, Rectangle}

class whiteboardScroller {

}


object whiteboardScroller{

  def getCanvas():ScrollPane = {

    var currentLayer = new Polyline()
    var selecting = false
    var  dragBox = new Rectangle(0, 0, 0, 0);
    var camadas = List(currentLayer)

    /////

    val page3 = new Pane(dragBox)
    page3.setPrefSize(400,400)
    page3.setMaxSize(400,400)


    page3.setOnMousePressed(event => {


      currentLayer = new Polyline()
      var coiso = currentLayer

      currentLayer.setOnContextMenuRequested(click=>{
        val delete = new MenuItem("Delete")
        val contextMenu = new ContextMenu(delete)

        delete.setOnAction(action => {
          camadas = camadas.filter(p=>p!=currentLayer)
          println("GONNA REMOVE")
          page3.getChildren.remove(coiso)
        })

        contextMenu.show(currentLayer, click.getScreenX, click.getScreenY)
      })

      page3.getChildren.add(currentLayer)

      currentLayer.setStrokeWidth(15)
      currentLayer.setOpacity(0.4)
      currentLayer.setSmooth(true)
      currentLayer.setStroke(Color.RED)
      currentLayer.getPoints.add(event.getX)
      currentLayer.getPoints.add(event.getY)


    })

    page3.setOnMouseDragged(event => {

      //graphicsContext.lineTo(event.getX, event.getY)
      //graphicsContext.stroke()


      if(event.getX < page3.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < page3.getHeight) {


        currentLayer.getPoints.add(event.getX)
        currentLayer.getPoints.add(event.getY)
      }

    })

    //////7

    val canvas = new ScrollPane()
    val pages = new VBox()

    val page1 = new Pane()
    val page2 = new Pane()

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


