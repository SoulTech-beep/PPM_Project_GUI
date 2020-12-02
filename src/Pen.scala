import javafx.scene.paint.Color

case class Pen(id: Int, color: Color, width: Double, opacity: Double){

  def changeColor(color: Color) : Pen = Pen.changeColor(this, color)
  def changeWidth(width:Double):Pen = Pen.changeWidth(this, width)
  def changeOpacity(opacity: Double):Pen = Pen.changeOpacity(this, opacity)
}

object Pen{

  def changeColor(pen:Pen, color:Color):Pen = Pen(id= pen.id, color = color, width = pen.width, opacity = pen.opacity)
  def changeWidth(pen: Pen, width:Double):Pen = Pen(id= pen.id,color= pen.color, width = width, opacity = pen.opacity)
  def changeOpacity(pen : Pen, opacity:Double):Pen = Pen(id= pen.id,color = pen.color, width = pen.width, opacity = opacity)

}
