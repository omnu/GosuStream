package com.gosustream.lol.web;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import util.LiveGameInfo;

import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.domain.LiveGameJson;
import com.gosustream.lol.domain.Player;
import com.gosustream.lol.domain.Region;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@RequestMapping("/livegames")
@Controller
@RooWebScaffold(path = "livegames", formBackingObject = LiveGame.class)
public class LiveGameController {

    private static final Logger log = LoggerFactory.getLogger(LiveGameController.class);

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "update/{region}/{alias}")
    public void checkGosuLiveGame(@PathVariable("region") String region,
            @PathVariable("alias") String alias) throws Exception {
        LiveGameInfo liveGameInfo = getLiveGameData(alias, region);
        if (liveGameInfo != null) {
            persistLiveGame(liveGameInfo.getGameId(), liveGameInfo.getObserverKey());
        }
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "add")
    public void addGosuLiveGames() throws Exception {
        List<Player> gosuList = Player.findAllPlayers();
        for (Player gosu : gosuList) {
            String alias = gosu.getUrlEncodedAlias();
            String region = gosu.getRegion().toUpperCase();

            LiveGameInfo liveGameInfo = getLiveGameData(alias, region);
            if (liveGameInfo != null) {
                persistLiveGame(liveGameInfo.getGameId(), liveGameInfo.getObserverKey());
            }
        }
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "removeInactiveGames")
    public void removeInactiveLiveGames() throws Exception {
        removeInactiveGames(LiveGame.findAllLiveGames());
        
    }

    @RequestMapping(value = "json", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<LiveGameJson> getLiveGames() {
        List<LiveGame> liveGameList = LiveGame.findLiveGamesByBroadcast(false).getResultList();
        return LiveGameJson.liveGamesToJson(liveGameList);
    }

    @RequestMapping(value = "getNextLiveGame", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    LiveGameJson getNextLiveGame(@RequestParam(value = "streamId", required = false) String streamId) throws UnirestException {
        LiveGame liveGame = null;
        if (streamId == null) {
            List<LiveGame> liveGames = LiveGame.findLiveGameByBroadcastAndOrderByPriority(false).getResultList();
            if (liveGames.isEmpty()) {
                return null;
            } else {
                liveGame = liveGames.get(0);
            }
        } else {
            List<LiveGame> streamingGames = LiveGame.findLiveGamesByStreamId(streamId).getResultList();
            if (streamingGames.isEmpty()) {
                List<LiveGame> liveGames = LiveGame.findLiveGameByBroadcastAndOrderByPriority(false).getResultList();
                if (liveGames.isEmpty()) {
                    return null;
                } else {
                    liveGame = liveGames.get(0);
                    liveGame.setBroadcast(true);
                    liveGame.setStreamId(streamId);
                    liveGame.merge();
                }
            } else {
                removeInactiveGames(streamingGames);
            }
        }
        if (liveGame == null) {
            return null;
        }
        return LiveGameJson.liveGameToJson(liveGame);
    }

    private void removeInactiveGames(List<LiveGame> liveGames) throws UnirestException {
        for(LiveGame liveGame : liveGames) {
            Set<Player> players = liveGame.getPlayers();
            Player gosu = players.iterator().next();
            String alias = gosu.getUrlEncodedAlias();
            String region = gosu.getRegion().toUpperCase();
            LiveGameInfo liveGameInfo = getLiveGameData(alias, region);
            if (liveGameInfo == null || !liveGameInfo.getGameId().equals(liveGame.getGameId())) {
                // remove finished game
                liveGame.remove();
            }
        }
    }
    
    /**
     * Check if current game already exists inside live games table and persist
     * if not
     * 
     * @param gameId
     *            - Riot game id for live game to persist
     * @param observerKey
     *            - Observer key to spectate the live game
     */
    private void persistLiveGame(String gameId, String observerKey) {
        if (LiveGame.findLiveGamesByGameId(gameId.toString()).getResultList().size() == 0) {
            LiveGame liveGame = new LiveGame();
            liveGame.setGameId(gameId.toString());
            liveGame.setObserverKey(observerKey);
            liveGame.persist();
        }
    }

    private LiveGameInfo getLiveGameData(String alias, String region) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest
                .get("https://community-league-of-legends.p.mashape.com/api/v1.0/" + region
                        + "/summoner/retrieveInProgressSpectatorGameInfo/" + alias)
                .header("X-Mashape-Key", "vZltgavJCxmshMmz2evwZblTuuDep1AA7FijsneomYUZASc9Uu").asJson();
        JSONObject result = response.getBody().getObject();
        try {
            JSONObject playerCredentials = result.getJSONObject("playerCredentials");
            Long gameId = playerCredentials.getLong("gameId");
            String observerKey = playerCredentials.getString("observerEncryptionKey");
            LiveGameInfo gameInfo = new LiveGameInfo();
            gameInfo.setGameId(gameId.toString());
            gameInfo.setObserverKey(observerKey);
            return gameInfo;
        } catch (JSONException e) {
            log.info("Player " + alias + " from region " + region + " is not in an active game!");
            return null;
        }
    }
}
