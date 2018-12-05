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
    VertexHandle inactiveHandle;

    Line(PApplet drawingSpace,float x, float y, float x2, float y2){
      super(drawingSpace, x,y);
      start = new VertexHandle(drawingSpace, x,y);
      PVector point = new PVector(x2,y2);
      point.sub(new PVector(x,y));
      end = new VertexHandle(drawingSpace, point);
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
    void manipulate(PVector mouse){
        PVector lineDelta = PVector.sub(end.getPosition(), start.getPosition());
        start.setPosition(mouse);
        end.setPosition(PVector.add(start.getPosition(), lineDelta));
    }

    @Override
    void drawShape(){
      app.fill(paint);
      app.strokeWeight(lineThickness);
      app.pushMatrix();
      //app.translate(pos.x, pos.y);
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
    void modify(PVector mouse){
      end.setPosition(mouse);
    }

    @Override
    boolean checkHandles(PVector mouse){
        if (start.overHandle(mouse)){
            activeHandle = start;
            inactiveHandle = end;
            return true;
        }
        else if( end.overHandle(mouse)){
            activeHandle = end;
            inactiveHandle = start;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        if(shift){
            float angle = PVector.angleBetween(PVector.sub(mouse, inactiveHandle.getPosition()), new PVector(1,0));
            float leftover = angle % QUARTER_PI;
            leftover = app.round(leftover);
            angle = app.floor(angle/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);  
            
            if(angle == 0 || angle % PI == 0){
                System.out.println("Horizonal");
                activeHandle.setPosition(new PVector(mouse.x, inactiveHandle.getPosition().y));
            }
            else if(angle % HALF_PI == 0 ){
                System.out.println("Vertical");
                activeHandle.setPosition(new PVector(inactiveHandle.getPosition().x, mouse.y));
            }
            else {
                System.out.println("Diagonal");
                float dist = mouse.dist(inactiveHandle.getPosition());
                //TODO: calculate vector of diagonal, scale by dist, and add to inactive pos
                //activeHandle.setPosition(new PVector(mouse.x, mouse.x));
            }
        }
        else{
            activeHandle.setPosition(mouse);
        }
    }
  }