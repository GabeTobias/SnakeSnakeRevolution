//Selection Colors
color[] selectOptions = {color(255,224,102),color(103,65,217),color(34,184,207),color(130,201,30),color(253,126,20),color(240,62,62)};

//Snake position
int sx1 = 300,sx2 = 100000;
int sy1 = 100,sy2 = 100;

//Charecter Selections
int snake1,snake2;

//Multiplayer Toggle
boolean secondPlayer;

//Player Readdy
boolean ready1,ready2;

//Ready Timer
Timer rTimer = new Timer(8000);

//Count Down Count
int countDown = -1;

void StartMenu(){}

void CharecterSelect() {
    pushMatrix();
    translate(width/4.5, 0);

    //Render Snakes
    fill(selectOptions[snake1]);
    rect(sx1,sy1, 200,200);                                                             //Player 1

    rect((snake1*100)+125,590,50,10,30);                                                //Cursor 1
    
    fill(selectOptions[snake2]);
    rect(sx2,sy2, 200,200);                                                             //Player 2

    rect((snake2*100)+125 + ((secondPlayer) ? 0:8000),590,50,10, 30);                    //Cursor 2

    snek.myColor = selectOptions[snake1];
    snek2.myColor = selectOptions[snake2];

    //Move Snake Positions
    if(secondPlayer){
        sx1 = (int)lerp(sx1,125,0.1f);
        sx2 = (int)lerp(sx2,470,0.1f);
    }

    //Draw Color options
    for (int i = 0; i < 6; ++i) {
        fill(selectOptions[i]);
        ellipse(
            (i*100)+150,
            550,
            50,50
        );
    }

    //Ready Box 1
    fill((ready1) ? 255:100);
    rect(sx1, 675, 200,50,20);

    //Ready Box 2
    fill((ready2) ? 255:100);
    rect(sx2, 675, 200,50,20);

    popMatrix();

    //Draw Countdown Timer
    if(countDown > -1 && countDown < 6){
        fill(255);
        textSize(100);
        text(countDown,(width/2)-(textWidth(str(countDown))/2),(height/2)-(textAscent()/2));
    }


    if(ready1 && ready2 || ready1 && !secondPlayer){
        countDown = 8 - rTimer.getTime();
        if(rTimer.Triggered()) GameMode++;
    }
}

void Instructions(){}