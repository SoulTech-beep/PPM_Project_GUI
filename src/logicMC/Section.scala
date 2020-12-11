package logicMC

import javafx.geometry.Pos
import javafx.scene.control.{ContextMenu, Label, MenuItem}
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import logicMC.Auxiliary.{getImageView, getPopup}
import logicMC.PageStyle.PageStyle
import logicMC.Section.{ID, Name}

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

  def addNewSectionName(mainSection :Section, s:Section, name:String):(Section, Section) = {
    if(s.sections.isEmpty){
      val sectionCreated = Section(s.id+".1",name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, sectionCreated::s.sections, s.whiteboards)

      updateAll(mainSection, sectionWeAreIn)
    }else{
      val maxID = s.sections.last.id.split('.').last.toInt + 1
      val newID : String = s.id + '.' + maxID.toString

      val sectionCreated = Section(newID,name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, (sectionCreated::s.sections).reverse, s.whiteboards)

      updateAll(mainSection, sectionWeAreIn)
    }

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
          return updatedSection.sections
        }
        if(head.id == updatedSection.id.substring(0, head.id.length) ) {
              if( head.id.length == updatedSection.id.length){
                updatedSection::tail
              }else {
                logicMC.Section(head.id, head.name, updateAllAuxiliary(head.sections, updatedSection), head.whiteboards)::tail
              }

        }else{
          head::updateAllAuxiliary(tail, updatedSection)
        }
  }


 //Enter on the section with the specific ID
  def enterSectionID(mainSection : Section, section: Section, sectionIDtoEnter:Int):(Section, Section) = {

    val sectionToEnter = section.sections.indexWhere( p => p.id.split('.').last.toInt == sectionIDtoEnter.toInt)

    if(sectionToEnter == -1){
      (mainSection, section)
    }else{
      (mainSection, section.sections.apply(sectionToEnter))
    }

  }


  //Go up by one level, unless we are the root section!
  def exitSection(mainSection: Section, section: Section):(Section, Section) = {

    if(section.id.length == 1){
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

        if( head.id.length == id.length){

          head
        }else {
          auxiliaryExit(head.sections, id)

        }
      }else{
        auxiliaryExit(tail, id)
      }
  }

  def addWhiteboardWithValues(mainSection : Section, section: Section, color:Color, sizeX:Double, sizeY:Double, name:String, style:PageStyle):(Section, Section) = {

    val id = ( section.whiteboards foldRight 0) ((a,b) => if(a.id > b) a.id else b)
    val new_wb = Whiteboard(id+1,color,(sizeX.toDouble, sizeY.toDouble),List(), name, style)

    val newSection = logicMC.Section(section.id, section.name, section.sections, new_wb::section.whiteboards)
    updateAll(mainSection, newSection)
    //TODO we gotta check if the update is working m8!
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

        if(newSection.id == section.id){
          newSection = Section(newSection.id, label.getText, newSection.sections, newSection.whiteboards)
          //In case the name was changed!
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

      getPopup[Section]("Rename Section",
      "Name",
        section.name,
        ("00b894","088c72","Change Name"),
        sectionLabel,
        updateSectionName,
        section.changeName
      )

    })

    vBox.setOnContextMenuRequested( p => contextMenu.show(vBox,p.getScreenX, p.getScreenY ))

  }


}