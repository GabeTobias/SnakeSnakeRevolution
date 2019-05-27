//GLOBALS
Snake snek = new Snake(5,5);
Snake snek2 = new Snake(15,5,true);

Food goal;
Timer GameTime = new Timer(100);
GameState State = GameState.Playing;

//GLOBALS INITS
ParticleSystem particles;
ParticleSystem particlesBad;

ParticleSystem Crash;
ParticleSystem Chomp;


SoundController sound;
Renderer renderer;
LevelManager manager;

//Global Controls
float pulse = 1;
float jumpScale = 7;

float tx = 0;

PImage Arrow;
PImage ArrowBad;
PImage ArrowNeg;
PImage Background;

PShader blur;

Minim minim;
Level level;

void InitGame(){
    //Initalize Level Manager
    manager = new LevelManager();

    CONTEXT = this;

    //Load Sound Controller
    sound = new SoundController();
    try{ sound.LoadMusic(); } catch(Exception e){ println(e); }
    //get Current Level from Level Init
    level = manager.getLevel();

    goal = new Food(15,15);
    
    //Create a new Particle System
    particles = new ParticleSystem(0,0);
    
    particles.myImage = Arrow;
    particles._rotZ = 0;
    
    particles._YMin = -5;
    particles._YMax = -5;
    
    //Create a new Particle System
    particlesBad = new ParticleSystem(0,0);
    
    particlesBad.myImage = ArrowBad;
    particlesBad._rotZ = 0;
    
    particlesBad._YMin = -5;
    particlesBad._YMax = -5;
    
    //Create a new Particle System
    Chomp = new ParticleSystem(goal._posX,goal._posY,0);
    
    Chomp._Start = color(240,62,62);
    Chomp._Stop = color(253,126,20);
    
    Chomp._width = 20;
    Chomp._height = 20;
    Chomp._depth = 20;
    
    Chomp._XMin = -10;
    Chomp._XMax =  10;
    Chomp._YMin = -10;
    Chomp._YMax =  10;
    Chomp._ZMin = -10;
    Chomp._ZMax =  10;
    
    //Create a new Particle System
    Crash = new ParticleSystem(snek._posX,snek._posY,0);
    
    Crash.LifeTime = 1;
    
    Crash._Start = color(150);
    Crash._Stop = color(255);
    
    Crash._width = 20;
    Crash._height = 20;
    Crash._depth = 20;
    
    Crash._XMin = -10;
    Crash._XMax =  10;
    Crash._YMin = -10;
    Crash._YMax =  10;
    Crash._ZMin = -10;
    Crash._ZMax =  10;


    println(level._file);

    //Initialize the Renderer
    renderer = new Renderer();
}


void Render(){
  
    //Clear the screen
    background(0);
  
    hint(ENABLE_DEPTH_TEST);
  
    pulse = lerp(pulse,0,0.05f);

    //Get Current Level screen size
    PVector s = manager.getLevelSize();

    camera();

    //Center Level on Screen
    pushMatrix();
    
    
    hint(DISABLE_DEPTH_TEST);

    tint(255,130);
    image(Background,0,0);
    
    translate(width/2, (height/2));
    rotateX(PI/6);
    translate(-w/2, -h/2);
    
    scale(0.8,0.8,0.8);
    
    translate(
        ((width-s.x)/2) + 300,
        ((height-s.y)/2) + 300
    );

    //Draw The Background
    fill(255,255,255,100);
    noStroke();
    //rect(0,0,s.x,s.y);

    hint(ENABLE_DEPTH_TEST);

    //Render level walls
    level.Show();
    
    if(level.onBeat)filter(blur);
    
    drawBKG();


    hint(DISABLE_DEPTH_TEST);
    
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
    
    if(State == GameState.LevelComp){
        popMatrix();

        fill(0,150);
        rect(0,0,width,height);

        fill(255);
        textSize(100);
        text("Level Complete", (width/2)-(textWidth("Level Complete")/2),(height/2));

        textSize(30);

        return;
    }

    if(State == GameState.Loading){
        popMatrix();

        fill(67,18,75);
        rect(0,0,width,height);

        fill(255,65,100);
        textSize(150);
        text("Loading Level", (width/2)-(textWidth("Loading Level")/2),(height/2));

        textSize(30);

        return;
    }

    if(State == GameState.Win){
        popMatrix();

        fill(255,255);
        rect(0,0,width,height);

        fill(25);
        textSize(150);
        text("Thanks for playing", (width/2)-(textWidth("Thanks for Playing")/2),(height/2));

        textSize(30);

        return;
    }
    
    hint(ENABLE_DEPTH_TEST);
    
    Chomp.Show();
    Crash.Show();
    
    popMatrix();
    
    
    pushMatrix();

    translate(0,0,200);

    //Draw Arrows
    fill(255);
    
    if(secondPlayer){
      DrawArrow(300,height-300,50,50,0);
      DrawArrow(400,height-250,50,50,PI/2);
      DrawArrow(350,height-150,50,50,PI);
      DrawArrow(250,height-200,50,50,PI+(PI/2));
    }
    
    translate(1270,0,0);
    
      DrawArrow(300,height-300,50,50,0);
      DrawArrow(400,height-250,50,50,PI/2);
      DrawArrow(350,height-150,50,50,PI);
      DrawArrow(250,height-200,50,50,PI+(PI/2));
    
    particles.Show();               //Render Scene Particles
    particlesBad.Show();
    
    popMatrix();
    
    //Draw Score
    textSize(75);
    
    fill(selectOptions[snake1]);
    text(manager.foodCountPlayer1,75,100);
    
    if(secondPlayer){
      fill(selectOptions[snake2]);
      text(manager.foodCountPlayer2,width-150,100);
    }
}

void DrawArrow(int x0, int y0, int w, int h,float Dir){
  pushMatrix();
  
  translate(x0,y0);
  rotate(Dir);
  
  image(ArrowNeg,0,0,w,h);
  
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
        sound._AFood.play(0);
        snek.AddNode(int(snek.Last().x), int(snek.Last().y),false);
        goal.Eat(false);
    }

    //Manipulate Food
    if(secondPlayer && goal.isEaten(snek2._posX, snek2._posY)) {
        sound._AFood.play(0);
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

    sound._lb = millis();
}

enum GameState {
    Playing,
    LevelComp,
    Loading,
    GameOver,
    Win
}
