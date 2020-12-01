package logicMC

import logicMC.Section.{ID, Name}

import scala.annotation.tailrec

case class Section(id: ID, name: Name, sections: List[Section], whiteboards: List[Whiteboard]){

}

object Section{

  type Size = (Double, Double)
  type ID = String
  type Name = String


  /*def getNewSection(s: logicMC.Section, somethingToChange: Any)(section: Boolean):logicMC.Section = somethingToChange match {
    case something:ID => logicMC.Section(something, s.name, s.sections, s.whiteboards)
    case something:Name => logicMC.Section(s.id, something, s.sections, s.whiteboards)
    case something:List[Any] => {
      if(section){
        logicMC.Section(s.id, s.name,something.asInstanceOf[List[logicMC.Section]], s.whiteboards)
      }else{
         logicMC.Section(s.id, s.name, s.sections, something.asInstanceOf[List[logicMC.Whiteboard]])
      }
    }
  }*/

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

  def addNewSection(mainSection :Section, s:Section):(Section, Section) = {
    val name = CommandLine.prompt("logicMC.Section name")

    if(s.sections.isEmpty){
      val sectionCreated = Section(s.id+".1",name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, sectionCreated::s.sections, s.whiteboards)

      (mainSection, sectionWeAreIn)
    }else{
      val maxID = s.sections.last.id.split('.').last.toInt + 1
      val newID : String = s.id + '.' + maxID.toString

      val sectionCreated = Section(newID,name, List(), List() )
      val sectionWeAreIn = logicMC.Section(s.id, s.name, (sectionCreated::s.sections).reverse, s.whiteboards)

      (mainSection, sectionWeAreIn)
    }
  }

  def updateAll(mainSection: Section, s:Section):(Section, Section) = {

    val newMainSections: List[Section] = updateAllAuxiliary(mainSection.sections, s)
    val newMainSection = Section(mainSection.id, mainSection.name, newMainSections, mainSection.whiteboards)

    if(s.id.length == 1){
      return (Section(s.id, s.name, s.sections, s.whiteboards),s)
    }

    (newMainSection, s)
  }

  def updateAllAuxiliary(list : List[Section], updatedSection:Section): List[Section] = list match {
    case Nil => updatedSection.sections

    case head::tail =>
        if(updatedSection.id.length == 1){
          //estávamos na secção original e como tal é só devolver o updatedSection.sections (que foi atualizado algures no método onde fizemos algo)
          return updatedSection.sections
        }
        println("head.id: " + head.id + "\tupdatedSection.id:" + updatedSection.id)
        if(head.id == updatedSection.id.substring(0, head.id.length) ) {
          println("1: " + head + tail)
          //é um pai do henrique ou então é o próprio henrique
              if( head.id.length == updatedSection.id.length){
                println("2")
                //é o próprio henrique
                updatedSection::tail
              }else {
                println("3")
                logicMC.Section(head.id, head.name, updateAllAuxiliary(head.sections, updatedSection), head.whiteboards)::tail
              //é um pai  mas nao é aquele que queremos atualizar, é um pai do henrique
              }

        }else{
          println("4: " + head + tail)
          head::updateAllAuxiliary(tail, updatedSection)
        }
  }

  def enterSection(mainSection : Section, section: Section):(Section, Section) = {
    val sectionIDtoEnter = CommandLine.prompt("Which section do you want to enter")

    val sectionToEnter = section.sections.indexWhere( p => p.id.split('.').last.toInt == sectionIDtoEnter.toInt)

    if(sectionToEnter == -1){
      println("NO SECTION")
      (mainSection, section)
    }else{
      (mainSection, section.sections.apply(sectionToEnter))
    }

  }

  def enterSectionID(mainSection : Section, section: Section, sectionIDtoEnter:Int):(Section, Section) = {

    val sectionToEnter = section.sections.indexWhere( p => p.id.split('.').last.toInt == sectionIDtoEnter.toInt)

    if(sectionToEnter == -1){
      println("NO SECTION")
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
      println("id: " + id + "\thead.id: " + head.id)
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

  def goToMainMenu(mainSection: Section, section: Section):(Section, Section) = {
    (mainSection, mainSection)
  }

  def changeName(mainSection: Section, section: Section):(Section, Section) = {
    //We are changing the current section name!

    val newSectionName = CommandLine.prompt("What's the new section name")
    val newSection = logicMC.Section(section.id, newSectionName, section.sections, section.whiteboards)
    updateAll(mainSection,newSection )
  }

  def addWhiteboard(mainSection : Section, section: Section):(Section, Section) = {
    val color = CommandLine.prompt("What color do you want to use as background")
    val sizeX = CommandLine.prompt("Width")
    val sizeY = CommandLine.prompt("Height")
    val name = CommandLine.prompt("Name")

    val id = ( section.whiteboards foldRight 0) ((a,b) => if(a.id > b) a.id else b)
    val new_wb = Whiteboard(id,color,(sizeX.toDouble, sizeY.toDouble),List(), name)

    val newSection = logicMC.Section(section.id, section.name, section.sections, new_wb::section.whiteboards)
    updateAll(mainSection, newSection)
    //TODO we gotta check if the update is working m8!
  }

  def removeWhiteboard(mainSection: Section, section: Section):(Section,Section) = {
    val whiteboardToRemove = CommandLine.prompt("What whiteboard do you want to remove")

    val newWhiteboards = section.whiteboards.filter(p => p.id != whiteboardToRemove.toInt)

    val newSection = Section(section.id, section.name, section.sections, newWhiteboards)

    updateAll(mainSection, newSection)
    //TODO we also gotta check if the update is working :c
  }

  //AINDA DOS ANTIGOS:


  def removeSection(mainSection: Section, section: Section):(Section, Section) = {
  //we are going to remove the secion we currently are in.

    if(section.id.length == 1){
      CommandLine.prompt(Console.RED + "Can't remove the current section" + Console.RESET)
      (mainSection, section)
    }else{
      val newMainSections: List[Section] = auxiliaryRemoveSection(mainSection.sections, section.id)
      val newMainSection = logicMC.Section(mainSection.id, mainSection.name, newMainSections, mainSection.whiteboards)

      (newMainSection, exitSection(mainSection, section)._2)
      //TODO when removed, we shall go to the upper level section! Now we are just going to the main section...
    }

  }

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
        println("4: " + head + tail)
        head::auxiliaryRemoveSection(tail, sectionToRemove)
      }
  }



}