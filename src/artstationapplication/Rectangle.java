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
 class Rectangle extends Shape{
    final int CENTER = app.CENTER;
    final int CORNERS = app.CORNERS;
    Handle widthHandleR;
    Handle widthHandleL;
    Handle heightHandleT;
    Handle heightHandleB;
    Handle[] activeHandle = new Handle[2];
//        float radius;
//        float widthModifier = 1;
//        float heightModifier = 1;
    PVector corner;
    boolean centerType;

    Rectangle(PApplet drawingSpace, float a, float b, float c, boolean style){ 
        super(drawingSpace, a,b);
        widthHandleR = new Handle(drawingSpace, this, c/2, new PVector(1,0));
        widthHandleL = new Handle(drawingSpace, this, c/2, new PVector(-1,0));
        heightHandleB = new Handle(drawingSpace, this, c/2, new PVector(0,1));
        heightHandleT = new Handle(drawingSpace, this, c/2, new PVector(0,-1));
        centerType = style;
        corner = new PVector(a+1,b+1); //default corner, will not be displayed 
    }

    @Override 
    boolean mouseOver(PVector mouse){
        if(mouse.x > pos.x + widthHandleL.getRadius() || mouse.x < pos.x - widthHandleL.getRadius()) return false;
        if(mouse.y > pos.y + heightHandleT.getRadius() || mouse.y < pos.y - heightHandleT.getRadius()) return false;
        return true;
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
      if(centerType){
        app.rectMode(CENTER);
        app.rect(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius());
        if(selected){
            app.noFill();
            app.strokeWeight(3);
            app.stroke(255,255, 0);
            app.rect(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius());
            drawHandles();
          }
      }
      else if(!centerType){
        app.rectMode(CORNERS);
        app.rect(0,0, corner.x, corner.y);
        if(selected){
            app.noFill();
            app.strokeWeight(3);
            app.stroke(255,255, 0);
            app.rect(0,0, corner.x, corner.y);
          }
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
    void modify(PVector mouse, boolean shift){
        float radius;
        if(centerType){
            radius = app.dist(mouse.x, mouse.y, pos.x, pos.y);
            widthHandleR.setRadius(radius);
            widthHandleL.setRadius(radius);
            heightHandleT.setRadius(radius);
            heightHandleB.setRadius(radius);
        }
        else if(!centerType) corner.set(mouse.x, mouse.y);
        rotation = app.atan2(mouse.y - pos.y, mouse.x - pos.x);
        rotation += offset;
        if(shift){ //implement shift
            float leftover = rotation % QUARTER_PI;
            leftover = app.round(leftover);
            rotation = app.floor(rotation/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);
        }
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

    @Override
    void adjustActiveHandle(PVector mouse){
        activeHandle[0].setRadius(app.dist(pos.x, pos.y, mouse.x, mouse.y));  
        activeHandle[1].setRadius(app.dist(pos.x, pos.y, mouse.x, mouse.y)); 
    }
  }
