package logicMC

case class Pen(color: String, width: Double, opacity: Double){

  def changeColor(color: String) : Pen = Pen.changeColor(this, color)
  def changeWidth(width:Double):Pen = Pen.changeWidth(this, width)
  def changeOpacity(opacity: Double):Pen = Pen.changeOpacity(this, opacity)
}

object Pen{

  def changeColor(pen:Pen, color:String):Pen = Pen(color = color, width = pen.width, opacity = pen.opacity)
  def changeWidth(pen: Pen, width:Double):Pen = Pen(color= pen.color, width = width, opacity = pen.opacity)
  def changeOpacity(pen : Pen, opacity:Double):Pen = Pen(color = pen.color, width = pen.width, opacity = opacity)

}
