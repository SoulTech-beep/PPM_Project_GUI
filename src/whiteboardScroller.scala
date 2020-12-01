import javafx.geometry.{Bounds, Insets, Pos}
import javafx.scene.control.ScrollPane
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{Background, BackgroundFill, BorderPane, CornerRadii, HBox, Pane, Priority, StackPane, VBox}
import javafx.scene.paint.Color
import javafx.beans.binding.Bindings
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.scene.Node
import javafx.scene.canvas.{Canvas, GraphicsContext}
import javafx.scene.input.MouseButton
import javafx.scene.transform.Scale

class whiteboardScroller {


}

object whiteboardScroller{

  def  initDraw( gc:GraphicsContext):Unit = {
    val canvasWidth = gc.getCanvas.getWidth;
    val canvasHeight = gc.getCanvas.getHeight;

    gc.setFill(Color.LIGHTGRAY);
    gc.setStroke(Color.BLACK);
    gc.setLineWidth(5);

    gc.fill();
    gc.strokeRect(
      0,              //x of the upper left corner
      0,              //y of the upper left corner
      canvasWidth,    //width of the rectangle
      canvasHeight);  //height of the rectangle

    gc.setFill(Color.RED);
    gc.setStroke(Color.BLUE);
    gc.setLineWidth(1);

  }

  def getCanvas():ScrollPane = {
    val canvas = new ScrollPane()

    val pages = new VBox()

    val page3 = new Canvas(400,400)
    val graphicsContext = page3.getGraphicsContext2D
    initDraw(graphicsContext)

    page3.setOnMousePressed(event => {

      graphicsContext.beginPath()
      graphicsContext.moveTo(event.getX,event.getY)
      graphicsContext.stroke()

    })

    page3.setOnMouseDragged(event => {

      graphicsContext.lineTo(event.getX, event.getY)
      graphicsContext.stroke()
    })

    page3.setOnMouseReleased(event => {

    })

    val page1 = new Pane()
    val page2 = new Pane()

    val pag = List(page3,page1, page2)

    page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    page2.setBackground(new Background(new BackgroundFill(Color.web("#2d3436"), CornerRadii.EMPTY, Insets.EMPTY)));

    page1.setPrefSize(1200, 800)
    page2.setPrefSize(1200, 800)

    pages.setMaxWidth(1200)

    pages.getChildren.addAll(page3,page1,page2)
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


