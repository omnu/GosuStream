package com.gosustream.lol.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class LiveGameJson implements Serializable {
    private static final long serialVersionUID = 3283846466600718874L;
    
    public String gameId;
    public String observerKey;
    public Date startTime;
    public int priority;
    public Boolean broadcast;

    public static LiveGameJson liveGameToJson(LiveGame liveGame) {
        LiveGameJson liveGameJson = new LiveGameJson();
        
        liveGameJson.gameId = liveGame.getGameId();
        liveGameJson.observerKey = liveGame.getObserverKey();
        liveGameJson.startTime = liveGame.getStartTime();
        liveGameJson.priority = liveGame.getPriority();
        liveGameJson.broadcast = liveGame.getBroadcast();
        
        return liveGameJson;
        
    }
    
    public static Collection<LiveGameJson> liveGamesToJson(Collection<LiveGame> liveGames) {
        List<LiveGameJson> liveGamesJson = new ArrayList<LiveGameJson>();
        
        for(LiveGame liveGame : liveGames) {
            liveGamesJson.add(liveGameToJson(liveGame));
        }
        
        return liveGamesJson;
    }
}
