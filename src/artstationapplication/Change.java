/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication;

/**
 *
 * @author wilder4690
 */
    //This will hold the information about the previous state of a shape, and can be used to revert 
    //back to that state when evoked. 
    public class Change{
        Transformation operation; 
        int index;
        Shape clone;
        float[] changedValues;

        Change(Transformation change, int id, float[] values){
            index = id;
            operation = change;
            changedValues = values;
        }
        
        Change(Transformation change, int id, float value){
            index = id;
            operation = change;
            changedValues = new float[1];
            changedValues[0] = value;
        }
        
        Transformation getOperation(){
            return operation;
        }
        
        int getIndex(){
            return index;
        }

        Shape getClone(){
            return clone;
        }
        
        void createClone(Shape original){
            clone = original;
        }


    }
