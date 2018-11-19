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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import processing.core.*;
import processing.javafx.PSurfaceFX;
import javafx.scene.image.*;

/**
 *
 * @author wilder4690
 */
public class ArtStationApplication extends PApplet{
    
    enum ShapeType {CIR, REC, TRI, LIN}
    enum Mode {DRAW, EDIT}
    ShapeType activeTool = ShapeType.CIR;
    Mode activeMode = Mode.DRAW;
    CanvasArea pad = new CanvasArea(this,900,900);
    float scaleFactor;
    int toolbarWidth = 200;
    float verticalPadding = 0.05f;
    float verticalScreenShare = 1 - 2*verticalPadding;
    float horizontalPadding = 0.15f;
    float horizontalScreenShare = 1 - 2*horizontalPadding;
    boolean[] keys = new boolean[255];
    boolean controlKey = false;
    float canvasX;
    float canvasY;
    
    //GUI
    ToggleGroup tg;

    @Override
    public void settings(){
        size(1280,720, FX2D);
    }
    
    @Override
    protected PSurface initSurface(){
        PSurface surface = super.initSurface();

        final PSurfaceFX FXSurface = (PSurfaceFX) surface;
        final Canvas canvas = (Canvas) FXSurface.getNative(); // canvas is the processing drawing
        final Stage stage = (Stage) canvas.getScene().getWindow(); // stage is the window

        stage.setTitle("Processing/JavaFX Example");
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        
        tg = new ToggleGroup();
        
        Image imageCircle = new Image(getClass().getResource("data/btnCircle.png").toExternalForm());
        ToggleButton btnCircle = new ToggleButton("Circle", new ImageView(imageCircle));
        btnCircle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnCircle.setStyle("-fx-padding:0");
        btnCircle.setToggleGroup(tg);
        
        Image imageSquare = new Image(getClass().getResource("data/btnSquare.png").toExternalForm());
        ToggleButton btnSquare = new ToggleButton("Square", new ImageView(imageSquare));
        btnSquare.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnSquare.setStyle("-fx-padding:0");
        btnSquare.setToggleGroup(tg);
        
        Image imageTriangle = new Image(getClass().getResource("data/btnTriangle.png").toExternalForm());
        ToggleButton btnTriangle = new ToggleButton("Triangle", new ImageView(imageTriangle));
        btnTriangle.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnTriangle.setStyle("-fx-padding:0");
        btnTriangle.setToggleGroup(tg);
        
        Image imageLine = new Image(getClass().getResource("data/btnLine.png").toExternalForm());
        ToggleButton btnLine = new ToggleButton("Line", new ImageView(imageLine));
        btnLine.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        btnLine.setStyle("-fx-padding:0");
        btnLine.setToggleGroup(tg);
        
               
        final BorderPane rootNode = new BorderPane();
        final TilePane toolPane = new TilePane(2,2);
        toolPane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, new CornerRadii(0),new Insets(0))));
        toolPane.getChildren().addAll(btnCircle, btnSquare, btnTriangle, btnLine);
        //rootNode.setTop(mb);
        rootNode.setLeft(toolPane);
        rootNode.setCenter(canvas);
        final Scene newscene = new Scene(rootNode); // Create a scene from the elements
        
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            // Do whatever you want
            scaleCanvas((float)stage.getWidth(),(float)stage.getHeight());
            System.out.println("Width Changed. New scaleFactor: "+scaleFactor);   
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
             // Do whatever you want
             scaleCanvas((float)stage.getWidth(),(float)stage.getHeight());
             System.out.println("Height Changed. New scaleFactor: "+scaleFactor);    
        });
        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setScene(newscene); // Replace the stage's scene with our new one.
            }
        });
        return surface; 
    }
    
    @Override 
    public void draw(){
        //checkInput();
        background(55,55,55);
        drawCanvasArea();
        //drawFrames();
        //drawToolbar();
    }
    
    void drawCanvasArea(){
        //scaleCanvas();
        pushMatrix();
          translate(width*horizontalPadding +toolbarWidth, height*verticalPadding);
          scale(scaleFactor,scaleFactor);
          pad.drawCanvas(canvasX, canvasY);
          canvasX = (mouseX - screenX(0,0))/scaleFactor;
          canvasY = (mouseY - screenY(0,0))/scaleFactor; 
        popMatrix();
    }
    
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
         private PApplet sketch;
        final int CENT = 0;
        final int CORN = 4;
        int canvasWidth;
        int canvasHeight;
        //int background = sketch.color(255, 0, 0);
        int gridDensity = 10; //must be less than canvasWidth if int division is used
        float gridSpacing;
        boolean gridOn = true;
        //ArrayList<Shape> shapes = new ArrayList<Shape>();
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

        void drawCanvas(float mx, float my) {
            fill(255,255,255);
            strokeWeight(0);
            stroke(0, 0, 0);
            rect(0, 0, canvasWidth, canvasHeight);
//            if (mousePressed) {
//                update(mx, my);
//            }
//            for (int i = 0; i < shapes.size(); i++) {
//                shapes.get(i).drawShape();
//            }
            if (gridOn) {
                drawGrid();
            }
        }

//        void update(float x, float y) {
//            switch (activeMode) {
//                case DRAW:
//                    shapes.get(shapes.size() - 1).modify(x, y);
//                    break;
//                case EDIT:
//                    shapes.get(currentShapeIndex).manipulate(x, y);
//                    break;
//            }
//        }

//        void drawShape(float x, float y, ShapeType type) {
//            switch (type) {
//                default:
//                    numberOfShapes++;
//                    currentShapeIndex = numberOfShapes - 1;
//                case CIR:
//                    shapes.add(new Circle(x, y, 50));
//                    break;
//                case REC:
//                    shapes.add(new Rectangle(x, y, 50, CORN));
//                    break;
//                case TRI:
//                    shapes.add(new Triangle(x, y, 50));
//                    break;
//                case LIN:
//                    shapes.add(new Line(x, y, 50, 50));
//                    break;
//            }
//        }

//        void toggleSelectShape(boolean toggleOn) {
//            if (toggleOn) {
//                shapes.get(currentShapeIndex).select();
//            } else {
//                shapes.get(currentShapeIndex).deselect();
//            }
//        }

//        void completeShape() {
//            shapes.get(shapes.size() - 1).finishShape();
//        }

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
