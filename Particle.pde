class ParticleSystem{
  //Is the particle 3D
  boolean TD;
  
  //Position of Emmiter & Start position of particle
  int _posX,_posY,_posZ;
  float _rotZ;
  
  //Min and Max Velocity Range
  int _XMax,_YMax = -10,_ZMax;
  int _XMin,_YMin = -10,_ZMin;
  
  //Size of Particles
  int _width = 50,_height = 50,_depth = 50;
  
  //Rang of Possible Colors
  color _Start = color(255),_Stop = color(255);
  
  PImage myImage;
  
  //Lifetime of a particle
  int LifeTime = 2;
  
  ArrayList<Particle> particles = new ArrayList<Particle>();
  
  public ParticleSystem(int x0, int y0){
    _posX = x0;
    _posY = y0;
  }
  
  public ParticleSystem(int x0, int y0, int z0){
    _posX = x0;
    _posY = y0;
    _posZ = z0;
    
    TD = true;
  }
  
  public void Show(){
    for(int i = 0; i < particles.size(); i++){
      Particle p = particles.get(i);
      

      p.Show();
      
      
      p.Update();
      
      if(p.dead) {
        particles.remove(p);
      }
    }
  }
  
  public void Update(){
    for(int i = 0; i < particles.size(); i++){
      Particle p = particles.get(i);

    }
  }
  
  public void Emit(){ 
    Particle p = getParticle();
    particles.add(p); 
    
    p.myImage = myImage;
  }
  
  public void Emit(int x0, int y0, int z0){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    p._posZ = z0;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public void Emit(int x0, int y0){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public void Emit(int x0, int y0, float rot){ 
    Particle p = getParticle();
    
    p._posX = x0;
    p._posY = y0;
    p._rotZ = rot;
    
    p.myImage = myImage;
    
    particles.add(p); 
  }
  
  public Particle getParticle(){
    if(TD){
      PVector v = getVel();
      color c = getColor();
      
      return new Particle(
        _posX,_posY,_posZ,
        (int)v.x,(int)v.y,(int)v.z,
        _width,_height,_depth,
        c,LifeTime*60
      );
    } else {
      PVector v = getVel();
      color c = getColor();
      
      return new Particle(
        _posX,_posY,
        (int)v.x,(int)v.y,
        _width,_height,
        c,LifeTime*60
      );
    }
  }
  
  PVector getVel(){
    return new PVector(
      random(_XMin,_XMax),
      random(_YMin,_YMax),
      random(_ZMin,_ZMax)
    );
  }
  
  color getColor(){
    return lerpColor(_Start,_Stop,random(1));
  }
  
}

class Particle{
  //Position of particle
  int _posX,_posY,_posZ;
  
  //Velocity of particle
  int _velX,_velY,_velZ;
  
  //Size of Particles
  int _width = 50,_height = 50,_depth = 50;
  
  color _color;
  
  PImage myImage;
  
  int life;
  int limit;
  
  float _rotZ;
  
  boolean dead;
  boolean TD;
  
  public Particle(int x0, int y0, int vx, int vy, int w, int h, color c, int lifetime){
    _color = c;
    
    _width = w;
    _height = h;
    
    _velX = vx;
    _velY = vy;
    
    _posX = x0;
    _posY = y0;
    
    limit = lifetime;
    TD = false;
  }
  
  public Particle(int x0, int y0, int z0, int vx, int vy, int vz, int w, int h, int d, color c, int lifetime){
    _color = c;
    
    _width = w;
    _height = h;
    _depth = d;
    
    _velX = vx;
    _velY = vy;
    _velZ = vz;
    
    _posX = x0;
    _posY = y0;
    _posZ = z0;
    
    limit = lifetime;
    TD = true;
  }
  
  public void Update(){
    _posX += _velX;
    _posY += _velY;
    _posZ += _velZ;
    
    life++;
        
    //For Later Cleanup
    if(life > limit) dead = true;
  }
  
  public void Show(){
    float s = 1-(float(life)/float(limit));
    fill(_color,s*255);
    
    if(TD){
      pushMatrix();
      
      translate(_posX,_posY,_posZ);
      box(_width,_height,_depth);
      
      popMatrix();
    } else {
      if(myImage == null)rect(_posX,_posY,_width,_height);
      else {
        pushMatrix();
        
        tint(255,s*255);
      
        translate(_posX,_posY);
        rotateZ(_rotZ);
      
        image(myImage,0,0,_width,_height);
      
        popMatrix();
      }
    }
  }
}
