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
    float[] boundingBox = {0,0,0,0}; // Index 0 is smallest x, 1 is smallest y, 2 is greatest x, 3 is greatest y

    Line(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      name = "Line";
      index = id;
      start = new VertexHandle(drawingSpace, x,y);
      PVector point = new PVector(50,50);
      point.sub(new PVector(x,y));
      end = new VertexHandle(drawingSpace, point);
    }
    
    //Copy Constructor
    Line(Line base, int id){
      super(base.app, base.fillColor, base.strokeColor, base.pos.x, base.pos.y);
      strokeWeight = base.strokeWeight;
      name = base.name;
      index = id;
      rotation = base.rotation;
      start = new VertexHandle(base.app, base.start.getPosition());
      end = new VertexHandle(base.app, base.end.getPosition());
    }
    
    //Load Constructor
    Line(PApplet drawingSpace, String[] input){
        super(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[2]), Float.valueOf(input[3]));
        strokeWeight = Float.valueOf(input[4]);
        completed = true;
        shift = false;
        name = "Line";
        index = Integer.valueOf(input[5]);
        start = new VertexHandle(drawingSpace, input[6].split("&"));
        end = new VertexHandle(drawingSpace, input[7].split("&"));
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
      //app.pushMatrix();
      app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
      //app.popMatrix();
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
    void finishHandles(){
        calculateBoundingBox();
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
        if(shift){
            float angle = PVector.angleBetween(PVector.sub(mouse, inactiveHandle.getPosition()), new PVector(1,0));
            float leftover = angle % QUARTER_PI;
            leftover = app.round(leftover);
            angle = app.floor(angle/QUARTER_PI)*QUARTER_PI+(leftover*QUARTER_PI);  
            
            if(angle == 0 || angle % PI == 0){
                activeHandle.setPosition(new PVector(mouse.x, inactiveHandle.getPosition().y));
            }
            else if(angle % HALF_PI == 0 ){
                activeHandle.setPosition(new PVector(inactiveHandle.getPosition().x, mouse.y));
            }
            else {
                float dist = mouse.dist(inactiveHandle.getPosition());
                int direction;
                if(mouse.y > inactiveHandle.getPosition().y){
                    direction = 1;
                }
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
    float[] getHandles(){
        float[] points = {
            start.getPosition().x,
            start.getPosition().y,
            end.getPosition().x,
            end.getPosition().y,
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
        //some area in thos situations
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
        output += end.save();
        return output;
    }
  }