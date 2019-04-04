
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