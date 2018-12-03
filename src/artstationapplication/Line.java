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
    VertexHandle start;
    VertexHandle end;
    VertexHandle activeHandle;

    Line(PApplet drawingSpace,float x, float y, float x2, float y2){
      super(drawingSpace, 0,0);
      start = new VertexHandle(drawingSpace, x, y);
      end = new VertexHandle(drawingSpace, x2, y2);
    }

    //
    @Override 
    boolean mouseOver(PVector mouse){
        //could create bounding box and check if outside top left and bottom right. Only do if will be reused, this will be sufficient for current use
        if(mouse.x > start.getPosition().x && mouse.x < end.getPosition().x || mouse.x < start.getPosition().x && mouse.x > end.getPosition().x){
            if(mouse.y > start.getPosition().y && mouse.y < end.getPosition().y || mouse.y < start.getPosition().y && mouse.y > end.getPosition().y )
                return true;
        }
        return false;
    }

    @Override
    void drawShape(){
      app.fill(paint);
      app.strokeWeight(lineThickness);
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
      if(selected){
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
        drawHandles();
      }
      app.popMatrix();
    }
    
    void drawHandles(){
        start.drawHandle();
        end.drawHandle();
    }

    @Override
    void modify(PVector mouse, boolean shift){
      //start.setPosition(mouse);
      end.setPosition(mouse);
      //tail.sub(pos);
    }

    @Override
    boolean checkHandles(PVector mouse){
        if (start.overHandle(mouse)){
            activeHandle = start;
            return true;
        }
        else if( end.overHandle(mouse)){
            activeHandle = end;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        activeHandle.setPosition(mouse);
    }
  }