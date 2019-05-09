class Node {
    int _posX, _posY;
    int _nxt;

    boolean second;

    public Node(int x0, int y0, int next, boolean s){
        //Set initial position
        _posX = x0;
        _posY = y0;
    
        //Set the index of the next node in the list
        _nxt = next;

        //Set attached Snake
        second = s;
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
        if(!second){
            _posX = snek.GetNodeX(_nxt);
            _posY = snek.GetNodeY(_nxt);
        } else {
            _posX = snek2.GetNodeX(_nxt);
            _posY = snek2.GetNodeY(_nxt);
        }
    }
}