
class Level {

    public int[][] Data;

    int _width, _height;
    int _stroke = 12;
    String _file;

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