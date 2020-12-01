import javafx.geometry.{Insets, Pos}
import javafx.scene.control.ScrollPane
import javafx.scene.image.{Image, ImageView}
import javafx.scene.layout.{Background, BackgroundFill, CornerRadii, Pane, VBox}
import javafx.scene.paint.Color

class whiteboardScroller {



}

object whiteboardScroller{
  def getCanvas():ScrollPane = {
    val canvas = new ScrollPane()

    val pages = new VBox()

    val page1 = new Pane()
    val page2 = new Pane()

    page1.setBackground(new Background(new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY)));
    page2.setBackground(new Background(new BackgroundFill(Color.web("#2d3436"), CornerRadii.EMPTY, Insets.EMPTY)));

    page1.setPrefSize(1200, 800)
    page2.setPrefSize(1200, 800)

    pages.getChildren.addAll(page1,page2)
    pages.setSpacing(80)
    pages.setAlignment(Pos.CENTER)

    canvas.setContent(pages)


    canvas
  }
}
