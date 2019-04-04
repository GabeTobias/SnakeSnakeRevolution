

class Snake extends Node{
    int _posX, _posY;
    int _velX = 1, _velY;

    ArrayList<Node> _nodes = new ArrayList<Node>();

    public Snake(int x0, int y0){
        //Pass variables to parent with a null next node
        super(x0,y0,0);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        //Add default body
        AddNode(x0,y0);
        AddNode(x0,y0);
        AddNode(x0,y0);
        AddNode(x0,y0);
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
        for(int i = _nodes.size()-1; i >= 0; i--) {
            //Get the current node
            Node n = _nodes.get(i);

            //Update current node data
            n.Show();
            n.Move();
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        //Loop head position
        if(_posX*TILESIZE > width) _posX = 0;
        if(_posY*TILESIZE > height) _posY = 0;
        if(_posX*TILESIZE < 0) _posX = width/TILESIZE;
        if(_posY*TILESIZE < 0) _posY = height/TILESIZE;
    }
    
    void AddNode(int x0, int y0) {
        //Find the last usable node
        int nxt = (_nodes.size() > 0) ? _nodes.size() : 0;

        //Create a new node
        Node n = new Node(x0,y0, nxt);

        //Add node
        _nodes.add(n);
    }

    ///////////////////////////////////////////////////////////////
    //////////////////////GETTERS & SETTERS////////////////////////
    ///////////////////////////////////////////////////////////////

    //Set the directio of the head node
    void SetVelocity(int x0, int y0){
        _velX = x0;
        _velY = y0;
    }

    //Get the current x position of a listed node
    int GetNodeX(int index){
        if(index > 0){
            return _nodes.get(index-1)._posX;
        } else {
            return _posX;
        }
    }

    //Get the current y position of a listed node
    int GetNodeY(int index){
        if(index > 0){
            return _nodes.get(index-1)._posY;
        } else {
            return _posY;
        }
    }
}