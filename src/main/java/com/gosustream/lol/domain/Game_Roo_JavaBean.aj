// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.Game;
import java.util.Date;

privileged aspect Game_Roo_JavaBean {
    
    public String Game.getGameId() {
        return this.gameId;
    }
    
    public void Game.setGameId(String gameId) {
        this.gameId = gameId;
    }
    
    public Date Game.getStartTime() {
        return this.startTime;
    }
    
    public void Game.setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    public Date Game.getEndTime() {
        return this.endTime;
    }
    
    public void Game.setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    
    public int Game.getPriority() {
        return this.priority;
    }
    
    public void Game.setPriority(int priority) {
        this.priority = priority;
    }
    
}
