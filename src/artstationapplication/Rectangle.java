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

    Rectangle(PApplet drawingSpace, int paint, int outline, float thickness, float a, float b, int id){ 
        super(drawingSpace, paint, outline, a,b);
        strokeWeight = thickness;
        index = id;
        widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
        widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
        heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
        heightHandleB = new Handle(drawingSpace, this, new PVector(0,1));
        corner = new PVector(a+1,b+1); //default corner, will not be displayed 
        name = "Rectangle";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Rectangle(Rectangle base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      widthHandleR = new Handle(base.widthHandleR, this);
      widthHandleL = new Handle (base.widthHandleL, this);
      heightHandleT = new Handle (base.heightHandleT, this);
      heightHandleB = new Handle (base.heightHandleB, this);
      rotation = base.rotation;
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Rectangle(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[6]),Float.valueOf(input[2]), Float.valueOf(input[3]), Integer.valueOf(input[7]));
        startingRotation = Float.valueOf(input[4]);
        rotation = Float.valueOf(input[5]);
        widthHandleR = new Handle(drawingSpace, this, input[8].split("&"));
        widthHandleL = new Handle(drawingSpace, this, input[9].split("&"));
        heightHandleT = new Handle(drawingSpace, this, input[10].split("&"));
        heightHandleB = new Handle(drawingSpace, this, input[11].split("&"));
        name = input[12];
    }

    @Override 
    boolean mouseOver(PVector mouse){
        float deltaX = mouse.x-pos.x;
        float deltaY = mouse.y-pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        
        if(rotX < -widthHandleL.getRadius() || rotX > widthHandleR.getRadius()) return false;
        if(rotY < -heightHandleT.getRadius() || rotY > heightHandleB.getRadius()) return false;
        return true;
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
        
      //Still uses corners drawing mode in order to enable assymetric scaling
      app.rectMode(CORNERS);
      if(alt) app.rect(0,0, widthHandleR.getRadius(), heightHandleB.getRadius());
      else app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
      app.popMatrix();
    }
    
    @Override
    void drawSelected(){
        app.pushMatrix();
        app.translate(pos.x, pos.y);
        app.rotate(rotation);
        //Still uses corners drawing mode in order to enable assymetric scaling
        app.rectMode(CORNERS);
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
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
    /*
        Method still messy because alternate drawing method was partially implemented.
        Will finish later. 
        Uses mouse position relative to shape position to scale and rotate shape.
        Used only during initial drawing to canvas.
    */
    void modify(PVector mouse){
        float radius = app.dist(mouse.x, mouse.y, pos.x, pos.y);
        //TODO: implement alt drawMode
        if(alt){
            rotation = 0;
            widthHandleR.calculateModifier(mouse.x - pos.x);
            heightHandleB.calculateModifier(mouse.y - pos.y);
        }
        else{
            widthHandleR.calculateModifier(radius);
            widthHandleL.calculateModifier(radius);
            heightHandleT.calculateModifier(radius);
            heightHandleB.calculateModifier(radius);
        
            rotation = app.atan2(mouse.y - pos.y, mouse.x - pos.x);
            rotation += startingRotation;

            if(shift){ //Snaps rotation to PI/4 increments if shift is held
                float leftover = rotation % QUARTER_PI;
                leftover = app.round(leftover);
                rotation = app.floor(rotation/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);
            }
        }
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
            //If shift is held, inactive handles are scaled proportionally with the active controller.
            //First calulates ratio between current handles, then scales handles accordingly
            
            float ratio1 = inactiveHandle[0].getRadius()/activeHandle.getRadius();
            float ratio2 = inactiveHandle[1].getRadius()/activeHandle.getRadius();
            float ratio3 = inactiveHandle[2].getRadius()/activeHandle.getRadius();
            activeHandle.calculateModifier(dist); 
            inactiveHandle[0].calculateModifier(dist * ratio1);  
            inactiveHandle[1].calculateModifier(dist * ratio2);
            inactiveHandle[2].calculateModifier(dist * ratio3);
        }
        else{
            activeHandle.calculateModifier(dist);  
        }
    }
    
    @Override
    void finishShape(){
        super.finishShape();
        if(alt){
            //Moves the position from corner to center
            float fullWidth = widthHandleR.getRadius();
            float fullHeight = heightHandleB.getRadius();
            setPosition(pos.x + fullWidth/2, pos.y + fullHeight/2);
            
            //Moves the handles to half the width and height
            widthHandleL.calculateModifier(app.abs(fullWidth/2));
            heightHandleT.calculateModifier(app.abs(fullHeight/2));
            widthHandleR.calculateModifier(app.abs(fullWidth/2));
            heightHandleB.calculateModifier(app.abs(fullHeight/2));
            
            alt = false;
        }
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
        heightHandleB.setRadius();

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
    }
    
    @Override
    Shape copy(int id){
        return new Rectangle(this, id);
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
        output += "\trectMode(CORNERS);\n";
        output += "\trect("+-widthHandleL.getRadius()+", "+-heightHandleT.getRadius()+", "+widthHandleR.getRadius()+", "+heightHandleB.getRadius()+");\n";
        output += "\tpopMatrix();\n\n";
        
        return output;
    }
    
    @Override
    PGraphics printToPGraphic(PGraphics ig){
        if(fillColor == NONE) ig.noFill();
        else ig.fill(fillColor);

        if(strokeWeight == 0) ig.noStroke();
        else{
            ig.stroke(strokeColor);
            ig.strokeWeight(strokeWeight);
        }
        
        ig.pushMatrix();
        ig.translate(pos.x, pos.y);
        ig.rotate(rotation);
        //Still uses corners drawing mode in order to enable assymetric scaling
        ig.rectMode(CORNERS);
        ig.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
        ig.popMatrix();
        return ig;
    }
    
    @Override
    String save(){
        String output ="Rectangle;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+startingRotation+","+rotation+","+strokeWeight+","+index+",";
        output += widthHandleR.save()+",";
        output += widthHandleL.save()+",";
        output += heightHandleT.save()+",";
        output += heightHandleB.save()+",";
        output += this.name;
        return output;
    }
  }
