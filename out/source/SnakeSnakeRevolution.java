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
    //Basic Setup
    //16X9
    //size(1920,1080,P2D);
    
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

public void draw(){
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

public void RunGame(){
    Render();                   //Render scene object to screen              
    HandleGameplay();           //Update scene object
}

public void RunGUI(){}


/*

Assets
    Arrow Tile Sets
    Misteps Box
    Background 

*/

public class Food {
    int _posX, _posY;

    Material _material;

    public Food (int x0, int y0) {
        _posX = x0;
        _posY = y0;    

        //Initialize Render Object
        _material = new Material();
        
        _material._color = new PVector(1,0.5f,0.5f);
    }

    public boolean isEaten(int xx, int yy){
        return (xx == _posX && yy == _posY);
    }

    public void Show(){
        fill(255,100,100);

        //Draw the head position
        ellipse(
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2),           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );
        
        fill(255);
    }

    public void ChangePosition(){
        _posX = (int)random(width/TILESIZE);
        _posY = (int)random(height/TILESIZE);    

        while(level.GetBlock(_posX,_posY) != 0){
            _posX = (int)random(width/TILESIZE);
            _posY = (int)random(height/TILESIZE);   
        }
    }

    public void Eat(boolean second){
        ChangePosition();
        if(second)manager.foodCountPlayer2++;
        else manager.foodCountPlayer2++;
    }
}
//Selection Colors
int[] selectOptions = {color(255,224,102),color(103,65,217),color(34,184,207),color(130,201,30),color(253,126,20),color(240,62,62)};

//Snake position
int sx1 = 300,sx2 = 100000;
int sy1 = 100,sy2 = 100;

//Charecter Selections
int snake1,snake2;

//Multiplayer Toggle
boolean secondPlayer;

//Player Readdy
boolean ready1,ready2;

//Ready Timer
Timer rTimer = new Timer(8000);

//Count Down Count
int countDown = -1;

public void StartMenu(){}

public void CharecterSelect() {
    pushMatrix();
    translate(width/4.5f, 0);

    //Render Snakes
    fill(selectOptions[snake1]);
    rect(sx1,sy1, 200,200);                                                             //Player 1

    rect((snake1*100)+125,590,50,10,30);                                                //Cursor 1
    
    fill(selectOptions[snake2]);
    rect(sx2,sy2, 200,200);                                                             //Player 2

    rect((snake2*100)+125 + ((secondPlayer) ? 0:8000),590,50,10, 30);                    //Cursor 2

    snek.myColor = selectOptions[snake1];
    snek2.myColor = selectOptions[snake2];

    //Move Snake Positions
    if(secondPlayer){
        sx1 = (int)lerp(sx1,125,0.1f);
        sx2 = (int)lerp(sx2,470,0.1f);
    }

    //Draw Color options
    for (int i = 0; i < 6; ++i) {
        fill(selectOptions[i]);
        ellipse(
            (i*100)+150,
            550,
            50,50
        );
    }

    //Ready Box 1
    fill((ready1) ? 255:100);
    rect(sx1, 675, 200,50,20);

    //Ready Box 2
    fill((ready2) ? 255:100);
    rect(sx2, 675, 200,50,20);

    popMatrix();

    //Draw Countdown Timer
    if(countDown > -1 && countDown < 6){
        fill(255);
        textSize(100);
        text(countDown,(width/2)-(textWidth(str(countDown))/2),(height/2)-(textAscent()/2));
    }


    if(ready1 && ready2 || ready1 && !secondPlayer){
        countDown = 8 - rTimer.getTime();
        if(rTimer.Triggered()) GameMode++;
    }
}

public void Instructions(){}
//GLOBALS
Snake snek = new Snake(5,5);
Snake snek2 = new Snake(15,5,true);

Food goal = new Food(15,15);
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

public void InitGame(){
    //Initalize Level Manager
    manager = new LevelManager();

    //Load Sound Controller
    sound = new SoundController();
    sound.LoadMusic();

    //Create a new Particle System
    particles = new ParticleSystem();

    //get Current Level from Level Init
    level = manager.getLevel();

    println(level._file);

    //Initialize the Renderer
    renderer = new Renderer();
}


public void Render(){
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

        fill(25,170);
        rect(0,0,800,800);

        fill(255);
        textSize(100);
        text("Game Over", 400-(textWidth("Game Over")/2),400);

        textSize(30);

        popMatrix();
        return;
    }

    if(State == GameState.Loading){

        fill(255,255);
        rect(0,0,800,800);

        fill(25);
        textSize(100);
        text("Loading", 400-(textWidth("Loading")/2),400);

        textSize(30);

        popMatrix();
        return;
    }

    if(State == GameState.Win){

        fill(255,255);
        rect(0,0,800,800);

        fill(25);
        textSize(80);
        text("Thanks for playing", 400-(textWidth("Thanks for Playing")/2),400);

        textSize(30);
    
        popMatrix();
        return;
    }

    particles.Show();               //Render Scene Particles

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
        snek.AddNode(PApplet.parseInt(snek.Last().x), PApplet.parseInt(snek.Last().y),false);
        goal.Eat(false);
    }

    //Manipulate Food
    if(secondPlayer && goal.isEaten(snek2._posX, snek2._posY)) {
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

    //pulse = 1;

    sound._lb = millis();
}

enum GameState {
    Playing,
    Loading,
    GameOver,
    Win
}
boolean W,A,S,D;
boolean I,J,K,L;

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
    if(State == GameState.GameOver) return;
    
    if(!sound.onBeat()) particles.Emmit(50,200,"Wrong");

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
        manager.ChangeLevel();
    }

}

public void MenuInput(){
    if(key == 'p'){
        GameMode = (GameMode+1)%4;
    }
    
    switch (GameMode) {
        //Start Screen
        case 0:
            GameMode++;
            break;

        //Player Select
        case 1:
            //Player 2 controls
            if(key == 'i') { 
                if(!secondPlayer) secondPlayer = true;
                else ready2 = true;
                
                rTimer.Reset();
            }
            if(key == 'j') snake2--;
            if(key == 'l') snake2++;

            //Player 1 controls
            if(key == 'a') snake1--;
            if(key == 'd') snake1++;
            if(key == 'w'){
                ready1 = true;
                rTimer.Reset();
            }

            snake1 = constrain(snake1,0,5);
            snake2 = constrain(snake2,0,5);
            break;

        //Instruction Screen
        case 2:
            GameMode++;
            break;
    }
}

class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
    int _tileSize;

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

        if(_height > _width){
            _tileSize = height / _height;
        } else {
            _tileSize = width / _width;
        }

        println(_tileSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void Show(){
        //Style Blocks
        fill(200);

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
        //Draw the head position
        rect(
            (x0 * TILESIZE),                              //X position scaled by tilesize
            (y0 * TILESIZE) + (pulse*jumpScale),                              //Y position scaled by tilesize
            (TILESIZE),                                   //width
            (TILESIZE)                                    //height
        );
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
        String[] returns = {"Levels/Level_3.JSON","Levels/Level_0.JSON","Levels/Level_2.JSON"};
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
        if(foodCountPlayer1 > 3 || foodCountPlayer2 > 3) ChangeLevel();

        if(State == GameState.Loading){
            if(millis() - LoadStart > 3000){
                State = GameState.Playing;
            }
        }
    }

    public void ChangeLevel(){
        State = GameState.Loading;
        LoadStart = millis();
        
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
}

public void LoadLevelData(){
    if(manager.currentLevel >= 2){
        State = GameState.Win;
    }

    manager.currentLevel++;
    level = manager.getLevel();
    
    manager.foodCountPlayer1 = 0;
    manager.foodCountPlayer2 = 0;

    snek.Reload();
    goal.ChangePosition();

    sound.PlayLevelSong(level);
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
        //Draw the head position
        rect(
            _posX * TILESIZE,           //X position scaled by tilesize
            _posY * TILESIZE,           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );
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


class Particle {
    //Position and velocity
    float _posX, _posY;
    float _velX, _velY = -10;

    //Visable properties
    int _padding = 5;
    int _alpha = 255;

    //Flag for particle system to remove it
    boolean dead;
    
    //Displayed text
    String _label = "Default";

    //Death timer
    Timer LifeTime;

    public Particle (float lifespan){
        //Initialize Death Timer
        LifeTime = new Timer(lifespan);
        LifeTime.Reset();
    }

    public void Show(){
        fill(100,255,100,_alpha);

        //Draw Background
        rect(
            _posX,
            _posY,
            textWidth(_label) + (_padding * 2),
            textAscent() + (_padding*2)
        );

        fill(100,100,100,_alpha);

        //Render Text
        text(
            _label,
            _posX + _padding,
            _posY + _padding*3
        );

        fill(255);
    }

    public void Update(){
        //Handle death system
        if(LifeTime.Triggered()) Die();

        //Handle Movement
        _posX = lerp(_posX, _posX + _velX, 0.2f);
        _posY = lerp(_posY, _posY + _velY, 0.2f);

        //Handle Alpha Fade
        _alpha -=  4f;
    }

    public void Die(){ dead = true; }
}



class ParticleSystem {
    //All active particles
    ArrayList<Particle> particles = new ArrayList<Particle>();

    //Default constructor
    public ParticleSystem(){}

    public void Show(){
        //resetShader();

        //Loop through particles
        for (int i = 0; i < particles.size(); ++i) {
            //Get Particle reference
            Particle p = particles.get(i);

            //Render the particle to screen
            p.Show();
        }
    }

    public void Update(){
        //Loop through particles
        for (int i = 0; i < particles.size(); ++i) {
            //Get Particle reference
            Particle p = particles.get(i);

            //Handle Particle position and life cycle
            p.Update();

            //Remove dead particles
            if(p.dead) particles.remove(p);
        }
    }

    public void Emmit(int x0, int y0){
        //Instantiate a new particle
        Particle p = new Particle(2*1000);

        //Set Particle position
        p._posX = x0;
        p._posY = y0;

        //Add Particle to Listing
        particles.add(p);
    }

    public void Emmit(int x0, int y0, String str){
        //Instantiate a new particle
        Particle p = new Particle(2*1000);
        
        //Set Particle Position
        p._posX = x0;
        p._posY = y0;

        //Set Particle Lable
        p._label = str;
        
        //Add Particle to Listing
        particles.add(p);
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

PImage Blur;

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
        fill((sound.onBeat() ? myColor:color(100)));

        //Draw the head position
        ellipse(
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2),           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );

        stroke((sound.onBeat() ? myColor:color(100)));
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
            
        }

        //Render Node Listing
        for(int i = _nodes.size()-2; i >= 0; i--) {
            //Get the current node
            Node n1 = _nodes.get(i);
            Node n2 = _nodes.get(i+1);

            if(abs(n1._posX - n2._posX) > 3) continue;
            if(abs(n1._posY - n2._posY) > 3) continue;

            line(
                n1._posX*TILESIZE + (TILESIZE/2),
                n1._posY*TILESIZE + (TILESIZE/2),
                n2._posX*TILESIZE + (TILESIZE/2),
                n2._posY*TILESIZE + (TILESIZE/2)
            );
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

            } else {
                boolean right = level.GetBlock(_posX+1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);
                boolean left = level.GetBlock(_posX-1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);

                _velX = (right) ? -1 : 1;
                _velY = 0;
            } 
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        _pVelX = _velX;
        _pVelY = _velY;

        //Screen Limits
        PVector levelSize = manager.getLevelSize();
        PVector levelLimit = new PVector(
            (width-levelSize.x)/2,
            (height-levelSize.y)/2
        );

        //Loop head position
        if(_posX*TILESIZE > levelSize.x-1) _posX = 0;
        if(_posY*TILESIZE > levelSize.y-1) _posY = 0;
        if(_posX*TILESIZE < 0) _posX = width/TILESIZE;
        if(_posY*TILESIZE < 0) _posY = (int)levelSize.y;

        //Death Checks
        if(OverlapsSelf()) {
            if(!secondPlayer) State = GameState.GameOver;
            else Reload();
            
            println("Overlap");
        }
    }

    public void DrawBlurr(int x0, int y0, int x1, int y1){
        int x2 = x0 * TILESIZE;
        int y2 = y0 * TILESIZE;

        int x3 = x1 * TILESIZE;
        int y3 = y1 * TILESIZE;

        
        boolean hor = x2 == x3;

        
        int w = abs(x2-x3) + ((hor) ? 0:16);
        int h = abs(y2-y3) + ((hor) ? 16:0);

        int xx = (x2 > x3) ? x3-4:x2-4;
        int yy = (y2 > y3) ? y3-4:y2-4;

        if(hor) xx -= 8;
        else yy -=8;

        tint(100,100,255,255);

        image(
            Blur,
            (x2 > x3) ? x3-4:x2-4,
            (y2 > y3) ? y3-4:y2-4,
            w+48,h+48
        );
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
    
    //Alternate beat detection system
    BeatDetect detector;

    //Audio beat timer
    Timer _beat = new Timer(fromBPM(45.75f/2.0f));

    //Music Timing Managment
    float _bpm = 96.0f;
    float _sub = 2.0f;
    float _lb = 0;

    boolean _MusicSynced;

    public SoundController(){
        //Initialize Minim library
        minim = new Minim(CONTEXT);

        //Initialize Beat Detector
        detector = new BeatDetect();

        //Load the current audio and play
        _player = minim.loadFile("test_song_1.mp3");
        _player.setGain(-80);
        _player.loop();
    }

    //Future Audio Managment
    public void LoadSounds(){}
    
    public void LoadMusic(){
        for(int i = 0; i < manager.Levels.size(); i++){
            //Load song into memory
            Level l = manager.Levels.get(i);
            AudioPlayer player = minim.loadFile(l._song);

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
  public void settings() {  fullScreen(P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakeSnakeRevolution" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
