class LevelManager {
    ArrayList<Level> Levels = new ArrayList<Level>();       //All leves neeeded to complete the game
    
    int LoadStart;

    int currentLevel;                                       //The current level loaded on the scene
    int foodCountPlayer1;
    int foodCountPlayer2;

    public LevelManager(){
        LoadLevels(SelectLevels());
    }

    String[] SelectLevels(){
        String[] returns = {"Levels/Easy_0.JSON","Levels/Medium_3.JSON","Levels/Medium_1.JSON","Levels/Medium_2.JSON","Levels/Medium_0.JSON","Levels/PobablyHard_0.JSON","Levels/Easy_2.JSON",};
        return returns;
    }

    void LoadLevels(String[] unloadedLevels){
        for (int i = 0; i < unloadedLevels.length; ++i) {
            //Load Level Data
            Level l = new Level(unloadedLevels[i]);
            Levels.add(l);
        }
    }

    void Update(){
        if(State == GameState.Win) return;

        if(foodCountPlayer1 > 3 || foodCountPlayer2 > 3) ChangeLevel();

        if(State == GameState.Loading){
            if(millis() - LoadStart > 3000){
                State = GameState.Playing;
            }
        }
    }

    void ChangeLevel(){
        State = GameState.Loading;
        LoadStart = millis();
        
        thread("LoadLevelData");
    }

    Level getLevel(){
        return Levels.get(currentLevel);
    }

    PVector getLevelSize(){
        return new PVector(
            level._width * level._tileSize,
            level._height * level._tileSize
        );
    }

    void Restart(){
        thread("ResetGame");
    }
}

void LoadLevelData(){
    if(manager.currentLevel >= 2){
        State = GameState.Win;
    }

    manager.currentLevel++;
    level = manager.getLevel();
    
    manager.foodCountPlayer1 = 0;
    manager.foodCountPlayer2 = 0;

    snek.Reload();
    goal.ChangePosition();

    sound.PlayLevelSong(level);
}

void ResetGame(){
    manager.currentLevel = 0;
    level = manager.getLevel();
    
    manager.foodCountPlayer1 = 0;
    manager.foodCountPlayer2 = 0;

    snek.Reload();
    goal.ChangePosition();

    sound.PlayLevelSong(level);
    State = GameState.Playing;
}

/*
    - PreLoad all levels and switch between the,
    - Load every level as the scene changes
    ** Preload only the levels needed to complete the game **
*/
