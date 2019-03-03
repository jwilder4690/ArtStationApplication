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
public class Picture extends Shape{
    PImage img;
    String imageLocation;
    VertexHandle cornerHandle;
    
    Picture(PApplet drawingSpace, String location, int outline, float thickness, float a, float b, int id){ 
        super(drawingSpace, -1 , outline, a,b);
        imageLocation = location;
        img = app.loadImage(location);
        strokeWeight = thickness;
        index = id;
        cornerHandle = new VertexHandle(app, img.width, img.height);
        name = "Picture";
    }
    
    /*
      Copy Constructor
      Used for creating an exact copy of base shape.
    */
    Picture(Picture base, int id){
      this(base.app, base.imageLocation, base.strokeColor, base.strokeWeight, base.pos.x+base.COPY_OFFSET, base.pos.y+base.COPY_OFFSET, id);
      rotation = base.rotation;
      cornerHandle = new VertexHandle(base.app, base.cornerHandle.getPosition());
      this.name = base.name;
    }
    
    /*
      Load Constructor
      Used for creating shape from information stored in save file.
    */ 
    Picture(PApplet drawingSpace, String[] input){
        this(drawingSpace, input[0], Integer.valueOf(input[1]), Float.valueOf(input[6]),Float.valueOf(input[2]), Float.valueOf(input[3]), Integer.valueOf(input[7]));
        startingRotation = Float.valueOf(input[4]);
        rotation = Float.valueOf(input[5]);
        name = input[9];
    }
       
    
    @Override 
    boolean mouseOver(PVector mouse){
        //Makes comparison at origin
        float deltaX = mouse.x-pos.x;
        float deltaY = mouse.y-pos.y;
        //Applies rotation matrix
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        
        //Checks if outside the box
        if(rotX < 0 || rotX > cornerHandle.getPosition().x) return false;
        if(rotY < 0 || rotY > cornerHandle.getPosition().y) return false;
        return true;
    }
    

    @Override
    void drawShape(){
        app.noFill(); 
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
        app.rectMode(app.CORNERS);
        app.rect(0,0, cornerHandle.getPosition().x, cornerHandle.getPosition().y); //used to create stroke for image
        app.image(img, 0,0, cornerHandle.getPosition().x, cornerHandle.getPosition().y);
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
        app.rect(0,0, cornerHandle.getPosition().x, cornerHandle.getPosition().y);
        cornerHandle.drawHandle();
        app.popMatrix();
    }


    @Override
    void modify(PVector mouse){
        //Unused because pictures just load in at 0,0 for ease of use.
    }
    
        @Override
    void resizeHandles(float size){
        cornerHandle.scaleSize(size);
    }

    @Override
    /*
      Applies a rotation matrix to account for rotation of picture, then uses
      rotated coordinates to check if over handle. *Note* This logic is same as 
      logic used in polygon if reference is needed. TODO: Find way to avoid doing
      rotatedMouse calculation as often, see next method as well.
    */
    boolean checkHandles(PVector mouse){
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rotatedMouse = new PVector(rotX, rotY);
        return cornerHandle.overHandle(rotatedMouse);
    }
    
        @Override
    void adjustActiveHandle(PVector mouse){
        //TODO: Find way to avoid doing rotatedMouse calculation as often
        float deltaX = mouse.x - pos.x;
        float deltaY = mouse.y - pos.y;
        float rotX = deltaX*app.cos(-rotation) - deltaY*app.sin(-rotation);
        float rotY = deltaY*app.cos(-rotation) + deltaX*app.sin(-rotation);
        PVector rotatedMouse = new PVector(rotX, rotY);
        if(shift){
            float ratio = img.height/(float)img.width; //maintains original image proportions
            rotatedMouse = new PVector(rotX, rotX*ratio);
        }
        cornerHandle.setPosition(rotatedMouse);
    }
    
    @Override
    void finishShape(){
        super.finishShape();
    }
    
    @Override
    float[] getResetFloats(){
        return new float[] {cornerHandle.getPosition().x, cornerHandle.getPosition().y, rotation};
    }
    
    @Override
    void setHandles(float[] mods){
        cornerHandle.setPosition(mods[0], mods[1]);
    }
    
    @Override
    void reset(){
        rotation = 0;
        cornerHandle.setPosition(img.width, img.height);
        this.setPosition(0,0);
    }
    
    @Override
    Shape copy(int id){
        return new Picture(this, id);
    }
    

    
    @Override
    String printToClipboard(){
        String output = "";
        output += "/*ADD ME AS A GLOBAL VARIABLE!\n\n";
        output += " PImage img"+index+";\n\n";
        output += "PUT ME IN YOUR setup() FUNCTION!\n\n";
        output += "img"+index+" = loadImage(\""+imageLocation+"\");\n\n";
        output += "*/\n";
        
        output += "\tnoFill();\n";
        if(strokeWeight == 0) output += "\tnoStroke();\n";
        else output += "\tstrokeWeight("+strokeWeight+");\n\tstroke("+strokeColor+");\n";

        output += "\tpushMatrix();\n";
        output += "\ttranslate("+pos.x+", "+pos.y+");\n";
        output += "\trotate("+rotation+");\n";
        output += "\trectMode(CORNERS);\n";
        output += "\trect(0, 0, "+cornerHandle.getPosition().x+", "+cornerHandle.getPosition().y+");\n";
        output += "\timage(img"+index+", 0, 0, "+cornerHandle.getPosition().x+", "+cornerHandle.getPosition().y+");\n";
        output += "\tpopMatrix();\n\n";
        
        return output;
    }
    
    @Override
    PGraphics printToPGraphic(PGraphics ig){
    //This works for exporting an image, not for exporting an SVG. 
        ig.noFill();
        if(strokeWeight == 0) ig.noStroke();
        else{
            ig.stroke(strokeColor);
            ig.strokeWeight(strokeWeight);
        }
        
        ig.pushMatrix();
        ig.translate(pos.x, pos.y);
        ig.rotate(rotation);
        ig.rectMode(app.CORNERS);
        ig.rect(0,0, cornerHandle.getPosition().x, cornerHandle.getPosition().y); //used to create stroke for image
        ig.image(img, 0,0, cornerHandle.getPosition().x, cornerHandle.getPosition().y);
        ig.popMatrix();
        return ig;
    }
    
    @Override
    String save(){
        String output ="Picture;";
        output += imageLocation+","+strokeColor+","+pos.x+","+pos.y+","+startingRotation+","+rotation+","+strokeWeight+","+index+",";
        output += cornerHandle.save()+",";
        output += this.name;
        return output;
    }
    
    //Temporary
    @Override
    boolean isPicture(){
        return true;
    }
}