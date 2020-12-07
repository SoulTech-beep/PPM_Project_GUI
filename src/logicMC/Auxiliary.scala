package logicMC

import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.geometry.{Insets, Pos}
import javafx.scene.Node
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{HBox, Pane, Priority}
import javafx.scene.paint.Color
import javafx.scene.shape.{Circle, Line}
import javafx.scene.text.{Font, FontWeight}
import logicMC.Colors.Colors



class Auxiliary(){

}

object Colors extends Enumeration {
   type Colors = Color
   val c1: Colors = Color.WHITE
   val c2: Colors = Color.web("#ffeaa7")
   val c3: Colors = Color.web("#2d3436")

}

object Auxiliary {

   def dottedPage(width: Double, height: Double, pane: Pane, step:Int): Unit = {
      val j = (step to height.toInt - step) by step
      val i = (step to width.toInt - step) by step

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

   def verticalLines(width: Double, height: Double, pane: Pane, step:Int): Unit = {
      val i = (step to width.toInt - step) by step

      i.foreach(w => {
         pane.getChildren.add(drawLine(w,w,5, height-5))
      })
   }

   def drawLine(startX:Double, endX:Double, startY:Double, endY: Double):Line = {
      val line = new Line()

      line.setStartX(startX)
      line.setEndX(endX)

      line.setStartY(startY)
      line.setEndY(endY)

      line.setStrokeWidth(2)
      line.setFill(Color.LIGHTGRAY)
      line.setStroke(Color.LIGHTGRAY)

      line
   }

   def horizontalLine(width: Double, height: Double, pane: Pane, step:Int): Unit = {
      val j = (step to height.toInt - step) by step

      j.foreach(h => {
         pane.getChildren.add(0, drawLine(5, width-5, h, h))
      })
   }

   val shadowEffect:String = "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.15), 15, 0, 0, 0);"
   val myFont: Font = Font.font("SF Pro Display", FontWeight.BLACK, 12)

   def getFont(size:Int):Font = {
      Font.font("SF Pro Display", FontWeight.BLACK, size)
   }

   def getImageView(imageLocation: String):ImageView = {
      val imageView = new ImageView(new Image(imageLocation))
      imageView.setPreserveRatio(true)
      imageView.setSmooth(true)
      imageView.setCache(true)
      imageView.setFitHeight(50)

      imageView
   }

   def getColorPicker():(HBox, ObjectProperty[Color]) = {

      val selectedColor : ObjectProperty[Color] = new SimpleObjectProperty[Color](Colors.c1)

      val colorPicker = new HBox()

      val c1 = getCircle(Colors.c1)
      val c2 = getCircle(Colors.c2)
      val c3 = getCircle(Colors.c3)

      val buttons = List(c1,c2,c3)

      //Default selected button
      c1.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")
      c1.setStrokeWidth(1.5)
      c1.setStyle(shadowEffect)

      setOnClickColor(c1, buttons, selectedColor, Colors.c1)
      setOnClickColor(c2, buttons,selectedColor ,Colors.c2)
      setOnClickColor(c3, buttons,selectedColor, Colors.c3)

      colorPicker.setAlignment(Pos.CENTER)
      colorPicker.setPadding(new Insets(10, 0, 10, 0))

      colorPicker.getChildren.addAll(getSpacer, c1)
      colorPicker.getChildren.addAll(getSpacer, c2)
      colorPicker.getChildren.addAll(getSpacer, c3, getSpacer)

      (colorPicker, selectedColor)
   }

   def getSpacer:HBox = {
      val spacer = new HBox()
      HBox.setHgrow(spacer, Priority.ALWAYS)

      spacer
   }

   def setScaleAnimation(node:Node):Unit = {
      node.setOnMouseEntered(_ => {
         node.setScaleX(1.1)
         node.setScaleY(1.1)
      })

      node.setOnMouseExited(_ => {
         node.setScaleX(1)
         node.setScaleY(1)
      })
   }

   def setOnClickColor(circle: Circle, list: List[Circle], selected: ObjectProperty[Color], color: Color):Unit = {

      circle.setOnMouseClicked(p => {
         selected.setValue(color)
         circle.setStrokeWidth(1.5)
         circle.setStyle(shadowEffect)

         list.foreach(p => if(p != circle) {
            p.setStyle("")
            p.setStrokeWidth(1)
         })
      })
   }

   def getCircle(colors: Colors):Circle = {
      val circle = new Circle()
      circle.setFill(colors)
      circle.setRadius(15)
      circle.setStroke(Color.BLACK)
      circle.setStrokeWidth(1.0)

      setScaleAnimation(circle)

      circle
   }

   def getStyledHBox():HBox =  {
      val hBox = new HBox()

      hBox.setAlignment(Pos.CENTER)
      hBox.setPadding(new Insets(10, 0, 10, 0))

      hBox
   }

}
