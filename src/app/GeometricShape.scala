package app

import app.ShapeType.ShapeType
import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Color

object ShapeType extends Enumeration {

  type ShapeType = String

  val square:String = "SQUARE"
  val circle:String = "CIRCLE"
  val polygon:String = "POLYGON"
  val line:String = "LINE"

}

case class GeometricShape(id: Int, color: Color, width: SimpleDoubleProperty, opacity: SimpleDoubleProperty, shape: ShapeType) extends PenTrait {

  def changeShape(st: ShapeType):GeometricShape = GeometricShape.changeShape(this, st)

  @Override
  def changeColor(color: Color) : GeometricShape= GeometricShape.changeColor(this, color)

  @Override
  def changeWidth(new_width:Double):GeometricShape= GeometricShape.changeWidth(this, new_width)

  @Override
  def changeOpacity(new_opacity:Double):GeometricShape= {
    /*this.opacity.set(new_opacity)
    this*/
    GeometricShape.changeOpacity(this, new_opacity)
  }

}


object GeometricShape {

  def changeColor(pen:GeometricShape, color:Color):GeometricShape= GeometricShape(id= pen.id, color = color, width = pen.width, opacity = pen.opacity, pen.shape)

  def changeWidth(pen: GeometricShape, width:Double):GeometricShape= {
    pen.width.set(width)
    GeometricShape(id= pen.id,color= pen.color, pen.width, opacity = pen.opacity, pen.shape)
  }

  def changeOpacity(pen: GeometricShape, opacity:Double):GeometricShape= {
    pen.opacity.set(opacity)
    GeometricShape(id= pen.id,color = pen.color, width = pen.width, opacity = pen.opacity, pen.shape)
  }


  def changeShape(pen: GeometricShape, st: ShapeType):GeometricShape = {
    GeometricShape(id= pen.id, color = pen.color, width = pen.width, opacity = pen.opacity, shape = st)
  }




}
