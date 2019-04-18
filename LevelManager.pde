class LevelManager {
    ArrayList<Level> Levels = new ArrayList<Level>();       //All leves neeeded to complete the game
    
    int LoadStart;

    int currentLevel;                                       //The current level loaded on the scene
    int foodCount;

    public LevelManager(){
        LoadLevels(SelectLevels());
    }

    String[] SelectLevels(){
        String[] returns = {"Levels/Level_1.JSON","Levels/Level_0.JSON","Levels/Level_2.JSON"};
        return returns;
    }

    void LoadLevels(String[] unloadedLevels){
        for (int i = 0; i < unloadedLevels.length; ++i) {
            Level l = new Level(unloadedLevels[i]);
            Levels.add(l);
        }
    }

    void Update(){
        if(foodCount > 3) ChangeLevel();

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
    manager.currentLevel++;
    level = manager.getLevel();
    manager.foodCount = 0;

    snek.Reload();
    goal.ChangePosition();
}

/*
    - PreLoad all levels and switch between the,
    - Load every level as the scene changes
    ** Preload only the levels needed to complete the game **
*/