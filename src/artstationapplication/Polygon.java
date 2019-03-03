/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;
import java.util.ArrayList;
import processing.core.*;

/**
 *
 * @author wilder4690
 */
public class Polygon extends Shape{
    ArrayList<VertexHandle> vertices = new ArrayList<>();
    VertexHandle activeHandle;
    final int SMALLEST_X = 0;
    final int SMALLEST_Y = 1;
    final int GREATEST_X = 2;
    final int GREATEST_Y = 3;
    float[] boundingBox = new float[4];  

    
    Polygon(PApplet drawingSpace, int paint, int outline, float thickness, float x, float y, int id){
        super(drawingSpace, paint, outline, x,y);
        strokeWeight = thickness;
        index = id;
        name = "Polygon";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Polygon(Polygon base, int id){
      this(base.app, base.fillColor, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      rotation = base.rotation;
      for(int i = 0; i < base.vertices.size(); i++){
          vertices.add(new VertexHandle(base.app, base.vertices.get(i).getPosition()));
      }
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Polygon(PApplet drawingSpace, String[] input){
        this(drawingSpace, Integer.valueOf(input[0]), Integer.valueOf(input[1]), Float.valueOf(input[6]), Float.valueOf(input[2]), Float.valueOf(input[3]),Integer.valueOf(input[7]));
        startingRotation = Float.valueOf(input[4]);
        rotation = Float.valueOf(input[5]);
        name = input[8];
        for(int i = 0; i < Integer.valueOf(input[9]); i++){
            vertices.add(new VertexHandle(drawingSpace, input[10+i].split("&")));
        }
    }
    
    /*
      TODO:
        Figure out method to change pivot point. May need to loop through each point
        and adjust by the same offset as the origin moved? 
    
    void adjustOrigin(PVector point){
        //fix (check handles is commented out for now)
        PVector delta = PVector.sub(point, pos);
        setPosition(point.x, point.y);
        for(int i = 1; i < vertices.size(); i++){
            vertices.get(i).shift(delta);
        }
    }
    */
    
    void addVertex(float x, float y){
        if(vertices.isEmpty()){
            setBoundingBox(0,0,0,0);
            setPosition(x,y);
            vertices.add(new VertexHandle(app, 0,0));
            origin = new PVector(x,y);
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
            calculateBoundingBox(x,y);
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
    void resizeHandles(float size){
        for(int i = 0; i < vertices.size(); i++){
            vertices.get(i).scaleSize(size);
        }
    }
    
    @Override
    /*
      Applies a rotation matrix to account for rotation of polygon, then uses
      rotated coordinates to check if over handles. TODO: Find way to avoid doing
      rotatedMouse calculation as often, see next method as well.
    */
    boolean checkHandles(PVector mouse){
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rotatedMouse = new PVector(rotX, rotY);
        for(int i = 0; i < vertices.size(); i++){
            if(vertices.get(i).overHandle(rotatedMouse)){
                activeHandle = vertices.get(i);
                return true;
            }
        }
        return false;
    }

    @Override
    void adjustActiveHandle(PVector mouse){
        //TODO: Find way to avoid doing rotatedMouse calculation as often
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rotatedMouse = new PVector(rotX, rotY);
        activeHandle.setPosition(rotatedMouse);
        calculateBoundingBox();
    }
    
    @Override
    float[] getResetFloats(){
        float[] points = new float[(2*vertices.size())+1];      
        for(int i = 0; i < vertices.size(); i++){
            points[2*i] = vertices.get(i).getPosition().x;
            points[2*i+1] = vertices.get(i).getPosition().y;
        }
        points[points.length-1] = rotation;
        return points;
    }
    
    @Override
    void setHandles(float[] mods){
        for(int i = 0; i < vertices.size(); i++){
            vertices.get(i).setPosition(mods[2*i], mods[2*i+1]);
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
        else if(rotY < boundingBox[SMALLEST_Y] || rotY > boundingBox[GREATEST_Y]) return false;
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
            //Origin/pivot point
            app.fill(0,255,0);
            app.ellipse(0,0, 15,15);
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
        //Origin/pivot point
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
    void finishShape(){
        super.finishShape();
        calculateBoundingBox();
    }
    
    @Override
    void modify(PVector mouse){
        addVertex(mouse);
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
            ig.endShape(app.CLOSE);
        }
        ig.popMatrix();
        return ig;
    }
    
    @Override
    String save(){
        String output ="Polygon;";
        output += fillColor+","+strokeColor+","+pos.x+","+pos.y+","+startingRotation+","+rotation+","+strokeWeight+","+index+",";
        output += this.name+",";
        output += vertices.size()+",";
        for(int i = 0; i < vertices.size(); i++){
            output += vertices.get(i).save();
            if(i != vertices.size()-1){
                output += ",";
            }
        }
        return output;
    }
}
