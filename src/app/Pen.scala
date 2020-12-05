package app

import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Color

case class Pen(id: Int, color: Color, width: SimpleDoubleProperty, opacity: SimpleDoubleProperty) extends PenTrait{

  @Override
  def changeColor(color: Color) : Pen = Pen.changeColor(this, color)

  @Override
  def changeWidth(new_width:Double):Pen = Pen.changeWidth(this, new_width)

  @Override
  def changeOpacity(new_opacity:Double):Pen = {
    /*this.opacity.set(new_opacity)
    this*/
    Pen.changeOpacity(this, new_opacity)
  }



}

object Pen{

  def changeColor(pen:Pen, color:Color):Pen = Pen(id= pen.id, color = color, width = pen.width, opacity = pen.opacity)

  def changeWidth(pen: Pen, width:Double):Pen = {
    pen.width.set(width)
    Pen(id= pen.id,color= pen.color, pen.width, opacity = pen.opacity)
  }

  def changeOpacity(pen : Pen, opacity:Double):Pen = {
    pen.opacity.set(opacity)
    Pen(id= pen.id,color = pen.color, width = pen.width, opacity = pen.opacity)
  }

}
