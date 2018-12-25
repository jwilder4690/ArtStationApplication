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
 class Line extends Shape{
    VertexHandle start;
    VertexHandle end;
    VertexHandle activeHandle;
    VertexHandle inactiveHandle;

    Line(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      name = "Line";
      index = id;
      start = new VertexHandle(drawingSpace, x,y);
      PVector point = new PVector(50,50);
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
      if(strokeWeight == 0){
          app.noStroke();
      }
      else{
        app.stroke(strokeColor);
        app.strokeWeight(strokeWeight);
      }
      app.pushMatrix();
      app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
      app.popMatrix();
    }
    
    @Override
    void drawSelected(){
        app.pushMatrix();
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
        drawHandles();
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
                activeHandle.setPosition(new PVector(mouse.x, inactiveHandle.getPosition().y));
            }
            else if(angle % HALF_PI == 0 ){
                activeHandle.setPosition(new PVector(inactiveHandle.getPosition().x, mouse.y));
            }
            else {
                float dist = mouse.dist(inactiveHandle.getPosition());
                int direction;
                if(mouse.y > inactiveHandle.getPosition().y){
                    direction = 1;
                }
                else direction = -1;
                PVector newAngle = new PVector(app.cos(angle), direction*app.sin(angle));
                newAngle.mult(dist);
                activeHandle.setPosition(PVector.add(newAngle, inactiveHandle.getPosition()));
            }
        }
        else{
            activeHandle.setPosition(mouse);
        }
    }
    
        @Override
    void finishHandles(){
        //pass
    }
  }