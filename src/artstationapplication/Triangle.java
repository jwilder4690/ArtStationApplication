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
    float heightModifier = 1;
    float leftModifier = 1;
    float rightModifier = 1;

    Triangle(PApplet drawingSpace, float x, float y, float tall){
      super(drawingSpace, x,y);
      altitude = tall;
      side = app.sqrt((float)(4.0/3.0)*app.sq(altitude));
    }

    @Override 
    boolean mouseOver(PVector mouse){
        return false;
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
      app.triangle(0, heightModifier*(-2*altitude/3), leftModifier*(-side/2), altitude/3, rightModifier*(side/2), altitude/3); 
      if(selected){
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.triangle(0, heightModifier*(-2*altitude/3), leftModifier*(-side/2), altitude/3, rightModifier*(side/2), altitude/3); 
      }
      app.popMatrix();
    }

    @Override
    void modify(PVector mouse, boolean shift){
      altitude = 3*app.dist(pos.x, pos.y, mouse.x, mouse.y);
      side = app.sqrt((float)(4.0/3.0)*app.sq(altitude));
      PVector orientation = PVector.sub(pos,mouse);
      rotation = (pos.x < mouse.x) ? PVector.angleBetween(orientation, new PVector(0,1)):  PI + PVector.angleBetween(orientation, new PVector(0,-1));
      rotation -= app.radians(60);
      if(shift){ //implement shift
          float leftover = rotation % app.radians(30);
          leftover = app.round(leftover);
          rotation = app.floor(rotation/app.radians(30))*app.radians(30)+(leftover*app.radians(30));
      }
    }

    @Override
    boolean checkHandles(PVector mouse){
        return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){

    }
  }
