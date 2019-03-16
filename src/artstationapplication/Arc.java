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
class Arc extends Shape{
    Handle widthHandleR;
    Handle widthHandleL;
    Handle heightHandleT;
    Handle heightHandleB;
    Handle start;
    Handle end;
    Handle[] activeHandle = new Handle[2];
    Handle[] inactiveHandle = new Handle[2];
    float majorRadius = 50;

    Arc(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace,paint,outline,x,y);
      strokeWeight = thickness;
      index = id;
      widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
      heightHandleB = new Handle(drawingSpace, this, new PVector(0,1));
      heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
      start = new Handle(drawingSpace, this, new PVector(0,-1));
      end = new Handle(drawingSpace, this, new PVector(1,0));
      name = "Arc";
    }
        
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Arc(Arc base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      widthHandleR = new Handle(base.widthHandleR, this);
      widthHandleL = new Handle (base.widthHandleL, this);
      heightHandleB = new Handle (base.heightHandleB, this);
      heightHandleT = new Handle (base.heightHandleT, this);
      start = new Handle(base.start, this);
      end = new Handle(base.end, this);
      rotation = base.rotation;
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Arc(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[6]), Float.valueOf(input[2]), Float.valueOf(input[3]), Integer.valueOf(input[7]));
        startingRotation = Float.valueOf(input[4]);
        rotation = Float.valueOf(input[5]);
        widthHandleR = new Handle(drawingSpace, this, input[8].split("&"));
        widthHandleL = new Handle(drawingSpace, this, input[9].split("&"));
        heightHandleT = new Handle(drawingSpace, this, input[10].split("&"));
        heightHandleB = new Handle(drawingSpace, this, input[11].split("&"));
        start = new Handle(drawingSpace, this, input[12].split("&"));
        end = new Handle(drawingSpace, this, input[13].split("&"));
        name = input[14];
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
      if(start.getOffset() < end.getOffset()) app.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(),start.getOffset(), end.getOffset(), app.PIE);
      else app.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(), start.getOffset(), end.getOffset() + 2*PI, app.PIE);
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
        if(start.getOffset() < end.getOffset()) app.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(),start.getOffset(), end.getOffset(), app.PIE);
        else app.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(), start.getOffset(), end.getOffset() + 2*PI, app.PIE);
        drawHandles();
        app.popMatrix();
    }
    

    void drawHandles(){   
        widthHandleL.drawHandle();
        widthHandleR.drawHandle();
        heightHandleT.drawHandle();
        heightHandleB.drawHandle();      
        start.drawHandle();
        end.drawHandle();
    }

    @Override
    /*
      Uses mouse position relative to shape position to scale shape.
      Used only during initial drawing to canvas.
    */
    void modify(PVector mouse){
      float radius = app.dist(mouse.x, mouse.y, pos.x, pos.y);
      rotation = 0; //no rotation when initially drawing
      widthHandleR.calculateModifier(radius);
      widthHandleL.calculateModifier(radius);
      heightHandleT.calculateModifier(radius);
      heightHandleB.calculateModifier(radius);
    }
    

        @Override
    void resizeHandles(float size){
        widthHandleL.scaleSize(size);
        widthHandleR.scaleSize(size);
        heightHandleT.scaleSize(size);
        heightHandleB.scaleSize(size);
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
        else if( start.overHandle(mouse, rotation)){
            activeHandle[0] = start;
            return true;
        }
        else if( end.overHandle(mouse, rotation)){
            activeHandle[0] = end;
            return true;
        }
        else return false;
    }    
    
    @Override
    void adjustActiveHandle(PVector mouse){

        //Handles for start and end of arc
        if(activeHandle[0] == start || activeHandle[0] == end){
            //activeHandle[0].setOffset(PVector.sub(mouse, pos));
            activeHandle[0].setOffset(mouse);
        }
        else{ //Handles for base ellipse 
            float ratio = inactiveHandle[0].getRadius()/activeHandle[0].getRadius();
            float dist = app.dist(pos.x, pos.y, mouse.x, mouse.y);
            if(shift){  
                //If shift is held, inactive handles are scaled proportionally with the active controller.
                //First calulates ratio between current handles, then scales handles accordingly

                activeHandle[0].calculateModifier(dist); 
                activeHandle[1].calculateModifier(dist);
                inactiveHandle[0].calculateModifier(dist * ratio);  
                inactiveHandle[1].calculateModifier(dist * ratio);
            }
            else{
                activeHandle[0].calculateModifier(dist);  
                activeHandle[1].calculateModifier(dist);
            }
        }
        calculateMajorRadius();
    }
    
    @Override
    void finishShape(){
        super.finishShape();
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
        heightHandleB.setRadius();     
        calculateMajorRadius();
    }
    
    void calculateMajorRadius(){
        if(widthHandleL.getRadius() > heightHandleT.getRadius()) majorRadius = widthHandleL.getRadius();
        else majorRadius = heightHandleT.getRadius();
        start.calculateModifier(1.5f*majorRadius);
        end.calculateModifier(1.5f*majorRadius);
    }
    
    @Override
    float[] getResetFloats(){
        return new float[] {widthHandleL.getModifier(), widthHandleR.getModifier(), heightHandleT.getModifier(), heightHandleB.getModifier(), rotation};
    }
    
    @Override
    void setHandles(float[] mods){
        widthHandleL.setModifier(mods[0]);
        widthHandleR.setModifier(mods[1]);
        heightHandleT.setModifier(mods[2]);
        heightHandleB.setModifier(mods[3]);
    }
    
    @Override
    void reset(){
        rotation = 0;
        widthHandleL.reset();
        widthHandleR.reset();
        heightHandleT.reset();
        heightHandleB.reset();
        calculateMajorRadius();
    }
    
    Shape copy(int id){
        Arc copy = new Arc(this, id);
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
        if(start.getOffset() < end.getOffset()) output += "\tarc(0,0,"+2*widthHandleL.getRadius()+", "+2*heightHandleT.getRadius()+", "+start.getOffset()+", "+end.getOffset()+", PIE);\n";
        else output += "\tarc(0,0,"+2*widthHandleL.getRadius()+", "+2*heightHandleT.getRadius()+", "+start.getOffset()+", "+(end.getOffset()+2*PI)+", PIE);\n";       
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
      if(start.getOffset() < end.getOffset()) ig.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(),start.getOffset(), end.getOffset(), app.PIE);
      else ig.arc(0,0, 2*widthHandleL.getRadius(), 2*heightHandleT.getRadius(), start.getOffset(), end.getOffset() + 2*PI, app.PIE);
      ig.popMatrix();
      return ig;
    }
    
    @Override
    String save(){
        String output ="Arc;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+startingRotation+","+rotation+","+strokeWeight+","+index+",";
        output += widthHandleL.save()+",";
        output += widthHandleR.save()+",";
        output += heightHandleT.save()+",";
        output += heightHandleB.save()+",";
        output += start.save()+",";
        output += end.save()+",";
        output += this.name;
        return output;
    }
 }
