package app

import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.control._
import javafx.scene.input.KeyCode
import javafx.scene.layout._
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

  var listWhiteboards: Map[String, WhiteboardGUI] = Map()
  var whiteboardOnPage: Option[WhiteboardGUI] = None

  @Override
  def initialize(): Unit = {
    //At the first time we must initialize with the GOD section (Which is the same as the current section at the beginning)
    currentSection = FxApp.app_state._2

    toolbar.setDisable(true)

    layoutShenanigans()

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
    setOnClickWhiteboardPane(whiteboard, Whiteboard.getWhiteboardPane(whiteboard, updateWhiteboardName, mySplitPane), currentSection.id + "w" + whiteboard.id)
  }

  def setOnClickWhiteboardPane(whiteboard: Whiteboard, vBox: VBox, id:String): VBox = {
    vBox.setOnMouseClicked(_ => {
      rightStackPane.getChildren.remove(canvasScroller)
      if(!listWhiteboards.contains(id)) {
        val toolBar: customToolBar = new customToolBar
        toolBar.setToolbar(toolbar)
        toolBar.initializeCustomToolBar()
        toolbar.setDisable(false)
        whiteboardOnPage = Some(new WhiteboardGUI(toolBar,whiteboard,mySplitPane))

        canvasScroller = whiteboardOnPage.get.getCanvas
        rightStackPane.getChildren.add(0, canvasScroller)
        listWhiteboards = listWhiteboards + (id -> whiteboardOnPage.get)
      } else {
        rightStackPane.getChildren.remove(canvasScroller)
        whiteboardOnPage = Some(listWhiteboards.get(id).get)
        canvasScroller = whiteboardOnPage.get.getCanvas
        rightStackPane.getChildren.add(0, canvasScroller)
        toolbar.getItems.clear()
        whiteboardOnPage.get.toolbar.buttonList.foreach(p => toolbar.getItems.add(p))
        toolbar.getItems.add( whiteboardOnPage.get.toolbar.optionsHBox)
      }

    })

    vBox
  }


  def updateSectionName(section: Section): Unit = {
    FxApp.app_state = Section.updateAll(FxApp.app_state._1, section)
  }

  def updateWhiteboardName(whiteboard: Whiteboard): Unit = {
    val index = currentSection.whiteboards.indexWhere(p => p.id == whiteboard.id)
    val newCurrentSectionWhiteboards = currentSection.whiteboards.updated(index, whiteboard)

    val newCurrentSection = Section(currentSection.id, currentSection.name, currentSection.sections, newCurrentSectionWhiteboards)

    FxApp.app_state = Section.updateAll(FxApp.app_state._1, newCurrentSection)

    //TODO Added, may or may not work!
    currentSection = FxApp.app_state._2
  }

  def getSectionPane(section: Section): VBox = {
    Section.getSectionPane(section, FxApp.app_state._1, FxApp.app_state._2, updateVisualState, updateSectionName, mySplitPane)
  }


  def blurBackground(startValue: Double, endValue: Double, duration: Double): Unit = {
    Auxiliary.blurBackground(startValue, endValue, duration, mySplitPane)
  }

  def addWhiteboardButtonOnClick(): Unit = {
    addWhiteboardButton.setOnAction(_ => {

      val whiteboardCreate = new WhiteboardCreate
      whiteboardCreate.initialize(mySplitPane, updateVisualState)
      whiteboardCreate.setState(FxApp.app_state)

      blurBackground(0, 30, 1000)

    })
  }


  def addSectionButtonOnClick(): Unit = {

    val popup = Auxiliary.setUpPopup("Add Section", "Section name", "", ("fdcb6e", "fcba03", "Add Section"), show = false)
    popup._1.setOnCloseRequest(_ => blurBackground(30, 0, 500))

    addSectionButton.setOnAction(_ => {

      popup._1.show()

      popup._2.setOnKeyPressed(p => if (p.getCode == KeyCode.ENTER) addNewSectionAndUpdateVisualState())
      popup._3.setOnMouseClicked(_ => addNewSectionAndUpdateVisualState())

      def addNewSectionAndUpdateVisualState():Unit = {
        if (!popup._2.getText.isBlank) {
          FxApp.app_state = Section.addNewSectionName(FxApp.app_state._1, FxApp.app_state._2, popup._2.getText)
          updateVisualState(FxApp.app_state._2)

          popup._2.setText("")
          popup._1.close()
          blurBackground(30, 0, 500)
        }
      }

      blurBackground(0, 30, 500)

    })

  }

}