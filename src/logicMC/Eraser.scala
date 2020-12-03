package logicMC

import javafx.beans.property.SimpleDoubleProperty

case class Eraser(radius: SimpleDoubleProperty) {

  def changeRadius(radius: Double): Eraser = {
    Eraser.changeRadius(this, radius)
  }

}

object Eraser {
  def changeRadius(eraser: Eraser, radius: Double): Eraser = {
    eraser.radius.set(radius)
    Eraser(radius = eraser.radius)
  }

}