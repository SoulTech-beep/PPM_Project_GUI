package app

import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty}
import javafx.scene.paint.Color

case class Pen(id: Int, color: ObjectProperty[Color], width: SimpleDoubleProperty, opacity: SimpleDoubleProperty) extends PenTrait {

  @Override
  def changeColor(color: Color): Pen = Pen.changeColor(this, color)

  @Override
  def changeWidth(new_width: Double): Pen = Pen.changeWidth(this, new_width)

  @Override
  def changeOpacity(new_opacity: Double): Pen = {

    Pen.changeOpacity(this, new_opacity)
  }

}

object Pen {

  def changeColor(pen: Pen, color: Color): Pen = {
    pen.color.set(color)
    getCopy(pen)
  }

  def changeWidth(pen: Pen, width: Double): Pen = {
    pen.width.set(width)
    getCopy(pen)
  }

  def changeOpacity(pen: Pen, opacity: Double): Pen = {
    pen.opacity.set(opacity)
    getCopy(pen)
  }

  def getCopy(pen: Pen):Pen = {
    Pen(pen.id, pen.color, pen.width, pen.opacity)
  }

}
