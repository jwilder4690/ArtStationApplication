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
import static processing.core.PApplet.sq;
import static processing.core.PApplet.sqrt;

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
    float verticalPadding = 0.05f;
    float verticalScreenShare = 1 - 2*verticalPadding;
    float horizontalPadding = 0.05f;
    float horizontalScreenShare = 1 - 2*horizontalPadding;
    boolean[] keys = new boolean[255];
    boolean controlKey = false;
    boolean handling = false;
    float canvasX;
    float canvasY;
    boolean shift = false;
    boolean alt = false;
    
    
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
                drawMode.fire();
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
            println(stage.getWidth(),stage.getHeight());
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
        return mySurface; 
    }
    
    @Override 
    public void draw(){
        //checkInput();
        background(55,55,55);
        drawCanvasArea();
        //drawFrames();
    }
       
    @Override
    public void mousePressed(){
        if(mouseOverCanvas()){
          if(activeMode == Mode.DRAW){
            pad.drawShape(canvasX, canvasY, activeTool);
          }
          else if(activeMode == Mode.EDIT){
          //???
          }
        }
    }
    
    @Override
    public void mouseReleased(){ 
        if(mouseOverCanvas()){
            if(activeMode == Mode.DRAW){
                pad.completeShape();
            }
            else if(activeMode == Mode.EDIT){
              handling = false;
            }
        }
     }

    @Override
    public void keyPressed(){
        println("Pressed a key");
        if(key == CODED){
            if(keyCode == SHIFT){
                shift = true;
                println("shift is true");
            }
            else if(keyCode == ALT){
                alt = true;
            }
        }
    }
    
    @Override
    public void keyReleased(){
        if(key == CODED){
            if(keyCode == SHIFT){
                shift = false;
            }
            else if(keyCode == ALT){
                alt = false;
            }
        }
    }
    
    boolean mouseOverCanvas(){
        if(canvasX >= 0 && canvasY >= 0 && canvasX <= pad.getHeight() && canvasY <= pad.getWidth()){
            return true;
        }
        else return false;
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
                        shapes.get(shapes.size() - 1).modify(mouse);
                    } break;
                case EDIT:
                    if(!handling){
                        shapes.get(currentShapeIndex).checkHandles(mouse);
                        if(!handling){ //redundant check is because the above function sets handling variable
                            if(shapes.get(currentShapeIndex).mouseOver(mouse)) shapes.get(currentShapeIndex).manipulate(mouse); 
                            else shapes.get(currentShapeIndex).changeRotation(mouse);
                        }
                    }
                    else{
                        shapes.get(currentShapeIndex).adjustActiveHandle(mouse);
                    } break;
            }
        }

        void drawShape(float x, float y, ShapeType type) {
            numberOfShapes++;
            currentShapeIndex = numberOfShapes - 1;
            switch (type) {
                case CIR:
                    shapes.add(new Circle(x, y, 50));
                    break;
                case REC:
                    shapes.add(new Rectangle(x, y, 50, CENT));
                    break;
                case TRI:
                    shapes.add(new Triangle(x, y, 50));
                    break;
                case LIN:
                    shapes.add(new Line(x, y, 50, 50));
                    break;
            }
        }

        void toggleSelectShape(boolean toggleOn) {
            if (toggleOn) {
                shapes.get(currentShapeIndex).select();
            } else {
                shapes.get(currentShapeIndex).deselect();
            }
        }

        void completeShape() {
            if(shapes.size() > 0){
                shapes.get(shapes.size()-1).finishShape();
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
     
     abstract class Shape{
        PVector pos;
        float rotation = 0;
        int paint = color(255,0,255);
        int editColor = color(255,255,0);
        float lineThickness = 1;
        boolean finished = false;
        boolean selected = false;
  
        Shape(float x, float y){
          pos = new PVector(x,y);
        }
        
        boolean getFinished(){
            return finished;
        }
        
        PVector getPosition(){
            return pos;
        }
        abstract void checkHandles(PVector mouse);
        abstract void adjustActiveHandle(PVector mouse);
        
        abstract void drawShape();

        abstract void modify(PVector mouse);
        
        abstract boolean mouseOver(PVector mouse);

        void manipulate(PVector mouse){
          pos.set(mouse);
        }
        
        void changeRotation(PVector mouse){
          PVector orientation = (pos.x < mouse.x) ? PVector.sub(pos,mouse): PVector.sub(mouse,pos);
          rotation = PVector.angleBetween(orientation, new PVector(0,1));
          if(shift){
              float leftover = rotation % QUARTER_PI;
              leftover = round(leftover);
              rotation = floor(rotation/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);
          }
        }

        void finishShape(){
          finished = true;
        }

        void select(){
          selected = true;
        }   

        void deselect(){
          selected = false;
        }
     }
     
     class Circle extends Shape{
        //float radius; //needed? Info contained in handle now
        Handle widthHandleR;
        Handle widthHandleL;
        Handle heightHandleT;
        Handle heightHandleB;
        Handle[] activeHandle = new Handle[2];

        Circle(float x, float y, float r){
          super(x,y);
          //radius = r;   
          widthHandleR = new Handle(r/2, new PVector(1,0));
          widthHandleL = new Handle(r/2, new PVector(-1,0));
          heightHandleB = new Handle(r/2, new PVector(0,1));
          heightHandleT = new Handle(r/2, new PVector(0,-1));
        }

        @Override 
        boolean mouseOver(PVector mouse){
            if(dist(pos.x, pos.y, mouse.x, mouse.y) < widthHandleL.getRadius()/2 || dist(pos.x, pos.y, mouse.x, mouse.y) < heightHandleT.getRadius()/2){
                return true;
            }
            return false;
        }
        
        @Override
        void drawShape(){
          fill(paint);
          if(lineThickness == 0){
            noStroke();
          }
          else{
            stroke(0,0,0);
            strokeWeight(lineThickness);
          }
          pushMatrix();
          translate(pos.x, pos.y);
          rotate(rotation);
          ellipse(0,0, widthHandleL.getRadius(), heightHandleT.getRadius());
          if(selected){
            noFill();
            strokeWeight(3);
            stroke(editColor);
            ellipse(0,0, widthHandleL.getRadius(), heightHandleT.getRadius());
            drawHandles();
          }
          popMatrix();
        }
        
        void drawHandles(){
            widthHandleL.drawHandle();
            float sx = screenX(widthHandleL.getX(), widthHandleL.getY());
            float sy =  screenY(widthHandleL.getX(), widthHandleL.getY());
                    println(sx +", " + sy);
            widthHandleL.setPos(sx, sy);
            widthHandleR.drawHandle();
            widthHandleR.setPos(screenX(widthHandleL.getX(), widthHandleL.getY()), screenY(widthHandleL.getX(), widthHandleL.getY()));
            heightHandleT.drawHandle();
            heightHandleT.setPos(screenX(heightHandleT.getX(), heightHandleT.getY()), screenY(heightHandleT.getX(), heightHandleT.getY()));
            heightHandleB.drawHandle();
            heightHandleB.setPos(screenX(heightHandleT.getX(), heightHandleT.getY()), screenY(heightHandleT.getX(), heightHandleT.getY()));
        }
        
        @Override
        void adjustActiveHandle(PVector mouse){
            activeHandle[0].setRadius(dist(pos.x, pos.y, mouse.x, mouse.y));  
            activeHandle[1].setRadius(dist(pos.x, pos.y, mouse.x, mouse.y)); 
        }

        @Override
        void modify(PVector mouse){
          float radius = dist(mouse.x, mouse.y, pos.x, pos.y);
          rotation = 0;
          widthHandleR.setRadius(radius);
          widthHandleL.setRadius(radius);
          heightHandleT.setRadius(radius);
          heightHandleB.setRadius(radius);
                       
          //PVector orientation = (pos.x < mouse.x) ? PVector.sub(pos,mouse): PVector.sub(mouse,pos);
          //rotation = PVector.angleBetween(orientation, new PVector(0,1));
        }
          
        @Override
        void checkHandles(PVector mouse){
            if(widthHandleL.overHandle(PVector.sub(mouse,pos)) || widthHandleR.overHandle(PVector.sub(mouse,pos))){
                widthHandleL.setRadius(dist(pos.x, pos.y, mouse.x, mouse.y));
                widthHandleR.setRadius(dist(pos.x, pos.y, mouse.x, mouse.y));
                activeHandle[0] = widthHandleL;
                activeHandle[1] = widthHandleR;
                handling = true;
            }
            if(heightHandleT.overHandle(PVector.sub(mouse,pos)) || heightHandleB.overHandle(PVector.sub(mouse,pos))){
                heightHandleT.setRadius(dist(pos.x, pos.y, mouse.x, mouse.y));
                heightHandleB.setRadius(dist(pos.x, pos.y, mouse.x, mouse.y));
                activeHandle[0] = heightHandleT;
                activeHandle[1] = heightHandleB;
                handling = true;
            }
        }     
     }
     
     class Rectangle extends Shape{
        final int CENT = 0;
        final int CORN = 4;
        float radius;
        float widthModifier = 1;
        float heightModifier = 1;
        PVector corner;
        int type;
  
        Rectangle(float a, float b, float c, int style){ 
          super(a,b);
          radius = c;
          type = style;
          corner = new PVector(a+1,b+1); //default corner, will not be displayed 
        }

        @Override 
        boolean mouseOver(PVector mouse){
            return false;
        }
        
        @Override
        void drawShape(){
          fill(paint);
          if(lineThickness == 0){
            noStroke();
          }
          else{
            strokeWeight(lineThickness);
          }
          pushMatrix();
          translate(pos.x, pos.y);
          rotate(rotation);
          if(type == CENT){
            rectMode(CENTER);
            rect(0,0, radius*widthModifier, radius*heightModifier);
            if(selected){
                noFill();
                strokeWeight(3);
                stroke(255,255, 0);
                rect(0,0, radius*widthModifier, radius*heightModifier);
              }
          }
          else if(type == CORN){
            rectMode(CORNERS);
            rect(0,0, corner.x, corner.y);
            if(selected){
                noFill();
                strokeWeight(3);
                stroke(255,255, 0);
                rect(0,0, corner.x, corner.y);
              }
          }
          popMatrix();
        }

        @Override
        void modify(PVector mouse){
          if(type == CENT) radius = 2*dist(mouse.x, mouse.y, pos.x, pos.y);
          else if(type == CORN) corner.set(mouse.x, mouse.y);
          PVector orientation = (pos.x < mouse.x) ? PVector.sub(pos,mouse): PVector.sub(mouse,pos);
          rotation = PVector.angleBetween(orientation, new PVector(0,1));
          if(shift){
              float leftover = rotation % QUARTER_PI;
              leftover = round(leftover);
              rotation = floor(rotation/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);
          }
        }
        
        @Override
        void checkHandles(PVector mouse){
         
        }
        
        @Override
        void adjustActiveHandle(PVector mouse){
            
        }
      }
     
     class Line extends Shape{
        PVector tail;

        Line(float x, float y, float x2, float y2){
          super(x,y);
          tail = new PVector(x2,y2);
        }

        @Override 
        boolean mouseOver(PVector mouse){
            return false;
        }
        
        @Override
        void drawShape(){
          fill(paint);
          strokeWeight(lineThickness);
          pushMatrix();
          translate(pos.x, pos.y);
          line(0,0, tail.x, tail.y);
          if(selected){
            noFill();
            strokeWeight(3);
            stroke(255,255, 0);
            line(0,0, tail.x, tail.y);
          }
          popMatrix();
        }

        @Override
        void modify(PVector mouse){
          tail.set(mouse.x, mouse.y);
          tail.sub(pos);
        }
        
        @Override
        void checkHandles(PVector mouse){
         
        }
        
        @Override
        void adjustActiveHandle(PVector mouse){
            
        }
      }
     
     class Triangle extends Shape{
        float altitude = 1;
        float side = 1;
        float heightModifier = 1;
        float leftModifier = 1;
        float rightModifier = 1;

        Triangle(float x, float y, float tall){
          super(x,y);
          altitude = tall;
          side = sqrt((float)(4.0/3.0)*sq(altitude));
        }

        @Override 
        boolean mouseOver(PVector mouse){
            return false;
        }
        
        @Override
        void drawShape(){
          fill(paint);
          if(lineThickness == 0){
            noStroke();
          }
          else{
            strokeWeight(lineThickness);
          }
          pushMatrix();
          translate(pos.x, pos.y);
          rotate(rotation);
          triangle(0, heightModifier*(-2*altitude/3), leftModifier*(-side/2), altitude/3, rightModifier*(side/2), altitude/3); 
          if(selected){
            noFill();
            strokeWeight(3);
            stroke(255,255, 0);
            triangle(0, heightModifier*(-2*altitude/3), leftModifier*(-side/2), altitude/3, rightModifier*(side/2), altitude/3); 
          }
          popMatrix();
        }

        @Override
        void modify(PVector mouse){
          altitude = 3*dist(pos.x, pos.y, mouse.x, mouse.y);
          side = sqrt((float)(4.0/3.0)*sq(altitude));
          PVector orientation = PVector.sub(pos,mouse);
          rotation = (pos.x < mouse.x) ? PVector.angleBetween(orientation, new PVector(0,1)):  PI + PVector.angleBetween(orientation, new PVector(0,-1));
          rotation -= radians(60);
          if(shift){
              float leftover = rotation % radians(30);
              leftover = round(leftover);
              rotation = floor(rotation/radians(30))*radians(30)+(leftover*radians(30));
          }
        }
        
        @Override
        void checkHandles(PVector mouse){
         
        }
        
        @Override
        void adjustActiveHandle(PVector mouse){
            
        }
      }
     
     class Handle{
         PVector screenPos;
         float modifier = 1;
         float radius;
         float size = 15;
         int paint = color(255,255,0);
         PVector offset;
         
         Handle(float r, PVector pos){
             radius = r;
             offset = pos;
        }
         
         float getX(){
             return radius*modifier*offset.x;
         }
         
         float getY(){
             return radius*modifier*offset.y;
         }
         
         void setPos(float x, float y){
             screenPos.set(x,y);
         }
         
         void setRadius(float r){
             radius = r;
         }
         
         boolean overHandle(PVector m){
             //println("Handle comparing: "+ m +" vs " + getX() +", "+ getY());
             return (dist(m.x, m.y, screenPos.x, screenPos.y) < size);
         }
         
         float getRadius(){
             return 2*radius*modifier;
         }
         
         void drawHandle(){
             fill(paint);
             strokeWeight(1);
             stroke(0,0,0);
             ellipse(getX(), getY(),size,size);
         }
     }
}
