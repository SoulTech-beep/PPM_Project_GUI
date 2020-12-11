package app

import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty, SimpleIntegerProperty}
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight

case class CustomText(textColor: ObjectProperty[Color], textWeight: ObjectProperty[FontWeight], textSize:SimpleIntegerProperty, opacity:SimpleDoubleProperty) {

  def changeTextWeight(fontWeight: FontWeight):CustomText = {
    CustomText.changeTextWeight(this, fontWeight)
  }

  def changeTextSize(newSize: Double):CustomText = {
    CustomText.changeTextSize(this, newSize.toInt)
  }

  def changeOpacity(newOpacity: Double):CustomText = {
    CustomText.changeOpacity(this, newOpacity)
  }

  def changeColor(color: Color): CustomText = {
    CustomText.changeColor(this, color)
  }

}

object CustomText{

  def changeColor(customText: CustomText, color:Color):CustomText = {
    customText.textColor.set(color)

    getCopy(customText)
  }

  def changeTextWeight(customText: CustomText, fontWeight: FontWeight):CustomText = {
    customText.textWeight.set(fontWeight)

    getCopy(customText)
  }

  def changeTextSize(customText: CustomText, size: Int):CustomText = {
    customText.textSize.set(size)

    getCopy(customText)
  }

  def changeOpacity(customText: CustomText, opacity: Double):CustomText = {
    customText.opacity.set(opacity)

    getCopy(customText)
  }

  def getCopy(customText: CustomText):CustomText = {
    CustomText(customText.textColor, customText.textWeight, customText.textSize, customText.opacity)
  }

}

