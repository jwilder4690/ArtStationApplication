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
    final float HALF_PI = app.HALF_PI;
    final float PI = app.PI;
    PVector startingRotation;
    float offset = 0;
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
        offset = rotation - app.atan2(mouse.y - pos.y, mouse.x - pos.x);
    }

    abstract boolean checkHandles(PVector mouse);

    abstract void adjustActiveHandle(PVector mouse);

    abstract void drawShape();

    abstract void modify(PVector mouse);

    abstract boolean mouseOver(PVector mouse);

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
        completed = true;
    }

    void select() {
        selected = true;
    }

    void deselect() {
        selected = false;
    }
}
