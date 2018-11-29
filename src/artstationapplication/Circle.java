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
class Circle extends Shape{
    //float radius; //needed? Info contained in handle now
    Handle widthHandleR;
    Handle widthHandleL;
    Handle heightHandleT;
    Handle heightHandleB;
    Handle[] activeHandle = new Handle[2];

    Circle(PApplet drawingSpace, float x, float y, float r){
      super(drawingSpace,x,y);
      //radius = r;   
      widthHandleR = new Handle(drawingSpace, this, r/2, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, r/2, new PVector(-1,0));
      heightHandleB = new Handle(drawingSpace, this, r/2, new PVector(0,1));
      heightHandleT = new Handle(drawingSpace, this, r/2, new PVector(0,-1));
    }

    @Override 
    boolean mouseOver(PVector mouse){
        if(app.dist(pos.x, pos.y, mouse.x, mouse.y) < widthHandleL.getRadius()/2 || app.dist(pos.x, pos.y, mouse.x, mouse.y) < heightHandleT.getRadius()/2){
            return true;
        }
        return false;
    }

    @Override
    void drawShape(){
      app.fill(paint);
      if(lineThickness == 0){
        app.noStroke();
      }
      else{
        app.stroke(0,0,0);
        app.strokeWeight(lineThickness);
      }
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.rotate(rotation);
      app.ellipse(0,0, widthHandleL.getRadius(), heightHandleT.getRadius());
      if(selected){
        app.noFill();
        app.strokeWeight(3);
        app.stroke(editColor);
        app.ellipse(0,0, widthHandleL.getRadius(), heightHandleT.getRadius());
        drawHandles();
      }
      app.popMatrix();
    }

    void drawHandles(){   
        widthHandleL.drawHandle();
        widthHandleR.drawHandle();
        heightHandleT.drawHandle();
        heightHandleB.drawHandle();            
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        activeHandle[0].setRadius(app.dist(pos.x, pos.y, mouse.x, mouse.y));  
        activeHandle[1].setRadius(app.dist(pos.x, pos.y, mouse.x, mouse.y)); 
    }

    @Override
    void modify(PVector mouse, boolean shift){
      float radius = app.dist(mouse.x, mouse.y, pos.x, pos.y);
      rotation = 0;
      widthHandleR.setRadius(radius);
      widthHandleL.setRadius(radius);
      heightHandleT.setRadius(radius);
      heightHandleB.setRadius(radius);
    }

    @Override
    boolean checkHandles(PVector mouse){
        if(widthHandleL.overHandle(mouse, rotation) || widthHandleR.overHandle(mouse,rotation)){
            activeHandle[0] = widthHandleL;
            activeHandle[1] = widthHandleR;
            return true;
        }
        else if (heightHandleT.overHandle(mouse,rotation) || heightHandleB.overHandle(mouse,rotation)){
            activeHandle[0] = heightHandleT;
            activeHandle[1] = heightHandleB;
            return true;
        }
        else return false;
    }     
 }
