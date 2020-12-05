package app

import javafx.scene.paint.Color

trait PenTrait {

  def changeColor(color: Color) : PenTrait
  def changeOpacity(new_width:Double) : PenTrait
  def changeWidth(new_opacity:Double) : PenTrait

}
