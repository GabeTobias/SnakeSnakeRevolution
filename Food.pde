
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
        ChangePosition();
        if(second)manager.foodCountPlayer2++;
        else manager.foodCountPlayer2++;
    }
}
