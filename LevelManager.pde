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
        String[] returns = {"Levels/Level_1.JSON","Levels/Level_0.JSON","Levels/Level_2.JSON"};
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

/*
    - PreLoad all levels and switch between the,
    - Load every level as the scene changes
    ** Preload only the levels needed to complete the game **
*/