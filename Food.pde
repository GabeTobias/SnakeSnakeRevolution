
public class Food {
    int _posX, _posY;

    Material _material;

    public Food (int x0, int y0) {
        //Give a random position
        ChangePosition();

        //Initialize Render Object
        _material = new Material();
        _material._color = new PVector(1,0.5f,0.5f);
    }

    boolean isEaten(int xx, int yy){
        return (xx == _posX && yy == _posY);
    }

    void Show(){
        fill((sound.onBeat() ? color(255,100,100):color(100)));
        noLights();
        
        pushMatrix();
      
        translate(            
            _posX * TILESIZE + (TILESIZE/2),                               //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2) - (pulse*jumpScale),           //Y position scaled by tilesize
            -(pulse*jumpScale)
        );
      
        //Draw the head position
        sphere(
            TILESIZE/4
        );
        
        popMatrix();
        
        fill(255);
    }

    void ChangePosition(){
        _posX = (int)random(level._width);
        _posY = (int)random(level._height);    

        while(level.GetBlock(_posX,_posY) != 0 || OverlapsSnake(_posX,_posY)){
            _posX = (int)random(level._width);
            _posY = (int)random(level._height);    
        }
    }

    boolean OverlapsSnake(int xx, int yy){
        if(snek.OverlapsPoint(xx,yy)) return true;
        
        if(secondPlayer && snek2.OverlapsPoint(xx,yy)) return true;

        return false;
    }

    void Eat(boolean second){
        //Handle Visuals
        FoodParticles();
        ChangePosition();
        
        //Increment Player Variable
        if(second)manager.foodCountPlayer2++;
        else manager.foodCountPlayer1++;
    }
    
    void FoodParticles() {
      //Emit 20 Food Particles
      for(int i = 0; i < 20; i++) Chomp.Emit(_posX*TILESIZE,_posY*TILESIZE,0);
    }
}
