/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication; 
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import processing.core.*;
import processing.javafx.PSurfaceFX;
import javafx.scene.image.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.*;
import javafx.beans.value.*;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import java.util.*;
import java.io.*;


//import java.awt.Toolkit;


/**
 *
 * @author wilder4690
 */
public class ArtStationApplication extends PApplet{
    

    enum Coordinates {OFF, MOUSE, TOP}
    ChangeList tasks = new ChangeList();
    Coordinates coordinateMode = Coordinates.TOP;
    float scaleFactor;
    float verticalPadding = 0.05f;
    float verticalScreenShare = 1 - 2*verticalPadding;
    float horizontalPadding = 0.05f;
    float horizontalScreenShare = 1 - 2*horizontalPadding;
    boolean[] keys = new boolean[255];
    float canvasX;
    float canvasY;
    int activeButton;
    CanvasArea pad;
    
    //GUI
    ToggleGroup toolGroup;
    ToggleGroup modeGroup;
    ListView<Shape> shapeViewer;
    ObservableList<Shape> shapes;
    final int NONE = -777; //Used for no fill color
    int toolBarWidth = 50;
    int controlBarWidth = 275;
    int spacing = 5;
    String dialog = "";
    
    //IO
    String fileLocation = "noFile";

    @Override
    public void settings(){
        size(displayWidth - toolBarWidth - controlBarWidth,displayHeight, FX2D);
    }
    
    @Override
    protected PSurface initSurface(){
        PSurface mySurface = super.initSurface();

        final PSurfaceFX FXSurface = (PSurfaceFX) mySurface;
        final Canvas canvas = (Canvas) FXSurface.getNative(); // canvas is the processing drawing
        final Stage stage = (Stage) canvas.getScene().getWindow(); // stage is the window
        
        //Menu Bar /////////////////////////////////////////////////////////////
        MenuBar mb = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        
        MenuItem open = new MenuItem("Open");
        MenuItem newDrawing = new MenuItem("New Drawing");
        MenuItem save = new MenuItem("Save");
        MenuItem saveAs = new MenuItem("Save As");
        MenuItem exit = new MenuItem("Exit");
        
        save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        
        fileMenu.getItems().addAll(open, newDrawing, save, saveAs, new SeparatorMenuItem(), exit);


        Menu optionsMenu = new Menu("Options");

        Menu gridOptions = new Menu("Grid Options");
        
        CheckMenuItem gridOn = new CheckMenuItem("Grid");
        gridOn.setAccelerator(KeyCombination.keyCombination("shortcut+G"));
        gridOn.setSelected(true);
        CheckMenuItem gridSnapOn = new CheckMenuItem("Snap");
        gridSnapOn.setAccelerator(KeyCombination.keyCombination("shortcut+L"));
        gridOptions.getItems().addAll(gridOn, gridSnapOn);
        optionsMenu.getItems().add(gridOptions);
        

        Menu mouseOptions = new Menu("Mouse Position");
        ToggleGroup tgMouse = new ToggleGroup();
        
        RadioMenuItem coordsOff = new RadioMenuItem("Off");
        RadioMenuItem coordsMouse = new RadioMenuItem("Mouse");
        RadioMenuItem coordsTop = new RadioMenuItem("Top");
        coordsOff.setToggleGroup(tgMouse);
        coordsMouse.setToggleGroup(tgMouse);
        coordsTop.setToggleGroup(tgMouse);
        mouseOptions.getItems().addAll(coordsOff, coordsMouse, coordsTop);
        optionsMenu.getItems().add(mouseOptions);
        optionsMenu.getItems().add(new SeparatorMenuItem());
        
        Menu exportMenu = new Menu("Export");
        
        MenuItem clipboardShapes = new MenuItem("... Shapes to Clipboard");
        MenuItem clipboardFile = new MenuItem("... File to Clipboard");
        MenuItem imageFile = new MenuItem("...as Image");
        MenuItem svgFile = new MenuItem("... as SVG");
        exportMenu.getItems().addAll(clipboardFile, clipboardShapes, imageFile, svgFile);

        mb.getMenus().addAll(fileMenu, optionsMenu, exportMenu);
        
        coordsOff.setOnAction(event -> coordinateMode = Coordinates.OFF); 
        coordsMouse.setOnAction(event -> coordinateMode = Coordinates.MOUSE); 
        coordsTop.setOnAction(event -> coordinateMode = Coordinates.TOP); 
        
        newDrawing.setOnAction(event -> clearScreen());
        saveAs.setOnAction(event ->{
            final FileChooser fileChooser = new FileChooser();
            File initialDirectory = new File("C:\\Users\\wilder4690\\Documents\\GitHub\\ArtStationApplication\\src\\artstationapplication\\data\\saveFiles");
            fileChooser.setInitialDirectory(initialDirectory);
            fileChooser.setTitle("Save Drawing");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ASF", "*.txt")
            );
            File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
            if(file != null){ 
                String location = file.getAbsolutePath();
                saveDrawing(location);
            }
        });
        
        save.setOnAction(event -> {
            if(fileLocation.equals("noFile")){
                saveAs.fire();
            }
            else saveDrawing(fileLocation);
        });
        
        open.setOnAction(event ->{
            final FileChooser fileChooser = new FileChooser();
            File initialDirectory = new File("C:\\Users\\wilder4690\\Documents\\GitHub\\ArtStationApplication\\src\\artstationapplication\\data\\saveFiles");
            fileChooser.setInitialDirectory(initialDirectory);
            fileChooser.setTitle("Open Drawing");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("ASF", "*.txt")
            );
            File file = fileChooser.showOpenDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
            if(file != null){ 
                String location = file.getAbsolutePath();
                loadDrawing(location);
            }
        });

        EventHandler<ActionEvent> MenuHandler = new EventHandler<ActionEvent>(){ 
            public void handle(ActionEvent ae){ 
                String name = ((MenuItem)ae.getTarget()).getText();
                MenuItem target = (MenuItem)ae.getTarget();
                //all menu Handling done here
                if(name.equals("Exit")) Platform.exit();
                else if(target == clipboardFile) exportProcessingFileToClipboard(); 
                else if(target == clipboardShapes) exportProcessingShapesToClipboard(); 
                else if(target == imageFile){
                        final FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Image");
                        fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                            new FileChooser.ExtensionFilter("TIFF", "*.tif"),
                            new FileChooser.ExtensionFilter("TARGA", "*.tga")
                        );
                        File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                        if(file != null){ 
                            String location = file.getAbsolutePath();
                            exportImageFile(location);
                        }
                        
                }
                else if(target == svgFile){
                        final FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save SVG");
                        fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("SVG", "*.svg")
                        );
                        File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                        if(file != null){ 
                            String location = file.getAbsolutePath();
                            exportSVGFile(location);
                        }
                }
            }
        };
        
        clipboardFile.setOnAction(MenuHandler);
        clipboardShapes.setOnAction(MenuHandler);
        imageFile.setOnAction(MenuHandler);
        svgFile.setOnAction(MenuHandler);
        exit.setOnAction(MenuHandler);

        
        
        //Mode buttons//////////////////////////////////////////////////////////
        modeGroup = new ToggleGroup();
        
        RadioButton drawMode = new RadioButton("Draw");
        drawMode.setToggleGroup(modeGroup);
        
        RadioButton editMode = new RadioButton("Edit");
        editMode.setToggleGroup(modeGroup);
                
        drawMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.drawMode();
                canvas.requestFocus();
            }
        });   
        
        editMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.editMode();
                canvas.requestFocus();
            }
        });  
        drawMode.setSelected(true);
        
        
        //Tool Buttons//////////////////////////////////////////////////////////
        toolGroup = new ToggleGroup();
        
        Image imageCircle = new Image(getClass().getResource("data/btnCircle.png").toExternalForm());
        ToggleButton btnCircle = new ToggleButton("Circle", new ImageView(imageCircle));
        btnCircle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnCircle.setStyle("-fx-padding:0; ");
        btnCircle.setToggleGroup(toolGroup);
        btnCircle.setTooltip(new Tooltip("Click in center, drag to size."));
        
        Image imageSquare = new Image(getClass().getResource("data/btnRectangle.png").toExternalForm());
        ToggleButton btnRectangle = new ToggleButton("Rectangle", new ImageView(imageSquare));
        btnRectangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnRectangle.setStyle("-fx-padding:0");
        btnRectangle.setToggleGroup(toolGroup);
        btnRectangle.setTooltip(new Tooltip("Click in center, drag to size. Hold SHIFT to snap rotation to 45 degree increments."));
        
        Image imageTriangle = new Image(getClass().getResource("data/btnTriangle.png").toExternalForm());
        ToggleButton btnTriangle = new ToggleButton("Triangle", new ImageView(imageTriangle));
        btnTriangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnTriangle.setStyle("-fx-padding:0");
        btnTriangle.setToggleGroup(toolGroup);
        btnTriangle.setTooltip(new Tooltip("Click in center, drag to size. Hold SHIFT to snap rotation to 30 degree increments."));
        
        Image imageLine = new Image(getClass().getResource("data/btnLine.png").toExternalForm());
        ToggleButton btnLine = new ToggleButton("Line", new ImageView(imageLine));
        btnLine.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnLine.setStyle("-fx-padding:0");
        btnLine.setToggleGroup(toolGroup);
        btnLine.setTooltip(new Tooltip("Click for first point and drag to second point."));
        
        Image imagePoly = new Image(getClass().getResource("data/btnPoly.png").toExternalForm());
        ToggleButton btnPoly = new ToggleButton("Poly", new ImageView(imagePoly));
        btnPoly.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnPoly.setStyle("-fx-padding:0");
        btnPoly.setToggleGroup(toolGroup);
        btnPoly.setTooltip(new Tooltip("Single click for each vertex, SPACE or right click to complete."));
        
        Image imageCurve = new Image(getClass().getResource("data/btnCurve.png").toExternalForm());
        ToggleButton btnCurve = new ToggleButton("Curve", new ImageView(imageCurve));
        btnCurve.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnCurve.setStyle("-fx-padding:0");
        btnCurve.setToggleGroup(toolGroup);
        btnCurve.setTooltip(new Tooltip("Click for first point and drag to second point."));
        
        EventHandler<ActionEvent> ToolHandler = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                String name = ((ToggleButton)ae.getTarget()).getText();
                //all tool handling done here
                if(name.equals("Circle")){
                    pad.setActiveTool(ShapeType.CIR);
                }
                else if(name.equals("Rectangle")){
                     pad.setActiveTool(ShapeType.REC);
                }
                else if(name.equals("Triangle")){
                     pad.setActiveTool(ShapeType.TRI);
                }
                else if(name.equals("Line")){
                     pad.setActiveTool(ShapeType.LIN);
                }
                else if(name.equals("Poly")){
                     pad.setActiveTool(ShapeType.POL);
                }
                else if(name.equals("Curve")){
                     pad.setActiveTool(ShapeType.CUR);
                }
                drawMode.setSelected(true);
                pad.drawMode();
                canvas.requestFocus(); 
            }
        };
        
        btnCircle.setOnAction(ToolHandler);
        btnRectangle.setOnAction(ToolHandler);
        btnLine.setOnAction(ToolHandler);
        btnTriangle.setOnAction(ToolHandler);
        btnPoly.setOnAction(ToolHandler);
        btnCurve.setOnAction(ToolHandler);
        
        //Canvas Menu///////////////////////////////////////////////////////////
        final HBox dimensionPane = new HBox(spacing);
        //final VBox canvasOptions = new VBox(spacing, dimensionPane, backgroundPane);
        final MenuButton canvasMenuButton = new MenuButton("Canvas Options");
        final CustomMenuItem canvasMenu = new CustomMenuItem(dimensionPane);
        canvasMenuButton.getItems().add(canvasMenu);
        final TextField widthTextField = new TextField(Integer.toString(900));
        final TextField heightTextField = new TextField(Integer.toString(900));
        final Label lblWidth = new Label("Width:");
        final Label lblHeight = new Label("Height:");
        
        
        dimensionPane.getChildren().addAll(lblWidth, widthTextField, lblHeight, heightTextField);
        
        canvasMenu.setHideOnClick(false);
        canvasMenuButton.setPrefWidth(controlBarWidth/2);
        dimensionPane.setPrefWidth(controlBarWidth -7*spacing);
        widthTextField.setPrefColumnCount(5);
        heightTextField.setPrefColumnCount(5);
                
        EventHandler<ActionEvent> textHandler = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                int newWidthVal = convertStringToInt(widthTextField.getText());
                int newHeightVal = convertStringToInt(heightTextField.getText());
                if(newWidthVal <= 0){   //Resets textfield back to current value if user input is invalid
                    widthTextField.setText(Integer.toString(pad.getWidth()));
                }
                else if(newHeightVal <= 0){  //Resets textfield back to current value if user input is invalid
                    heightTextField.setText(Integer.toString(pad.getHeight()));
                }
                else{
                    pad.setWidth(newWidthVal);
                    pad.setHeight(newHeightVal);
                    scaleCanvas((float)(stage.getWidth()-toolBarWidth - controlBarWidth),(float)(stage.getHeight() - 2*mb.getHeight()));
                    canvas.setWidth(stage.getWidth()-toolBarWidth - controlBarWidth);
                    pad.setGridDensity();
                    canvas.requestFocus();
                    for(int i = 0; i < shapes.size(); i++){ 
                        shapes.get(i).resizeHandles(20/scaleFactor);
                    }
                }
            }
        };
        
        widthTextField.setOnAction(textHandler);
        heightTextField.setOnAction(textHandler);
        
        //Reference Image Button////////////////////////////////////////////////
        final HBox referencePane = new HBox(spacing);
        final Button btnReferenceImage = new Button("Reference Image");
        final CheckBox cbReferenceImage = new CheckBox();
        
        btnReferenceImage.setTooltip(new Tooltip("Loads reference image to background of sketch"));
        btnReferenceImage.setPrefWidth(controlBarWidth/2);
        cbReferenceImage.setSelected(false);
        
        referencePane.getChildren().addAll(btnReferenceImage, cbReferenceImage);
        
        btnReferenceImage.setOnAction(event ->{
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Reference Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("TARGA", "*.tga")
            );
            File file = fileChooser.showOpenDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
            if(file != null){ 
                String location = file.getAbsolutePath();
                pad.loadReferenceImage(location);
                cbReferenceImage.setSelected(true);
            }
        });
        
        cbReferenceImage.setOnAction(event ->{
            pad.toggleReferenceImage(cbReferenceImage.isSelected());
        });
                
        //Grid Menu///////////////////////////////////////////////////////////
        int gridMax = 100;
        final VBox gridPane = new VBox();
        final MenuButton gridMenuButton = new MenuButton("Grid Options");
        final CustomMenuItem gridMenu = new CustomMenuItem(gridPane);
        gridMenuButton.getItems().add(gridMenu);
        final GridPane gridSettings = new GridPane();
        final Label lblGrid = new Label("Grid");
        final Label lblGridOn = new Label("On");
        final Label lblGridSnap = new Label("Snap");
        final TextField gridTextField = new TextField("10");
        final CheckBox cbGridOn = new CheckBox();
        final CheckBox cbGridSnap = new CheckBox();
        Slider gridSlider = new Slider(0, gridMax, 10);
        
        gridMenuButton.setPrefWidth(controlBarWidth/2);
        gridPane.setMaxWidth(controlBarWidth -7*spacing);
        lblGrid.setUnderline(true);
        lblGridOn.setUnderline(true);
        lblGridSnap.setUnderline(true);
        gridTextField.setPrefColumnCount(3);
        gridSlider.setShowTickMarks(true);
        gridSlider.setMajorTickUnit(gridMax/4);
        gridSlider.setMinorTickCount(gridMax/4-1);
        gridSlider.setSnapToTicks(true);
        gridSlider.setShowTickLabels(true);
        gridSettings.setPrefWidth(controlBarWidth -7*spacing);
        gridSettings.setHgap(2*spacing);
        gridSettings.setAlignment(Pos.CENTER);
        gridSettings.setHalignment(lblGrid, HPos.CENTER);
        gridSettings.setHalignment(cbGridOn, HPos.CENTER);
        gridSettings.setHalignment(cbGridSnap, HPos.CENTER);
        cbGridOn.setSelected(true);
        gridMenu.setHideOnClick(false);
        
        //Lambda? wtf this is so much more intuitive
        //gridMenuButton.setOnAction( //TODO use lamba to see if we can focus on canvas if menu is open);
        //gridMenuButton.setOnMouseClicked(e -> canvas.requestFocus());
        
        gridSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                int output = newVal.intValue();
                gridTextField.setText(Integer.toString(output));
                pad.setGridDensity(output);
            }
        });
        
        gridTextField.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                int newValue = convertStringToInt(gridTextField.getText());
                if(newValue == -1){
                    newValue = (int)gridSlider.getValue();
                    gridTextField.setText(Integer.toString((int)gridSlider.getValue()));
                }
                else if(newValue < 0){
                    newValue = 0;
                    gridTextField.setText("0");
                }
                else if(newValue > gridMax){
                    newValue = gridMax;
                    gridTextField.setText(Integer.toString(newValue));
                }
                gridSlider.setValue(newValue);
            }
        });
        
        cbGridOn.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.toggleGrid(cbGridOn.isSelected());
                gridOn.setSelected(cbGridOn.isSelected());
                gridTextField.setDisable(!cbGridOn.isSelected());
                gridSlider.setDisable(!cbGridOn.isSelected());
                cbGridSnap.setDisable(!cbGridOn.isSelected());

            }
        });
        
        cbGridSnap.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.toggleSnap(cbGridSnap.isSelected());
            }
        });
        
        gridSettings.add(lblGrid, 0,0);
        gridSettings.add(lblGridOn, 1, 0);
        gridSettings.add(lblGridSnap, 2,0);
        gridSettings.add(gridTextField, 0,1);
        gridSettings.add(cbGridOn, 1,1);
        gridSettings.add(cbGridSnap, 2,1);
        gridPane.getChildren().addAll(gridSettings, gridSlider);

        
        //Fill & Stroke Pane////////////////////////////////////////////////////
        final GridPane colorPane = new GridPane();
               
        final Label lblColor = new Label("Color");
        final Label lblFill = new Label("Fill:");
        final Label lblStroke = new Label("Stroke:");
        final Label lblWeight = new Label("Weight");
        final Label lblNone = new Label("None");
        final Label lblBackground = new Label("Canvas:");
        
        final ColorPicker colorPickerFill = new ColorPicker(Color.WHITE);
        final ColorPicker colorPickerStroke = new ColorPicker(Color.BLACK);
        final ColorPicker colorPickerBackground = new ColorPicker(Color.WHITE);
        
        final CheckBox cbNoFill = new CheckBox();
        final CheckBox cbNoStroke = new CheckBox();
        final CheckBox cbNoBackground = new CheckBox();
        
        final Slider weightSlider = new Slider(0, 10, 1);
        final TextField weightTextField = new TextField("1");
        
        colorPane.setVgap(spacing/2);
        colorPane.setHgap(spacing);
        colorPane.setHalignment(lblFill, HPos.RIGHT);
        colorPane.setHalignment(lblStroke, HPos.RIGHT);
        colorPane.setHalignment(lblBackground, HPos.RIGHT);
        colorPane.setHalignment(lblColor, HPos.CENTER);
        colorPane.setHalignment(lblNone, HPos.CENTER);
        colorPane.setHalignment(cbNoStroke, HPos.CENTER);
        colorPane.setHalignment(cbNoFill, HPos.CENTER);
        colorPane.setHalignment(cbNoBackground, HPos.CENTER);
        lblColor.setUnderline(true);
        lblNone.setUnderline(true);
        weightTextField.setPrefColumnCount(3);

        cbNoFill.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(cbNoFill.isSelected()){
                    pad.setCurrentFillColor(NONE);
                    colorPickerFill.setDisable(true);
                }
                else{
                    pad.setCurrentFillColor(convertColorToInt(colorPickerFill.getValue()));
                    colorPickerFill.setDisable(false);
                }
                if(pad.isEditMode()){
                    shapes.get(pad.listIndex).setFillColor(pad.getCurrentFillColor());
                }
                canvas.requestFocus(); 
            }
        });
        
        cbNoStroke.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(cbNoStroke.isSelected()){
                    pad.setCurrentStrokeWeight(0);
                    colorPickerStroke.setDisable(true);
                    weightSlider.setDisable(true);
                    weightTextField.setDisable(true);
                }
                else{
                    pad.setCurrentStrokeWeight((float)convertStringToDouble(weightTextField.getText()));
                    pad.setCurrentStrokeColor(convertColorToInt(colorPickerStroke.getValue()));
                    colorPickerStroke.setDisable(false);
                    weightSlider.setDisable(false);
                    weightTextField.setDisable(false);
                }
                if(pad.isEditMode()){
                    shapes.get(pad.listIndex).setStrokeWeight(pad.getCurrentStrokeWeight());
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerFill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentFillColor(convertColorToInt(colorPickerFill.getValue()));
                if(pad.isDrawMode()){     
                    //canvas.requestFocus(); 
                }
                else{
                    tasks.push(new Change(Transformation.FIL, pad.listIndex, shapes.get(pad.listIndex).getFillColor()));
                    shapes.get(pad.listIndex).setFillColor(convertColorToInt(colorPickerFill.getValue()));
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerStroke.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentStrokeColor(convertColorToInt(colorPickerStroke.getValue()));
                if(pad.isDrawMode()){     
                    //canvas.requestFocus(); 
                }
                else{
                    tasks.push(new Change(Transformation.STF, pad.listIndex, shapes.get(pad.listIndex).getStrokeColor()));
                    shapes.get(pad.listIndex).setStrokeColor(convertColorToInt(colorPickerStroke.getValue()));
                }
                canvas.requestFocus();  
            }
        });
        
        weightSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                weightTextField.setText(Double.toString(newVal.floatValue()));
                pad.setCurrentStrokeWeight((float)newVal.floatValue());
                if(pad.isEditMode()){
                    tasks.push(new Change(Transformation.STW, pad.listIndex, shapes.get(pad.listIndex).strokeWeight));
                    shapes.get(pad.listIndex).setStrokeWeight(newVal.floatValue());
                }
            }
        });
        
        weightTextField.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                double newValue = convertStringToDouble(weightTextField.getText());
                if(newValue == -1){
                    newValue = weightSlider.getValue();
                    weightTextField.setText(Double.toString(weightSlider.getValue()));
                }
                else if(newValue < 0){
                    newValue = 0;
                    weightSlider.setValue(0);
                    weightTextField.setText("0");
                }
                else if(newValue > 100){
                    weightSlider.setValue(100);
                }
                else{
                    weightSlider.setValue(newValue);
                }
                pad.setCurrentStrokeWeight((float)newValue);
                if(pad.isEditMode()){
                    tasks.push(new Change(Transformation.STW, pad.listIndex, shapes.get(pad.listIndex).strokeWeight));
                    shapes.get(pad.listIndex).setStrokeWeight((float)newValue);
                }
            }
        });
        
        cbNoBackground.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(cbNoBackground.isSelected()){
                    pad.setBackgroundColor(NONE);
                    colorPickerBackground.setDisable(true);
                }
                else{
                    pad.setBackgroundColor(convertColorToInt(colorPickerBackground.getValue()));
                    colorPickerBackground.setDisable(false);
                }
            }
        });
        
        colorPickerBackground.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setBackgroundColor(convertColorToInt(colorPickerBackground.getValue()));
            }
        });

        colorPane.setMaxWidth(controlBarWidth -7*spacing);
        colorPane.add(lblColor, 1,0);
        colorPane.add(lblNone, 2,0);
        colorPane.add(lblBackground, 0,1);
        colorPane.add(colorPickerBackground, 1,1);
        colorPane.add(cbNoBackground, 2,1);
        colorPane.add(lblFill, 0,2);
        colorPane.add(colorPickerFill, 1,2);
        colorPane.add(cbNoFill, 2,2);
        colorPane.add(lblStroke, 0,3);
        colorPane.add(colorPickerStroke, 1,3);
        colorPane.add(cbNoStroke, 2,3);
        colorPane.add(lblWeight, 0, 4);
        colorPane.add(weightSlider, 1, 4);
        colorPane.add(weightTextField, 2,4);
        
        //Observable List///////////////////////////////////////////////////////  
        HBox listPanel = new HBox();
        Region strut = new Region();
        VBox listControls = new VBox(spacing);
        VBox.setVgrow(strut, Priority.ALWAYS);
        shapes = FXCollections.observableArrayList();   
        ObservableList<Shape> shapeTypes = shapes;
        shapeViewer = new ListView<>(shapeTypes);
        
        Image imageUpArrow = new Image(getClass().getResource("data/btnUpArrow.png").toExternalForm());
        Button btnUpArrow = new Button("Up", new ImageView(imageUpArrow));
        btnUpArrow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnUpArrow.setStyle("-fx-padding:0; ");
        btnUpArrow.setTooltip(new Tooltip("Moves selected shape backward."));
        
        Image imageDownArrow = new Image(getClass().getResource("data/btnDownArrow.png").toExternalForm());
        Button btnDownArrow = new Button("Down", new ImageView(imageDownArrow));
        btnDownArrow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnDownArrow.setStyle("-fx-padding:0; ");
        btnDownArrow.setTooltip(new Tooltip("Moves selected shape forward."));
        
        Button btnDelete = new Button("Delete");
        btnDelete.setTooltip(new Tooltip("Deletes current shape."));
        btnDelete.setMinWidth(toolBarWidth + spacing);
                
        Button btnReset = new Button("Reset");
        btnReset.setTooltip(new Tooltip("Removes all rotation and scaling from current shape."));
        btnReset.setMinWidth(toolBarWidth + spacing);
        
        Button btnCopy = new Button("Copy");
        btnCopy.setTooltip(new Tooltip("Creates an identical copy of currently selected shape."));
        btnCopy.setMinWidth(toolBarWidth + spacing);
        
        btnUpArrow.setOnAction(event -> {
            if(pad.listIndex-1 < 0 || pad.listIndex-1 == shapes.size()) {
                canvas.requestFocus();
                return;
            }
            tasks.push(new Change(Transformation.ORD, pad.listIndex, new float[]{pad.listIndex-1, pad.listIndex}));
            swapElements(pad.listIndex, pad.listIndex-1);
            canvas.requestFocus();
        });
        btnDownArrow.setOnAction(event -> {
            if(pad.listIndex+1 < 0 || pad.listIndex+1 >= shapes.size()){
                canvas.requestFocus();
                return;
            }
            tasks.push(new Change(Transformation.ORD, pad.listIndex, new float[]{pad.listIndex+1, pad.listIndex}));
            swapElements(pad.listIndex, pad.listIndex+1);
            canvas.requestFocus();
        });
        btnDelete.setOnAction(event -> {
            if(shapes.isEmpty()) return;
            Change currentChange = new Change(Transformation.DEL, pad.listIndex, 0);
            currentChange.createClone(shapes.get(pad.listIndex));
            tasks.push(currentChange);
            deleteShape();
            canvas.requestFocus();
        });
        btnReset.setOnAction(event -> {
            if(!shapes.isEmpty()) shapes.get(pad.listIndex).reset();
        });
        btnCopy.setOnAction(event -> copyShape());

        
        listControls.getChildren().addAll(btnUpArrow, btnDownArrow, strut, btnCopy, btnReset, btnDelete);
        listPanel.getChildren().addAll(listControls, shapeViewer);
        
        listControls.setAlignment(Pos.CENTER);
        listControls.setPadding(new Insets(0,spacing,0,0));
        listPanel.setMaxSize(controlBarWidth -7*spacing, controlBarWidth*3);
        shapeViewer.setPrefHeight(controlBarWidth*1.5);
        MultipleSelectionModel<Shape> selectionModel = shapeViewer.getSelectionModel();
        
        selectionModel.selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                editMode.setSelected(true);
                pad.editMode();
                pad.listIndex = (int)newVal;
            }
        });
        
        //Processing Canvas/////////////////////////////////////////////////////
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        
        
        final BorderPane rootNode = new BorderPane();
        rootNode.setPadding(new Insets(0,8*spacing,0,0));
        
        //Key Events for full scene
        rootNode.addEventFilter(KeyEvent.KEY_PRESSED, event ->{
            if(event.getCode() == KeyCode.ESCAPE){
                //Does this work?
                pad.drawMode();
            }
        });
        rootNode.addEventFilter(KeyEvent.KEY_RELEASED, event->{
            if(event.getCode() == KeyCode.DELETE){
                if(!shapes.isEmpty()){
                    Change currentChange = new Change(Transformation.DEL, pad.listIndex, 0);
                    currentChange.createClone(shapes.get(pad.listIndex));
                    tasks.push(currentChange);
                    deleteShape();
                }
            }
            else if(event.getCode() == KeyCode.ALT){
                
            }
            else if(event.getCode() == KeyCode.SPACE){
                if(pad.isDrawMode()){
                    editMode.setSelected(true);
                    pad.completeShape();  
                }
                else{
                    drawMode.setSelected(true);
                    pad.drawMode();  
                }
            }
            //canvas.requestFocus();
        });
        
        
        
        final VBox controls = new VBox(spacing);
        final VBox modes = new VBox(spacing);      
        final TilePane toolPane = new TilePane();
        

        controls.setPadding(new Insets(spacing, 2*spacing, spacing, spacing));
        controls.setMinWidth(controlBarWidth -4*spacing);
        controls.getChildren().addAll(canvasMenuButton, referencePane,gridMenuButton, colorPane,listPanel);
        modes.getChildren().addAll(drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        modes.setPadding(new Insets(spacing, 0,0,0));
        toolPane.setPadding(new Insets(0,spacing,0,spacing));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnLine, btnPoly, btnCurve);
        toolPane.setPrefColumns(1);
        toolPane.setHgap(spacing);
        toolPane.setVgap(spacing);
        
        //rootNode.setMargin(toolPane, new Insets(spacing));
        rootNode.setTop(mb);
        rootNode.setLeft(toolPane);
        rootNode.setRight(controls);
        rootNode.setCenter(canvas);
                
        final Scene newscene = new Scene(rootNode); // Create a scene from the elements

        //Window Properties/////////////////////////////////////////////////////
        stage.setTitle("Art Station");
        stage.setMaximized(true);
        stage.setMinWidth(controlBarWidth + toolBarWidth);

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            scaleCanvas((float)(stage.getWidth()-toolBarWidth - controlBarWidth),(float)(stage.getHeight() - 2*mb.getHeight()));
            canvas.setWidth(stage.getWidth()-toolBarWidth - controlBarWidth);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
             scaleCanvas((float)(stage.getWidth()-toolBarWidth - controlBarWidth),(float)(stage.getHeight() - 2*mb.getHeight()));
             //canvas.setHeight(stage.getHeight()-toolBarWidth - controlBarWidth);
        });
        
        //Cross Element Event Handling//////////////////////////////////////////
        ////Use for handling of triggers that need to reference mulitple GUI elements
        
        gridSnapOn.setOnAction(event ->{
            pad.toggleSnap(gridSnapOn.isSelected());
            pad.toggleGrid(true);
            cbGridSnap.setSelected(gridSnapOn.isSelected());
            gridOn.setSelected(true);
            cbGridOn.setSelected(gridOn.isSelected());
            gridTextField.setDisable(!cbGridOn.isSelected());
            gridSlider.setDisable(!cbGridOn.isSelected());
            cbGridSnap.setDisable(!cbGridOn.isSelected());
        });
        
        gridOn.setOnAction(event -> {
            pad.toggleGrid(gridOn.isSelected());
            cbGridOn.setSelected(gridOn.isSelected());
            gridTextField.setDisable(!cbGridOn.isSelected());
            gridSlider.setDisable(!cbGridOn.isSelected());
            cbGridSnap.setDisable(!cbGridOn.isSelected());
        });


         pad = new CanvasArea(this,900,900, shapes, tasks);

        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setScene(newscene); // Replace the stage's scene with our new one.
            }
        });
        canvas.requestFocus();
        return mySurface; 
    }
    
    @Override 
    public void draw(){
        background(55,55,55);
        drawCanvasArea();
        drawFrames();
        drawMouse();
        drawDialog();
        checkInput();
    }
       
    @Override
    public void mousePressed(){
        activeButton = mouseButton; //stores a copy because mouseButton drops value before triggering mouseReleased/mouseClicked
        if(mouseButton == LEFT){
            if(mouseOverCanvas()){
              dialog = "";
              if(pad.isDrawMode()){
                pad.drawShape(canvasX, canvasY);
              }
              else if(pad.isEditMode()){
                pad.checkForTransformation(new PVector(canvasX, canvasY));
              }
            }
        }
    }
    
        
    @Override
    //Check mouse LEFT vs RIGHT against "activeButton" because mouseButton variable drops its value before these checks run
    public void mouseReleased(){ 
        if(activeButton == LEFT){
            if(pad.isDrawMode()){
                if(pad.activeTool == ShapeType.POL){
                    pad.completeVertex(canvasX, canvasY);
                }
                else{
                    pad.completeShape(); 
                }
            }
        }
        else if(activeButton == RIGHT){
            if(pad.isDrawMode()){
               pad.completeShape();  
           }
            else{  
                pad.drawMode();
            }
        }
        if(pad.isEditMode()){
            pad.clearSubMode();
        }
    }

    @Override
    public void keyPressed(){
        if(key < 255){
            keys[key] = true;
        }
        if(key == CODED){
            switch(keyCode){
                case SHIFT: pad.setShift(true); break;
                case ALT: pad.setAlt(true); break;
                case CONTROL: pad.setControl(true); break;                   
            }
        }
    }
    
    @Override
    public void keyReleased(){
        if(key < 255){
            keys[key] = false;
        }
        if(key == CODED){
            switch(keyCode){
                case SHIFT: pad.setShift(false); break;
                case ALT: pad.setAlt(false); break;
                case CONTROL: pad.setControl(false); break; 
            }
        }
    }

        void undo(Change task){
            switch(task.getOperation()){
                case ROT: shapes.get(task.getIndex()).setRotation(task.changedValues[0]);dialog = "Reverted rotation.";  break;
                case TRA: shapes.get(task.getIndex()).manipulate(task.changedValues[0],task.changedValues[1]); dialog = "Reverted translation.";  break;
                case SCA: shapes.get(task.getIndex()).setHandles(task.changedValues); dialog = "Reverted scaling."; break;
                case DEL: shapes.add(task.getClone()); dialog = "Reverted deletion.";  break;
                case ORD: swapElements((int)task.changedValues[0], (int) task.changedValues[1]); dialog = "Reverted reordering."; break;
                case ADD: deleteShape(task.getIndex()); dialog = "Reverted addition of new shape."; break;
                case COP: break;
                case FIL: shapes.get(task.getIndex()).setFillColor((int) task.changedValues[0]); break;
                case STF: shapes.get(task.getIndex()).setStrokeColor((int) task.changedValues[0]); break;
                case STW: shapes.get(task.getIndex()).setStrokeWeight((int) task.changedValues[0]); break;   
            }
        }
    
    void checkInput(){
        if((keys['z'] || keys['Z']) && pad.control){
            Change item = tasks.pop();
            if(item != null) undo(item);
            keys['z'] = false;
            keys['Z'] = false;
        }
    }
    
    void swapElements(int currentIndex, int newIndex){
        if(newIndex < 0 || newIndex == shapes.size()) return;
        Collections.swap(shapes, currentIndex, newIndex);
        shapeViewer.getSelectionModel().select(newIndex);
    }
    
    void clearScreen(){
        for(int i = shapes.size()-1; i >= 0; i--){
            shapes.remove(i);
        }
        pad.listIndex = 0;
        pad.drawMode();
    }
    
    void deleteShape(){
        deleteShape(pad.listIndex);
    }
     
    void deleteShape(int target){
        if(pad.modifying) pad.completeShape(); //finishes polygon if interrupted while drawing
        if(!shapes.isEmpty()){
            shapes.remove(target);
            pad.listIndex = shapes.size() - 1; 
            if(pad.listIndex < 0){
                pad.listIndex = 0;
                pad.drawMode(); //switches back to draw mode if no other shapes to edit. 
            }
            shapeViewer.getSelectionModel().select(pad.listIndex);
        }
    }
    
    void copyShape() {
        if(pad.isEditMode() && !shapes.isEmpty()){
            shapes.add(shapes.get(pad.listIndex).copy(shapes.size()));
            pad.listIndex++;
            shapes.get(pad.listIndex).finishShape();
        }
    }
    
    int convertColorToInt(Color col){
        int r = (int)(col.getRed()*255);
        int g = (int)(col.getGreen()*255);
        int b = (int)(col.getBlue()*255);
        int a = (int)(col.getOpacity()*255);
        
        return color(r,g,b,a);
    }
    
    double convertStringToDouble(String number){
        double result;
        try{
            result = Double.parseDouble(number);
        }
        catch (NumberFormatException e){
            result = -1;
        }
        return result; 
    }
    
    int convertStringToInt(String number){
        int result;
        try{
            result = Integer.parseInt(number);
        }
        catch (NumberFormatException e){
            result = -1;
        }
        return result; 
    }
    
    boolean mouseOverCanvas(){
        return (canvasX >= 0 && canvasY >= 0 && canvasY <= pad.getHeight() && canvasX <= pad.getWidth());
    }
    
    void drawMouse(){
        fill(155);
        switch(coordinateMode){
            case OFF: break;
            case MOUSE:  text( "X: "+canvasX + ", Y: "+ canvasY,mouseX, mouseY-5); break;
            case TOP: text( "X: "+canvasX + ", Y: "+ canvasY,width*horizontalPadding , height*verticalPadding - spacing); break;
        }
    }
    
    void setDialog(String input){
        dialog = input;
    }
    
    void drawDialog(){
        fill(155);
        text(dialog, width*horizontalPadding, height*verticalPadding - 4*spacing);
    }
    
    void drawFrames(){
        rectMode(CORNER);
        noStroke();
        fill(55,55,55);
        rect(0,0, width*horizontalPadding, height);
        rect(0,0, width, height*verticalPadding);
        rect(0, height*verticalPadding + (pad.getHeight()*scaleFactor), width, height*verticalPadding);
        rect( width*horizontalPadding + (pad.getWidth()*scaleFactor), 0, width, height);
      }
    
    void drawCanvasArea(){
        pushMatrix();
          translate(width*horizontalPadding, height*verticalPadding);
          scale(scaleFactor,scaleFactor);
          pad.drawCanvas(canvasX, canvasY);
          canvasX = (mouseX - screenX(0,0))/scaleFactor;
          canvasY = (mouseY - screenY(0,0))/scaleFactor; 
        popMatrix();
    }
    
    //Scales canvas based on how much of the central screen remains after padding is removed
    void scaleCanvas(float wide, float tall){
        scaleFactor = (horizontalScreenShare*wide)/(float)pad.getWidth();
        if( scaleFactor > (verticalScreenShare*tall)/(float)pad.getHeight()){
          scaleFactor = (verticalScreenShare*tall)/(float)pad.getHeight();
        }
    }
    
    
    void exportProcessingFileToClipboard(){
        String output = "void setup(){\n ";
        output += "\tsize("+pad.getWidth()+", "+pad.getHeight()+");\n}\n\n";
        output += "void draw(){ \n";
        output += "\tbackground("+pad.getBackgroundColor()+");\n\n";
        
        for (int i = 0; i < shapes.size(); i++) {
            output += shapes.get(i).printToClipboard();
        }
        output += "}";
        
        //Prepare String for clipbaord
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
    }
    
    void exportProcessingShapesToClipboard(){
        String output = "";
        for (int i = 0; i < shapes.size(); i++) {
            output += shapes.get(i).printToClipboard();
        }
        //Prepare String for clipbaord
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
    }
    
    void exportImageFile(String location){
        PGraphics exportGraphic = createGraphics(pad.getWidth(), pad.getHeight());
        exportGraphic.beginDraw();
        if(pad.getBackgroundColor() != NONE) exportGraphic.background(pad.getBackgroundColor());
        for(int i = 0; i < shapes.size(); i++){
            shapes.get(i).printToPGraphic(exportGraphic);
        }
        exportGraphic.endDraw();
        exportGraphic.save(location);
        dialog = "Image saved in "+location;
    }
    
    void exportSVGFile(String location){
        PGraphics exportGraphic = createGraphics(pad.getWidth(), pad.getHeight(), SVG, location);
        exportGraphic.beginDraw();
        if(pad.getBackgroundColor() != NONE) exportGraphic.background(pad.getBackgroundColor());
        for(int i = 0; i < shapes.size(); i++){
            shapes.get(i).printToPGraphic(exportGraphic);
        }
        exportGraphic.dispose();
        exportGraphic.endDraw();
        dialog = "Image saved in "+location;
    }
    
    //Semicolon is used as delimiter for an object. Comma is delimiter for values. 
    //Parsing should split first by ';' and the first element ("Bezier", "Circle", "Rectangle", etc) 
    //will determine which load constructor to use.
    void loadDrawing(String location){
        clearScreen();
        String line;
        String[] pieces;
        
        try{
            BufferedReader reader = new BufferedReader(new FileReader(location));
            
            //Canvas information////////////////////////////////////////////////
            line = reader.readLine();
            pieces = line.split(",");
            pad.setWidth(Integer.valueOf(pieces[0]));
            pad.setHeight(Integer.valueOf(pieces[1]));
            pad.setBackgroundColor(Integer.valueOf(pieces[2]));
            pad.setGridDensity(Integer.valueOf(pieces[3]));
            
            //Creates shapes with load constructor//////////////////////////////
            while((line = reader.readLine()) != null){
                pieces = line.split(";");
                pad.loadShape(pieces[0], pieces[1].split(","));
            }
            fileLocation = location;
        }
        catch (FileNotFoundException e){
            dialog = "File not found.";
        }
        catch (IOException e){
            dialog = "IO Exception.";
        }
    }
    
    void saveDrawing(String location){
        File file = new File(location);
        PrintWriter output;
        try{
            file.createNewFile();
        } catch( IOException e1){
            dialog = "Could not create File.";
        }
        try{
            output = new PrintWriter(file);
            output.println(pad.getWidth()+","+pad.getHeight()+","+pad.getBackgroundColor()+","+pad.getGridDensity()); 
            for(int i = 0; i < shapes.size(); i++){
                output.println(shapes.get(i).save());
            }
            output.flush();
            output.close();
        }
        catch(FileNotFoundException e){
            dialog = "File not found.";
        }
    }
    
    public static void main(String[] args) {
        String[] processingArgs = {"Art Station"};
        ArtStationApplication app = new ArtStationApplication();
        PApplet.runSketch(processingArgs, app);
    }
}
