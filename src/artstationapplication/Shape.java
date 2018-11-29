/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import processing.core.*;

/**
 *
 * @author wilder4690
 */
abstract class Shape{
    PApplet app;
    PVector pos;
    final float QUARTER_PI = app.QUARTER_PI;
    final float PI = app.PI;
    PVector startingRotation;
    float angularChange = 0;
    float rotation = 0;
    int paint;
    int editColor;
    float lineThickness = 1;
    boolean completed = false;
    boolean selected = false;
    boolean shift = false;

    Shape(PApplet drawingSpace, float x, float y) {
        app = drawingSpace;
        pos = new PVector(x, y);
        paint = app.color(255, 0, 255);
        editColor = app.color(255, 255, 0);
    }

    boolean getFinished() {
        return completed;
    }

    PVector getPosition() {
        return pos;
    }
    
    void setShift(boolean turn){
        shift = turn;
    }

    void setStartingRotation(PVector mouse) {
        startingRotation = PVector.sub(mouse, pos);
    }

    abstract boolean checkHandles(PVector mouse);

    abstract void adjustActiveHandle(PVector mouse);

    abstract void drawShape();

    abstract void modify(PVector mouse, boolean shift);

    abstract boolean mouseOver(PVector mouse);

    void manipulate(PVector mouse) {
        pos.set(mouse);
    }

    void changeRotation(PVector mouse, boolean shiftKey) {
        PVector orientation = PVector.sub(mouse,pos);
        rotation = PVector.angleBetween(orientation, startingRotation);
        //angleBetween is giving us the correct angle, but once it goes past PI it begins shrinking, causing the rotation to reverse. 
        if(PI - rotation < 0.05){
            setStartingRotation(mouse);
        }

        if (shiftKey) {
            float leftover = rotation % QUARTER_PI;
            leftover = app.round(leftover);
            rotation = app.floor(rotation / QUARTER_PI) * QUARTER_PI + (leftover * QUARTER_PI);
        }
    }

    void finishShape() {
        completed = true;
    }

    void select() {
        selected = true;
    }

    void deselect() {
        selected = false;
    }
}
