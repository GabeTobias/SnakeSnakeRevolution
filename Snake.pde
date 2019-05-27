
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
        fill(myColor);
        
        noLights();
        
        pushMatrix();
      
        translate(            
            _posX * TILESIZE + (TILESIZE/2),           //X position scaled by tilesize
            _posY * TILESIZE + (TILESIZE/2)           //Y position scaled by tilesize
        );
      
        //Draw the head position
        sphere(
            TILESIZE/2                   //width
        );
        
        popMatrix();

        stroke(myColor);
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
            
            int x0 = _posX*TILESIZE + (TILESIZE/2);
            int y0 = _posY*TILESIZE + (TILESIZE/2);
                   
            int x1 = _nodes.get(0)._posX*TILESIZE + (TILESIZE/2);
            int y1 = _nodes.get(0)._posY*TILESIZE + (TILESIZE/2);
            
            int sx = (x0 > x1) ? x0-x1 : x1-x0;
            int sy = (y0 > y1) ? y0-y1 : y1-y0;
            
            int xx = (x0 > x1) ? x1 + ((x0-x1)/2) : x0 + ((x1-x0)/2);
            int yy = (y0 > y1) ? y1 + ((y0-y1)/2) : y0 + ((y1-y0)/2);
            
            pushMatrix();

            translate(xx,yy);

            if(sx == 0) box(TILESIZE/16,sy,TILESIZE/16);
            else box(sx,TILESIZE/16,TILESIZE/16);
            
            popMatrix();

        }

        //Render Node Listing
        for(int i = _nodes.size()-2; i >= 0; i--) {
            //Get the current node
            Node n1 = _nodes.get(i);
            Node n2 = _nodes.get(i+1);

            if(abs(n1._posX - n2._posX) > 3) continue;
            if(abs(n1._posY - n2._posY) > 3) continue;
            
            int x0 = n1._posX*TILESIZE + (TILESIZE/2);
            int y0 = n1._posY*TILESIZE + (TILESIZE/2);
                   
            int x1 = n2._posX*TILESIZE + (TILESIZE/2);
            int y1 = n2._posY*TILESIZE + (TILESIZE/2);
            
            int sx = (x0 > x1) ? x0-x1 : x1-x0;
            int sy = (y0 > y1) ? y0-y1 : y1-y0;
            
            int xx = (x0 > x1) ? x1 + ((x0-x1)/2) : x0 + ((x1-x0)/2);
            int yy = (y0 > y1) ? y1 + ((y0-y1)/2) : y0 + ((y1-y0)/2);
            
            pushMatrix();

            translate(xx,yy);

            if(sx == 0) box(TILESIZE/16,sy,TILESIZE/16);
            else box(sx,TILESIZE/16,TILESIZE/16);

            popMatrix();

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
                
                Crash._XMin = 0;
                Crash._XMax = 0;
                Crash._YMin = -10;
                Crash._YMax =  10;
                Crash._ZMin = -10;
                Crash._ZMax =  10;

                sound._ACrash.play(0);
            } else {
                boolean right = level.GetBlock(_posX+1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);
                boolean left = level.GetBlock(_posX-1,_posY) != 0 && !OverlapsPoint(_posX-1,_posY);

                _velX = (right) ? -1 : 1;
                _velY = 0;
                
                Crash._XMin = -10;
                Crash._XMax =  10;
                Crash._YMin = 0;
                Crash._YMax = 0;
                Crash._ZMin = -10;
                Crash._ZMax =  10;
                
                sound._ACrash.play(0);
            } 
            
            if(sound.onBeat())
              for(int i = 0; i < 50; i++) Crash.Emit(_posX*TILESIZE,_posY*TILESIZE,0);
        }

        //Move the snake head
        _posX += _velX;
        _posY += _velY;

        _pVelX = _velX;
        _pVelY = _velY;

        //Screen Limits
        PVector levelSize = manager.getLevelSize();

        //Loop head position Bottom and Right
        if(_posX*TILESIZE > levelSize.x-1) _posX = 0;
        if(_posY*TILESIZE > levelSize.y-1) _posY = 0;

        //Loop head position Top and Left
        if(_posX < 0) _posX = (int)(levelSize.x / TILESIZE)-1;
        if(_posY < 0) _posY = (int)(levelSize.y / TILESIZE)-1;

        //Death Checks
        if(OverlapsSelf()) {
            if(!secondPlayer) {
              //sound._AGameOver.play(0);
              State = GameState.GameOver;
            } else Reload();
            
            println("Overlap");
        }
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
    
    void ResetSnakeSize(){
        _nodes.clear();
      
        //Add default body
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
        AddNode(_posX,_posY,false);
    }

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
