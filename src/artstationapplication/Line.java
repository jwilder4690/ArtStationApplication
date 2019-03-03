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
 class Line extends Shape{
    VertexHandle start;
    VertexHandle end;
    VertexHandle activeHandle;
    VertexHandle inactiveHandle;
    final int SMALLEST_X = 0;
    final int SMALLEST_Y = 1;
    final int GREATEST_X = 2;
    final int GREATEST_Y = 3;
    int padding = 15;
    float[] boundingBox = {0,0,0,0};

    Line(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      index = id;
      start = new VertexHandle(drawingSpace, x,y);
      PVector point = new PVector(50,50);
      point.sub(new PVector(x,y));
      end = new VertexHandle(drawingSpace, point);
      name = "Line";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Line(Line base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      start = new VertexHandle(base.app, base.start.getPosition().x+base.COPY_OFFSET, base.start.getPosition().y+base.COPY_OFFSET);
      end = new VertexHandle(base.app, base.end.getPosition().x+base.COPY_OFFSET, base.end.getPosition().y+base.COPY_OFFSET);
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Line(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[4]), Float.valueOf(input[2]), Float.valueOf(input[3]),Integer.valueOf(input[5]));
        start = new VertexHandle(drawingSpace, input[6].split("&"));
        end = new VertexHandle(drawingSpace, input[7].split("&"));
        name = input[8];
    }

    @Override
    float[] getPositionFloats(){
        return start.getPositionFloats();
    }
    
    @Override
    boolean mouseOver(PVector mouse){
        if(mouse.x < boundingBox[SMALLEST_X] || mouse.x > boundingBox[GREATEST_X]) return false;
        if(mouse.y < boundingBox[SMALLEST_Y] || mouse.y > boundingBox[GREATEST_Y]) return false;
        return true;
    }
    
    @Override
    //calculates offset between start and end, and maintain it after moving start
    void manipulate(PVector mouse){
        PVector lineDelta = PVector.sub(end.getPosition(), start.getPosition());
        start.setPosition(mouse);
        end.setPosition(PVector.add(start.getPosition(), lineDelta));
        calculateBoundingBox();
    }

    @Override
    void drawShape(){
      if(strokeWeight == 0){
          app.noStroke();
      }
      else{
        app.stroke(strokeColor);
        app.strokeWeight(strokeWeight);
      }
      app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
    }
    
    @Override
    void drawSelected(){
        app.pushMatrix();
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
        //bounding box
        app.noFill();
        app.stroke(0,0,0);
        app.strokeWeight(1);
        app.rectMode(app.CORNERS);
        app.rect(boundingBox[SMALLEST_X], boundingBox[SMALLEST_Y], boundingBox[GREATEST_X], boundingBox[GREATEST_Y]);
        drawHandles();
        app.popMatrix();
    }
    
    void drawHandles(){
        start.drawHandle();
        end.drawHandle();
    }

    @Override
    void modify(PVector mouse){
      end.setPosition(mouse);
    }
    
    @Override
    void resizeHandles(float size){
        start.scaleSize(size);
        end.scaleSize(size);
    }
    
    @Override
    void finishShape(){
        calculateBoundingBox();
        super.finishShape();
    }

    @Override
    boolean checkHandles(PVector mouse){
        if (start.overHandle(mouse)){
            activeHandle = start;
            inactiveHandle = end;
            return true;
        }
        else if( end.overHandle(mouse)){
            activeHandle = end;
            inactiveHandle = start;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        /*
            Holding shift can be used to maintain vertical, horizontal, or diagonal lines.
            The angle of the mouse relative to the x unit vector is calculated and rounded 
            to the nearest PI/4. That angle is used to determine behavior. 
        */
        if(shift){
            float angle = PVector.angleBetween(PVector.sub(mouse, inactiveHandle.getPosition()), new PVector(1,0));
            float leftover = angle % QUARTER_PI;
            leftover = app.round(leftover);
            angle = app.floor(angle/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);  
            
            if(angle == 0 || angle % PI == 0){ //Horizontal
                activeHandle.setPosition(new PVector(mouse.x, inactiveHandle.getPosition().y));
            }
            else if(angle % HALF_PI == 0 ){ //Vertical
                activeHandle.setPosition(new PVector(inactiveHandle.getPosition().x, mouse.y));
            }
            else { //Diagonal
                float dist = mouse.dist(inactiveHandle.getPosition());
                int direction;
                if(mouse.y > inactiveHandle.getPosition().y) direction = 1;
                else direction = -1;
                
                PVector newAngle = new PVector(app.cos(angle), direction*app.sin(angle));
                newAngle.mult(dist);
                activeHandle.setPosition(PVector.add(newAngle, inactiveHandle.getPosition()));
            }
        }
        else{
            activeHandle.setPosition(mouse);
        }
         calculateBoundingBox();
    }
    
    @Override
    float[] getResetFloats(){
        float[] points = {
            start.getPosition().x,
            start.getPosition().y,
            end.getPosition().x,
            end.getPosition().y,
            rotation
        };
        return points;
    }
    
    @Override
    void setHandles(float[] mods){
        start.setPosition(mods[0],mods[1]);
        end.setPosition(mods[2],mods[3]);
        calculateBoundingBox();
    }
     
    void setBoundingBox(float[] point){
        float a = point[0];
        float b = point[1];
        float c = point[0];
        float d = point[1];
        //padding is for vertical or horizontal line, to give the bounding box
        //some area in those situations
        boundingBox[SMALLEST_X] = a - padding;
        boundingBox[SMALLEST_Y] = b - padding;
        boundingBox[GREATEST_X] = c + padding;
        boundingBox[GREATEST_Y] = d + padding;        
    }
 
     //Compares new point to existing bounding box points to see if new extreme
    void calculateBoundingBox(float[] point){
        float x = point[0];
        float y = point[1];
        if(x > boundingBox[GREATEST_X]) boundingBox[GREATEST_X] = x + padding;
        if(x < boundingBox[SMALLEST_X]) boundingBox[SMALLEST_X] = x - padding;
        if(y > boundingBox[GREATEST_Y]) boundingBox[GREATEST_Y] = y + padding;
        if(y < boundingBox[SMALLEST_Y]) boundingBox[SMALLEST_Y] = y - padding;
    }
        
    void calculateBoundingBox(){
        setBoundingBox(start.getPositionFloats());
        calculateBoundingBox(end.getPositionFloats());
    }
    
        @Override
    Shape copy(int id){
        Line copy = new Line(this, id);
        copy.calculateBoundingBox();
        return copy;
    }
    
        @Override
    String printToClipboard(){
        String output = "";
        if(strokeWeight == 0) output += "\tnoStroke();\n";
        else output += "\tstrokeWeight("+strokeWeight+");\n\tstroke("+strokeColor+");\n";

        output += "\tline("+start.getPosition().x+", "+start.getPosition().y+", "+end.getPosition().x+", "+end.getPosition().y+");\n";
        
        return output;
    }
    
    @Override
    PGraphics printToPGraphic(PGraphics ig){
       if(strokeWeight == 0){
          ig.noStroke();
      }
      else{
        ig.stroke(strokeColor);
        ig.strokeWeight(strokeWeight);
      }
      ig.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
      return ig;
    }
    
    @Override
    String save(){
        String output ="Line;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+strokeWeight+","+index+",";
        output += start.save()+",";
        output += end.save()+",";
        output += this.name;
        return output;
    }
  }