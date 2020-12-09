package app

import javafx.animation.{Animation, Interpolator, Transition}
import javafx.beans.property.{ObjectProperty, SimpleObjectProperty}
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.util.Duration
import javafx.geometry.Insets
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii

object Colors extends Enumeration {
  type Colors = Color
  val c1: Colors = Color.WHITE
  val c2: Colors = Color.web("#ffeaa7")
  val c3: Colors = Color.web("#2d3436")
}

class ColorStyle {

  var light:Boolean = true

  var popupBackgroundColor =new SimpleObjectProperty[Color](Color.web("#fcfcfc"))
  var popupSectionColor = new SimpleObjectProperty[Color](Color.WHITE)

  /*var popupBackgroundColor: lightAndDarkColor = (ColorStyle.getColorObject("#fcfcfc"), ColorStyle.getColorObject("#000000"))
  var popupSectionColor: lightAndDarkColor = (ColorStyle.getColorObject("#ffffff"), ColorStyle.getColorObject("#636e72"))
  var popupTextColor: lightAndDarkColor = (ColorStyle.getColorObject("#ffffff"), ColorStyle.getColorObject("#000000"))*/

  /*def changePopupBackgroundColor():Unit = {
    popupBackgroundColor.set(ColorStyle.getColorObject("#000000").get())
  }

  def changePopupSectionColor():Unit = {
    popupSectionColor.set(ColorStyle.getColorObject("636e72").get())
  }*/

  /*def changeWithAnimation(pane:Pane):Unit = {

    val animation:Animation = new Transition() {

      {
        setCycleDuration(Duration.millis(1000))
        setInterpolator(Interpolator.EASE_BOTH)
      }

      override def interpolate(v: Double): Unit = {
        val vColor = new Color(1,0,0,1-v)
        pane.setBackground(new Background(new BackgroundFill(vColor, CornerRadii.EMPTY, Insets.EMPTY)))
      }
    }

    animation.play()

  }*/

}

object ColorStyle{


  def getColorObject(string: String):SimpleObjectProperty[Color] = {
    new SimpleObjectProperty[Color](Color.web(string))
  }

}
