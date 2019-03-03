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
public class Bezier extends Shape{
    enum Vertex {HEAD, TAIL, BOTH}
    VertexHandle start;
    VertexHandle end;
    VertexHandle startController;
    VertexHandle endController;
    VertexHandle activeHandle;
    VertexHandle inactiveHandle;
    VertexHandle[] vertices = new VertexHandle[4];
    final int SMALLEST_X = 0;
    final int SMALLEST_Y = 1;
    final int GREATEST_X = 2;
    final int GREATEST_Y = 3;
    int padding = 15;
    float[] boundingBox = {0,0,0,0}; // Index 0 is smallest x, 1 is smallest y, 2 is greatest x, 3 is greatest y
    
    
    
    Bezier(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
      super(drawingSpace, paint, outline, x,y);
      strokeWeight = thickness;
      index = id;
      start = new VertexHandle(drawingSpace, x,y);
      PVector point = new PVector(50,50);
      point.sub(new PVector(x,y));
      end = new VertexHandle(drawingSpace, point);
      startController = new VertexHandle(drawingSpace, x,y);
      endController = new VertexHandle(drawingSpace, point);
      name = "Bezier";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Bezier(Bezier base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      start = new VertexHandle(base.app, base.start.getPosition().x+base.COPY_OFFSET, base.start.getPosition().y+base.COPY_OFFSET);
      end = new VertexHandle(base.app, base.end.getPosition().x+base.COPY_OFFSET, base.end.getPosition().y+base.COPY_OFFSET);
      startController = new VertexHandle(base.app, base.startController.getPosition().x+base.COPY_OFFSET, base.startController.getPosition().y+base.COPY_OFFSET);
      endController = new VertexHandle(base.app, base.endController.getPosition().x+base.COPY_OFFSET, base.endController.getPosition().y+base.COPY_OFFSET);
      this.name = base.name;
      completed = true; //must keep in order to not override vertex information when finishShape() is called
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Bezier(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[4]), Float.valueOf(input[2]), Float.valueOf(input[3]), Integer.valueOf(input[5]));
        start = new VertexHandle(drawingSpace, input[6].split("&"));
        startController = new VertexHandle(drawingSpace, input[7].split("&"));
        end = new VertexHandle(drawingSpace, input[8].split("&"));
        endController = new VertexHandle(drawingSpace, input[9].split("&"));
        name = input[10];
        completed = true; //must keep in order to not override vertex information when finishShape() is called
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
        moveAnchor(Vertex.BOTH, mouse);
        calculateBoundingBox();
    }
    
    @Override
    void manipulate(float x, float y){
        manipulate(new PVector(x,y));
    }

    @Override
    void drawShape(){
        if(completed){
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
            //Bezier Constructor is ACCA (anchor, controller, controller, anchor)
            bezier(start.getPosition(), startController.getPosition() ,endController.getPosition(), end.getPosition());
        }
        else{
            app.stroke(0,0,0);
            app.strokeWeight(1);
            app.line(start.getPosition().x, start.getPosition().y, end.getPosition().x, end.getPosition().y);
        }
    }
    
        @Override
    void drawSelected(){
        app.pushMatrix();
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        bezier(start.getPosition(), startController.getPosition() ,endController.getPosition(), end.getPosition());
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
        app.line(start.getPosition().x, start.getPosition().y, startController.getPosition().x, startController.getPosition().y);
        app.line(end.getPosition().x, end.getPosition().y, endController.getPosition().x, endController.getPosition().y);
        start.drawHandle();
        end.drawHandle();
        startController.drawHandle();
        endController.drawHandle();
    }
    
    //Helper method to condense Bezier code using PVectors
    private void bezier(PVector head, PVector hc, PVector tc, PVector tail){
        app.bezier(head.x, head.y, hc.x, hc.y, tc.x, tc.y, tail.x, tail.y);
    }
    
    
    @Override
    void finishShape(){
        if(!completed){
            //Using this method to position the controller handles relative 
            //to the start and end vertex when first drawn to canvas.
            super.finishShape();
            PVector between = PVector.sub(start.getPosition(), end.getPosition());
            between.mult(0.4f);
            endController.setPosition(PVector.add(between, end.getPosition()));
            between.mult(-1);
            startController.setPosition(PVector.add(start.getPosition(), between));
        }
        vertices[0] = start;
        vertices[1] = end;
        vertices[2] = startController;
        vertices[3] = endController;
        calculateBoundingBox();
    }
    
    @Override
    void modify(PVector mouse){
      end.setPosition(mouse);
    }   
    
    @Override
    void resizeHandles(float size){
        start.scaleSize(size);
        end.scaleSize(size);
        startController.scaleSize(size);
        endController.scaleSize(size);
    }
    
    @Override
    boolean checkHandles(PVector mouse){
        if (start.overHandle(mouse)){
            activeHandle = start;
            inactiveHandle = end;
            return true;
        }
        else if(end.overHandle(mouse)){
            activeHandle = end;
            inactiveHandle = start;
            return true;
        }
        else if(startController.overHandle(mouse)){
            activeHandle = startController;
            inactiveHandle = endController;
            return true;
        }
        else if(endController.overHandle(mouse)){
            activeHandle = endController;
            inactiveHandle = startController;
            return true;
        }
        else return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        if(activeHandle == startController){
            activeHandle.setPosition(mouse);
        }
        else if(activeHandle == endController){
            activeHandle.setPosition(mouse);
        }
        else if(activeHandle == start){
            moveAnchor(Vertex.HEAD, mouse);
        }
        else{
            moveAnchor(Vertex.TAIL, mouse);
        }
        calculateBoundingBox();
    }
    
    @Override
    float[] getResetFloats(){
        float[] points = {
            start.getPosition().x,
            start.getPosition().y,
            startController.getPosition().x,
            startController.getPosition().y,
            end.getPosition().x,
            end.getPosition().y,
            endController.getPosition().x,
            endController.getPosition().y,
            rotation 
        };
        return points;
    }
    
    @Override
    void setHandles(float[] mods){
        start.setPosition(mods[0],mods[1]);
        startController.setPosition(mods[2],mods[3]);
        end.setPosition(mods[4],mods[5]);
        endController.setPosition(mods[6],mods[7]);
        calculateBoundingBox();
    }
    
    //Helper method that moves controller along with designated anchor. BOTH is
    //not technically necessary, but simplifies use.
    private void moveAnchor(Vertex v, PVector mouse){
        PVector delta = PVector.sub(startController.getPosition(),start.getPosition());;
        PVector delta2 = PVector.sub(endController.getPosition(), end.getPosition());
        switch(v){
            case HEAD:
                start.setPosition(mouse);
                startController.setPosition(PVector.add(mouse, delta));
                break;
            case TAIL:
                end.setPosition(mouse);
                endController.setPosition(PVector.add(mouse, delta2)); 
                break;
            case BOTH:
                PVector delta3 = PVector.sub(end.getPosition(), start.getPosition());
                moveAnchor(Vertex.HEAD, mouse);
                moveAnchor(Vertex.TAIL, PVector.add(mouse, delta3));          
        }
    }
    
    void setBoundingBox(float[] point1, float[] point2){
        float a = point1[0];
        float b = point1[1];
        float c = point2[0];
        float d = point2[1];
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
        for(int i = 0; i < vertices.length; i++){
            if(i == 0){
                setBoundingBox(vertices[i].getPositionFloats(),vertices[i].getPositionFloats());
            }
            calculateBoundingBox(vertices[i].getPositionFloats());
        }
    }
    
    @Override
    Shape copy(int id){
        Bezier copy = new Bezier(this, id);
        return copy;
    }

    @Override
    String printToClipboard(){
        String output = "";
        
        if(fillColor == NONE) output += "\tnoFill();\n";
        else output += "\tfill("+fillColor+");\n";
        if(strokeWeight == 0) output += "\tnoStroke();\n";
        else output += "\tstroke("+strokeColor+");\n\tstrokeWeight("+strokeWeight+");\n";
        
        output += "\tbezier("+start.getPosition().x+", "+start.getPosition().y+", "+startController.getPosition().x+", "+startController.getPosition().y+", "+endController.getPosition().x+", "+endController.getPosition().y+", "+end.getPosition().x+", "+end.getPosition().y+");\n";
        output += "\n";    
            
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
      //Bezier Constructor is ACCA (anchor, controller, controller, anchor)
      ig.bezier(start.getPosition().x, start.getPosition().y, startController.getPosition().x, startController.getPosition().y ,endController.getPosition().x, endController.getPosition().y, end.getPosition().x, end.getPosition().y);
      return ig;
    }
    
    @Override
    String save(){
        String output ="Bezier;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+strokeWeight+","+index+",";
        output += start.save()+",";
        output += startController.save()+",";
        output += end.save()+",";
        output += endController.save()+",";
        output += this.name;
        return output;
    }
}
