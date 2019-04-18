

class Particle {
    //Position and velocity
    float _posX, _posY;
    float _velX, _velY = -10;

    //Visable properties
    int _padding = 5;
    int _alpha = 255;

    //Flag for particle system to remove it
    boolean dead;
    
    //Displayed text
    String _label = "Default";

    //Death timer
    Timer LifeTime;

    public Particle (float lifespan){
        //Initialize Death Timer
        LifeTime = new Timer(lifespan);
        LifeTime.Reset();
    }

    void Show(){
        fill(100,255,100,_alpha);

        //Draw Background
        rect(
            _posX,
            _posY,
            textWidth(_label) + (_padding * 2),
            textAscent() + (_padding*2)
        );

        fill(100,100,100,_alpha);

        //Render Text
        text(
            _label,
            _posX + _padding,
            _posY + _padding*3
        );

        fill(255);
    }

    void Update(){
        //Handle death system
        if(LifeTime.Triggered()) Die();

        //Handle Movement
        _posX = lerp(_posX, _posX + _velX, 0.2f);
        _posY = lerp(_posY, _posY + _velY, 0.2f);

        //Handle Alpha Fade
        _alpha -=  4f;
    }

    void Die(){ dead = true; }
}



class ParticleSystem {
    //All active particles
    ArrayList<Particle> particles = new ArrayList<Particle>();

    //Default constructor
    public ParticleSystem(){}

    void Show(){
        //resetShader();

        //Loop through particles
        for (int i = 0; i < particles.size(); ++i) {
            //Get Particle reference
            Particle p = particles.get(i);

            //Render the particle to screen
            p.Show();
        }
    }

    void Update(){
        //Loop through particles
        for (int i = 0; i < particles.size(); ++i) {
            //Get Particle reference
            Particle p = particles.get(i);

            //Handle Particle position and life cycle
            p.Update();

            //Remove dead particles
            if(p.dead) particles.remove(p);
        }
    }

    void Emmit(int x0, int y0){
        //Instantiate a new particle
        Particle p = new Particle(2*1000);

        //Set Particle position
        p._posX = x0;
        p._posY = y0;

        //Add Particle to Listing
        particles.add(p);
    }

    void Emmit(int x0, int y0, String str){
        //Instantiate a new particle
        Particle p = new Particle(2*1000);
        
        //Set Particle Position
        p._posX = x0;
        p._posY = y0;

        //Set Particle Lable
        p._label = str;
        
        //Add Particle to Listing
        particles.add(p);
    }
}