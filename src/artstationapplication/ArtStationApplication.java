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


/**
 *
 * @author wilder4690
 */
public class ArtStationApplication extends PApplet{
    
    enum ShapeType {CIR, REC, TRI, LIN}
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

    
    
    //GUI
    ToggleGroup toolGroup;
    ToggleGroup modeGroup;

    @Override
    public void settings(){
        size(displayWidth,displayHeight, FX2D);
        //size(1280,720, FX2D);
    }
    
    @Override
    protected PSurface initSurface(){
        PSurface mySurface = super.initSurface();

        final PSurfaceFX FXSurface = (PSurfaceFX) mySurface;
        final Canvas canvas = (Canvas) FXSurface.getNative(); // canvas is the processing drawing
        final Stage stage = (Stage) canvas.getScene().getWindow(); // stage is the window

        stage.setTitle("Processing/JavaFX Example");
        stage.setMaximized(true);
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        
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
                //drawMode.fire() did nothing not sure why
                activeMode = Mode.DRAW;
                pad.toggleSelectShape(false);
                canvas.requestFocus();
            }
        };
        
        btnCircle.setOnAction(ToolHandler);
        btnRectangle.setOnAction(ToolHandler);
        btnLine.setOnAction(ToolHandler);
        btnTriangle.setOnAction(ToolHandler);
        
        final VBox modes = new VBox(5);      
        final BorderPane rootNode = new BorderPane();
        final TilePane toolPane = new TilePane(2,2);
        //toolPane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(0),new Insets(0))));
        modes.getChildren().addAll(drawMode, editMode, new Separator(Orientation.HORIZONTAL));
        toolPane.getChildren().addAll(modes, btnCircle, btnRectangle, btnTriangle, btnLine);
        toolPane.setPrefColumns(1);
        toolPane.setHgap(5);
        toolPane.setVgap(5);
        //rootNode.setTop(mb);
        rootNode.setLeft(toolPane);
        rootNode.setMargin(toolPane, new Insets(5));
        rootNode.setCenter(canvas);
        final Scene newscene = new Scene(rootNode); // Create a scene from the elements
        
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            scaleCanvas((float)(stage.getWidth()-toolPane.getWidth()),(float)stage.getHeight());
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
             // Do whatever you want
             scaleCanvas((float)(stage.getWidth()-toolPane.getWidth()),(float)stage.getHeight());
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
    
    @Override
    public void mouseReleased(){ 
        if(mouseOverCanvas()){ //prevents calling complete shape without any shapes
            if(activeMode == Mode.DRAW){
                pad.completeShape(); //this code doesn't appear to be doing anything any more
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
            if(keyCode == SHIFT){
                shift = true;
            }
            else if(keyCode == ALT){
                alt = true;
            }
            else if(keyCode == CONTROL){
                control = true;
            }
        }
    }
    
    @Override
    public void keyReleased(){
        if(key < 255){
            keys[key] = false;
        }
        if(key == CODED){
            if(keyCode == SHIFT){
                shift = false;
            }
            else if(keyCode == ALT){
                alt = false;
            }
            else if(keyCode == CONTROL){
                control = false;
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
                update(new PVector(mx, my));
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
            numberOfShapes++;
            currentShapeIndex = numberOfShapes - 1;
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

        void completeShape() {
            if(shapes.size() > 0){
                shapes.get(shapes.size()-1).finishShape();
            }
            activeMode = Mode.EDIT;
            pad.toggleSelectShape(true);
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
