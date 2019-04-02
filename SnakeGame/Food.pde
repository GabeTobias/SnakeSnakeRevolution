
public class Food {
    int _posX, _posY;

    public Food (int x0, int y0) {
        _posX = x0;
        _posY = y0;    
    }

    boolean isEaten(int xx, int yy){
        return (xx == _posX && yy == _posY);
    }

    void Show(){
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

    void Eat(){
        _posX = (int)random(width/TILESIZE);
        _posY = (int)random(height/TILESIZE);    
    }
}
