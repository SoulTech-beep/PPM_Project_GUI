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

  def  initDraw( gc:GraphicsContext):Unit = {
    val canvasWidth = gc.getCanvas.getWidth
    val canvasHeight = gc.getCanvas.getHeight

    gc.setFill(Color.LIGHTGRAY)
    gc.setStroke(Color.BLACK)
    gc.setLineWidth(5)

    gc.fill()
    gc.strokeRect(
      0,              //x of the upper left corner
      0,              //y of the upper left corner
      canvasWidth,    //width of the rectangle
      canvasHeight)  //height of the rectangle

    gc.setFill(Color.RED)
    gc.setStroke(Color.BLUE)
    gc.setLineWidth(1)

  }

  def getCanvas():ScrollPane = {

    var currentLayer = new Polyline()
    var selecting = false
    var  dragBox = new Rectangle(0, 0, 0, 0);
    var camadas = List(currentLayer)

   /////

    val page3 = new Pane(dragBox)
    page3.setPrefSize(400,400)

    page3.setOnMousePressed(event => {

      if(!selecting){

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

        currentLayer.setStrokeWidth(5)
        currentLayer.getPoints.add(event.getX)
        currentLayer.getPoints.add(event.getY)

      }else{
        dragBox.setVisible(true);
        dragBox.setTranslateX(event.getX)
        dragBox.setTranslateY(event.getY)
      }


    })

    page3.setOnMouseDragged(event => {
        if(!selecting){

          //graphicsContext.lineTo(event.getX, event.getY)
          //graphicsContext.stroke()
          currentLayer.getPoints.add(event.getX)
          currentLayer.getPoints.add(event.getY)
        }else{
          dragBox.setWidth(event.getX - dragBox.getTranslateX)
          dragBox.setHeight(event.getY - dragBox.getTranslateY)
        }
    })

    page3.setOnMouseReleased(event => {
        if(selecting){
          camadas = currentLayer::camadas

          dragBox.setVisible(false)
          println("vamos ver quais foram os selecionados!")


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


