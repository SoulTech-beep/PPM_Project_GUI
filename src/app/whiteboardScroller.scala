package app

import javafx.geometry.{Insets, Point2D, Pos}
import javafx.scene.Group
import javafx.scene.control.{ContextMenu, MenuItem}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Line, Polyline, Rectangle}

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

    var currentRectangle:Rectangle = new Rectangle()

    var isGeometricShape = true
    var isLine = false
    var isFirstPoint = true
    var firstPoint:Point2D = null
    var currentLine:Line = null
    var polygon : Group = null
    var lineToAdd:Line = null

    page.setOnMouseClicked(event => {
      if(toolBar.selectedTool == ToolType.geometricShape){
        if(isGeometricShape){
          if(isFirstPoint){
            polygon = new Group()
            currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
            currentLine.setStroke(Color.BLACK)
            polygon.getChildren.add(currentLine)

            page.getChildren.add(polygon)
            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false
          }else{
            if(firstPoint.distance(event.getX, event.getY) != 0){
              currentLine.setEndX(event.getX)
              currentLine.setEndY(event.getY)

              currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
              currentLine.setStroke(Color.BLACK)
              polygon.getChildren.add(currentLine)
            }else{
              isFirstPoint = true
            }
          }
        }
      }

    })

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

      if(toolBar.selectedTool == ToolType.geometricShape){
        if(isGeometricShape){
          if(!isFirstPoint){
            currentLine.setEndX(event.getX)
            currentLine.setEndY(event.getY)
          }
        }
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

    /*verticalLines(width, height, page)
    horizontalLine(width, height, page)*/

    dottedPage(width, height, page)


    page
  }

  def dottedPage(width:Double, height:Double, pane:Pane):Unit = {
    val j = (30 to height.toInt-30) by 30
    val i = (30 to width.toInt - 30) by 30

    j.foreach(h => {
      i.foreach(w => {
        val circle = new Circle()

        circle.setCenterX(w)
        circle.setCenterY(h)

        circle.setRadius(1.5)
        circle.setFill(Color.LIGHTGRAY)
        circle.setStroke(Color.LIGHTGRAY)

        pane.getChildren.add(0, circle)
      })
    })

  }

  def horizontalLine( width : Double, height: Double, pane:Pane):Unit = {
    val j = (30 to height.toInt-30) by 30

    j.foreach(h => {
      val line = new Line()

      line.setStartX(0)
      line.setEndX(width)

      line.setStartY(h)
      line.setEndY(h)

      line.setStrokeWidth(2)
      line.setFill(Color.LIGHTGRAY)
      line.setStroke(Color.LIGHTGRAY)

      pane.getChildren.add(0, line)
    })
  }

  def verticalLines( width : Double, height: Double, pane:Pane):Unit = {
    val i = (30 to width.toInt - 30) by 30

    i.foreach(w => {
      val line = new Line()

      line.setStartX(w)
      line.setEndX(w)

      line.setStartY(0)
      line.setEndY(height)

      line.setStrokeWidth(2)
      line.setFill(Color.LIGHTGRAY)
      line.setStroke(Color.LIGHTGRAY)

      pane.getChildren.add(0, line)
    })
  }

  def getCanvas(toolBar:customToolBar):ZoomableScrollPane = {

    var selecting = false

    /////
    val canvas = new ZoomableScrollPane()
    val pages = new VBox()

    val page3 = whiteboardScroller.createPage(Color.WHITE, 1200, 1200, toolBar)

    //page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)))

    pages.setMaxWidth(1200)

    pages.getChildren.addAll(page3)

    pages.setSpacing(80)
    pages.setAlignment(Pos.CENTER)

    canvas.init(pages)

    canvas
  }


}


