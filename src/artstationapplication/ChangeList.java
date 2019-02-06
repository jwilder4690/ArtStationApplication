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
    public class ChangeList{
        //Keeps log of 20 most recent changes 
        final int UNDO_LIMIT = 20;
        int last = UNDO_LIMIT - 1;
        Change[] stack = new Change[UNDO_LIMIT];
        int index = 0; //next available slot
        
        ChangeList(){
            //Nothing in constructor?
        }
        
        
        void push(Change task){
            stack[index] = task;
            index++;
            if(index == UNDO_LIMIT) index = 0; //Wraps back to beginning to overwrite
        }
        
        //Needs to go to element previous to index to get last change
        Change pop(){
            Change task;
            index--;
            if(index == -1){ //Wraps to end of array if more changes, otherwise stays at beginning
                if(stack[last] != null) index = last;
                else index = 0;            
            }
            
            if(stack[index] != null)  task = stack[index];
            else task = null;
            stack[index] = null;

            return task;
        }
    }
    

