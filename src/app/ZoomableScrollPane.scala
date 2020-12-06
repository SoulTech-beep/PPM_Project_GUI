package app

import javafx.event.EventHandler
import javafx.geometry.{Point2D, Pos}
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.input.{KeyCode, ZoomEvent}
import javafx.scene.layout.VBox
import javafx.scene.{Group, Node}


class ZoomableScrollPane extends ScrollPane{

  var scaleValue = 0.7
  val zoomIntensity = 0.1
  var target :  Node = null
  var zoomNode :  Node = null


  def teste(tar:Node):Unit = {

    tar.setOnZoom(new EventHandler[ZoomEvent]() {
      override def handle(event: ZoomEvent): Unit = {
        tar.setScaleX(tar.getScaleX * event.getZoomFactor)
        tar.setScaleY(tar.getScaleY * event.getZoomFactor)
        event.consume()
      }
    })

  }

  def init(tar: Node):Unit = {

    target = tar
    zoomNode = new Group(target)
    setContent(outerNode(zoomNode))

    val osName = System.getProperty("os.name").toLowerCase
    val isMacOs = osName.startsWith("mac os x")
    if (isMacOs) {
      teste(target)
    }

    setOnKeyPressed(e => {
      if(e.getCode == KeyCode.CONTROL) {
        setPannable(true)
      }
    })

    setOnKeyReleased(e => {
      if(e.getCode == KeyCode.CONTROL) {
        setPannable(false)
      }
    })

    setHbarPolicy(ScrollBarPolicy.AS_NEEDED)
    setVbarPolicy(ScrollBarPolicy.AS_NEEDED)
    setFitToHeight(true) //center

    setFitToWidth(true)

    updateScale();
  }

  private def outerNode(node: Node):VBox = {
    val outerNode = centeredNode(node)
    outerNode.setOnScroll((e) => {
      if (e.isControlDown) {
        e.consume()
        val point2D: Point2D = new Point2D(e.getX, e.getY)
        onScroll(e.getTextDeltaY, point2D)
      }
      })
    outerNode
  }

  private def centeredNode(node: Node): VBox = {
    val vBox = new VBox(node)
    vBox.setAlignment(Pos.CENTER)
    vBox
  }

  private def onScroll(wheelDelta: Double, mousePoint: Point2D): Unit = {
    val zoomFactor = Math.exp(wheelDelta * zoomIntensity)
    val innerBounds = zoomNode.getLayoutBounds
    val viewportBounds = getViewportBounds
    // calculate pixel offsets from [0, 1] range
    val valX = getHvalue * (innerBounds.getWidth - viewportBounds.getWidth)
    val valY = getVvalue * (innerBounds.getHeight - viewportBounds.getHeight)
    scaleValue = scaleValue * zoomFactor
    updateScale()
    layout() // refresh ScrollPane scroll positions & target bounds

    // convert target coordinates to zoomTarget coordinates
    val posInZoomTarget = target.parentToLocal(zoomNode.parentToLocal(mousePoint))
    // calculate adjustment of scroll position (pixels)
    val adjustment = target.getLocalToParentTransform.deltaTransform(posInZoomTarget.multiply(zoomFactor - 1))
    // convert back to [0, 1] range
    // (too large/small values are automatically corrected by ScrollPane)
    val updatedInnerBounds = zoomNode.getBoundsInLocal
    setHvalue((valX + adjustment.getX) / (updatedInnerBounds.getWidth - viewportBounds.getWidth))
    setVvalue((valY + adjustment.getY) / (updatedInnerBounds.getHeight - viewportBounds.getHeight))
  }


  private def updateScale(): Unit = {
    target.setScaleX(scaleValue)
    target.setScaleY(scaleValue)
  }

}
