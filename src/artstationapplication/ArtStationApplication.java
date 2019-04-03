/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package artstationapplication; 
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import processing.core.*;
import processing.javafx.PSurfaceFX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.value.*;
import javafx.scene.paint.Color;
import java.util.*;
import java.io.*;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;



/**
 *
 * @author wilder4690
 */
public class ArtStationApplication extends PApplet{
    
    final int ZOOM_UNIT = 80;
    enum Coordinates {OFF, MOUSE, TOP}
    Coordinates coordinateMode = Coordinates.TOP;
    float scaleFactor;
    float zoomFactor = 1;
    float focusX;
    float focusY;
    float verticalPadding = 0.05f;
    float verticalScreenShare = 1 - 2*verticalPadding;
    float horizontalPadding = 0.05f;
    float horizontalScreenShare = 1 - 2*horizontalPadding;
    int spacing = 5;
    String dialog = "";
    String fileLocation = "noFile";
    String refLocation = "noFile";
    boolean[] keys = new boolean[255];
    float canvasX;
    float canvasY;
    int activeButton;
    
    CanvasArea pad;
    GUI gui = new GUI();
    ChangeList tasks = new ChangeList();
    Stage stage;
    
    @Override
    public void settings(){
        size(displayWidth - gui.toolBarWidth - gui.controlBarWidth,displayHeight, FX2D);
    }
    
    @Override
    protected PSurface initSurface(){
        PSurface mySurface = super.initSurface();
        
        final PSurfaceFX FXSurface = (PSurfaceFX) mySurface;
        final Canvas canvas = (Canvas) FXSurface.getNative(); // canvas is the processing drawing
        stage = (Stage) canvas.getScene().getWindow(); // stage is the window
        Platform.setImplicitExit(false);
        
        gui.initializeGUI();
        
        //Event Handling for all GUI elements///////////////////////////////////
        gui.coordsOff.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
               coordinateMode = Coordinates.OFF;
            }
        });   

        gui.coordsMouse.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
              coordinateMode = Coordinates.MOUSE;
            }
        }); 
        
        gui.coordsTop.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
              coordinateMode = Coordinates.TOP;
            }
        }); 
        
        gui.newDrawing.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
              clearScreen();
            }
        });
        
        
        gui.saveAs.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                final FileChooser fileChooser = new FileChooser();
                File initialDirectory = new File(".\\src\\artstationapplication\\data\\saveFiles");
                fileChooser.setInitialDirectory(initialDirectory);
                fileChooser.setTitle("Save Drawing");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ASF", "*.txt")
                );
                File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                if(file != null){ 
                    String location = file.getAbsolutePath();
                    saveDrawing(location);
                }
            }
        });

        gui.save.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(fileLocation.equals("noFile")){
                    gui.saveAs.fire();
                }
                else saveDrawing(fileLocation);
            }
        });
        
        gui.open.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                final FileChooser fileChooser = new FileChooser();
                File initialDirectory = new File(".\\src\\artstationapplication\\data\\saveFiles");
                fileChooser.setInitialDirectory(initialDirectory);
                fileChooser.setTitle("Open Drawing");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("ASF", "*.txt")
                );
                File file = fileChooser.showOpenDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                if(file != null){ 
                    String location = file.getAbsolutePath();
                    loadDrawing(location);
                }
            }
        });

        EventHandler<ActionEvent> MenuHandler = new EventHandler<ActionEvent>(){ 
            public void handle(ActionEvent ae){ 
                String name = ((MenuItem)ae.getTarget()).getText();
                MenuItem target = (MenuItem)ae.getTarget();
                //TODO: create window asking user if they want to exit or hide
                if(name.equals("Exit")) stage.hide(); 
                else if(target == gui.clipboardFile) exportProcessingFileToClipboard(); 
                else if(target == gui.clipboardShapes) exportProcessingShapesToClipboard(); 
                else if(target == gui.imageFile){
                        final FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save Image");
                        fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("PNG", "*.png"),
                            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                            new FileChooser.ExtensionFilter("TIFF", "*.tif"),
                            new FileChooser.ExtensionFilter("TARGA", "*.tga")
                        );
                        File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                        if(file != null){ 
                            String location = file.getAbsolutePath();
                            exportImageFile(location);
                        }
                        
                }
                else if(target == gui.svgFile){
                        final FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save SVG");
                        fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("SVG", "*.svg")
                        );
                        File file = fileChooser.showSaveDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                        if(file != null){ 
                            String location = file.getAbsolutePath();
                            exportSVGFile(location);
                        }
                }
            }
        };
        
        gui.clipboardFile.setOnAction(MenuHandler);
        gui.clipboardShapes.setOnAction(MenuHandler);
        gui.imageFile.setOnAction(MenuHandler);
        gui.svgFile.setOnAction(MenuHandler);
        gui.exit.setOnAction(MenuHandler);
        
        EventHandler<ActionEvent> gridToggle = new EventHandler<ActionEvent>(){ 
            public void handle(ActionEvent ae){ 
                if(ae.getSource() == gui.cbGridOn) gui.gridOn.setSelected(gui.cbGridOn.isSelected()); 
                else gui.cbGridOn.setSelected(gui.gridOn.isSelected()); 
                pad.toggleGrid(gui.gridOn.isSelected());
  
                //Disables other grid options when grid is turned off
                gui.gridTextField.setDisable(!gui.cbGridOn.isSelected());
                gui.gridSlider.setDisable(!gui.cbGridOn.isSelected());
                gui.cbGridSnap.setDisable(!gui.cbGridOn.isSelected());
            }
        };
        
        EventHandler<ActionEvent> snapToggle = new EventHandler<ActionEvent>(){ 
            public void handle(ActionEvent ae){ 
                if(ae.getSource() == gui.cbGridSnap) gui.gridSnapOn.setSelected(gui.cbGridSnap.isSelected());
                else gui.cbGridSnap.setSelected(gui.gridSnapOn.isSelected());
                pad.toggleSnap(gui.gridSnapOn.isSelected());
            }
        };
        
        gui.gridOn.setOnAction(gridToggle);
        gui.cbGridOn.setOnAction(gridToggle); 
        gui.gridSnapOn.setOnAction(snapToggle);
        gui.cbGridSnap.setOnAction(snapToggle);
        
        gui.drawMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.drawMode();
                canvas.requestFocus();
            }
        });   
        
        gui.editMode.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.editMode();
                canvas.requestFocus();
            }
        }); 
        
        EventHandler<ActionEvent> ToolHandler = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                String name = ((ToggleButton)ae.getTarget()).getText();
                gui.drawMode.setSelected(true);
                pad.drawMode();
                //all tool handling done here
                switch(name){
                    case "Circle": pad.setActiveTool(ShapeType.CIR); break;
                    case "Rectangle": pad.setActiveTool(ShapeType.REC); break;
                    case "Triangle": pad.setActiveTool(ShapeType.TRI); break;
                    case "Line": pad.setActiveTool(ShapeType.LIN); break;
                    case "Poly": pad.setActiveTool(ShapeType.POL);break;
                    case "Curve": pad.setActiveTool(ShapeType.CUR); break;
                    case "Picture":
                        pad.setActiveTool(ShapeType.PIC);
                        pad.createShape(canvasX, canvasY); 
                        final FileChooser fileChooser = getImageFileChooser();
                        File file = fileChooser.showOpenDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                        if(file != null){ 
                            pad.createPicture(cleanseFilePath(file.getAbsolutePath()));
                            pad.completeShape();
                        }
                        break;
                    case "Arc": pad.setActiveTool(ShapeType.ARC); break;
                }

                canvas.requestFocus(); 
            }
        };
        
        gui.btnCircle.setOnAction(ToolHandler);
        gui.btnRectangle.setOnAction(ToolHandler);
        gui.btnLine.setOnAction(ToolHandler);
        gui.btnTriangle.setOnAction(ToolHandler);
        gui.btnPoly.setOnAction(ToolHandler);
        gui.btnCurve.setOnAction(ToolHandler);
        gui.btnPicture.setOnAction(ToolHandler);
        gui.btnArc.setOnAction(ToolHandler);
        
        EventHandler<ActionEvent> textHandler = new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                int newWidthVal = convertStringToInt(gui.widthTextField.getText());
                int newHeightVal = convertStringToInt(gui.heightTextField.getText());
                if(newWidthVal <= 0){   //Resets textfield back to current value if user input is invalid
                    gui.widthTextField.setText(Integer.toString(pad.getWidth()));
                }
                else if(newHeightVal <= 0){  //Resets textfield back to current value if user input is invalid
                    gui.heightTextField.setText(Integer.toString(pad.getHeight()));
                }
                else{
                    pad.setWidth(newWidthVal);
                    pad.setHeight(newHeightVal);
                    scaleCanvas((float)(stage.getWidth()-gui.toolBarWidth - gui.controlBarWidth),(float)(stage.getHeight() - 2*gui.mb.getHeight()));
                    canvas.setWidth(stage.getWidth()-gui.toolBarWidth - gui.controlBarWidth);
                    pad.calculateGridSpacing();
                    canvas.requestFocus();
                    for(int i = 0; i < gui.shapes.size(); i++){ 
                        gui.shapes.get(i).resizeHandles(20/scaleFactor);
                    }
                }
            }
        };
        
        gui.widthTextField.setOnAction(textHandler);
        gui.heightTextField.setOnAction(textHandler);
        
        gui.btnReferenceImage.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                final FileChooser fileChooser = getImageFileChooser();
                File file = fileChooser.showOpenDialog(stage); //arg here is a Window that input will be blocked to until dialog complete (can be null)
                if(file != null){ 
                    PImage loadedImage = loadImage(file.getAbsolutePath());
                    refLocation = file.getAbsolutePath();

                    //Popup to handle resize////////////////////////////////////////////
                    if(loadedImage.width > pad.getWidth() || loadedImage.height > pad.getHeight()){
                        final Stage windowAlert = new Stage();
                        windowAlert.initModality(Modality.APPLICATION_MODAL);

                        final FlowPane dialogBox = new FlowPane(Orientation.VERTICAL, spacing, 2*spacing);
                        final HBox options = new HBox(spacing);
                        final Button maintain = new Button("Maintain Canvas");
                        final Button resize = new Button("Resize Canvas");
                        final Label text = new Label("The reference image is larger than the canvas.\nHow would you like to resolve this?");

                        text.setWrapText(true);
                        dialogBox.setAlignment(Pos.CENTER);

                        options.getChildren().addAll(maintain, resize);
                        dialogBox.getChildren().addAll(text, options);
                        Scene dialogScene = new Scene(dialogBox, 400, 200);
                        windowAlert.setScene(dialogScene);
                        windowAlert.show();

                        maintain.setOnAction(new EventHandler<ActionEvent>(){
                            public void handle(ActionEvent ae){
                                windowAlert.close();
                            }
                        });
                                   
                        resize.setOnAction(new EventHandler<ActionEvent>(){
                            public void handle(ActionEvent ae){
                                pad.setHeight(loadedImage.height);
                                pad.setWidth(loadedImage.width);
                                gui.widthTextField.setText(loadedImage.width+"");
                                gui.heightTextField.setText(loadedImage.height+"");
                                pad.calculateGridSpacing();
                                windowAlert.close();
                            }
                        });
                }
                    pad.loadReferenceImage(loadedImage);
                    gui.cbReferenceImage.setSelected(true);
                }
            }
        });
        
        gui.cbReferenceImage.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                pad.toggleReferenceImage(gui.cbReferenceImage.isSelected());
            }
        });
        
        gui.gridSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                int output = newVal.intValue();
                gui.gridTextField.setText(Integer.toString(output));
                pad.setGridDensity(output);
            }
        });
        
        gui.gridTextField.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                int newValue = convertStringToInt(gui.gridTextField.getText());
                if(newValue == -1){
                    newValue = (int)gui.gridSlider.getValue();
                    gui.gridTextField.setText(Integer.toString((int)gui.gridSlider.getValue()));
                }
                else if(newValue < 0){
                    newValue = 0;
                    gui.gridTextField.setText("0");
                }
                else if(newValue > gui.gridMax){
                    newValue = gui.gridMax;
                    gui.gridTextField.setText(Integer.toString(newValue));
                }
                gui.gridSlider.setValue(newValue);
            }
        });
                
        gui.cbNoFill.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(gui.cbNoFill.isSelected()){
                    pad.setCurrentFillColor(gui.NONE);
                    gui.colorPickerFill.setDisable(true);
                }
                else{
                    pad.setCurrentFillColor(convertColorToInt(gui.colorPickerFill.getValue()));
                    gui.colorPickerFill.setDisable(false);
                }
                if(pad.isEditMode()){
                    gui.shapes.get(pad.listIndex).setFillColor(pad.getCurrentFillColor());
                }
                canvas.requestFocus(); 
            }
        });
        
        gui.cbNoStroke.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(gui.cbNoStroke.isSelected()){
                    pad.setCurrentStrokeWeight(0);
                    gui.colorPickerStroke.setDisable(true);
                    gui.weightSlider.setDisable(true);
                    gui.weightTextField.setDisable(true);
                }
                else{
                    pad.setCurrentStrokeWeight((float)convertStringToDouble(gui.weightTextField.getText()));
                    pad.setCurrentStrokeColor(convertColorToInt(gui.colorPickerStroke.getValue()));
                    gui.colorPickerStroke.setDisable(false);
                    gui.weightSlider.setDisable(false);
                    gui.weightTextField.setDisable(false);
                }
                if(pad.isEditMode()){
                    gui.shapes.get(pad.listIndex).setStrokeWeight(pad.getCurrentStrokeWeight());
                }
                canvas.requestFocus(); 
            }
        });
        
        gui.colorPickerFill.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentFillColor(convertColorToInt(gui.colorPickerFill.getValue()));
                if(pad.isDrawMode()){     
                    //canvas.requestFocus(); 
                }
                else{
                    tasks.push(new Change(Transformation.FIL, pad.listIndex, gui.shapes.get(pad.listIndex).getFillColor()));
                    gui.shapes.get(pad.listIndex).setFillColor(convertColorToInt(gui.colorPickerFill.getValue()));
                }
                canvas.requestFocus(); 
            }
        });
        
        gui.colorPickerStroke.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setCurrentStrokeColor(convertColorToInt(gui.colorPickerStroke.getValue()));
                if(pad.isDrawMode()){     
                    //canvas.requestFocus(); 
                }
                else{
                    tasks.push(new Change(Transformation.STF, pad.listIndex, gui.shapes.get(pad.listIndex).getStrokeColor()));
                    gui.shapes.get(pad.listIndex).setStrokeColor(convertColorToInt(gui.colorPickerStroke.getValue()));
                }
                canvas.requestFocus();  
            }
        });
        
        gui.weightSlider.valueProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                gui.weightTextField.setText(Double.toString(newVal.floatValue()));
                pad.setCurrentStrokeWeight((float)newVal.floatValue());
                if(pad.isEditMode()){
                    tasks.push(new Change(Transformation.STW, pad.listIndex, gui.shapes.get(pad.listIndex).strokeWeight));
                    gui.shapes.get(pad.listIndex).setStrokeWeight(newVal.floatValue());
                }
            }
        });
        
        gui.weightTextField.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                double newValue = convertStringToDouble(gui.weightTextField.getText());
                if(newValue == -1){
                    newValue = gui.weightSlider.getValue();
                    gui.weightTextField.setText(Double.toString(gui.weightSlider.getValue()));
                }
                else if(newValue < 0){
                    newValue = 0;
                    gui.weightSlider.setValue(0);
                    gui.weightTextField.setText("0");
                }
                else if(newValue > 100){
                    gui.weightSlider.setValue(100);
                }
                else{
                    gui.weightSlider.setValue(newValue);
                }
                pad.setCurrentStrokeWeight((float)newValue);
                if(pad.isEditMode()){
                    tasks.push(new Change(Transformation.STW, pad.listIndex, gui.shapes.get(pad.listIndex).strokeWeight));
                    gui.shapes.get(pad.listIndex).setStrokeWeight((float)newValue);
                }
            }
        });
        
        gui.cbNoBackground.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(gui.cbNoBackground.isSelected()){
                    pad.setBackgroundColor(gui.NONE);
                    gui.colorPickerBackground.setDisable(true);
                }
                else{
                    pad.setBackgroundColor(convertColorToInt(gui.colorPickerBackground.getValue()));
                    gui.colorPickerBackground.setDisable(false);
                }
            }
        });
        
        gui.colorPickerBackground.setOnAction(new EventHandler() {
            public void handle(Event t) {
                pad.setBackgroundColor(convertColorToInt(gui.colorPickerBackground.getValue()));
            }
        });
        
        gui.btnUpArrow.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(pad.listIndex-1 < 0 || pad.listIndex-1 == gui.shapes.size()) {
                    canvas.requestFocus();
                    return;
                }
                tasks.push(new Change(Transformation.ORD, pad.listIndex, new float[]{pad.listIndex-1, pad.listIndex}));
                swapElements(pad.listIndex, pad.listIndex-1);
                canvas.requestFocus();
            }
        });
        
        gui.btnDownArrow.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(pad.listIndex+1 < 0 || pad.listIndex+1 >= gui.shapes.size()){
                    canvas.requestFocus();
                    return;
                }
                tasks.push(new Change(Transformation.ORD, pad.listIndex, new float[]{pad.listIndex+1, pad.listIndex}));
                swapElements(pad.listIndex, pad.listIndex+1);
                canvas.requestFocus();
            }
        });
        
        gui.btnDelete.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(gui.shapes.isEmpty()) return;
                Change currentChange = new Change(Transformation.DEL, pad.listIndex, 0);
                currentChange.createClone(gui.shapes.get(pad.listIndex));
                tasks.push(currentChange);
                deleteShape();
                canvas.requestFocus();
            }
        });
        
        gui.btnReset.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(!gui.shapes.isEmpty()) {
                    tasks.push(new Change(Transformation.RES, pad.listIndex, gui.shapes.get(pad.listIndex).getResetFloats()));
                    gui.shapes.get(pad.listIndex).reset();
                }
                canvas.requestFocus();
            }
        });
        
        gui.btnCopy.setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent ae){
                if(!gui.shapes.isEmpty()){
                    copyShape();
                    tasks.push(new Change(Transformation.COP, pad.listIndex, 0));
                }
                canvas.requestFocus();
            }
        });
        
        gui.selectionModel.selectedIndexProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                gui.editMode.setSelected(true);
                pad.editMode();
                pad.listIndex = (int)newVal;
            }
        });
        
        ContextMenu dropDown = new ContextMenu();

        MenuItem rename = new MenuItem("Rename");
        rename.setOnAction(new EventHandler<ActionEvent>() { //selecting rename on the drop down
            @Override
            public void handle(ActionEvent arg0) {
                try {
                    TextInputDialog nameDialog = new TextInputDialog(gui.shapes.get(pad.listIndex).getName());
                    nameDialog.setTitle("Rename");
                    nameDialog.setHeaderText("Rename the shape");
                    nameDialog.setContentText("Enter new name");

                    Optional<String> result = nameDialog.showAndWait();
                    if (result.isPresent()) {
                        String cleanedInput = cleanseUserInput(result.get());
                        gui.shapes.get(gui.selectionModel.getSelectedIndex()).setName(cleanedInput);
                        gui.shapeViewer.refresh();
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
        });
        //adding rename option to the dropdown menu .addAll can be used in future for multiple items
        dropDown.getItems().add(rename); 

        gui.shapeViewer.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                if (gui.shapes.size() > 0)
                    dropDown.show(gui.shapeViewer, event.getScreenX(), event.getScreenY());
                }
        });
		
        //Key Events for full scene
        gui.rootNode.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>(){
            public void handle(KeyEvent event){
                switch(event.getCode()){
                    case ESCAPE:
                        pad.drawMode();
                        event.consume();    //consume so it does not trigger closing the application
                        break;
                    case CONTROL:
                        pad.setControl(true);
                        pad.toggleGrid(true);
                        pad.toggleSnap(true); 
                        dialog = "SNAP and GRID are on.";
                        break;
                    case ALT:
                        pad.setAlt(true);
                        break;
                    case SHIFT:
                        pad.setShift(true);
                        break;
                }
            }
        });
        
        gui.rootNode.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>(){
            public void handle(KeyEvent event){
                switch(event.getCode()){
                    case DELETE: 
                        if(!gui.shapes.isEmpty()){
                            Change currentChange = new Change(Transformation.DEL, pad.listIndex, 0);
                            currentChange.createClone(gui.shapes.get(pad.listIndex));
                            tasks.push(currentChange);
                            deleteShape();
                        }break;
                    case CONTROL: 
                        pad.setControl(false);
                        pad.toggleGrid(gui.cbGridOn.isSelected());
                        pad.toggleSnap(gui.cbGridSnap.isSelected()); 
                        dialog = "";
                        break;
                    case ALT: pad.setAlt(false); break;
                    case SHIFT: pad.setShift(false); break;
                    case SPACE:
                        if(pad.isDrawMode()){
                            gui.editMode.setSelected(true);
                            pad.completeShape();  
                        }
                        else{
                            gui.drawMode.setSelected(true);
                            pad.drawMode();  
                        }break;
                }
            }
        });
        
        //Mouse events for full scene
        gui.rootNode.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>(){
            public void handle(ScrollEvent event){
                if(mouseOverCanvas()){
                    float delta = (float)event.getDeltaY(); //one scroll unit is 40
                    zoomFactor += delta/ZOOM_UNIT; 
                    if(zoomFactor < 1) zoomFactor = 1;

                //Focuses zoom on mouse for generation of canvas.
                    focusX = mouseX*(1-zoomFactor);
                    focusY = mouseY*(1-zoomFactor);
                }
            }
        });
        
        canvas.widthProperty().unbind();
        canvas.heightProperty().unbind();
        gui.rootNode.setCenter(canvas);
                
        final Scene newscene = new Scene(gui.rootNode); // Create a scene from the elements

        //Window Properties/////////////////////////////////////////////////////
        stage.setTitle("Art Station");
        stage.setMaximized(true);
        stage.setMinWidth(gui.controlBarWidth + gui.toolBarWidth);

        stage.widthProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                scaleCanvas((float)(stage.getWidth()-gui.toolBarWidth - gui.controlBarWidth),(float)(stage.getHeight() - 2*gui.mb.getHeight()));
                canvas.setWidth(stage.getWidth()-gui.toolBarWidth - gui.controlBarWidth);
            }
        });

        stage.heightProperty().addListener(new ChangeListener<Number>(){
            public void changed(ObservableValue<? extends Number> changed, Number oldVal, Number newVal){
                scaleCanvas((float)(stage.getWidth()-gui.toolBarWidth - gui.controlBarWidth),(float)(stage.getHeight() - 2*gui.mb.getHeight()));
            }
        });


         pad = new CanvasArea(this,900,900, gui.shapes, tasks);

        
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.setScene(newscene); // Replace the stage's scene with our new one.
            }
        });
        
        canvas.requestFocus(); //needed?
        return mySurface; 
    }
    
    FileChooser getImageFileChooser(){
            final FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Reference Image");
            //TODO: is there a to show all at once for loading purposes?
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("GIF", "*.gif"),
                new FileChooser.ExtensionFilter("TARGA", "*.tga")
            );
            return fileChooser;
    }
    
    @Override 
    public void draw(){
        background(55,55,55);
        generateCanvasArea();
        drawFrames();
        drawMouse();
        drawDialog();
        checkInput();
    }
       
    @Override
    public void mousePressed(){
        activeButton = mouseButton; //stores a copy because mouseButton drops value before triggering mouseReleased/mouseClicked
        if(mouseButton == LEFT){
            if(mouseOverCanvas()){
              dialog = "";
              if(pad.isDrawMode()){ //creates new shape
                pad.createShape(canvasX, canvasY);
              }
              else if(pad.isEditMode()){
                pad.checkForTransformation(new PVector(canvasX, canvasY));
              }
            }
        }
    }
    
        
    @Override
    //Check mouse LEFT vs RIGHT against "activeButton" because mouseButton variable drops its value before these checks run
    public void mouseReleased(){ 
        if(activeButton == LEFT){
            if(pad.isDrawMode()){
                if(pad.activeTool == ShapeType.POL){
                    if(pad.overOrigin(new PVector(canvasX, canvasY))){
                        pad.completeShape();
                        gui.editMode.setSelected(true);
                    }
                    else pad.completeVertex(canvasX, canvasY);
                }
                else{
                    pad.completeShape(); 
                    gui.editMode.setSelected(true);
                }
            }
        }
        else if(activeButton == RIGHT){
            if(pad.isDrawMode()){
               pad.completeShape();  
               gui.editMode.setSelected(true);
           }
            else{  
                pad.drawMode();
                gui.drawMode.setSelected(true);
            }
        }
        if(pad.isEditMode()){
            pad.clearSubMode();
        }
    }

    @Override
    public void keyPressed(){
        if(key < 255){
            keys[key] = true;
        }
    }
    
    @Override
    public void keyReleased(){
        if(key < 255){
            keys[key] = false;
        }
    }

    //Uses change operation and stored information to revert shape back to previous state. 
    void undo(Change task){
        switch(task.getOperation()){
            case ROT: gui.shapes.get(task.getIndex()).setRotation(task.changedValues[0]); dialog = "Reverted rotation.";  break;
            case TRA: gui.shapes.get(task.getIndex()).manipulate(task.changedValues[0],task.changedValues[1]); dialog = "Reverted translation.";  break;
            case SCA: gui.shapes.get(task.getIndex()).setHandles(task.changedValues); dialog = "Reverted scaling."; break;
            case DEL: gui.shapes.add(task.getClone()); dialog = "Reverted deletion.";  break;
            case ORD: swapElements((int)task.changedValues[0], (int) task.changedValues[1]); dialog = "Reverted reordering."; break;
            case ADD: deleteShape(task.getIndex()); dialog = "Reverted addition of new shape."; break;
            case COP: deleteShape(task.getIndex()); dialog = "Reverted copying of shape."; break;
            case RES: 
                gui.shapes.get(task.getIndex()).setHandles(task.changedValues); 
                gui.shapes.get(task.getIndex()).setRotation(task.changedValues[task.changedValues.length-1]); 
                break;
            case FIL: gui.shapes.get(task.getIndex()).setFillColor((int) task.changedValues[0]); break;
            case STF: gui.shapes.get(task.getIndex()).setStrokeColor((int) task.changedValues[0]); break;
            case STW: gui.shapes.get(task.getIndex()).setStrokeWeight((int) task.changedValues[0]); break;   
        }
    }
    
    void checkInput(){
        if((keys['z'] || keys['Z']) && pad.control){
            Change item = tasks.pop();
            if(item != null) undo(item);
            keys['z'] = false;
            keys['Z'] = false;
        }
    }
    
    void swapElements(int currentIndex, int newIndex){
        if(newIndex < 0 || newIndex == gui.shapes.size()) return;
        Collections.swap(gui.shapes, currentIndex, newIndex);
        gui.selectionModel.select(newIndex);
    }
    
    void clearScreen(){
        for(int i = gui.shapes.size()-1; i >= 0; i--){
            gui.shapes.remove(i);
        }
        pad.listIndex = 0;
        pad.drawMode();
    }
    
    void deleteShape(){
        deleteShape(pad.listIndex);
    }
     
    void deleteShape(int target){
        if(pad.modifying) pad.completeShape(); //finishes polygon if interrupted while drawing
        if(!gui.shapes.isEmpty()){
            gui.shapes.remove(target);
            pad.listIndex = gui.shapes.size() - 1; 
            if(pad.listIndex < 0){
                pad.listIndex = 0;
                pad.drawMode(); //switches back to draw mode if no other shapes to edit. 
            }
            gui.selectionModel.select(pad.listIndex);
        }
    }
    
    void copyShape() {
        if(pad.isEditMode() && !gui.shapes.isEmpty()){
            gui.shapes.add(gui.shapes.get(pad.listIndex).copy(gui.shapes.size()));
            pad.listIndex = gui.shapes.size()-1;
            gui.shapes.get(pad.listIndex).finishShape(); 
        }
    }
    
    int convertColorToInt(Color col){
        int r = (int)(col.getRed()*255);
        int g = (int)(col.getGreen()*255);
        int b = (int)(col.getBlue()*255);
        int a = (int)(col.getOpacity()*255);
        
        return color(r,g,b,a);
    }
    
    double convertStringToDouble(String number){
        double result;
        try{
            result = Double.parseDouble(number);
        }
        catch (NumberFormatException e){
            result = -1;
        }
        return result; 
    }
    
    int convertStringToInt(String number){
        int result;
        try{
            result = Integer.parseInt(number);
        }
        catch (NumberFormatException e){
            result = -1;
        }
        return result; 
    }
    
    String cleanseUserInput(String input){
        input = input.replace(",", " ");
        input = input.replace("&", "+");
        input = input.replace(";", " ");
        return input;
    }
    
    String cleanseFilePath(String path){
        String cleaned = path.replace("\\", "/");
        System.out.println(cleaned);
        return cleaned;
    }
    
    boolean mouseOverCanvas(){
        return (canvasX >= 0 && canvasY >= 0 && canvasY <= pad.getHeight() && canvasX <= pad.getWidth());
    }
    
    //TODO: Could add transformation designator here
    void drawMouse(){
        fill(155);
        switch(coordinateMode){
            case OFF: break;
            case MOUSE:  text( "X: "+canvasX + ", Y: "+ canvasY,mouseX, mouseY-5); break;
            case TOP: text( "X: "+canvasX + ", Y: "+ canvasY,width*horizontalPadding , height*verticalPadding - gui.spacing); break;
        }
    }
    
    void setDialog(String input){
        dialog = input;
    }
    
    void drawDialog(){
        fill(155);
        text(dialog, width*horizontalPadding, height*verticalPadding - 4*gui.spacing);
    }
    
    //Used to mask outside of canvas for shapes that are larger than canvas area.
    void drawFrames(){
        rectMode(CORNER);
        noStroke();
        fill(55,55,55);
        rect(0,0, width*horizontalPadding, height);
        rect(0,0, width, height*verticalPadding);
        rect(0, height*verticalPadding + (pad.getHeight()*scaleFactor), width, height);
        rect( width*horizontalPadding + (pad.getWidth()*scaleFactor), 0, width, height);
      }
    
    void generateCanvasArea(){
        pushMatrix();
          translate(width*horizontalPadding + focusX, height*verticalPadding + focusY);
          scale(scaleFactor*zoomFactor,scaleFactor*zoomFactor);
          pad.drawCanvas(canvasX, canvasY);
          //Converts mouseX and mouseY to match canvas. (ie 0,0 mouse position is top left of canvas)
          canvasX = (mouseX - screenX(0,0))/(scaleFactor*zoomFactor);
          canvasY = (mouseY - screenY(0,0))/(scaleFactor*zoomFactor); 
        popMatrix();
    }
    
    //Scales canvas based on how much of the central screen remains after padding is removed
    void scaleCanvas(float wide, float tall){
        scaleFactor = (horizontalScreenShare*wide)/(float)pad.getWidth();
        if( scaleFactor > (verticalScreenShare*tall)/(float)pad.getHeight()){
          scaleFactor = (verticalScreenShare*tall)/(float)pad.getHeight();
        }
    }
    
    
    void exportProcessingFileToClipboard(){
        String output = "void setup(){\n ";
        output += "\tsize("+pad.getWidth()+", "+pad.getHeight()+");\n}\n\n";
        output += "void draw(){ \n";
        output += "\tbackground("+pad.getBackgroundColor()+");\n\n";
        
        for (int i = 0; i < gui.shapes.size(); i++) {
            output += gui.shapes.get(i).printToClipboard();
        }
        output += "}";
        
        //Prepare String for clipbaord
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
    }
    
    void exportProcessingShapesToClipboard(){
        String output = "";
        for (int i = 0; i < gui.shapes.size(); i++) {
            output += gui.shapes.get(i).printToClipboard();
        }
        //Prepare String for clipbaord
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(output);
        clipboard.setContent(content);
    }
    
    void exportImageFile(String location){
        PGraphics exportGraphic = createGraphics(pad.getWidth(), pad.getHeight());
        exportGraphic.beginDraw();
        if(pad.getBackgroundColor() != gui.NONE) exportGraphic.background(pad.getBackgroundColor());
        for(int i = 0; i < gui.shapes.size(); i++){
            gui.shapes.get(i).printToPGraphic(exportGraphic);
        }
        exportGraphic.endDraw();
        exportGraphic.save(location);
        dialog = "Image saved in "+location;
    }
    
    void exportSVGFile(String location){
        PGraphics exportGraphic = createGraphics(pad.getWidth(), pad.getHeight(), SVG, location);
        exportGraphic.beginDraw();
        if(pad.getBackgroundColor() != gui.NONE) exportGraphic.background(pad.getBackgroundColor());
        for(int i = 0; i < gui.shapes.size(); i++){
            /*
                Gonna have to use hacky bandaid to skip over Pictures until I can dig into 
                why SVGs are failing with the image() call. TODO: fix SVGs with Picture 
                class. Remove out conditional to see bug. 
            */
            if(!gui.shapes.get(i).isPicture()) gui.shapes.get(i).printToPGraphic(exportGraphic);
        }
        exportGraphic.dispose();
        exportGraphic.endDraw();
        dialog = "SVG saved in "+location;
    }
    
    //Semicolon is used as delimiter for an object. Comma is delimiter for values. 
    //Parsing should split first by ';' and the first element ("Bezier", "Circle", "Rectangle", etc) 
    //will determine which load constructor to use.
    void loadDrawing(String location){
        clearScreen();
        String line;
        String[] pieces;
        
        try{
            BufferedReader reader = new BufferedReader(new FileReader(location));
            
            //Canvas information////////////////////////////////////////////////
            line = reader.readLine();
            pieces = line.split(",");
            pad.setWidth(Integer.valueOf(pieces[0]));
            pad.setHeight(Integer.valueOf(pieces[1]));
            pad.setBackgroundColor(Integer.valueOf(pieces[2]));
            pad.setGridDensity(Integer.valueOf(pieces[3]));
            if(!pieces[4].equals("noFile")){
                pad.loadReferenceImage(loadImage(pieces[4]));
                gui.cbReferenceImage.setSelected(true);
            }
            
            //Creates shapes with load constructor//////////////////////////////
            while((line = reader.readLine()) != null){
                pieces = line.split(";");
                pad.loadShape(pieces[0], pieces[1].split(","));
            }
            fileLocation = location;
        }
        catch (FileNotFoundException e){
            dialog = "File not found.";
        }
        catch (IOException e){
            dialog = "IO Exception.";
        }
    }
    
    void saveDrawing(String location){
        File file = new File(location);
        PrintWriter output;
        try{
            file.createNewFile();
        } catch( IOException e1){
            dialog = "Could not create File.";
        }
        try{
            output = new PrintWriter(file);
            output.println(pad.getWidth()+","+pad.getHeight()+","+pad.getBackgroundColor()+","+pad.getGridDensity()+","+refLocation); 
            for(int i = 0; i < gui.shapes.size(); i++){
                output.println(gui.shapes.get(i).save());
            }
            output.flush();
            output.close();
        }
        catch(FileNotFoundException e){
            dialog = "File not found.";
        }
    }
    
    public Stage getWindow(){
        return stage;
    }
    
    public static void main(String[] args) {
        String[] processingArgs = {"Art Station"};
        ArtStationApplication app = new ArtStationApplication();
        PApplet.runSketch(processingArgs, app);
    }
}
