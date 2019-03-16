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
     class Handle{
         PApplet app;
         float modifier = 1;
         float radius = 50;
         float size = 20;
         int paint;
         PVector offset;
         Shape parent;
         
         Handle(PApplet drawingSpace, Shape parent, PVector which){
             app = drawingSpace;
             offset = which;
             this.parent = parent;
             paint = app.color(255,255,0);
        }
         
        /*
          Copy Constructor
          Used for creating an exact copy of base Handle.
        */
         Handle(Handle base, Shape parent){
             this(base.app, parent, base.offset.copy());
             radius = base.radius;
             modifier = base.modifier;
         }
         
         /*
           Load Constructor
           Used for creating shape from information stored in save file.
         */ 
         Handle(PApplet drawingSpace, Shape parent, String[] input){
            app = drawingSpace;
            modifier = Float.valueOf(input[0]);
            radius = Float.valueOf(input[1]);
            paint = app.color(255,255,0);
            offset = new PVector(Float.valueOf(input[2]),Float.valueOf(input[3]));
            this.parent = parent;
         }
         
         PVector getPosition(float rot){
             //Manually applies the rotation matrix to the handle position.
             //TODO: this still seems inefficient to do this calculation constantly
             float pointX = modifier*radius*offset.x;
             float pointY = modifier*radius*offset.y;
             return new PVector(parent.getPosition().x + pointX * app.cos(rot) - pointY*app.sin(rot), parent.getPosition().y + pointX*app.sin(rot) + pointY*app.cos(rot));
         }
         
         void setOffset(PVector mouse){
           float rotation = parent.rotation;
            float deltaX = mouse.x - parent.pos.x;
            float deltaY = mouse.y - parent.pos.y;
            float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
            float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
            PVector rotatedMouse = new PVector(rotX, rotY);
            rotatedMouse.normalize(); //must be length of 1 in order to scale correctly based on radius and modifier
            offset.set(rotatedMouse);
         }
         
         float getOffset(){
             float angle = app.atan2(offset.y,offset.x); 
             return angle;
         }
         
         void calculateModifier(float r){
             modifier = r/radius;
         }
        
         void setModifier(float mod){
             modifier = mod;
         }
         
         float getModifier(){
             return modifier;
         }
         
         void setRadius(){
             radius = radius*modifier;
             modifier = 1;
         }
                 
         boolean overHandle(PVector m, float rot){
             return (m.dist(getPosition(rot)) < size);
         }
         
         float getRadius(){
             return radius*modifier;
         }
         
        void scaleSize(float newSize){
            size = newSize;
        }

         void drawHandle(){
             app.fill(paint);
             app.strokeWeight(1);
             app.stroke(0,0,0);
             app.ellipse(modifier*radius*offset.x, modifier*radius*offset.y, size,size);
         }
         
         void reset(){
             modifier = 1;
         }
         
         String save(){
             String output = "";
             output += modifier+"&"+radius+"&"+offset.x+"&"+offset.y;
             return output;
         }
     }
