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
import javafx.stage.Stage;
import processing.core.*;
import processing.javafx.PSurfaceFX;
import javafx.scene.image.*;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.collections.*;
import javafx.beans.value.*;
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
    int controlBarWidth = 100;
    int spacing = 5;
    Region strut = new Region();

    
    
    //GUI
    ToggleGroup toolGroup;
    ToggleGroup modeGroup;
    ListView<String> shapeViewer;

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
        
        //Observable List///////////////////////////////////////////////////////
        ObservableList<String> shapeTypes = FXCollections.observableArrayList("Circle", "Rectangle", "Triangle", "Line", "Polygon");
        shapeViewer = new ListView<String>(shapeTypes);
        shapeViewer.setPrefSize(controlBarWidth, controlBarWidth*5);
        MultipleSelectionModel<String> selectionModel = shapeViewer.getSelectionModel();
        
        selectionModel.selectedItemProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal){
            
            }
        });
        
        //Processing Canvas/////////////////////////////////////////////////////
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        
        
        final BorderPane rootNode = new BorderPane();
        final VBox controls = new VBox(spacing);
        final VBox modes = new VBox(spacing);      
        final TilePane toolPane = new TilePane(2,2);
        
        //toolPane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(0),new Insets(0))));
        //strut.setPrefHeight(1);
        controls.getChildren().add(shapeViewer);
        modes.getChildren().addAll(new Separator(Orientation.HORIZONTAL),drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnLine, btnPoly);
        toolPane.setPrefColumns(1);
        toolPane.setHgap(5);
        toolPane.setVgap(5);
        
        rootNode.setMargin(toolPane, new Insets(5));
        rootNode.setTop(mb);
        rootNode.setLeft(toolPane);
        rootNode.setRight(controls);
        rootNode.setCenter(canvas);
                
        final Scene newscene = new Scene(rootNode); // Create a scene from the elements
        
        

        //Window Properties/////////////////////////////////////////////////////
        stage.setTitle("Art Station");
        stage.setMaximized(true);

        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            scaleCanvas((float)(stage.getWidth()-toolPane.getWidth()),(float)stage.getHeight());
            canvas.setWidth(stage.getWidth()-toolBarWidth - controlBarWidth);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
             // Do whatever you want
             scaleCanvas((float)(stage.getWidth()-toolPane.getWidth()),(float)stage.getHeight());
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
        if(mouseButton == RIGHT){
            pad.completeShape(); 
        }
    }
    
    @Override
    public void mouseReleased(){ 
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
    
    void checkInput(){
        if(keys['z'] && control){
            //TODO: Undo function
        }
        if(keys[' ']){
            activeMode = Mode.DRAW;
            pad.toggleSelectShape(false);
        }
    
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
        ArrayList<Shape> shapes = new ArrayList<>();
        int numberOfShapes = 0;
        int currentShapeIndex = 0;
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
            if(shapes.get(currentShapeIndex).checkHandles(mouse)){
                shapes.get(currentShapeIndex).setShift(shift);
                return Transformation.SCA;
            }
            else if(shapes.get(currentShapeIndex).mouseOver(mouse)) return Transformation.TRA;
            else{
                shapes.get(currentShapeIndex).setStartingRotation(mouse);
                shapes.get(currentShapeIndex).setShift(shift);
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
                        shapes.get(currentShapeIndex).setShift(shift);
                        shapes.get(shapes.size() - 1).modify(mouse);
                    } break;
                case EDIT:
                    switch(subMode){
                        case SCA: shapes.get(currentShapeIndex).adjustActiveHandle(mouse); break;
                        case TRA: shapes.get(currentShapeIndex).manipulate(mouse); break;
                        case ROT: shapes.get(currentShapeIndex).changeRotation(mouse);
                            break;
                    } break;
            }
        }

        void drawShape(float x, float y, ShapeType type) {
            if(!modifying){
                numberOfShapes++;
                currentShapeIndex = numberOfShapes - 1;
            }
            switch (type) {
                case CIR:
                    shapes.add(new Circle(sketch, x, y, 50));
                    break;
                case REC:
                    shapes.add(new Rectangle(sketch, x, y, 50, CENT));
                    break;
                case TRI:
                    shapes.add(new Triangle(sketch, x, y, 50));
                    break;
                case LIN:
                    shapes.add(new Line(sketch, x, y, 50, 50));
                    break;
                case POL:
                    if(!modifying){
                        modifying = true;
                        shapes.add(new Polygon(sketch, x, y));
                    }
                    break;
            }
        }

        void toggleSelectShape(boolean toggleOn) {
            if(shapes.size() > 0){
                if (toggleOn) {
                    shapes.get(currentShapeIndex).select();
                } else {
                    shapes.get(currentShapeIndex).deselect();
                }
            }
        }
        
        void completeVertex(float x, float y){
            shapes.get(shapes.size()-1).modify(new PVector(x,y));
        }

        void completeShape() {
            if(shapes.size() > 0){
                shapes.get(shapes.size()-1).finishShape();
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
