package logicMC

import app.PageStyle.PageStyle
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control.{ContextMenu, Label, MenuItem, TextField}
import javafx.scene.image.Image
import javafx.scene.input.{KeyCode, MouseButton}
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.{Modality, Stage}
import logicMC.Auxiliary.{getImageView, setOnClickColor}
import logicMC.Section.{ID, Name}
import logicMC.Whiteboard.checkTextFieldAndChange

import scala.annotation.tailrec

case class Section(id: ID, name: Name, sections: List[Section], whiteboards: List[Whiteboard]){

  def changeName(name:String):Section = {
    Section.changeName(this, name)
  }

}

object Section{

  type Size = (Double, Double)
  type ID = String
  type Name = String

  def changeName(s:Section, name:String):Section = {
    Section(s.id, name, s.sections, s.whiteboards)
  }

  def describe(mainSection: Section, section: Section):(Section, Section) = {
    println("---[ Sections Description ]---")
    describeAuxiliary(mainSection, 0)
    (mainSection, section)
  }

  def describeAuxiliary(section : Section, mul : Int):Unit = {
    println(tabMulti(mul)("\t") + "logicMC.Section id: " + section.id + " , name: " + section.name)
    describeWhiteboards(section, mul)
    println()

    section.sections.foreach(p => {
      describeAuxiliary(p, mul+1)
    })


  }

  def describeWhiteboards(s: Section, mul:Int):Unit = {
    if(s.whiteboards.nonEmpty){
      s.whiteboards.foreach(w => println(tabMulti(mul)("\t") + w ))
    }
  }

  def tabMulti(mul: Int)(str:String = "\t"):String = mul match {
    case 0 => str
    case _ => str + tabMulti(mul-1)(str)
  }


  def addNewSectionName(mainSection :Section, s:Section, name:String):(Section, Section) = {
    if(s.sections.isEmpty){
      val sectionCreated = Section(s.id+".1",name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, sectionCreated::s.sections, s.whiteboards)

      updateAll(mainSection, sectionWeAreIn)
      //(mainSection, sectionWeAreIn)
    }else{
      val maxID = s.sections.last.id.split('.').last.toInt + 1
      val newID : String = s.id + '.' + maxID.toString

      val sectionCreated = Section(newID,name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, (sectionCreated::s.sections).reverse, s.whiteboards)

      //(mainSection, sectionWeAreIn)
      updateAll(mainSection, sectionWeAreIn)
    }

    //TODO If some problem exists check here!
  }

  def updateAll(mainSection: Section, s:Section):(Section, Section) = {

    if(s.id.length == 1){
      return (Section(s.id, s.name, s.sections, s.whiteboards),s)
    }

    val newMainSections: List[Section] = updateAllAuxiliary(mainSection.sections, s)
    val newMainSection = Section(mainSection.id, mainSection.name, newMainSections, mainSection.whiteboards)

    (newMainSection, s)
  }

  def updateAllAuxiliary(list : List[Section], updatedSection:Section): List[Section] = list match {
    case Nil => updatedSection.sections

    case head::tail =>
        if(updatedSection.id.length == 1){
          //estávamos na secção original e como tal é só devolver o updatedSection.sections (que foi atualizado algures no método onde fizemos algo)
          return updatedSection.sections
        }
        if(head.id == updatedSection.id.substring(0, head.id.length) ) {
          //é um pai do henrique ou então é o próprio henrique
              if( head.id.length == updatedSection.id.length){
                //é o próprio henrique
                updatedSection::tail
              }else {
                logicMC.Section(head.id, head.name, updateAllAuxiliary(head.sections, updatedSection), head.whiteboards)::tail
              //é um pai  mas nao é aquele que queremos atualizar, é um pai do henrique
              }

        }else{
          head::updateAllAuxiliary(tail, updatedSection)
        }
  }


  def enterSectionID(mainSection : Section, section: Section, sectionIDtoEnter:Int):(Section, Section) = {

    val sectionToEnter = section.sections.indexWhere( p => p.id.split('.').last.toInt == sectionIDtoEnter.toInt)

    if(sectionToEnter == -1){
      (mainSection, section)
    }else{
      (mainSection, section.sections.apply(sectionToEnter))
    }

  }


  def exitSection(mainSection: Section, section: Section):(Section, Section) = {

    if(section.id.length == 1){
      //se somos a raiz
      return (mainSection, section)
    }

    val topID = section.id.substring(0, section.id.lastIndexOf('.'))
    val newSection = auxiliaryExit(List(mainSection), topID)
    (mainSection, newSection)
  }

  @tailrec
  def auxiliaryExit(list: List[Section], id:String):Section = list match{
    case head::tail =>
      if(head.id == id.substring(0, head.id.length) ) {
        //é um pai do henrique ou então é o próprio henrique
        if( head.id.length == id.length){
          //é o próprio henrique
          head
        }else {
          auxiliaryExit(head.sections, id)
          //é um pai  mas nao é aquele que queremos atualizar, é um pai do henrique
        }
      }else{
        auxiliaryExit(tail, id)
      }
  }

  def describeCurrentSection(mainSection: Section, section: Section):(Section, Section)={
    println(section)
    (mainSection, section)
  }

  def goToMainMenu(mainSection: Section):(Section, Section) = {
    (mainSection, mainSection)
  }


  def addWhiteboardWithValues(mainSection : Section, section: Section, color:Color, sizeX:Double, sizeY:Double, name:String, style:PageStyle):(Section, Section) = {

    val id = ( section.whiteboards foldRight 0) ((a,b) => if(a.id > b) a.id else b)
    val new_wb = Whiteboard(id+1,color,(sizeX.toDouble, sizeY.toDouble),List(), name, style)

    val newSection = logicMC.Section(section.id, section.name, section.sections, new_wb::section.whiteboards)
    updateAll(mainSection, newSection)
    //TODO we gotta check if the update is working m8!
  }


  //AINDA DOS ANTIGOS:



  def auxiliaryRemoveSection(list : List[Section], sectionToRemove: ID): List[Section] = list match {
    case Nil => Nil
    case head::tail =>
      if(head.id == sectionToRemove.substring(0, head.id.length) ) {
        //é um pai do henrique ou então é o próprio henrique

        if( head.id.length == sectionToRemove.length){
          //é o próprio henrique
          tail //apaguei o que eu queria!
        }else {
          logicMC.Section(head.id, head.name, auxiliaryRemoveSection(head.sections, sectionToRemove), head.whiteboards)::tail
          //é um pai  mas nao é aquele que queremos atualizar, é um pai do henrique
        }

      }else{
        head::auxiliaryRemoveSection(tail, sectionToRemove)
      }
  }

  def getSectionPane(section: Section, godSection:Section, currentSection: Section,updateVisualState: Section => Unit, updateSectionName:Section => Unit):VBox = {
    val imageView = getImageView("images/folder.png")

    val label = new Label(section.name)
    label.setFont(Auxiliary.myFont)

    val vBox = new VBox(imageView, label)
    vBox.setSpacing(10)
    vBox.setAlignment(Pos.CENTER)

    imageView.setOnMouseClicked(key => {
      if(key.getButton == MouseButton.PRIMARY){
        
        val sectionToEnter = section.id.substring(section.id.lastIndexOf('.')+1, section.id.length).toInt

        var newSection = Section.enterSectionID(godSection, currentSection, sectionToEnter)._2

        //se a que entrámos foi a que mudámos agora,
        if(newSection.id == section.id){
          newSection = Section(newSection.id, label.getText, newSection.sections, newSection.whiteboards)
        }

        updateVisualState(newSection)

      }

    })

    getRename(section, vBox, label, updateSectionName)

    vBox
  }



  def getRename(section:Section, vBox:VBox, sectionLabel:Label,  updateSectionName: Section =>Unit):Unit = {

    val renameMenuItem = new MenuItem("Rename")
    val contextMenu = new ContextMenu(renameMenuItem)

    renameMenuItem.setOnAction(_ => {

      val popupStage = new Stage()
      popupStage.setTitle("Rename Whiteboard")
      popupStage.initModality(Modality.APPLICATION_MODAL)

      val label = new Label("Name")
      label.setFont(Auxiliary.getFont(14))
      label.setPadding(new Insets(5,0,0,5))

      val nameTextField = new TextField(section.name)
      nameTextField.setFont(Auxiliary.getFontWeight(14, FontWeight.LIGHT))
      nameTextField.setPromptText("New name")
      nameTextField.selectAll()

      VBox.setMargin(nameTextField, new Insets(10, 10, 10, 10))

      val vBoxTextField = new VBox(label)
      vBoxTextField.getChildren.add(nameTextField)
      vBoxTextField.setStyle("-fx-background-color:white; -fx-background-radius:15px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 45, 0, 0, 0);")
      vBoxTextField.setPadding(new Insets(5, 5, 5, 5))
      VBox.setMargin(vBoxTextField, new Insets(10,10,0,10))

      val okButton = Auxiliary.getButtonWithColor("00b894","088c72","Change Name")

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
          checkTextFieldAndChange(nameTextField, sectionLabel, popupStage)
          updateSectionName(section.changeName(nameTextField.getText))
        }
      })

      okButton.setOnMouseClicked(_ => {
        checkTextFieldAndChange(nameTextField, sectionLabel, popupStage)
        updateSectionName(section.changeName(nameTextField.getText))
      })

    })

    vBox.setOnContextMenuRequested( p => contextMenu.show(vBox,p.getScreenX, p.getScreenY ))

  }


}