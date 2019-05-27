int cols, rows;
int scl = 30;
int menuScl = 30;
int w = 2000;
int h = 1600;

float flying = 0;
float localPulse;

float[][] terrain;

void setupBKG() {
  cols = (w / scl)+50;
  rows = (h/ scl)+20;
  terrain = new float[cols][rows];
}


void drawBKG() {

  flying -= 0.01;

  float yoff = flying;
  for (int y = 0; y < rows; y++) {
    float xoff = 0;
    for (int x = 0; x < cols; x++) {
      terrain[x][y] = map(noise(xoff, yoff), 0, 1, -100, 100);
      xoff += 0.2;
    }
    yoff += 0.2;
  }

  stroke(255,100);
  strokeWeight(1);
  fill(0,0,0,100);

  localPulse = lerp(localPulse,constrain(pulse,0f,1f),0.1f);

  
  for (int y = 0; y < rows-50; y++) {
    beginShape(TRIANGLE_STRIP);
    for (int x = 0; x < cols-71; x++) {
      vertex(x*scl*1.5, y*scl*1.5, (terrain[x][y]/2) - 70);
      vertex(x*scl*1.5, (y+1)*scl*1.5, (terrain[x][y+1]/2) - 70);
    }
    endShape();
  }
  
  noStroke();
}

void drawMenuBKG() {

  flying -= 0.005;

  float yoff = flying;
  for (int y = 0; y < rows; y++) {
    float xoff = 0;
    for (int x = 0; x < cols; x++) {
      terrain[x][y] = map(noise(xoff/5, yoff/5), 0, 1, -100, 100);
      xoff += 0.2;
    }
    yoff += 0.2;
  }

  stroke(255);
  strokeWeight(1);
  fill(0,0,0,0);

  translate(width/2, (height/2));
  rotateX(PI/3);
  translate(-w/2, -h/2);  

  for (int y = 0; y < rows-2; y++) {
    beginShape(TRIANGLE_STRIP);
    for (int x = 0; x < cols; x++) {
      vertex((x*menuScl + (menuScl*2))-26*menuScl, y*menuScl, (terrain[x][y]*5) - 100);
      vertex((x*menuScl + (menuScl*2))-26*menuScl, (y+1)*menuScl, (terrain[x][y+1]*5) - 100);
    }
    endShape();
  }
  
  noStroke();
}
