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
public class VertexHandle {
    PApplet app;
    PVector pos;
    float size = 20;
    int paint;
    
    VertexHandle(PApplet drawingSpace, float x, float y){
        app = drawingSpace;
        pos = new PVector(x,y);
        paint = app.color(255,255,0);
    }
    
    VertexHandle(PApplet drawingSpace, PVector point){
        this(drawingSpace, point.x, point.y);
    }
    
    /*
      Load Constructor
      Used for creating Handle from information stored in save file.
    */ 
    VertexHandle(PApplet drawingSpace, String[] input){
        this(drawingSpace, Float.valueOf(input[0]), Float.valueOf(input[1]));
    }
    
    PVector getPosition(){
        return pos;
    }
    
    void setPosition(PVector point){
        pos = point.copy();
    }
    
    void setPosition(float x, float y){
       pos.set(x,y);
    }
    
    float[] getPositionFloats(){
        return new float[] {pos.x, pos.y};
    }
     
    void shift(PVector shift){
        pos.sub(shift);
    }
    
    void scaleSize(float newSize){
        size = newSize;
    }
    
    boolean overHandle(PVector mouse){
        return mouse.dist(pos) < size;
    }
    
    void drawHandle(){
        app.fill(paint);
        app.strokeWeight(1);
        app.stroke(0,0,0);
        app.ellipse(pos.x, pos.y, size,size);
    }
    
    String save(){
        String output = "";
        output += pos.x+"&"+pos.y;
        return output;
    }
}
