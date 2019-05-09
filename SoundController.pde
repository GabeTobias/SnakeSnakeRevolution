

class SoundController {
    HashMap <String,AudioPlayer> SongLibrary = new HashMap<String,AudioPlayer>();

    //Active Audio Players
    AudioPlayer _player;
    AudioInput _input;
    
    //Alternate beat detection system
    BeatDetect detector;

    //Audio beat timer
    Timer _beat = new Timer(fromBPM(45.75/2.0));

    //Music Timing Managment
    float _bpm = 96.0;
    float _sub = 2.0;
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
        float sec = bpm / 60.0;                     //Convert from bpm to seconds
        float millis = sec * 1000;                  //Convert from seconds to milliseconds

        return int(millis);
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