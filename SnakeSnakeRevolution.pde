import ddf.minim.*;
import ddf.minim.analysis.*;
import java.util.Map;

//CONSTANTS
final int TILESIZE = 40;

//DEBUGGERY
Object CONTEXT;
int bkg = 255;

int GameMode = 0;

void setup(){
    //Basic Setup
    size(800,800, P2D);
    noStroke();
    frameRate(120);

    //Load Font
    PFont maven = createFont("Maven.ttf", 128);
    textFont(maven);

    //Set File loading Context
    CONTEXT = this;

    //Initialize Gameplay variables
    InitGame();

    //Load images
    Blur = loadImage("Blurr.png");
}

void draw(){
    switch (GameMode) {
        //Start Screen
        case 0:
            background(0);
            StartMenu();
            break;

        //Single Player Select
        case 1:
            background(125);
            CharecterSelect();
            break;

        //Instruction Screen
        case 2:
            background(250);
            Instructions();
            break;

        //Gameplay
        case 3: 
            RunGame();
            break;
    }
}

void RunGame(){
    Render();                   //Render scene object to screen              
    HandleGameplay();           //Update scene object
}

void RunGUI(){}


/*

Assets
    Arrow Tile Sets
    Misteps Box
    Background 

*/