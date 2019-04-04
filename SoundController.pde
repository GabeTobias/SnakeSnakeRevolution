

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
    Timer _beat = new Timer(fromBPM(45.75/2.0));

    //Music Timing Managment
    float _bpm = 96.0;
    float _sub = 2.0;

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
        float sec = bpm / 60.0;                     //Convert from bpm to seconds
        float millis = sec * 1000;                  //Convert from seconds to milliseconds

        return int(millis);
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