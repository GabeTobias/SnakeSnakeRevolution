

void keyPressed(){
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

void onBeatEvent(){
    
    //Delay Gameplay 100 milliseconds
    if(GameTime.Triggered()){
        snek.Move();   
    }

    sound._lb = millis();
}