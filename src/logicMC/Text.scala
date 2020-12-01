package logicMC

case class Text(text:String, id:Int, size:(Double, Double), position:(Double, Double)) extends BlackBox {

  override def translate(offset: (Double, Double)): BlackBox = Text.translate(this, offset)

  override def changeSize(size: (Double, Double)): BlackBox = Text.changeSize(this, size)
}

object Text {

  def changeSize(text:Text, size:(Double, Double)):Text = {
    Text(text.text, text.id, size, text.position)
  }

  def translate(text: Text, offset: (Double, Double)):Text = {
    val newPosition = BlackBox.newPosition(text, offset)
    val newSize = (text.size._1 + offset._1, text.size._2 + offset._2)

    Text(text.text, text.id, newSize, newPosition)
  }

}