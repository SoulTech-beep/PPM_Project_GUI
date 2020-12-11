package app

import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.geometry.Insets
import javafx.scene.control.{Button, Label, ToggleButton, ToggleGroup}
import javafx.scene.layout._
import javafx.scene.paint.Color
import javafx.stage.{Stage, WindowEvent}
import logicMC.Auxiliary.{getSpacer, getStyledHBox}
import logicMC.PageSize.PageSize
import logicMC.PageStyle.PageStyle
import logicMC.{Auxiliary, Colors, PageSize, PageStyle}

class PagePicker {

  var buttonClicked = false

  var colorVBox: VBox = new VBox()
  var sizeVBox: VBox = new VBox()
  var pageVBox: VBox = new VBox()

  var selectedSizeGroup: ToggleGroup = new ToggleGroup()
  var selectedSize: PageSize = PageSize.A4

  var selectedStyle: PageStyle = PageStyle.SIMPLE
  var selectedStyleButtons: List[Pane] = List()

  var selectedColor: ObjectProperty[Color] = new SimpleObjectProperty[Color](Colors.c1)

  def wasClicked:Boolean = {
    buttonClicked
  }

  def getPage:(Color, PageSize, PageStyle) = {
    (selectedColor.get(), selectedSize, selectedStyle)
  }

  def initialize(): VBox = {
    val pageStyleLabel = Auxiliary.setUpPopupLabel("Page Style")
    val pageColorLabel = Auxiliary.setUpPopupLabel("Page Color")
    val pageSizeLabel = Auxiliary.setUpPopupLabel("Page Size")

    colorVBox = Auxiliary.setUpPopupSection(pageColorLabel, getColorPicker)()
    sizeVBox = Auxiliary.setUpPopupSection(pageSizeLabel, PagePicker.getSizePicker(setSize))()
    pageVBox = Auxiliary.setUpPopupSection(pageStyleLabel, getStylePicker)()

    colorVBox.setPrefWidth(286)

    val novaMainVBox = Auxiliary.setUpPopupSection(pageVBox, colorVBox, sizeVBox, setCreateButton())("#fcfcfc")

    novaMainVBox
  }

  def getColorPicker: HBox = {
    val colorPicker = Auxiliary.getColorPicker

    selectedColor = colorPicker._2

    colorPicker._1
  }

  def setStyleToggle(style: PageStyle, function: (Double, Double, Pane, Int, Double) => Unit, setDefault: Boolean = false): Pane = {
    val pane = new Pane()

    selectedStyleButtons = pane :: selectedStyleButtons

    pane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)))
    resetBorder(pane)

    pane.setPrefSize(50, 50)
    function(50, 50, pane, 10, 1)

    def select(): Unit = {
      selectedStyle = style
      pane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), new BorderWidths(1.5))))
      pane.setStyle(Auxiliary.getShadow())
    }

    def resetBorder(paneToReset: Pane): Unit = {
      paneToReset.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)))

    }

    if (setDefault) {
      select()
    }

    pane.setOnMouseClicked(_ => {
      select()

      selectedStyleButtons.foreach(f => {
        if (f != pane) {
          resetBorder(f)
          f.setStyle("")
        }
      })
    })

    Auxiliary.setScaleAnimation(pane)

    pane
  }


  def getStylePicker: HBox = {
    val hBox = getStyledHBox

    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SIMPLE, (_: Double, _: Double, _: Pane, _:Int, _:Double) => (), setDefault = true), getSpacer, setStyleToggle(PageStyle.DOTTED, Auxiliary.dottedPage))
    hBox.getChildren.addAll(getSpacer, setStyleToggle(PageStyle.SQUARED, Auxiliary.squaredPage), getSpacer)
    hBox.getChildren.addAll(setStyleToggle(PageStyle.LINED, Auxiliary.horizontalLine), getSpacer)

    hBox
  }

  def setSize(text: String, pageSize: PageSize, setDefault: Boolean = false): ToggleButton = {
    val toggleButton = PagePicker.getSizeToggleButton(text, selectedSizeGroup, setDefault)

    toggleButton.setOnAction(_ => {
      selectedSize = pageSize
    })

    toggleButton
  }


  def setCreateButton(): Button = {
    val button = Auxiliary.getButtonWithColor(name = "Create")

    button.setOnAction(_ => {
      buttonClicked = true
      Auxiliary.fireCloseRequestStage(button)
    })

    button
  }


}

object PagePicker {

  def getSizePicker(setSize:(String, PageSize, Boolean)=>ToggleButton): HBox = {
    val hBox = getStyledHBox

    hBox.getChildren.addAll(getSpacer, setSize("A4", PageSize.A4,true))
    hBox.getChildren.addAll(getSpacer, setSize("A3", PageSize.A3, false), getSpacer)

    hBox
  }

  def getSizeToggleButton(text:String , selectedSizeGroup: ToggleGroup, setDefault:Boolean = false): ToggleButton = {
    val toggleButton = new ToggleButton(text)
    toggleButton.setPadding(new Insets(5, 20, 5, 20))
    toggleButton.setToggleGroup(selectedSizeGroup)
    toggleButton.setSelected(setDefault)

    toggleButton.getStyleClass.add("start-stop")
    toggleButton.setStyle("-fx-background-radius: 25px;")

    toggleButton
  }

}
