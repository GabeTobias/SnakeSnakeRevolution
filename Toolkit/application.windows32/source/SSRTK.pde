import static javax.swing.JOptionPane.*;
import controlP5.*;

ControlP5 cp5;
String textValue = "";

//CONSTANTS
int TILESIZE = 40;

//Level Data
int[][] Data;
int _width,_height;
String _fileName;

//Manipulation States
int tileType = 1;
boolean FileOpen = true;
boolean DragState;
boolean DisplayPanel;
boolean Saving, Loading;

//INPUT
boolean _shift,_s,_l;

void setup(){
    size(800,800);
    noStroke();
    //frame.setResizable(true);

    //Initialize Controls Inputs
    InitGUI();

    //Calculate Grid Size
    int size = 800/TILESIZE;

    //Initialize data array
    Data = new int[size][size];

    //Set Level Size
    _width = size;
    _height = size;
}

void InitGUI(){
    cp5 = new ControlP5(this);

    PFont font = createFont("arial",15);
    
    cp5.addTextfield("BPM")
        .setPosition(20,730)
        .setSize(200,30)
        .setFont(font)
        .setFocus(true)
        .setColor(color(255))
        .setColorBackground(100)
        .setColorForeground(200) 
        .setColorCaptionLabel(color(0)) 
        .setVisible(false)
    ;

    cp5.addTextfield("Song")
        .setPosition(250,730)
        .setSize(200,30)
        .setFont(font)
        .setFocus(true)
        .setColor(color(255))
        .setColorBackground(100)
        .setColorForeground(200) 
        .setColorCaptionLabel(color(0)) 
        .setVisible(false)
    ;

    cp5.addTextfield("Width")
        .setPosition(500,715)
        .setSize(50,30)
        .setFont(font)
        .setFocus(true)
        .setColor(color(255))
        .setColorBackground(100)
        .setColorForeground(200) 
        .setVisible(false)
        .setLabelVisible(false)
    ;
    
    cp5.addTextfield("Height")
        .setPosition(570,715)
        .setSize(50,30)
        .setFont(font)
        .setFocus(true)
        .setColor(color(255))
        .setColorBackground(100)
        .setColorForeground(200) 
        .setVisible(false)
        .setLabelVisible(false)
    ;

    cp5.addButton("Load")
        .setValue(0)
        .setPosition(670,715)
        .setSize(100,30)
        .setColorBackground(100)
        .setColorForeground(200) 
        .setVisible(false)
    ;

    cp5.addButton("Save")
        .setValue(0)
        .setPosition(670,755)
        .setSize(100,30)
        .setColorBackground(100)
        .setColorForeground(200) 
        .setVisible(false)
    ;

    cp5.addButton("Resize")
        .setValue(0)
        .setPosition(500,755)
        .setSize(120,30)
        .setColorBackground(100)
        .setColorForeground(200) 
        .setVisible(false)
    ;
}

void draw(){
    background(255);

    //Loop through all spaces in level
    for (int x0 = 0; x0 < _width; ++x0) {
        for (int y0 = 0; y0 < _height; ++y0) {
            DrawTile(x0,y0);                            //Render each space
        }
    }

    //Draw Overlaping grid
    DrawGrid();

    //Draw cursor position
    DrawCursor();

    //Update for inputs
    HandleInput();

    if(DisplayPanel){
        DrawDisplay();
    }
}

void DrawDisplay(){
    fill(255);
    rect(0,700,800,100);
}

void DrawTile(int x0, int y0){
    fill(GetColor(x0,y0));

    rect(
        x0 * TILESIZE,              //X position scaled by tilesize
        y0 * TILESIZE,              //Y position scaled by tilesize
        TILESIZE,                   //width
        TILESIZE                    //height
    );
}

void DrawGrid(){
    //Style for Grid
    strokeWeight(2);
    stroke(200);

    for (int i = 1; i < _width; ++i) {
        //get current distance from orgin
        int xx = i * TILESIZE;
        
        //Draw a vertical and horizontal line
        line(xx,0,xx,_height*TILESIZE);
    }

    for (int i = 1; i < _height; ++i) {
        //get current distance from orgin
        int yy = i * TILESIZE;
        
        //Draw a vertical and horizontal line
        line(0,yy,_width*TILESIZE,yy);
    }

    //Remove style
    noStroke();
}

void DrawCursor(){
    fill(0,100);

    //convert mouse position to grid position
    int pX = (mouseX/TILESIZE)*TILESIZE;
    int pY = (mouseY/TILESIZE)*TILESIZE;

    //Draw it onto the screen
    rect(pX,pY,TILESIZE,TILESIZE);
}

int GetColor(int x0, int y0){
    //Read tile value
    int tile = Data[x0][y0];
    
    //Determine color for each type
    switch (tile) {
        default :
            return color(100,100,100);
        case 1:
            return color(255,100,100);
        case 2:
            return color(100,255,100);
        case 3:
            return color(100,100,255);
    }
}

void HandleInput(){
    //Save Current File
    if(_s && _shift && !Saving){
        Saving = true;
        selectOutput("Select a file to write to:", "folderSelected");           //Select file to save
    }

    if(_l && _shift && !Loading){
        Loading = true;
        selectInput("Select a folder to write to:", "fileSelected");            //Select destination folder
    }
}

void folderSelected(File selection) {
  if (selection == null) {} else {
    SaveFile(selection.getAbsolutePath());                                      //Save File
    showMessageDialog(null, "Saved", "Saved", INFORMATION_MESSAGE);             //Alert that file has saved
  }
}

void fileSelected(File selection) {
  if (selection == null) {} else {
    LoadFile(selection.getAbsolutePath());                                      //Load File
  }
}

void LoadFile(String name){
    //Load JSON Object from file
    JSONObject file = loadJSONObject(name);

    //Load file Dimensions
    int _width = file.getInt("width");
    int _height = file.getInt("height");

    //cp5.get(Textfield.class,"BPM").setText(file.getString("BPM"));
    cp5.get(Textfield.class,"Song").setText(file.getString("Song"));

    //Reset Data 
    Data = new int[_width][_height];

    //Loop through all spaces in level
    for (int x0 = 0; x0 < _width; ++x0) {
        for (int y0 = 0; y0 < _height; ++y0) {
            //Create name for data
            String id = str(x0) + "," + str(y0);

            //Load data into object
            Data[x0][y0] = file.getInt(id);
        }
    }
}

void SaveFile(String name){
    //Create JSON Object
    JSONObject file = new JSONObject();

    //Save file Dimensions
    file.setInt("width",_width);
    file.setInt("height",_height);

    //Save Level information
    file.setInt("BPM",int(cp5.get(Textfield.class,"BPM").getText()));
    file.setString("Song",cp5.get(Textfield.class,"Song").getText());

    //Loop through all spaces in level
    for (int x0 = 0; x0 < _width; ++x0) {
        for (int y0 = 0; y0 < _height; ++y0) {
            //Create name for data
            String id = str(x0) + "," + str(y0);

            //Load data into object
            file.setInt(id,Data[x0][y0]);
        }
    }

    //Save JSON Object to file
    saveJSONObject(file, name);
}

void mousePressed(){
    if(mouseY > 700 && DisplayPanel) return;

    //convert mouse position to grid position
    int pX = (mouseX/TILESIZE);
    int pY = (mouseY/TILESIZE);

    if(pX < 0 || pY < 0 || pX >= _width || pY >= _height) return;

    //Determine state of selected tile
    if(Data[pX][pY] == 0){
        Data[pX][pY] = tileType;                        //Set tile type in Data
        DragState = true;                               //Set drag state to add tiles
    } else {
        Data[pX][pY] = 0;                               //Set tile type in Data
        DragState = false;                              //Set drag state to remove tiles
    }
}

void mouseDragged(){
    if(mouseY > 700 && DisplayPanel) return;

    //convert mouse position to grid position
    int pX = (mouseX/TILESIZE);
    int pY = (mouseY/TILESIZE);

    if(pX < 0 || pY < 0 || pX >= _width || pY >= _height) return;

    //Add or remove tiles based on tile state
    if(DragState) Data[pX][pY] = tileType;
    else Data[pX][pY] = 0;
}

void keyPressed() {
    //Set key to be true if key is NOT already true
    if(keyCode == SHIFT && !_shift) _shift = true;
    if(keyCode == char('S') && !_s) _s = true;
    if(keyCode == char('L') && !_l) _l = true;
}

void keyReleased() {
    //ALWAYS Set keys to be false on release
    if(keyCode == SHIFT) _shift = false;
    if(keyCode == char('S')) _s = false;
    if(keyCode == char('L')) _l = false;

    //Reset save combo
    if(!_s || !_shift){
        Saving = false;
    }

    //Reset load combo
    if(!_l || !_shift){
        Loading = false;
    }

    if(keyCode == char('1')) tileType = 1;
    if(keyCode == char('2')) tileType = 2;
    if(keyCode == char('3')) tileType = 3;

    if(keyCode == char('P')) DisplayPanel = !DisplayPanel;

    cp5.get(Textfield.class,"BPM").setVisible(DisplayPanel);
    cp5.get(Textfield.class,"Song").setVisible(DisplayPanel);
    
    cp5.get(Textfield.class,"Width").setVisible(DisplayPanel);
    cp5.get(Textfield.class,"Height").setVisible(DisplayPanel);

    cp5.get(Button.class,"Save").setVisible(DisplayPanel);
    cp5.get(Button.class,"Load").setVisible(DisplayPanel);

    cp5.get(Button.class,"Resize").setVisible(DisplayPanel);

}

public void Save(int value){
    selectOutput("Select a file to write to:", "folderSelected");           //Select file to save
}

public void Load(int value){
    selectInput("Select a folder to write to:", "fileSelected");            //Select destination folder
}

public void Resize(){
    int w = int(cp5.get(Textfield.class,"Width").getText());
    int h = int(cp5.get(Textfield.class,"Height").getText());

    _width = w;
    _height = h;

    //Determine tileScale based on longest side
    if(w == 0 || h == 0) return;

    if(w > h){
        TILESIZE = 800/w;
    } else {
        TILESIZE = 800/h;
    }

    //Initialize data array
    Data = new int[_width][_height];
}

/*
    TODO:
        Render
        Placement & Deletion
        Click and Drag
        Load and Save
*/
