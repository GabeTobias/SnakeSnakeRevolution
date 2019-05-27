//Selection Colors
color[] selectOptions = {color(45, 226, 230), color(255, 108, 17), color(255, 66, 100), color(35, 244, 192)};

//Snake position
float sx1 = 300, sx2 = 100000;
int sy1 = 100, sy2 = 100;

//Charecter Selections
int snake1, snake2;

//Multiplayer Toggle
boolean secondPlayer;

//Player Readdy
boolean ready1, ready2;

//Ready Timer
Timer rTimer = new Timer(5000);

//Count Down Count
int countDown = -1;

//GUI Images
PImage Title;
PImage Instructions;

void StartMenu() {
  //Render Background Mesh
  MenuBackground();

  //Reset Camera View
  camera();
  hint(DISABLE_DEPTH_TEST);

  //Draw Title Image
  tint(255);
  image(Title, (width/2)-638, (height/2)-384, 1366, 768);
}

void CharecterSelect() {
  
  //Render Background Mesh
  MenuBackground();

  pushMatrix();

    //Offset the screen position
    translate(width/3.5, 200, 200);
  
    //Draw Snake A
    fill(selectOptions[snake1]);
    rect((snake1*100)+225, 590, 50, 10, 30);                                              //Cursor 1
    DrawSnakeA();                                                                         //Player 1
  
    //Draw Snake B
    if (secondPlayer) {
      fill(selectOptions[snake2]);
      rect((snake2*100)+225, 590, 50, 10, 30);                                            //Cursor 2
      DrawSnakeB();                                                                       //Player 2
    }
  
    //Pass Color values to snake Objects
    snek.myColor = selectOptions[snake1];
    snek2.myColor = selectOptions[snake2];
  
    //Move Snake Positions
    if (secondPlayer) {
      sx1 = (int)lerp(sx1, 125, 0.1f);
      sx2 = (int)lerp(sx2, 470, 0.1f);
    }
  
    //Draw Color options
    for (int i = 0; i < 4; ++i) {
      fill(selectOptions[i]);
      ellipse(
        (i*100)+250, 
        550, 
        50, 50
        );
    }
  
    //Ready Box 1
    fill((ready1) ? 255:25);
    rect(sx1, 675, 200, 50, 20);
  
    //Ready Box 2
    fill((ready2) ? 255:25);
    rect(sx2, 675, 200, 50, 20);
  
    textSize(20);
  
    //Ready1  Text
    fill((ready1) ? 25:255);
    text(
      (ready1) ? "Ready":"Selecting", 
      (!ready1) ? sx1+36:sx1+60, 708
      );
  
    //Ready2 Text
    fill((ready2) ? 25:255);
    text(
      (ready2) ? "Ready":"Selecting", 
      (!ready2) ? sx2+36:sx2+60, 708
    );

  popMatrix();

  //Draw Countdown Timer
  if (countDown > -1) {
    fill(255);
    textSize(200);
    text(countDown, textWidth(str(countDown)), textAscent()*1.3);
  }

  //Handle Countdown
  if (ready1 && ready2 || ready1 && !secondPlayer) {
    countDown = 5 - rTimer.getTime();
    if (rTimer.Triggered()) GameMode++;
  }

  //Offset Margin for Arrow Drawing
  pushMatrix();
  translate(0, 0, 200);

  //Draw Arrows
  fill(255);

  //Set Text Properties
  textSize(20);
  fill(255);

  //Draw Directions
  DrawDirections();

  //Render Particles
  particles.Show();               //Render Scene Particles
  particlesBad.Show();

  popMatrix();
}

void Instructions() {
  tint(255, 255);
  image(Instructions, 0, 0, width, height);
}

void MenuBackground(){
  //Draw Background Mesh
  pushMatrix();
  drawMenuBKG();
  popMatrix();
  
  //Draw Background Sunset
  tint(255, 130);
  image(Background, 0, 0);
}

void BackgroundTexture(){}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

void DrawDirections(){
  //Draw Arrows A
  DrawDirectionsA();

  //Offset Arrows to right side
  translate(1270, 0, 0);
  textSize(20);
  fill(255);
  
  //Draw Arrows B
  DrawDirectionsB();
}

void DrawDirectionsA(){
  DrawArrow(250, height-375, 50, 50, 0);
  text("Select", 320, height-340);

  DrawArrow(300, height-225, 50, 50, PI/2);
  text("Move Right", 320, height-265);

  DrawArrow(250, height-250, 50, 50, PI+(PI/2));
  text("Left", 320, height-190);
}

void DrawDirectionsB(){
  //Check for Second Player
  if (secondPlayer) {
    DrawArrow(100, height-375, 50, 50, 0);
    text("Select", 180, height-340);
  
    DrawArrow(150, height-225, 50, 50, PI/2);
    text("Move Right", 180, height-265);
  
    DrawArrow(100, height-250, 50, 50, PI+(PI/2));
    text("Move Left", 180, height-190);
  } else {
    DrawArrow(100, height-375, 50, 50, 0);
    text("Join Game", 180, height-340);
  }
}

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

float t0 = 0;
float t1 = 200;

float sbx1;

void DrawSnakeA() {
  float offset = constrain( (cos(millis()/200.0) ), 0, 0.5);
  sbx1 = lerp(sbx1, constrain(offset*300, 0, 150), 0.2);

  if (sbx1 < 20) {
    t1 = lerp(t1, 0, 0.2);
    t0 = lerp(t0, 150, 0.2);
  }

  if (sbx1 > 130) {
    t1 = lerp(t1, 150, 0.2);
    t0 = lerp(t0, 0, 0.2);
  }

  //fill(255);
  ellipse(sbx1+sx1+25, 100, 50, 50);

  noFill();

  stroke(selectOptions[snake1]);
  strokeWeight(25);
  strokeJoin(ROUND);

  beginShape();

  vertex(sbx1+sx1+25, 100, 0);
  vertex(t1+sx1+25, 200, 0);
  vertex(t0+sx1+25, 200, 0);
  vertex(t1+sx1+25, 200, 0);

  vertex(t0+sx1+25, 200, 0);
  vertex(t0+sx1+25, 300, 0);
  vertex(t1+sx1+25, 300, 0);
  vertex(t0+sx1+25, 300, 0);

  vertex(t1+sx1+25, 300, 0);
  vertex(t1+sx1+25, 400, 0);

  endShape();

  noStroke();
}

void DrawSnakeB() {
  float offset = constrain( (cos(millis()/200.0) ), 0, 0.5);
  sbx1 = lerp(sbx1, constrain(offset*300, 0, 150), 0.2);

  if (sbx1 < 20) {
    t1 = lerp(t1, 0, 0.2);
    t0 = lerp(t0, 150, 0.2);
  }

  if (sbx1 > 130) {
    t1 = lerp(t1, 150, 0.2);
    t0 = lerp(t0, 0, 0.2);
  }

  //fill(255);
  ellipse(sbx1+sx2+25, 100, 50, 50);

  noFill();

  stroke(selectOptions[snake2]);
  strokeWeight(25);
  strokeJoin(ROUND);

  beginShape();

  vertex(sbx1+sx2+25, 100);
  vertex(t1+sx2+25, 200);
  vertex(t0+sx2+25, 200);
  vertex(t1+sx2+25, 200);

  vertex(t0+sx2+25, 200);
  vertex(t0+sx2+25, 300);
  vertex(t1+sx2+25, 300);
  vertex(t0+sx2+25, 300);

  vertex(t1+sx2+25, 300);
  vertex(t1+sx2+25, 400);

  endShape();

  noStroke();
}
