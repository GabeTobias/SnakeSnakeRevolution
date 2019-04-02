import ddf.minim.*;

//CONSTANTS
final int TILESIZE = 50;

//GLOBALS
Snake snek = new Snake(5,5);
Food goal = new Food(15,15);

Timer GameTime = new Timer(100);
Minim minim;

void setup(){
    size(800,800);
    
    minim = new Minim(this);

    snek.AddNode(4,5);
    snek.AddNode(4,5);
    snek.AddNode(4,5);
    snek.AddNode(4,5);
}

void draw(){
    //Update the game on timer
    if(GameTime.Triggered()) HandleGame();
}

void HandleGame(){
    //Clear Background
    background(51);

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
    if(key == 'w') snek.SetVelocity(0,-1);
    if(key == 'a') snek.SetVelocity(-1,0);
    if(key == 's') snek.SetVelocity(0,1);
    if(key == 'd') snek.SetVelocity(1,0);
}
