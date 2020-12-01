package logicMC

import scala.annotation.tailrec

class CommandLineObject(val name: String, val exec: (Section, Section) => (Section, Section))

object CommandLine {

  def prompt(msg: String) : String = {
    print(msg + ": ")
    scala.io.StdIn.readLine()
  }

  def optionPrompt(options: Map[String, CommandLineObject]): Option[CommandLineObject] = {
    println()
    println(Console.BLUE +  "---[ Options ]---" + Console.RESET)
    options.foreach(option => println( "\t" +Console.GREEN +  option._1 + ") " + option._2.name + Console.RESET))
    options.get(prompt(Console.YELLOW +  "\n\tAction" + Console.RESET))
  }
}

object App {

  val options: Map[String, CommandLineObject] = Map[String, CommandLineObject](
    "1" -> new CommandLineObject("Add logicMC.Section", Section.addNewSection ),
    "2" -> new CommandLineObject("Update all", Section.updateAll),
    "3" -> new CommandLineObject("Describe Sections", Section.describe),
    "4" -> new CommandLineObject("Enter logicMC.Section", Section.enterSection),
    "5" -> new CommandLineObject("Go to main", Section.goToMainMenu),
    "6" -> new CommandLineObject("Go up level", Section.exitSection),
    "7"-> new CommandLineObject("Current logicMC.Section", Section.describeCurrentSection),
    "8" -> new CommandLineObject("Change Name", Section.changeName),
    "9" -> new CommandLineObject("Add logicMC.Whiteboard", Section.addWhiteboard),
    "10" -> new CommandLineObject("Remove whiteboard", Section.removeWhiteboard),
    "11" -> new CommandLineObject("Remove logicMC.Section", Section.removeSection)
  )


  def main(args: Array[String]): Unit = {
    val originalSection = new Section("0", "Default", List(), List())
    mainLoop(originalSection, originalSection)
  }

  @tailrec
  def mainLoop(section: (Section, Section)):Unit = mainLoop(
    CommandLine.optionPrompt(options) match {
      case Some(opt) => opt.exec(section._1, section._2)
      case _ => println(Console.RED + "Invalid option" + Console.RESET); section
    }
  )

  /**
   * macaco: 0
   * |---antonio: 0.1
   * |   |---sapo
   * |   |---laranja
   * |   |---|---henrique
   * |   |-------|---tiago
   * |---miguel
   *     |---...
   */

}
