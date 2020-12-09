package app


import java.io.File

import app.PageStyle.PageStyle
import javafx.animation.{KeyFrame, KeyValue, Timeline}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.event.ActionEvent
import javafx.geometry.{Insets, Point2D, Pos}
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.input.{ContextMenuEvent, KeyCode}
import javafx.scene.layout._
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import javafx.scene.paint.{Color, ImagePattern}
import javafx.scene.shape._
import javafx.scene.text.Text
import javafx.scene.{Node, Scene}
import javafx.stage.{Modality, Stage, WindowEvent}
import javafx.util.Duration
import logicMC.Auxiliary
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.{ImageType, PDFRenderer}
import org.apache.pdfbox.tools.imageio.ImageIOUtil

import scala.reflect.io.Path.jfile2path
import scala.util.control.Breaks

class whiteboardScroller {

  var camadas: List[Polyline] = List()
  var camadas_node : List[Node] = List()

  val pages: List[Pane] = List()
  val toolbar: customToolBar = new customToolBar()

  var canvasFinal : ZoomableScrollPane = null

  def getCanvas(toolBar: customToolBar, pane: Node): ZoomableScrollPane = whiteboardScroller.getCanvas(this,toolBar,pane)

  def createPage(backgroundColor: Color, width: Double, height: Double, toolBar: customToolBar,pane:Node, pageStyle: PageStyle): Pane = whiteboardScroller.createPage(backgroundColor, width, height, toolBar, pageStyle,pane, this)

}

object whiteboardScroller {

  //TODO WHEN SELECTED SHOULD WE BE ABLE TO ERASE EVERYTHING SELECTED?

  def setOnEnter(button:Button, textFields: TextField*):Unit = {
    textFields.foreach(textField => textField.setOnKeyPressed(key => if(key.getCode == KeyCode.ENTER){
     button.fireEvent(new ActionEvent())
   }))
  }

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

  def testeTexto(customToolBar: customToolBar, page:Pane, wb:whiteboardScroller):Text = {

    //só agora aqui selecionarmos a ferramenta na tool bar, de forma a q cada caixa de texto tenha as suas próprias configs...
    val textHolder = new Text("Lorem Ipsum")

    def resizeText(button:Button): (VBox, TextField) = {
      val width = new TextField(textHolder.getLayoutBounds.getWidth.toString)
      val vBox = new VBox(width)

      VBox.setMargin(width, new Insets(10))

      vBox.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBox.setPadding(new Insets(5, 5, 5, 5))
      vBox.setAlignment(Pos.CENTER)
      VBox.setMargin(vBox, new Insets(10,10,0,10))

      setOnEnter(button, width)

      (vBox, width)
    }

    def deleteText(stage:Stage): Button ={
      val deleteButton = new Button("Delete")

      VBox.setMargin(deleteButton, new Insets(0, 10, 20, 10))

      deleteButton.setFont(Auxiliary.getFont(16))

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
        page.getChildren.remove(textHolder)
        wb.camadas_node = wb.camadas_node.filter(p => p != textHolder)
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

      val vBoxTextField = new VBox()
      vBoxTextField.getChildren.add(textField)
      vBoxTextField.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBoxTextField.setPadding(new Insets(5, 5, 5, 5))
      VBox.setMargin(vBoxTextField, new Insets(5,10,0,10))

      val changeButton = PagePicker.getButton("Confirm")
      val resizeTextValues = resizeText(changeButton)

      vBox.getChildren.addAll(resizeTextValues._1,vBoxTextField, changeButton, deleteText(stage))

      changeButton.setOnAction(_ =>{
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST))

        if(textHolder.getBoundsInParent.getMinX + resizeTextValues._2.getText.toDouble < page.getMaxWidth ) {
          textHolder.setWrappingWidth(resizeTextValues._2.getText().toDouble )
        }
      })

      stage.setOnCloseRequest(_ => {
        Auxiliary.blurBackground(30, 0, 500, page)
      })

      VBox.setMargin(changeButton, new Insets(0, 10, 0, 10))
      VBox.setMargin(textField, new Insets(10, 10, 10, 10))

      vBox.setAlignment(Pos.CENTER)
      vBox.setSpacing(20)

      val scene= new Scene(vBox)
      scene.getStylesheets.add("testStyle.css")
      Auxiliary.blurBackground(0, 30, 1000, page)

      stage.setScene(scene)
      stage.initModality(Modality.APPLICATION_MODAL)
      stage.setTitle("Change text")
      stage.getIcons.add(new Image("images/textIcon.png"))
      stage.setResizable(false)
      stage.setWidth(400)

      stage.show()

    })

    textHolder.setFont(Auxiliary.getFontWeight(customToolBar.textTool.textSize.get, customToolBar.textTool.textWeight.get))
    textHolder.setOpacity(customToolBar.textTool.opacity.get)
    textHolder.setFill(customToolBar.textTool.textColor.get)

    textHolder.setWrappingWidth(200-20)

    textHolder
  }

  def createPage(backgroundColor: Color, width: Double, height: Double, toolBar: customToolBar, pageStyle: PageStyle, pane: Node, wb:whiteboardScroller): Pane = {
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
        getPdfView(page, s.split('/').last.replace(".pdf",num.toString),listFiles,0,200, new Point2D(20,20), wb)
        pdfNum = pdfNum + 1
        toolBar.selectedTool = ToolType.move
      }

      if(toolBar.selectedTool == ToolType.image) {
        if (toolBar.imagePath != "") {

          val image: Image = new Image(toolBar.imagePath)
          val iP: ImagePattern = new ImagePattern(image)
          val square = new Rectangle(image.getWidth, image.getHeight, iP)
          page.getChildren.add(square)

          wb.camadas_node = square :: wb.camadas_node

          toolBar.imagePath = ""
          toolBar.selectedTool = ToolType.move

          def deleteImage(delete: MenuItem): Unit = {
            //TODO SERA QUE REALMENTE AS REMOVE OU É COMO O VIDEO??
            delete.setOnAction(_ => {
              wb.camadas_node = wb.camadas_node.filter(p => p != square)
              page.getChildren.remove(square)
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

              if(square.getBoundsInParent.getMinY + height.getText.toDouble < page.getMaxHeight && square.getBoundsInParent.getMinX + width.getText.toDouble < page.getMaxWidth ) {
                square.setHeight(height.getText().toDouble)
                square.setWidth(width.getText().toDouble )
              }

            })
          }
          val cm: ContextMenu = new ContextMenu()
          square.setOnContextMenuRequested(click => contextMenuNode(cm, click, square)(resizeImage)(deleteImage))

        }
      }

      if(toolBar.selectedTool == ToolType.text){
        val texto = testeTexto(toolBar, page, wb)

        texto.setLayoutX(20)
        texto.setLayoutY(20)

        wb.camadas_node = texto :: wb.camadas_node

        page.getChildren.add(texto)

      }

      if(toolBar.selectedTool == ToolType.video) {
        if(toolBar.videoPath != "") {
          val video: Media = new Media(toolBar.videoPath)
          val player: MediaPlayer = new MediaPlayer(video)
          val mediaView: MediaView = new MediaView(player)

          mediaView.setFitHeight(video.getHeight)
          mediaView.setFitWidth(video.getWidth)


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
          videoToolBar.getChildren.addAll(play,pause,fast,slow,restart)

          val sp: StackPane = new StackPane()
          sp.getChildren.addAll( mediaView, videoToolBar)

          page.getChildren.add(sp)

          sp.setOnMouseEntered(e =>{
            videoToolBar.setVisible(true)
          })
          sp.setOnMouseExited(e =>{
            videoToolBar.setVisible(false)
          })
          videoToolBar.setVisible(false)

          videoToolBar.setAlignment(Pos.BOTTOM_CENTER)
          videoToolBar.setSpacing(20)

          HBox.setMargin(videoToolBar, new Insets(0,0,20,0))
          videoToolBar.setPadding(new Insets(0,0,20,0))

          wb.camadas_node = sp :: wb.camadas_node
          toolBar.videoPath = ""
          toolBar.selectedTool = ToolType.move

          def deleteVideo(delete: MenuItem): Unit ={
            delete.setOnAction(_ => {
              player.dispose()
              page.getChildren.remove(sp)
              wb.camadas_node = wb.camadas_node.filter(p => p != sp)
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

              if(sp.getBoundsInParent.getMinY + newSize < page.getMaxHeight && sp.getBoundsInParent.getMinX + newSize*(video.getWidth.toDouble/video.getHeight) < page.getMaxWidth ) {
                mediaView.setFitWidth(newSize*(video.getWidth.toDouble/video.getHeight))
                mediaView.setFitHeight(newSize)
              }
            })
          }

          val cm: ContextMenu = new ContextMenu()
          sp.setOnContextMenuRequested(click => contextMenuNode(cm, click, sp) (resizeVideo) (deleteVideo))
        }
      }

      if (toolBar.selectedTool == ToolType.geometricShape) {
        if (toolBar.shapePen.shape == ShapeType.polygon) {
          if (isFirstPoint) {
            polygon = new Polyline()
            currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
            currentLine.setStroke(toolBar.shapePen.strokeColor.get)
            currentLine.setStrokeWidth(toolBar.shapePen.strokeWidth.get)
            currentLine.setOpacity(toolBar.shapePen.opacity.get)

            polygon.getPoints.addAll(event.getX, event.getY)

            page.getChildren.add(polygon)
            page.getChildren.add(currentLine)

            firstPoint = new Point2D(event.getX, event.getY)
            isFirstPoint = false

            wb.camadas = polygon :: wb.camadas
          } else {
            if (firstPoint.distance(event.getX, event.getY) > 20) {
              currentLine.setEndX(event.getX)
              currentLine.setEndY(event.getY)

              page.getChildren.remove(currentLine)

              currentLine = new Line(event.getX, event.getY, event.getX, event.getY)

              page.getChildren.add(currentLine)

              currentLine.setStroke(toolBar.shapePen.strokeColor.get)
              currentLine.setStrokeWidth(toolBar.shapePen.strokeWidth.get)
              currentLine.setOpacity(toolBar.shapePen.opacity.get)

              polygon.getPoints.addAll(event.getX, event.getY)
            } else {
              polygon.getPoints.addAll(polygon.getPoints.get(0), polygon.getPoints.get(1))
              page.getChildren.remove(currentLine)

              isFirstPoint = true

              def deletePolygon(delete:MenuItem):Unit = {
                delete.setOnAction(_ => {
                  page.getChildren.remove(polygon)
                  wb.camadas = wb.camadas.filter(p=> p!= polygon)
                })
              }

              val contextMenu = new ContextMenu()
              polygon.setOnContextMenuRequested(click => contextMenuNode(contextMenu, click,polygon)(_ => ()) (deletePolygon))

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

          wb.camadas_node = wb.camadas_node.updated(wb.camadas_node.indexOf(teste), teste)
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

        wb.camadas.foreach(c => {
          val shape = Shape.intersect(selectionPolyline, c)
          //val shape = selectionPolyline.intersects(c.getBoundsInParent)
          if (shape.getLayoutBounds.getWidth != -1) {
            selectedPolyline = c :: selectedPolyline
            c.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
          } else {
            c.setStyle("")
          }
        })

        wb.camadas_node.foreach(c => {
          val shape = selectionPolyline.intersects(c.getBoundsInParent)
          if (shape) {
            selectedShapes = c::selectedShapes
            c.setStyle(c.getStyle + "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
          } else {
            c.setStyle("")
          }
        })

      }

    })

    page.setOnMousePressed(event => {

      if(toolBar.selectedTool == ToolType.move) {
        dragX = event.getX
        dragY = event.getY

        val rectangle = new Rectangle(1, 1)
        rectangle.setX(event.getX)
        rectangle.setY(event.getY)

        //TODO rectangle as global variable
        page.getChildren.add(rectangle)

        val newsel :Polyline = wb.camadas.find(c => {

          val shape = Shape.intersect(rectangle, c)
          if(shape.getLayoutBounds.getWidth != -1){
            true
          }else{
            false
          }
        }).getOrElse(null)

        page.getChildren.remove(rectangle)

        //val newsel: Polyline = wb.camadas.find(c => c.intersects(dragX-c.getLayoutX,dragY-c.getLayoutY,1,1) ).getOrElse(null)
        val newselNodes: Node = wb.camadas_node.find(c => c.intersects(dragX-c.getLayoutX,dragY-c.getLayoutY,1,1) ).getOrElse(null)

        if(newsel == null) {

          selectedShapes.foreach(c => c.setStyle(""))
          selectedPolyline.foreach(c => c.setStyle(""))

          if(newselNodes == null) {
            page.getChildren.remove(selectionPolyline)

            selectedPolyline = List()
            selectedShapes = List()

          }  else if (!selectedShapes.contains(newselNodes)) {
            page.getChildren.remove(selectionPolyline)

            selectedPolyline = List()
            selectedShapes = List(newselNodes)
            newselNodes.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
          }
        } else if(!selectedPolyline.contains(newsel)) {
          selectedShapes.foreach(c => c.setStyle(""))
          selectedPolyline.foreach(c => c.setStyle(""))

          page.getChildren.remove(selectionPolyline)

          selectedPolyline = List(newsel)
          selectedShapes = List()


          newsel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0);")
        }


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

          var node:Node = new Node{}

          if (toolBar.shapePen.shape == ShapeType.square) {
            if (isFirstPoint) {
              currentRectangle = new Rectangle()
              node = currentRectangle
              currentRectangle.setX(event.getX)
              currentRectangle.setY(event.getY)
              currentRectangle.setWidth(0)
              currentRectangle.setHeight(0)

              currentRectangle.setStroke(toolBar.shapePen.strokeColor.get())
              currentRectangle.setStrokeWidth(toolBar.shapePen.strokeWidth.get)
              currentRectangle.setFill(toolBar.shapePen.fillColor.get())
              currentRectangle.setOpacity(toolBar.shapePen.opacity.get)

              page.getChildren.add(currentRectangle)
              firstPoint = new Point2D(event.getX, event.getY)
              isFirstPoint = false

              wb.camadas_node = currentRectangle :: wb.camadas_node
            } else {
              isFirstPoint = true
            }
          }

          if (toolBar.shapePen.shape == ShapeType.circle) {
            if (isFirstPoint) {
              currentCircle = new Circle()
              node = currentCircle
              currentCircle.setCenterX(event.getX)
              currentCircle.setCenterY(event.getY)
              currentCircle.setStroke(toolBar.shapePen.strokeColor.get())
              currentCircle.setStrokeWidth(toolBar.shapePen.strokeWidth.get)
              currentCircle.setFill(toolBar.shapePen.fillColor.get)
              currentCircle.setOpacity(toolBar.shapePen.opacity.get)
              
              page.getChildren.add(currentCircle)
              firstPoint = new Point2D(event.getX, event.getY)
              isFirstPoint = false

              wb.camadas_node = currentCircle::wb.camadas_node
            } else {
              isFirstPoint = true
            }
          }

          if (toolBar.shapePen.shape == ShapeType.line) {
            if (isFirstPoint) {
              currentLine = new Line(event.getX, event.getY, event.getX, event.getY)
              node = currentLine

              currentLine.setStroke(toolBar.shapePen.strokeColor.get())
              currentLine.setStrokeWidth(toolBar.shapePen.strokeWidth.get)
              currentLine.setOpacity(toolBar.shapePen.opacity.get)

              page.getChildren.add(currentLine)
              firstPoint = new Point2D(event.getX, event.getY)
              isFirstPoint = false

              wb.camadas_node = currentLine::wb.camadas_node
            } else {
              isFirstPoint = true
            }
          }

          def deleteShape(delete:MenuItem):Unit = {
            delete.setOnAction( _ => {
              page.getChildren.remove(node)
              wb.camadas_node = wb.camadas_node.filter(p => p!= node)
            })
          }

          val contextMenu = new ContextMenu()
          node.setOnContextMenuRequested(click => contextMenuNode(contextMenu, click, node) (_ => ()) (deleteShape))

        }

        if (toolBar.selectedTool == ToolType.pen || toolBar.selectedTool == ToolType.marker) {

          eraserCircle.setOpacity(0)

          currentLayer = new Polyline()
          currentLayer.setSmooth(true)
          currentLayer.setStrokeMiterLimit(1)

          val tempCurrentLayer = currentLayer
          wb.camadas = tempCurrentLayer :: wb.camadas

          currentLayer.setOnContextMenuRequested(click => {

            val delete = new MenuItem("Delete")
            val contextMenu = new ContextMenu(delete)

            delete.setOnAction(_ => {
              wb.camadas = wb.camadas.filter(p => p != currentLayer)
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

          var porApagar: List[Polyline] = List()

          wb.camadas.foreach(c => {

            val range = (0 until c.getPoints.size).toList //it's going to c.getPoints.size-1

            val eraserRadius = toolBar.eraserFinal.radius.get()
            val points = c.getPoints

            val loop = new Breaks

            loop.breakable {
              range.foreach(p => if (p % 2 == 0) {
                if (points.get(p) + c.getLayoutX > event.getX - eraserRadius && points.get(p) +c.getLayoutX < event.getX + eraserRadius && points.get(p + 1) +c.getLayoutY > event.getY - eraserRadius && points.get(p + 1) +c.getLayoutY< event.getY + eraserRadius) {


                  porApagar = c :: porApagar
                  page.getChildren.remove(c)
                  loop.break()
                }
              })
            }

          })
          wb.camadas = wb.camadas.filter(e => !porApagar.contains(e))
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

        //just to move the selectionPolyline!
        if(selectionPolyline.getBoundsInParent.getMinX + event.getX - dragX >= 0 && selectionPolyline.getBoundsInParent.getMaxX + event.getX - dragX <= page.getWidth){
          selectionPolyline.setTranslateX(selectionPolyline.getTranslateX + event.getX - dragX)
        }

        if(selectionPolyline.getBoundsInParent.getMinY + event.getY - dragY >= 0 && selectionPolyline.getBoundsInParent.getMaxY + event.getY - dragY <= page.getHeight) {
          selectionPolyline.setTranslateY(selectionPolyline.getTranslateY + event.getY - dragY)

        }

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

        wb.camadas.foreach(c => {
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

        wb.camadas = wb.camadas.filter(e => !porApagar.contains(e))

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

  def addPageButton(pages: VBox, toolBar:customToolBar, pane:Node, wb:whiteboardScroller):Button = {

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


        if(pagePicker.wasClicked){
          val values = pagePicker.getPage()

          pages.getChildren.add(pages.getChildren.size()-1, whiteboardScroller.createPage(values._1, values._2._1*5, values._2._2*5, toolBar, values._3, pane, wb))
        }

      })

    })

    addPageButton
  }

  def getCanvas(wb: whiteboardScroller,toolBar: customToolBar, pane: Node): ZoomableScrollPane = {

    /////
    if(wb.canvasFinal == null) {
      val canvas = new ZoomableScrollPane()
      val pages = new VBox()

      val page3 = whiteboardScroller.createPage(Color.WHITE, 1200, 1200, toolBar, PageStyle.DOTTED, pane, wb)

      //page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)))

      pages.getChildren.addAll(page3, addPageButton(pages, toolBar, pane, wb))

      pages.setSpacing(50)
      pages.setAlignment(Pos.CENTER)

      canvas.init(pages)

      wb.canvasFinal = canvas

    }
    wb.canvasFinal
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

  def getPdfView(page: Pane,filename: String,fl: List[File], num:Int,h:Double, point: Point2D, wb:whiteboardScroller):Unit = {
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
        page.getChildren.remove(sp)
        wb.camadas_node = wb.camadas_node.filter(p => p != sp)
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
        if(sp.getBoundsInParent.getMinY + newSize < page.getMaxHeight && sp.getBoundsInParent.getMinX + newSize*(image.getWidth/image.getHeight) < page.getMaxWidth ) {
          square.setHeight(size.getText().toDouble)
          square.setWidth(size.getText().toDouble * (image.getWidth / image.getHeight))
        }
      })
    }

    val cm: ContextMenu = new ContextMenu()

    sp.setOnContextMenuRequested(click => contextMenuNode(cm, click, sp) (resizePdf) (deletePdf))

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
        getPdfView(page,filename, fl, num - 1,square.getHeight, new Point2D(sp.getLayoutX, sp.getLayoutY), wb)
      })
      moveBar.getChildren.add(prevButton)
    }

    if(fl.size > num+1) {
      val nextButton: Button = new Button(">")
      nextButton.setOnAction(e => {
        page.getChildren.remove(sp)
        getPdfView(page,filename, fl, num + 1,square.getHeight, new Point2D(sp.getLayoutX, sp.getLayoutY), wb)
      })
      moveBar.getChildren.add(nextButton)
    }
    sp.getChildren.add(moveBar)
    page.getChildren.add(sp)

    sp.setLayoutX(point.getX)
    sp.setLayoutY(point.getY)

    wb.camadas_node = sp :: wb.camadas_node
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






