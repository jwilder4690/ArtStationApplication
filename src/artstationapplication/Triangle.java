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
 class Triangle extends Shape{
    float altitude = 1;
    float side = 1;
    Handle widthHandleL;
    Handle widthHandleR;
    Handle heightHandleT;
    Handle activeHandle;
    Handle[] inactiveHandle = new Handle[2];


    Triangle(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      name = "Triangle";
      index = id;
      widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
      heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
      altitude = 50;
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
      if(fillColor == NONE){
          app.noFill();
      }
      else{
        app.fill(fillColor);
      }
      if(strokeWeight == 0){
        app.noStroke();
      }
      else{
        app.stroke(strokeColor);
        app.strokeWeight(strokeWeight);
      }
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.rotate(rotation);
      app.triangle(0, -heightHandleT.getRadius(), -widthHandleL.getRadius(), 0, widthHandleR.getRadius(),0); 
      app.popMatrix();
    }
    
        @Override
    void drawSelected(){
        app.pushMatrix();
        app.translate(pos.x, pos.y);
        app.rotate(rotation); 
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.triangle(0, -heightHandleT.getRadius(), -widthHandleL.getRadius(), 0, widthHandleR.getRadius(),0); 
        drawHandles();
        app.popMatrix();
    }
    
    void drawHandles(){   
        widthHandleL.drawHandle();
        widthHandleR.drawHandle();
        heightHandleT.drawHandle();           
    }

    @Override
    void modify(PVector mouse){
      float radius = app.dist(pos.x, pos.y, mouse.x, mouse.y);
      heightHandleT.setModifier(radius);
      widthHandleL.setModifier(app.sqrt((float)(4.0/3.0)*app.sq(radius))/2);
      widthHandleR.setModifier(app.sqrt((float)(4.0/3.0)*app.sq(radius))/2);

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
            inactiveHandle[0] = heightHandleT;
            inactiveHandle[1] = widthHandleR;
            return true;
        }
        else if (heightHandleT.overHandle(mouse,rotation)){
            activeHandle = heightHandleT;
            inactiveHandle[0] = widthHandleL;
            inactiveHandle[1] = widthHandleR;
            return true;
        }
        else if(widthHandleR.overHandle(mouse, rotation)){
            activeHandle = widthHandleR;
            inactiveHandle[0] = heightHandleT;
            inactiveHandle[1] = widthHandleL;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        float delta1 = (activeHandle.getRadius() - inactiveHandle[0].getRadius())/activeHandle.getRadius();
        float delta2 = (activeHandle.getRadius() - inactiveHandle[1].getRadius())/activeHandle.getRadius();
        float dist = app.dist(pos.x, pos.y, mouse.x, mouse.y);
        if(shift){       
            activeHandle.setModifier(dist); 
            inactiveHandle[0].setModifier(dist - dist * delta1);  
            inactiveHandle[1].setModifier(dist - dist * delta2);  
        }
        else{
            activeHandle.setModifier(dist);  
        }
//        float radius = app.dist(pos.x, pos.y, mouse.x, mouse.y);  
//        activeHandle.setModifier(radius);
    }
    
    @Override
    void finishHandles(){
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
    }
    
    void reset(){
        rotation = 0;
        widthHandleL.reset();
        widthHandleR.reset();
        heightHandleT.reset();
    }
    
  }
