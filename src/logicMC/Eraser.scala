package logicMC

case class Eraser(radius: Double) {

  def changeRadius(radius: Double): Eraser = {
    Eraser(radius)
  }

}
