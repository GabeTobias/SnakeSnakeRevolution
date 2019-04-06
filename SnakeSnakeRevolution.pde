import ddf.minim.*;
import ddf.minim.analysis.*;

//CONSTANTS
final int TILESIZE = 20;

//GLOBALS
Snake snek = new Snake(5,5);
Food goal = new Food(15,15);
Timer GameTime = new Timer(100);

GameState State;

//GLOBALS INITS
Minim minim;
SoundController sound;

//DEBUGGERY
Object CONTEXT;
int bkg = 100;

void setup(){
    //Basic Setup
    size(800,800);
    frameRate(120);

    //Set File loading Context
    CONTEXT = this;

    //Load Sound Controller
    sound = new SoundController();
    sound.LoadMusic();
}

void draw(){
    //Update the game on timer
    if(GameTime.Triggered()) HandleGame();
}

void HandleGame(){
    //Reset Background color
    bkg = 200;

    //Update Sound and Beat Detection
    sound.Update();

    //Clear Background
    background(bkg);

    //Handle Game Over State
    if(State == GameState.GameOver){
        text("Game Over", 10,20);
        return;
    }


    //Manipulate Snake Object
    snek.Show();
    snek.Move();

    //Manipulate Food
    if(goal.isEaten(snek._posX,snek._posY)) {
        snek.AddNode(snek._posX,snek._posY);
        goal.Eat();
    }
    
    goal.Show();
}

void keyPressed(){
    //TODO: Replace with analog arduino inputs
    //Handle Keyboard Inputs
    if(key == 'w') snek.SetVelocity (0,-1);
    if(key == 'a') snek.SetVelocity(-1, 0);
    if(key == 's') snek.SetVelocity( 0, 1);
    if(key == 'd') snek.SetVelocity( 1, 0);
}

void onBeatEvent(){
    bkg = 51;
}

enum GameState {
    MainMenu ,
    Playing,
    Paused,
    Loading,
    GameOver
}

/*
TODO:
    Sync Snake movment to beat
    Level Loading
        Multiple Songs
        BPM Change
        Default Color shift

*/