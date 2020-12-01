package logicMC

case class ToolPicker(firstColor: String = "Black",
                 secondColor:String = "Blue",
                 thirdColor:String = "Yellow",
                 pen:Pen = Pen(color = "Black", width = 1, opacity = 1),
                 marker: Pen = Pen(color = "Yellow", width = 2, opacity = .5),
                 eraser:Eraser =  Eraser(radius = 10)){

}

//Selecter -> Retângulo (vértice_1, vértice_2), lista caixas_regras
//GeometricShape -> RetÂngulo, Círculo, e Triângulo, Seta

object ToolPicker{

  type Selecter = (Int, Int)

  def changeFirstColor(tp: ToolPicker, color:String): ToolPicker = {
    ToolPicker(firstColor = color, secondColor = tp.secondColor, thirdColor = tp.thirdColor, pen = tp.pen,marker = tp.marker, eraser = tp.eraser)
  }

  def changeSecondColor(tp: ToolPicker, color:String): ToolPicker = {
    ToolPicker(firstColor = tp.firstColor, secondColor = color, thirdColor = tp.thirdColor, pen = tp.pen,marker = tp.marker, eraser = tp.eraser)
  }

  def changeThirdColor(tp: ToolPicker, color:String): ToolPicker = {
    ToolPicker(firstColor = tp.firstColor, secondColor = tp.secondColor, thirdColor = color, pen = tp.pen,marker = tp.marker, eraser = tp.eraser)
  }

  def changePen(tp: ToolPicker, pen:Pen): ToolPicker = {
    ToolPicker(firstColor = tp.firstColor, secondColor = tp.secondColor, thirdColor = tp.thirdColor, pen = pen,marker = tp.marker, eraser = tp.eraser)
  }

  def changeMarker(tp: ToolPicker, pen:Pen):ToolPicker = {
    ToolPicker(firstColor = tp.firstColor, secondColor = tp.secondColor, thirdColor = tp.thirdColor, pen = tp.pen,marker = pen, eraser = tp.eraser)
  }

  def changeEraser(tp: ToolPicker, eraser:Eraser):ToolPicker = {
    ToolPicker(firstColor = tp.firstColor, secondColor = tp.secondColor, thirdColor = tp.thirdColor, pen = tp.pen,marker = tp.marker, eraser = eraser)
  }

}