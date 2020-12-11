package logicMC

import javafx.animation.{KeyFrame, KeyValue, Timeline}
import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty, SimpleObjectProperty}
import javafx.geometry.{Insets, Pos}
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.effect.GaussianBlur
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout.{HBox, Pane, Priority, VBox}
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Line}
import javafx.scene.text.{Font, FontWeight}
import javafx.scene.{Node, Scene}
import javafx.stage.{Modality, Stage}
import javafx.util.Duration
import logicMC.Colors.Colors


object ShapeType extends Enumeration {

  type ShapeType = String

  val square: String = "SQUARE"
  val circle: String = "CIRCLE"
  val polygon: String = "POLYGON"
  val line: String = "LINE"

}

object PageStyle extends Enumeration {

  type PageStyle = String

  val DOTTED = "DOTTED"
  val SQUARED = "SQUARED"
  val LINED = "LINED"
  val SIMPLE = "SIMPLE"
}

object Colors extends Enumeration {
  type Colors = Color
  val c1: Colors = Color.WHITE
  val c2: Colors = Color.web("#ffeaa7")
  val c3: Colors = Color.web("#2d3436")
}

object PageSize extends Enumeration {

  type PageSize = (Double, Double)

  val A4: (Double, Double) = (1050, 1485)
  val A3: (Double, Double) = (1485, 2100)
}


class Auxiliary() {

}

object Auxiliary {

  //App default shadow effect
  val shadowEffect: String = getShadow()
  //App default font
  val myFont: Font = Font.font("SF Pro Display", FontWeight.BLACK, 12)

  def squaredPage(width: Double, height: Double, pane: Pane, step: Int, opacity: Double = 1): Unit = {
    verticalLines(width, height, pane, step, opacity)
    horizontalLine(width, height, pane, step, opacity)
  }

  def verticalLines(width: Double, height: Double, pane: Pane, step: Int, opacity: Double = 1): Unit = {
    val i = (step to width.toInt - step) by step

    i.foreach(w => {
      pane.getChildren.add(drawLine(w, w, 5, height - 5, opacity))
    })
  }

  //Axuxiliary methods for whiteboard background.
  def drawLine(startX: Double, endX: Double, startY: Double, endY: Double, opacity: Double = 1): Line = {
    val line = new Line()

    line.setStartX(startX)
    line.setEndX(endX)

    line.setStartY(startY)
    line.setEndY(endY)

    line.setOpacity(opacity)

    line.setStrokeWidth(2)
    line.setFill(Color.LIGHTGRAY)
    line.setStroke(Color.LIGHTGRAY)

    line
  }

  def horizontalLine(width: Double, height: Double, pane: Pane, step: Int, opacity: Double = 1): Unit = {
    val j = (step to height.toInt - step) by step

    j.foreach(h => {
      pane.getChildren.add(0, drawLine(5, width - 5, h, h, opacity))
    })
  }

  def dottedPage(width: Double, height: Double, pane: Pane, step: Int, opacity: Double = 1): Unit = {
    val j = (step to height.toInt - step) by step
    val i = (step to width.toInt - step) by step

    j.foreach(h => {
      i.foreach(w => {
        val circle = new Circle()

        circle.setCenterX(w)
        circle.setCenterY(h)

        circle.setOpacity(opacity)

        circle.setRadius(1.5)
        circle.setFill(Color.LIGHTGRAY)
        circle.setStroke(Color.LIGHTGRAY)

        pane.getChildren.add(0, circle)
      })
    })

  }

  def getImageView(imageLocation: String): ImageView = {
    val imageView = new ImageView(new Image(imageLocation))
    imageView.setPreserveRatio(true)
    imageView.setSmooth(true)
    imageView.setCache(true)
    imageView.setFitHeight(50)

    imageView
  }

  def getColorPicker: (HBox, ObjectProperty[Color]) = {

    val selectedColor: ObjectProperty[Color] = new SimpleObjectProperty[Color](Colors.c1)

    val colorPicker = new HBox()

    val c1 = getCircle(Colors.c1)
    val c2 = getCircle(Colors.c2)
    val c3 = getCircle(Colors.c3)

    val buttons = List(c1, c2, c3)

    //Default selected button
    c1.setStyle(getShadow(0.4))
    c1.setStrokeWidth(1.5)
    c1.setStyle(shadowEffect)

    setOnClickColor(c1, buttons, selectedColor, Colors.c1)
    setOnClickColor(c2, buttons, selectedColor, Colors.c2)
    setOnClickColor(c3, buttons, selectedColor, Colors.c3)

    colorPicker.setAlignment(Pos.CENTER)
    colorPicker.setPadding(new Insets(10, 0, 10, 0))

    colorPicker.getChildren.addAll(getSpacer, c1)
    colorPicker.getChildren.addAll(getSpacer, c2)
    colorPicker.getChildren.addAll(getSpacer, c3, getSpacer)

    (colorPicker, selectedColor)
  }

  def getShadow(opacity: Double = 0.15): String = {
    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, " + opacity + "), 15, 0, 0, 0);"
  }

  def getSpacer: HBox = {
    val spacer = new HBox()
    HBox.setHgrow(spacer, Priority.ALWAYS)

    spacer
  }

  def setOnClickColor(circle: Circle, list: List[Circle], selected: ObjectProperty[Color], color: Color): Unit = {

    circle.setOnMouseClicked(_ => {
      selected.setValue(color)
      circle.setStrokeWidth(1.5)
      circle.setStyle(shadowEffect)

      list.foreach(p => if (p != circle) {
        p.setStyle("")
        p.setStrokeWidth(1)
      })
    })
  }

  def getCircle(colors: Colors): Circle = {
    val circle = new Circle()
    circle.setFill(colors)
    circle.setRadius(15)
    circle.setStroke(Color.BLACK)
    circle.setStrokeWidth(1.0)

    setScaleAnimation(circle)

    circle
  }

  def setScaleAnimation(node: Node): Unit = {
    node.setOnMouseEntered(_ => {
      node.setScaleX(1.1)
      node.setScaleY(1.1)
    })

    node.setOnMouseExited(_ => {
      node.setScaleX(1)
      node.setScaleY(1)
    })
  }

  def getStyledHBox: HBox = {
    val hBox = new HBox()

    hBox.setAlignment(Pos.CENTER)
    hBox.setPadding(new Insets(10, 0, 10, 0))

    hBox
  }

  def blurBackground(startValue: Double, endValue: Double, duration: Double, pane: Node): Unit = {
    val gaussianBlur = new GaussianBlur(startValue)
    val value = new SimpleDoubleProperty(startValue)

    pane.setEffect(gaussianBlur)

    value.addListener((_, _, newV) => {
      gaussianBlur.setRadius(newV.doubleValue())
    })

    val timeline = new Timeline()
    val kv: KeyValue = new KeyValue(value, double2Double(endValue))
    val kf = new KeyFrame(Duration.millis(duration), kv)

    timeline.getKeyFrames.add(kf)
    timeline.play()
  }

  def getPopup[T](popupStageTitle: String, popupLabelTitle: String, popupTextFieldTitle: String, confirmationButton: (String, String, String), label: Label, update: T => Unit, change: String => T): Unit = {
    val popup = setUpPopup(popupStageTitle, popupLabelTitle, popupTextFieldTitle, confirmationButton)

    setUpPopupTextFieldChangedUpdateRespectiveLabel[T](popup._2, label, popup._1, update, change)

    setUpPopupButtonClicked[T](label, popup._2, popup._1, popup._3, update, change)

  }

  def setUpPopup(popupStageTitle: String, popupLabelTitle: String, popupTextFieldTitle: String, confirmationButton: (String, String, String)): (Stage, TextField, Button) = {

    val popupStage: Stage = setUpPopupStage(popupStageTitle)
    val label: Label = setUpPopupLabel(popupLabelTitle)

    val nameTextField: TextField = setUpPopupTextField(popupTextFieldTitle)

    val vBoxTextField: VBox = setUpPopupSection(label, nameTextField)(" -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")

    val okButton = Auxiliary.getButtonWithColor(confirmationButton._1, confirmationButton._2, confirmationButton._3)

    val innervBox = setUpPopupSection(vBoxTextField, okButton)()
    innervBox.setSpacing(20)
    innervBox.setAlignment(Pos.CENTER)
    innervBox.setPadding(new Insets(10, 10, 10, 10))

    setUpPoupScene(innervBox, popupStage)

    popupStage.show()

    (popupStage, nameTextField, okButton)

  }

  def getButtonWithColor(color1: String = "55efc4", hoverColor: String = "00b894", name: String): Button = {
    val deleteButton = new Button(name)

    VBox.setMargin(deleteButton, new Insets(0, 10, 10, 10))

    deleteButton.setFont(Auxiliary.getFont(16)())

    val style = "-fx-background-radius:15px; -fx-text-fill: white;"

    deleteButton.setStyle(style + "-fx-background-color:#" + color1 + ";")

    deleteButton.setOnMouseEntered(_ => {
      deleteButton.setStyle(style + "-fx-background-color:#" + hoverColor + ";")
    })

    deleteButton.setOnMouseExited(_ => {
      deleteButton.setStyle(style + "-fx-background-color:#" + color1 + ";")
    })

    deleteButton.setMaxWidth(Double.MaxValue)
    deleteButton.setPrefHeight(35)

    deleteButton
  }

  def setUpPopupStage(popupTitle: String): Stage = {
    val popupStage = new Stage()
    popupStage.setTitle(popupTitle)
    popupStage.initModality(Modality.APPLICATION_MODAL)

    popupStage
  }

  def setUpPopupLabel(text: String): Label = {
    val label = new Label(text)
    label.setFont(Auxiliary.getFont(14)())
    label.setPadding(new Insets(5, 0, 0, 5))

    label
  }

  def getFont(size: Int = 12)(fontWeight: FontWeight = FontWeight.BLACK): Font = {
    Font.font("SF Pro Display", fontWeight, size)
  }

  def setUpPopupTextField(text: String): TextField = {
    val nameTextField = new TextField(text)
    nameTextField.setFont(Auxiliary.getFont(14)(FontWeight.LIGHT))
    nameTextField.setPromptText("New name")
    nameTextField.selectAll()

    VBox.setMargin(nameTextField, new Insets(10, 10, 10, 10))

    nameTextField
  }

  def setUpPopupSection(nodes: Node*)(style: String = ""): VBox = {
    val vBoxTextField = new VBox()
    nodes.foreach(node => vBoxTextField.getChildren.add(node))

    vBoxTextField.setStyle("-fx-background-color:white;" + style)
    vBoxTextField.setPadding(new Insets(5, 5, 5, 5))
    VBox.setMargin(vBoxTextField, new Insets(10, 10, 0, 10))

    vBoxTextField
  }

  def setUpPoupScene(content: VBox, popupStage: Stage): Scene = {
    val scene = new Scene(content)
    scene.getStylesheets.add("testStyle.css")

    popupStage.setTitle("Change name")
    popupStage.getIcons.add(new Image("images/renameIcon.png"))
    popupStage.setWidth(400)
    popupStage.setResizable(false)

    popupStage.setScene(scene)

    scene
  }

  def setUpPopupTextFieldChangedUpdateRespectiveLabel[T](textField: TextField, label: Label, stage: Stage, update: T => Unit, change: String => T): Unit = {

    textField.setOnKeyPressed(key => {
      if (key.getCode == KeyCode.ENTER) {
        checkTextFieldAndChange(textField, label, stage)
        update(change(label.getText))
      }
    })

  }

  def setUpPopupButtonClicked[T](label: Label, textField: TextField, stage: Stage, button: Button, update: T => Unit, change: String => T): Unit = {
    button.setOnMouseClicked(_ => {
      checkTextFieldAndChange(textField, label, stage)
      update(change(textField.getText))
    })

  }

  def checkTextFieldAndChange(textField: TextField, label: Label, popup: Stage): Unit = {
    if (!textField.getText.isBlank) {
      label.setText(textField.getText)
      popup.close()
    }
  }

}
