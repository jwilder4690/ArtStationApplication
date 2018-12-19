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
         float size = 15;
         int paint;
         PVector offset;
         Shape parent;
         

         
         Handle(PApplet drawingSpace, Shape parent, PVector which){
             app = drawingSpace;
             offset = which;
             this.parent = parent;
             paint = app.color(255,255,0);
        }
         
         PVector getPosition(float rot){
             
             //Optional TODO: if this works set the position in a PVector that manually updates on mouse release of rotation, to avoid doing this calculation constantly
             float pointX = modifier*radius*offset.x;
             float pointY = modifier*radius*offset.y;
             return new PVector(parent.getPosition().x + pointX * app.cos(rot) - pointY*app.sin(rot), parent.getPosition().y + pointX*app.sin(rot) + pointY*app.cos(rot));
         }
                  
         void setRadius(float r){
             modifier = r/radius;
         }
         
         boolean overHandle(PVector m, float rot){
             return (m.dist(getPosition(rot)) < size);
         }
         
         float getRadius(){
             return radius*modifier;
         }
         
         void drawHandle(){
             app.fill(paint);
             app.strokeWeight(1);
             app.stroke(0,0,0);
             app.ellipse(modifier*radius*offset.x, modifier*radius*offset.y, size,size);
         }
     }
