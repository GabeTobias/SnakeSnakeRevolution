import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 
import java.util.Map; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class SnakeSnakeRevolution extends PApplet {





//CONSTANTS
int TILESIZE = 40;

//DEBUGGERY
Object CONTEXT;
int bkg = 255;

int GameMode = 0;

public void setup(){
    
    noStroke();
    frameRate(120);

    LoadFonts();
    LoadImages();
    LoadShaders();

    InitGame();
    
    LoadAudio();
    
    setupBKG();
}

public void LoadFonts(){
    //Load Font
    PFont maven = createFont("Road_Rage.otf", 128);
    textFont(maven);
}

public void LoadShaders(){
    blur = loadShader("blur.frag");
}

public void LoadAudio(){
    sound._ASelect = minim.loadFile("Select.wav");;
    sound._AWrong = minim.loadFile("OffBeat.wav");
    sound._ALoading = minim.loadFile("Loading.wav");
    sound._ASecond =  minim.loadFile("Player2.wav");
    sound._AFood = minim.loadFile("food.wav");
    sound._ACrash = minim.loadFile("collision.wav");
    sound._AWin = minim.loadFile("win.wav");
}

public void LoadImages(){
    Title = loadImage("Title.png");
    Arrow = loadImage("Arrow.png");
    ArrowBad = loadImage("ArrowBad.png");
    ArrowNeg = loadImage("ArrowOutline.png");
    Background = loadImage("Background.png");
    Instructions = loadImage("instructions.png");
}

public void draw(){
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

public void RunGame(){
    Render();                   //Render scene object to screen              
    HandleGameplay();           //Update scene object
}
int cols, rows;
int scl = 30;
int menuScl = 30;
int w = 2000;
int h = 1600;

float flying = 0;
float localPulse;

float[][] terrain;

public void setupBKG() {
  cols = (w / scl)+50;
  rows = (h/ scl)+20;
  terrain = new float[cols][rows];
}


public void drawBKG() {

  flying -= 0.01f;

  float yoff = flying;
  for (int y = 0; y < rows; y++) {
    float xoff = 0;
    for (int x = 0; x < cols; x++) {
      terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, 100);
      xoff += 0.2f;
    }
    yoff += 0.2f;
  }

  stroke(255,100);
  strokeWeight(1);
  fill(0,0,0,100);

  localPulse = lerp(localPulse,constrain(pulse,0f,1f),0.1f);

  
  for (int y = 0; y < rows-50; y++) {
    beginShape(TRIANGLE_STRIP);
    for (int x = 0; x < cols-71; x++) {
      vertex(x*scl*1.5f, y*scl*1.5f, (terrain[x][y]/2) - 70);
      vertex(x*scl*1.5f, (y+1)*scl*1.5f, (terrain[x][y+1]/2) - 70);
    }
    endShape();
  }
  
  noStroke();
}

public void drawMenuBKG() {

  flying -= 0.005f;

  float yoff = flying;
  for (int y = 0; y < rows; y++) {
    float xoff = 0;
    for (int x = 0; x < cols; x++) {
      terrain[x][y] = map(noise(xoff/5, yoff/5), 0, 1, -100, 100);
      xoff += 0.2f;
    }
    yoff += 0.2f;
  }

  stroke(255);
  strokeWeight(1);
  fill(0,0,0,0);

  translate(width/2, (height/2));
  rotateX(PI/3);
  translate(-w/2, -h/2);  

  for (int y = 0; y < rows-2; y++) {
    beginShape(TRIANGLE_STRIP);
    for (int x = 0; x < cols; x++) {
      vertex((x*menuScl + (menuScl*2))-26*menuScl, y*menuScl, (terrain[x][y]*5) - 100);
      vertex((x*menuScl + (menuScl*2))-26*menuScl, (y+1)*menuScl, (terrain[x][y+1]*5) - 100);
    }
    endShape();
  }
  
  noStroke();
}

public class Food {
    int _posX, _posY;

    Material _material;

    public Food (int x0, int y0) {
        //Give a random position
        ChangePosition();

        //Initialize Render Object
        _material = new Material();
        _material._color = new PVector(1,0.5f,0.5f);
    }

    public boolean isEaten(int xx, int yy){
        return (xx == _posX && yy == _posY);
    }

    public void Show(){
        fill((sound.onBeat() ? color(255,100,100):color(100)));
        noLights();
        
        pushMatrix();
      
        translate(            
            _posX * TILESIZE + (TILESIZE/2),                               //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2) - (pulse*jumpScale),           //Y position scaled by tilesize
            -(pulse*jumpScale)
        );
      
        //Draw the head position
        sphere(
            TILESIZE/4
        );
        
        popMatrix();
        
        fill(255);
    }

    public void ChangePosition(){
        _posX = (int)random(level._width);
        _posY = (int)random(level._height);    

        while(level.GetBlock(_posX,_posY) != 0 || OverlapsSnake(_posX,_posY)){
            _posX = (int)random(level._width);
            _posY = (int)random(level._height);    
        }
    }

    public boolean OverlapsSnake(int xx, int yy){
        if(snek.OverlapsPoint(xx,yy)) return true;
        
        if(secondPlayer && snek2.OverlapsPoint(xx,yy)) return true;

        return false;
    }

    public void Eat(boolean second){
        //Handle Visuals
        FoodParticles();
        ChangePosition();
        
        //Increment Player Variable
        if(second)manager.foodCountPlayer2++;
        else manager.foodCountPlayer1++;
    }
    
    public void FoodParticles() {
      //Emit 20 Food Particles
      for(int i = 0; i < 20; i++) Chomp.Emit(_posX*TILESIZE,_posY*TILESIZE,0);
    }
}
//Selection Colors
int[] selectOptions = {color(45, 226, 230), color(255, 108, 17), color(255, 66, 100), color(35, 244, 192)};

//Snake position
float sx1 = 300, sx2 = 100000;
int sy1 = 100, sy2 = 100;

//Charecter Selections
int snake1, snake2;

//Multiplayer Toggle
boolean secondPlayer;

//Player Readdy
boolean ready1, ready2;

//Ready Timer
Timer rTimer = new Timer(5000);

//Count Down Count
int countDown = -1;

//GUI Images
PImage Title;
PImage Instructions;

public void StartMenu() {
  //Render Background Mesh
  MenuBackground();

  //Reset Camera View
  camera();
  hint(DISABLE_DEPTH_TEST);

  //Draw Title Image
  tint(255);
  image(Title, (width/2)-638, (height/2)-384, 1366, 768);
}

public void CharecterSelect() {
  
  //Render Background Mesh
  MenuBackground();

  pushMatrix();

    //Offset the screen position
    translate(width/3.5f, 200, 200);
  
    //Draw Snake A
    fill(selectOptions[snake1]);
    rect((snake1*100)+225, 590, 50, 10, 30);                                              //Cursor 1
    DrawSnakeA();                                                                         //Player 1
  
    //Draw Snake B
    if (secondPlayer) {
      fill(selectOptions[snake2]);
      rect((snake2*100)+225, 590, 50, 10, 30);                                            //Cursor 2
      DrawSnakeB();                                                                       //Player 2
    }
  
    //Pass Color values to snake Objects
    snek.myColor = selectOptions[snake1];
    snek2.myColor = selectOptions[snake2];
  
    //Move Snake Positions
    if (secondPlayer) {
      sx1 = (int)lerp(sx1, 125, 0.1f);
      sx2 = (int)lerp(sx2, 470, 0.1f);
    }
  
    //Draw Color options
    for (int i = 0; i < 4; ++i) {
      fill(selectOptions[i]);
      ellipse(
        (i*100)+250, 
        550, 
        50, 50
        );
    }
  
    //Ready Box 1
    fill((ready1) ? 255:25);
    rect(sx1, 675, 200, 50, 20);
  
    //Ready Box 2
    fill((ready2) ? 255:25);
    rect(sx2, 675, 200, 50, 20);
  
    textSize(20);
  
    //Ready1  Text
    fill((ready1) ? 25:255);
    text(
      (ready1) ? "Ready":"Selecting", 
      (!ready1) ? sx1+36:sx1+60, 708
      );
  
    //Ready2 Text
    fill((ready2) ? 25:255);
    text(
      (ready2) ? "Ready":"Selecting", 
      (!ready2) ? sx2+36:sx2+60, 708
    );

  popMatrix();

  //Draw Countdown Timer
  if (countDown > -1) {
    fill(255);
    textSize(200);
    text(countDown, textWidth(str(countDown)), textAscent()*1.3f);
  }

  //Handle Countdown
  if (ready1 && ready2 || ready1 && !secondPlayer) {
    countDown = 5 - rTimer.getTime();
    if (rTimer.Triggered()) GameMode++;
  }

  //Offset Margin for Arrow Drawing
  pushMatrix();
  translate(0, 0, 200);

  //Draw Arrows
  fill(255);

  //Set Text Properties
  textSize(20);
  fill(255);

  //Draw Directions
  DrawDirections();

  //Render Particles
  particles.Show();               //Render Scene Particles
  particlesBad.Show();

  popMatrix();
}

public void Instructions() {
  tint(255, 255);
  image(Instructions, 0, 0, width, height);
}

public void MenuBackground(){
  //Draw Background Mesh
  pushMatrix();
  drawMenuBKG();
  popMatrix();
  
  //Draw Background Sunset
  tint(255, 130);
  image(Background, 0, 0);
}

public void BackgroundTexture(){}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public void DrawDirections(){
  //Draw Arrows A
  DrawDirectionsA();

  //Offset Arrows to right side
  translate(1270, 0, 0);
  textSize(20);
  fill(255);
  
  //Draw Arrows B
  DrawDirectionsB();
}

public void DrawDirectionsA(){
  DrawArrow(250, height-375, 50, 50, 0);
  text("Select", 320, height-340);

  DrawArrow(300, height-225, 50, 50, PI/2);
  text("Move Right", 320, height-265);

  DrawArrow(250, height-250, 50, 50, PI+(PI/2));
  text("Left", 320, height-190);
}

public void DrawDirectionsB(){
  //Check for Second Player
  if (secondPlayer) {
    DrawArrow(100, height-375, 50, 50, 0);
    text("Select", 180, height-340);
  
    DrawArrow(150, height-225, 50, 50, PI/2);
    text("Move Right", 180, height-265);
  
    DrawArrow(100, height-250, 50, 50, PI+(PI/2));
    text("Move Left", 180, height-190);
  } else {
    DrawArrow(100, height-375, 50, 50, 0);
    text("Join Game", 180, height-340);
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

float t0 = 0;
float t1 = 200;

float sbx1;

public void DrawSnakeA() {
  float offset = constrain( (cos(millis()/200.0f) ), 0, 0.5f);
  sbx1 = lerp(sbx1, constrain(offset*300, 0, 150), 0.2f);

  if (sbx1 < 20) {
    t1 = lerp(t1, 0, 0.2f);
    t0 = lerp(t0, 150, 0.2f);
  }

  if (sbx1 > 130) {
    t1 = lerp(t1, 150, 0.2f);
    t0 = lerp(t0, 0, 0.2f);
  }

  //fill(255);
  ellipse(sbx1+sx1+25, 100, 50, 50);

  noFill();

  stroke(selectOptions[snake1]);
  strokeWeight(25);
  strokeJoin(ROUND);

  beginShape();

  vertex(sbx1+sx1+25, 100, 0);
  vertex(t1+sx1+25, 200, 0);
  vertex(t0+sx1+25, 200, 0);
  vertex(t1+sx1+25, 200, 0);

  vertex(t0+sx1+25, 200, 0);
  vertex(t0+sx1+25, 300, 0);
  vertex(t1+sx1+25, 300, 0);
  vertex(t0+sx1+25, 300, 0);

  vertex(t1+sx1+25, 300, 0);
  vertex(t1+sx1+25, 400, 0);

  endShape();

  noStroke();
}

public void DrawSnakeB() {
  float offset = constrain( (cos(millis()/200.0f) ), 0, 0.5f);
  sbx1 = lerp(sbx1, constrain(offset*300, 0, 150), 0.2f);

  if (sbx1 < 20) {
    t1 = lerp(t1, 0, 0.2f);
    t0 = lerp(t0, 150, 0.2f);
  }

  if (sbx1 > 130) {
    t1 = lerp(t1, 150, 0.2f);
    t0 = lerp(t0, 0, 0.2f);
  }

  //fill(255);
  ellipse(sbx1+sx2+25, 100, 50, 50);

  noFill();

  stroke(selectOptions[snake2]);
  strokeWeight(25);
  strokeJoin(ROUND);

  beginShape();

  vertex(sbx1+sx2+25, 100);
  vertex(t1+sx2+25, 200);
  vertex(t0+sx2+25, 200);
  vertex(t1+sx2+25, 200);

  vertex(t0+sx2+25, 200);
  vertex(t0+sx2+25, 300);
  vertex(t1+sx2+25, 300);
  vertex(t0+sx2+25, 300);

  vertex(t1+sx2+25, 300);
  vertex(t1+sx2+25, 400);

  endShape();

  noStroke();
}
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

public void InitGame(){
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


public void Render(){
  
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
    
    scale(0.8f,0.8f,0.8f);
    
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

public void DrawArrow(int x0, int y0, int w, int h,float Dir){
  pushMatrix();
  
  translate(x0,y0);
  rotate(Dir);
  
  image(ArrowNeg,0,0,w,h);
  
  popMatrix();
}

public void HandleGameplay(){       
    //Update the level manager
    manager.Update();

    //Handle Game Over State
    if(State == GameState.GameOver || State == GameState.Loading){
        return;
    }

    //Manipulate Food
    if(goal.isEaten(snek._posX, snek._posY)) {
        sound._AFood.play(0);
        snek.AddNode(PApplet.parseInt(snek.Last().x), PApplet.parseInt(snek.Last().y),false);
        goal.Eat(false);
    }

    //Manipulate Food
    if(secondPlayer && goal.isEaten(snek2._posX, snek2._posY)) {
        sound._AFood.play(0);
        snek2.AddNode(PApplet.parseInt(snek2.Last().x), PApplet.parseInt(snek2.Last().y),true);
        goal.Eat(true);
    }

    //Update Sound and Beat Detection
    sound.Update();      

    //Update all scene particles
    particles.Update();
}

public void onBeatEvent(){
    
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
boolean W,A,S,D;
boolean I,J,K,L;
int InputFrame;

public void keyPressed(){
    if(GameMode == 3) GameplayInput();
    else MenuInput();
}

public void keyReleased(){
    if(GameMode == 3) GameplayRelease();
}

public void GameplayRelease(){
    if(key == 'w')  W = false;
    if(key == 'a')  A = false;
    if(key == 's')  S = false;
    if(key == 'd')  D = false;

    if(key == 'i')  I = false;
    if(key == 'j')  J = false;
    if(key == 'k')  K = false;
    if(key == 'l')  L = false;
}

public void GameplayInput(){
    if(State == GameState.Win || State == GameState.GameOver) {
        if(key == 'w' && !W){
            GameMode = 0;
            
            manager.Restart();

            ready1 = false;
            ready2 = false;
            
            secondPlayer = false;

            countDown = -1;
            
            snek.ResetSnakeSize();
            snek2.ResetSnakeSize();
            
            sound._ALoading.play(0);

            setupBKG();

            W = true;
        }
        return;
    }
    
    if(!sound.onBeat()){
      
      if(key == 'w')particlesBad.Emit(300,height-300,0);
      if(key == 'a')particlesBad.Emit(250,height-200,PI+(PI/2));
      if(key == 's')particlesBad.Emit(350,height-150,PI);
      if(key == 'd')particlesBad.Emit(400,height-250,PI/2);
      
      if(key == 'i')particlesBad.Emit(300-1270,height-300,0);
      if(key == 'j')particlesBad.Emit(250-1270,height-200,PI+(PI/2));
      if(key == 'k')particlesBad.Emit(350-1270,height-150,PI);
      if(key == 'l')particlesBad.Emit(400-1270,height-250,PI/2);
      
      sound._AWrong.play(0);
    } else {
      
      if(key == 'w')particles.Emit(300,height-300,0);
      if(key == 'a')particles.Emit(250,height-200,PI+(PI/2));
      if(key == 's')particles.Emit(350,height-150,PI);
      if(key == 'd')particles.Emit(400,height-250,PI/2);
      
      if(key == 'i')particles.Emit(300-1270,height-300,0);
      if(key == 'j')particles.Emit(250-1270,height-200,PI+(PI/2));
      if(key == 'k')particles.Emit(350-1270,height-150,PI);
      if(key == 'l')particles.Emit(400-1270,height-250,PI/2);
      
      pulse = jumpScale;
    }

    //TODO: Replace with analog arduino inputs
    //Handle Player 1 Inputs
    if(key == 'w' && !W){
        if(snek._pVelY == 0 && level.GetBlock(snek._posX,snek._posY-1) != 1) 
            snek.SetVelocity (0,-1);
        
        W = true;
    }
    if(key == 'a' && !A){
        if(snek._pVelX == 0 && level.GetBlock(snek._posX-1,snek._posY) != 1) 
            snek.SetVelocity(-1, 0);
        
        A = true;
    }

    if(key == 's' && !S) {
        if(snek._pVelY == 0 && level.GetBlock(snek._posX,snek._posY+1) != 1) 
            snek.SetVelocity( 0, 1);
        
        S = true;
    }

    if(key == 'd' && !D) {
        if(snek._pVelX == 0 && level.GetBlock(snek._posX+1,snek._posY) != 1) 
            snek.SetVelocity( 1, 0);
        
        D = true;
    }

    //Handle Player 2 Inputs
    if(key == 'i'){
        if(snek2._pVelY == 0 && level.GetBlock(snek2._posX,snek2._posY-1) != 1)
            snek2.SetVelocity (0,-1);

        I = true;
    }
    if(key == 'j'){
        if(snek2._pVelX == 0 && level.GetBlock(snek2._posX-1,snek2._posY) != 1) 
            snek2.SetVelocity(-1, 0);

        J = true;
    }
    if(key == 'k') {
        if(snek2._pVelY == 0 && level.GetBlock(snek2._posX,snek2._posY+1) != 1) 
            snek2.SetVelocity( 0, 1);

        K = true;
    }
    if(key == 'l') {
        if(snek2._pVelX == 0 && level.GetBlock(snek2._posX+1,snek2._posY) != 1) 
            snek2.SetVelocity( 1, 0);

        L = true;
    }

    if(key == 'g') {
         State = GameState.GameOver;
    }

    if(key == 'o') {
        manager.LevelComplete();
    }

}

public void MenuInput(){
    if(millis() - InputFrame < 200) return;
  
    if(key == 'p'){
        sound._ALoading.play(0);
        GameMode = (GameMode+1)%4;
    }
    
    switch (GameMode) {
        //Start Screen
        case 0:
             if(key == 's'){
               GameMode++;
               sound._ALoading.play(0);
             }
            break;

        //Player Select
        case 1:
            //Player 2 controls
            if(key == 'i'  && !ready2) { 
                if(!secondPlayer) {
                  sound._ASecond.play(0);
                  secondPlayer = true;
                }
                else ready2 = true;
                
                sound._ASecond.play(0);
                
                rTimer.Reset();
            }
            if(key == 'j' && !ready2){
              sound._ASelect.play(0);
              snake2--;
            }
            if(key == 'l' && !ready2){
              sound._ASelect.play(0);
              snake2++;
            }

            //Player 1 controls
            if(key == 'a' && !ready1) {
              sound._ASelect.play(0);
              snake1--;
            }
            if(key == 'd' && !ready1){
              sound._ASelect.play(0);
              snake1++;
            }
            if(key == 'w'  && !ready1){
                sound._ASecond.play(0);
                ready1 = true;
                rTimer.Reset();
            }

            snake1 = constrain(snake1,0,3);
            snake2 = constrain(snake2,0,3);
            break;

        //Instruction Screen
        case 2:
            sound._ALoading.play(0);
            GameMode++;
            break;
    }
    
    InputFrame = millis();
}

class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
    int _tileSize;

    boolean onBeat;
    boolean hasFlipped;

    String _file,_song;

    public Level(int w, int h){
        //Initialize the array
        Data = new int[w][h];

        //Set Level size
        _width = w;
        _height = h;
    }

    public Level(String name){
        //Load file name
        _file = name;

        //Load level to data
        LoadLevel(name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void LoadLevel(String name){
        //Load JSON Object from file
        JSONObject file = loadJSONObject(name);

        //Load file Dimensions
        _width = file.getInt("width");
        _height = file.getInt("height");

        _song = file.getString("Song");

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

        if(width > height){
            _tileSize = height / _height;
        } else {
            _tileSize = width / _width;
        }

        println(_tileSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void Show(){
        lights();
      
        if(sound.onBeat() && !hasFlipped){
          onBeat = !onBeat;
          hasFlipped = true;
        }
        
        if(!sound.onBeat()) hasFlipped = false;
      
        //Style Blocks
        fill(onBeat ? color(67,18,75):color(37,1,45));

        //Loop through all spaces in level
        for (int x0 = 0; x0 < _width; ++x0) {
            for (int y0 = 0; y0 < _height; ++y0) {
                
                if(GetBlock(x0,y0) != 0)
                    DrawBlock(x0,y0);                   //Render each space
            }
        }

        //Reset block style
        fill(255);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void DrawBlock(int x0, int y0){
        noStroke();  
      
        pushMatrix();
      
        translate(            
            (x0 * TILESIZE)+30,                              //X position scaled by tilesize
            (y0 * TILESIZE)+30,                              //Y position scaled by tilesize
            -(pulse*jumpScale)
        );
      
        //Draw the head position
        box(
            TILESIZE,                   //width
            TILESIZE,                   //height
            TILESIZE/3
        );
        
        popMatrix();
    }

    public int GetBlock(int x0, int y0){
        if(x0 < 0 || y0 < 0 || x0 >= _width || y0 >= _height) return 0;

        return Data[x0][y0];
    }
}
class LevelManager {
    ArrayList<Level> Levels = new ArrayList<Level>();       //All leves neeeded to complete the game
    
    int LoadStart;

    int currentLevel;                                       //The current level loaded on the scene
    int foodCountPlayer1;
    int foodCountPlayer2;

    public LevelManager(){
        LoadLevels(SelectLevels());
    }

    public String[] SelectLevels(){
        String[] returns = {"Levels/Easy_0.JSON","Levels/Medium_3.JSON","Levels/Medium_1.JSON","Levels/Medium_2.JSON","Levels/Medium_0.JSON","Levels/PobablyHard_0.JSON","Levels/Easy_2.JSON",};
        return returns;
    }

    public void LoadLevels(String[] unloadedLevels){
        for (int i = 0; i < unloadedLevels.length; ++i) {
            //Load Level Data
            Level l = new Level(unloadedLevels[i]);
            Levels.add(l);
        }
    }

    public void Update(){
        if(State == GameState.Win) return;

        if(foodCountPlayer1 > 3 || foodCountPlayer2 > 3) LevelComplete();

        if(State == GameState.LevelComp) {
          if(millis() - LoadStart > 1000){
            ChangeLevel();
          }
        }

        if(State == GameState.Loading){
            if(millis() - LoadStart > 3000){
                State = GameState.Playing;
            }
        }
    }

    public void LevelComplete(){
      State = GameState.LevelComp;
      LoadStart = millis();

      manager.foodCountPlayer1 = 0;
      manager.foodCountPlayer2 = 0;

    }

    public void ChangeLevel(){
        State = GameState.Loading;
        LoadStart = millis();
        
        sound._ALoading.play(0);
        
        thread("LoadLevelData");
    }

    public Level getLevel(){
        return Levels.get(currentLevel);
    }

    public PVector getLevelSize(){
        return new PVector(
            level._width * level._tileSize,
            level._height * level._tileSize
        );
    }

    public void Restart(){
        thread("ResetGame");
    }
}

public void LoadLevelData(){
    if(manager.currentLevel >= 2){
        sound._AWin.play(0);
        State = GameState.Win;
    }

    manager.currentLevel++;
    level = manager.getLevel();
    
    manager.foodCountPlayer1 = 0;
    manager.foodCountPlayer2 = 0;

    snek.Reload();
    snek2.Reload();
    goal.ChangePosition();

    sound.PlayLevelSong(level);
}

public void ResetGame(){
    manager.currentLevel = 0;
    level = manager.getLevel();
    
    manager.foodCountPlayer1 = 0;
    manager.foodCountPlayer2 = 0;
    
    snek.Reload();
    snek2.Reload();
    goal.ChangePosition();

    sound.PlayLevelSong(level);

    //TODO: Change to play menu song
    //sound.PlayLevelSong(level);
    State = GameState.Playing;
}

/*
    - PreLoad all levels and switch between the,
    - Load every level as the scene changes
    ** Preload only the levels needed to complete the game **
*/
class Node {
    int _posX, _posY;
    int _nxt;

    boolean second;

    public Node(int x0, int y0, int next, boolean s){
        //Set initial position
        _posX = x0;
        _posY = y0;
    
        //Set the index of the next node in the list
        _nxt = next;

        //Set attached Snake
        second = s;
    }

    public void Show(){
        pushMatrix();
      
        translate(            
            _posX * TILESIZE,                               //X position scaled by tilesize
            _posY * TILESIZE  - (pulse*jumpScale)           //Y position scaled by tilesize
        );
      
        //Draw the head position
        box(
            TILESIZE,                   //width
            TILESIZE,                    //height
            TILESIZE
        );
        
        popMatrix();
    }

    public void Move() {
        if(!second){
            _posX = snek.GetNodeX(_nxt);
            _posY = snek.GetNodeY(_nxt);
        } else {
            _posX = snek2.GetNodeX(_nxt);
            _posY = snek2.GetNodeY(_nxt);
        }
    }
}
class ParticleSystem{
  //Is the particle 3D
  boolean TD;
  
  //Position of Emmiter & Start position of particle
  int _posX,_posY,_posZ;
  float _rotZ;
  
  //Min and Max Velocity Range
  int _XMax,_YMax = -10,_ZMax;
  int _XMin,_YMin = -10,_ZMin;
  
  //Size of Particles
  int _width = 50,_height = 50,_depth = 50;
  
  //Rang of Possible Colors
  int _Start = color(255),_Stop = color(255);
  
  PImage myImage;
  
  //Lifetime of a particle
  int LifeTime = 2;
  
  ArrayList<Particle> particles = new ArrayList<Particle>();
  
  public ParticleSystem(int x0, int y0){
    _posX = x0;
    _posY = y0;
  }
  
  public ParticleSystem(int x0, int y0, int z0){
    _posX = x0;
    _posY = y0;
    _posZ = z0;
    
    TD = true;
  }
  
  public void Show(){
    for(int i = 0; i < particles.size(); i++){
      Particle p = particles.get(i);
      

      p.Show();
      
      
      p.Update();
      
      if(p.dead) {
        particles.remove(p);
      }
    }
  }
  
  public void Update(){
    for(int i = 0; i < particles.size(); i++){
      Particle p = particles.get(i);

    }
  }
  
  public void Emit(){ 
    Particle p = getParticle();
    particles.add(p); 
    
    p.myImage = myImage;
  }
  
  public void Emit(int x0, int y0, int z0){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    p._posZ = z0;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public void Emit(int x0, int y0){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public void Emit(int x0, int y0, float rot){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    p._rotZ = rot;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public Particle getParticle(){
    if(TD){
      PVector v = getVel();
      int c = getColor();
      
      return new Particle(
        _posX,_posY,_posZ,
        (int)v.x,(int)v.y,(int)v.z,
        _width,_height,_depth,
        c,LifeTime*60
      );
    } else {
      PVector v = getVel();
      int c = getColor();
      
      return new Particle(
        _posX,_posY,
        (int)v.x,(int)v.y,
        _width,_height,
        c,LifeTime*60
      );
    }
  }
  
  public PVector getVel(){
    return new PVector(
      random(_XMin,_XMax),
      random(_YMin,_YMax),
      random(_ZMin,_ZMax)
    );
  }
  
  public int getColor(){
    return lerpColor(_Start,_Stop,random(1));
  }
  
}

class Particle{
  //Position of particle
  int _posX,_posY,_posZ;
  
  //Velocity of particle
  int _velX,_velY,_velZ;
  
  //Size of Particles
  int _width = 50,_height = 50,_depth = 50;
  
  int _color;
  
  PImage myImage;
  
  int life;
  int limit;
  
  float _rotZ;
  
  boolean dead;
  boolean TD;
  
  public Particle(int x0, int y0, int vx, int vy, int w, int h, int c, int lifetime){
    _color = c;
    
    _width = w;
    _height = h;
    
    _velX = vx;
    _velY = vy;
    
    _posX = x0;
    _posY = y0;
    
    limit = lifetime;
    TD = false;
  }
  
  public Particle(int x0, int y0, int z0, int vx, int vy, int vz, int w, int h, int d, int c, int lifetime){
    _color = c;
    
    _width = w;
    _height = h;
    _depth = d;
    
    _velX = vx;
    _velY = vy;
    _velZ = vz;
    
    _posX = x0;
    _posY = y0;
    _posZ = z0;
    
    limit = lifetime;
    TD = true;
  }
  
  public void Update(){
    _posX += _velX;
    _posY += _velY;
    _posZ += _velZ;
    
    life++;
        
    //For Later Cleanup
    if(life > limit) dead = true;
  }
  
  public void Show(){
    float s = 1-(PApplet.parseFloat(life)/PApplet.parseFloat(limit));
    fill(_color,s*255);
    
    if(TD){
      pushMatrix();
      
      translate(_posX,_posY,_posZ);
      box(_width,_height,_depth);
      
      popMatrix();
    } else {
      if(myImage == null)rect(_posX,_posY,_width,_height);
      else {
        pushMatrix();
        
        tint(255,s*255);
      
        translate(_posX,_posY);
        rotateZ(_rotZ);
      
        image(myImage,0,0,_width,_height);
      
        popMatrix();
      }
    }
  }
}


class Renderer {
    //Static Layer Shaders
    PShader _bkgShader;
    PShader _floorShader;
    PShader _skyShader;

    //Non-static shader
    PShader _objectShader;

    PImage tilemap = new PImage(800/TILESIZE,800/TILESIZE);

    //Default Constructor
    public Renderer(){
        _objectShader = loadShader("obj.frag");
        _floorShader = loadShader("Floor.frag");
        
        //Bind Initial level to image
        BindFloorToImage();
    }

    //Fire before rendering any non-static objects
    public void PreRender(){
        //Render the Background
        LoadShader(_bkgShader);
        DrawBackground();

        //Render the Floor
        LoadShader(_floorShader);
        DrawFloor();
    }

    //Fire After all non-static objects
    public void PostRender() {
        //Use Default Shader
        resetShader();

        //Render the Sky
        LoadShader(_skyShader);
        DrawSky();
    }

    //Load Shaders
    public void LoadShader(PShader shader){}

    //Static Shader Functions
    public void DrawBackground(){
        //Clear Background
        background(bkg);
    }
    public void DrawSky(){}

    //Floor Functions
    public void BindFloorToImage(){
        for (int x0 = 0; x0 < 800/TILESIZE; ++x0) {
            for (int y0 = 0; y0 < 800/TILESIZE; ++y0) {
                if(level.GetBlock(x0,y0) != 0){
                    tilemap.set(x0,y0,color(255,0,0));
                }
            }
        }

        _floorShader.set("_tilemap",tilemap);
        _floorShader.set("_color", new PVector(1,1,1));
    }

    public void DrawFloor(){
        shader(_floorShader);
        rect(0,0,800,800);

        image(tilemap,0,0,200,200);
    }

    //Render Object Functions
    public void BindMaterial(Material material, int x0, int y0){
        _objectShader.set("_color",material._color);
        _objectShader.set("_opacity",1f);

        _objectShader.set("_shape",material._shape.getValue());

        _objectShader.set("_position", PApplet.parseFloat(x0), PApplet.parseFloat(y0));
    }

    //Renderer for all non-static objects
    public void DrawObject(Material material, int x0, int y0){
        //Bind Properties
        BindMaterial(material, x0, y0);

        //Handle Rendering
        shader(_objectShader);
        rect(x0,y0,TILESIZE,TILESIZE*1.2f);
    }
}

class Material {
    Shape _shape;
    PVector _color;

    boolean _recieveLight;
    boolean _shadows;
    boolean _pulsing;

    public Material(){
        _color = new PVector(0.5f,0.5f,1);
        _shape = Shape.Square;
    }
}

enum Shape {
    Circle(0),
    Square(1),
    Cone(2),
    Billboard(3);

    private final int value;
    private Shape(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum ShaderType {
    Default,
    LineShader,
    BackgroundShader
}

/*

RenderObject:
    Shape
    Properties
    Vert Shader
    Frag Shader

Rendering:
    Render the Background:
        LoadShader(BKGShader)
        DrawBackground()

    Render the Floor:
        LoadShader(FloorShader)
        BindFloorToImage()
        BindImageToShader()
        DrawFloor()

    Render the Foreground:
        for(All Render Objects){
            LoadShader(ObjectShader)
            BindMaterial(ObjectMaterial)
            DrawShape(ObjectShape)
        }

    Render the Sky:
        LoadShader(BKGShader)
        DrawSky()
*/

class Snake extends Node{
    int _posX, _posY;
    int _velX = 1, _velY;
    int _pVelX = 1, _pVelY;

    boolean second;

    int myColor;

    ArrayList<Node> _nodes = new ArrayList<Node>();

    public Snake(int x0, int y0){
        //Pass variables to parent with a null next node
        super(x0,y0,0,false);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        //Add default body
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
    }

    public Snake(int x0, int y0, boolean s){
        //Pass variables to parent with a null next node
        super(x0,y0,0,true);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        second = s;

        //Add default body
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);

        second = true;
    }

    public void Show(){
        fill(myColor);
        
        noLights();
        
        pushMatrix();
      
        translate(            
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2)           //Y position scaled by tilesize
        );
      
        //Draw the head position
        sphere(
            TILESIZE/2                   //width
        );
        
        popMatrix();

        stroke(myColor);
        strokeWeight(TILESIZE/2);

        boolean b = (abs(_posX - _nodes.get(0)._posX) > 3);
        boolean a = (abs(_posY - _nodes.get(0)._posY) > 3);

        //Connect Head to body
        if(!a && !b){
            line(
                _posX*TILESIZE + (TILESIZE/2),
                _posY*TILESIZE + (TILESIZE/2),
                _nodes.get(0)._posX*TILESIZE + (TILESIZE/2),
                _nodes.get(0)._posY*TILESIZE + (TILESIZE/2)
            );
            
            int x0 = _posX*TILESIZE + (TILESIZE/2);
            int y0 = _posY*TILESIZE + (TILESIZE/2);
                   
            int x1 = _nodes.get(0)._posX*TILESIZE + (TILESIZE/2);
            int y1 = _nodes.get(0)._posY*TILESIZE + (TILESIZE/2);
            
            int sx = (x0 > x1) ? x0-x1 : x1-x0;
            int sy = (y0 > y1) ? y0-y1 : y1-y0;
            
            int xx = (x0 > x1) ? x1 + ((x0-x1)/2) : x0 + ((x1-x0)/2);
            int yy = (y0 > y1) ? y1 + ((y0-y1)/2) : y0 + ((y1-y0)/2);
            
            pushMatrix();

            translate(xx,yy);

            if(sx == 0) box(TILESIZE/16,sy,TILESIZE/16);
            else box(sx,TILESIZE/16,TILESIZE/16);
            
            popMatrix();

        }

        //Render Node Listing
        for(int i = _nodes.size()-2; i >= 0; i--) {
            //Get the current node
            Node n1 = _nodes.get(i);
            Node n2 = _nodes.get(i+1);

            if(abs(n1._posX - n2._posX) > 3) continue;
            if(abs(n1._posY - n2._posY) > 3) continue;
            
            int x0 = n1._posX*TILESIZE + (TILESIZE/2);
            int y0 = n1._posY*TILESIZE + (TILESIZE/2);
                   
            int x1 = n2._posX*TILESIZE + (TILESIZE/2);
            int y1 = n2._posY*TILESIZE + (TILESIZE/2);
            
            int sx = (x0 > x1) ? x0-x1 : x1-x0;
            int sy = (y0 > y1) ? y0-y1 : y1-y0;
            
            int xx = (x0 > x1) ? x1 + ((x0-x1)/2) : x0 + ((x1-x0)/2);
            int yy = (y0 > y1) ? y1 + ((y0-y1)/2) : y0 + ((y1-y0)/2);
            
            pushMatrix();

            translate(xx,yy);

            if(sx == 0) box(TILESIZE/16,sy,TILESIZE/16);
            else box(sx,TILESIZE/16,TILESIZE/16);

            popMatrix();

        }
        
        noStroke();
    }

    public void Move() {
        //Move Node Listing
        for(int i = _nodes.size()-1; i >= 0; i--) {
            //Get the current node
            Node n = _nodes.get(i);

            //Update current node data
            n.Move();
        }

        int nextX = _posX + _velX;
        int nextY = _posY + _velY;

        //Handle Level Collision
        if(level.GetBlock(nextX,nextY) != 0){
            //if moving along x axis change to y direction
            if(nextX != _posX){
                boolean up = level.GetBlock(_posX,_posY+1) != 0 && !OverlapsPoint(_posX,_posY+1);
                boolean down = level.GetBlock(_posX,_posY-1) != 0 && !OverlapsPoint(_posX,_posY-1);

                _velX = 0;
                _velY = (up) ? -1 : 1;
                
                Crash._XMin = 0;
                Crash._XMax = 0;
                Crash._YMin = -10;
                Crash._YMax =  10;
                Crash._ZMin = -10;
                Crash._ZMax =  10;

                sound._ACrash.play(0);
            } else {
                boolean right = level.GetBlock(_posX+1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);
                boolean left = level.GetBlock(_posX-1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);

                _velX = (right) ? -1 : 1;
                _velY = 0;
                
                Crash._XMin = -10;
                Crash._XMax =  10;
                Crash._YMin = 0;
                Crash._YMax = 0;
                Crash._ZMin = -10;
                Crash._ZMax =  10;
                
                sound._ACrash.play(0);
            } 
            
            if(sound.onBeat())
              for(int i = 0; i < 50; i++) Crash.Emit(_posX*TILESIZE,_posY*TILESIZE,0);
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        _pVelX = _velX;
        _pVelY = _velY;

        //Screen Limits
        PVector levelSize = manager.getLevelSize();

        //Loop head position Bottom and Right
        if(_posX*TILESIZE > levelSize.x-1) _posX = 0;
        if(_posY*TILESIZE > levelSize.y-1) _posY = 0;

        //Loop head position Top and Left
        if(_posX < 0) _posX = (int)(levelSize.x / TILESIZE)-1;
        if(_posY < 0) _posY = (int)(levelSize.y / TILESIZE)-1;

        //Death Checks
        if(OverlapsSelf()) {
            if(!secondPlayer) {
              //sound._AGameOver.play(0);
              State = GameState.GameOver;
            } else Reload();
            
            println("Overlap");
        }
    }

    public void AddNode(int x0, int y0, boolean s) {
        //Find the last usable node
        int nxt = (_nodes.size() > 0) ? _nodes.size() : 0;

        //Create a new node
        Node n = new Node(x0,y0, nxt,s);

        //Add nod
        _nodes.add(n);
    }

    public void Reload(){
        _nodes.clear();

        //Set default position of head
        _posX = 2;
        _posY = 2;

        //Add default body
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
    }

    ///////////////////////////////////////////////////////////////
    //////////////////////GETTERS & SETTERS////////////////////////
    ///////////////////////////////////////////////////////////////
    
    public void ResetSnakeSize(){
        _nodes.clear();
      
        //Add default body
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
    }

    //Set the directio of the head node
    public void SetVelocity(int x0, int y0){
        _velX = x0;
        _velY = y0;
    }

    //Get the current x position of a listed node
    public int GetNodeX(int index){
        if(index > 0){
            return _nodes.get(index-1)._posX;
        } else {
            return _posX;
        }
    }

    //Get the current y position of a listed node
    public int GetNodeY(int index){
        if(index > 0){
            return _nodes.get(index-1)._posY;
        } else {
            return _posY;
        }
    }

    //Detect Overlaps with the head node
    public boolean OverlapsSelf(){
        boolean r = false;

        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(_posX == n._posX && _posY == n._posY)
                r = true;
        }

        return r;
    }

    //Detect Overlaps with the head node
    public boolean OverlapsPoint(int x0, int y0){
        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(x0 == n._posX && y0 == n._posY)
                return true;
        }

        return false;
    }

    public PVector Last(){
        return new PVector(_nodes.get(_nodes.size()-1)._posX,_nodes.get(_nodes.size()-1)._posY);
    }
}


class SoundController {
    HashMap <String,AudioPlayer> SongLibrary = new HashMap<String,AudioPlayer>();

    //Active Audio Players
    AudioPlayer _player;
    AudioInput _input;
    
    //Sound Effects
    AudioPlayer _ASelect;
    AudioPlayer _AGameOver;
    AudioPlayer _AWin;
    AudioPlayer _AWrong;
    AudioPlayer _ALoading;
    AudioPlayer _ASecond;
    AudioPlayer _AFood;
    AudioPlayer _ACrash;

    
    //Alternate beat detection system
    BeatDetect detector;

    //Audio beat timer
    Timer _beat = new Timer(fromBPM(45.75f/2.0f));

    //Music Timing Managment
    float _bpm = 122.0f/2;
    float _sub = 2.0f;
    float _lb = 0;
    
    float LastFrame;
    float DeltaTime;
    
    boolean _MusicSynced;

    public SoundController(){
        //Initialize Minim library
        minim = new Minim(CONTEXT);

        //Initialize Beat Detector
        detector = new BeatDetect();

        //Load the current audio and play
        _player = minim.loadFile("The Synth Wars - Jack O'Reilly.mp3");
        //_player.setGain(-80);
        _player.loop();
    }

    //Future Audio Managment
    public void LoadSounds(){}
    
    public void LoadMusic() throws Exception {
        for(int i = 0; i < manager.Levels.size(); i++){
            //Load song into memory
            Level l = manager.Levels.get(i);
            AudioPlayer player = minim.loadFile(l._song);

            if(player == null) throw new Exception("Null Pointer");

            //Add Song to library
            SongLibrary.put(l._song,player);
        }
    }

    public void PlayLevelSong(Level l){
        //Pause current song
        _player.pause();

        //Select levels song from library
        _player = SongLibrary.get(l._song);

        //Play song
        _player.loop();
    }

    public void Update(){
        boolean checkBeat = onBeat();               //Check if frame is on beat
        if(checkBeat) onBeatEvent();                //Change background to beat
        
        DeltaTime = millis()-LastFrame;
        LastFrame = millis();
    }


    public void SetBPM(int bpm){
        _beat.setInterval(fromBPM(bpm));            //Set the timer Interval to bpm
        _beat.Reset();                              //Reset the timer
        _beat.setLatency(100);                      //Adjust the latency
        _bpm = bpm;
    }

    public void SetSubdivision(int sub){
        _sub = sub;
    }

    public int fromBPM(float bpm){
        float sec = bpm / 60.0f;                     //Convert from bpm to seconds
        float millis = sec * 1000;                  //Convert from seconds to milliseconds

        return PApplet.parseInt(millis);
    }

    ///////////////////////////////////////////////////////////////
    /////////////////////////Beat Detection////////////////////////
    ///////////////////////////////////////////////////////////////

    public boolean onBeat(){
        int beatTiming = fromBPM(_bpm/_sub);        //Current time between beats
        int time = _player.position();              //Time since audio started
        int offset = time % beatTiming;             //Time till the next beat
        
        return (offset <= 300);
    }
    
    public boolean onOffBeat(){
        int beatTiming = fromBPM((_bpm/_sub)*2);        //Current time between beats
        int time = _player.position();              //Time since audio started
        int offset = time % beatTiming;             //Time till the next beat
        
        return (offset <= 300);
    }
    
    public boolean ExactBeat(){
      int beatTiming = fromBPM((_bpm/_sub)*2);        //Current time between beats
      int time = _player.position();              //Time since audio started
      int offset = time % beatTiming;             //Time till the next beat
      
      return offset <= DeltaTime;
    }

    ///////////////////////////Depricated///////////////////////////

    //Detect Down Beat
    public boolean detectTiming() {
        if(!_MusicSynced && _beat.Triggered()){     //Check for the first 
            _player.cue(0);                         //Restart the audio
            _MusicSynced = true;                    //tag the audio as started
        }

        return _beat.Triggered();                   //Check the audio timer
    }

    //TODO: Detect audio desync
    public void Calibrate(){
        if(detectRythm() && !onBeat()) {            //Detect Audio Desync
            _beat.Reset();                          //Reset the Audio
        }
    }

    public boolean detectRythm(){
        detector.detect(_player.mix);               //Process the current position in audio
        return detector.isOnset();                  //Check the audio for intensity
    }
}

class Timer {
    float _interval;                                            //Time between ticks
    float _latency;
    float _previousTick;                                        //Last time the system fired

    public Timer(float interval){
        //Set interval for timer
        _interval = interval;
    }

    public boolean Triggered(){
        //Time since the last trigger
        float currentInterval = millis() - _previousTick;

        //Check if too much time has passed
        if(currentInterval >= _interval-_latency){
            if(currentInterval >= _interval) 
                _previousTick = millis();                        //Update the previous interval
            
            return true;                                         //Return the frame to be true
        }

        //Return nothing
        return false;
    }

    public int getTime(){
        return (int)((millis() - _previousTick)/1000);
    }

    public void setLatency(float val){
        _latency = val;
    }

    public void Reset(){
        _previousTick = millis();
    }

    public void setInterval(float inter){
        _interval = inter;
    }
}
  public void settings() {  fullScreen(P3D,2); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakeSnakeRevolution" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
