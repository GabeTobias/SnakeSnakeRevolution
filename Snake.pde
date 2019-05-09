
PImage Blur;

class Snake extends Node{
    int _posX, _posY;
    int _velX = 1, _velY;
    int _pVelX = 1, _pVelY;

    boolean second;

    color myColor;

    ArrayList<Node> _nodes = new ArrayList<Node>();

    public Snake(int x0, int y0){
        //Pass variables to parent with a null next node
        super(x0,y0,0,false);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        //Add default body
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
        AddNode(x0,y0,false);
    }

    public Snake(int x0, int y0, boolean s){
        //Pass variables to parent with a null next node
        super(x0,y0,0,true);

        //Set default position of head
        _posX = x0;
        _posY = y0;

        second = s;

        //Add default body
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);
        AddNode(x0,y0,s);

        second = true;
    }

    void Show(){
        fill((sound.onBeat() ? myColor:color(100)));

        //Draw the head position
        ellipse(
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2),           //Y position scaled by tilesize
            TILESIZE,                   //width
            TILESIZE                    //height
        );

        stroke((sound.onBeat() ? myColor:color(100)));
        strokeWeight(TILESIZE/2);

        boolean b = (abs(_posX - _nodes.get(0)._posX) > 3);
        boolean a = (abs(_posY - _nodes.get(0)._posY) > 3);

        //Connect Head to body
        if(!a && !b){
            line(
                _posX*TILESIZE + (TILESIZE/2),
                _posY*TILESIZE + (TILESIZE/2),
                _nodes.get(0)._posX*TILESIZE + (TILESIZE/2),
                _nodes.get(0)._posY*TILESIZE + (TILESIZE/2)
            );
            
        }

        //Render Node Listing
        for(int i = _nodes.size()-2; i >= 0; i--) {
            //Get the current node
            Node n1 = _nodes.get(i);
            Node n2 = _nodes.get(i+1);

            if(abs(n1._posX - n2._posX) > 3) continue;
            if(abs(n1._posY - n2._posY) > 3) continue;

            line(
                n1._posX*TILESIZE + (TILESIZE/2),
                n1._posY*TILESIZE + (TILESIZE/2),
                n2._posX*TILESIZE + (TILESIZE/2),
                n2._posY*TILESIZE + (TILESIZE/2)
            );
        }

        noStroke();
    }

    void Move() {
        //Move Node Listing
        for(int i = _nodes.size()-1; i >= 0; i--) {
            //Get the current node
            Node n = _nodes.get(i);

            //Update current node data
            n.Move();
        }

        int nextX = _posX + _velX;
        int nextY = _posY + _velY;

        //Handle Level Collision
        if(level.GetBlock(nextX,nextY) != 0){
            //if moving along x axis change to y direction
            if(nextX != _posX){
                boolean up = level.GetBlock(_posX,_posY+1) != 0 && !OverlapsPoint(_posX,_posY+1);
                boolean down = level.GetBlock(_posX,_posY-1) != 0 && !OverlapsPoint(_posX,_posY-1);

                _velX = 0;
                _velY = (up) ? -1 : 1;

            } else {
                boolean right = level.GetBlock(_posX+1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);
                boolean left = level.GetBlock(_posX-1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);

                _velX = (right) ? -1 : 1;
                _velY = 0;
            } 
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        _pVelX = _velX;
        _pVelY = _velY;

        //Loop head position
        if(_posX*TILESIZE > width-1) _posX = 0;
        if(_posY*TILESIZE > height-1) _posY = 0;
        if(_posX*TILESIZE < 0) _posX = width/TILESIZE;
        if(_posY*TILESIZE < 0) _posY = height/TILESIZE;

        //Death Checks
        if(OverlapsSelf()) {
            if(!secondPlayer) State = GameState.GameOver;
            else Reload();
            
            println("Overlap");
        }
    }

    void DrawBlurr(int x0, int y0, int x1, int y1){
        int x2 = x0 * TILESIZE;
        int y2 = y0 * TILESIZE;

        int x3 = x1 * TILESIZE;
        int y3 = y1 * TILESIZE;

        
        boolean hor = x2 == x3;

        
        int w = abs(x2-x3) + ((hor) ? 0:16);
        int h = abs(y2-y3) + ((hor) ? 16:0);

        int xx = (x2 > x3) ? x3-4:x2-4;
        int yy = (y2 > y3) ? y3-4:y2-4;

        if(hor) xx -= 8;
        else yy -=8;

        tint(100,100,255,255);

        image(
            Blur,
            (x2 > x3) ? x3-4:x2-4,
            (y2 > y3) ? y3-4:y2-4,
            w+48,h+48
        );
    }
    
    void AddNode(int x0, int y0, boolean s) {
        //Find the last usable node
        int nxt = (_nodes.size() > 0) ? _nodes.size() : 0;

        //Create a new node
        Node n = new Node(x0,y0, nxt,s);

        //Add nod
        _nodes.add(n);
    }

    void Reload(){
        _nodes.clear();

        //Set default position of head
        _posX = 2;
        _posY = 2;

        //Add default body
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
        AddNode(_posX,_posY,second);
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

    //Detect Overlaps with the head node
    boolean OverlapsSelf(){
        boolean r = false;

        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(_posX == n._posX && _posY == n._posY)
                r = true;
        }

        return r;
    }

    //Detect Overlaps with the head node
    boolean OverlapsPoint(int x0, int y0){
        for(int i = 0; i < _nodes.size(); i++){
            Node n = _nodes.get(i);
            
            if(x0 == n._posX && y0 == n._posY)
                return true;
        }

        return false;
    }

    PVector Last(){
        return new PVector(_nodes.get(_nodes.size()-1)._posX,_nodes.get(_nodes.size()-1)._posY);
    }
}