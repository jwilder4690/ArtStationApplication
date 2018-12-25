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


/**
 *
 * @author wilder4690
 */
public class ArtStationApplication extends PApplet{
    
    enum ShapeType {CIR, REC, TRI, LIN, POL}
    enum Mode {DRAW, EDIT}
    enum Transformation {ROT, SCA, TRA, NON}
    ShapeType activeTool = ShapeType.CIR;
    Mode activeMode = Mode.DRAW;
    Transformation subMode = Transformation.NON;
    CanvasArea pad = new CanvasArea(this,900,900);
    float scaleFactor;
    float verticalPadding = 0.05f;
    float verticalScreenShare = 1 - 2*verticalPadding;
    float horizontalPadding = 0.05f;
    float horizontalScreenShare = 1 - 2*horizontalPadding;
    boolean[] keys = new boolean[255];
    float canvasX;
    float canvasY;
    int activeButton;
    
    //GUI
    ToggleGroup toolGroup;
    ToggleGroup modeGroup;
    ListView<Shape> shapeViewer;
    ObservableList<Shape> shapes;
    final int NONE = -777; //Used for no fill color
    int listIndex = -1;
    int toolBarWidth = 50;
    int controlBarWidth = 275;
    int spacing = 5;

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
        
        //Dummy Menu Bar from Practice//////////////////////////////////////////
        MenuBar mb = new MenuBar();

        Menu fileMenu = new Menu("File");
        MenuItem open = new MenuItem("Open");
        MenuItem close = new MenuItem("Close");
        MenuItem save = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");
        fileMenu.getItems().addAll(open, close, save, new SeparatorMenuItem(), exit);
        mb.getMenus().add(fileMenu);

        Menu optionsMenu = new Menu("Options");

        Menu inDevicesMenu = new Menu("Input Devices");
        MenuItem keyboard = new MenuItem("Keyboard");
        MenuItem mouse = new MenuItem("Mouse");
        MenuItem touchscreen = new MenuItem("Touchscreen");
        inDevicesMenu.getItems().addAll(keyboard,mouse,touchscreen);
        optionsMenu.getItems().add(inDevicesMenu);

        Menu clockMenu = new Menu("Clock Style");
        MenuItem analog = new MenuItem("Analog");
        MenuItem digital = new MenuItem("Digital");
        clockMenu.getItems().addAll(analog,digital);
        optionsMenu.getItems().add(clockMenu);

        optionsMenu.getItems().add(new SeparatorMenuItem());

        MenuItem reset = new MenuItem("Reset");
        optionsMenu.getItems().add(reset);

        mb.getMenus().add(optionsMenu);

        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        helpMenu.getItems().add(about);

        mb.getMenus().add(helpMenu);

        EventHandler<ActionEvent> MenuHandler = new EventHandler<ActionEvent>(){ 
            public void handle(ActionEvent ae){ 
                String name = ((MenuItem)ae.getTarget()).getText();
                //all menu Handling done here
                if(name.equals("Exit")) Platform.exit();

                //response.setText(name + " selected.");
            }
        };
        
        open.setOnAction(MenuHandler);
        close.setOnAction(MenuHandler);
        save.setOnAction(MenuHandler);
        exit.setOnAction(MenuHandler);
        keyboard.setOnAction(MenuHandler);
        mouse.setOnAction(MenuHandler);
        touchscreen.setOnAction(MenuHandler);
        analog.setOnAction(MenuHandler);
        digital.setOnAction(MenuHandler);
        reset.setOnAction(MenuHandler);
        about.setOnAction(MenuHandler);
        
        
        //Mode buttons//////////////////////////////////////////////////////////
        modeGroup = new ToggleGroup();
        
        RadioButton drawMode = new RadioButton("Draw");
        drawMode.setToggleGroup(modeGroup);
        
        RadioButton editMode = new RadioButton("Edit");
        editMode.setToggleGroup(modeGroup);
                
        drawMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                activeMode = Mode.DRAW;
                //pad.toggleSelectShape(false);
                canvas.requestFocus();
            }
        });   
        
        editMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                activeMode = Mode.EDIT;
                //pad.toggleSelectShape(true);
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
        btnRectangle.setTooltip(new Tooltip("Click in center, drag to size. Hold SHIFT to snap rotation to 45 degree increments. "));
        
        Image imageTriangle = new Image(getClass().getResource("data/btnTriangle.png").toExternalForm());
        ToggleButton btnTriangle = new ToggleButton("Triangle", new ImageView(imageTriangle));
        btnTriangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnTriangle.setStyle("-fx-padding:0");
        btnTriangle.setToggleGroup(toolGroup);
        btnTriangle.setTooltip(new Tooltip("Click in center, drag to size."));
        
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
        btnPoly.setTooltip(new Tooltip("Single click for each vertex, SPACE to complete."));
        
        EventHandler<ActionEvent> ToolHandler = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                String name = ((ToggleButton)ae.getTarget()).getText();
                //all tool handling done here
                if(name.equals("Circle")){
                    activeTool = ShapeType.CIR;
                }
                else if(name.equals("Rectangle")){
                    activeTool = ShapeType.REC;
                }
                else if(name.equals("Triangle")){
                    activeTool = ShapeType.TRI;
                }
                else if(name.equals("Line")){
                    activeTool = ShapeType.LIN;
                }
                else if(name.equals("Poly")){
                    activeTool = ShapeType.POL;
                }
                drawMode.setSelected(true);
                activeMode = Mode.DRAW;
                //pad.toggleSelectShape(false);
                canvas.requestFocus(); 
            }
        };
        
        btnCircle.setOnAction(ToolHandler);
        btnRectangle.setOnAction(ToolHandler);
        btnLine.setOnAction(ToolHandler);
        btnTriangle.setOnAction(ToolHandler);
        btnPoly.setOnAction(ToolHandler);
        
        //Grid Menu///////////////////////////////////////////////////////////
        int gridMax = 100;
        final VBox canvasPane = new VBox();
        final MenuButton gridMenuButton = new MenuButton("Grid Options");
        final CustomMenuItem gridMenu = new CustomMenuItem(canvasPane);
        gridMenuButton.getItems().add(gridMenu);
        final GridPane gridSettings = new GridPane();
        final Label lblGrid = new Label("Grid");
        final Label lblGridOn = new Label("On");
        final Label lblGridSnap = new Label("Snap");
        final TextField gridTextField = new TextField("10");
        final CheckBox cbGridOn = new CheckBox();
        final CheckBox cbGridSnap = new CheckBox();
        Slider gridSlider = new Slider(0, gridMax, 10);


        canvasPane.setMaxWidth(controlBarWidth -7*spacing);
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
        
        //Lamba? wtf this is so much more intuitive
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
        
        //maybe... get User Input
        canvasPane.setOnMouseExited(e -> canvas.requestFocus());
        
        gridSettings.add(lblGrid, 0,0);
        gridSettings.add(lblGridOn, 1, 0);
        gridSettings.add(lblGridSnap, 2,0);
        gridSettings.add(gridTextField, 0,1);
        gridSettings.add(cbGridOn, 1,1);
        gridSettings.add(cbGridSnap, 2,1);
        canvasPane.getChildren().addAll(gridSettings, gridSlider);

        
        //Fill & Stroke Pane////////////////////////////////////////////////////
        final GridPane colorPane = new GridPane();
               
        final Label lblColor = new Label("Color");
        final Label lblFill = new Label("Fill:");
        final Label lblStroke = new Label("Stroke:");
        final Label lblWeight = new Label("Weight");
        final Label lblNone = new Label("None");
        
        final ColorPicker colorPickerFill = new ColorPicker(Color.WHITE);
        final ColorPicker colorPickerStroke = new ColorPicker(Color.BLACK);
        final CheckBox cbNoFill = new CheckBox();
        final CheckBox cbNoStroke = new CheckBox();
        final Slider weightSlider = new Slider(0, 10, 1);
        final TextField weightTextField = new TextField("1");
        
        colorPane.setVgap(spacing/2);
        colorPane.setHgap(spacing);
        colorPane.setHalignment(lblFill, HPos.RIGHT);
        colorPane.setHalignment(lblColor, HPos.CENTER);
        colorPane.setHalignment(cbNoStroke, HPos.CENTER);
        colorPane.setHalignment(cbNoFill, HPos.CENTER);
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
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setFillColor(pad.getCurrentFillColor());
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
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setStrokeWeight(pad.getCurrentStrokeWeight());
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerFill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentFillColor(convertColorToInt(colorPickerFill.getValue()));
                if(activeMode == Mode.DRAW){     
                    //canvas.requestFocus(); 
                }
                else{
                    shapes.get(listIndex).setFillColor(convertColorToInt(colorPickerFill.getValue()));
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerStroke.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentStrokeColor(convertColorToInt(colorPickerStroke.getValue()));
                if(activeMode == Mode.DRAW){     
                    //canvas.requestFocus(); 
                }
                else{
                    shapes.get(listIndex).setStrokeColor(convertColorToInt(colorPickerStroke.getValue()));
                }
                canvas.requestFocus();  
            }
        });
        
        weightSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                weightTextField.setText(Double.toString(newVal.floatValue()));
                pad.setCurrentStrokeWeight((float)newVal.floatValue());
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setStrokeWeight(newVal.floatValue());
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
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setStrokeWeight((float)newValue);
                }
            }
        });

        colorPane.setMaxWidth(controlBarWidth -7*spacing);
        colorPane.add(lblColor, 1,0);
        colorPane.add(lblNone, 2,0);
        colorPane.add(lblFill, 0,1);
        colorPane.add(colorPickerFill, 1,1);
        colorPane.add(cbNoFill, 2,1);
        colorPane.add(lblStroke, 0,2);
        colorPane.add(colorPickerStroke, 1,2);
        colorPane.add(cbNoStroke, 2,2);
        colorPane.add(lblWeight, 0, 3);
        colorPane.add(weightSlider, 1, 3);
        colorPane.add(weightTextField, 2,3);
        
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
        btnUpArrow.setTooltip(new Tooltip("Click to move selected shape backward."));
        
        Image imageDownArrow = new Image(getClass().getResource("data/btnDownArrow.png").toExternalForm());
        Button btnDownArrow = new Button("Down", new ImageView(imageDownArrow));
        btnDownArrow.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnDownArrow.setStyle("-fx-padding:0; ");
        btnDownArrow.setTooltip(new Tooltip("Click to move selected shape forward."));
        
        Button btnDelete = new Button("Delete");
        Button btnReset = new Button("Reset");
        
        btnUpArrow.setOnAction(event -> swapElements(listIndex, listIndex-1));
        btnDownArrow.setOnAction(event -> swapElements(listIndex, listIndex+1));
        
        btnDelete.setOnAction(event -> deleteShape());
        btnReset.setOnAction(event -> shapes.get(listIndex).reset());

        
        listControls.getChildren().addAll(btnUpArrow, btnDownArrow, strut, btnReset, btnDelete);
        listPanel.getChildren().addAll(listControls, shapeViewer);
        
        //shapeViewer.setMaxSize(controlBarWidth -7*spacing, controlBarWidth*5);
        listControls.setAlignment(Pos.CENTER);
        btnDelete.setMinWidth(toolBarWidth + spacing);
        btnReset.setMinWidth(toolBarWidth + spacing);
        listControls.setPadding(new Insets(0,spacing,0,0));
        listPanel.setMaxSize(controlBarWidth -7*spacing, controlBarWidth*3);
        //listControls.setPrefColumns(1);
        shapeViewer.setPrefHeight(controlBarWidth*1.5);
        MultipleSelectionModel<Shape> selectionModel = shapeViewer.getSelectionModel();
        
        selectionModel.selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                editMode.setSelected(true);
                activeMode = Mode.EDIT;
                //pad.deselectShape(listIndex);
                listIndex = (int)newVal;
                //pad.selectShape(listIndex);
            }
        });
        
        //Processing Canvas/////////////////////////////////////////////////////
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        
        
        final BorderPane rootNode = new BorderPane();
        rootNode.setPadding(new Insets(0,8*spacing,0,0));
        
        //Key Events for full scene
        rootNode.addEventFilter(KeyEvent.KEY_RELEASED, event->{
            if(event.getCode() == KeyCode.DELETE){
                deleteShape();
            }
            else if(event.getCode() == KeyCode.ALT){
                
            }
            else if(event.getCode() == KeyCode.SPACE){
                if(activeMode == Mode.DRAW){
                    editMode.setSelected(true);
                    pad.completeShape();  
                }
                else{
                    drawMode.setSelected(true);
                    activeMode = Mode.DRAW;
                    //pad.toggleSelectShape(false);   
                }
            }
        });
        
        final VBox controls = new VBox(spacing);
        final VBox modes = new VBox(spacing);      
        final TilePane toolPane = new TilePane();
        

        controls.setPadding(new Insets(spacing, 2*spacing, spacing, spacing));
        controls.getChildren().addAll(gridMenuButton, colorPane,listPanel);
        modes.getChildren().addAll(drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        modes.setPadding(new Insets(spacing, 0,0,0));
        toolPane.setPadding(new Insets(0,spacing,0,spacing));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnLine, btnPoly);
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

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            scaleCanvas((float)(stage.getWidth()-toolBarWidth - controlBarWidth),(float)(stage.getHeight() - 2*mb.getHeight()));
            canvas.setWidth(stage.getWidth()-toolBarWidth - controlBarWidth);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
             scaleCanvas((float)(stage.getWidth()-toolBarWidth - controlBarWidth),(float)(stage.getHeight() - 2*mb.getHeight()));
             //canvas.setHeight(stage.getHeight()-toolBarWidth - controlBarWidth);
        });
        
        
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
    }
       
    @Override
    public void mousePressed(){
        activeButton = mouseButton; //stores a copy because mouseButton drops value before triggering mouseReleased/mouseClicked
        if(mouseButton == LEFT){
            if(mouseOverCanvas()){
              if(activeMode == Mode.DRAW){
                pad.drawShape(canvasX, canvasY, activeTool);
              }
              else if(activeMode == Mode.EDIT){
                if(subMode == Transformation.NON){
                    subMode = pad.checkForTransformation(new PVector(canvasX, canvasY));
                }
              }
            }
        }
    }
    
        
    @Override
    //Check mouse LEFT vs RIGHT against "activeButton" because mouseButton variable drops its value before these checks run
    public void mouseReleased(){ 
        if(activeButton == LEFT){
            if(activeMode == Mode.DRAW){
                if(activeTool == ShapeType.POL){
                    pad.completeVertex(canvasX, canvasY);
                }
                else{
                    pad.completeShape(); 
                }
            }
        }
        else if(activeButton == RIGHT){
            if(activeMode == Mode.DRAW){
               pad.completeShape();  
           }
            else{  
                activeMode = Mode.DRAW;
                //pad.toggleSelectShape(false);  
            }
        }
        if(activeMode == Mode.EDIT){
            subMode = Transformation.NON;
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
    
    void swapElements(int currentIndex, int newIndex){
        if(newIndex < 0 || newIndex == shapes.size()) return;
        Collections.swap(shapes, currentIndex, newIndex);
        shapeViewer.getSelectionModel().select(newIndex);
    }
    
    void deleteShape(){
        if(activeMode == Mode.EDIT && !shapes.isEmpty()){
            shapes.remove(listIndex);
            listIndex = shapes.size() - 1;
            shapeViewer.getSelectionModel().select(listIndex);
            if(listIndex < 0) listIndex = 0;
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
        return (canvasX >= 0 && canvasY >= 0 && canvasX <= pad.getHeight() && canvasY <= pad.getWidth());
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
    
    public static void main(String[] args) {
        String[] processingArgs = {"Art Station"};
        ArtStationApplication app = new ArtStationApplication();
        PApplet.runSketch(processingArgs, app);
    }
    
    class CanvasArea {
        final private PApplet sketch;
        final boolean CENT = true;
        final boolean CORN = false;
        int canvasWidth;
        int canvasHeight;
        int background = color(245, 245, 245);
        int gridDensity = 10; //must be less than canvasWidth if int division is used
        float gridSpacing;
        boolean gridOn = true;
        boolean gridSnapOn = false;
        boolean modifying = false; //true while creating polygon so that user can click points for vertices
        boolean shift = false;
        boolean alt = false;
        boolean control = false;
        int currentFillColor = color(255,255,255); 
        int currentStrokeColor = color(0,0,0);
        float currentStrokeWeight = 1;

        CanvasArea(PApplet sketch, int w, int h) {
            this.sketch = sketch;
            canvasWidth = w;
            canvasHeight = h;
            gridSpacing = canvasWidth / (float)gridDensity;
        }
        
        void setShift(boolean val){
            shift = val;
        }
        
        void setAlt(boolean val){
            alt = val;
        }
        
        void setControl(boolean val){
            control = val;
        }

        int getWidth() {
            return canvasWidth;
        }

        int getHeight() {
            return canvasHeight;
        }
        
        void setCurrentFillColor(int color){
            currentFillColor = color;
        }
        
        void setCurrentStrokeColor(int color){
            currentStrokeColor = color;
        }
        
        void setCurrentStrokeWeight(float weight){
            currentStrokeWeight = weight;
        }
        
        int getCurrentFillColor(){
            return currentFillColor;
        }
        
        float getCurrentStrokeWeight(){
            return currentStrokeWeight;
        }
        
        Transformation checkForTransformation(PVector mouse){
            if(shapes.isEmpty()) return Transformation.NON;
            if(shapes.get(listIndex).checkHandles(mouse)){
                shapes.get(listIndex).setShift(shift);
                return Transformation.SCA;
            }
            else if(shapes.get(listIndex).mouseOver(mouse)) return Transformation.TRA;
            else{
                shapes.get(listIndex).setStartingRotation(mouse);
                shapes.get(listIndex).setShift(shift);
                return Transformation.ROT;
            }
        }

        void drawCanvas(float mx, float my) {
            rectMode(CORNERS);
            fill(background);
            noStroke();
            rect(0, 0, canvasWidth, canvasHeight);
            if (mousePressed && shapes.size() > 0){
                if(modifying){
                    ellipse(mx, my, 10, 10);
                }
                else{
                    update(new PVector(mx, my));
                }
            }
            for (int i = 0; i < shapes.size(); i++) {
                shapes.get(i).drawShape();
            }
            if(activeMode == Mode.EDIT){
                if(!shapes.isEmpty()) shapes.get(listIndex).drawSelected();
            }
            if (gridOn) {
                drawGrid();
            }
        }

        void update(PVector mouse) {
            switch (activeMode) {
                case DRAW:
                    if(!shapes.get(shapes.size() - 1).getFinished()){
                        shapes.get(listIndex).setShift(shift);
                        shapes.get(shapes.size() - 1).modify(mouse);
                    } break;
                case EDIT:
                    shapes.get(listIndex).setShift(shift);
                    switch(subMode){
                        case SCA: shapes.get(listIndex).adjustActiveHandle(mouse); break;
                        case TRA: 
                            if(gridSnapOn)shapes.get(listIndex).manipulate(snapToGrid(mouse));
                            else shapes.get(listIndex).manipulate(mouse);
                            break;
                        case ROT: shapes.get(listIndex).changeRotation(mouse); break;
                    } break;
            }
        }

        void drawShape(float x, float y, ShapeType type) {
            listIndex = shapeViewer.getItems().size();
            if(gridSnapOn){
                x = snapToGrid(x);
                y = snapToGrid(y);
            }
            switch (type) {
                case CIR:
                    shapes.add(new Circle(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
                    break;
                case REC:
                    shapes.add(new Rectangle(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex, CENT));
                    break;
                case TRI:
                    shapes.add(new Triangle(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
                    break;
                case LIN:
                    shapes.add(new Line(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
                    break;
                case POL:
                    if(!modifying){
                        modifying = true;
                        shapes.add(new Polygon(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y,listIndex));
                    }
                    break;
            }
        }
        
        
        void completeVertex(float x, float y){
            shapes.get(shapes.size()-1).modify(new PVector(x,y));
        }

        void completeShape() {
            if(shapes.size() > 0){
                shapes.get(shapes.size()-1).finishShape();
                listIndex = shapes.size()-1;
                activeMode = Mode.EDIT;
                //toggleSelectShape(true);
                modifying = false;
            }
        }
        
        void toggleSnap(boolean flip){
            gridSnapOn = flip;
        }
        
        void toggleGrid(boolean flip){
            gridOn = flip;
        }
        
        void setGridDensity(int newDensity){
            gridDensity = newDensity;
            gridSpacing = canvasWidth / (float)gridDensity;
        }
        
        float snapToGrid(float point){
           float newPoint = round(point/gridSpacing);
           newPoint = newPoint*gridSpacing;
           return newPoint;
        }
        
        PVector snapToGrid(PVector mouse){
            return new PVector(snapToGrid(mouse.x), snapToGrid(mouse.y));
        }

        void drawGrid() {
            strokeWeight(1);
            stroke(155, 155, 155);
            for (int i = 1; i < gridDensity; i++) {
                line(i * gridSpacing, 0, i * gridSpacing, canvasHeight);
            }
            int i = 1;
            while (i * gridSpacing < canvasHeight) {
                line(0, i * gridSpacing, canvasWidth, i * gridSpacing);
                i++;
            }
        }
    }
}
