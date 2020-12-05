package logicMC

import logicMC.ShapeType.ShapeType

case class Shape(shapeType: ShapeType, id : Int, size: (Double,Double), position: (Double,Double)) extends BlackBox {


  override def translate(offset: (Double, Double)): Shape = Shape.translate(this, offset)


  override def changeSize(size: (Double, Double)): Shape = Shape.changeSize(this, size)
}

object Shape {

  def changeSize(shape:Shape, size:(Double, Double)):Shape = {
    Shape(shape.shapeType, shape.id, size, shape.position)
  }

  def translate(shape: Shape, offset:(Double, Double)):Shape = {
    val newPosition = BlackBox.newPosition(shape, offset)
    val newSize = (shape.size._1 + offset._1, shape.size._2 + offset._2)

    Shape(shape.shapeType, shape.id, newSize, newPosition)
  }


  def changeShapeID(shape: Shape, id:Int): Shape = {
    Shape(shape.shapeType, id, shape.size, shape.position)
  }

}

