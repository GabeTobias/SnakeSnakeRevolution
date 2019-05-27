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
        pushMatrix();
      
        translate(            
            _posX * TILESIZE,                               //X position scaled by tilesize
            _posY * TILESIZE  - (pulse*jumpScale)           //Y position scaled by tilesize
        );
      
        //Draw the head position
        box(
            TILESIZE,                   //width
            TILESIZE,                    //height
            TILESIZE
        );
        
        popMatrix();
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
