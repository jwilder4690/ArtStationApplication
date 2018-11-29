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
 class Line extends Shape{
    PVector tail;

    Line(PApplet drawingSpace,float x, float y, float x2, float y2){
      super(drawingSpace, x,y);
      tail = new PVector(x2,y2);
    }

    @Override 
    boolean mouseOver(PVector mouse){
        return false;
    }

    @Override
    void drawShape(){
      app.fill(paint);
      app.strokeWeight(lineThickness);
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.line(0,0, tail.x, tail.y);
      if(selected){
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.line(0,0, tail.x, tail.y);
      }
      app.popMatrix();
    }

    @Override
    void modify(PVector mouse, boolean shift){
      tail.set(mouse.x, mouse.y);
      tail.sub(pos);
    }

    @Override
    boolean checkHandles(PVector mouse){
        return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){

    }
  }