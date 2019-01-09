/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;
import java.util.ArrayList;
import processing.core.*;
import java.util.Collections;

/**
 *
 * @author wilder4690
 */
public class Polygon extends Shape{
    ArrayList<VertexHandle> vertices = new ArrayList<>();
    VertexHandle activeHandle;
    VertexHandle origin;
    final int SMALLEST_X = 0;
    final int SMALLEST_Y = 1;
    final int GREATEST_X = 2;
    final int GREATEST_Y = 3;
    float[] boundingBox = new float[4]; // Index 0 is smallest x, 1 is smallest y, 2 is greatest x, 3 is greatest y  

    
    Polygon(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
        super(drawingSpace, paint, outline, x,y);
        strokeWeight = thickness;
        name = "Polygon";
        index = id;
        origin = new VertexHandle(app, 0,0);
    }
    
    //Copy Constructor
    Polygon(Polygon base, int id){
      super(base.app, base.fillColor, base.strokeColor, base.pos.x, base.pos.y);
      strokeWeight = base.strokeWeight;
      name = base.name;
      index = id;
      rotation = base.rotation;
      origin = new VertexHandle(base.app, base.origin.getPosition());
      for(int i = 0; i < base.vertices.size(); i++){
          vertices.add(new VertexHandle(base.app, base.vertices.get(i).getPosition()));
      }
      completed = base.completed;
      this.calculateBoundingBox();
    }
    
    void adjustOrigin(PVector point){
        //fix (check handles is commented out for now)
        PVector delta = PVector.sub(point, pos);
        setPosition(point.x, point.y);
        for(int i = 1; i < vertices.size(); i++){
            vertices.get(i).shift(delta);
        }
    }
    
    void addVertex(float x, float y){
        
        if(vertices.isEmpty()){
            setBoundingBox(0,0,0,0);
            setPosition(x,y);
            vertices.add(new VertexHandle(app, 0,0));
        }
        else{
            PVector vPos = new PVector(x,y);
            vPos.sub(pos);
            vertices.add(new VertexHandle(app, vPos));
            calculateBoundingBox(vPos.x,vPos.y);
        }
    }
    
    void addVertex(PVector mouse){
        addVertex(mouse.x, mouse.y);
    }
    
    void setBoundingBox(float a, float b, float c, float d){
        boundingBox[SMALLEST_X] = a;
        boundingBox[SMALLEST_Y] = b;
        boundingBox[GREATEST_X] = c;
        boundingBox[GREATEST_Y] = d;        
    }
    
    void calculateBoundingBox(){
        for(int i = 0; i < vertices.size(); i++){

            float x = vertices.get(i).getPosition().x;
            float y = vertices.get(i).getPosition().y;
            if(i == 0){
                setBoundingBox(x,y,x,y);
            }
            if(x > boundingBox[GREATEST_X]) boundingBox[GREATEST_X] = x;
            if(x < boundingBox[SMALLEST_X]) boundingBox[SMALLEST_X] = x;
            if(y > boundingBox[GREATEST_Y]) boundingBox[GREATEST_Y] = y;
            if(y < boundingBox[SMALLEST_Y]) boundingBox[SMALLEST_Y] = y;
        }
    }
    
    //Compares new point to existing bounding box points to see if new extreme
    void calculateBoundingBox(float x, float y){
        if(x > boundingBox[GREATEST_X]) boundingBox[GREATEST_X] = x;
        if(x < boundingBox[SMALLEST_X]) boundingBox[SMALLEST_X] = x;
        if(y > boundingBox[GREATEST_Y]) boundingBox[GREATEST_Y] = y;
        if(y < boundingBox[SMALLEST_Y]) boundingBox[SMALLEST_Y] = y;
    }
    
    @Override
    boolean checkHandles(PVector mouse){
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rot = new PVector(rotX, rotY);
        for(int i = 0; i < vertices.size(); i++){
                if(vertices.get(i).overHandle(rot)){
                    activeHandle = vertices.get(i);
                    return true;
                }
            }
        return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rot = new PVector(rotX, rotY);
        activeHandle.setPosition(rot);
        if(activeHandle == origin){
            adjustOrigin(rot);
        }
        calculateBoundingBox();
    }
       
    @Override
    boolean mouseOver(PVector mouse){
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);

        if(rotX < boundingBox[SMALLEST_X] || rotX > boundingBox[GREATEST_X]) return false;
        if(rotY < boundingBox[SMALLEST_Y] || rotY > boundingBox[GREATEST_Y]) return false;
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
        if(completed){
            app.beginShape();
            for(int i = 0; i < vertices.size(); i++){
                app.vertex(vertices.get(i).getPositionFloats());
            }
            app.endShape(app.CLOSE);
        }
        if(!completed){
            app.noFill();
            app.strokeWeight(1);
            app.beginShape();
            for(int i = 0; i < vertices.size(); i++){
                app.vertex(vertices.get(i).getPositionFloats());
            }
            app.endShape();
            for(int i = 0; i < vertices.size(); i++){
                vertices.get(i).drawHandle();
            }
        }
        app.popMatrix();    
    }
    
        @Override
    void drawSelected(){
        app.pushMatrix();
        app.translate(pos.x, pos.y);
        app.rotate(rotation);
        app.noFill();
        app.strokeWeight(3);
        app.stroke(255,255, 0);
        app.beginShape();
        for(int i = 0; i < vertices.size(); i++){
            app.vertex(vertices.get(i).getPositionFloats());
        }
        app.endShape(app.CLOSE); 
        drawHandles();
        //pivot point
        app.fill(0,255,0);
        app.ellipse(0,0, 15,15);
        //bounding box
        app.noFill();
        app.stroke(0,0,0);
        app.strokeWeight(1);
        app.rectMode(app.CORNERS);
        app.rect(boundingBox[SMALLEST_X], boundingBox[SMALLEST_Y], boundingBox[GREATEST_X], boundingBox[GREATEST_Y]);
        app.fill(0,255,0);
        app.ellipse(0,0, 15,15);
        app.popMatrix();
    }

    void drawHandles(){
        for(int i = 0; i < vertices.size(); i++){
            vertices.get(i).drawHandle();
        }
    }
    
    @Override
    void modify(PVector mouse){
        addVertex(mouse);
    }

    @Override
    void finishHandles(){
        //pass
    }
    
        @Override
    Shape copy(int id){
        return new Polygon(this, id);
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
        output += "\tbeginShape();\n";
        for(int i = 0; i < vertices.size(); i++){
            output += "\tvertex("+vertices.get(i).getPosition().x+", "+vertices.get(i).getPosition().y+");\n"; 
        }
        output += "\tendShape(CLOSE);\n";
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
        if(completed){
            ig.beginShape();
            for(int i = 0; i < vertices.size(); i++){
                ig.vertex(vertices.get(i).getPositionFloats());
            }
            ig.endShape(ig.CLOSE);
        }
        return ig;
    }
}
