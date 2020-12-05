package app

import app.ShapeType.ShapeType
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Color

object ShapeType extends Enumeration {

  type ShapeType = String

  val square:String = "SQUARE"
  val circle:String = "CIRCLE"

}

case class GeometricShape(override val id: Int, override val color: Color, override val width: SimpleDoubleProperty, override val opacity: SimpleDoubleProperty, shape: ShapeType) extends Pen(id,color,width,opacity){

  def changeShape(st: ShapeType):GeometricShape = GeometricShape.changeShape(this, st)

}


object GeometricShape {

  def changeShape(pen: Pen, st: ShapeType):GeometricShape = {
    GeometricShape(id= pen.id, color = pen.color, width = pen.width, opacity = pen.opacity, shape = st)
  }




}