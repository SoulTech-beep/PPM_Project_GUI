package app

import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.image.{Image, WritableImage}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.scene.{Node, Scene, SnapshotParameters}
import javafx.stage.{DirectoryChooser, Modality, Stage}
import logicMC.PageStyle.PageStyle
import logicMC.{Auxiliary, Whiteboard}


class WhiteboardGUI(tb: customToolBar, wb:Whiteboard, pane:SplitPane) extends ZoomableScrollPane {

  val whiteboardProperties:Whiteboard = wb
  val insertedIn:SplitPane = pane
  val pages: VBox = new VBox()

  val toolbar: customToolBar = tb
  var canvasFinal : Option[ZoomableScrollPane] = None

  def toPdf():Unit = {

    var images: List[WritableImage] = List()

    pages.getChildren.forEach(p => {
      val writableImage: WritableImage = p.snapshot(new SnapshotParameters, null)
      images = writableImage :: images
    })

    val directoryChooser = new DirectoryChooser


  }

  def getCanvas: ZoomableScrollPane = {
    if(canvasFinal.isEmpty)
      WhiteboardGUI.createCanvas(this)

    canvasFinal.get
  }

  def createPage(backgroundColor: Color, width: Double, height: Double, pageStyle: PageStyle): Pane = WhiteboardGUI.createCanvasPage(backgroundColor, width, height, pageStyle, this)

}

object WhiteboardGUI {

  var pdfNum : Int = 0

  def createCanvasPage(backgroundColor: Color, width: Double, height: Double, pageStyle: PageStyle, wb:WhiteboardGUI): Pane = {

    val page = new WhiteboardPage(wb.toolbar)

    page.setup(backgroundColor,width,height,pageStyle)

    page
  }

  def addPageButton(pages: VBox, pane:Node, wb:WhiteboardGUI):Button = {

    val addPageButton = new Button("Add new page")
    addPageButton.setFont(Auxiliary.getFont(14)())
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
          val values = pagePicker.getPage

          pages.getChildren.add(pages.getChildren.size()-1, WhiteboardGUI.createCanvasPage(values._1, values._2._1, values._2._2, values._3, wb))
        }

      })

    })

    addPageButton
  }

  def createCanvas(wb: WhiteboardGUI): Unit = {

    val canvas = new ZoomableScrollPane()

    val page = createCanvasPage(wb.whiteboardProperties.color, wb.whiteboardProperties.size._1, wb.whiteboardProperties.size._2, wb.whiteboardProperties.style, wb)

    wb.pages.getChildren.addAll(page, addPageButton(wb.pages, wb.insertedIn, wb))

    wb.pages.setSpacing(50)
    wb.pages.setAlignment(Pos.CENTER)

    wb.pages.setPadding(new Insets(100,100,50,100))

    canvas.init(wb.pages)

    wb.canvasFinal = Some(canvas)
  }
}