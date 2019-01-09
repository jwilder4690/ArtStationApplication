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
    Handle widthHandleR;
    Handle widthHandleL;
    Handle heightHandleT;
    Handle heightHandleB;
    Handle[] activeHandle = new Handle[2];
    Handle[] inactiveHandle = new Handle[2];

    Circle(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace,paint,outline,x,y);
      strokeWeight = thickness;
      name = "Circle";
      index = id;
      widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
      heightHandleB = new Handle(drawingSpace, this, new PVector(0,1));
      heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
    }
        
    //Copy constructor
    Circle(Circle base, int id){
      super(base.app, base.fillColor, base.strokeColor, base.pos.x, base.pos.y);
      strokeWeight = base.strokeWeight;
      name = base.name;
      index = id;
      widthHandleR = new Handle(base.widthHandleR, this);
      widthHandleL = new Handle (base.widthHandleL, this);
      heightHandleB = new Handle (base.heightHandleB, this);
      heightHandleT = new Handle (base.heightHandleT, this);
      rotation = base.rotation;
    }

    @Override 
    boolean mouseOver(PVector mouse){
        //This equation checks for point inside rotated ellipse. No changes needed to be made to account for y down coordinate system. 
        if(app.sq(app.cos(rotation)*(mouse.x - pos.x)+ app.sin(rotation)*(mouse.y - pos.y))/app.sq(widthHandleR.getRadius()) + app.sq(app.sin(rotation)*(mouse.x - pos.x)- app.cos(rotation)*(mouse.y - pos.y))/app.sq(heightHandleT.getRadius()) >= 1) return false;
        else return true;
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
      app.ellipse(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius());
      app.popMatrix();
    }
    
    @Override
    void drawSelected(){
        app.pushMatrix();
        app.translate(pos.x, pos.y);
        app.rotate(rotation);
        app.noFill();
        app.strokeWeight(3);
        app.stroke(editColor);
        app.ellipse(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius());
        drawHandles();
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
      float radius = app.dist(mouse.x, mouse.y, pos.x, pos.y);
      rotation = 0;
      widthHandleR.setModifier(radius);
      widthHandleL.setModifier(radius);
      heightHandleT.setModifier(radius);
      heightHandleB.setModifier(radius);
    }

    @Override
    boolean checkHandles(PVector mouse){
        if(widthHandleL.overHandle(mouse, rotation) || widthHandleR.overHandle(mouse,rotation)){
            activeHandle[0] = widthHandleL;
            activeHandle[1] = widthHandleR;
            inactiveHandle[0] = heightHandleT;
            inactiveHandle[1] = heightHandleB;
            return true;
        }
        else if (heightHandleT.overHandle(mouse,rotation) || heightHandleB.overHandle(mouse,rotation)){
            activeHandle[0] = heightHandleT;
            activeHandle[1] = heightHandleB;
            inactiveHandle[0] = widthHandleL;
            inactiveHandle[1] = widthHandleR;
            return true;
        }
        else return false;
    }    
    
    @Override
    void adjustActiveHandle(PVector mouse){
        float delta = (activeHandle[0].getRadius() - inactiveHandle[0].getRadius())/activeHandle[0].getRadius();
        float dist = app.dist(pos.x, pos.y, mouse.x, mouse.y);
        if(shift){       
            activeHandle[0].setModifier(dist); 
            activeHandle[1].setModifier(dist);
            inactiveHandle[0].setModifier(dist - dist * delta);  
            inactiveHandle[1].setModifier(dist - dist * delta);
        }
        else{
            activeHandle[0].setModifier(dist);  
            activeHandle[1].setModifier(dist);
        }
    }
    
    @Override
    void finishHandles(){
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
        heightHandleB.setRadius();
    }
    
    void reset(){
        rotation = 0;
        widthHandleL.reset();
        widthHandleR.reset();
        heightHandleT.reset();
        heightHandleB.reset();
    }
    
    Shape copy(int id){
        Circle copy = new Circle(this, id);
        return copy;
    }
    
    @Override
    String printToClipboard(){
        String output = "";
        if(fillColor == NONE) output += "\tnoFill();\n";
        else output += "\tfill("+fillColor+");\n";

        if(strokeWeight == 0) output += "\tnoStroke();\n";
        else output += "\tstrokeWeight("+strokeWeight+");\n\tstroke("+strokeColor+");\n";

        output += "\tpushMatrix();\n";
        output += "\ttranslate("+pos.x+", "+pos.y+");\n";
        output += "\trotate("+rotation+");\n";
        output += "\tellipse(0,0,"+2*widthHandleL.getRadius()+", "+2*heightHandleT.getRadius()+");\n";
        output += "\tpopMatrix();\n\n";
        
        return output;
    }
    
    @Override
    PGraphics printToPGraphic(PGraphics ig){
      if(fillColor == NONE){
          ig.noFill();
      }
      else{
        ig.fill(fillColor);
      }
      if(strokeWeight == 0){
        ig.noStroke();
      }
      else{
        ig.stroke(strokeColor);
        ig.strokeWeight(strokeWeight);
      }
      ig.pushMatrix();
      ig.translate(pos.x, pos.y);
      ig.rotate(rotation);
      ig.ellipse(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius());
      ig.popMatrix();
      return ig;
    }
 }
