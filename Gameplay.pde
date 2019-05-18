//GLOBALS
Snake snek = new Snake(5,5);
Snake snek2 = new Snake(15,5,true);

Food goal;
Timer GameTime = new Timer(100);
GameState State = GameState.Playing;

//GLOBALS INITS
ParticleSystem particles;
SoundController sound;
Renderer renderer;
LevelManager manager;

//Global Controls
float pulse;
float jumpScale = 10;

Minim minim;
Level level;

void InitGame(){
    //Initalize Level Manager
    manager = new LevelManager();

    //Load Sound Controller
    sound = new SoundController();
    sound.LoadMusic();

    //Create a new Particle System
    particles = new ParticleSystem();

    //get Current Level from Level Init
    level = manager.getLevel();

    goal = new Food(15,15);

    println(level._file);

    //Initialize the Renderer
    renderer = new Renderer();
}


void Render(){
    //Clear the screen
    background(0);

    pulse = lerp(pulse,0,0.1f);

    //Get Current Level screen size
    PVector s = manager.getLevelSize();

    //Center Level on Screen
    pushMatrix();
    translate(
        (width-s.x)/2,
        (height-s.y)/2
    );

    //Draw The Background
    fill(255);
    rect(0,0,s.x,s.y);

    //Render level walls
    level.Show();

    //Render Non-static objects
    snek.Show();                    //Manipulate Snake Object
    if(secondPlayer)snek2.Show();   //Manipulate Snake Object

    goal.Show();                    //Render Goal Object

    TILESIZE = level._tileSize;

    if(State == GameState.GameOver){
        popMatrix();

        fill(25,170);
        rect(0,0,width,height);

        fill(255);
        textSize(100);
        text("Game Over", (width/2)-(textWidth("Game Over")/2),(height/2));

        textSize(30);

        return;
    }

    if(State == GameState.Loading){
        popMatrix();

        fill(255,255);
        rect(0,0,width,height);

        fill(25);
        textSize(100);
        text("Loading", (width/2)-(textWidth("Loading")/2),(height/2));

        textSize(30);

        return;
    }

    if(State == GameState.Win){
        popMatrix();

        fill(255,255);
        rect(0,0,width,height);

        fill(25);
        textSize(80);
        text("Thanks for playing", (width/2)-(textWidth("Thanks for Playing")/2),(height/2));

        textSize(30);

        return;
    }

    particles.Show();               //Render Scene Particles

    popMatrix();
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
        snek.AddNode(int(snek.Last().x), int(snek.Last().y),false);
        goal.Eat(false);
    }

    //Manipulate Food
    if(secondPlayer && goal.isEaten(snek2._posX, snek2._posY)) {
        snek2.AddNode(int(snek2.Last().x), int(snek2.Last().y),true);
        goal.Eat(true);
    }

    //Update Sound and Beat Detection
    sound.Update();      

    //Update all scene particles
    particles.Update();
}

void onBeatEvent(){
    
    //Delay Gameplay 100 milliseconds
    if(GameTime.Triggered() && State == GameState.Playing){
        snek.Move();
        if(secondPlayer) snek2.Move();
    }

    //pulse = 1;

    sound._lb = millis();
}

enum GameState {
    Playing,
    Loading,
    GameOver,
    Win
}