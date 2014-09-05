package com.gosustream.lol.web;

import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.domain.LiveGameJson;
import com.gosustream.lol.domain.Player;
import com.gosustream.lol.domain.Region;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@RequestMapping("/livegames")
@Controller
@RooWebScaffold(path = "livegames", formBackingObject = LiveGame.class)
public class LiveGameController {

    private static final Logger log = LoggerFactory.getLogger(LiveGameController.class);

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "update/{region}/{alias}")
    public void checkGosuLiveGame(@PathVariable("region") String region,
            @PathVariable("alias") String alias) throws Exception {
        if (Region.valueOf(region) == null) {
            // Invalid region
            throw new Exception();
        }

        // These code snippets use an open-source library.
        // http://unirest.io/java
        HttpResponse<JsonNode> response = Unirest
                .get("https://community-league-of-legends.p.mashape.com/api/v1.0/" + region
                        + "/summoner/retrieveInProgressSpectatorGameInfo/" + alias)
                .header("X-Mashape-Key", "vZltgavJCxmshMmz2evwZblTuuDep1AA7FijsneomYUZASc9Uu").asJson();
        JSONObject result = response.getBody().getObject();
        JSONObject playerCredentials = result.getJSONObject("playerCredentials");
        Long gameId = playerCredentials.getLong("gameId");
        String observerKey = playerCredentials.getString("observerEncryptionKey");

        persistLiveGame(gameId.toString(), observerKey);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "update")
    public void checkGosuLiveGame() throws Exception {
        List<Player> gosuList = Player.findAllPlayers();
        for (Player gosu : gosuList) {
            String alias = gosu.getUrlEncodedAlias();
            String region = gosu.getRegion().toUpperCase();
            HttpResponse<JsonNode> response = Unirest
                    .get("https://community-league-of-legends.p.mashape.com/api/v1.0/" + region
                            + "/summoner/retrieveInProgressSpectatorGameInfo/" + alias)
                    .header("X-Mashape-Key", "vZltgavJCxmshMmz2evwZblTuuDep1AA7FijsneomYUZASc9Uu").asJson();
            JSONObject result = response.getBody().getObject();
            try {
                JSONObject playerCredentials = result.getJSONObject("playerCredentials");
                Long gameId = playerCredentials.getLong("gameId");
                String observerKey = playerCredentials.getString("observerEncryptionKey");
                persistLiveGame(gameId.toString(), observerKey);
            } catch (JSONException e) {
                log.info("Player " + gosu.getAlias() + " from region " + gosu.getRegion() + " is not in an active game!");
            }
        }
        // These code snippets use an open-source library.
        // http://unirest.io/java

    }

    @RequestMapping(value = "json", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<LiveGameJson> getLiveGames() {
        List<LiveGame> liveGameList = LiveGame.findLiveGamesByBroadcast(false).getResultList();
        return LiveGameJson.liveGamesToJson(liveGameList);
    }

    @RequestMapping(value = "getNextLiveGame", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody LiveGameJson getNextLiveGame() {
        LiveGame liveGame = LiveGame.findLiveGameOrderByPriority().getSingleResult();
        liveGame.setBroadcast(true);
        liveGame.merge();
        return LiveGameJson.liveGameToJson(liveGame);
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
}
