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
    Handle activeHandle;
    Handle[] inactiveHandle = new Handle[3];
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
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        return (app.abs(rotX) < widthHandleL.getRadius() && app.abs(rotY) < heightHandleT.getRadius());
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
        //Still uses corners drawing mode in order to enable assymetric scaling
        app.rectMode(CORNERS);
        app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
        if(selected){
            app.noFill();
            app.strokeWeight(3);
            app.stroke(255,255, 0);
            app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
            drawHandles();
          }
      }
      //Currently not using, alt draw mode maybe?
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
    void modify(PVector mouse){
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
        if(widthHandleL.overHandle(mouse, rotation) ){
            activeHandle = widthHandleL;
            inactiveHandle[0] = widthHandleR;
            inactiveHandle[1] = heightHandleT;
            inactiveHandle[2] = heightHandleB;
            return true;
        }
        else if (widthHandleR.overHandle(mouse,rotation)){
            activeHandle = widthHandleR;
            inactiveHandle[0] = widthHandleL;
            inactiveHandle[1] = heightHandleT;
            inactiveHandle[2] = heightHandleB;
            return true;
        }
        else if(heightHandleT.overHandle(mouse,rotation)){
            activeHandle = heightHandleT;
            inactiveHandle[0] = heightHandleB;
            inactiveHandle[1] = widthHandleL;
            inactiveHandle[2] = widthHandleR;
            return true;
        }
        else if(heightHandleB.overHandle(mouse,rotation)){
            activeHandle = heightHandleB;
            inactiveHandle[0] = heightHandleT;
            inactiveHandle[1] = widthHandleL;
            inactiveHandle[2] = widthHandleR;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        float dist = app.dist(pos.x, pos.y, mouse.x, mouse.y);
        if(shift){      
            float delta0 = (activeHandle.getRadius() - inactiveHandle[0].getRadius())/activeHandle.getRadius();
            float delta1 = (activeHandle.getRadius() - inactiveHandle[1].getRadius())/activeHandle.getRadius();
            float delta2 = (activeHandle.getRadius() - inactiveHandle[2].getRadius())/activeHandle.getRadius();
            activeHandle.setRadius(dist); 
            inactiveHandle[0].setRadius(dist - dist * delta0);  
            inactiveHandle[1].setRadius(dist - dist * delta1);
            inactiveHandle[2].setRadius(dist - dist * delta2);
        }
        else{
            activeHandle.setRadius(dist);  
        }
    }
  }
