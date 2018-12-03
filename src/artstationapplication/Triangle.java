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
 class Triangle extends Shape{
    float altitude = 1;
    float side = 1;
    Handle widthHandleL;
    Handle widthHandleR;
    Handle heightHandleT;
    Handle activeHandle;


    Triangle(PApplet drawingSpace, float x, float y, float tall){
      super(drawingSpace, x,y);
      widthHandleR = new Handle(drawingSpace, this, tall, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, tall, new PVector(-1,0));
      heightHandleT = new Handle(drawingSpace, this, tall, new PVector(0,-1));
      altitude = tall;
      side = app.sqrt((float)(4.0/3.0)*app.sq(altitude));
    }

    @Override 
    boolean mouseOver(PVector mouse){
        int x1 = (int) widthHandleL.getPosition(rotation).x;
        int y1 = (int) widthHandleL.getPosition(rotation).y;
        int x2 = (int) widthHandleR.getPosition(rotation).x;
        int y2 = (int) widthHandleR.getPosition(rotation).y;
        int x3 = (int) heightHandleT.getPosition(rotation).x;
        int y3 = (int) heightHandleT.getPosition(rotation).y;
        int px = (int) mouse.x;
        int py = (int) mouse.y;
        int a0 = app.abs((x2-x1)*(y3-y1)-(x3-x1)*(y2-y1));
        int a1 = app.abs((x1-px)*(y2-py)-(x2-px)*(y1-py));
        int a2 = app.abs((x2-px)*(y3-py)-(x3-px)*(y2-py));
        int a3 = app.abs((x3-px)*(y1-py)-(x1-px)*(y3-py));
  
        return (app.abs(a1+a2+a3 - a0) <= 1/256);
    }

    @Override
    void drawShape(){
      app.fill(paint);
      if(lineThickness == 0){
        app.noStroke();
      }
      else{
        app.strokeWeight(lineThickness);
      }
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.rotate(rotation);
      app.triangle(0, -heightHandleT.getRadius(), -widthHandleL.getRadius(), 0, widthHandleR.getRadius(),0); 
      if(selected){
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.triangle(0, -heightHandleT.getRadius(), -widthHandleL.getRadius(), 0, widthHandleR.getRadius(),0); 
        drawHandles();
      }
      app.popMatrix();
    }
    
    void drawHandles(){   
        widthHandleL.drawHandle();
        widthHandleR.drawHandle();
        heightHandleT.drawHandle();           
    }

    @Override
    void modify(PVector mouse, boolean shift){
      //altitude = 3*app.dist(pos.x, pos.y, mouse.x, mouse.y);
      //side = app.sqrt((float)(4.0/3.0)*app.sq(altitude));
      float radius = app.dist(pos.x, pos.y, mouse.x, mouse.y);
      heightHandleT.setRadius(radius);
      widthHandleL.setRadius(app.sqrt((float)(4.0/3.0)*app.sq(radius))/2);
      widthHandleR.setRadius(app.sqrt((float)(4.0/3.0)*app.sq(radius))/2);
//      PVector orientation = PVector.sub(pos,mouse);
//      rotation = (pos.x < mouse.x) ? PVector.angleBetween(orientation, new PVector(0,1)):  PI + PVector.angleBetween(orientation, new PVector(0,-1));
//      rotation -= app.radians(60);
      rotation = app.atan2(mouse.y - pos.y, mouse.x - pos.x);
      rotation += offset;
      if(shift){ //implement shift
          float leftover = rotation % app.radians(30);
          leftover = app.round(leftover);
          rotation = app.floor(rotation/app.radians(30))*app.radians(30)+(leftover*app.radians(30));
      }
    }

    @Override
    boolean checkHandles(PVector mouse){
        if(widthHandleL.overHandle(mouse, rotation)){
            activeHandle = widthHandleL;
            return true;
        }
        else if (heightHandleT.overHandle(mouse,rotation)){
            activeHandle = heightHandleT;
            return true;
        }
        else if(widthHandleR.overHandle(mouse, rotation)){
            activeHandle = widthHandleR;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        float radius = app.dist(pos.x, pos.y, mouse.x, mouse.y);  
        activeHandle.setRadius(radius);
    }
  }
