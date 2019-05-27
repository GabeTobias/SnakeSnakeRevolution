
class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
    int _tileSize;

    boolean onBeat;
    boolean hasFlipped;

    String _file,_song;

    public Level(int w, int h){
        //Initialize the array
        Data = new int[w][h];

        //Set Level size
        _width = w;
        _height = h;
    }

    public Level(String name){
        //Load file name
        _file = name;

        //Load level to data
        LoadLevel(name);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    void LoadLevel(String name){
        //Load JSON Object from file
        JSONObject file = loadJSONObject(name);

        //Load file Dimensions
        _width = file.getInt("width");
        _height = file.getInt("height");

        _song = file.getString("Song");

        //Reset Data 
        Data = new int[_width][_height];

        //Loop through all spaces in level
        for (int x0 = 0; x0 < _width; ++x0) {
            for (int y0 = 0; y0 < _height; ++y0) {
                //Create name for data
                String id = str(x0) + "," + str(y0);

                //Load data into object
                Data[x0][y0] = file.getInt(id);
            }
        }

        if(width > height){
            _tileSize = height / _height;
        } else {
            _tileSize = width / _width;
        }

        println(_tileSize);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void Show(){
        lights();
      
        if(sound.onBeat() && !hasFlipped){
          onBeat = !onBeat;
          hasFlipped = true;
        }
        
        if(!sound.onBeat()) hasFlipped = false;
      
        //Style Blocks
        fill(onBeat ? color(67,18,75):color(37,1,45));

        //Loop through all spaces in level
        for (int x0 = 0; x0 < _width; ++x0) {
            for (int y0 = 0; y0 < _height; ++y0) {
                
                if(GetBlock(x0,y0) != 0)
                    DrawBlock(x0,y0);                   //Render each space
            }
        }

        //Reset block style
        fill(255);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void DrawBlock(int x0, int y0){
        noStroke();  
      
        pushMatrix();
      
        translate(            
            (x0 * TILESIZE)+30,                              //X position scaled by tilesize
            (y0 * TILESIZE)+30,                              //Y position scaled by tilesize
            -(pulse*jumpScale)
        );
      
        //Draw the head position
        box(
            TILESIZE,                   //width
            TILESIZE,                   //height
            TILESIZE/3
        );
        
        popMatrix();
    }

    public int GetBlock(int x0, int y0){
        if(x0 < 0 || y0 < 0 || x0 >= _width || y0 >= _height) return 0;

        return Data[x0][y0];
    }
}
