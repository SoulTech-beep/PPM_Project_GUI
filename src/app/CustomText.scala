package app

import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight

case class CustomText(textColor: ObjectProperty[Color], textWeight: ObjectProperty[FontWeight], textSize:SimpleIntegerProperty, opacity:SimpleDoubleProperty){

  def changeTextColor(color: Color):Unit = {
    this.textColor.set(color)
  }

  def changeTextWeight(fontWeight: FontWeight):Unit = {
    this.textWeight.set(fontWeight)
  }

  def changeTextSize(newSize: Int):Unit = {
    this.textSize.set(newSize)
  }

  def changeOpacity(newOpacity: Double):Unit = {
    this.opacity.set(newOpacity)
  }

}
