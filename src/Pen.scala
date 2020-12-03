import javafx.beans.property.SimpleDoubleProperty
import javafx.scene.paint.Color

case class Pen(id: Int, color: Color, width: SimpleDoubleProperty, opacity: SimpleDoubleProperty){

  def changeColor(color: Color) : Pen = Pen.changeColor(this, color)

  def changeWidth(new_width:Double):Unit = {
    this.width.set(new_width)
  }

  def changeOpacity(new_opacity:Double):Unit = {
    this.opacity.set(new_opacity)
  }



}

object Pen{

  def changeColor(pen:Pen, color:Color):Pen = Pen(id= pen.id, color = color, width = pen.width, opacity = pen.opacity)
  //def changeWidth(pen: Pen, width:SimpleDoubleProperty):Pen = Pen(id= pen.id,color= pen.color, width = width, opacity = pen.opacity)
  //def changeOpacity(pen : Pen, opacity:SimpleDoubleProperty):Pen = Pen(id= pen.id,color = pen.color, width = pen.width, opacity = opacity)

}
