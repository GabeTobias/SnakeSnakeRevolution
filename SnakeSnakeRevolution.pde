import ddf.minim.*;
import ddf.minim.analysis.*;
import java.util.Map;

//CONSTANTS
int TILESIZE = 40;

//DEBUGGERY
Object CONTEXT;
int bkg = 255;

int GameMode = 0;

void setup(){
    fullScreen(P3D,2);
    noStroke();
    frameRate(120);

    LoadFonts();
    LoadImages();
    LoadShaders();

    InitGame();
    
    LoadAudio();
    
    setupBKG();
}

void LoadFonts(){
    //Load Font
    PFont maven = createFont("Road_Rage.otf", 128);
    textFont(maven);
}

void LoadShaders(){
    blur = loadShader("blur.frag");
}

void LoadAudio(){
    sound._ASelect = minim.loadFile("Select.wav");;
    sound._AWrong = minim.loadFile("OffBeat.wav");
    sound._ALoading = minim.loadFile("Loading.wav");
    sound._ASecond =  minim.loadFile("Player2.wav");
    sound._AFood = minim.loadFile("food.wav");
    sound._ACrash = minim.loadFile("collision.wav");
    sound._AWin = minim.loadFile("win.wav");
}

void LoadImages(){
    Title = loadImage("Title.png");
    Arrow = loadImage("Arrow.png");
    ArrowBad = loadImage("ArrowBad.png");
    ArrowNeg = loadImage("ArrowOutline.png");
    Background = loadImage("Background.png");
    Instructions = loadImage("instructions.png");
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
            background(0);
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
