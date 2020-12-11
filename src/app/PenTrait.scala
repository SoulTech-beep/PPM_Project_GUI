package app

import javafx.scene.paint.Color

trait PenTrait {

  def changeColor(color: Color) : PenTrait
  def changeOpacity(newOpacity:Double) : PenTrait
  def changeWidth(newWidth:Double) : PenTrait

}
