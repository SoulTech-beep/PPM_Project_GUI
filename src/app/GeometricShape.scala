package app

import app.ShapeType.ShapeType
import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty}
import javafx.scene.paint.Color

object ShapeType extends Enumeration {

  type ShapeType = String

  val square:String = "SQUARE"
  val circle:String = "CIRCLE"
  val polygon:String = "POLYGON"
  val line:String = "LINE"

}

case class GeometricShape(id: Int, strokeColor: ObjectProperty[Color], strokeWidth: SimpleDoubleProperty, opacity: SimpleDoubleProperty, shape: ShapeType, fillColor: ObjectProperty[Color]) extends PenTrait {

  def changeShape(st: ShapeType):GeometricShape = GeometricShape.changeShape(this, st)

  @Override
  def changeColor(color: Color) : GeometricShape= GeometricShape.changeStrokeColor(this, color)

  @Override
  def changeWidth(new_width:Double):GeometricShape= GeometricShape.changeStrokeWidth(this, new_width)

  @Override
  def changeOpacity(new_opacity:Double):GeometricShape= {
    GeometricShape.changeOpacity(this, new_opacity)
  }

  def changeFillColor(new_fillColor: Color):GeometricShape = {
    GeometricShape.changeFillColor(this, new_fillColor)
  }

}


object GeometricShape {

  def changeStrokeColor(pen:GeometricShape, strokeColor:Color):GeometricShape= {
    pen.strokeColor.set(strokeColor)
    GeometricShape(id= pen.id, strokeColor = pen.strokeColor, strokeWidth = pen.strokeWidth, opacity = pen.opacity, shape = pen.shape, fillColor = pen.fillColor)
  }

  def changeStrokeWidth(pen: GeometricShape, strokeWidth:Double):GeometricShape= {
    pen.strokeWidth.set(strokeWidth)
    GeometricShape(id= pen.id,strokeColor= pen.strokeColor, pen.strokeWidth, opacity = pen.opacity, pen.shape, fillColor = pen.fillColor)
  }

  def changeOpacity(pen: GeometricShape, opacity:Double):GeometricShape= {
    pen.opacity.set(opacity)
    GeometricShape(id= pen.id,strokeColor = pen.strokeColor, strokeWidth = pen.strokeWidth, opacity = pen.opacity, pen.shape, fillColor = pen.fillColor)
  }


  def changeShape(pen: GeometricShape, shapeType: ShapeType):GeometricShape = {
    GeometricShape(id= pen.id, strokeColor = pen.strokeColor, strokeWidth = pen.strokeWidth, opacity = pen.opacity, shape = shapeType, fillColor = pen.fillColor)
  }

  def changeFillColor(pen:GeometricShape, fillColor: Color) = {
    pen.fillColor.set(fillColor)
    GeometricShape(id = pen.id, strokeColor = pen.strokeColor, strokeWidth = pen.strokeWidth, opacity = pen.opacity,shape = pen.shape, fillColor = pen.fillColor)
  }

}
