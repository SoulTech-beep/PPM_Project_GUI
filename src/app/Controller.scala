package app

import javafx.fxml.{FXML, FXMLLoader}
import javafx.geometry.{Insets, Pos}
import javafx.scene.control._
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.layout._
import javafx.scene.{Parent, Scene}
import javafx.stage.{Modality, Stage}
import logicMC.{Auxiliary, Section, Whiteboard}

class Controller {

  @FXML
  private var mySplitPane: SplitPane = _
  //Left Side of SplitPane
  @FXML
  private var leftAnchorPane: AnchorPane = _
  @FXML
  private var sectionsScrollPane: ScrollPane = _

  //Left Toolbar
  @FXML
  private var goBackButton: Button = _
  @FXML
  private var leftSpacer: HBox = _
  @FXML
  private var currentSectionLabel: Label = _
  @FXML
  private var rightSpacer: HBox = _
  //Add Button
  @FXML
  private var addSectionButton: MenuItem = _
  @FXML
  private var addWhiteboardButton: MenuItem = _

  //Right Side of SplitPane
  @FXML
  private var rightStackPane: StackPane = _

  @FXML
  private var toolbar: ToolBar = _

  //FlowPane where all the sections and whiteboards will appear on the left side of the split pane
  private val sectionsVBox: FlowPane = new FlowPane()

  //Current section being shown on the left side of the split pane
  private var currentSection: Section = _

  //private var app.customToolBar:app.customToolBar = new app.customToolBar

  private var canvasScroller: ScrollPane = new ScrollPane()

  var listWhiteboards: Map[String, whiteboardScroller] = Map()
  val toolBar: customToolBar = new customToolBar
  var whiteboardOnPage: whiteboardScroller = new whiteboardScroller

  @Override
  def initialize(): Unit = {
    /*app.customToolBar.setToolbar(toolbar)
    app.customToolBar.initializeCustomToolBar()*/

    //At the first time we must initialize with the GOD section (Which is the same as the current section at the beginning)
    currentSection = FxApp.app_state._2

    toolBar.setToolbar(toolbar)
    toolBar.initializeCustomToolBar()

    layoutShenanigans()

    //TODO remove: debug variables in order to help us previewing a whiteboard on the right side
    //canvasScroller = app.whiteboardScroller.getCanvas(app.customToolBar)
    rightStackPane.getChildren.add(0, canvasScroller)

    addSectionButtonOnClick()
    addWhiteboardButtonOnClick()
  }

  def layoutShenanigans(): Unit = {
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
    sectionsVBox.setPadding(new Insets(10, 10, 10, 10))

    updateVisualState(FxApp.app_state._2)

  }

  def updateVisualState(newCurrentSection: Section): Unit = {

    FxApp.app_state = (FxApp.app_state._1, newCurrentSection)

    if (newCurrentSection.id.length > 1)
      goBackButton.setDisable(false)
    else
      goBackButton.setDisable(true)

    currentSection = newCurrentSection
    currentSectionLabel.setText(currentSection.name)

    sectionsVBox.getChildren.clear()

    currentSection.sections.sortWith((p1, p2) => p1.id < p2.id).foreach(p => sectionsVBox.getChildren.add(getSectionPane(p)))
    currentSection.whiteboards.sortWith((w1, w2) => w1.id < w2.id).foreach(p => sectionsVBox.getChildren.add(getWhiteboardPane(p)))

  }

  def getWhiteboardPane(whiteboard: Whiteboard): VBox = {
    setOnClickWhiteboardPane(whiteboard, Whiteboard.getWhiteboardPane(whiteboard, updateWhiteboardName), currentSection.id + "w" + whiteboard.id)
  }

  def setOnClickWhiteboardPane(whiteboard: Whiteboard, vBox: VBox, id:String): VBox = {
    vBox.setOnMouseClicked(_ => {
      rightStackPane.getChildren.remove(canvasScroller)
      if(!listWhiteboards.contains(id)) {
        whiteboardOnPage = new whiteboardScroller()
        canvasScroller = whiteboardOnPage.getCanvas(whiteboard,toolBar, mySplitPane)
        rightStackPane.getChildren.add(0, canvasScroller)
        listWhiteboards = listWhiteboards + (id -> whiteboardOnPage)
      } else {
        rightStackPane.getChildren.remove(canvasScroller)
        whiteboardOnPage = listWhiteboards.get(id).get
        canvasScroller = whiteboardOnPage.getCanvas(whiteboard,toolBar, mySplitPane)
        rightStackPane.getChildren.add(0, canvasScroller)
      }
      //TODO if the one we click is the one being displayed, let's not remove and update everything...

    })

    vBox
  }

  def updateSectionName(section:Section):Unit = {
    FxApp.app_state = Section.updateAll(FxApp.app_state._1, section)

  }

  def updateWhiteboardName(whiteboard: Whiteboard): Unit = {
    val index = currentSection.whiteboards.indexWhere(p => p.id == whiteboard.id)
    val newCurrentSectionWhiteboards = currentSection.whiteboards.updated(index, whiteboard)

    val newCurrentSection = Section(currentSection.id, currentSection.name, currentSection.sections, newCurrentSectionWhiteboards)

    FxApp.app_state = Section.updateAll(FxApp.app_state._1, newCurrentSection)
  }

  def getSectionPane(section: Section): VBox = {
    Section.getSectionPane(section, FxApp.app_state._1, FxApp.app_state._2, updateVisualState, updateSectionName)
  }


  def blurBackground(startValue: Double, endValue: Double, duration: Double): Unit = {
    Auxiliary.blurBackground(startValue, endValue, duration, mySplitPane)
  }

  def addWhiteboardButtonOnClick(): Unit = {

    addWhiteboardButton.setOnAction(_ => {
      val fxmlLoader = new FXMLLoader(getClass.getResource("./WhiteboardCreate.fxml"))

      val mainViewRoot: Parent = fxmlLoader.load()

      val scene = new Scene(mainViewRoot)
      scene.getStylesheets.add("testStyle.css")

      blurBackground(0, 30, 1000)

      val createWhiteboardController = fxmlLoader.getController.asInstanceOf[WhiteboardCreate]
      createWhiteboardController.setState(FxApp.app_state)

      val secondStage: Stage = new Stage()
      secondStage.setScene(scene)
      secondStage.initModality(Modality.APPLICATION_MODAL)
      secondStage.show()
      secondStage.setTitle("Add Whiteboard")
      secondStage.getIcons.add(new Image("images/addIcon.png"))

      secondStage.setResizable(false)

      secondStage.setOnCloseRequest(_ => {
        updateVisualState(FxApp.app_state._2)
        blurBackground(30, 0, 500)
      })

    })
  }

  def getAddSectionPopup(stage: Stage):VBox = {
    val vBox = new VBox()
    vBox.setSpacing(20)
    vBox.setAlignment(Pos.CENTER)
    vBox.setPadding(new Insets(10, 10, 10, 10))

    val nameTextField = new TextField()
    nameTextField.setPromptText("Section name")
    nameTextField.getStyleClass.add("customTextField")

    nameTextField.setOnKeyPressed(p => {
      if (p.getCode == KeyCode.ENTER) {
        if (!nameTextField.getText.isBlank) {
          FxApp.app_state = Section.addNewSectionName(FxApp.app_state._1, FxApp.app_state._2, nameTextField.getText)
          updateVisualState(FxApp.app_state._2)

          blurBackground(30, 0, 500)
          stage.close()
        }
      }
    })

    val okButton = new Button("Add Section")
    okButton.setFont(Auxiliary.getFont(16))

    val style = "-fx-background-radius:15px; -fx-text-fill: white;"
    okButton.setStyle(style + "-fx-background-color:#55efc4;")

    okButton.setOnMouseEntered(_ =>{
      okButton.setStyle(style + "-fx-background-color:#00b894;")
    })

    okButton.setOnMouseExited(_ => {
      okButton.setStyle(style + "-fx-background-color:#55efc4;")
    })

    okButton.setMaxWidth(Double.MaxValue)
    okButton.setPrefHeight(35)


    okButton.setOnMouseClicked(_ => {
      //TODO check if name isn't empty
      if (!nameTextField.getText.isBlank) {
        FxApp.app_state = Section.addNewSectionName(FxApp.app_state._1, FxApp.app_state._2, nameTextField.getText)
        updateVisualState(FxApp.app_state._2)

        blurBackground(30, 0, 500)
        stage.close()
      }
    })

    vBox.getChildren.addAll(nameTextField, okButton)

    vBox
  }

  def addSectionButtonOnClick(): Unit = {
    addSectionButton.setOnAction(_ => {

      val popupStage: Stage = new Stage()
      popupStage.setTitle("Add Section")
      popupStage.initModality(Modality.APPLICATION_MODAL)
      popupStage.getIcons.add(new Image("images/addIcon.png"))
      popupStage.setResizable(false)

      val scene = new Scene(getAddSectionPopup(popupStage))
      scene.getStylesheets.add("testStyle.css")

      popupStage.setScene(scene)

      blurBackground(0, 30, 1000)

      popupStage.show()

      popupStage.setOnCloseRequest(_ => blurBackground(30, 0, 500))

    })

  }

}