package logicMC

case class Selector(v1: (Double,Double), v2: (Double,Double), selected: List[String]) {

}

object Selector{

  def addAreaSelectedObject(s: Selector, sObjects: List[String]): Selector = {
    Selector(s.v1, s.v2, sObjects ++ s.selected)
  }

}