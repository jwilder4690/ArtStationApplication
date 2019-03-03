/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import javafx.scene.paint.Color;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import processing.core.PApplet;
import static processing.core.PConstants.PI;
import processing.core.PVector;


/**
 *
 * @author wilder4690
 */
public class ArtStationApplicationTest {
    
    public ArtStationApplicationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    
    /**
     * Test of convertStringToDouble method, of class ArtStationApplication.
     */
    @Test
    public void testConvertColorToInt() {
        System.out.println("convertColorToInt");
        Color col = Color.CADETBLUE;
        ArtStationApplication instance = new ArtStationApplication();
        int result = instance.convertColorToInt(col);
        System.out.println(result);
        //assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of convertStringToDouble method, of class ArtStationApplication.
     */
    @Test
    public void testConvertStringToDouble() {
        System.out.println("convertStringToDouble");
        String number = "2";
        ArtStationApplication instance = new ArtStationApplication();
        double expResult = 2.0;
        double result = instance.convertStringToDouble(number);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of convertStringToInt method, of class ArtStationApplication.
     */
    @Test
    public void testConvertStringToInt() {
        System.out.println("convertStringToInt");
        String number = "2";
        ArtStationApplication instance = new ArtStationApplication();
        int expResult = 2;
        int result = instance.convertStringToInt(number);
        assertEquals(expResult, result);
    }

    /**
     * Test of cleanseFilePath method, of class ArtStationApplication.
     */
    @Test
    public void testCleanseFilePath() {
        System.out.println("cleanseFilePath");
        String path = "Documents\\GitHub\\ArtStationApplication\\test";
        ArtStationApplication instance = new ArtStationApplication();
        String expResult = "Documents/GitHub/ArtStationApplication/test";
        String result = instance.cleanseFilePath(path);
        assertEquals(expResult, result);
    }
    
    /*
        Behavior Tests
    */
    @Test
    public void testBehavior(){
        System.out.println("Behavior Test "
                + "\n After this test runs successfully, open images in testOutputs folder and compare to images inside expectedOutputs. "
                + "\n Run the main app and open the saved file located in the savedTestFile. "
                + "\n All outputs should match. \n");
        String[] processingArgs = {"Art Station"};
        ArtStationApplication app = new ArtStationApplication();
        PApplet.runSketch(processingArgs, app); 
        
        app.pad.setBackgroundColor(-10510688);
        app.pad.editMode();
        
        /////////////////////////////Circle Tests////////////////////////////////
        app.pad.setActiveTool(ShapeType.CIR);
        app.pad.setCurrentStrokeWeight(3);
        int row = 50;
        app.pad.createShape(50, row);
        app.pad.createShape(50, row);
        app.pad.shapes.get(app.pad.listIndex).manipulate(150,row);
        app.pad.createShape(250, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(250,row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(250, row-25));
        app.pad.createShape(350, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(350, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(350,row-25));
        app.pad.shapes.get(app.pad.listIndex).setStartingRotation(new PVector(400,row));
        app.pad.shapes.get(app.pad.listIndex).changeRotation(new PVector(400, row+50));
        app.pad.createShape(450, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(400, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(425,row));
        app.copyShape();
        app.pad.createShape(550, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(550, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(550, row-25));
        app.pad.shapes.get(app.pad.listIndex).setRotation(PI/4);
        app.pad.shapes.get(app.pad.listIndex).reset();
        app.pad.createShape(650, row);
        app.deleteShape();
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(750, row);
        app.pad.createShape(850, row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        /////////////////////////Rectangle Tests////////////////////////////////
        app.pad.setActiveTool(ShapeType.REC);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        row += 100;
        
        app.pad.createShape(50, row);
        app.pad.createShape(50, row);
        app.pad.shapes.get(app.pad.listIndex).manipulate(150,row);
        app.pad.createShape(250, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(250,row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(250, row-25));
        app.pad.createShape(350, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(350, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(350,row-25));
        app.pad.shapes.get(app.pad.listIndex).setStartingRotation(new PVector(400,row));
        app.pad.shapes.get(app.pad.listIndex).changeRotation(new PVector(400, row+50));
        app.pad.createShape(450, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(500, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(475,row));
        app.copyShape();
        app.pad.createShape(550, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(550, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(550, row-25));
        app.pad.shapes.get(app.pad.listIndex).setRotation(PI/4);
        app.pad.shapes.get(app.pad.listIndex).reset();
        app.pad.createShape(650, row);
        app.deleteShape();
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(750, row);
        app.pad.createShape(850, row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        /////////////////////////Triangle Tests////////////////////////////////
        app.pad.setActiveTool(ShapeType.TRI);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        row += 150;
        
        app.pad.createShape(50, row);
        app.pad.createShape(50, row);
        app.pad.shapes.get(app.pad.listIndex).manipulate(150,row);
        app.pad.createShape(250, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(250,row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(250, row-75));
        app.pad.createShape(350, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(350, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(350,row-75));
        app.pad.shapes.get(app.pad.listIndex).setStartingRotation(new PVector(400,row));
        app.pad.shapes.get(app.pad.listIndex).changeRotation(new PVector(400, row+50));
        app.pad.createShape(450, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(500, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(475,row));
        app.copyShape();
        app.pad.createShape(550, row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(550, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(550, row-25));
        app.pad.shapes.get(app.pad.listIndex).setRotation(PI/4);
        app.pad.shapes.get(app.pad.listIndex).reset();
        app.pad.createShape(650, row);
        app.deleteShape();
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(750, row);
        app.pad.createShape(850, row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        /////////////////////////Polygon Tests////////////////////////////////
        app.pad.setActiveTool(ShapeType.POL);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        row += 100;
        
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(200,row);
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(300,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(200,row-100));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(200, row-50));
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(400,row+50);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(300, row-50));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(300,row));
        app.pad.shapes.get(app.pad.listIndex).setStartingRotation(new PVector(400,row));
        app.pad.shapes.get(app.pad.listIndex).changeRotation(new PVector(400+25, row));
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(500,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(500, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(475,row));
        app.copyShape();
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(600,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(500, row-100));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(500, row-50));
        app.pad.shapes.get(app.pad.listIndex).setRotation(PI/4);
        app.pad.shapes.get(app.pad.listIndex).reset(); //doesnt reset poly handles
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(700,row);
        app.deleteShape();
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(800,row);
        app.pad.createShape(100, row);
        app.pad.completeVertex(100, row);
        app.pad.completeVertex(0, row);
        app.pad.completeVertex(0, row-100);
        app.pad.completeVertex(50, row-100);
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(900,row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        ////////////////////////////Line Tests//////////////////////////////////
        app.pad.setActiveTool(ShapeType.LIN);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        row += 100;
        
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(100,row);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(200,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(300, row-100));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(300, row-50));
        //skip test space for rotation, as lines cannot be rotated
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(400,row);
        app.copyShape();
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(500,row);
        app.deleteShape();
        //skip test for reset, as reset does nothing to lines
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(700,row);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(800,row);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        ////////////////////////////Bezier Tests//////////////////////////////////
        app.pad.setActiveTool(ShapeType.CUR);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        row += 100;
        
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(100,row);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(200,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(300, row-100));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(300, row-50));
        //skip test space for rotation, as bezier curves cannot be rotated
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(400,row);
        app.copyShape();
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(500,row);
        app.deleteShape();
        //skip test for reset, as reset does nothing to bezier curves
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(700,row);
        app.pad.createShape(0, row);
        app.pad.shapes.get(app.pad.listIndex).modify(new PVector(100,row));
        app.pad.shapes.get(app.pad.listIndex).finishShape();
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(40, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(25, row-70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(60, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(75, row+70));
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(100, row));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(100, row-100));
        app.pad.shapes.get(app.pad.listIndex).manipulate(800,row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        /////////////////////////////Picture Tests///////////////////////////////
        app.pad.setActiveTool(ShapeType.PIC);
        app.pad.setCurrentFillColor(-1);
        app.pad.setCurrentStrokeColor(-16777216);
        //row can stay the same because pictures draw from upper left
        
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(0,row);
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(100,row);
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(200,row);
        app.pad.shapes.get(app.pad.listIndex).checkHandles(new PVector(300,row+100));
        app.pad.shapes.get(app.pad.listIndex).adjustActiveHandle(new PVector(275, row+75));
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(300,row);
        app.pad.shapes.get(app.pad.listIndex).setStartingRotation(new PVector(400,row));
        app.pad.shapes.get(app.pad.listIndex).changeRotation(new PVector(400, row+100));
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(400,row);
        app.copyShape();
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(500,row);
        app.pad.shapes.get(app.pad.listIndex).setRotation(PI/4);
        app.pad.shapes.get(app.pad.listIndex).reset();
        app.pad.shapes.get(app.pad.listIndex).manipulate(500,row); //have to move again becase reset also return picture to topleft as intended
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(600,row);
        app.deleteShape();
        app.pad.setCurrentFillColor(-65536);
        app.pad.setCurrentStrokeColor(-16744448);
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(700,row);
        app.pad.createPicture("./test/artstationapplication/assets/pup.png");
        app.pad.completeShape();
        app.pad.shapes.get(app.pad.listIndex).manipulate(800,row);
        app.pad.shapes.get(app.pad.listIndex).setFillColor(-16744448);
        app.pad.shapes.get(app.pad.listIndex).setStrokeColor(-65536);
        
        app.exportImageFile("./test/artstationapplication/testOutputs/output.png");
        app.saveDrawing("./test/artstationapplication/testOutputs/testSaveFile/output.txt");
    }
}
