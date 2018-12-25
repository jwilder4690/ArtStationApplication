/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import processing.core.*;
import javafx.scene.paint.Color;

/**
 *
 * @author wilder4690
 */
abstract class Shape{
    PApplet app;
    PVector pos;
    final float QUARTER_PI = app.QUARTER_PI;
    final float HALF_PI = app.HALF_PI;
    final int NONE = -777;
    final float PI = app.PI;
    PVector startingRotation;
    float offset = 0;
    float rotation = 0;
    int fillColor;
    int strokeColor;
    int editColor;
    float strokeWeight = 1;
    boolean completed = false;
    boolean shift = false;
    String name = "shape";
    int index;

    Shape(PApplet drawingSpace, int paint, int outline, float x, float y) {
        app = drawingSpace;
        pos = new PVector(x, y);
        fillColor = paint;
        strokeColor = outline;
        editColor = app.color(255, 255, 0);
    }
    
    int getFillColor(){
        return fillColor;
    }
    
    int getStrokeColor(){
        return strokeColor;
    }
    
    void setFillColor(int newColor){
        fillColor = newColor;
    }
    
    void setStrokeColor(int newColor){
        strokeColor = newColor;
    }
    
    void setStrokeWeight(float weight){
        strokeWeight = weight;
    }

    boolean getFinished() {
        return completed;
    }

    PVector getPosition() {
        return pos;
    }
    
    void setPosition(float x, float y){
        pos.set(x,y);
    }
    
    void setShift(boolean turn){
        shift = turn;
    }

    void setStartingRotation(PVector mouse) {
        offset = rotation - app.atan2(mouse.y - pos.y, mouse.x - pos.x);
    }
    
    
    @Override
    public String toString(){
        return "("+index+") - " +name;
    }

    abstract boolean checkHandles(PVector mouse);

    abstract void adjustActiveHandle(PVector mouse);

    abstract void drawShape();
    
    abstract void drawSelected();

    abstract void modify(PVector mouse);

    abstract boolean mouseOver(PVector mouse);
    
    abstract void finishHandles();

    void manipulate(PVector mouse) {
        pos.set(mouse);
        
    }

    void changeRotation(PVector mouse) {         
          rotation = app.atan2(mouse.y - pos.y, mouse.x - pos.x);
          rotation += offset;
        if (shift) {
            float leftover = rotation % QUARTER_PI;
            leftover = app.round(leftover);
            rotation = app.floor(rotation / QUARTER_PI) * QUARTER_PI + (leftover * QUARTER_PI);
        }
    }

    void finishShape() {
        finishHandles();
        completed = true;
    }

//    void select() {
//        selected = true;
//    }
//
//    void deselect() {
//        selected = false;
//    }
    
    void reset(){
        //pass
    }
}
