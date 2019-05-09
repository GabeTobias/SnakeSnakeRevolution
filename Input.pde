boolean W,A,S,D;
boolean I,J,K,L;

void keyPressed(){
    if(GameMode == 3) GameplayInput();
    else MenuInput();
}

void keyReleased(){
    if(GameMode == 3) GameplayRelease();
}

void GameplayRelease(){
    if(key == 'w')  W = false;
    if(key == 'a')  A = false;
    if(key == 's')  S = false;
    if(key == 'd')  D = false;

    if(key == 'i')  I = false;
    if(key == 'j')  J = false;
    if(key == 'k')  K = false;
    if(key == 'l')  L = false;
}

void GameplayInput(){
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

void MenuInput(){
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