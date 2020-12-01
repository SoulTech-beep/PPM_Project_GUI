import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout.{FlowPane, Pane, VBox}
import javafx.scene.text.{Font, FontWeight}
import javafx.stage.{Modality, Stage}
import logicMC.{Section, Whiteboard}

class Controller{

  @FXML
  private var sectionsScrollPane: ScrollPane = _

  @FXML
  private var goBackButton:Button = _

  private var sectionsVBox: FlowPane = new FlowPane()

  @FXML
  private var currentSectionLabel: Label = _

  var myFont: Font = Font.font("SF Pro Display", FontWeight.BLACK, 12)

  var currentSection:Section = _

  @Override
  def initialize(): Unit = {
    currentSectionLabel.setText("")
    currentSectionLabel.setFont(myFont)

    goBackButton.setOnMouseClicked(event => {
      FxApp.app_state = Section.exitSection(FxApp.app_state._1, FxApp.app_state._2)
      updateStuff(FxApp.app_state._2)
    })

    teste()
  }

  def updateStuff(newCurrentSection: Section):Unit={
    println("---updateStuff---")

    currentSection = newCurrentSection

    sectionsVBox.getChildren.clear()

    FxApp.app_state._2.sections.foreach(p => sectionsVBox.getChildren.add(getSectionPane(p)))
    FxApp.app_state._2.whiteboards.foreach(p => sectionsVBox.getChildren.add(getWhiteboardPane(p)))
  }

  def teste():Unit = {
    currentSectionLabel.setText(FxApp.originalSection.name)

    sectionsScrollPane.setContent(sectionsVBox)

    sectionsScrollPane.setFitToWidth(true)
    sectionsScrollPane.setFitToHeight(true)

    sectionsVBox.setHgap(20)
    sectionsVBox.setVgap(20)

    sectionsVBox.setPadding(new Insets(10,10,10,10))

    currentSection = FxApp.app_state._2

    FxApp.originalSection.sections.foreach(p => sectionsVBox.getChildren.add(getSectionPane(p)))
    FxApp.originalSection.whiteboards.foreach(p => sectionsVBox.getChildren.add(getWhiteboardPane(p)))

  }

  def teste1(section: Section):Unit = {
    var pane = new Pane(new Label(section.name + "\t" + section.id))
    sectionsVBox.getChildren.add(pane)

  }

  def getWhiteboardPane(whiteboard: Whiteboard):VBox = {

    var image = new Image("images/book.png")
    var imageView = new ImageView()
    imageView.setImage(image)
    imageView.setPreserveRatio(true)
    imageView.setSmooth(true)
    imageView.setCache(true)
    imageView.setFitHeight(50)

    var label = new Label(whiteboard.name)
    label.setFont(myFont)

    var vBox = new VBox(imageView, label)

    vBox.setSpacing(10)
    vBox.setAlignment(Pos.CENTER)

    val rename = new MenuItem("Rename")
    val contextMenu = new ContextMenu(rename)

    rename.setOnAction(p => {

      val popupStage: Stage = new Stage()
      popupStage.setTitle("Whiteboard rename")
      popupStage.initModality(Modality.APPLICATION_MODAL)

      val nameTextField = new TextField()
      nameTextField.setPromptText("New name")

      nameTextField.setOnKeyPressed(p => {
        if(p.getCode == KeyCode.ENTER){
          if(!nameTextField.getText.isBlank) {
            label.setText(nameTextField.getText)
            popupStage.close()
            updateWhiteboardName(whiteboard, nameTextField.getText)
          }
        }
      })

      val okButton = new Button("Change name")

      val vBox = new VBox(nameTextField, okButton)
      vBox.setSpacing(20)
      vBox.setAlignment(Pos.CENTER)
      vBox.setPadding(new Insets(10,10,10,10))

      val scene = new Scene(vBox)

      popupStage.setScene(scene)
      popupStage.show()

      okButton.setOnMouseClicked(p => {
        //TODO check if name isn't empty
        if(!nameTextField.getText.isBlank){
          label.setText(nameTextField.getText)
          popupStage.close()
          updateWhiteboardName(whiteboard, nameTextField.getText)
        }
      })

    })

    vBox.setOnContextMenuRequested( p => contextMenu.show(vBox,p.getScreenX, p.getScreenY ))

    vBox
  }

  def updateWhiteboardName(whiteboard: Whiteboard, name:String):Unit = {
    val newWhiteboard = whiteboard.changeName(name)

    val index = currentSection.whiteboards.indexWhere(p => p.id == newWhiteboard.id)
    val newCurrentSectionWhiteboards = currentSection.whiteboards.updated(index, newWhiteboard)

    val newCurrentSection = Section( currentSection.id,currentSection.name, currentSection.sections, newCurrentSectionWhiteboards)

    Section.describeCurrentSection(newCurrentSection, newCurrentSection)

    FxApp.app_state = Section.updateAll(FxApp.app_state._1, newCurrentSection)

    Section.describe(FxApp.app_state._1,FxApp.app_state._2 )
  }

  def getSectionPane(section: Section):VBox = {

    var image = new Image("images/folder.png")
    var imageView = new ImageView()
    imageView.setImage(image)
    imageView.setPreserveRatio(true)
    imageView.setSmooth(true)
    imageView.setCache(true)
    imageView.setFitHeight(50)

    var label = new Label(section.name)
    label.setFont(myFont)

    var vBox = new VBox(imageView, label)

    vBox.setSpacing(10)
    vBox.setAlignment(Pos.CENTER)

    imageView.setOnMouseClicked(event => {
      val sectionToEnter = section.id.substring(section.id.lastIndexOf('.')+1, section.id.length).toInt
      val newSection = Section.enterSectionID(FxApp.app_state._1, FxApp.app_state._2, sectionToEnter)._2
      FxApp.app_state = (FxApp.app_state._1, newSection)

      updateStuff(newSection)
      println(Console.BLUE + Console.BOLD + FxApp.app_state._2 + Console.RESET)
    })

    vBox
  }



  def btnPenClicked():Unit = {
    println("O HENRIQUE PARECE UMA CASTANHA")
  }

}