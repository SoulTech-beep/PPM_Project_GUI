package logicMC

import app.PageStyle.PageStyle
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.{Modality, Stage}
import logicMC.Auxiliary.getImageView
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




  def getWhiteboardPane(whiteboard: Whiteboard, updateWhiteboardName: Whiteboard =>Unit):VBox = {
    val imageView = getImageView("images/book.png")

    val nameLabel = new Label(whiteboard.name)
    nameLabel.setFont(Auxiliary.myFont)
    nameLabel.setAlignment(Pos.BASELINE_CENTER)
    //TODO FIX THE ICON OFFSET THINGY THAT IS FUCKING THIS SHIT UP, FACK.
    nameLabel.setMaxWidth(60)
    nameLabel.setMaxHeight(40)
    nameLabel.setWrapText(true)

    val vBox = new VBox(imageView, nameLabel)
    vBox.setSpacing(10)
    vBox.setAlignment(Pos.CENTER)

    val renameMenuItem = new MenuItem("Rename")
    val contextMenu = new ContextMenu(renameMenuItem)

    renameMenuItem.setOnAction(_ => {

      val popupStage = new Stage()
      popupStage.setTitle("Rename Whiteboard")
      popupStage.initModality(Modality.APPLICATION_MODAL)

      val label = new Label("Name")
      label.setFont(Auxiliary.getFont(14))
      label.setPadding(new Insets(5,0,0,5))

      val nameTextField = new TextField(whiteboard.name)
      nameTextField.setFont(Auxiliary.getFontWeight(14, FontWeight.LIGHT))
      nameTextField.setPromptText("New name")
      nameTextField.selectAll()

      VBox.setMargin(nameTextField, new Insets(10, 10, 10, 10))

      val vBoxTextField = new VBox(label)
      vBoxTextField.getChildren.add(nameTextField)
      vBoxTextField.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBoxTextField.setPadding(new Insets(5, 5, 5, 5))
      VBox.setMargin(vBoxTextField, new Insets(10,10,0,10))

      val okButton = Auxiliary.getButtonWithColor("fdcb6e","fcba03","Change Name")

      val innervBox = new VBox(vBoxTextField, okButton)
      innervBox.setStyle("-fx-background-color: white;")

      innervBox.setSpacing(20)
      innervBox.setAlignment(Pos.CENTER)
      innervBox.setPadding(new Insets(10,10,10,10))

      val scene = new Scene(innervBox)
      scene.getStylesheets.add("testStyle.css")
      popupStage.setTitle("Change name")
      popupStage.getIcons.add(new Image("images/renameIcon.png"))
      popupStage.setWidth(400)
      popupStage.setResizable(false)

      popupStage.setScene(scene)
      popupStage.show()

      nameTextField.setOnKeyPressed(p => {
        if(p.getCode == KeyCode.ENTER) {
          checkTextFieldAndChange(nameTextField, nameLabel, popupStage)
          updateWhiteboardName(whiteboard.changeName(nameTextField.getText))
        }
      })

      okButton.setOnMouseClicked(_ => {
        checkTextFieldAndChange(nameTextField, nameLabel, popupStage)
        updateWhiteboardName(whiteboard.changeName(nameTextField.getText))
      })

    })

    vBox.setOnContextMenuRequested( p => contextMenu.show(vBox,p.getScreenX, p.getScreenY ))

    vBox
  }

  def checkTextFieldAndChange(textField:TextField, label:Label, popup: Stage):Unit = {
    if(!textField.getText.isBlank){
      label.setText(textField.getText)
      popup.close()
    }
  }


}