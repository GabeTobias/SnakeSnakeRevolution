
class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void Show(){
        //Style Blocks
        fill(200);

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

    public void DrawBlockRounded(int x0, int y0){
        boolean tl = false;
        boolean tr = false;
        boolean bl = false;
        boolean br = false;

        tl = Data[x0][y0+1] == 0 || Data[x0-1][y0] == 0;
        tr = Data[x0][y0+1] == 0 || Data[x0+1][y0] == 0;
        bl = Data[x0][y0-1] == 0 || Data[x0-1][y0] == 0;
        br = Data[x0][y0-1] == 0 || Data[x0+1][y0] == 0;

        //Draw the head position
        rect(
            (x0 * TILESIZE),                              //X position scaled by tilesize
            (y0 * TILESIZE),                              //Y position scaled by tilesize
            (TILESIZE),                                   //width
            (TILESIZE)                                    //height
            ,(tl) ? 0:10
            ,(tr) ? 0:10
            ,(br) ? 0:10
            ,(bl) ? 0:10
        );
    }

    public void DrawBlock(int x0, int y0){
        //Draw the head position
        rect(
            (x0 * TILESIZE),                              //X position scaled by tilesize
            (y0 * TILESIZE),                              //Y position scaled by tilesize
            (TILESIZE),                                   //width
            (TILESIZE)                                    //height
        );
    }

    public int GetBlock(int x0, int y0){
        if(x0 < 0 || y0 < 0 || x0 >= _width || y0 >= _height) return 0;

        return Data[x0][y0];
    }
}