boolean W,A,S,D;
boolean I,J,K,L;
int InputFrame;

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

void MenuInput(){
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
