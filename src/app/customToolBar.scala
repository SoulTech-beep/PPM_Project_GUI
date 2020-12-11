package app

import app.ToolType.ToolType
import app.customToolBar.getIcon
import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty, SimpleIntegerProperty, SimpleObjectProperty}
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Polygon, Rectangle, Shape}
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import logicMC.ShapeType.ShapeType
import logicMC.{Eraser, ShapeType}

object ToolType extends Enumeration {

  type ToolType = String

  val pen: String = "PEN"
  val marker: String = "MARKER"
  val eraser: String = "ERASER"
  val selector: String = "SELECTOR"
  val geometricShape: String = "GEOMETRIC_SHAPE"
  val text: String = "TEXT"
  val image: String = "IMAGE"
  val video: String = "VIDEO"
  val pdf: String = "PDF"
  val move: String = "MOVE"
}

class customToolBar {

  val optionsHBox: HBox = new HBox()
  @FXML
  var toolbar: ToolBar = _
  var buttonList: List[Node] = List()
  var penList: List[(Pen, ToolType)] = List()

  var imagePath: String = ""
  var videoPath: String = ""

  var selectedPen: Option[Pen] = None
  var selectedTool: ToolType = ToolType.move

  var shapePen: GeometricShape = GeometricShape(0, new SimpleObjectProperty[Color](Color.BLACK), new SimpleDoubleProperty(1), new SimpleDoubleProperty(1), ShapeType.square, new SimpleObjectProperty[Color](Color.YELLOW))
  var textTool: CustomText = CustomText(new SimpleObjectProperty[Color](Color.BLACK), new SimpleObjectProperty[FontWeight](FontWeight.BOLD), new SimpleIntegerProperty(10), new SimpleDoubleProperty(1))
  var eraserFinal: Eraser = new Eraser(new SimpleDoubleProperty(50))

  var buttonStyle: String = "-fx-background-color: #b2bec3; -fx-background-radius: 25px"

  def setToolbar(tb: ToolBar): Unit = {
    toolbar = tb
  }


  def initializeCustomToolBar(): Unit = {

    toolbar.getItems.clear()

    val penTool: Pen = Pen(0, new SimpleObjectProperty[Color](Color.BLACK), new SimpleDoubleProperty(5), new SimpleDoubleProperty(1))
    val markerTool: Pen = Pen(1, new SimpleObjectProperty[Color](Color.YELLOW), new SimpleDoubleProperty(15), new SimpleDoubleProperty(0.5))

    penList = (penTool, ToolType.pen) :: (markerTool, ToolType.marker) :: penList

    selectedTool = ToolType.pen
    selectedPen = Some(penTool)

    getSeparator

    setMediaButton("PDF", "images/pdf.png", ToolType.image)
    setMediaButton("Video", "images/video.png", ToolType.video, isImagePath = false)
    setMediaButton("Image", "images/image.png", ToolType.image)

    getSeparator

    setTextButton()
    setShapeButton()

    getSeparator

    setEraserButton()
    setPenButton("images/marker.png", ToolType.marker)
    setPenButton("images/ball-point.png", ToolType.pen)

    getSeparator

    setSelectionButton()
    setMoveButton()

    toolbar.getItems.add(optionsHBox)

    HBox.setMargin(optionsHBox, new Insets(0, 0, 0, 12))
    optionsHBox.setSpacing(15)
  }

  def getSeparator: Unit = {
    val separator = new Separator()
    separator.getStylesheets.add("separator.css")
    separator.setId("my-separator")
    separator.setPrefHeight(20)

    HBox.setMargin(separator, new Insets(0, 3, 0, 3))

    toolbar.getItems.add(0, separator)

    buttonList = separator :: buttonList
  }

  def setShapeButton(): Unit = {

    def setGeometricShapeButton(menuItem: MenuItem, menuButton: MenuButton, menuItemsList: List[MenuItem], shapeType: ShapeType, getShape: (Color, Boolean) => Shape): Unit = {
      menuItem.setOnAction(_ => {
        shapePen = shapePen.changeShape(shapeType)
        menuButton.setGraphic(getShape(Color.BLACK, false))
        resetMenuItem(menuItem, menuItemsList)

        buttonSetOnActionHelper(menuButton, ToolType.geometricShape)
      })
    }

    val shapeButton: MenuButton = new MenuButton()

    val circle: MenuItem = new MenuItem("Circle")
    val square: MenuItem = new MenuItem("Square")
    val line: MenuItem = new MenuItem("Line")
    val polygon: MenuItem = new MenuItem("Polygon")

    shapeButton.getItems.addAll(circle, square, line, polygon)

    buttonList = shapeButton :: buttonList

    val menuItemList = List(circle, square, line, polygon)

    setGeometricShapeButton(line, shapeButton, menuItemList, ShapeType.line, getLine)
    setGeometricShapeButton(square, shapeButton, menuItemList, ShapeType.square, getSquare)
    setGeometricShapeButton(circle, shapeButton, menuItemList, ShapeType.circle, getCircle)
    setGeometricShapeButton(polygon, shapeButton, menuItemList, ShapeType.polygon, getHexagon)

    shapeButton.setStyle(buttonStyle)

    shapeButton.setGraphic(getIcon("images/geometric.png"))

    toolbar.getItems.add(0, shapeButton)

  }

  def getToolBarStyledButton(imageLocation: String): Button = {
    val button = new Button()
    button.setStyle(buttonStyle)

    buttonList = button :: buttonList

    button.setGraphic(getIcon(imageLocation))

    toolbar.getItems.add(0, button)

    button
  }

  def setMediaButton(mediaType: String, image: String, toolType: ToolType, isImagePath: Boolean = true): Unit = {
    val mediaButton = getToolBarStyledButton(image)

    mediaButton.setOnAction(_ => {
      if (isImagePath) imagePath = "" else videoPath = ""

      selectedTool = toolType
      optionsHBox.getChildren.clear()
      resetButton(Some(mediaButton))
      getFileChooser(mediaType)
    })

  }

  def setTextButton(): Unit = {
    val textButton: Button = getToolBarStyledButton("images/text.png")

    textButton.setOnAction(_ => buttonSetOnActionHelper(textButton, ToolType.text))
  }

  def setMoveButton(): Unit = {
    val moveButton = getToolBarStyledButton("images/move.png")

    moveButton.setOnAction(_ => {
      selectedTool = ToolType.move
      optionsHBox.getChildren.clear()
      resetButton(Some(moveButton))

    })

  }

  def setEraserButton(): Unit = {

    val eraserButton: Button = getToolBarStyledButton("images/eraser.png")

    eraserButton.setOnAction(event => {
      selectTool(ToolType.eraser)
      resetButton(Some(eraserButton))

    })

  }

  def setPenButton(imageLocation: String, toolType: ToolType): Unit = {

    val penButton: Button = getToolBarStyledButton(imageLocation)

    penButton.setOnAction(event => {
      //TODO order was previously in reverse (selecttool and then reversebutton
      buttonSetOnActionHelper(penButton, toolType)

    })

  }

  def setSelectionButton(): Unit = {

    val selectionButton: Button = getToolBarStyledButton("images/lasso.png")

    selectionButton.setOnAction(event => {
      selectedTool = ToolType.selector
      optionsHBox.getChildren.clear()

      resetButton(Some(selectionButton))
    })

  }

  def getFileChooser(fileType: String): FileChooser = {

    val fileChooser = new FileChooser

    if (fileType == "Image") {
      getSpecificFileChooser("Image", true, "*.png", "*.jpg", "*.gif")
    }
    else if (fileType == "Video") {
      getSpecificFileChooser("Video", false, "*.mp4", "*.avi")

    } else if (fileType == "PDF") {
      getSpecificFileChooser("PDF", true, "*.pdf")

    }

    fileChooser
  }

  def resetButton(buttonToSelect: Option[Control]): Unit = {
    buttonList.foreach(p => p.setStyle(buttonStyle))

    if (buttonToSelect.isDefined) {
      buttonToSelect.get.setStyle("-fx-background-color: #636e72; -fx-background-radius: 25px")
    }

  }

  def resetMenuItem(menuItem: MenuItem, lst: List[MenuItem]): Unit = {
    buttonList.foreach(p => p.setStyle(buttonStyle))
    lst.foreach(p => p.setStyle(""))

    menuItem.setStyle("-fx-padding: 0 10 0 10;-fx-background-radius: 18; -fx-border-radius: 18; -fx-background-color: rgba(99, 110, 114,0.2);")
  }

  def getFontStylePicker(): MenuButton = {
    val menuButton = new MenuButton()
    menuButton.setStyle(buttonStyle)

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

    if (toolName == ToolType.pen || toolName == ToolType.marker) {
      selectedTool = toolName
      selectedPen = Some(penList.find(p => p._2 == toolName).get._1)


      val slOpacity: Slider = getSliderMenu(selectedPen.get.opacity.get(), (0, 1), 0.1)
      val slWidth: Slider = getSliderMenu(selectedPen.get.width.get(), (1, 15))

      setSliderResult[Any](slOpacity, (newOpacity => {
        penList.foreach(p1 => if (p1._2 == toolName) {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeOpacity(newOpacity.doubleValue()), p1._2))
        })
      }))

      setSliderResult[Any](slWidth, (newWidth => {
        penList.foreach(p1 => if (p1._2 == toolName) {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeOpacity(newWidth.doubleValue()), p1._2))
        })
      }))

      optionsHBox.getChildren.addAll( setColorPicker[Pen](_.changeColor(_), a => a.color, transparent = false, toolName=toolName), toMenuItem(slWidth, "images/width.png"), toMenuItem(slOpacity, "images/opacity.png"))

    }

    if (toolName == ToolType.text) {
      getTextTool(toolName)
    }

    if (toolName == ToolType.eraser) {
      getEraserTool(toolName)
    }

    if (toolName == ToolType.geometricShape) {

      selectedTool = toolName

      val slOpacity: Slider = getSliderMenu(shapePen.opacity.get(), (0, 1), 0.1)
      setSliderResult[GeometricShape](slOpacity, shapePen.changeOpacity)

      val slWidth: Slider = getSliderMenu(shapePen.strokeWidth.get(), (1, 15))
      setSliderResult[GeometricShape](slWidth, shapePen.changeWidth)

      optionsHBox.getChildren.addAll(
        setColorPicker[GeometricShape](_.changeFillColor(_), a => a.fillColor, transparent = true, toolName = toolName)
        , toMenuItem(slWidth, "images/width.png")
        , toMenuItem(slOpacity, "images/opacity.png"),
        setColorPicker[GeometricShape](_.changeColor(_), a => a.strokeColor, fill = false, toolName = toolName))

    }

  }

  def setColorPicker[P](f: (P, Color) => P, f1: P => ObjectProperty[Color], fill: Boolean = true, transparent: Boolean = false, toolName: ToolType): MenuButton = {
    val menuButton = new MenuButton()
    menuButton.setStyle(buttonStyle)

    val redColor = new Button()
    val blackColor = new Button()
    val blueColor = new Button()
    val transparentColor = new Button()

    val specifiedColorsMenuItem = new CustomMenuItem()
    val colorPickerMenuItem = new CustomMenuItem()

    val hboxColors = new HBox()
    if (transparent) {
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor, transparentColor)

    } else {
      hboxColors.getChildren.addAll(redColor, blackColor, blueColor)
    }

    hboxColors.setAlignment(Pos.CENTER)
    hboxColors.setSpacing(10)

    specifiedColorsMenuItem.setContent(hboxColors)
    specifiedColorsMenuItem.setHideOnClick(false)

    val colorPicker = new ColorPicker()
    colorPicker.setOnAction(a => {
      changeSelectedToolColor(colorPicker.getValue)
      menuButton.setGraphic(getCircle(colorPicker.getValue, fill))
    })

    colorPickerMenuItem.setContent(colorPicker)
    colorPickerMenuItem.setHideOnClick(false)

    redColor.setGraphic(getCircle(Color.RED, fill))
    blackColor.setGraphic(getCircle(Color.BLACK, fill))
    blueColor.setGraphic(getCircle(Color.BLUE, fill))
    transparentColor.setGraphic(getIcon("images/transparent.png"))

    setUpSetColorButton(redColor, Color.RED)
    setUpSetColorButton(blueColor, Color.BLUE)
    setUpSetColorButton(blackColor, Color.BLACK)

    transparentColor.setOnAction(p => {
      menuButton.setGraphic(getIcon("images/transparent.png"))
      changeSelectedToolColor(Color.TRANSPARENT)
    })

    if (shapePen.fillColor.get() == Color.TRANSPARENT && transparent) {
      menuButton.setGraphic(getIcon("images/transparent.png"))
    } else {
      changeSelectedColorIcon()
    }

    menuButton.getItems.addAll(specifiedColorsMenuItem, colorPickerMenuItem)

    def setUpSetColorButton(button: Button, color: Color):Unit = {
      button.setOnAction(_ => {
        menuButton.setGraphic(getCircle(color, fill))
        changeSelectedToolColor(color)
      })
    }

    def changeSelectedColorIcon():Unit = {
      if(toolName == ToolType.geometricShape){
        menuButton.setGraphic(getCircle(f1(shapePen.asInstanceOf[P]).get(), fill))
      }else{
        menuButton.setGraphic(getCircle(f1(selectedPen.get.asInstanceOf[P]).get(), fill))
      }
    }

    def changeSelectedToolColor(color:Color):Unit = {
      if(toolName == ToolType.geometricShape){
        shapePen = f(shapePen.asInstanceOf[P], color).asInstanceOf[GeometricShape]
      }else{
        penList.foreach(p1 => if (p1._2 == toolName) {
          penList = penList.updated(penList.indexOf(p1), (p1._1.changeColor(color), p1._2))
        })
      }
    }


    menuButton
  }

  def getEraserTool(toolName: ToolType): Unit = {
    selectedTool = toolName

    val eraserRadius: Slider = getSliderMenu(eraserFinal.radius.get(), (30, 100), 30)
    setSliderResult[Eraser](eraserRadius, eraserFinal.changeRadius)

    optionsHBox.getChildren.add(toMenuItem(eraserRadius, "images/width.png"))
  }

  def getTextTool(toolName: ToolType): Unit = {
    selectedTool = toolName

    val slOpacity: Slider = getSliderMenu(textTool.opacity.get(), (0, 1), 0.1)
    setSliderResult[CustomText](slOpacity, textTool.changeOpacity)

    val sliderSize: Slider = getSliderMenu(textTool.textSize.get, (12, 60), 6)
    setSliderResult[CustomText](sliderSize, textTool.changeTextSize)

    val menuButton = new MenuButton()
    menuButton.setGraphic(getCircle(textTool.textColor.get(), fill = true))
    menuButton.setStyle(buttonStyle)

    val colorPicker = new ColorPicker()
    colorPicker.setOnAction(a => {
      textTool.changeColor(colorPicker.getValue)
      menuButton.setGraphic(getCircle(colorPicker.getValue, fill = true))
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

  def getLine(color: Color, fill: Boolean): Polygon = {
    val line = new Polygon()
    line.setStroke(color)
    line.setStrokeWidth(2)
    if (fill)
      line.setFill(color)
    else {
      line.setFill(Color.TRANSPARENT)
    }
    line.getPoints.addAll(1.0, 1.0, 18.4, 18.4)

    line
  }

  def getHexagon(color: Color, fill: Boolean): Polygon = {
    val hex = new Polygon()
    hex.setStroke(color)
    hex.setStrokeWidth(2)
    if (fill)
      hex.setFill(color)
    else {
      hex.setFill(Color.TRANSPARENT)
    }
    hex.getPoints.addAll(1.0, 9.0, 10.0, 1.0, 18.4, 9.0, 15.0, 18.4, 5.0, 18.4)

    hex
  }

  def getSquare(color: Color, fill: Boolean): Rectangle = {
    val square = new Rectangle()
    square.setStroke(color)
    square.setStrokeWidth(2)
    if (fill)
      square.setFill(color)
    else {
      square.setFill(Color.TRANSPARENT)
    }
    square.setHeight(18.4)
    square.setWidth(18.4)

    square
  }

  def getCircle(color: Color, fill: Boolean): Circle = {
    val circle = new Circle()
    circle.setStrokeWidth(2)
    circle.setStroke(color)
    if (fill)
      circle.setFill(color)
    else
      circle.setFill(Color.TRANSPARENT)
    circle.setRadius(9.2)

    circle
  }

  def getSliderMenu(initial: Double, range: (Int, Int), majorTickUnit: Double = 2): Slider = {
    val slider = new Slider(range._1, range._2, initial)
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

    menu.setGraphic(getIcon(imageLocation))
    menu.getItems.add(menuItem)

    menu.setStyle(buttonStyle)

    menu
  }

  def getSpecificFileChooser(fileType: String, isImagePath: Boolean, fileExtension: String*): FileChooser = {

    val fileChooser = new FileChooser
    fileChooser.setTitle("Select " + fileType)

    val extensionFilter = new ExtensionFilter(fileType + " Files")
    fileExtension.foreach(e => extensionFilter.getExtensions.add(e))

    fileChooser.getExtensionFilters.addAll(extensionFilter)
    val selectedFile = fileChooser.showOpenDialog(toolbar.getScene.getWindow)
    if (selectedFile != null) {
      if (isImagePath)
        imagePath = selectedFile.toURI.toString
      else
        videoPath = selectedFile.toURI.toString
    } else {
      resetButton(None)
    }

    fileChooser

  }

  def buttonSetOnActionHelper(button: Control, toolType: ToolType): Unit = {
    resetButton(Some(button))
    selectTool(toolType)
  }

  def setSliderResult[T](slider: Slider, f: Double => T): Unit = {
    slider.valueProperty().addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t: Number, t1: Number): Unit = {
        f(t1.doubleValue())
      }
    })
  }

}

object customToolBar {

  def getIcon(imageLocation: String): ImageView = {
    val icon = new ImageView(new Image(imageLocation))
    icon.setSmooth(true)
    icon.setFitHeight(20)
    icon.setFitWidth(20)

    icon
  }

}
