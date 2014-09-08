package util;

import java.util.List;

import com.gosustream.lol.domain.Player;

public class LiveGameInfo {

    private String gameId;

    private String observerKey;

    private String gameType;

    private List<Player> teamOne;

    private List<Player> teamTwo;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getObserverKey() {
        return observerKey;
    }

    public void setObserverKey(String observerKey) {
        this.observerKey = observerKey;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public List<Player> getTeamOne() {
        return teamOne;
    }

    public void setTeamOne(List<Player> teamOne) {
        this.teamOne = teamOne;
    }

    public List<Player> getTeamTwo() {
        return teamTwo;
    }

    public void setTeamTwo(List<Player> teamTwo) {
        this.teamTwo = teamTwo;
    }
}
