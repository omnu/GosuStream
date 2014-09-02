package com.gosustream.lol.web;
import java.util.Collection;
import java.util.List;

import org.json.JSONObject;
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
import com.gosustream.lol.domain.Region;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@RequestMapping("/livegames")
@Controller
@RooWebScaffold(path = "livegames", formBackingObject = LiveGame.class)
public class LiveGameController {
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "update/{region}/{alias}")
    public void checkGosuLiveGame(@PathVariable("region") String region,
                                  @PathVariable("alias") String alias) throws Exception {
        if(Region.valueOf(region) == null) {
            // Invalid region
            throw new Exception();
        }
        
        // These code snippets use an open-source library. http://unirest.io/java
        HttpResponse<JsonNode> response = Unirest.get("https://community-league-of-legends.p.mashape.com/api/v1.0/" + region + "/summoner/retrieveInProgressSpectatorGameInfo/"+ alias)
        .header("X-Mashape-Key", "vZltgavJCxmshMmz2evwZblTuuDep1AA7FijsneomYUZASc9Uu").asJson();
        JSONObject result = response.getBody().getObject();
        JSONObject playerCredentials = result.getJSONObject("playerCredentials");
        Long gameId = playerCredentials.getLong("gameId");
        String observerKey = playerCredentials.getString("observerEncryptionKey");

        // Check if current game already exists inside live games table and persist if not
        if (LiveGame.findLiveGamesByGameId(gameId.toString()).getResultList().size() == 0) {
            LiveGame liveGame = new LiveGame();
            liveGame.setGameId(gameId.toString());
            liveGame.setObserverKey(observerKey);
            liveGame.persist();
        }
    }
    
    
    @RequestMapping(value = "json", method = RequestMethod.GET, produces="application/json")
    public @ResponseBody Collection<LiveGameJson> getLiveGames() {
        List<LiveGame> liveGameList = LiveGame.findLiveGamesByBroadcast(false).getResultList();
        return LiveGameJson.liveGamesToJson(liveGameList);
    }
}
