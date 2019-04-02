

class Node {
    int _posX, _posY;
    int _nxt;

    public Node(int x0, int y0, int next){
        //Set initial position
        _posX = x0;
        _posY = y0;
    
        //Set the index of the next node in the list
        _nxt = next;
    }

    void Show(){
        //Draw the head position
        rect(
            _posX * TILESIZE,           //X position scaled by tilesize
            _posY * TILESIZE,           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );
    }

    void Move() {
        _posX = snek.GetNodeX(_nxt);
        _posY = snek.GetNodeY(_nxt);
    }
}