/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


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
}
