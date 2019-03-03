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
    Handle widthHandleL;
    Handle widthHandleR;
    Handle heightHandleT;
    Handle activeHandle;
    Handle[] inactiveHandle = new Handle[2];

    Triangle(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      index = id;
      widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
      widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
      heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
      name = "Triangle";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Triangle(Triangle base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      widthHandleR = new Handle(base.widthHandleR, this);
      widthHandleL = new Handle (base.widthHandleL, this);
      heightHandleT = new Handle (base.heightHandleT, this);
      rotation = base.rotation;
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Triangle(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]),Float.valueOf(input[6]), Float.valueOf(input[2]), Float.valueOf(input[3]), Integer.valueOf(input[7]));
        startingRotation = Float.valueOf(input[4]);
        rotation = Float.valueOf(input[5]);
        completed = true;
        widthHandleR = new Handle(drawingSpace, this, input[8].split("&"));
        widthHandleL = new Handle(drawingSpace, this, input[9].split("&"));
        heightHandleT = new Handle(drawingSpace, this, input[10].split("&"));
        name = input[11];
    }
    

    @Override 
    boolean mouseOver(PVector mouse){
        //Uses some fancy math to determine if mouse is inside three points
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
        //If shape is selected this method will be called to draw a highlighted outline over shape. 
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


    /*
      Uses mouse position relative to shape position to scale and rotate shape.
      Used only during initial drawing to canvas.
    */
    @Override
    void modify(PVector mouse){
      float heightOfTriangle = app.dist(pos.x, pos.y, mouse.x, mouse.y);
      //Calculates half width based on height to maintain equilateral triangle
      float halfWidthOfTriangle = app.sqrt((float)(4.0/3.0)*app.sq(heightOfTriangle))/2; 
      
      heightHandleT.calculateModifier(heightOfTriangle);
      widthHandleL.calculateModifier(halfWidthOfTriangle);
      widthHandleR.calculateModifier(halfWidthOfTriangle);

      rotation = app.atan2(mouse.y - pos.y, mouse.x - pos.x) + PI/2;
      rotation += startingRotation;
      if(shift){ //Snaps rotation to 30 degree increments if shift key pressed
          float leftover = rotation % app.radians(30);
          leftover = app.round(leftover);
          rotation = app.floor(rotation/app.radians(30))*app.radians(30)+(leftover*app.radians(30));
      }
    }

        @Override
    void resizeHandles(float size){
        widthHandleL.scaleSize(size);
        widthHandleR.scaleSize(size);
        heightHandleT.scaleSize(size);
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
        float dist = app.dist(pos.x, pos.y, mouse.x, mouse.y);
        
        if(shift){
            //If shift is held, inactive handles are scaled proportionally with the active controller.
            //First calulates ratio between current handles, then scales handles accordingly
            
              float ratio1 = inactiveHandle[0].getRadius()/activeHandle.getRadius();
              float ratio2 = inactiveHandle[1].getRadius()/activeHandle.getRadius();
              activeHandle.calculateModifier(dist); 
              inactiveHandle[0].calculateModifier(dist * ratio1);  
              inactiveHandle[1].calculateModifier(dist * ratio2);  
        }
        else{
            activeHandle.calculateModifier(dist);  
        }
    }
    
    @Override
    void finishShape(){
        super.finishShape();
        //Sets the base radius to return to when 'reset' button is pressed.
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
    }
    
        @Override
    float[] getResetFloats(){
        return new float[] {widthHandleL.getModifier(), widthHandleR.getModifier(), heightHandleT.getModifier(), rotation};
    }
    
    @Override
    void setHandles(float[] mods){
        widthHandleL.setModifier(mods[0]);
        widthHandleR.setModifier(mods[1]);
        heightHandleT.setModifier(mods[2]);
    }
    
    @Override
    void reset(){
        rotation = 0;
        widthHandleL.reset();
        widthHandleR.reset();
        heightHandleT.reset();
    }
    
    @Override
    Shape copy(int id){
        return new Triangle(this, id);
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
        output += "\ttriangle(0, "+-heightHandleT.getRadius()+", "+-widthHandleL.getRadius()+", 0, "+widthHandleR.getRadius()+", 0);\n";
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
      ig.triangle(0, -heightHandleT.getRadius(), -widthHandleL.getRadius(), 0, widthHandleR.getRadius(),0); 
      ig.popMatrix();
      return ig;
    }
    
    @Override
    String save(){
        String output ="Triangle;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+startingRotation+","+rotation+","+strokeWeight+","+index+",";
        output += widthHandleR.save()+",";
        output += widthHandleL.save()+",";
        output += heightHandleT.save()+",";
        output += this.name;
        return output;
    }
  }
