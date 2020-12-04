import javafx.fxml.{FXML, FXMLLoader}
import javafx.geometry.{Insets, Pos}
import javafx.scene.{Parent, Scene}
import javafx.scene.control._
import javafx.scene.input.KeyCode
import javafx.scene.layout._
import javafx.stage.{Modality, Stage}
import logicMC.{Auxiliary, Section, Whiteboard}

class Controller{

  //Left Side of SplitPane
  @FXML
  private var leftAnchorPane:AnchorPane = _
  @FXML
  private var sectionsScrollPane: ScrollPane = _

  //Left Toolbar
  @FXML
  private var goBackButton:Button = _
  @FXML
  private var leftSpacer:HBox = _
  @FXML
  private var currentSectionLabel: Label = _
  @FXML
  private var rightSpacer:HBox = _
    //Add Button
    @FXML
    private var addSectionButton:MenuItem = _
    @FXML
    private var addWhiteboardButton:MenuItem = _

  //Right Side of SplitPane
  @FXML
  private var rightStackPane:StackPane = _

  @FXML
  private var toolbar:ToolBar = _

  //FlowPane where all the sections and whiteboards will appear on the left side of the split pane
  private val sectionsVBox: FlowPane = new FlowPane()

  //Current section being shown on the left side of the split pane
  private var currentSection:Section = _

  //private var customToolBar:customToolBar = new customToolBar

  private var canvasScroller:ScrollPane = new ScrollPane()

  @Override
  def initialize(): Unit = {
    /*customToolBar.setToolbar(toolbar)
    customToolBar.initializeCustomToolBar()*/

    //At the first time we must initialize with the GOD section (Which is the same as the current section at the beginning)
    currentSection = FxApp.app_state._2

    layoutShenanigans()

    //TODO remove: debug variables in order to help us previewing a whiteboard on the right side
    //canvasScroller = whiteboardScroller.getCanvas(customToolBar)
    rightStackPane.getChildren.add(0,canvasScroller)

    addSectionButtonOnClick()
    addWhiteboardButtonOnClick()
  }

  def layoutShenanigans():Unit = {
    //When resizing the application window, the left anchor pane will not automatically expand
    SplitPane.setResizableWithParent(leftAnchorPane, false)

    //In order to center the section label!
    HBox.setHgrow(leftSpacer, Priority.SOMETIMES)
    HBox.setHgrow(rightSpacer, Priority.SOMETIMES)

    currentSectionLabel.setFont(Auxiliary.myFont)

    goBackButton.setOnMouseClicked(_ => {
      //Oh, right! We must go one upper level and update the left side graphics
      FxApp.app_state = Section.exitSection(FxApp.app_state._1, FxApp.app_state._2)
      updateVisualState(FxApp.app_state._2)
    })

    sectionsScrollPane.setContent(sectionsVBox)

    sectionsScrollPane.setFitToWidth(true)
    sectionsScrollPane.setFitToHeight(true)
    sectionsVBox.setHgap(20)
    sectionsVBox.setVgap(20)
    sectionsVBox.setPadding(new Insets(10,10,10,10))

    updateVisualState(FxApp.app_state._2)

  }

  def updateVisualState(newCurrentSection: Section):Unit={

    if(newCurrentSection.id.length > 1)
      goBackButton.setDisable(false)
    else
      goBackButton.setDisable(true)

    currentSection = newCurrentSection
    currentSectionLabel.setText(currentSection.name)

    sectionsVBox.getChildren.clear()

    currentSection.sections.sortWith((p1,p2) => p1.id < p2.id).foreach(p => sectionsVBox.getChildren.add(getSectionPane(p)))
    currentSection.whiteboards.sortWith((w1,w2)=> w1.id<w2.id).foreach(p => sectionsVBox.getChildren.add(getWhiteboardPane(p)))

  }

  def getWhiteboardPane(whiteboard: Whiteboard):VBox = {
    setOnClickWhiteboardPane(whiteboard, Whiteboard.getWhiteboardPane(whiteboard, updateWhiteboardName))
  }

  def setOnClickWhiteboardPane(whiteboard: Whiteboard, vBox: VBox):VBox = {
    vBox.setOnMouseClicked(event => {
      rightStackPane.getChildren.remove(canvasScroller)

      val toolBar:customToolBar = new customToolBar
      toolBar.setToolbar(toolbar)
      toolBar.initializeCustomToolBar()

      canvasScroller = whiteboardScroller.getCanvas(toolBar)
      rightStackPane.getChildren.add(0, canvasScroller)

      //TODO if the one we click is the one being displayed, let's not remove and update everything...

    })

    vBox
  }

  def updateWhiteboardName(whiteboard: Whiteboard):Unit = {
    val index = currentSection.whiteboards.indexWhere(p => p.id == whiteboard.id)
    val newCurrentSectionWhiteboards = currentSection.whiteboards.updated(index, whiteboard)

    val newCurrentSection = Section( currentSection.id,currentSection.name, currentSection.sections, newCurrentSectionWhiteboards)

    FxApp.app_state = Section.updateAll(FxApp.app_state._1, newCurrentSection)
  }

  def getSectionPane(section: Section):VBox = {
    Section.getSectionPane(section, FxApp.app_state._1, FxApp.app_state._2, updateVisualState)
  }

def addWhiteboardButtonOnClick():Unit = {
  /*addWhiteboardButton.setOnAction(event => {


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


    val colorPicker = Auxiliary.getColorPicker()

    val innervBox = new VBox(nameTextField, colorPicker._1, xTextField, yTextField, okButton)
    innervBox.setSpacing(20)
    innervBox.setAlignment(Pos.CENTER)
    innervBox.setPadding(new Insets(10,10,10,10))

    val scene = new Scene(innervBox)

    popupStage.setScene(scene)
    popupStage.show()

    okButton.setOnMouseClicked(p => {
      //TODO check if name isn't empty
      if(!nameTextField.getText.isBlank){


        FxApp.app_state = Section.addWhiteboardWithValues(FxApp.app_state._1, FxApp.app_state._2,colorPicker._2.get().toString, xTextField.getText.toDouble, yTextField.getText.toDouble, nameTextField.getText)
        println("color: " + colorPicker._2.get())
        updateVisualState(FxApp.app_state._2)

        popupStage.close()
      }
    })

  })
*/
  addWhiteboardButton.setOnAction(_ =>  {
    val fxmlLoaderWhiteboard = new FXMLLoader(getClass.getResource("WhiteboardCreate.fxml"))
    val mainViewRoot: Parent = fxmlLoaderWhiteboard.load()
    val scene = new Scene(mainViewRoot)

    val createWhiteboardController = fxmlLoaderWhiteboard.getController.asInstanceOf[WhiteboardCreate]
    createWhiteboardController.setState(FxApp.app_state)

    val secondStage: Stage = new Stage()
    secondStage.setScene(scene)
    secondStage.show()

    secondStage.setOnCloseRequest(_ => {
      updateVisualState(FxApp.app_state._2)
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
              updateVisualState(FxApp.app_state._2)

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
            updateVisualState(FxApp.app_state._2)

            popupStage.close()
          }
        })

      })

  }

}