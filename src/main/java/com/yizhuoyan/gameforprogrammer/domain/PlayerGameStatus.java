package com.yizhuoyan.gameforprogrammer.domain;

import java.util.Objects;

import com.yizhuoyan.gameforprogrammer.util.AlgorithmUtil;

/**
 * Created by ben on 10/27/18.
 */
public class PlayerGameStatus implements AlgorithmUtil{
    public static final int MAX_LEVEL=12;
    int currentLevel;
    String currentLevelKey;


    public PlayerGameStatus() {
        this.currentLevelKey=uuid32();
        this.currentLevel = 1;
    }

    public boolean passCurrentLevel(String key){
        if(Objects.equals(key,currentLevelKey)){
            this.currentLevel++;
            this.currentLevelKey= uuid32();
            return true;
        }
        return false;
    }

    public String newCurrentLevelKey(){
        this.currentLevelKey= uuid32();
        return this.currentLevelKey;
    }


    public int getCurrentLevel() {
        return currentLevel;
    }

    public String getCurrentLevelKey() {
        return currentLevelKey;
    }

    public String getCurrentLevelViewName(){
        if(this.currentLevel>=10){
            return this.currentLevel+"";
        }
        return "0"+this.currentLevel;
    }
}
