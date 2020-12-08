package app


import java.io.File

import app.PageStyle.PageStyle
import javafx.animation.{KeyFrame, KeyValue, Timeline}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.geometry.{Bounds, Insets, Point2D, Pos}
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.layout._
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.{Color, ImagePattern}
import javafx.scene.shape._
import javafx.scene.text.Text
import javafx.scene.{Node, Scene}
import javafx.stage.{Modality, Stage}
import javafx.util.Duration
import logicMC.Auxiliary
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.{ImageType, PDFRenderer}
import org.apache.pdfbox.tools.imageio.ImageIOUtil

import scala.reflect.io.Path.jfile2path
import scala.util.control.Breaks

class whiteboardScroller {

  val pages: List[Pane] = List()
  val toolbar: customToolBar = new customToolBar()

}

object whiteboardScroller {

  var camadas: List[Polyline] = List()
  var camadas_node : List[Node] = List()
  var camadas_SPMediaView :List[StackPane] = List()

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

  def testeTexto():(TextArea, Text) = {
    val textArea = new TextArea()
    textArea.setPrefSize(200, 40)
    textArea.setWrapText(true)

    val textHolder = new Text()
    var oldHeight = 0.0

    textArea.setFont(Auxiliary.myFont)

    textHolder.setWrappingWidth(200-20)

    textHolder.textProperty().bind({
      textArea.textProperty()
    })

    textHolder.layoutBoundsProperty().addListener(new ChangeListener[Bounds] {
      override def changed(observableValue: ObservableValue[_ <: Bounds], oldValue: Bounds, newValue: Bounds): Unit = {
        if(oldHeight != newValue.getHeight ){
          println(newValue.getHeight)
          oldHeight = newValue.getHeight
          textArea.setPrefHeight(textHolder.getLayoutBounds.getHeight*1.07 + 20)
        }
      }
    })

    (textArea, textHolder)
  }

  def createPage(backgroundColor: Color, width: Double, height: Double, toolBar: customToolBar, pageStyle: PageStyle): Pane = {
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


    var selectedShapes : List[Node] = List()
    var selectedPolyline :List[Polyline] = List()
    var selectedSPMediaView : List[StackPane] = List()

    var currentLayer = new Polyline()

    var pdfNum : Int = 0

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

      if(toolBar.selectedTool == ToolType.pdf) {
        val s:String = toolBar.imagePath.replaceAll("%20"," ")
        val num = pdfNum
        generateImageFromPDF(s, "png", num)
        val listFiles: List[File] = getListOfFiles("src/output/" + s.split('/').last.replace(".pdf",num.toString))
        getPdfView(page, s.split('/').last.replace(".pdf",num.toString),listFiles,0,200)
        pdfNum = pdfNum + 1
        toolBar.selectedTool = ToolType.move
      }

      if(toolBar.selectedTool == ToolType.image) {
        if(toolBar.imagePath != "") {

          val image:Image = new Image(toolBar.imagePath)
          val iP:ImagePattern = new ImagePattern(image)
          val square = new Rectangle(image.getWidth,image.getHeight,iP)

          square.setOnContextMenuRequested(click => {

            val delete = new MenuItem("Delete")

            val resize : CustomMenuItem = new CustomMenuItem()

            val width = new TextField(square.getWidth.toString)
            val height = new TextField(square.getHeight.toString)
            val set = new Button("Change size")
            val vBox = new VBox(width, height, set)
            vBox.setSpacing(10)
            vBox.setAlignment(Pos.CENTER)

            resize.setContent(vBox)
            set.setOnAction(_ => {
              square.setWidth(width.getText.toDouble)
              square.setHeight(height.getText.toDouble)
            })


            val contextMenu = new ContextMenu(resize, delete)

            delete.setOnAction(_ => {
              camadas_node = camadas_node.filter(p => p != currentLayer)
              page.getChildren.remove(square)
            })

            contextMenu.show(square, click.getScreenX, click.getScreenY)
          })

          page.getChildren.add(square)

          camadas_node = square :: camadas_node
        }
      }
      //commit

      if (toolBar.selectedTool == ToolType.text) {
        val ttt = testeTexto()
        ttt._2.setOpacity(0)
        page.getChildren.addAll(ttt._1, ttt._2)

        /*text.setPrefHeight(40)
        text.setMinWidth(40)


        text.textProperty().addListener(new ChangeListener[String] {
          override def changed(observableValue: ObservableValue[_ <: String], t: String, t1: String): Unit = {
            val len = t1.length
            text.setPrefWidth(len*10)

            if(t1.isEmpty || t.isEmpty){
              text.setPrefWidth(40)
            }

            val altura = t1.count(p => if(p=='\n') true else false)
            text.setPrefHeight(altura*10)

            if(t1.isEmpty || t.isEmpty){
              text.setPrefHeight(40)
            }

          }
        })*/


      }


      if (toolBar.selectedTool == ToolType.video) {
        if (toolBar.videoPath != "") {
          val video: Media = new Media(toolBar.videoPath)
          val player: MediaPlayer = new MediaPlayer(video)
          val mediaView: MediaView = new MediaView(player)

          val play: Button = new Button()
          play.setText("play")
          play.setOnAction(e => {
            player.play()
          })

          val pause: Button = new Button()
          pause.setText("pause")
          pause.setOnAction(e => {
            player.pause()
          })


          val fast: Button = new Button()
          fast.setText("fast")
          fast.setOnAction(e => {
            player.setRate(1.5)
          })

          val slow: Button = new Button()
          slow.setText("slow")
          slow.setOnAction(e => {
            player.setRate(0.5)
          })

          val restart: Button = new Button()
          restart.setText("restart")
          restart.setOnAction(e => {
            player.seek(player.getStartTime)
            player.play
          })


          val videoToolBar: HBox = new HBox()
          videoToolBar.getChildren.addAll(play, pause, fast, slow, restart)

          val sp: StackPane = new StackPane()
          sp.getChildren.addAll(mediaView, videoToolBar)

          page.getChildren.add(sp)

          sp.setOnMouseEntered(e => {
            videoToolBar.setVisible(true)
          })
          sp.setOnMouseExited(e => {
            videoToolBar.setVisible(false)
          })
          videoToolBar.setVisible(false)

          videoToolBar.setAlignment(Pos.BOTTOM_CENTER)
          videoToolBar.setSpacing(20)

          HBox.setMargin(videoToolBar, new Insets(0, 0, 20, 0))
          videoToolBar.setPadding(new Insets(0, 0, 20, 0))

          camadas_SPMediaView = sp :: camadas_SPMediaView
          toolBar.videoPath = ""
          toolBar.selectedTool = ToolType.move
        }
      }

      if (toolBar.selectedTool == ToolType.geometricShape) {
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

            camadas = polygon :: camadas
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

      if (toolBar.selectedTool == ToolType.geometricShape) {
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
      }



    })

    page.setOnMouseReleased(event => {

      if (toolBar.selectedTool == ToolType.geometricShape && toolBar.shapePen.shape != ShapeType.polygon)
        isFirstPoint=true

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


          val shape = selectionPolyline.intersects(c.asInstanceOf[Node].getBoundsInParent)
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

        val newsel: Polyline = camadas.find(c => c.intersects(dragX-c.getLayoutX,dragY-c.getLayoutY,1,1) ).getOrElse(null)

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

              camadas_node = currentRectangle :: camadas_node
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

          currentLayer.setOnMouseClicked(_ => println("clickado"))

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
                if (points.get(p) + c.getLayoutX > event.getX - eraserRadius && points.get(p) +c.getLayoutX < event.getX + eraserRadius && points.get(p + 1) +c.getLayoutY > event.getY - eraserRadius && points.get(p + 1) +c.getLayoutY< event.getY + eraserRadius) {

                  println("a apagar: " + c)

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

        //TODO Selecting multiple objects have some problem :D
        selectedShapes.foreach(teste => {

          if(teste.getBoundsInParent.getMinX + event.getX - dragX >= 0 && teste.getBoundsInParent.getMaxX + event.getX - dragX <= page.getWidth){
            teste.setTranslateX(teste.getTranslateX + event.getX - dragX)
          }

          if(teste.getBoundsInParent.getMinY + event.getY - dragY >= 0 && teste.getBoundsInParent.getMaxY + event.getY - dragY <= page.getHeight) {
            teste.setTranslateY(teste.getTranslateY + event.getY - dragY)
          }


        })

        selectedPolyline.foreach(teste => {

          if(teste.getBoundsInParent.getMinX + event.getX - dragX >= 0 && teste.getBoundsInParent.getMaxX + event.getX - dragX <= page.getWidth){
            teste.setTranslateX(teste.getTranslateX + event.getX - dragX)
          }

          if(teste.getBoundsInParent.getMinY + event.getY - dragY >= 0 && teste.getBoundsInParent.getMaxY + event.getY - dragY <= page.getHeight) {
            teste.setTranslateY(teste.getTranslateY + event.getY - dragY)

          }

        })

        dragX = event.getX
        dragY = event.getY
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

            if (points.get(i) + c.getLayoutX > event.getX - eraserRadius && points.get(i) + c.getLayoutX < event.getX + eraserRadius && points.get(i + 1) + c.getLayoutY > event.getY - eraserRadius && points.get(i + 1) + c.getLayoutY < event.getY + eraserRadius) {
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


    def applyPageStyle(newPageStyle:PageStyle):Unit = newPageStyle match {
      case PageStyle.DOTTED => Auxiliary.dottedPage(width, height, page, 30)
      case PageStyle.LINED => Auxiliary.horizontalLine(width, height, page, 30)
      case PageStyle.SQUARED => Auxiliary.squaredPage(width, height, page, 30)
      case _ =>
    }

    applyPageStyle(pageStyle)

    page
  }

  def dottedPage(width: Double, height: Double, pane: Pane): Unit = {
    Auxiliary.dottedPage(width, height, pane, 30)
  }

  def horizontalLine(width: Double, height: Double, pane: Pane): Unit = {
    Auxiliary.horizontalLine(width, height, pane, 30)
  }

  def verticalLines(width: Double, height: Double, pane: Pane): Unit = {
    Auxiliary.verticalLines(width, height, pane, 30)
  }

  def addPageButton(pages: VBox, toolBar:customToolBar, pane:Node):Button = {

    val addPageButton = new Button("Add new page")
    addPageButton.setFont(Auxiliary.getFont(14))
    VBox.setMargin(addPageButton, new Insets(0,0,50,0))
    addPageButton.setPrefSize(130, 50)

    addPageButton.setOnAction(_ => {

      val pagePicker = new PagePicker()

      val scene= new Scene(pagePicker.initialize())
      scene.getStylesheets.add("testStyle.css")

      Auxiliary.blurBackground(0, 30, 1000, pane)

      val stage:Stage = new Stage()
      stage.setScene(scene)
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.show()
      stage.setTitle("Add Page")
      stage.getIcons.add(new Image("images/addIcon.png"))
      stage.setResizable(false)


      stage.setOnCloseRequest(_ => {
        Auxiliary.blurBackground(30, 0, 500, pane)

        val values = pagePicker.getPage()

        pages.getChildren.add(pages.getChildren.size()-1, whiteboardScroller.createPage(values._1, values._2._1*5, values._2._2*5, toolBar, values._3))
      })

    })

    addPageButton
  }

  def getCanvas(toolBar: customToolBar, pane: Node): ZoomableScrollPane = {

    /////
    val canvas = new ZoomableScrollPane()
    val pages = new VBox()

    val page3 = whiteboardScroller.createPage(Color.WHITE, 1200, 1200, toolBar, PageStyle.DOTTED)

    //page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)))

    pages.getChildren.addAll(page3, addPageButton(pages, toolBar, pane))

    pages.setSpacing(50)
    pages.setAlignment(Pos.CENTER)

    canvas.init(pages)

    canvas
  }

  //TODO tirar for
  def generateImageFromPDF(filename: String, extension: String, ranNum: Int): Unit = {

    val document = PDDocument.load(new File(filename.replace("file:/","")))
    val pdfRenderer = new PDFRenderer(document)
    val file:File = new File("src/output/" + filename.split('/').last.replace(".pdf",ranNum.toString))
    file.mkdir()
    for (page <- 0 until document.getNumberOfPages) {
      val bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB)
      //TODO create output directory
      ImageIOUtil.writeImage(bim, String.format("src/output/" + file.getName + "/pdf-" + ( page + 1) + "." + extension), 300)
    }
    document.close()
  }

  def getPdfView(page: Pane,filename: String,fl: List[File], num:Int,h:Double):Unit = {
    val sp:StackPane = new StackPane()
    val path:String = fl(num).toURI().toString().replaceAll("%20"," ")
    val image: Image = new Image(path)
    val iP: ImagePattern = new ImagePattern(image)
    val square = new Rectangle(image.getWidth, image.getHeight, iP)
    square.setHeight(h)
    square.setWidth(h*(image.getWidth/image.getHeight))
    square.setStroke(Color.BLACK)
    sp.getChildren.addAll(square)
    square.setStrokeWidth(square.getWidth/100)

    sp.setOnContextMenuRequested(click => {

      val delete = new MenuItem("Delete")

      val resize : CustomMenuItem = new CustomMenuItem()

      val size = new TextField(square.getHeight.toString)
      val set = new Button("Change size")
      val vBox = new VBox(size, set)
      vBox.setSpacing(10)
      vBox.setAlignment(Pos.CENTER)

      resize.setContent(vBox)
      set.setOnAction(_ => {
        square.setWidth(size.getText.toDouble*(image.getWidth/image.getHeight))
        square.setHeight(size.getText.toDouble)
      })

      val contextMenu = new ContextMenu(resize, delete)

      delete.setOnAction(_ => {
        val dir : File = new File("src/output/" + filename)
        dir.deleteRecursively()
        page.getChildren.remove(sp)
        camadas_node = camadas_node.filter(p => p != sp)
      })

      contextMenu.show(square, click.getScreenX, click.getScreenY)



    })

    val moveBar: HBox = new HBox()

    sp.setOnMouseEntered(e =>{
      moveBar.setVisible(true)
    })
    sp.setOnMouseExited(e =>{
      moveBar.setVisible(false)
    })
    moveBar.setVisible(false)

    moveBar.setAlignment(Pos.BOTTOM_CENTER)
    moveBar.setSpacing(20)

    HBox.setMargin(moveBar, new Insets(0,0,20,0))
    moveBar.setPadding(new Insets(0,0,20,0))

    if(num != 0) {
      val prevButton: Button = new Button("<")
      prevButton.setOnAction(e => {
        page.getChildren.remove(sp)
        getPdfView(page,filename, fl, num - 1,square.getHeight)
      })
      moveBar.getChildren.add(prevButton)
    }

    if(fl.size > num+1) {
      val nextButton: Button = new Button(">")
      nextButton.setOnAction(e => {
        page.getChildren.remove(sp)
        getPdfView(page,filename, fl, num + 1,square.getHeight)
      })
      moveBar.getChildren.add(nextButton)
    }
    sp.getChildren.add(moveBar)
    page.getChildren.add(sp)
    camadas_node = sp :: camadas_node
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }


}
