package app

import javafx.animation.{KeyFrame, KeyValue, Timeline}
import javafx.geometry.{Insets, Point2D, Pos}
import javafx.scene.Node
import javafx.scene.control.{ContextMenu, MenuItem}
import javafx.scene.image.Image
import javafx.scene.layout._
import javafx.scene.paint.{Color, ImagePattern}
import javafx.scene.shape._
import javafx.util.Duration

import scala.util.control.Breaks

class whiteboardScroller {

  val pages: List[Pane] = List()
  val toolbar: customToolBar = new customToolBar()
}

object whiteboardScroller {

  //TODO WHEN SELECTED SHOULD WE BE ABLE TO ERASE EVERYTHING SELECTED?

  def makeDraggable(node: Node): Unit = {
    var dragX: Double = 0
    var dragY: Double = 0

    node.setOnMousePressed(me => {
      dragX = me.getX
      dragY = me.getY
    })

    node.setOnMouseDragged(me => {
      node.setLayoutX(node.getLayoutX + me.getX - dragX)
      node.setLayoutY(node.getLayoutY + me.getY - dragY)

    })
  }

  def createPage(backgroundColor: Color, width: Double, height: Double, toolBar: customToolBar): Pane = {
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

    var camadas: List[Polyline] = List()
    var camadas_node : List[Node] = List()

    var selectedShapes : List[Node] = List()
    var selectedPolyline :List[Polyline] = List()

    var currentLayer = new Polyline()

    var isFirstPoint = true
    var firstPoint: Point2D = null
    var currentLine: Line = null
    var polygon: Polyline = null

    var currentCircle: Circle = null

    var currentRectangle: Rectangle = null

    var selectionPolyline = new Polyline()

    var dragX: Double = 0
    var dragY: Double = 0

    page.setOnMouseClicked(event => {

      if(toolBar.selectedTool == ToolType.image) {
        if(toolBar.imagePath != "") {
          val image:Image = new Image(toolBar.imagePath)
          val iP:ImagePattern = new ImagePattern(image)
          val square = new Rectangle(image.getWidth,image.getHeight,iP)
          page.getChildren.add(square)

          camadas_node = square :: camadas_node
        }
      }


      if (toolBar.selectedTool == ToolType.geometricShape) {

        if (toolBar.shapePen.shape == ShapeType.square) {
          if (isFirstPoint) {
            currentRectangle = new Rectangle()
            currentRectangle.setX(event.getX)
            currentRectangle.setY(event.getY)
            currentRectangle.setWidth(0)
            currentRectangle.setHeight(0)

            currentRectangle.setStroke(toolBar.shapePen.color.get())
            currentRectangle.setStrokeWidth(toolBar.shapePen.width.get)
            currentRectangle.setFill(toolBar.shapePen.color.get())
            currentRectangle.setOpacity(toolBar.shapePen.opacity.get)

            page.getChildren.add(currentRectangle)
            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false

            camadas_node = currentRectangle::camadas_node
          } else {
            isFirstPoint = true
          }
        }

        if (toolBar.shapePen.shape == ShapeType.circle) {
          if (isFirstPoint) {
            currentCircle = new Circle()
            currentCircle.setCenterX(event.getX)
            currentCircle.setCenterY(event.getY)
            currentCircle.setStroke(toolBar.shapePen.color.get())
            currentCircle.setStrokeWidth(toolBar.shapePen.width.get)
            currentCircle.setFill(toolBar.shapePen.color.get)
            currentCircle.setOpacity(toolBar.shapePen.opacity.get)

            page.getChildren.add(currentCircle)
            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false

            camadas_node = currentCircle::camadas_node
          } else {
            isFirstPoint = true
          }
        }

        if (toolBar.shapePen.shape == ShapeType.polygon) {
          if (isFirstPoint) {
            polygon = new Polyline()
            currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
            currentLine.setStroke(toolBar.shapePen.color.get)
            currentLine.setStrokeWidth(toolBar.shapePen.width.get)
            currentLine.setOpacity(toolBar.shapePen.opacity.get)

            polygon.getPoints.addAll(event.getX, event.getY)

            page.getChildren.add(polygon)
            page.getChildren.add(currentLine)

            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false

            camadas = polygon::camadas
          } else {
            if (firstPoint.distance(event.getX, event.getY) > 20) {
              currentLine.setEndX(event.getX)
              currentLine.setEndY(event.getY)

              page.getChildren.remove(currentLine)

              currentLine = new Line(event.getX, event.getY, event.getX, event.getY)

              page.getChildren.add(currentLine)

              currentLine.setStroke(toolBar.shapePen.color.get)
              currentLine.setStrokeWidth(toolBar.shapePen.width.get)
              currentLine.setOpacity(toolBar.shapePen.opacity.get)

              polygon.getPoints.addAll(event.getX, event.getY)
            } else {
              polygon.getPoints.addAll(polygon.getPoints.get(0), polygon.getPoints.get(1))
              page.getChildren.remove(currentLine)

              isFirstPoint = true
            }
          }
        }

        if (toolBar.shapePen.shape == ShapeType.line) {
          if (isFirstPoint) {
            currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
            currentLine.setStroke(Color.BLACK)

            page.getChildren.add(currentLine)
            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false

            camadas_node = currentLine::camadas_node
          } else {
            isFirstPoint = true
          }
        }

      }
    })

    page.setOnMouseMoved(event => {
      if (toolBar.selectedTool.equals(ToolType.eraser)) {
        eraserCircle.setOpacity(1)
        eraserCircle.setCenterX(event.getX)
        eraserCircle.setCenterY(event.getY)
      } else {
        eraserCircle.setOpacity(0)
      }

      //TODO Change this to on mouse moved
      if (toolBar.selectedTool == ToolType.geometricShape) {

        if (toolBar.shapePen.shape == ShapeType.square) {
          if (!isFirstPoint) {

            val deltaX = event.getX - firstPoint.getX
            val deltaY = event.getY - firstPoint.getY
            if (deltaX < 0) {
              currentRectangle.setX(event.getX)
              currentRectangle.setWidth(-deltaX)
            }
            else {
              currentRectangle.setX(firstPoint.getX)
              currentRectangle.setWidth(event.getX - firstPoint.getX)
            }
            if (deltaY < 0) {
              currentRectangle.setY(event.getY)
              currentRectangle.setHeight(-deltaY)
            }
            else {
              currentRectangle.setY(firstPoint.getY)
              currentRectangle.setHeight(event.getY - firstPoint.getY)
            }


          }
        }

        if (toolBar.shapePen.shape == ShapeType.circle) {
          if (!isFirstPoint) {
            val currentPoint = new Point2D(event.getX, event.getY)
            val radius = currentPoint.distance(new Point2D(currentCircle.getCenterX, currentCircle.getCenterY))
            currentCircle.setRadius(radius)
          }
        }

        if (toolBar.shapePen.shape == ShapeType.polygon) {
          if (!isFirstPoint) {
            if (firstPoint.distance(new Point2D(event.getX, event.getY)) < 20) {
              currentLine.setEndX(firstPoint.getX)
              currentLine.setEndY(firstPoint.getY)

            } else {
              currentLine.setEndX(event.getX)
              currentLine.setEndY(event.getY)
            }
          }
        }

        if (toolBar.shapePen.shape == ShapeType.line) {
          if (!isFirstPoint) {
            if (firstPoint.distance(new Point2D(event.getX, event.getY)) < 20) {
              currentLine.setEndX(firstPoint.getX)
              currentLine.setEndY(firstPoint.getY)
            } else {
              currentLine.setEndX(event.getX)
              currentLine.setEndY(event.getY)
            }
          }
        }

      }

    })

    page.setOnMouseReleased(_ => {

      if(toolBar.selectedTool == ToolType.move) {

        selectedShapes.foreach(teste => {


          teste.setLayoutX(teste.getLayoutX + teste.getTranslateX)
          teste.setLayoutY(teste.getLayoutY + teste.getTranslateY)

          teste.setTranslateX(0)
          teste.setTranslateY(0)

          camadas_node = camadas_node.updated(camadas_node.indexOf(teste), teste)
        })

        selectedPolyline.foreach(teste => {
          teste.setLayoutX(teste.getLayoutX + teste.getTranslateX)
          teste.setLayoutY(teste.getLayoutY + teste.getTranslateY)

          teste.setTranslateX(0)
          teste.setTranslateY(0)


        })


      }

      if (toolBar.selectedTool == ToolType.selector) {
        selectionPolyline.getPoints.add(selectionPolyline.getPoints.get(0))
        selectionPolyline.getPoints.add(selectionPolyline.getPoints.get(1))

        val keyValue1 = new KeyValue(
          selectionPolyline.strokeDashOffsetProperty,
          double2Double(0.0)
        )

        val keyValue2 = new KeyValue(
          selectionPolyline.strokeDashOffsetProperty,
          double2Double(40.0)
        )

        val timeline = new Timeline(

          new KeyFrame(Duration.ZERO,
            keyValue1),
          new KeyFrame(Duration.seconds(2), keyValue2)
        )

        timeline.setCycleCount(Int.MaxValue)

        timeline.play()

        selectedShapes = List()
        selectedPolyline = List()

        camadas.foreach(c => {
          val shape = selectionPolyline.intersects(c.getBoundsInParent)
          if (shape) {

            selectedPolyline = c :: selectedPolyline

            c.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
          } else {
            c.setStyle("")
          }
        })

        camadas_node.foreach(c => {


          val shape = selectionPolyline.intersects(c.asInstanceOf[Shape].getBoundsInParent)
          if (shape) {

            selectedShapes = c::selectedShapes

            c.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")

          } else {
            c.setStyle("")
          }
        })

        selectedPolyline = selectionPolyline :: selectedPolyline

      }

    })

    page.setOnMousePressed(event => {

      if(toolBar.selectedTool == ToolType.move) {
        dragX = event.getX
        dragY = event.getY

        val newsel: Polyline = camadas.find(c => c.intersects(dragX,dragY,1,1) ).getOrElse(null)

        if(newsel == null)
          selectedPolyline = List()
        else if(!selectedPolyline.contains(newsel))
          selectedPolyline = List(newsel)


      } else {

        if (selectionPolyline != null) {
          page.getChildren.remove(selectionPolyline)
        }

        if (toolBar.selectedTool == ToolType.selector) {

          eraserCircle.setOpacity(0)

          selectionPolyline = new Polyline()
          selectionPolyline.setSmooth(true)
          selectionPolyline.setStrokeMiterLimit(1)
          selectionPolyline.setStrokeWidth(2)
          selectionPolyline.getStrokeDashArray.addAll(20)
          selectionPolyline.setFill(Color.TRANSPARENT)

          page.getChildren.add(selectionPolyline)

          selectionPolyline.getPoints.add(event.getX)
          selectionPolyline.getPoints.add(event.getY)

        }

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
          currentLayer.setStroke(toolBar.selectedPen.color.get())
          currentLayer.getPoints.add(event.getX)
          currentLayer.getPoints.add(event.getY)

        } else if (toolBar.selectedTool == ToolType.eraser) {
          println("erasing")

          var porApagar: List[Polyline] = List()

          camadas.foreach(c => {

            val range = (0 until c.getPoints.size).toList //it's going to c.getPoints.size-1

            val eraserRadius = toolBar.eraserFinal.radius.get()
            val points = c.getPoints

            val loop = new Breaks

            loop.breakable {
              range.foreach(p => if (p % 2 == 0) {
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
      }})

    page.setOnMouseEntered(_ => {
      if (toolBar.selectedTool.equals(ToolType.eraser)) {
        eraserCircle.setRadius(toolBar.eraserFinal.radius.get())
      }
    })
    page.setOnMouseDragged(event => {

      if(toolBar.selectedTool == ToolType.move) {

        selectedShapes.foreach(teste => {
          teste.setTranslateX(teste.getTranslateX + event.getX - dragX)
          teste.setTranslateY(teste.getTranslateY + event.getY - dragY)
        })

        selectedPolyline.foreach(teste => {
          teste.setTranslateX(teste.getTranslateX + event.getX - dragX)
          teste.setTranslateY(teste.getTranslateY + event.getY - dragY)
        })

        dragX = event.getX
        dragY = event.getY
      }

      if (toolBar.selectedTool == ToolType.selector) {

        if (event.getX < page.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < page.getHeight) {
          selectionPolyline.getPoints.add(event.getX)
          selectionPolyline.getPoints.add(event.getY)
        }

      }

      if (toolBar.selectedTool == ToolType.pen || toolBar.selectedTool == ToolType.marker) {

        if (event.getX < page.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < page.getHeight) {
          currentLayer.getPoints.add(event.getX)
          currentLayer.getPoints.add(event.getY)
        }
      } else if (toolBar.selectedTool == ToolType.eraser) {

        eraserCircle.setOpacity(1)
        eraserCircle.setCenterX(event.getX)
        eraserCircle.setCenterY(event.getY)

        var porApagar: List[Polyline] = List()

        camadas.foreach(c => {
          var i = 0

          val eraserRadius = toolBar.eraserFinal.radius.get()
          val points = c.getPoints

          while (i < points.size() - 1) {

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
    dottedPage(width, height, page)

    page
  }

  def dottedPage(width: Double, height: Double, pane: Pane): Unit = {
    val j = (30 to height.toInt - 30) by 30
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

  def horizontalLine(width: Double, height: Double, pane: Pane): Unit = {
    val j = (30 to height.toInt - 30) by 30

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

  def verticalLines(width: Double, height: Double, pane: Pane): Unit = {
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

  def getCanvas(toolBar: customToolBar): ZoomableScrollPane = {

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


