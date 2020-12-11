package app

import javafx.beans.property.{ObjectProperty, SimpleDoubleProperty}
import javafx.scene.paint.Color
import logicMC.ShapeType.ShapeType


case class GeometricShape(id: Int, strokeColor: ObjectProperty[Color], strokeWidth: SimpleDoubleProperty, opacity: SimpleDoubleProperty, shape: ShapeType, fillColor: ObjectProperty[Color]) extends PenTrait {

  def changeShape(shapeType: ShapeType): GeometricShape = GeometricShape.changeShape(this, shapeType)

  @Override
  def changeColor(color: Color): GeometricShape = GeometricShape.changeStrokeColor(this, color)

  @Override
  def changeWidth(newWidth: Double): GeometricShape = GeometricShape.changeStrokeWidth(this, newWidth)

  @Override
  def changeOpacity(newOpacity: Double): GeometricShape = {
    GeometricShape.changeOpacity(this, newOpacity)
  }

  def changeFillColor(newFillColor: Color): GeometricShape = {
    GeometricShape.changeFillColor(this, newFillColor)
  }

}


object GeometricShape {

  def changeStrokeColor(pen: GeometricShape, strokeColor: Color): GeometricShape = {
    pen.strokeColor.set(strokeColor)

    getCopy(pen)
  }

  def getCopy(geometricShape: GeometricShape): GeometricShape = {

    GeometricShape(id = geometricShape.id,
      strokeColor = geometricShape.strokeColor,
      strokeWidth = geometricShape.strokeWidth,
      opacity = geometricShape.opacity,
      shape = geometricShape.shape,
      fillColor = geometricShape.fillColor)
  }

  def changeStrokeWidth(pen: GeometricShape, strokeWidth: Double): GeometricShape = {
    pen.strokeWidth.set(strokeWidth)

    getCopy(pen)
  }

  def changeOpacity(pen: GeometricShape, opacity: Double): GeometricShape = {
    pen.opacity.set(opacity)

    getCopy(pen)
  }

  def changeShape(pen: GeometricShape, shapeType: ShapeType): GeometricShape = {
    GeometricShape(id = pen.id, strokeColor = pen.strokeColor, strokeWidth = pen.strokeWidth, opacity = pen.opacity, shapeType, fillColor = pen.fillColor)
  }

  def changeFillColor(pen: GeometricShape, fillColor: Color): GeometricShape = {
    pen.fillColor.set(fillColor)

    getCopy(pen)
  }

}
