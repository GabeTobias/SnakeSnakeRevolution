
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

    boolean isEaten(int xx, int yy){
        return (xx == _posX && yy == _posY);
    }

    void Show(){
        fill(255,100,100);

        //Draw the head position
        ellipse(
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2),           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );
        
        fill(255);
    }

    void ChangePosition(){
        _posX = (int)random(width/TILESIZE);
        _posY = (int)random(height/TILESIZE);    

        while(level.GetBlock(_posX,_posY) != 0){
            _posX = (int)random(width/TILESIZE);
            _posY = (int)random(height/TILESIZE);   
        }
    }

    void Eat(boolean second){
        ChangePosition();
        if(second)manager.foodCountPlayer2++;
        else manager.foodCountPlayer2++;
    }
}
