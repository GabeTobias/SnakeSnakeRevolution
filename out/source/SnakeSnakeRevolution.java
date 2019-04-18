import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.*; 
import ddf.minim.analysis.*; 

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
final int TILESIZE = 40;

//GLOBALS
Snake snek = new Snake(5,5);
Food goal = new Food(15,15);
Timer GameTime = new Timer(100);
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

public void setup(){
    //Basic Setup
    
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
}

public void draw(){
    sound.Update();             //Update Sound and Beat Detection

    Render();                   //Render scene object to screen              
    HandleGameplay();           //Update scene object
}

public void Render(){
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

    particles.Show();               //Render Scene Particles
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
        snek.AddNode(PApplet.parseInt(snek.Last().x), PApplet.parseInt(snek.Last().y));
        goal.Eat();
    }

    //Update all scene particles
    particles.Update();
}

public void keyPressed(){
    if(State == GameState.GameOver) return;
    
    if(!sound.onBeat()) particles.Emmit(50,200,"Wrong");

    //TODO: Replace with analog arduino inputs
    //Handle Keyboard Inputs
    if(key == 'w'){
        if(snek._velY == 0) snek.SetVelocity (0,-1);
        else println("Wrong");
    }
    if(key == 'a'){
        if(snek._velX == 0) snek.SetVelocity(-1, 0);
        else println("Wrong");
    }
    if(key == 's') {
        if(snek._velY == 0) snek.SetVelocity( 0, 1);
        else println("Wrong");
    }
    if(key == 'd') {
        if(snek._velX == 0) snek.SetVelocity( 1, 0);
        else println("Wrong");
    }

    if(key == 'k') {
         State = GameState.GameOver;
    }

    if(key == 'o') {
        manager.ChangeLevel();
    }
}

public void onBeatEvent(){
    
    //Delay Gameplay 100 milliseconds
    if(GameTime.Triggered()){
        snek.Move();   
    }

    sound._lb = millis();
}

enum GameState {
    MainMenu,
    Playing,
    Paused,
    Loading,
    GameOver
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
        rect(
            _posX * TILESIZE,           //X position scaled by tilesize
            _posY * TILESIZE,           //Y position scaled by tilesize
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

    public void Eat(){
        ChangePosition();
        manager.foodCount++;
    }
}

class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
    String _file;

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
            (y0 * TILESIZE),                              //Y position scaled by tilesize
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
    int foodCount;

    public LevelManager(){
        LoadLevels(SelectLevels());
    }

    public String[] SelectLevels(){
        String[] returns = {"Levels/Level_1.JSON","Levels/Level_0.JSON","Levels/Level_2.JSON"};
        return returns;
    }

    public void LoadLevels(String[] unloadedLevels){
        for (int i = 0; i < unloadedLevels.length; ++i) {
            Level l = new Level(unloadedLevels[i]);
            Levels.add(l);
        }
    }

    public void Update(){
        if(foodCount > 3) ChangeLevel();

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
}

public void LoadLevelData(){
    manager.currentLevel++;
    level = manager.getLevel();
    manager.foodCount = 0;

    snek.Reload();
    goal.ChangePosition();
}

/*
    - PreLoad all levels and switch between the,
    - Load every level as the scene changes
    ** Preload only the levels needed to complete the game **
*/


class Node {
    int _posX, _posY;
    int _nxt;

    public Node(int x0, int y0, int next){
        //Set initial position
        _posX = x0;
        _posY = y0;
    
        //Set the index of the next node in the list
        _nxt = next;
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
        _posX = snek.GetNodeX(_nxt);
        _posY = snek.GetNodeY(_nxt);
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


class Snake extends Node{
    int _posX, _posY;
    int _velX = 1, _velY;

    ArrayList<Node> _nodes = new ArrayList<Node>();

    public Snake(int x0, int y0){
        //Pass variables to parent with a null next node
        super(x0,y0,0);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        //Add default body
        AddNode(x0,y0);
        AddNode(x0,y0);
        AddNode(x0,y0);
        AddNode(x0,y0);

    }

    public void Show(){
        fill((sound.onBeat() ? color(100):color(100,100,255)));

        //Draw the head position
        ellipse(
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2),           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );

        stroke((sound.onBeat() ? color(100):color(100,100,255)));
        strokeWeight(20);

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
                boolean up = level.GetBlock(_posX,_posY+1) != 0;
                boolean down = level.GetBlock(_posX,_posY-1) != 0;

                _velX = 0;
                _velY = (up) ? -1 : 1;

            } else {
                boolean right = level.GetBlock(_posX+1,_posY) != 0;
                boolean left = level.GetBlock(_posX-1,_posY) != 0;

                _velX = (right) ? -1 : 1;
                _velY = 0;
            }
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        //Loop head position
        if(_posX*TILESIZE > width-1) _posX = 0;
        if(_posY*TILESIZE > height-1) _posY = 0;
        if(_posX*TILESIZE < 0) _posX = width/TILESIZE;
        if(_posY*TILESIZE < 0) _posY = height/TILESIZE;

        //Death Checks
        if(OverlapsSelf()) State = GameState.GameOver;
    }
    
    public void AddNode(int x0, int y0) {
        //Find the last usable node
        int nxt = (_nodes.size() > 0) ? _nodes.size() : 0;

        //Create a new node
        Node n = new Node(x0,y0, nxt);

        //Add nod
        _nodes.add(n);
    }

    public void Reload(){
        _nodes.clear();

        //Set default position of head
        _posX = 2;
        _posY = 2;

        //Add default body
        AddNode(_posX,_posY);
        AddNode(_posX,_posY);
        AddNode(_posX,_posY);
        AddNode(_posX,_posY);
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
        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(_posX == n._posX && _posY == n._posY)
                return true;
        }

        return false;
    }

    public PVector Last(){
        return new PVector(_nodes.get(_nodes.size()-1)._posX,_nodes.get(_nodes.size()-1)._posY);
    }
}


class SoundController {
    //Audio file listing
    String[] Music;
    String[] Sounds;
    
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
        _player.play();
    }

    //Future Audio Managment
    public void LoadSounds(){}
    public void LoadMusic(){}


    public void Update(){
        boolean checkBeat = onBeat();              //Check if frame is on beat
        if(checkBeat) onBeatEvent();               //Change background to beat
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

    //TODO: Detect audio desync
    public void Calibrate(){
        if(detectRythm() && !onBeat()) {            //Detect Audio Desync
            _beat.Reset();                          //Reset the Audio
        }
    }

    ///////////////////////////////////////////////////////////////
    /////////////////////////Beat Detection////////////////////////
    ///////////////////////////////////////////////////////////////

    public boolean onBeat(){
        int beatTiming = fromBPM(_bpm/_sub);        //Current time between beats
        int time = _player.position();              //Time since audio started
        int offset = time % beatTiming;             //Time till the next beat
        
        return (offset <= 250);
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
  public void settings() {  size(800,800, P2D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakeSnakeRevolution" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
