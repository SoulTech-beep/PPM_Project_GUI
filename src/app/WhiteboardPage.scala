package app

import java.io.File

import javafx.animation.{KeyFrame, KeyValue, Timeline}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Point2D, Pos}
import javafx.scene.{Node, Scene}
import javafx.scene.control.{Button, ContextMenu, CustomMenuItem, Label, MenuItem, TextArea, TextField}
import javafx.scene.image.Image
import javafx.scene.input.{ContextMenuEvent, KeyCode, MouseButton, MouseEvent}
import javafx.scene.layout.{AnchorPane, Background, BackgroundFill, CornerRadii, HBox, Pane, StackPane, VBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.{Color, ImagePattern}
import javafx.scene.shape.{Circle, Line, Polyline, Rectangle, Shape}
import javafx.scene.text.Text
import javafx.stage.{Modality, Stage, WindowEvent}
import javafx.util.Duration
import logicMC.{Auxiliary, Colors, PageStyle, ShapeType}
import logicMC.PageStyle.PageStyle
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.{ImageType, PDFRenderer}
import org.apache.pdfbox.tools.imageio.ImageIOUtil

import scala.reflect.io.Path.jfile2path
import scala.util.control.Breaks

class WhiteboardPage(tb: customToolBar) extends Pane{

  val toolbar: customToolBar = tb
  val eraserCircle = new Circle()

  //entity storage
  var camadas: List[Polyline] = List()
  var camadas_node : List[Node] = List()

  //entity creation variables
  var isFirstPoint = true
  var firstPoint: Point2D = new Point2D(0,0)
  var currentLine: Line = new Line()
  var polygon: Polyline = new Polyline()
  var currentLayer = new Polyline()
  var currentCircle: Circle = new Circle()
  var currentRectangle: Rectangle = new Rectangle()

  //entity move variables
  var selectedShapes : List[Node] = List()
  var selectedPolyline :List[Polyline] = List()
  var selectionPolyline: Option[Polyline] = None
  var dragX: Double = 0
  var dragY: Double = 0

  def setup(backgroundColor: Color, width: Double, height: Double, pageStyle: PageStyle):Unit = WhiteboardPage.setup(backgroundColor,width,height,pageStyle,this)

}

object WhiteboardPage {

  var pdfNum : Int = 0

  def setup(backgroundColor: Color, width: Double, height: Double, pageStyle: PageStyle, wp:WhiteboardPage): Unit = {

    wp.setPrefSize(width, height)
    wp.setMaxSize(width, height)
    wp.setBackground(new Background(new BackgroundFill(backgroundColor, CornerRadii.EMPTY, Insets.EMPTY)))

    //TODO criar função para setup de eraser
    wp.eraserCircle.setStrokeWidth(1)
    wp.eraserCircle.setStroke(Color.DARKGRAY)
    wp.eraserCircle.setOpacity(0)
    wp.eraserCircle.setFill(Color.TRANSPARENT)
    wp.getChildren.add(wp.eraserCircle)

    var opacity:Double = 1
    if(backgroundColor == Colors.c3){
      opacity = 0.3
    }

    def applyPageStyle(newPageStyle:PageStyle):Unit = newPageStyle match {
      case PageStyle.DOTTED => Auxiliary.dottedPage(width, height, wp, 30, opacity)
      case PageStyle.LINED => Auxiliary.horizontalLine(width, height, wp, 30, opacity)
      case PageStyle.SQUARED => Auxiliary.squaredPage(width, height, wp, 30, opacity)
      case _ =>
    }

    applyPageStyle(pageStyle)

    mouseClickedListeners(wp)

    mouseMovedListeners(wp)

    mouseReleasedListeners(wp)

    mousePressedListeners(wp)

    mouseEnteredListeners(wp)

    mouseDraggedListeners(wp)

    mouseExitedListeners(wp)
  }


  //MOUSE -----CLICKED----- AND AUXILIARY FUNCTIONS


  def mouseClickedListeners(wp:WhiteboardPage): Unit = {
    wp.setOnMouseClicked(event => {

      if(wp.toolbar.selectedTool == ToolType.pdf) {
        clickedWithPDF(wp)
      }

      else if(wp.toolbar.selectedTool == ToolType.image) {
        clickedWithImage(wp)
      }

      else if(wp.toolbar.selectedTool == ToolType.text){
        clickedWithText(wp)
      }

      else if(wp.toolbar.selectedTool == ToolType.video) {
        clickedWithVideo(wp)
      }

      else if (wp.toolbar.selectedTool == ToolType.geometricShape && event.getButton == MouseButton.PRIMARY) {
        clickedWithGeometric(wp,event)
      }
    })
  }

  def clickedWithPDF(wp:WhiteboardPage): Unit = {
    if (wp.toolbar.imagePath != "") {
      val s: String = wp.toolbar.imagePath.replaceAll("%20", " ")
      val num = pdfNum
      generateImageFromPDF(s, "png", num)
      val listFiles: List[File] = getListOfFiles("src/output/" + s.split('/').last.replace(".pdf", num.toString))
      getPdfView(wp, s.split('/').last.replace(".pdf", num.toString), listFiles, 0, 200, new Point2D(20, 20), wp)
      pdfNum = pdfNum + 1
      wp.toolbar.selectedTool = ToolType.move

      wp.toolbar.resetButton(None)
    }
  }

  def clickedWithImage(wp:WhiteboardPage): Unit = {

    if (wp.toolbar.imagePath != "") {

      val image: Image = new Image(wp.toolbar.imagePath)
      val iP: ImagePattern = new ImagePattern(image)
      val square = new Rectangle(image.getWidth, image.getHeight, iP)
      square.setX(10)
      square.setY(10)
      wp.getChildren.add(square)

      wp.toolbar.resetButton(None)

      wp.camadas_node = square :: wp.camadas_node

      wp.toolbar.imagePath = ""
      wp.toolbar.selectedTool = ToolType.move

      def deleteImage(delete: MenuItem): Unit = {
        delete.setOnAction(_ => {
          wp.camadas_node = wp.camadas_node.filter(p => p != square)
          wp.getChildren.remove(square)
        })
      }

      def resizeImage(resize: CustomMenuItem): Unit = {
        val height = new TextField(square.getHeight.toString)
        val width = new TextField(square.getWidth.toString)
        val setButton = new Button("Change size")
        val vBox = new VBox(height, width, setButton)
        vBox.setSpacing(10)
        vBox.setAlignment(Pos.CENTER)
        resize.setContent(vBox)

        setOnEnter(setButton, height, width)

        setButton.setOnAction(_ => {

          if(square.getBoundsInParent.getMinY + height.getText.toDouble < wp.getMaxHeight && square.getBoundsInParent.getMinX + width.getText.toDouble < wp.getMaxWidth ) {
            square.setHeight(height.getText().toDouble)
            square.setWidth(width.getText().toDouble )
          }

        })
      }
      val cm: ContextMenu = new ContextMenu()
      square.setOnContextMenuRequested(click => contextMenuNode(cm, click, square)(resizeImage)(deleteImage))

    }

  }

  def clickedWithText(wp:WhiteboardPage): Unit = {

    val texto = testeTexto(wp.toolbar, wp)

    texto.setLayoutX(100)
    texto.setLayoutY(100)

    wp.camadas_node = texto :: wp.camadas_node

    wp.getChildren.add(texto)

  }

  def clickedWithVideo(wp:WhiteboardPage): Unit = {

    if(wp.toolbar.videoPath != "") {
      val video: Media = new Media(wp.toolbar.videoPath)
      val player: MediaPlayer = new MediaPlayer(video)
      val mediaView: MediaView = new MediaView(player)

      mediaView.setFitWidth(450)

      def setVideoButton(button:Button,path:String)(fclick: => Unit): Unit = {
        button.setGraphic(customToolBar.getIcon(path))
        button.setOnAction(_ => {
          fclick
        })
      }

      val play: Button = new Button()
      setVideoButton(play,"images/play.png")(player.play())

      val pause: Button = new Button()
      setVideoButton(pause,"images/pause.png")(player.pause())

      val fast: Button = new Button()
      setVideoButton(fast,"images/fast.png")(player.setRate(1.5))

      val slow: Button = new Button()
      setVideoButton(slow,"images/slow.png")(player.setRate(0.5))

      val restart: Button = new Button()
      restart.setGraphic(customToolBar.getIcon("images/restart.png"))
      restart.setOnAction(_ => {
        player.seek(player.getStartTime)
        player.setRate(1)
        player.play()
      })

      val videoToolBar: HBox = new HBox()
      videoToolBar.getChildren.addAll(play,pause,fast,slow,restart)

      val sp: AnchorPane = new AnchorPane()
      sp.getChildren.addAll( mediaView, videoToolBar)

      AnchorPane.setBottomAnchor(videoToolBar, 0)
      AnchorPane.setLeftAnchor(videoToolBar, 0)
      AnchorPane.setRightAnchor(videoToolBar, 0)

      wp.getChildren.add(sp)
      wp.toolbar.resetButton(None)

      sp.setLayoutX(50)
      sp.setLayoutY(50)

      sp.setOnMouseEntered(_=>{
        videoToolBar.setOpacity(1)
      })

      sp.setOnMouseExited(_ =>{
        videoToolBar.setOpacity(0)
      })

      videoToolBar.setOpacity(0)

      videoToolBar.setAlignment(Pos.BOTTOM_CENTER)
      videoToolBar.setSpacing(20)

      HBox.setMargin(videoToolBar, new Insets(0,0,20,0))
      videoToolBar.setPadding(new Insets(0,0,20,0))

      wp.camadas_node = sp :: wp.camadas_node
      wp.toolbar.videoPath = ""
      wp.toolbar.selectedTool = ToolType.move

      def deleteVideo(delete: MenuItem): Unit ={
        delete.setOnAction(_ => {
          player.dispose()
          wp.getChildren.remove(sp)
          wp.camadas_node = wp.camadas_node.filter(p => p != sp)
        })
      }

      def resizeVideo(resize: CustomMenuItem): Unit = {
        val size = new TextField()
        if(mediaView.getFitHeight > 0) {size.setText(mediaView.getFitHeight.toString)}
        else {size.setText(video.getHeight.toString)}

        val set = new Button("Change size")
        val vBox = new VBox(size, set)
        vBox.setSpacing(10)
        vBox.setAlignment(Pos.CENTER)
        resize.setContent(vBox)

        setOnEnter(set, size)

        set.setOnAction(_ => {
          val newSize:Double = size.getText().toDouble

          if(sp.getBoundsInParent.getMinY + newSize < wp.getMaxHeight && sp.getBoundsInParent.getMinX + newSize*(video.getWidth.toDouble/video.getHeight) < wp.getMaxWidth ) {

            if(newSize*(video.getWidth.toDouble/video.getHeight) > 100){

              mediaView.setFitWidth(newSize*(video.getWidth.toDouble/video.getHeight))
              mediaView.setFitHeight(newSize)

            }

          }
        })
      }


      val cm: ContextMenu = new ContextMenu()
      sp.setOnContextMenuRequested(click => contextMenuNode(cm, click, sp) (resizeVideo) (deleteVideo))
    }

  }

  def clickedWithGeometric(wp:WhiteboardPage,event: MouseEvent): Unit = {

    if (wp.toolbar.shapePen.shape == ShapeType.polygon) {
      if (wp.isFirstPoint) {
        wp.polygon = new Polyline()

        setShapeParameters(wp.polygon,wp.toolbar.shapePen)

        wp.currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
        setShapeParameters(wp.currentLine,wp.toolbar.shapePen)

        wp.polygon.getPoints.addAll(event.getX, event.getY)

        wp.getChildren.add(wp.polygon)
        wp.getChildren.add(wp.currentLine)

        wp.firstPoint = new Point2D(event.getX, event.getY)
        wp.isFirstPoint = false

        wp.camadas = wp.polygon :: wp.camadas
      } else {
        if (wp.firstPoint.distance(event.getX, event.getY) > 20) {
          wp.currentLine.setEndX(event.getX)
          wp.currentLine.setEndY(event.getY)

          wp.getChildren.remove(wp.currentLine)

          wp.currentLine = new Line(event.getX, event.getY, event.getX, event.getY)

          wp.getChildren.add(wp.currentLine)

          setShapeParameters(wp.currentLine,wp.toolbar.shapePen)

          wp.polygon.getPoints.addAll(event.getX, event.getY)
        } else {
          wp.polygon.getPoints.addAll(wp.polygon.getPoints.get(0), wp.polygon.getPoints.get(1))
          wp.getChildren.remove(wp.currentLine)

          wp.isFirstPoint = true

          val tempPolygon = wp.polygon

          def deletePolygon(delete:MenuItem):Unit = {
            delete.setOnAction(_ => {
              wp.getChildren.remove(tempPolygon)
              wp.camadas = wp.camadas.filter(p=> p!= wp.polygon)
            })
          }

          val contextMenu = new ContextMenu()
          tempPolygon.setOnContextMenuRequested(click => contextMenuNode(contextMenu, click,tempPolygon)(_ => ()) (deletePolygon))

        }
      }
    }

  }


  //MOUSE -----MOVED----- AND AUXILIARY FUNCTIONS


  def mouseMovedListeners(wp:WhiteboardPage): Unit = {
    wp.setOnMouseMoved(event => {
      if (wp.toolbar.selectedTool.equals(ToolType.eraser)) {
        movedWithEraser(wp,event)
      }
      else if (wp.toolbar.selectedTool == ToolType.geometricShape) {
        movedWithGeometric(wp,event)
      }
    })
  }

  def movedWithEraser(wp:WhiteboardPage,event:MouseEvent):Unit = {
    if (event.getX + wp.eraserCircle.getRadius < wp.getWidth && event.getY + wp.eraserCircle.getRadius < wp.getHeight && event.getX - wp.eraserCircle.getRadius > 0 && event.getY - wp.eraserCircle.getRadius > 0) {
      wp.eraserCircle.setOpacity(1)
      wp.eraserCircle.setCenterX(event.getX)
      wp.eraserCircle.setCenterY(event.getY)
    }
  }

  def movedWithGeometric(wp:WhiteboardPage,event:MouseEvent):Unit = {

    if (wp.toolbar.shapePen.shape == ShapeType.polygon) {
      if (!wp.isFirstPoint) {
        if (wp.firstPoint.distance(new Point2D(event.getX, event.getY)) < 20) {
          wp.currentLine.setEndX(wp.firstPoint.getX)
          wp.currentLine.setEndY(wp.firstPoint.getY)

        } else {
          val deltaX = event.getX - wp.firstPoint.getX
          val deltaY = event.getY - wp.firstPoint.getY
          if(wp.firstPoint.getX + deltaX < wp.getWidth && wp.firstPoint.getX - deltaX > 0 && wp.firstPoint.getY + deltaY < wp.getHeight && wp.firstPoint.getY - deltaY > 0) {
            wp.currentLine.setEndX(event.getX)
            wp.currentLine.setEndY(event.getY)
          }
        }
      }
    }

  }


  //MOUSE -----RELEASED----- AND AUXILIARY FUNCTIONS


  def mouseReleasedListeners(wp:WhiteboardPage): Unit = {

    wp.setOnMouseReleased(_ => {

      if (wp.toolbar.selectedTool == ToolType.geometricShape && wp.toolbar.shapePen.shape != ShapeType.polygon) {
        wp.isFirstPoint=true
      }

      else if(wp.toolbar.selectedTool == ToolType.move) {
        releasedWithMove(wp)
      }

      else if (wp.toolbar.selectedTool == ToolType.selector) {
        releasedWithSelector(wp)
      }

    })

  }

  def releasedWithMove(wp:WhiteboardPage): Unit = {

    wp.selectedShapes.foreach(teste => {

      teste.setLayoutX(teste.getLayoutX + teste.getTranslateX)
      teste.setLayoutY(teste.getLayoutY + teste.getTranslateY)

      teste.setTranslateX(0)
      teste.setTranslateY(0)

      wp.camadas_node = wp.camadas_node.updated(wp.camadas_node.indexOf(teste), teste)
    })

    wp.selectedPolyline.foreach(teste => {

      teste.setLayoutX(teste.getLayoutX + teste.getTranslateX)
      teste.setLayoutY(teste.getLayoutY + teste.getTranslateY)

      teste.setTranslateX(0)
      teste.setTranslateY(0)

    })

  }

  def releasedWithSelector(wp:WhiteboardPage): Unit = {

    wp.selectionPolyline.get.getPoints.add(wp.selectionPolyline.get.getPoints.get(0))
    wp.selectionPolyline.get.getPoints.add(wp.selectionPolyline.get.getPoints.get(1))

    val keyValue1 = new KeyValue(
      wp.selectionPolyline.get.strokeDashOffsetProperty,
      double2Double(0.0)
    )

    val keyValue2 = new KeyValue(
      wp.selectionPolyline.get.strokeDashOffsetProperty,
      double2Double(40.0)
    )

    val timeline = new Timeline(

      new KeyFrame(Duration.ZERO,
        keyValue1),
      new KeyFrame(Duration.seconds(2), keyValue2)
    )

    timeline.setCycleCount(Int.MaxValue)

    timeline.play()

    wp.selectedShapes = List()
    wp.selectedPolyline = List()

    wp.camadas.foreach(c => {
      val shape = Shape.intersect(wp.selectionPolyline.get, c)
      //val shape = selectionPolyline.intersects(c.getBoundsInParent)
      if (shape.getLayoutBounds.getWidth != -1) {
        wp.selectedPolyline = c :: wp.selectedPolyline
        c.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")

        c.toFront()
      } else {
        c.setStyle("")
      }
    })

    wp.camadas_node.foreach(c => {
      val shape = wp.selectionPolyline.get.intersects(c.getBoundsInParent)
      if (shape) {
        wp.selectedShapes = c::wp.selectedShapes
        c.setStyle(c.getStyle + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")

        c.toFront()

      } else {
        c.setStyle("")
      }
    })


  }


  //MOUSE -----PRESSED----- AND AUXILIARY FUNCTIONS


  def mousePressedListeners(wp:WhiteboardPage): Unit = {
    wp.setOnMousePressed(event => {

      if(wp.toolbar.selectedTool == ToolType.move) {
        pressedWithMove(wp,event)
      } else {

        if (wp.selectionPolyline.isDefined) {
          wp.getChildren.remove(wp.selectionPolyline)
        }

        if (wp.toolbar.selectedTool == ToolType.selector) {
          pressedWithSelector(wp,event)
        }

        else if (wp.toolbar.selectedTool == ToolType.geometricShape && event.getButton == MouseButton.PRIMARY) {
          pressedWithGeometric(wp,event)
        }

        else if (wp.toolbar.selectedTool == ToolType.pen || wp.toolbar.selectedTool == ToolType.marker) {
          pressedWithPen(wp, event)
        }

        else if (wp.toolbar.selectedTool == ToolType.eraser) {
          pressedWithEraser(wp,event)
        }
      }
    })
  }

  def pressedWithMove(wp:WhiteboardPage, event: MouseEvent): Unit = {

    wp.dragX = event.getX
    wp.dragY = event.getY

    val rectangle = new Rectangle(1, 1)
    rectangle.setX(event.getX)
    rectangle.setY(event.getY)

    //TODO rectangle as global variable
    wp.getChildren.add(rectangle)

    val newsel :Option[Polyline] = wp.camadas.find(c => {

      val shape = Shape.intersect(rectangle, c)
      if(shape.getLayoutBounds.getWidth != -1){
        true
      }else{
        false
      }
    })

    wp.getChildren.remove(rectangle)

    val newselNodes: Option[Node] = wp.camadas_node.find(c => c.intersects(wp.dragX-c.getLayoutX,wp.dragY-c.getLayoutY,1,1) )

    if(newsel.isEmpty) {


      if(newselNodes.isEmpty) {
        wp.getChildren.remove(wp.selectionPolyline)

        wp.selectedShapes.foreach(c => c.setStyle(""))
        wp.selectedPolyline.foreach(c => c.setStyle(""))

        wp.selectedPolyline = List()
        wp.selectedShapes = List()

      }  else if (!wp.selectedShapes.contains(newselNodes.get)) {

        wp.selectedShapes.foreach(c => c.setStyle(""))
        wp.selectedPolyline.foreach(c => c.setStyle(""))

        wp.getChildren.remove(wp.selectionPolyline)

        wp.selectedPolyline = List()
        wp.selectedShapes = List(newselNodes.get)

        newselNodes.get.toFront()
        newselNodes.get.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")

      }
    } else if(!wp.selectedPolyline.contains(newsel.get)) {

      wp.selectedShapes.foreach(c => c.setStyle(""))
      wp.selectedPolyline.foreach(c => c.setStyle(""))

      wp.getChildren.remove(wp.selectionPolyline)

      wp.selectedPolyline = List(newsel.get)
      wp.selectedShapes = List()

      newsel.get.toFront()
      newsel.get.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
    }

  }

  def pressedWithSelector(wp:WhiteboardPage, event: MouseEvent): Unit = {

    wp.eraserCircle.setOpacity(0)

    wp.selectionPolyline = Some(new Polyline())
    wp.selectionPolyline.get.setSmooth(true)
    wp.selectionPolyline.get.setStrokeMiterLimit(1)
    wp.selectionPolyline.get.setStrokeWidth(2)
    wp.selectionPolyline.get.getStrokeDashArray.addAll(20)
    wp.selectionPolyline.get.setFill(Color.TRANSPARENT)

    wp.getChildren.add(wp.selectionPolyline.get)

    wp.selectionPolyline.get.getPoints.add(event.getX)
    wp.selectionPolyline.get.getPoints.add(event.getY)

  }

  def pressedWithGeometric(wp:WhiteboardPage, event: MouseEvent): Unit = {

    var node:Node = new Node{}

    if (wp.toolbar.shapePen.shape == ShapeType.square) {
      if (wp.isFirstPoint && event.getX + 10 < wp.getWidth && event.getY + 10 < wp.getHeight) {
        wp.currentRectangle = new Rectangle()
        node = wp.currentRectangle
        wp.currentRectangle.setX(event.getX)
        wp.currentRectangle.setY(event.getY)
        wp.currentRectangle.setWidth(10)
        wp.currentRectangle.setHeight(10)

        setShapeParameters(wp.currentRectangle,wp.toolbar.shapePen)

        wp.getChildren.add(wp.currentRectangle)
        wp.firstPoint = new Point2D(event.getX, event.getY)
        wp.isFirstPoint = false

        wp.camadas_node = wp.currentRectangle :: wp.camadas_node
      } else {
        wp.isFirstPoint = true

      }
    }

    if (wp.toolbar.shapePen.shape == ShapeType.circle) {
      if (wp.isFirstPoint && event.getX + 5 < wp.getWidth && event.getY + 5 < wp.getHeight && event.getY - 5 > 0 && event.getX - 5 > 0) {
        wp.currentCircle = new Circle()
        node = wp.currentCircle
        wp.currentCircle.setCenterX(event.getX)
        wp.currentCircle.setCenterY(event.getY)
        setShapeParameters(wp.currentCircle,wp.toolbar.shapePen)
        wp.currentCircle.setRadius(5)

        wp.getChildren.add(wp.currentCircle)

        wp.firstPoint = new Point2D(event.getX, event.getY)
        wp.isFirstPoint = false

        wp.camadas_node = wp.currentCircle::wp.camadas_node
      } else {
        wp.isFirstPoint = true
      }
    }

    if (wp.toolbar.shapePen.shape == ShapeType.line) {
      if (wp.isFirstPoint) {
        wp.currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
        node = wp.currentLine
        wp.currentLine.setEndX(event.getX)
        setShapeParameters(wp.currentLine,wp.toolbar.shapePen)

        wp.getChildren.add(wp.currentLine)
        wp.firstPoint = new Point2D(event.getX, event.getY)
        wp.isFirstPoint = false

        wp.camadas_node = wp.currentLine::wp.camadas_node
      } else {
        wp.isFirstPoint = true
      }
    }

    def deleteShape(delete:MenuItem):Unit = {
      delete.setOnAction( _ => {
        wp.getChildren.remove(node)
        wp.camadas_node = wp.camadas_node.filter(p => p!= node)
      })
    }

    val contextMenu = new ContextMenu()
    node.setOnContextMenuRequested(click => contextMenuNode(contextMenu, click, node) (_ => ()) (deleteShape))

  }

  def pressedWithPen(wp:WhiteboardPage, event: MouseEvent): Unit = {

    wp.eraserCircle.setOpacity(0)

    wp.currentLayer = new Polyline()
    wp.currentLayer.setSmooth(true)
    wp.currentLayer.setStrokeMiterLimit(1)

    val tempCurrentLayer = wp.currentLayer
    wp.camadas = tempCurrentLayer :: wp.camadas

    wp.currentLayer.setOnContextMenuRequested(click => {

      val delete = new MenuItem("Delete")
      val contextMenu = new ContextMenu(delete)

      delete.setOnAction(_ => {
        wp.camadas = wp.camadas.filter(p => p != wp.currentLayer)
        wp.getChildren.remove(tempCurrentLayer)
      })

      contextMenu.show(wp.currentLayer, click.getScreenX, click.getScreenY)
    })

    wp.getChildren.add(wp.currentLayer)


    wp.currentLayer.setStrokeWidth(wp.toolbar.selectedPen.get.width.get())
    wp.currentLayer.setOpacity(wp.toolbar.selectedPen.get.opacity.get())
    wp.currentLayer.setSmooth(true)
    wp.currentLayer.setStroke(wp.toolbar.selectedPen.get.color.get())
    wp.currentLayer.getPoints.add(event.getX)
    wp.currentLayer.getPoints.add(event.getY)

  }

  def pressedWithEraser(wp:WhiteboardPage,event:MouseEvent): Unit = {

    var porApagar: List[Polyline] = List()

    wp.camadas.foreach(c => {

      val range = (0 until c.getPoints.size).toList //it's going to c.getPoints.size-1

      val eraserRadius = wp.toolbar.eraserFinal.radius.get()
      val points = c.getPoints

      val loop = new Breaks

      loop.breakable {
        range.foreach(p => if (p % 2 == 0) {
          if (points.get(p) + c.getLayoutX > event.getX - eraserRadius && points.get(p) +c.getLayoutX < event.getX + eraserRadius && points.get(p + 1) +c.getLayoutY > event.getY - eraserRadius && points.get(p + 1) +c.getLayoutY< event.getY + eraserRadius) {


            porApagar = c :: porApagar
            wp.getChildren.remove(c)
            loop.break()
          }
        })
      }

    })
    wp.camadas = wp.camadas.filter(e => !porApagar.contains(e))

  }


  //MOUSE -----ENTERED----- AND AUXILIARY FUNCTIONS


  def mouseEnteredListeners(wp:WhiteboardPage): Unit = {

    wp.setOnMouseEntered(_ => {
      if (wp.toolbar.selectedTool.equals(ToolType.eraser)) {
        wp.eraserCircle.setOpacity(1)
        wp.eraserCircle.setRadius(wp.toolbar.eraserFinal.radius.get())
      }
    })

  }


  //MOUSE -----EXITED----- AND AUXILIARY FUNCTIONS


  def mouseExitedListeners(wp:WhiteboardPage): Unit = {
    wp.setOnMouseExited(_ => {
      wp.eraserCircle.setOpacity(0)
    })
  }


  //MOUSE -----DRAGGED----- AND AUXILIARY FUNCTIONS


  def mouseDraggedListeners(wp:WhiteboardPage): Unit = {

    wp.setOnMouseDragged(event => {

      if(wp.toolbar.selectedTool == ToolType.move) {
        draggedWithMove(wp,event)
      }

      else if (wp.toolbar.selectedTool == ToolType.geometricShape) {
        draggedWithGeometric(wp,event)
      }

      else if (wp.toolbar.selectedTool == ToolType.selector) {
        draggedWithSelector(wp,event)
      }

      else if (wp.toolbar.selectedTool == ToolType.pen || wp.toolbar.selectedTool == ToolType.marker) {
        draggedWithPen(wp,event)
      }

      else if (wp.toolbar.selectedTool == ToolType.eraser) {
        draggedWithEraser(wp, event)
      }

    })

  }

  def draggedWithMove(wp:WhiteboardPage,event:MouseEvent):Unit = {

    wp.selectedShapes.foreach(teste => {

      if(teste.getBoundsInParent.getMinX + event.getX - wp.dragX >= 0 && teste.getBoundsInParent.getMaxX + event.getX - wp.dragX <= wp.getWidth){
        teste.setTranslateX(teste.getTranslateX + event.getX - wp.dragX)
      }

      if(teste.getBoundsInParent.getMinY + event.getY - wp.dragY >= 0 && teste.getBoundsInParent.getMaxY + event.getY - wp.dragY <= wp.getHeight) {
        teste.setTranslateY(teste.getTranslateY + event.getY - wp.dragY)
      }
    })

    //just to move the selectionPolyline!
    if(wp.selectionPolyline.isDefined) {
      if (wp.selectionPolyline.get.getBoundsInParent.getMinX + event.getX - wp.dragX >= 0 && wp.selectionPolyline.get.getBoundsInParent.getMaxX + event.getX - wp.dragX <= wp.getWidth) {
        wp.selectionPolyline.get.setTranslateX(wp.selectionPolyline.get.getTranslateX + event.getX - wp.dragX)
      }

      if (wp.selectionPolyline.get.getBoundsInParent.getMinY + event.getY - wp.dragY >= 0 && wp.selectionPolyline.get.getBoundsInParent.getMaxY + event.getY - wp.dragY <= wp.getHeight) {
        wp.selectionPolyline.get.setTranslateY(wp.selectionPolyline.get.getTranslateY + event.getY - wp.dragY)
      }
    }
    wp.selectedPolyline.foreach(teste => {

      if(teste.getBoundsInParent.getMinX + event.getX - wp.dragX >= 0 && teste.getBoundsInParent.getMaxX + event.getX - wp.dragX <= wp.getWidth){
        teste.setTranslateX(teste.getTranslateX + event.getX - wp.dragX)
      }

      if(teste.getBoundsInParent.getMinY + event.getY - wp.dragY >= 0 && teste.getBoundsInParent.getMaxY + event.getY - wp.dragY <= wp.getHeight) {
        teste.setTranslateY(teste.getTranslateY + event.getY - wp.dragY)

      }

    })

    wp.dragX = event.getX
    wp.dragY = event.getY

  }

  def draggedWithGeometric(wp:WhiteboardPage,event:MouseEvent):Unit = {

    if (wp.toolbar.shapePen.shape == ShapeType.square) {
      if (!wp.isFirstPoint) {

        val deltaX = event.getX - wp.firstPoint.getX
        val deltaY = event.getY - wp.firstPoint.getY
        if (deltaX < -10 ) {
          if(wp.firstPoint.getX + deltaX > 0) {
            wp.currentRectangle.setX(event.getX)
            wp.currentRectangle.setWidth(-deltaX)
          }
        }
        else {
          if (deltaX > 10 && wp.firstPoint.getX + deltaX < wp.getWidth ) {
            wp.currentRectangle.setX(wp.firstPoint.getX)
            wp.currentRectangle.setWidth(event.getX - wp.firstPoint.getX)
          }
        }
        if (deltaY < -10) {
          if(wp.firstPoint.getY + deltaY > 0) {
            wp.currentRectangle.setY(event.getY)
            wp.currentRectangle.setHeight(-deltaY)
          }
        }
        else {
          if( deltaY > 10 && wp.firstPoint.getY + deltaY < wp.getHeight) {
            wp.currentRectangle.setY(wp.firstPoint.getY)
            wp.currentRectangle.setHeight(event.getY - wp.firstPoint.getY)
          }
        }

      }
    }

    if (wp.toolbar.shapePen.shape == ShapeType.circle) {
      if (!wp.isFirstPoint) {
        val currentPoint = new Point2D(event.getX, event.getY)
        val radius = currentPoint.distance(new Point2D(wp.currentCircle.getCenterX, wp.currentCircle.getCenterY))
        if(radius > 5 && wp.firstPoint.getX + radius < wp.getWidth && wp.firstPoint.getX - radius > 0 && wp.firstPoint.getY + radius < wp.getHeight && wp.firstPoint.getY - radius > 0)
          wp.currentCircle.setRadius(radius)
      }
    }

    if (wp.toolbar.shapePen.shape == ShapeType.line) {
      if (!wp.isFirstPoint) {
        val deltaX = event.getX - wp.firstPoint.getX
        val deltaY = event.getY - wp.firstPoint.getY
        if(wp.firstPoint.getX + deltaX < wp.getWidth && wp.firstPoint.getX + deltaX > 0 && wp.firstPoint.getY + deltaY < wp.getHeight && wp.firstPoint.getY + deltaY > 0) {
          wp.currentLine.setEndX(event.getX)
          wp.currentLine.setEndY(event.getY)
        }
      }
    }

  }

  def draggedWithSelector(wp:WhiteboardPage,event:MouseEvent):Unit = {

    if (event.getX < wp.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < wp.getHeight) {
      wp.selectionPolyline.get.getPoints.add(event.getX)
      wp.selectionPolyline.get.getPoints.add(event.getY)
    }

  }

  def draggedWithPen(wp:WhiteboardPage,event:MouseEvent):Unit = {
    if (event.getX < wp.getWidth && event.getX >= 0 && event.getY >= 0 && event.getY < wp.getHeight) {
      wp.currentLayer.getPoints.add(event.getX)
      wp.currentLayer.getPoints.add(event.getY)
    }
  }

  def draggedWithEraser(wp:WhiteboardPage,event:MouseEvent):Unit = {

    if(event.getX + wp.eraserCircle.getRadius < wp.getWidth && event.getY + wp.eraserCircle.getRadius < wp.getHeight && event.getX - wp.eraserCircle.getRadius > 0 && event.getY - wp.eraserCircle.getRadius > 0) {
      wp.eraserCircle.setOpacity(1)
      wp.eraserCircle.setCenterX(event.getX)
      wp.eraserCircle.setCenterY(event.getY)
    }

    var porApagar: List[Polyline] = List()

    wp.camadas.foreach(c => {
      var i = 0

      val eraserRadius = wp.toolbar.eraserFinal.radius.get()
      val points = c.getPoints

      while (i < points.size() - 1) {

        if (points.get(i) + c.getLayoutX > event.getX - eraserRadius && points.get(i) + c.getLayoutX < event.getX + eraserRadius && points.get(i + 1) + c.getLayoutY > event.getY - eraserRadius && points.get(i + 1) + c.getLayoutY < event.getY + eraserRadius) {
          porApagar = c :: porApagar
          wp.getChildren.remove(c)
          i = points.size()
        } else {
          i = i + 2
        }
      }
    })

    wp.camadas = wp.camadas.filter(e => !porApagar.contains(e))

  }


  //OTHER FUNCTIONS

  def setOnEnter(button:Button, textFields: TextField*):Unit = {
    textFields.foreach(textField => textField.setOnKeyPressed(key => if(key.getCode == KeyCode.ENTER){
      button.fireEvent(new ActionEvent())
    }))
  }

  def testeTexto(customToolBar: customToolBar, wp:WhiteboardPage):Text = {
    customToolBar.selectedTool = ToolType.move

    //só agora aqui selecionarmos a ferramenta na tool bar, de forma a q cada caixa de texto tenha as suas próprias configs...
    val textHolder = new Text("Lorem Ipsum")

    def resizeText(button:Button): (VBox, TextField) = {
      val width = new TextField(textHolder.getLayoutBounds.getWidth.toString)

      val label = new Label("Text width")
      label.setFont(Auxiliary.getFont(14)())
      label.setPadding(new Insets(5,0,0,5))

      val vBox = new VBox(label,width)

      VBox.setMargin(width, new Insets(10))

      vBox.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBox.setPadding(new Insets(5, 5, 5, 5))
      vBox.setAlignment(Pos.TOP_LEFT)
      VBox.setMargin(vBox, new Insets(10,10,0,10))

      setOnEnter(button, width)

      (vBox, width)
    }

    def deleteText(stage:Stage): Button ={
      val deleteButton = new Button("Delete")

      VBox.setMargin(deleteButton, new Insets(5, 10, 20, 10))

      deleteButton.setFont(Auxiliary.getFont(16)())

      val style = "-fx-background-radius:15px; -fx-text-fill: white;"

      deleteButton.setStyle(style + "-fx-background-color:#ff7675;")

      deleteButton.setOnMouseEntered(_ => {
        deleteButton.setStyle(style + "-fx-background-color:#d63031;")
      })

      deleteButton.setOnMouseExited(_ => {
        deleteButton.setStyle(style + "-fx-background-color:#ff7675;")
      })

      deleteButton.setMaxWidth(Double.MaxValue)
      deleteButton.setPrefHeight(35)

      deleteButton.setOnAction(_ => {
        wp.getChildren.remove(textHolder)
        wp.camadas_node = wp.camadas_node.filter(p => p != textHolder)
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))
      })

      deleteButton
    }


    textHolder.setOnContextMenuRequested(_ => {
      val stage = new Stage()

      val textField = new TextArea(textHolder.getText)

      textField.textProperty().addListener(new ChangeListener[PageStyle] {
        override def changed(observableValue: ObservableValue[_ <: PageStyle], t: PageStyle, t1: PageStyle): Unit = {
          textHolder.setText(t1)
        }
      })

      val vBox = new VBox()
      vBox.setStyle("-fx-background-color: white;")

      val label = new Label("Text")
      label.setFont(Auxiliary.getFont(14)())
      label.setPadding(new Insets(5,0,0,5))

      val vBoxTextField = new VBox(label)
      vBoxTextField.getChildren.add(textField)
      vBoxTextField.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBoxTextField.setPadding(new Insets(5, 5, 5, 5))
      VBox.setMargin(vBoxTextField, new Insets(5,10,0,10))

      val changeButton = Auxiliary.getButtonWithColor(name = "Confirm")
      val resizeTextValues = resizeText(changeButton)

      vBox.getChildren.addAll(resizeTextValues._1,vBoxTextField, changeButton, deleteText(stage))
      vBox.setPadding(new Insets(10,10,5,10))

      changeButton.setOnAction(_ =>{
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))

        if(textHolder.getBoundsInParent.getMinX + resizeTextValues._2.getText.toDouble < wp.getMaxWidth ) {
          textHolder.setWrappingWidth(resizeTextValues._2.getText().toDouble )
        }
      })

      stage.setOnCloseRequest(_ => {
        Auxiliary.blurBackground(30, 0, 500, wp)
      })

      VBox.setMargin(changeButton, new Insets(10, 10, 0, 10))
      VBox.setMargin(textField, new Insets(10, 10, 10, 10))

      vBox.setAlignment(Pos.CENTER)
      vBox.setSpacing(20)

      val scene= new Scene(vBox)
      scene.getStylesheets.add("testStyle.css")
      Auxiliary.blurBackground(0, 30, 1000, wp)

      stage.setScene(scene)
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle("Change text")
      stage.getIcons.add(new Image("images/textIcon.png"))
      stage.setResizable(false)
      stage.setWidth(400)

      stage.show()

    })

    textHolder.setFont(Auxiliary.getFont(customToolBar.textTool.textSize.get)(customToolBar.textTool.textWeight.get))
    textHolder.setOpacity(customToolBar.textTool.opacity.get)
    textHolder.setFill(customToolBar.textTool.textColor.get)

    textHolder.setWrappingWidth(200-20)

    textHolder
  }

  def setShapeParameters(shape:Shape, pen:GeometricShape):Unit = {
    shape.setStroke(pen.strokeColor.get())
    shape.setStrokeWidth(pen.strokeWidth.get)
    shape.setOpacity(pen.opacity.get)
    shape.setFill(pen.fillColor.get)
  }

  //TODO tirar for
  def generateImageFromPDF(filename: String, extension: String, ranNum: Int): Unit = {

    val document = PDDocument.load(new File(filename.replace("file:/","")))
    val pdfRenderer = new PDFRenderer(document)
    val file:File = new File("src/output/" + filename.split('/').last.replace(".pdf",ranNum.toString))
    file.mkdir()
    for (page <- 0 until document.getNumberOfPages) {
      val bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB)
      ImageIOUtil.writeImage(bim, String.format("src/output/" + file.getName + "/pdf-" + ( page + 1) + "." + extension), 300)
    }
    document.close()
  }

  //noinspection AccessorLikeMethodIsUnit
  def getPdfView(page: Pane, filename: String, fl: List[File], num:Int, h:Double, point: Point2D, wp:WhiteboardPage):Unit = {
    val sp:StackPane = new StackPane()
    val path:String = fl(num).toURI.toString.replaceAll("%20"," ")
    val image: Image = new Image(path)
    val iP: ImagePattern = new ImagePattern(image)
    val square = new Rectangle(image.getWidth, image.getHeight, iP)
    square.setHeight(h)
    square.setWidth(h*(image.getWidth/image.getHeight))
    square.setStroke(Color.BLACK)
    sp.getChildren.add(square)
    square.setStrokeWidth(square.getWidth/100)

    def deletePdf(delete: MenuItem): Unit ={
      delete.setOnAction(_ => {
        val dir : File = new File("src/output/" + filename)
        dir.deleteRecursively()
        wp.getChildren.remove(sp)
        wp.camadas_node = wp.camadas_node.filter(p => p != sp)
      })
    }
    def resizePdf(resize: CustomMenuItem): Unit = {
      val size = new TextField(square.getHeight.toString)
      val set = new Button("Change size")
      val vBox = new VBox(size, set)
      vBox.setSpacing(10)
      vBox.setAlignment(Pos.CENTER)
      resize.setContent(vBox)

      setOnEnter(set, size)

      set.setOnAction(_ => {
        val newSize:Double = size.getText().toDouble
        if(sp.getBoundsInParent.getMinY + newSize < wp.getMaxHeight && sp.getBoundsInParent.getMinX + newSize*(image.getWidth/image.getHeight) < wp.getMaxWidth ) {
          square.setHeight(size.getText().toDouble)
          square.setWidth(size.getText().toDouble * (image.getWidth / image.getHeight))
        }
      })
    }

    val cm: ContextMenu = new ContextMenu()

    sp.setOnContextMenuRequested(click => contextMenuNode(cm, click, sp) (resizePdf) (deletePdf))

    val moveBar: HBox = new HBox()

    sp.setOnMouseEntered(_ =>{
      moveBar.setVisible(true)
    })
    sp.setOnMouseExited(_ =>{
      moveBar.setVisible(false)
    })
    moveBar.setVisible(false)

    moveBar.setAlignment(Pos.BOTTOM_CENTER)
    moveBar.setSpacing(20)

    HBox.setMargin(moveBar, new Insets(0,0,20,0))
    moveBar.setPadding(new Insets(0,0,20,0))

    if(num != 0) {
      val prevButton: Button = new Button("<")
      prevButton.setOnAction(_ => {
        wp.getChildren.remove(sp)
        getPdfView(page,filename, fl, num - 1,square.getHeight, new Point2D(sp.getLayoutX, sp.getLayoutY), wp)
      })
      moveBar.getChildren.add(prevButton)
    }

    if(fl.size > num+1) {
      val nextButton: Button = new Button(">")
      nextButton.setOnAction(_ => {
        wp.getChildren.remove(sp)
        getPdfView(page,filename, fl, num + 1,square.getHeight, new Point2D(sp.getLayoutX, sp.getLayoutY), wp)
      })
      moveBar.getChildren.add(nextButton)
    }
    sp.getChildren.add(moveBar)
    wp.getChildren.add(sp)

    sp.setLayoutX(point.getX)
    sp.setLayoutY(point.getY)

    wp.camadas_node = sp :: wp.camadas_node
  }

  def getListOfFiles(dir: String):List[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile).toList
    } else {
      List[File]()
    }
  }

  def contextMenuNode(cm: ContextMenu, click: ContextMenuEvent, node : Node)(fResize: CustomMenuItem  => Unit) (fDelete: MenuItem => Unit) :Unit = {
    cm.getItems.clear()
    val resize : CustomMenuItem = new CustomMenuItem()
    val delete = new MenuItem("Delete")

    cm.setAutoHide(true)
    fResize(resize)
    fDelete(delete)
    cm.getItems.addAll(resize, delete)

    cm.show(node, click.getScreenX, click.getScreenY)

  }

}
