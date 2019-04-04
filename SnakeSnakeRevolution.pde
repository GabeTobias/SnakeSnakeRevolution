import ddf.minim.*;
import ddf.minim.analysis.*;

//CONSTANTS
final int TILESIZE = 20;

//GLOBALS
Snake snek = new Snake(5,5);
Food goal = new Food(15,15);

Timer GameTime = new Timer(100);

Minim minim;
SoundController sound;


//DEBUGGERY\\
Object FFD;
int bkg = 100;

void setup(){
    size(800,800);

    frameRate(120);

    snek.AddNode(4,5);
    snek.AddNode(4,5);
    snek.AddNode(4,5);
    snek.AddNode(4,5);

    FFD = this;

    sound = new SoundController();
    sound.LoadMusic();
}

void draw(){
    println(frameRate);
 
    //Update the game on timer
    if(GameTime.Triggered()) HandleGame();
}

void HandleGame(){
    //Clear Background
    background(bkg);

    //Update Sound and Beat Detection
    sound.Update();

    //Manipulate Snake Object
    snek.Show();
    snek.Move();

    //Manipulate Food
    if(goal.isEaten(snek._posX,snek._posY)) {
        goal.Eat();
        snek.AddNode(snek._posX,snek._posY);
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
