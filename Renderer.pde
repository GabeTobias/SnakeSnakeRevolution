

class Renderer {
    //Static Layer Shaders
    PShader _bkgShader;
    PShader _floorShader;
    PShader _skyShader;

    //Non-static shader
    PShader _objectShader;

    PImage tilemap = new PImage(800/TILESIZE,800/TILESIZE);

    //Default Constructor
    public Renderer(){
        _objectShader = loadShader("obj.frag");
        _floorShader = loadShader("Floor.frag");
        
        //Bind Initial level to image
        BindFloorToImage();
    }

    //Fire before rendering any non-static objects
    void PreRender(){
        //Render the Background
        LoadShader(_bkgShader);
        DrawBackground();

        //Render the Floor
        LoadShader(_floorShader);
        DrawFloor();
    }

    //Fire After all non-static objects
    void PostRender() {
        //Use Default Shader
        resetShader();

        //Render the Sky
        LoadShader(_skyShader);
        DrawSky();
    }

    //Load Shaders
    void LoadShader(PShader shader){}

    //Static Shader Functions
    void DrawBackground(){
        //Clear Background
        background(bkg);
    }
    void DrawSky(){}

    //Floor Functions
    void BindFloorToImage(){
        for (int x0 = 0; x0 < 800/TILESIZE; ++x0) {
            for (int y0 = 0; y0 < 800/TILESIZE; ++y0) {
                if(level.GetBlock(x0,y0) != 0){
                    tilemap.set(x0,y0,color(255,0,0));
                }
            }
        }

        _floorShader.set("_tilemap",tilemap);
        _floorShader.set("_color", new PVector(1,1,1));
    }

    void DrawFloor(){
        shader(_floorShader);
        rect(0,0,800,800);

        image(tilemap,0,0,200,200);
    }

    //Render Object Functions
    void BindMaterial(Material material, int x0, int y0){
        _objectShader.set("_color",material._color);
        _objectShader.set("_opacity",1f);

        _objectShader.set("_shape",material._shape.getValue());

        _objectShader.set("_position", float(x0), float(y0));
    }

    //Renderer for all non-static objects
    void DrawObject(Material material, int x0, int y0){
        //Bind Properties
        BindMaterial(material, x0, y0);

        //Handle Rendering
        shader(_objectShader);
        rect(x0,y0,TILESIZE,TILESIZE*1.2f);
    }
}

class Material {
    Shape _shape;
    PVector _color;

    boolean _recieveLight;
    boolean _shadows;
    boolean _pulsing;

    public Material(){
        _color = new PVector(0.5f,0.5f,1);
        _shape = Shape.Square;
    }
}

enum Shape {
    Circle(0),
    Square(1),
    Cone(2),
    Billboard(3);

    private final int value;
    private Shape(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum ShaderType {
    Default,
    LineShader,
    BackgroundShader
}

/*

RenderObject:
    Shape
    Properties
    Vert Shader
    Frag Shader

Rendering:
    Render the Background:
        LoadShader(BKGShader)
        DrawBackground()

    Render the Floor:
        LoadShader(FloorShader)
        BindFloorToImage()
        BindImageToShader()
        DrawFloor()

    Render the Foreground:
        for(All Render Objects){
            LoadShader(ObjectShader)
            BindMaterial(ObjectMaterial)
            DrawShape(ObjectShape)
        }

    Render the Sky:
        LoadShader(BKGShader)
        DrawSky()
*/