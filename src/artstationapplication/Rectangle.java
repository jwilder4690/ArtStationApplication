/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;
import processing.core.*;
import javafx.scene.paint.Color;
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

    Rectangle(PApplet drawingSpace, int paint, int outline, float thickness, float a, float b, int id, boolean style){ 
        super(drawingSpace, paint, outline, a,b);
        strokeWeight = thickness;
        name = "Rectangle";
        index = id;
        widthHandleR = new Handle(drawingSpace, this, new PVector(1,0));
        widthHandleL = new Handle(drawingSpace, this, new PVector(-1,0));
        heightHandleB = new Handle(drawingSpace, this, new PVector(0,1));
        heightHandleT = new Handle(drawingSpace, this, new PVector(0,-1));
        centerType = style;
        corner = new PVector(a+1,b+1); //default corner, will not be displayed 
    }
    
    //Copy constructor
    Rectangle(Rectangle base, int id){
      super(base.app, base.fillColor, base.strokeColor, base.pos.x, base.pos.y);
      strokeWeight = base.strokeWeight;
      name = base.name;
      index = id;
      widthHandleR = new Handle(base.widthHandleR, this);
      widthHandleL = new Handle (base.widthHandleL, this);
      heightHandleB = new Handle (base.heightHandleB, this);
      heightHandleT = new Handle (base.heightHandleT, this);
      rotation = base.rotation;
      centerType = base.centerType;
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
      if(centerType){
        //Still uses corners drawing mode in order to enable assymetric scaling
        app.rectMode(CORNERS);
        app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
      }
      //Currently not using, alt draw mode maybe?
      else if(!centerType){
        app.rectMode(CORNERS);
        app.rect(0,0, corner.x, corner.y);
      }
      app.popMatrix();
    }
    
    @Override
    void drawSelected(){
      app.pushMatrix();
      app.translate(pos.x, pos.y);
      app.rotate(rotation);
      if(centerType){
        //Still uses corners drawing mode in order to enable assymetric scaling
        app.rectMode(CORNERS);
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
        drawHandles();
      }
      //Currently not using, alt draw mode maybe?
      else if(!centerType){
        app.rectMode(CORNERS);
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.rect(0,0, corner.x, corner.y);
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
            widthHandleR.setModifier(radius);
            widthHandleL.setModifier(radius);
            heightHandleT.setModifier(radius);
            heightHandleB.setModifier(radius);
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
            activeHandle.setModifier(dist); 
            inactiveHandle[0].setModifier(dist - dist * delta0);  
            inactiveHandle[1].setModifier(dist - dist * delta1);
            inactiveHandle[2].setModifier(dist - dist * delta2);
        }
        else{
            activeHandle.setModifier(dist);  
        }
    }
    
    @Override
    void finishHandles(){
        widthHandleL.setRadius();
        widthHandleR.setRadius();
        heightHandleT.setRadius();
        heightHandleB.setRadius();
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
      if(centerType){
        //Still uses corners drawing mode in order to enable assymetric scaling
        ig.rectMode(CORNERS);
        ig.rect(-widthHandleL.getRadius(), -heightHandleT.getRadius(), widthHandleR.getRadius(), heightHandleB.getRadius());
      }
      //Currently not using, alt draw mode maybe?
      else if(!centerType){
        ig.rectMode(CORNERS);
        ig.rect(0,0, corner.x, corner.y);
      }
      ig.popMatrix();
      return ig;
    }
    
        @Override
    String save(){
        String output ="";
        return output;
    }
  }
