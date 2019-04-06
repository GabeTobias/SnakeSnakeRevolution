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

public void setup(){
    //Basic Setup
    
    frameRate(120);

    //Set File loading Context
    CONTEXT = this;

    //Load Sound Controller
    sound = new SoundController();
    sound.LoadMusic();
}

public void draw(){
    //Update the game on timer
    if(GameTime.Triggered()) HandleGame();
}

public void HandleGame(){
    //Reset Background color
    bkg = 200;

    //Update Sound and Beat Detection
    sound.Update();

    //Clear Background
    background(bkg);


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

public void keyPressed(){
    //TODO: Replace with analog arduino inputs
    //Handle Keyboard Inputs
    if(key == 'w') snek.SetVelocity (0,-1);
    if(key == 'a') snek.SetVelocity(-1, 0);
    if(key == 's') snek.SetVelocity( 0, 1);
    if(key == 'd') snek.SetVelocity( 1, 0);
}

public void onBeatEvent(){
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
    Death State
    Sync Snake movment to beat
    Level Loading
        Multiple Songs
        BPM Change
        Default Color shift

*/

public class Food {
    int _posX, _posY;

    public Food (int x0, int y0) {
        _posX = x0;
        _posY = y0;    
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

    public void Eat(){
        _posX = (int)random(width/TILESIZE);
        _posY = (int)random(height/TILESIZE);    
    }
}


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
        //Draw the head position
        rect(
            _posX * TILESIZE,           //X position scaled by tilesize
            _posY * TILESIZE,           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );
    }

    public void Move() {
        for(int i = _nodes.size()-1; i >= 0; i--) {
            //Get the current node
            Node n = _nodes.get(i);

            //Update current node data
            n.Show();
            n.Move();
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        //Loop head position
        if(_posX*TILESIZE > width) _posX = 0;
        if(_posY*TILESIZE > height) _posY = 0;
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

        //Add node
        _nodes.add(n);
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

    public boolean OverlapsSelf(){
        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(_posX == n._posX && _posY == n._posY)
                return true;
        }

        return false;
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

    boolean _MusicSynced;

    public SoundController(){
        //Initialize Minim library
        minim = new Minim(CONTEXT);

        //Initialize Beat Detector
        detector = new BeatDetect();

        //Load the current audio and play
        _player = minim.loadFile("test_song_1.mp3");
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
  public void settings() {  size(800,800); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "SnakeSnakeRevolution" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
