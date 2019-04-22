import ddf.minim.*;
import ddf.minim.analysis.*;

//CONSTANTS
final int TILESIZE = 40;

//GLOBALS
Snake snek = new Snake(5,5);
Food goal = new Food(15,15);
Timer GameTime = new Timer(50);
GameState State;

//GLOBALS INITS
ParticleSystem particles;
SoundController sound;
Renderer renderer;
LevelManager manager;

Minim minim;
Level level;

//DEBUGGERY
Object CONTEXT;
int bkg = 255;

void setup(){
    //Basic Setup
    size(800,800, P2D);
    noStroke();
    frameRate(120);

    //Set File loading Context
    CONTEXT = this;

    //Load Sound Controller
    sound = new SoundController();
    sound.LoadMusic();

    //Create a new Particle System
    particles = new ParticleSystem();

    //Initalize Level Manager
    manager = new LevelManager();

    //get Current Level from Level Init
    level = manager.getLevel();

    println(level._file);

    //Initialize the Renderer
    renderer = new Renderer();

    Blur = loadImage("Blurr.png");
}

void draw(){
    sound.Update();             //Update Sound and Beat Detection

    Render();                   //Render scene object to screen              
    HandleGameplay();           //Update scene object
}

void Render(){
    //Clear the screen
    background(bkg);

    //Render level walls
    level.Show();

    //Render Non-static objects
    snek.Show();                    //Manipulate Snake Object
    goal.Show();                    //Render Goal Object

    if(State == GameState.GameOver){

        fill(25,170);
        rect(0,0,800,800);

        fill(255);
        textSize(100);
        text("Game Over", 400-(textWidth("Game Over")/2),400);

        textSize(30);

        return;
    }

    if(State == GameState.Loading){

        fill(255,255);
        rect(0,0,800,800);

        fill(25);
        textSize(100);
        text("Loading", 400-(textWidth("Loading")/2),400);

        textSize(30);

        return;
    }

    if(State == GameState.Win){

        fill(255,255);
        rect(0,0,800,800);

        fill(25);
        textSize(80);
        text("Thanks for playing", 400-(textWidth("Thanks for Playing")/2),400);

        textSize(30);

        return;
    }


    particles.Show();               //Render Scene Particles
}

void HandleGameplay(){
    //Update the level manager
    manager.Update();

    //Handle Game Over State
    if(State == GameState.GameOver || State == GameState.Loading){
        return;
    }

    //Manipulate Food
    if(goal.isEaten(snek._posX, snek._posY)) {
        snek.AddNode(int(snek.Last().x), int(snek.Last().y));
        goal.Eat();
    }

    //Update all scene particles
    particles.Update();
}

enum GameState {
    MainMenu,
    Playing,
    Paused,
    Loading,
    GameOver,
    Win
}

/*
TODO:
    Level Loading
        Multiple Songs
        BPM Change
        Default Color shift
        Basic Toolkit


Level Loading Sequence
    Pause Gameplay
    Fade snake out 
    Fade level out 

    Load new Level Data

    Fade level in
    Set Snake Position
    Fade Snake in
    Play Level
*/