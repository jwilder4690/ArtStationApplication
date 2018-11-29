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
        float radius;
        float widthModifier = 1;
        float heightModifier = 1;
        PVector corner;
        boolean centerType;
  
        Rectangle(PApplet drawingSpace, float a, float b, float c, boolean style){ 
          super(drawingSpace, a,b);
          radius = c;
          centerType = style;
          corner = new PVector(a+1,b+1); //default corner, will not be displayed 
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
          if(centerType){
            app.rectMode(CENTER);
            app.rect(0,0, radius*widthModifier, radius*heightModifier);
            if(selected){
                app.noFill();
                app.strokeWeight(3);
                app.stroke(255,255, 0);
                app.rect(0,0, radius*widthModifier, radius*heightModifier);
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

        @Override
        void modify(PVector mouse, boolean shift){
          if(centerType) radius = 2*app.dist(mouse.x, mouse.y, pos.x, pos.y);
          else if(!centerType) corner.set(mouse.x, mouse.y);
          PVector orientation = (pos.x < mouse.x) ? PVector.sub(pos,mouse): PVector.sub(mouse,pos);
          rotation = PVector.angleBetween(orientation, new PVector(0,1));
          if(shift){ //implement shift
              float leftover = rotation % QUARTER_PI;
              leftover = app.round(leftover);
              rotation = app.floor(rotation/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);
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
