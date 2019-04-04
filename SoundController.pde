

class SoundController {

    String[] Music;
    String[] Sounds;
    
    AudioPlayer _player;
    AudioInput _input;
    
    BeatDetect detector;

    Timer _beat = new Timer(fromBPM(45.75/2.0));
    int _bpm;

    boolean _MusicSynced;

    public SoundController(){
        //Initialize Minim library
        minim = new Minim(FFD);

        //Initialize Beat Detector
        detector = new BeatDetect();

        _player = minim.loadFile("test_song_1.mp3");
        _player.play();
    }

    public void LoadSounds(){}
    public void LoadMusic(){}


    public void Update(){
        if(onBeat()){
            bkg = 51;
        } else {
            bkg = 200;
        }
    }


    public void SetBPM(int bpm){
        _beat = new Timer(fromBPM(bpm));
        _beat.setLatency(100);
        _bpm = bpm;
    }

    public int fromBPM(float bpm){
        float sec = bpm / 60.0;
        float millis = sec * 1000;

        return int(millis);
    }

    //TODO: Detect audio desync
    public void Calibrate(){
        if(detectRythm() && !onBeat()){
            _beat.Reset();
        }
    }

    ///////////////////////////////////////////////////////////////
    /////////////////////////Beat Detection////////////////////////
    ///////////////////////////////////////////////////////////////

    public boolean onBeat(){
        int offset = _player.position()%fromBPM(96.0/4.0);
        return (offset <= 150);
    }

    ///////////////////////////Depricated///////////////////////////

    //Detect Down Beat
    public boolean detectTiming() {
        if(!_MusicSynced && _beat.Triggered()){
            _player.cue(0);
            _MusicSynced = true;
        }

        return _beat.Triggered();
    }

    public boolean detectRythm(){
        detector.detect(_player.mix);
        return detector.isOnset();
    }
}