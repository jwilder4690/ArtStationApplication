/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.collections.*;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.beans.binding.Bindings;

/**
 *
 * @author wilder4690
 */
public class GUI {
    
    BorderPane rootNode;
    
    //Values
    final int NONE = -777; //Used for no fill color
    int toolBarWidth = 50;
    int controlBarWidth = 275;
    int spacing = 5;
    int gridMax = 100;
    
    //Elements
    ToggleGroup toolGroup; //nn
    ToggleGroup modeGroup; //nn
    ListView<Shape> shapeViewer; //nn
    ObservableList<Shape> shapes;
    MenuBar mb;
    MenuItem open, newDrawing, save, saveAs, exit, clipboardShapes, clipboardFile, imageFile, svgFile;
    CheckMenuItem gridOn;
    CheckMenuItem gridSnapOn;
    RadioMenuItem coordsOff;
    RadioMenuItem coordsMouse;
    RadioMenuItem coordsTop;
    
    RadioButton drawMode;
    RadioButton editMode;
    
    ToggleButton btnCircle, btnRectangle, btnTriangle, btnPoly, btnCurve, btnLine, btnPicture, btnArc; 
    
    TextField widthTextField;
    TextField heightTextField;
    
    Button btnReferenceImage;
    CheckBox cbReferenceImage;
    
    TextField gridTextField ;
    CheckBox cbGridOn;
    CheckBox cbGridSnap;
    Slider gridSlider;
    
    ColorPicker colorPickerFill;
    ColorPicker colorPickerStroke;
    ColorPicker colorPickerBackground;
    CheckBox cbNoFill;
    CheckBox cbNoStroke;
    CheckBox cbNoBackground;
    Slider weightSlider;
    TextField weightTextField;
    
    Button btnUpArrow, btnDownArrow, btnDelete, btnReset, btnCopy;
    MultipleSelectionModel<Shape> selectionModel;
    
    GUI(){
        
    }
    
    void initializeGUI(){
        //Menu Bar /////////////////////////////////////////////////////////////
        mb = new MenuBar();
        
        Menu fileMenu = new Menu("File");
        
        open = new MenuItem("Open");
        newDrawing = new MenuItem("New Drawing");
        save = new MenuItem("Save");
        saveAs = new MenuItem("Save As");
        exit = new MenuItem("Exit");
        
        save.setAccelerator(KeyCombination.keyCombination("shortcut+S"));
        
        fileMenu.getItems().addAll(open, newDrawing, save, saveAs, new SeparatorMenuItem(), exit);

        Menu optionsMenu = new Menu("Options");

        Menu gridOptions = new Menu("Grid Options");
        
        gridOn = new CheckMenuItem("Grid");
        gridOn.setAccelerator(KeyCombination.keyCombination("shortcut+G"));
        gridOn.setSelected(true);
        gridSnapOn = new CheckMenuItem("Snap");
        gridSnapOn.setAccelerator(KeyCombination.keyCombination("shortcut+L"));
        gridOptions.getItems().addAll(gridOn, gridSnapOn);
        optionsMenu.getItems().add(gridOptions);

        Menu mouseOptions = new Menu("Mouse Position");
        ToggleGroup tgMouse = new ToggleGroup();
        
        coordsOff = new RadioMenuItem("Off");
        coordsMouse = new RadioMenuItem("Mouse");
        coordsTop = new RadioMenuItem("Top");
        coordsOff.setToggleGroup(tgMouse);
        coordsMouse.setToggleGroup(tgMouse);
        coordsTop.setToggleGroup(tgMouse);
        mouseOptions.getItems().addAll(coordsOff, coordsMouse, coordsTop);
        optionsMenu.getItems().add(mouseOptions);
        
        Menu exportMenu = new Menu("Export");
        
        clipboardShapes = new MenuItem("... Processing shapes to Clipboard");
        clipboardFile = new MenuItem("... Processing file to Clipboard");
        imageFile = new MenuItem("... as Image");
        svgFile = new MenuItem("... as SVG");
        exportMenu.getItems().addAll(clipboardFile, clipboardShapes, imageFile, svgFile);

        mb.getMenus().addAll(fileMenu, optionsMenu, exportMenu);

        //Mode buttons//////////////////////////////////////////////////////////
        modeGroup = new ToggleGroup();
        
        drawMode = new RadioButton("Draw");
        drawMode.setToggleGroup(modeGroup);
        
        editMode = new RadioButton("Edit");
        editMode.setToggleGroup(modeGroup); 
        drawMode.setSelected(true);

        //Tool Buttons//////////////////////////////////////////////////////////
        toolGroup = new ToggleGroup();
        
        Image imageCircle = new Image(getClass().getResource("data/btnCircle.png").toExternalForm());
        Image imageCirclePressed = new Image(getClass().getResource("data/btnCirclePressed.png").toExternalForm());
        final ImageView circleView = new ImageView();
        btnCircle = new ToggleButton("Circle", circleView);
        circleView.imageProperty().bind(Bindings
                .when(btnCircle.selectedProperty())
                .then(imageCirclePressed)
                .otherwise(imageCircle)
        );
        btnCircle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnCircle.setStyle("-fx-padding:0; ");
        btnCircle.setToggleGroup(toolGroup);
        btnCircle.setTooltip(new Tooltip("Click in center, drag to size."));
        
        Image imageRectangle = new Image(getClass().getResource("data/btnRectangle.png").toExternalForm());
        Image imageRectanglePressed = new Image(getClass().getResource("data/btnRectanglePressed.png").toExternalForm());
        final ImageView rectangleView = new ImageView();
        btnRectangle = new ToggleButton("Rectangle", rectangleView);
        rectangleView.imageProperty().bind(Bindings
                .when(btnRectangle.selectedProperty())
                .then(imageRectanglePressed)
                .otherwise(imageRectangle)
        );
        btnRectangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnRectangle.setStyle("-fx-padding:0");
        btnRectangle.setToggleGroup(toolGroup);
        btnRectangle.setTooltip(new Tooltip("Click in center, drag to size. Hold SHIFT to snap rotation to 45 degree increments. Hold ALT to draw from corner to corner."));
        
        Image imageTriangle = new Image(getClass().getResource("data/btnTriangle.png").toExternalForm());
        Image imageTrianglePressed = new Image(getClass().getResource("data/btnTrianglePressed.png").toExternalForm());
        final ImageView triangleView = new ImageView();
        btnTriangle = new ToggleButton("Triangle", triangleView);
        triangleView.imageProperty().bind(Bindings
                .when(btnTriangle.selectedProperty())
                .then(imageTrianglePressed)
                .otherwise(imageTriangle)
        );
        btnTriangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnTriangle.setStyle("-fx-padding:0");
        btnTriangle.setToggleGroup(toolGroup);
        btnTriangle.setTooltip(new Tooltip("Click in center, drag to size. Hold SHIFT to snap rotation to 30 degree increments."));
        
        Image imageLine = new Image(getClass().getResource("data/btnLine.png").toExternalForm());
        Image imageLinePressed = new Image(getClass().getResource("data/btnLinePressed.png").toExternalForm());
        final ImageView lineView = new ImageView();
        btnLine = new ToggleButton("Line", lineView);
        lineView.imageProperty().bind(Bindings
                .when(btnLine.selectedProperty())
                .then(imageLinePressed)
                .otherwise(imageLine)
        );
        btnLine.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnLine.setStyle("-fx-padding:0");
        btnLine.setToggleGroup(toolGroup);
        btnLine.setTooltip(new Tooltip("Click for first point and drag to second point."));
        
        Image imagePoly = new Image(getClass().getResource("data/btnPoly.png").toExternalForm());
        Image imagePolyPressed = new Image(getClass().getResource("data/btnPolyPressed.png").toExternalForm());
        final ImageView polyView = new ImageView();
        btnPoly = new ToggleButton("Poly", polyView);
        polyView.imageProperty().bind(Bindings
                .when(btnPoly.selectedProperty())
                .then(imagePolyPressed)
                .otherwise(imagePoly)
        );
        btnPoly.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnPoly.setStyle("-fx-padding:0");
        btnPoly.setToggleGroup(toolGroup);
        btnPoly.setTooltip(new Tooltip("Single click for each vertex, SPACE, right click, or click on first point to complete."));
        
        Image imageCurve = new Image(getClass().getResource("data/btnCurve.png").toExternalForm());
        Image imageCurvePressed = new Image(getClass().getResource("data/btnCurvePressed.png").toExternalForm());
        final ImageView curveView = new ImageView();
        btnCurve = new ToggleButton("Curve", curveView);
        curveView.imageProperty().bind(Bindings
                .when(btnCurve.selectedProperty())
                .then(imageCurvePressed)
                .otherwise(imageCurve)
        );
        btnCurve.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnCurve.setStyle("-fx-padding:0");
        btnCurve.setToggleGroup(toolGroup);
        btnCurve.setTooltip(new Tooltip("Click for first point and drag to second point."));
        
        Image imagePicture = new Image(getClass().getResource("data/btnPicture.png").toExternalForm());
        Image imagePicturePressed = new Image(getClass().getResource("data/btnPicturePressed.png").toExternalForm());
        final ImageView pictureView = new ImageView();
        btnPicture = new ToggleButton("Picture", pictureView);
        pictureView.imageProperty().bind(Bindings
                .when(btnPicture.selectedProperty())
                .then(imagePicturePressed)
                .otherwise(imagePicture)
        );
        btnPicture.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnPicture.setStyle("-fx-padding:0");
        btnPicture.setToggleGroup(toolGroup);
        btnPicture.setTooltip(new Tooltip("Select image to load from file."));
        
        Image imageArc = new Image(getClass().getResource("data/btnArc.png").toExternalForm());
        Image imageArcPressed = new Image(getClass().getResource("data/btnArcPressed.png").toExternalForm());
        final ImageView arcView = new ImageView();
        btnArc = new ToggleButton("Arc", arcView);
        arcView.imageProperty().bind(Bindings
                .when(btnArc.selectedProperty())
                .then(imageArcPressed)
                .otherwise(imageArc)
        );
        btnArc.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnArc.setStyle("-fx-padding:0");
        btnArc.setToggleGroup(toolGroup);
        btnArc.setTooltip(new Tooltip("Click in center, drag to size. Then use handles to adjust start and end of arc."));
        

        //Canvas Menu///////////////////////////////////////////////////////////
        final HBox dimensionPane = new HBox(spacing);
        final MenuButton canvasMenuButton = new MenuButton("Canvas Options");
        final CustomMenuItem canvasMenu = new CustomMenuItem(dimensionPane);
        canvasMenuButton.getItems().add(canvasMenu);
        widthTextField = new TextField(Integer.toString(900));
        heightTextField = new TextField(Integer.toString(900));
        final Label lblWidth = new Label("Width:");
        final Label lblHeight = new Label("Height:");
        
        dimensionPane.getChildren().addAll(lblWidth, widthTextField, lblHeight, heightTextField);
        
        canvasMenu.setHideOnClick(false);
        canvasMenuButton.setPrefWidth(controlBarWidth/2);
        dimensionPane.setPrefWidth(controlBarWidth -7*spacing);
        widthTextField.setPrefColumnCount(5);
        heightTextField.setPrefColumnCount(5);
                
        //Reference Image Button////////////////////////////////////////////////
        final HBox referencePane = new HBox(spacing);
        btnReferenceImage = new Button("Reference Image");
        cbReferenceImage = new CheckBox();
        
        btnReferenceImage.setTooltip(new Tooltip("Loads reference image to background of sketch"));
        btnReferenceImage.setPrefWidth(controlBarWidth/2);
        cbReferenceImage.setSelected(false);
        
        referencePane.getChildren().addAll(btnReferenceImage, cbReferenceImage);
        
        //Grid Menu///////////////////////////////////////////////////////////
        final VBox gridPane = new VBox();
        final MenuButton gridMenuButton = new MenuButton("Grid Options");
        final CustomMenuItem gridMenu = new CustomMenuItem(gridPane);
        gridMenuButton.getItems().add(gridMenu);
        final GridPane gridSettings = new GridPane();
        final Label lblGrid = new Label("Grid");
        final Label lblGridOn = new Label("On");
        final Label lblGridSnap = new Label("Snap");
        gridTextField = new TextField("10");
        cbGridOn = new CheckBox();
        cbGridSnap = new CheckBox();
        gridSlider = new Slider(0, gridMax, 10);
        
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
        
        colorPickerFill = new ColorPicker(Color.WHITE);
        colorPickerStroke = new ColorPicker(Color.BLACK);
        colorPickerBackground = new ColorPicker(Color.WHITE);
        
        cbNoFill = new CheckBox();
        cbNoStroke = new CheckBox();
        cbNoBackground = new CheckBox();
        
        weightSlider = new Slider(0, 10, 1);
        weightTextField = new TextField("1");
        
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
        shapeViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectionModel = shapeViewer.getSelectionModel();
        
        Image imageUpArrow = new Image(getClass().getResource("data/btnUpArrow.png").toExternalForm());
        btnUpArrow = new Button("Up", new ImageView(imageUpArrow));
        btnUpArrow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnUpArrow.setStyle("-fx-padding:0; ");
        btnUpArrow.setTooltip(new Tooltip("Moves selected shape backward."));
        
        Image imageDownArrow = new Image(getClass().getResource("data/btnDownArrow.png").toExternalForm());
        btnDownArrow = new Button("Down", new ImageView(imageDownArrow));
        btnDownArrow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnDownArrow.setStyle("-fx-padding:0; ");
        btnDownArrow.setTooltip(new Tooltip("Moves selected shape forward."));
        
        btnDelete = new Button("Delete");
        btnDelete.setTooltip(new Tooltip("Deletes current shape."));
        btnDelete.setMinWidth(toolBarWidth + spacing);
                
        btnReset = new Button("Reset");
        btnReset.setTooltip(new Tooltip("Removes all rotation and scaling from current shape."));
        btnReset.setMinWidth(toolBarWidth + spacing);
        
        btnCopy = new Button("Copy");
        btnCopy.setTooltip(new Tooltip("Creates an identical copy of currently selected shape."));
        btnCopy.setMinWidth(toolBarWidth + spacing);
        
        listControls.getChildren().addAll(btnUpArrow, btnDownArrow, strut, btnCopy, btnReset, btnDelete);
        listPanel.getChildren().addAll(listControls, shapeViewer);
        
        listControls.setAlignment(Pos.CENTER);
        listControls.setPadding(new Insets(0,spacing,0,0));
        listPanel.setMaxSize(controlBarWidth -7*spacing, controlBarWidth*3);
        shapeViewer.setPrefHeight(controlBarWidth*1.5);
        
        //Processing Canvas/////////////////////////////////////////////////////
        rootNode = new BorderPane();
        rootNode.setPadding(new Insets(0,8*spacing,0,0));
         
        final VBox controls = new VBox(spacing);
        final VBox modes = new VBox(spacing);      
        final TilePane toolPane = new TilePane();  

        controls.setPadding(new Insets(spacing, 2*spacing, spacing, spacing));
        controls.setMinWidth(controlBarWidth -4*spacing);
        controls.getChildren().addAll(canvasMenuButton, referencePane,gridMenuButton, colorPane,listPanel);
        modes.getChildren().addAll(drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        modes.setPadding(new Insets(spacing, 0,0,0));
        toolPane.setPadding(new Insets(0,spacing,0,spacing));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnArc, btnLine, btnCurve, btnPoly, btnPicture);
        toolPane.setPrefColumns(1);
        toolPane.setHgap(spacing);
        toolPane.setVgap(spacing);
        
        rootNode.setTop(mb);
        rootNode.setLeft(toolPane);
        rootNode.setRight(controls); 
    }    
}
