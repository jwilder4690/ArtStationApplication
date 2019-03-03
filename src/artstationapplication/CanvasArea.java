/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import javafx.collections.ObservableList;
import processing.core.*;


/**
 *
 * @author wilder4690
 */
 
    class CanvasArea {
        final private PApplet sketch;
        ObservableList<Shape> shapes;
        ChangeList tasks;
        enum Mode {DRAW, EDIT}
        ShapeType activeTool = ShapeType.CIR;
        Mode activeMode = Mode.DRAW;
        Transformation subMode = Transformation.NON; 
        PImage referenceImage;
        boolean referenceOn = false;
        int backgroundColor = -263191;
        int canvasWidth;
        int canvasHeight;
        int gridDensity = 10; //must be less than canvasWidth if int division is used
        float gridSpacing;
        boolean gridOn = true;
        boolean gridSnapOn = false;
        boolean modifying = false; //true while creating polygon so that user can click points for vertices
        boolean shift = false;
        boolean alt = false;
        boolean control = false;
        int currentFillColor = -1;
        int currentStrokeColor = 0;
        float currentStrokeWeight = 1;
        int listIndex = -1;
        int spacing = 5; //try to remove
        String outDialog;

        CanvasArea(PApplet sketch, int w, int h, ObservableList inList, ChangeList inTasks) {
            this.sketch = sketch;
            canvasWidth = w;
            canvasHeight = h;
            gridSpacing = canvasWidth / (float)gridDensity;
            shapes = inList;
            tasks = inTasks;
        }
        
        void setActiveTool(ShapeType tool){
            activeTool = tool;
        }
        
        boolean isEditMode(){
            return activeMode == Mode.EDIT; 
        }
        
        boolean isDrawMode(){
            return activeMode == Mode.DRAW;
        }
        
        void setWidth(int wide){
            canvasWidth = wide;
        }
        
        void setHeight(int tall){
            canvasHeight = tall;
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
        
        void setBackgroundColor(int color){
            backgroundColor = color;
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
        
        int getBackgroundColor(){
            return backgroundColor;
        }
        
        int getGridDensity(){
            return gridDensity;
        }
        
        //Uses string information loaded from save file to recreate shapes
        void loadShape(String shapeType, String[] shapeInfo){
            switch(shapeType){
                case "Circle": shapes.add(new Circle(sketch, shapeInfo)); break;
                case "Bezier": shapes.add(new Bezier(sketch, shapeInfo)); break;
                case "Rectangle": shapes.add(new Rectangle(sketch, shapeInfo)); break;
                case "Line": shapes.add(new Line(sketch, shapeInfo)); break;
                case "Triangle": shapes.add(new Triangle(sketch, shapeInfo)); break;
                case "Polygon": shapes.add(new Polygon(sketch, shapeInfo)); break;
                case "Picture": shapes.add(new Picture(sketch, shapeInfo)); break;
            }
            listIndex = shapes.size()-1;
            shapes.get(listIndex).finishShape();
        }
        
        void loadReferenceImage(PImage loaded){
            referenceImage = loaded;
            referenceOn = true;
        }
        
        //Checks if mouse is over handles, over shape, or over nothing and sets 
        //transformation accordingly. //TODO: Add cursor designator for scaling,
        //rotating, and translating?
        void checkForTransformation(PVector mouse){
            if(shapes.isEmpty()) subMode =  Transformation.NON;
            else if(shapes.get(listIndex).checkHandles(mouse)){
                shapes.get(listIndex).setShift(shift);
                tasks.push(new Change(Transformation.SCA, listIndex, shapes.get(listIndex).getResetFloats()));
                subMode = Transformation.SCA;
            }
            else if(shapes.get(listIndex).mouseOver(mouse)){
                tasks.push(new Change(Transformation.TRA, listIndex, shapes.get(listIndex).getPositionFloats()));
                subMode = Transformation.TRA;
            }
            else{
                shapes.get(listIndex).setStartingRotation(mouse);
                shapes.get(listIndex).setShift(shift);
                tasks.push(new Change(Transformation.ROT, listIndex, shapes.get(listIndex).rotation));
                subMode = Transformation.ROT;
            }
        }

        //Draws canvas area, updates shapes based on mouse input, then draws shapes to screen.
        void drawCanvas(float mouseX, float mouseY) {
            drawBackground();
            if (sketch.mousePressed && shapes.size() > 0){
                if(modifying){ //draws dummy ellipse to represent polygon vertex. 
                    sketch.ellipse(mouseX, mouseY, 10, 10);
                }
                else{
                    update(new PVector(mouseX, mouseY));
                }
            }
            if(!shapes.isEmpty()){
                for (int i = 0; i < shapes.size(); i++) {
                    shapes.get(i).drawShape();
                }
                if(activeMode == Mode.EDIT){ //draws selection highlight around current shape.
                    shapes.get(listIndex).drawSelected();
                }
            }
            if (gridOn) {
                drawGrid();
            }
        }
        
        void drawBackground(){
            sketch.rectMode(sketch.CORNERS);
            sketch.fill(backgroundColor);
            sketch.noStroke();
            sketch.rect(0, 0, canvasWidth, canvasHeight);
            if(referenceOn && referenceImage != null) sketch.image(referenceImage, 0, 0);
        }
        
        void drawGrid() {
            sketch.strokeWeight(1);
            sketch.stroke(155, 155, 155);
            for (int i = 1; i < gridDensity; i++) {
                sketch.line(i * gridSpacing, 0, i * gridSpacing, canvasHeight);
            }
            int i = 1;
            while (i * gridSpacing < canvasHeight) {
                sketch.line(0, i * gridSpacing, canvasWidth, i * gridSpacing);
                i++;
            }
        }
                
        /*
            This method is only called when the mouse is being pressed. If draw mode
            is active, the shape currently being draw is adjusted via modify. If edit
            mode is active, the shape is updated based on the current transformation. 
        */
        void update(PVector mouse) {
            switch (activeMode) {
                case DRAW:
                    if(!shapes.get(shapes.size() - 1).getFinished()){
                        shapes.get(listIndex).setShift(shift);
                        shapes.get(listIndex).setAlt(alt);
                        if(gridOn && gridSnapOn && activeTool == ShapeType.LIN) {
                            shapes.get(shapes.size() - 1).modify(snapToGrid(mouse));
                        }
                        else shapes.get(shapes.size() - 1).modify(mouse);
                    } break;
                case EDIT:
                    shapes.get(listIndex).setShift(shift);
                    switch(subMode){
                        case SCA:
                            if(gridOn && gridSnapOn && (activeTool == ShapeType.POL ||  activeTool == ShapeType.LIN || activeTool == ShapeType.CUR)){
                                shapes.get(listIndex).adjustActiveHandle(snapToGrid(mouse));
                            }
                            else shapes.get(listIndex).adjustActiveHandle(mouse);
                            break;
                        case TRA: 
                            if(gridSnapOn && gridOn) shapes.get(listIndex).manipulate(snapToGrid(mouse));
                            else shapes.get(listIndex).manipulate(mouse);
                            break;
                        case ROT: shapes.get(listIndex).changeRotation(mouse); break;
                    } break;
            }
        }

        //Adds new shape to shape list based on the currently active tool
        void createShape(float x, float y) {
            if(!modifying){
                listIndex = shapes.size();
                tasks.push(new Change(Transformation.ADD, listIndex, 0));
            }
            if(gridSnapOn && gridOn){
                x = snapToGrid(x);
                y = snapToGrid(y);
            }
            switch (activeTool) {
                case CIR:
                    shapes.add(new Circle(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
                    break;
                case REC:
                    shapes.add(new Rectangle(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
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
                    }break;
                case CUR: 
                    shapes.add(new Bezier(sketch, currentFillColor, currentStrokeColor, currentStrokeWeight, x, y, listIndex));
                    break;
            }
        }
        
        void createPicture(String location){
            shapes.add(new Picture(sketch, location, currentStrokeColor, currentStrokeWeight, 0, 0, listIndex));
        }
        
        boolean overOrigin(PVector mouse){
            return (mouse.dist(shapes.get(listIndex).origin) < 20);
        }
        
        void completeVertex(float x, float y){
            if(gridOn && gridSnapOn){
                x = snapToGrid(x);
                y = snapToGrid(y);
            }
            shapes.get(shapes.size()-1).modify(new PVector(x,y));
        }

        void completeShape() {
            if(shapes.size() > 0){
                listIndex = shapes.size()-1;
                if(!shapes.get(listIndex).getFinished()) shapes.get(listIndex).finishShape();
                activeMode = Mode.EDIT;
                modifying = false;
            }
        }
        
        void toggleReferenceImage(boolean flip){
            referenceOn = flip;
        }
        
        void toggleSnap(boolean flip){
            gridSnapOn = flip;
        }
        
        void toggleGrid(boolean flip){
            gridOn = flip;
        }
        
        void setGridDensity(int newDensity){ //Use when density changed 
            gridDensity = newDensity;
            gridSpacing = canvasWidth / (float)gridDensity;
        }
        
        void calculateGridSpacing(){ //Use when width changed
            gridSpacing = canvasWidth / (float)gridDensity;
        }
        
        float snapToGrid(float point){
           float newPoint = sketch.round(point/gridSpacing);
           newPoint = newPoint*gridSpacing;
           return newPoint;
        }
        
        PVector snapToGrid(PVector mouse){
            return new PVector(snapToGrid(mouse.x), snapToGrid(mouse.y));
        }
        
        void drawMode(){
            activeMode = Mode.DRAW;
        }
        
        void editMode(){
            activeMode = Mode.EDIT;
        }
        
        void clearSubMode(){
            subMode = Transformation.NON;
        }
    }
