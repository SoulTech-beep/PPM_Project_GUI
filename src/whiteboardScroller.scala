import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.{Bounds, Insets, Pos}
import javafx.scene.control.{Button, ContextMenu, MenuItem}
import javafx.scene.input.MouseButton
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Polyline, Shape}

import scala.util.control.Breaks

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

    val eraserCircle = new Circle()

    eraserCircle.setStrokeWidth(1)
    eraserCircle.setStroke(Color.DARKGRAY)
    eraserCircle.setOpacity(0)
    eraserCircle.setFill(Color.TRANSPARENT)
    page.getChildren.add(eraserCircle)

    var camadas:List[Polyline] = List()
    var currentLayer = new Polyline()

    page.setOnMousePressed(event => {


      if (toolBar.selectedTool == ToolType.pen || toolBar.selectedTool == ToolType.marker) {

        eraserCircle.setOpacity(0)

        currentLayer = new Polyline()
        currentLayer.setSmooth(true)
        currentLayer.setStrokeMiterLimit(1)

        val tempCurrentLayer = currentLayer
        camadas = tempCurrentLayer :: camadas

        currentLayer.setOnContextMenuRequested(click => {

          val delete = new MenuItem("Delete")
          val contextMenu = new ContextMenu(delete)

          delete.setOnAction(_ => {
            camadas = camadas.filter(p => p != currentLayer)
            page.getChildren.remove(tempCurrentLayer)
          })

          contextMenu.show(currentLayer, click.getScreenX, click.getScreenY)
        })



        page.getChildren.add(currentLayer)

        currentLayer.setStrokeWidth(toolBar.selectedPen.width.get())
        currentLayer.setOpacity(toolBar.selectedPen.opacity.get())
        currentLayer.setSmooth(true)
        currentLayer.setStroke(Color.RED)
        currentLayer.getPoints.add(event.getX)
        currentLayer.getPoints.add(event.getY)

      } else if (toolBar.selectedTool == ToolType.eraser) {
        println("erasing")

        var porApagar: List[Polyline] = List()


        /*camadas.foreach(c => {
          val intersect = Shape.intersect(eraserCircle, c)
          if(intersect.getBoundsInLocal.getWidth != -1){
            porApagar = c::porApagar
            page.getChildren.remove(c)
          }
        })

        camadas = camadas.filter(e => !porApagar.contains(e))*/

        camadas.foreach(c => {

          val range = (0 until c.getPoints.size).toList //it's going to c.getPoints.size-1

          val eraserRadius = toolBar.eraserFinal.radius.get()
          val points = c.getPoints

          val loop = new Breaks

          loop.breakable{
           range.foreach(p => if(p%2 == 0){
              if (points.get(p) > event.getX - eraserRadius && points.get(p) < event.getX + eraserRadius && points.get(p + 1) > event.getY - eraserRadius && points.get(p + 1) < event.getY + eraserRadius) {
                porApagar = c :: porApagar
                page.getChildren.remove(c)
                loop.break()
                println("HEHE NO BREAK")
              }
            })
          }

        })

        camadas = camadas.filter(e => !porApagar.contains(e))
      }
    })


    page.setOnMouseEntered(_ => {
      if(toolBar.selectedTool.equals(ToolType.eraser)){
        eraserCircle.setRadius(toolBar.eraserFinal.radius.get())
      }
    })

    page.setOnMouseMoved(event => {
      if(toolBar.selectedTool.equals(ToolType.eraser)){
        eraserCircle.setOpacity(1)
        eraserCircle.setCenterX(event.getX)
        eraserCircle.setCenterY(event.getY)
      }else{
        eraserCircle.setOpacity(0)
      }
    })

    page.setOnMouseDragged(event => {

      if(toolBar.selectedTool == ToolType.pen || toolBar.selectedTool == ToolType.marker){

        if(event.getX < page.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < page.getHeight) {
          currentLayer.getPoints.add(event.getX)
          currentLayer.getPoints.add(event.getY)
        }
      }else if(toolBar.selectedTool == ToolType.eraser){

        eraserCircle.setOpacity(1)
        eraserCircle.setCenterX(event.getX)
        eraserCircle.setCenterY(event.getY)

        var porApagar: List[Polyline] = List()

        camadas.foreach(c =>  {
          var i = 0

          val eraserRadius = toolBar.eraserFinal.radius.get()
          val points = c.getPoints

          while (i < points.size()-1) {

            if (points.get(i) > event.getX - eraserRadius && points.get(i) < event.getX + eraserRadius && points.get(i + 1) > event.getY - eraserRadius && points.get(i + 1) < event.getY + eraserRadius) {
              porApagar = c :: porApagar
              page.getChildren.remove(c)
              i = points.size()
            } else {
              i = i + 2
            }
          }
        })

        camadas = camadas.filter(e => !porApagar.contains(e))

      }

      })



    page
  }

  def getCanvas(toolBar:customToolBar):ZoomableScrollPane = {

    var selecting = false

    /////

    val canvas = new ZoomableScrollPane()
    val pages = new VBox()

    val page1 = new Pane()
    val page2 = new Pane()
    val page3 = whiteboardScroller.createPage(Color.GREENYELLOW, 600, 600, toolBar)

    page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)))
    page2.setBackground(new Background(new BackgroundFill(Color.web("#2d3436"), CornerRadii.EMPTY, Insets.EMPTY)))

    page1.setPrefSize(1200, 800)
    page2.setPrefSize(1200, 800)

    pages.setMaxWidth(1200)

    pages.getChildren.addAll(page3,page1,page2)

    pages.setSpacing(80)
    pages.setAlignment(Pos.CENTER)

    canvas.init(pages)

    canvas
  }


}


