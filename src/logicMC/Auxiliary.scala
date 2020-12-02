package logicMC

import javafx.beans.property.{ObjectProperty, Property, SimpleObjectProperty}
import javafx.geometry.Pos
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.HBox
import javafx.scene.paint.{Color, Paint}
import javafx.scene.shape.Circle
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
   val myFont: Font = Font.font("SF Pro Display", FontWeight.BLACK, 12)

   def getImageView(imageLocation: String):ImageView = {
      val imageView = new ImageView(new Image(imageLocation))
      imageView.setPreserveRatio(true)
      imageView.setSmooth(true)
      imageView.setCache(true)
      imageView.setFitHeight(50)

      imageView
   }

   def getColorPicker():(HBox, ObjectProperty[Paint]) = {

      val selectedColor : ObjectProperty[Paint] = new SimpleObjectProperty[Paint](Colors.c1)

      val colorPicker = new HBox()

      val c1 = getCircle(Colors.c1)
      val c2 = getCircle(Colors.c2)
      val c3 = getCircle(Colors.c3)

      val buttons = List(c1,c2,c3)

      //Default selected button
      c1.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")

      setOnClickColor(c1, buttons, selectedColor)
      setOnClickColor(c2, buttons,selectedColor)
      setOnClickColor(c3, buttons,selectedColor)

      colorPicker.setSpacing(20)
      colorPicker.setAlignment(Pos.CENTER)
      colorPicker.getChildren.addAll(c1,c2,c3)

      (colorPicker, selectedColor)
   }

   def setOnClickColor(circle: Circle, list: List[Circle], selected: ObjectProperty[Paint]):Unit = {
      circle.setOnMouseClicked(p => {
         selected.setValue(circle.getFill)
         circle.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")
         list.foreach(p => if(p != circle) p.setStyle(""))
      })
   }

   def getCircle(colors: Colors):Circle = {
      val circle = new Circle()
      circle.setFill(colors)
      circle.setRadius(10)
      circle.setStroke(Color.BLACK)


      circle
   }

}
