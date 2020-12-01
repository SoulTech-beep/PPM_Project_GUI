import javafx.fxml.FXML
import javafx.geometry.{Insets, Pos}
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.image.{Image, ImageView}
import javafx.scene.input.KeyCode
import javafx.scene.layout._
import javafx.scene.paint.{Color, Paint}
import javafx.scene.shape.Circle
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

  @FXML
  private var rightStackPane:StackPane = _

  @FXML
  private var leftSpacer:HBox = _

  @FXML
  private var rightSpacer:HBox = _

  @FXML
  private var addSectionButton:MenuItem = _

  @FXML
  private var addWhiteboardButton:MenuItem = _

  var myFont: Font = Font.font("SF Pro Display", FontWeight.BLACK, 12)

  var currentSection:Section = _

  var selectedColor: Paint = Color.WHITE


  @Override
  def initialize(): Unit = {

    rightStackPane.getChildren.add(0, whiteboardScroller.getCanvas())

    HBox.setHgrow(leftSpacer, Priority.SOMETIMES)
    HBox.setHgrow(rightSpacer, Priority.SOMETIMES)

    currentSectionLabel.setText("")
    currentSectionLabel.setFont(myFont)

    goBackButton.setDisable(true)

    goBackButton.setOnMouseClicked(event => {
      FxApp.app_state = Section.exitSection(FxApp.app_state._1, FxApp.app_state._2)
      updateStuff(FxApp.app_state._2)
    })



    teste()
    addSectionButtonOnClick()
    addWhiteboardButtonOnClick()
  }

  def updateStuff(newCurrentSection: Section):Unit={
    println("---updateStuff---")

    if(newCurrentSection.id.length > 1){
      goBackButton.setDisable(false)
    }else{
      goBackButton.setDisable(true)
    }

    currentSection = newCurrentSection
    currentSectionLabel.setText(currentSection.name)

    sectionsVBox.getChildren.clear()

    FxApp.app_state._2.sections.sortWith((p1,p2) => p1.id < p2.id).foreach(p => sectionsVBox.getChildren.add(getSectionPane(p)))
    FxApp.app_state._2.whiteboards.sortWith((w1,w2)=> w1.id<w2.id).foreach(p => sectionsVBox.getChildren.add(getWhiteboardPane(p)))
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
    label.setAlignment(Pos.BASELINE_CENTER)
    //TODO FIX THE ICON OFFSET THINGY THAT IS FUCKING THIS SHIT UP, FACK.
    label.setMaxWidth(60)
    label.setMaxHeight(40)
    label.setWrapText(true)

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

      val innervBox = new VBox(nameTextField, okButton)
      innervBox.setSpacing(20)
      innervBox.setAlignment(Pos.CENTER)
      innervBox.setPadding(new Insets(10,10,10,10))


      val scene = new Scene(innervBox)

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


def addWhiteboardButtonOnClick():Unit = {
  addWhiteboardButton.setOnAction(event => {

    val popupStage: Stage = new Stage()
    popupStage.setTitle("Add Section")
    popupStage.initModality(Modality.APPLICATION_MODAL)

    val nameTextField = new TextField()
    nameTextField.setPromptText("Whiteboard name")

    val colorTextField = new TextField()
    colorTextField.setPromptText("Color name")

    val xTextField = new TextField()
    xTextField.setPromptText("X")

    val yTextField = new TextField()
    yTextField.setPromptText("Y")

    val okButton = new Button("Add Whiteboard")


    val innervBox = new VBox(nameTextField, colorPicker(), xTextField, yTextField, okButton)
    innervBox.setSpacing(20)
    innervBox.setAlignment(Pos.CENTER)
    innervBox.setPadding(new Insets(10,10,10,10))

    val scene = new Scene(innervBox)

    popupStage.setScene(scene)
    popupStage.show()

    okButton.setOnMouseClicked(p => {
      //TODO check if name isn't empty
      if(!nameTextField.getText.isBlank){


        FxApp.app_state = Section.addWhiteboardWithValues(FxApp.app_state._1, FxApp.app_state._2,selectedColor.toString, xTextField.getText.toDouble, yTextField.getText.toDouble, nameTextField.getText)

        updateStuff(FxApp.app_state._2)

        popupStage.close()
      }
    })

  })

}

  def addSectionButtonOnClick():Unit = {
    addSectionButton.setOnAction(event => {

        val popupStage: Stage = new Stage()
        popupStage.setTitle("Add Section")
        popupStage.initModality(Modality.APPLICATION_MODAL)

        val nameTextField = new TextField()
        nameTextField.setPromptText("Section name")

        nameTextField.setOnKeyPressed(p => {
          if(p.getCode == KeyCode.ENTER){
            if(!nameTextField.getText.isBlank) {
              FxApp.app_state = Section.addNewSectionName(FxApp.app_state._1, FxApp.app_state._2, nameTextField.getText)
              updateStuff(FxApp.app_state._2)

              popupStage.close()
            }
          }
        })

        val okButton = new Button("Add Section")

        val innervBox = new VBox(nameTextField, okButton)
        innervBox.setSpacing(20)
        innervBox.setAlignment(Pos.CENTER)
        innervBox.setPadding(new Insets(10,10,10,10))


        val scene = new Scene(innervBox)

        popupStage.setScene(scene)
        popupStage.show()

        okButton.setOnMouseClicked(p => {
          //TODO check if name isn't empty
          if(!nameTextField.getText.isBlank){
            FxApp.app_state = Section.addNewSectionName(FxApp.app_state._1, FxApp.app_state._2, nameTextField.getText)
            updateStuff(FxApp.app_state._2)

            popupStage.close()
          }
        })

      })

  }

  def colorPicker():HBox = {

    val colors = new HBox()

    val c1 = new Circle()
    c1.setFill(Color.WHITE)
    c1.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")

    val c2 = new Circle()
    c2.setFill(Color.web("#ffeaa7"))

    val c3 = new Circle()
    c3.setFill(Color.web("#2d3436"))
    c3.setOnMouseClicked(p => {
      selectedColor = c3.getFill
      c3.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")
      c1.setStyle("")
      c2.setStyle("")
    })

    c2.setOnMouseClicked(p => {
      selectedColor = c2.getFill
      c2.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")
      c1.setStyle("")
      c3.setStyle("")
    })

    c1.setOnMouseClicked(p =>  {
      selectedColor = c1.getFill
      c1.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);")
      c2.setStyle("")
      c3.setStyle("")
    })

    c1.setRadius(10)
    c2.setRadius(10)
    c3.setRadius(10)

    c1.setStroke(Color.BLACK)
    c2.setStroke(Color.BLACK)
    c3.setStroke(Color.BLACK)

    colors.setSpacing(20)
    colors.setAlignment(Pos.CENTER)

    colors.getChildren.addAll(c1,c2,c3)
    colors
  }


}