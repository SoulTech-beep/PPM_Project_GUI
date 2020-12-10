package app

import app.ToolType.{ToolType, selector}
import javafx.beans.property.{ObjectProperty, SimpleBooleanProperty, SimpleDoubleProperty, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.{Node, Scene}
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{HBox, VBox}
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Polygon, Rectangle}
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.{FileChooser, Modality, Stage}
import logicMC.Eraser

object ToolType extends Enumeration {

  type ToolType = String

  val pen:String = "PEN"
  val marker:String = "MARKER"
  val eraser:String = "ERASER"
  val selector:String = "SELECTOR"
  val geometricShape:String = "GEOMETRIC_SHAPE"
  val text:String="TEXT"
  val image:String="IMAGE"
  val video:String = "VIDEO"
  val pdf:String = "PDF"
  val move:String = "MOVE"
}

class customToolBar {

  @FXML
  var toolbar: ToolBar = _

  var selectedPen:Pen = null
  var selectedTool:ToolType = null

  val optionsHBox:HBox = new HBox()

  var textTool: CustomText = CustomText(new SimpleObjectProperty[Color](Color.BLACK), new SimpleObjectProperty[FontWeight](FontWeight.BOLD), new SimpleIntegerProperty(10), new SimpleDoubleProperty(1))

  var imagePath: String = ""
  var videoPath: String = ""
  var buttonList:List[Node] = List()
  var penList:List[(Pen,ToolType)] = List()
  var eraserFinal:Eraser = new Eraser(new SimpleDoubleProperty(50))
  var shapePen:GeometricShape = GeometricShape(0,new SimpleObjectProperty[Color](Color.BLACK), new SimpleDoubleProperty(1), new SimpleDoubleProperty(1), ShapeType.square, new SimpleObjectProperty[Color](Color.YELLOW))

  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb;
  }

  def initializeCustomToolBar(): Unit ={

    toolbar.getItems.clear()

    val penTool: Pen = Pen(0,new SimpleObjectProperty[Color](Color.BLACK), new SimpleDoubleProperty(5), new SimpleDoubleProperty(1))
    val markerTool:Pen = Pen(1, new SimpleObjectProperty[Color](Color.YELLOW), new SimpleDoubleProperty(15),new SimpleDoubleProperty(0.5))

    penList = (penTool, ToolType.pen) :: penList
    penList = (markerTool, ToolType.marker) :: penList

    selectedTool = ToolType.pen
    selectedPen = penTool

    getSeparator()

    setPDFButton()
    setVideoButton()
    setImageButton()

    getSeparator()

    setTextButton()
    setShapeButton()

    getSeparator()

    setEraserButton()
    setPenButton("images/marker.png", ToolType.marker)
    setPenButton("images/ball-point.png", ToolType.pen)

    getSeparator()

    setSelectionButton()
    setMoveButton()

    toolbar.getItems.add(optionsHBox)

    HBox.setMargin(optionsHBox, new Insets(0,0,0,12))
    optionsHBox.setSpacing(15)
  }

  def getSeparator():Unit = {
    val separator = new Separator()
    separator.getStylesheets.add("separator.css")
    separator.setId("my-separator")
    separator.setPrefHeight(20)

    HBox.setMargin(separator, new Insets(0,3,0,3))

    toolbar.getItems.add(0, separator)
  }

  def setShapeButton():Unit = {
    val shapeButton:MenuButton = new MenuButton()

    val circle : MenuItem = new MenuItem("Circle")
    val square : MenuItem = new MenuItem("Square")
    val line : MenuItem = new MenuItem("Line")
    val polygon : MenuItem = new MenuItem("Polygon")


    shapeButton.getItems().addAll(circle, square, line, polygon)

    buttonList = shapeButton :: buttonList

    val menuItemList = List(circle,square,line,polygon)

    line.setOnAction(_ => {
      shapePen = shapePen.changeShape(ShapeType.line)
      selectTool(ToolType.geometricShape)
      shapeButton.setGraphic(getLine(Color.BLACK,false))

      resetMenuItem(line, menuItemList)

      resetButton(shapeButton)
    })

    polygon.setOnAction(_ => {
      shapePen = shapePen.changeShape(ShapeType.polygon)
      selectTool(ToolType.geometricShape)
      shapeButton.setGraphic(getHexagon(Color.BLACK,fill = false))
      resetMenuItem(polygon,menuItemList)
      resetButton(shapeButton)

    })

    circle.setOnAction(_ => {
      shapePen = shapePen.changeShape(ShapeType.circle)
      selectTool(ToolType.geometricShape)
      shapeButton.setGraphic(getCircle(Color.BLACK,fill = false))

      resetMenuItem(circle,menuItemList)

      resetButton(shapeButton)

    })

    square.setOnAction(_ => {
      shapePen = shapePen.changeShape(ShapeType.square)
      selectTool(ToolType.geometricShape)
      shapeButton.setGraphic(getSquare(Color.BLACK,false))

      resetMenuItem(square,menuItemList)

      resetButton(shapeButton)

    })

    shapeButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/geometric.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    shapeButton.setGraphic(icon)

    toolbar.getItems.add(0,shapeButton)

  }

  def setPDFButton(): Unit = {
    val pdfButton:Button = new Button()

    pdfButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    buttonList = pdfButton :: buttonList

    pdfButton.setOnAction(_ => {
      imagePath = ""
      selectedTool = ToolType.pdf
      optionsHBox.getChildren.clear()
      resetButton(pdfButton)

      getFileChooser("PDF")
    })

    val icon = new ImageView(new Image("images/pdf.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    pdfButton.setGraphic(icon)

    toolbar.getItems.add(0,pdfButton)

  }

  def setImageButton(): Unit = {
    val imageButton:Button = new Button()

    imageButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    buttonList = imageButton :: buttonList

    imageButton.setOnAction(_ => {
      imagePath = ""
      selectedTool = ToolType.image
      optionsHBox.getChildren.clear()
      resetButton(imageButton)

      getFileChooser("Image")
    })

    val icon = new ImageView(new Image("images/image.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    imageButton.setGraphic(icon)

    toolbar.getItems.add(0,imageButton)

  }

  def setVideoButton(): Unit = {
    val videoButton: Button = new Button()

    videoButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    buttonList = videoButton :: buttonList

    videoButton.setOnAction(_ => {
      videoPath = ""
      selectedTool = ToolType.video
      optionsHBox.getChildren.clear()
      resetButton(videoButton)

      getFileChooser("Video")
    })

    val icon = new ImageView(new Image("images/video.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    videoButton.setGraphic(icon)

    toolbar.getItems.add(0,videoButton)

  }

  def setTextButton(): Unit = {
    val textButton: Button = new Button()

    textButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    buttonList = textButton :: buttonList

    textButton.setOnAction(_ => {
      resetButton(textButton)

      selectTool(ToolType.text)
    })

    val icon = new ImageView(new Image("images/text.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    textButton.setGraphic(icon)

    toolbar.getItems.add(0,textButton)

  }

  def getFileChooser(fileType : String):FileChooser = {

    val fileChooser = new FileChooser

    if(fileType == "Image"){
      fileChooser.setTitle("Select Image")
      fileChooser.getExtensionFilters.addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"))
      val selectedFile = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
      if(selectedFile != null) imagePath = selectedFile.toURI().toString()
      else resetButton(null)

    }
    else if(fileType == "Video") {
      fileChooser.setTitle("Select Video")
      fileChooser.getExtensionFilters.addAll(new ExtensionFilter("Video Files", "*.mp4", "*.avi"))
      val selectedFile = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
      if(selectedFile != null) videoPath = selectedFile.toURI().toString()
      else resetButton(null)

    } else if(fileType == "PDF") {
      fileChooser.setTitle("Select PDF")
      fileChooser.getExtensionFilters.addAll(new ExtensionFilter("PDF Files", "*.pdf"))
      val selectedFile = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
      if(selectedFile != null) imagePath = selectedFile.toURI().toString()
      else resetButton(null)

    }


    fileChooser
  }

  def resetButton(buttonToSelect: Control):Unit = {
    buttonList.foreach( p => p.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px"))

    if(buttonToSelect != null){
      buttonToSelect.setStyle("-fx-background-color: #636e72; -fx-background-radius: 25px")
    }

  }

  def resetMenuItem(menuItem: MenuItem, lst: List[MenuItem]): Unit ={
    buttonList.foreach( p => p.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px"))
    lst.foreach(p => p.setStyle(""))

    menuItem.setStyle("-fx-padding: 0 10 0 10;-fx-background-radius: 18; -fx-border-radius: 18; -fx-background-color: rgba(99, 110, 114,0.2);")
  }

  def setSelectionButton(): Unit = {

    val selectionButton:Button = new Button()

    buttonList = selectionButton::buttonList

    selectionButton.setOnAction(event => {
      selectedTool = ToolType.selector
      optionsHBox.getChildren.clear()

      resetButton(selectionButton)
    })

    selectionButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/lasso.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    selectionButton.setGraphic(icon)

    toolbar.getItems.add(0,selectionButton)

  }

  def setMoveButton():Unit = {
    val moveButton = new Button()

    buttonList = moveButton :: buttonList

    moveButton.setOnAction(_ => {
      selectedTool = ToolType.move
      optionsHBox.getChildren.clear()
      resetButton(moveButton)

    })

    moveButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/move.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    moveButton.setGraphic(icon)

    toolbar.getItems.add(0,moveButton)

  }

  def setEraserButton(): Unit = {

    val eraserButton:Button = new Button()

    buttonList = eraserButton::buttonList

    eraserButton.setOnAction(event => {
      selectTool(ToolType.eraser)
      resetButton(eraserButton)

    })

    eraserButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image("images/eraser.png"))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    eraserButton.setGraphic(icon)

    toolbar.getItems.add(0,eraserButton)

  }

  def setPenButton(imageLocation: String, toolType: ToolType):Unit = {

    val penButton:Button = new Button()

    buttonList = penButton::buttonList

    penButton.setOnAction(event => {
      selectTool(toolType)
      resetButton(penButton)

    })

    penButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val icon = new ImageView(new Image(imageLocation))
    icon.setFitWidth(20)
    icon.setFitHeight(20)

    penButton.setGraphic(icon)

    toolbar.getItems.add(0,penButton)
  }

  def getFontStylePicker():MenuButton = {
    val menuButton = new MenuButton()
    menuButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")


    val bold = new Button("Bold")
    val light = new Button("Light")
    val regular = new Button("Regular")

    val hBoxStyles = new HBox()
    hBoxStyles.getChildren.addAll(light, regular, bold)
    hBoxStyles.setAlignment(Pos.CENTER)
    hBoxStyles.setSpacing(10)

    val specifiedStyleMenuItem = new CustomMenuItem()
    specifiedStyleMenuItem.setContent(hBoxStyles)
    specifiedStyleMenuItem.setHideOnClick(false)

    bold.setOnAction(p => textTool.changeTextWeight(FontWeight.BLACK))
    light.setOnAction(p => textTool.changeTextWeight(FontWeight.LIGHT))
    regular.setOnAction(p => textTool.changeTextWeight(FontWeight.NORMAL))

    menuButton.getItems.add(specifiedStyleMenuItem)

    val icon = new ImageView(new Image("images/textStyle.png"))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    menuButton.setGraphic(icon)

    menuButton
  }

  def selectTool(toolName: ToolType): Unit = {
    optionsHBox.getChildren.clear()

    if(toolName == ToolType.text){

      selectedTool = toolName

      val slOpacity: Slider = getSliderMenu(textTool.opacity.get(), (0,1), 0.1)

      slOpacity.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          textTool.changeOpacity(t1.doubleValue())
        }
      })

      val sliderSize: Slider = getSliderMenu(textTool.textSize.get, (12,60), 6)
      sliderSize.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          textTool.changeTextSize(t1.intValue())
        }
      })

      val menuButton = new MenuButton()
      menuButton.setGraphic(getCircle(textTool.textColor.get(), true))
      menuButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")


      val colorPicker = new ColorPicker()
      colorPicker.setOnAction(a => {
        textTool.changeTextColor(colorPicker.getValue)
        menuButton.setGraphic(getCircle(colorPicker.getValue,true))
      })

      val colorPickerMenuItem = new CustomMenuItem()

      colorPickerMenuItem.setContent(colorPicker)
      colorPickerMenuItem.setHideOnClick(false)

      menuButton.getItems.addAll(colorPickerMenuItem)

      optionsHBox.getChildren.addAll(
        menuButton,
        toMenuItem(slOpacity, "images/opacity.png"),
        toMenuItem(sliderSize, "images/width.png"),
        getFontStylePicker())

    }

    if(toolName == ToolType.pen || toolName == ToolType.marker){
      selectedTool = toolName
      selectedPen = penList.find(p => p._2 == toolName).get._1

      val dropDown = new MenuButton()
      dropDown.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

      val redColor = new Button()
      val blackColor = new Button()
      val blueColor = new Button()

      val setColors = new CustomMenuItem()
      val colorPickerMenu = new CustomMenuItem()

      val hboxColors = new HBox()
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor)
      hboxColors.setAlignment(Pos.CENTER)
      hboxColors.setSpacing(10)

      setColors.setContent(hboxColors)
      setColors.setHideOnClick(false)

      val colorPicker = new ColorPicker()
      colorPicker.setOnAction(a => {
        dropDown.setGraphic(getCircle(colorPicker.getValue,true))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(colorPicker.getValue), p1._2))
        })
      })
      //colorPicker.getStyleClass.add("button")

      colorPickerMenu.setContent(colorPicker)
      colorPickerMenu.setHideOnClick(false)

      redColor.setGraphic(getCircle(Color.RED,true))
      blackColor.setGraphic(getCircle(Color.BLACK,true))
      blueColor.setGraphic(getCircle(Color.BLUE,true))

      redColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.RED,true))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.RED), p1._2))
        })
      })

      blueColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLUE,true))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.BLUE), p1._2))
        })
      })

      blackColor.setOnAction(p => {
        dropDown.setGraphic(getCircle(Color.BLACK,true))
        penList.foreach(p1 => if(p1._2 == toolName)  {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(Color.BLACK), p1._2))
        })
      })

      dropDown.getItems.addAll(setColors, colorPickerMenu)

      penList.foreach(p => if(p._2 == toolName)  {

        dropDown.setGraphic(getCircle(p._1.color.get(), true))

        val slOpacity: Slider = getSliderMenu(p._1.opacity.get(), (0,1), 0.1)
        val slWidth: Slider = getSliderMenu(p._1.width.get(), (1,15))

        slOpacity.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p1 => if(p1._2 == toolName)  {
              penList = penList.updated(penList.indexOf(p1), (p1._1.changeOpacity(t1.doubleValue()), p1._2))
            })
          }
        })

        slWidth.valueProperty().addListener(new ChangeListener[Number] {
          override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
            penList.foreach(p2 => if(p2._2 == toolName)  {
              penList = penList.updated(penList.indexOf(p2), (p2._1.changeWidth(t1.doubleValue()), p2._2))
            })
          }
        })

        return optionsHBox.getChildren.addAll(dropDown, toMenuItem(slWidth,"images/width.png"), toMenuItem(slOpacity, "images/opacity.png"))
      })

    } else if(toolName == ToolType.eraser) {
      selectedTool = toolName
      val eraserRadius: Slider = getSliderMenu(eraserFinal.radius.get(), (30,100), 30)

      eraserRadius.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          eraserFinal = eraserFinal.changeRadius(t1.doubleValue())
        }
      })

      return optionsHBox.getChildren.add(toMenuItem(eraserRadius,"images/width.png"))

    } else if(toolName == ToolType.geometricShape) {

      selectedTool = toolName

      val slOpacity: Slider = getSliderMenu(shapePen.opacity.get(), (0,1), 0.1)
      val slWidth: Slider = getSliderMenu(shapePen.strokeWidth.get(), (1,15))

      slOpacity.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          shapePen = shapePen.changeOpacity(t1.doubleValue())
        }
      })

      slWidth.valueProperty().addListener(new ChangeListener[Number] {
        override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
          shapePen = shapePen.changeWidth(t1.doubleValue())
        }
      })


        return optionsHBox.getChildren.addAll(
          setColorPicker(((a,b)=> a.changeFillColor(b)), (a) => a.fillColor, true, true)
          ,toMenuItem(slWidth,"images/width.png")
          , toMenuItem(slOpacity, "images/opacity.png"),
          setColorPicker(((a,b)=>a.changeColor(b)), (a)=> a.strokeColor, false))


    }

  }

  def setColorPicker(f:(GeometricShape, Color) => (GeometricShape), f1:(GeometricShape) => (ObjectProperty[Color]), fill:Boolean = true, transparent:Boolean = false):MenuButton = {
    val menuButton = new MenuButton()
    menuButton.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    val redColor = new Button()
    val blackColor = new Button()
    val blueColor = new Button()
    val transparentColor = new Button()

    val specifiedColorsMenuItem = new CustomMenuItem()
    val colorPickerMenuItem = new CustomMenuItem()

    val hboxColors = new HBox()
    if(transparent){
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor, transparentColor)

    }else{
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor)
    }

    hboxColors.setAlignment(Pos.CENTER)
    hboxColors.setSpacing(10)

    specifiedColorsMenuItem.setContent(hboxColors)
    specifiedColorsMenuItem.setHideOnClick(false)

    //TODO can't select custom colors!
    val colorPicker = new ColorPicker()
    colorPicker.setOnAction(a => {
      shapePen = f(shapePen, colorPicker.getValue)
      menuButton.setGraphic(getCircle(colorPicker.getValue,fill))
    })

    colorPickerMenuItem.setContent(colorPicker)
    colorPickerMenuItem.setHideOnClick(false)

    redColor.setGraphic(getCircle(Color.RED,fill))
    blackColor.setGraphic(getCircle(Color.BLACK,fill))
    blueColor.setGraphic(getCircle(Color.BLUE,fill))

    val icon = new ImageView(new Image("images/transparent.png"))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    transparentColor.setGraphic(icon)

    redColor.setOnAction(p => {
      menuButton.setGraphic(getCircle(Color.RED,fill))
      shapePen = f(shapePen, Color.RED)
    })

    blueColor.setOnAction(p => {
      menuButton.setGraphic(getCircle(Color.BLUE,fill))
      shapePen = f(shapePen, Color.BLUE)
    })

    blackColor.setOnAction(p => {
      menuButton.setGraphic(getCircle(Color.BLACK,fill))
      shapePen = f(shapePen, Color.BLACK)
    })

    transparentColor.setOnAction(p => {
      val icon1 = new ImageView(new Image("images/transparent.png"))
      icon1.setSmooth(true)
      icon1.setFitHeight(20)
      icon1.setFitWidth(20)

      menuButton.setGraphic(icon1)
      shapePen = f(shapePen, Color.TRANSPARENT)
    })

    if(shapePen.fillColor.get()==Color.TRANSPARENT && transparent){
      menuButton.setGraphic(icon)
    }else{
      menuButton.setGraphic(getCircle(f1(shapePen).get(), fill))
    }

    menuButton.getItems.addAll(specifiedColorsMenuItem, colorPickerMenuItem)

    menuButton
  }

  def getLine(color: Color, fill:Boolean):Polygon = {
    val line = new Polygon()
    line.setStroke(color)
    line.setStrokeWidth(2)
    if(fill)
      line.setFill(color)
    else {
      line.setFill(Color.TRANSPARENT)
    }
    line.getPoints.addAll(1.0, 1.0, 18.4, 18.4)

    line
  }

  def getHexagon(color: Color, fill:Boolean):Polygon = {
    val hex = new Polygon()
    hex.setStroke(color)
    hex.setStrokeWidth(2)
    if(fill)
      hex.setFill(color)
    else {
      hex.setFill(Color.TRANSPARENT)
    }
    hex.getPoints.addAll(1.0, 9.0, 10.0, 1.0, 18.4, 9.0, 15.0 ,18.4 ,5.0 ,18.4)

    hex
  }

  def getSquare(color: Color, fill:Boolean):Rectangle = {
    val square = new Rectangle()
    square.setStroke(color)
    square.setStrokeWidth(2)
    if(fill)
      square.setFill(color)
    else {
      square.setFill(Color.TRANSPARENT)
    }
    square.setHeight(18.4)
    square.setWidth(18.4)

    square
  }


  def getCircle(color: Color, fill:Boolean):Circle = {
    val circle = new Circle()
    circle.setStrokeWidth(2)
    circle.setStroke(color)
    if(fill)
      circle.setFill(color)
    else
      circle.setFill(Color.TRANSPARENT)
    circle.setRadius(9.2)

    circle
  }

  def getSliderMenu(initial: Double, range:(Int,Int), majorTickUnit: Double = 2):Slider = {
    val slider = new Slider(range._1,range._2, initial)
    slider.setSnapToTicks(true)
    slider.setMajorTickUnit(majorTickUnit)
    slider.setShowTickLabels(true)
    slider
  }

  def toMenuItem(n: Node, imageLocation: String): MenuButton = {
    val menu: MenuButton = new MenuButton()
    val menuItem = new CustomMenuItem()
    menuItem.setContent(n)
    menuItem.setHideOnClick(false)

    val icon = new ImageView(new Image(imageLocation))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    menu.setGraphic(icon)
    menu.getItems.add(menuItem)

    menu.setStyle("-fx-background-color: #b2bec3; -fx-background-radius: 25px")

    menu
  }



}

object customToolBar {

  def getIcon(imageLocation:String): ImageView ={
    val icon = new ImageView(new Image(imageLocation))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    icon
  }

}
