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

  def getShape: Option[Shape] ={

    val choice = CommandLine.prompt("1) Quadrado \t 2) Triangle \t 3) Circle").toInt
    val height = CommandLine.prompt("Height").toDouble
    val width = CommandLine.prompt("Width").toDouble

    if(choice== 1){
      Some(Shape(ShapeType.Square, 0, (width, height), (0,0)))

    }else if(choice == 2){
      Some(Shape(ShapeType.Triangle,0, (width.toInt, height.toInt),(0, 0)))

    }else if(choice==3){
      Some(Shape(ShapeType.Circle, 0, (width.toInt, height.toInt), (0, 0)))

    }else{
      None
    }

  }

  def changeShapeID(shape: Shape, id:Int): Shape = {
    Shape(shape.shapeType, id, shape.size, shape.position)
  }

}

