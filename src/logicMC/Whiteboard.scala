package logicMC

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

case class Whiteboard(id: Int, color: String, size: Size, children : List[BlackBox], name: String){

  def changeName(name:String):Whiteboard = {
    Whiteboard.changeName(this, name)
  }

}

object Whiteboard{
  type Size = (Double, Double)

  def changeName(w:Whiteboard, name:String):Whiteboard={
    Whiteboard(w.id, w.color, w.size, w.children,name)
  }

  def changeColor( w : Whiteboard, new_color: String): Whiteboard = {
    Whiteboard(w.id, new_color, w.size, w.children,w.name)
  }

  def changeSize(w: Whiteboard, new_size: Size): Whiteboard = {
    Whiteboard(w.id, w.color, new_size, w.children, w.name)
    //TODO Update hidden elements.
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

  def translateBlackBox(whiteboard: Whiteboard):Whiteboard = {
    val translateX = CommandLine.prompt("Offset quantity for X").toDouble
    val translateY = CommandLine.prompt("Offset quantity for Y").toDouble

    val blackBoxID = CommandLine.prompt("Blackbox to select").toInt

    val selectedBlackBox = whiteboard.children.find(p => p.id == blackBoxID)
    val blackBoxIndex = whiteboard.children.indexWhere(p => p.id == blackBoxID)

    val updatedBlackBox = selectedBlackBox.get.translate(translateX, translateY)

    Whiteboard(whiteboard.id, whiteboard.color, whiteboard.size, whiteboard.children.updated(blackBoxIndex, updatedBlackBox), whiteboard.name)
  }

  def removeBlackBox(whiteboard: Whiteboard):Whiteboard = {
    val blackBoxID = CommandLine.prompt("Blackbox to remove").toInt

    Whiteboard(whiteboard.id, whiteboard.color, whiteboard.size, whiteboard.children.filter(p => p.id != blackBoxID), whiteboard.name)
  }

  def changeBlackBoxSize(whiteboard: Whiteboard):Whiteboard = {
    val sizeX = CommandLine.prompt("Which width do you want to set").toDouble
    val sizeY = CommandLine.prompt("Which height do you want to set").toDouble

    val blackBoxID = CommandLine.prompt("Blackbox to change size").toInt

    val blackBoxIndex = whiteboard.children.indexWhere(p => p.id == blackBoxID)
    val selectedBlackBox = whiteboard.children.find(p => p.id == blackBoxID)

    val updatedBlackBox = selectedBlackBox.get.changeSize(sizeX, sizeY)

    Whiteboard(whiteboard.id, whiteboard.color, whiteboard.size, whiteboard.children.updated(blackBoxIndex, updatedBlackBox), whiteboard.name)
  }

  def addShape(w : Whiteboard): Whiteboard = {

    val idNum = ( w.children foldRight 0) ((a,b) => if(a.id > b) a.id else b)
    val newChildren = Shape.getShape

    if(newChildren.isDefined){
      Whiteboard(w.id, w.color, w.size, Shape.changeShapeID(newChildren.get, idNum) :: w.children, w.name)
    }else{
      w
    }

  }

}