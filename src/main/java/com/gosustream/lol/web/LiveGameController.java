package com.gosustream.lol.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

import com.gosustream.lol.domain.Constants;
import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.domain.LiveGameJson;
import com.gosustream.lol.domain.Player;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@RequestMapping("/livegames")
@Controller
@RooWebScaffold(path = "livegames", formBackingObject = LiveGame.class)
public class LiveGameController {

    private static final Logger log = LoggerFactory.getLogger(LiveGameController.class);

    @RequestMapping(value = "add/{region}/{alias}", produces = "application/json")
    public @ResponseBody
    LiveGame checkGosuLiveGame(@PathVariable("region") String region,
            @PathVariable("alias") String alias) throws Exception {
        LiveGameInfo liveGameInfo = getLiveGameData(alias, region);
        LiveGame result = new LiveGame();
        if (liveGameInfo != null) {
            result = persistLiveGame(liveGameInfo, region.toLowerCase());
        }
        return result;
    }

    @RequestMapping(value = "add", produces = "application/json")
    public @ResponseBody
    List<LiveGame> addGosuLiveGames(@RequestParam(value = "region", required = false) String region)
            throws Exception {
        List<LiveGame> result = new ArrayList<LiveGame>();
        List<Player> gosuList;
        if (region == null) {
            gosuList = Player.findPlayersByIsGosu(true).getResultList();
        } else {
            gosuList = Player.findPlayersByIsGosuAndRegion(true, region).getResultList();
        }
        for (Player gosu : gosuList) {
            String alias = gosu.getUrlEncodedAlias();
            String gosuRegion = gosu.getRegion().toUpperCase();

            LiveGameInfo liveGameInfo = getLiveGameData(alias, gosuRegion);
            if (liveGameInfo != null) {
                result.add(persistLiveGame(liveGameInfo, gosu.getRegion()));
            }
        }
        return result;
    }

    @RequestMapping(value = "removeInactiveLiveGames")
    public @ResponseBody
    List<LiveGame> removeInactiveLiveGames() throws Exception {
        return removeInactiveGames(LiveGame.findAllLiveGames());

    }

    @RequestMapping(value = "json", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Collection<LiveGameJson> getLiveGames() {
        List<LiveGame> liveGameList = LiveGame.findLiveGamesByBroadcast(false).getResultList();
        return LiveGameJson.liveGamesToJson(liveGameList);
    }

    @RequestMapping(value = "getNextLiveGame", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    LiveGameJson getNextLiveGame(@RequestParam(value = "streamId", required = false) String streamId,
            @RequestParam(value = "region", required = false) String region) throws Exception {
        LiveGame liveGame = null;
        if (streamId == null) {
            List<LiveGame> liveGames = LiveGame.findLiveGameByBroadcastAndRegionAndOrderByPriority(false, region)
                    .getResultList();
            if (liveGames.isEmpty()) {
                return null;
            } else {
                liveGame = liveGames.get(0);
            }
        } else {
            List<LiveGame> streamingGames = LiveGame.findLiveGamesByStreamId(streamId).getResultList();
            if (streamingGames.isEmpty()) {
                List<LiveGame> liveGames = LiveGame.findLiveGameByBroadcastAndRegionAndOrderByPriority(false, region)
                        .getResultList();
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
                // After removing the stale streamed game grab the next and return
                
            }
        }
        if (liveGame == null) {
            return null;
        }
        return LiveGameJson.liveGameToJson(liveGame);
    }

    @RequestMapping(value = "getNextLiveGameOPGG", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getNextLiveGameFromOpgg(@RequestParam(value = "streamId", required = false) String streamId,
            @RequestParam(value = "region", required = false) String region) throws Exception {
        Document doc = Jsoup.connect(Constants.URL_OPGG).get();
        Elements summoners = doc.getElementsByClass("SpectatorSummoner");
        String gameLink = "";
        Long startTime = Long.MIN_VALUE;
        for (int i = 0; i < summoners.size(); i++) {
            Element info = summoners.get(0).getElementsByClass("Spectate").first();
            String queueType = info.getElementsByClass("QueueType").first().text();
            if(queueType.equals("Ranked Solo 5v5") || queueType.equals("솔랭 2인")) {
                Long gameDuration = Long.MIN_VALUE;
                try {
                gameDuration = Long.parseLong(info.getElementsByClass("_countdown").first().attr("data-timestamp"));
                } catch (Exception e) {
                    continue;
                }
                if(startTime < gameDuration) {
                    startTime = gameDuration;
                    gameLink = info.getElementsByClass("SpectateButton").first().select("a").attr("href");
                }
            }
        }
         gameLink = gameLink.substring(gameLink.indexOf("id=") + 3);
        return gameLink;
    }

    @RequestMapping(value = "checkLiveGameStatus/{gameId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    boolean checkLiveGameStatus(@PathVariable("gameId") String gameId) throws Exception {
        String region = "kr";
        try {
            HttpResponse<JsonNode> response = Unirest
                    .get("https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.2/match/" + gameId
                            + "?api_key=" + Constants.RIOT_API_KEY).asJson();
            response.getBody().getObject();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private List<LiveGame> removeInactiveGames(List<LiveGame> liveGames) throws Exception {
        List<LiveGame> result = new ArrayList<LiveGame>();
        for (LiveGame liveGame : liveGames) {
            Set<Player> players = liveGame.getPlayers();
            Player gosu = players.iterator().next();
            String alias = gosu.getUrlEncodedAlias();
            String region = gosu.getRegion().toUpperCase();
            LiveGameInfo liveGameInfo = getLiveGameData(alias, region);
            if (liveGameInfo == null || !liveGameInfo.getGameId().equals(liveGame.getGameId())) {
                // remove finished game
                liveGame.remove();
                result.add(liveGame);
            }
        }
        return result;
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
    private LiveGame persistLiveGame(LiveGameInfo liveGameInfo, String region) {
        String gameId = liveGameInfo.getGameId();
        String observerKey = liveGameInfo.getObserverKey();
        LiveGame liveGame = new LiveGame();
        if (LiveGame.findLiveGamesByGameId(gameId.toString()).getResultList().size() == 0) {
            liveGame.setGameId(gameId.toString());
            liveGame.setObserverKey(observerKey);
            liveGame.setRegion(region);
            liveGame.persist();

            for (Player gosu : liveGameInfo.getTeamOne()) {
                gosu.setLiveGame(liveGame);
                gosu.merge();
            }

            for (Player gosu : liveGameInfo.getTeamTwo()) {
                gosu.setLiveGame(liveGame);
                gosu.merge();
            }
        }
        return liveGame;
    }

    private LiveGameInfo getLiveGameData(String alias, String region) throws Exception {
        HttpResponse<JsonNode> response = Unirest
                .get("https://community-league-of-legends.p.mashape.com/api/v1.0/" + region
                        + "/summoner/retrieveInProgressSpectatorGameInfo/" + alias)
                .header("X-Mashape-Key", "vZltgavJCxmshMmz2evwZblTuuDep1AA7FijsneomYUZASc9Uu").asJson();
        JSONObject result = response.getBody().getObject();
        try {
            // Get live game data as JSON and format decode it into
            // LiveGameDataInfo
            JSONObject gameResult = result.getJSONObject("game");
            JSONArray teamOne = gameResult.getJSONObject("teamOne").getJSONArray("array");
            JSONArray teamTwo = gameResult.getJSONObject("teamTwo").getJSONArray("array");
            String gameType = gameResult.getString("queueTypeName");

            if (gameType.equals(Constants.RANKED_QUEUE_TYPE)) {
                JSONObject playerCredentials = result.getJSONObject("playerCredentials");
                Long gameId = playerCredentials.getLong("gameId");
                String observerKey = playerCredentials.getString("observerEncryptionKey");
                LiveGameInfo gameInfo = new LiveGameInfo();
                gameInfo.setGameType(gameType);
                gameInfo.setGameId(gameId.toString());
                gameInfo.setObserverKey(observerKey);
                gameInfo.setTeamOne(persistPlayersInTeam(teamOne, region));
                gameInfo.setTeamTwo(persistPlayersInTeam(teamTwo, region));
                return gameInfo;
            } else {
                return null;
            }
        } catch (JSONException e) {
            log.info("Player " + alias + " from region " + region + " is not in an active game!");
            return null;
        }
    }

    private List<Player> persistPlayersInTeam(JSONArray team, String region) throws JSONException, Exception {
        List<Player> gosuList = new ArrayList<Player>();
        for (int i = 0; i < team.length(); i++) {
            JSONObject gosuInfo = team.getJSONObject(i);
            Long accountId = gosuInfo.getLong("summonerId");
            gosuList.add(PlayerController.updateGosuInRegionWithAlias(region.toLowerCase(), accountId.toString()));
        }
        return gosuList;
    }
}
