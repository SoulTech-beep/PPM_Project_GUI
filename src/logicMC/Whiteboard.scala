package logicMC

import javafx.geometry.Pos
import javafx.scene.control._
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import logicMC.Auxiliary.{getImageView, getPopup}
import logicMC.PageStyle.PageStyle
import logicMC.Whiteboard.Size

trait BlackBox {
  val id: Int
  val size: (Double, Double)
  val position: (Double, Double)

  def getBounds: ((Double, Double), (Double, Double)) = (size, position)

  def translate(offset: (Double, Double)): BlackBox

  def changeSize(size: (Double, Double)): BlackBox
}

object BlackBox {

  def newPosition(blackBox: BlackBox, offset:(Double, Double)):(Double, Double) = {
    (blackBox.position._1 + offset._1, blackBox.position._2 + offset._2)
  }

}

case class Whiteboard(id: Int, color: Color, size: Size, children : List[BlackBox], name: String, style:PageStyle){

  def changeName(name:String):Whiteboard = {
    Whiteboard.changeName(this, name)
  }

}

object Whiteboard{
  type Size = (Double, Double)

  def changeName(w:Whiteboard, name:String):Whiteboard={
    Whiteboard(w.id, w.color, w.size, w.children,name, w.style)
  }

  def changeColor( w : Whiteboard, new_color: Color): Whiteboard = {
    Whiteboard(w.id, new_color, w.size, w.children,w.name, w.style)
  }

  def changeSize(w: Whiteboard, new_size: Size): Whiteboard = {
    Whiteboard(w.id, w.color, new_size, w.children, w.name, w.style)
  }

  def getSelectedBlackboxes(whiteboard: Whiteboard,selector: Selector): List[BlackBox] ={
    whiteboard.children.filter(blackBox => {

      if( blackBox.getBounds._1._1 <= selector.v2._1
        && blackBox.getBounds._2._1 >= selector.v1._1
        && blackBox.getBounds._1._2 <= selector.v2._2
        && blackBox.getBounds._2._2 >= selector.v1._2){
        true
      }else {
        false
      }

    })

  }


  def getWhiteboardPane(whiteboard: Whiteboard, updateWhiteboardName: Whiteboard =>Unit):VBox = {
    val imageView = getImageView("images/book.png")

    val nameLabel = new Label(whiteboard.name)
    nameLabel.setFont(Auxiliary.myFont)
    nameLabel.setAlignment(Pos.BASELINE_CENTER)

    nameLabel.setMaxWidth(60)
    nameLabel.setMaxHeight(40)
    nameLabel.setWrapText(true)

    val vBox = new VBox(imageView, nameLabel)
    vBox.setSpacing(10)
    vBox.setAlignment(Pos.CENTER)

    val renameMenuItem = new MenuItem("Rename")
    val contextMenu = new ContextMenu(renameMenuItem)

    renameMenuItem.setOnAction(_ => {

      getPopup[Whiteboard]("Rename Whiteboard",
        "Name",
        whiteboard.name,
        ("fdcb6e","fcba03","Change Name"),
        nameLabel,
        updateWhiteboardName, whiteboard.changeName
      )

    })

    vBox.setOnContextMenuRequested( p => contextMenu.show(vBox,p.getScreenX, p.getScreenY ))

    vBox
  }


}