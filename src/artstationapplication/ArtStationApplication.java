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
    boolean shift =false;
    boolean alt = false;
    boolean control = false;
    int toolBarWidth = 50;
    int controlBarWidth = 275;
    int spacing = 5;
    Region strut = new Region();
    CheckBox cbNoFill;
    CheckBox cbNoStroke;
    final int NONE = -777;
    int currentFillColor = color(255,255,255); 
    int currentStrokeColor = color(0,0,0);
    float currentStrokeWeight = 1;
    
    
    //GUI
    ToggleGroup toolGroup;
    ToggleGroup modeGroup;
    ListView<Shape> shapeViewer;
    ObservableList<Shape> shapes;
    int listIndex = -1;

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
                pad.toggleSelectShape(false);
                canvas.requestFocus();
            }
        });   
        
        editMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                activeMode = Mode.EDIT;
                pad.toggleSelectShape(true);
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
                pad.toggleSelectShape(false);
                canvas.requestFocus(); 
            }
        };
        
        btnCircle.setOnAction(ToolHandler);
        btnRectangle.setOnAction(ToolHandler);
        btnLine.setOnAction(ToolHandler);
        btnTriangle.setOnAction(ToolHandler);
        btnPoly.setOnAction(ToolHandler);
        
        //Fill & Stroke Pane////////////////////////////////////////////////////////
        final GridPane colorPane = new GridPane();
               
        final Label lblColor = new Label("Color");
        final Label lblFill = new Label("Fill:");
        final Label lblStroke = new Label("Stroke:");
        final Label lblWeight = new Label("Weight");
        final Label lblNone = new Label("None");
        
        final ColorPicker colorPickerFill = new ColorPicker(Color.WHITE);
        final ColorPicker colorPickerStroke = new ColorPicker(Color.BLACK);
        cbNoFill = new CheckBox();
        cbNoStroke = new CheckBox();
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
                    currentFillColor = NONE;
                    colorPickerFill.setDisable(true);
                }
                else{
                    currentFillColor = convertColorToInt(colorPickerFill.getValue());
                    colorPickerFill.setDisable(false);
                }
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setFillColor(currentFillColor);
                }
                canvas.requestFocus(); 
            }
        });
        
        cbNoStroke.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(cbNoStroke.isSelected()){
                    currentStrokeWeight = 0;
                    colorPickerStroke.setDisable(true);
                    weightSlider.setDisable(true);
                    weightTextField.setDisable(true);
                }
                else{
                    currentStrokeWeight = (float)convertStringToDouble(weightTextField.getText());
                    currentStrokeColor = convertColorToInt(colorPickerStroke.getValue());
                    colorPickerStroke.setDisable(false);
                    weightSlider.setDisable(false);
                    weightTextField.setDisable(false);
                }
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setStrokeWeight(currentStrokeWeight);
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerFill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                currentFillColor = convertColorToInt(colorPickerFill.getValue());
                if(activeMode == Mode.DRAW){     
                    //canvas.requestFocus(); 
                }
                else{
                    shapes.get(listIndex).setFillColor(currentFillColor);
                }
                canvas.requestFocus(); 
            }
        });
        
        colorPickerStroke.setOnAction(new EventHandler() {
            public void handle(Event t) {
                currentStrokeColor = convertColorToInt(colorPickerStroke.getValue());
                if(activeMode == Mode.DRAW){     
                    //canvas.requestFocus(); 
                }
                else{
                    shapes.get(listIndex).setStrokeColor(currentStrokeColor);
                }
                canvas.requestFocus();  
            }
        });
        
        weightSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                weightTextField.setText(Double.toString(newVal.floatValue()));
                currentStrokeWeight = (float)newVal.floatValue();
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
                currentStrokeWeight = (float)newValue;
                if(activeMode == Mode.EDIT){
                    shapes.get(listIndex).setStrokeWeight(currentStrokeWeight);
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
        shapes = FXCollections.observableArrayList();   
        ObservableList<Shape> shapeTypes = shapes;
        shapeViewer = new ListView<>(shapeTypes);
        
        shapeViewer.setMaxSize(controlBarWidth -7*spacing, controlBarWidth*5);
        shapeViewer.setPrefHeight(controlBarWidth*2);
        MultipleSelectionModel<Shape> selectionModel = shapeViewer.getSelectionModel();
        
        selectionModel.selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                editMode.setSelected(true);
                activeMode = Mode.EDIT;
                pad.deselectShape(listIndex);
                listIndex = (int)newVal;
                pad.selectShape(listIndex);
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
                if(activeMode == Mode.EDIT && !shapes.isEmpty()){
                    shapes.remove(listIndex);
                    listIndex = shapes.size()-1;
                }
            }
            else if(event.getCode() == KeyCode.SPACE){
                if(activeMode == Mode.DRAW){
                    editMode.setSelected(true);
                    pad.completeShape();  
                }
                else{
                    drawMode.setSelected(true);
                    activeMode = Mode.DRAW;
                    pad.toggleSelectShape(false);   
                }
            }
        });
        
        final VBox controls = new VBox(spacing);
        final VBox modes = new VBox(spacing);      
        final TilePane toolPane = new TilePane(2,2);
        
        //toolPane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(0),new Insets(0))));
        //strut.setPrefHeight(1);
        controls.setPadding(new Insets(spacing, 2*spacing, spacing, spacing));
        controls.getChildren().addAll(colorPane,shapeViewer);
        modes.getChildren().addAll(drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        modes.setPadding(new Insets(spacing, 0,0,0));
        toolPane.setPadding(new Insets(0,spacing,0,spacing));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnLine, btnPoly);
        toolPane.setPrefColumns(1);
        toolPane.setHgap(5);
        toolPane.setVgap(5);
        
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
        checkInput();
        background(55,55,55);
        drawCanvasArea();
        drawFrames();
    }
       
    @Override
    public void mousePressed(){
        //if(mouseButton == LEFT){
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
       // }
        if(mouseButton == RIGHT){
//            if(activeMode == Mode.EDIT){
//                activeMode = Mode.DRAW;
//                pad.toggleSelectShape(false);
//            }
            if(activeMode == Mode.DRAW){
               pad.completeShape();  
           }
        }
    }
    
    @Override
    public void mouseReleased(){ 
        //if(mouseButton == LEFT){
            if(mouseOverCanvas()){ //prevents calling complete shape without any shapes
                if(activeMode == Mode.DRAW){
                    if(activeTool == ShapeType.POL){
                        pad.completeVertex(canvasX, canvasY);
                    }
                    else{
                        pad.completeShape(); 
                    }
                }
            }
            if(activeMode == Mode.EDIT){
                subMode = Transformation.NON;
            }
       // }
//        else if(mouseButton == RIGHT){
//            
//        }
    }

    @Override
    public void keyPressed(){
        if(key < 255){
            keys[key] = true;
        }
        if(key == CODED){
            switch(keyCode){
                case SHIFT: shift = true; break;
                case ALT: alt = true; break;
                case CONTROL: control = true; break;                   
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
                case SHIFT: shift = false; break;
                case ALT: alt = false; break;
                case CONTROL: control = false; break;
            }
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
    
    void checkInput(){
        if(keys['z'] && control){
            //TODO: Undo function
        }
//        if(keys[' ']){
//            System.out.println("Filtered before getting herer?");
//            activeMode = Mode.DRAW;
//            pad.toggleSelectShape(false);
//            keys[' '] = false;
//        }
//        if(keys[DELETE]){
//            if(activeMode == Mode.EDIT && !shapes.isEmpty()){
//                shapes.remove(listIndex);
//                listIndex = shapes.size()-1;
//            }
//            keys[DELETE] = false;
//        }
    
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
        boolean modifying = false; //true while creating polygon so that user can click points for vertices

        CanvasArea(PApplet sketch, int w, int h) {
            this.sketch = sketch;
            canvasWidth = w;
            canvasHeight = h;
            gridSpacing = canvasWidth / (float)gridDensity;
        }

        int getWidth() {
            return canvasWidth;
        }

        int getHeight() {
            return canvasHeight;
        }
        
        Transformation checkForTransformation(PVector mouse){
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
            strokeWeight(0);
            stroke(0, 0, 0);
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
                    switch(subMode){
                        case SCA: shapes.get(listIndex).adjustActiveHandle(mouse); break;
                        case TRA: shapes.get(listIndex).manipulate(mouse); break;
                        case ROT: shapes.get(listIndex).changeRotation(mouse);
                            break;
                    } break;
            }
        }

        void drawShape(float x, float y, ShapeType type) {
            listIndex = shapeViewer.getItems().size();
            if(cbNoFill.isSelected()){
                currentFillColor = NONE;
            }
            if(cbNoStroke.isSelected()){
                currentStrokeColor = NONE;
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
        
        void selectShape(int index){
            if(index > -1 && index < shapes.size()){
                shapes.get(index).select();
            }
        }
        
        void deselectShape(int index){
            if(index > -1 && index < shapes.size()){
                shapes.get(index).deselect();
            }
        }

        void toggleSelectShape(boolean toggleOn) {
            if(shapes.size() > 0){
                if (toggleOn) {
                    shapes.get(listIndex).select();
                } else {
                    shapes.get(listIndex).deselect();
                }
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
                toggleSelectShape(true);
                modifying = false;
            }
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
