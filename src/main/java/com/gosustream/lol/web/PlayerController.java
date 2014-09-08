package com.gosustream.lol.web;

import java.net.URLEncoder;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.gosustream.lol.domain.Constants;
import com.gosustream.lol.domain.Player;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

@RequestMapping("/players")
@Controller
@RooWebScaffold(path = "players", formBackingObject = Player.class)
public class PlayerController {

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/updateGosu/{region}")
    public void updateGosuInRegion(@PathVariable("region") String region) throws Exception {
        // These code snippets use an open-source library.
        // http://unirest.io/java
        HttpResponse<JsonNode> response = Unirest
                .get("https://" + region + ".api.pvp.net/api/lol/" + region
                        + "/v2.5/league/challenger?type=RANKED_SOLO_5x5&api_key=" + Constants.RIOT_API_KEY).asJson();
        JSONObject result = response.getBody().getObject();
        JSONArray gosuList = result.getJSONArray("entries");

        for (int i = 0; i < gosuList.length(); i++) {
            JSONObject gosuInfo = gosuList.getJSONObject(i);
            String alias = gosuInfo.getString("playerOrTeamName");
            // Check to see if player exists and update his/her point
            List<Player> gosuCheck = Player.findPlayersByRegionAndAlias(region, alias).getResultList();
            if (gosuCheck.isEmpty()) {
                // Player does not exist inside the database
                Player gosu = new Player();
                gosu.setAlias(alias);
                gosu.setUrlEncodedAlias(URLEncoder.encode(alias, "UTF-8"));
                gosu.setAccountId(gosuInfo.getLong("playerOrTeamId"));
                gosu.setPoint(2600 + gosuInfo.getInt("leaguePoints"));
                gosu.setRegion(region);
                gosu.setIsGosu(true);
                gosu.persist();
            } else {
                Player gosu = gosuCheck.get(0);
                gosu.setPoint(2600 + gosuInfo.getInt("leaguePoints"));
                gosu.merge();
            }
        }
    }
    
    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/updateGosu/{region}/{accountId}", produces = "application/json")
    public static Player updateGosuInRegionWithAlias(@PathVariable("region") String region,
            @PathVariable("accountId") String accountId) throws Exception {
        // These code snippets use an open-source library.
        // http://unirest.io/java
        HttpResponse<JsonNode> response = Unirest
                .get("https://" + region + ".api.pvp.net/api/lol/" + region + "/v2.5/league/by-summoner/" + accountId
                        + "/entry?api_key=" + Constants.RIOT_API_KEY).asJson();
        JSONObject result = response.getBody().getObject();
        JSONArray gosuList = result.getJSONArray(accountId).getJSONObject(0).getJSONArray("entries");

        JSONObject gosuInfo = gosuList.getJSONObject(0);
        String alias = gosuInfo.getString("playerOrTeamName");
        // Check to see if player exists and update his/her point
        List<Player> gosuCheck = Player.findPlayersByRegionAndAlias(region, alias).getResultList();
        Player gosu = null;
        if (gosuCheck.isEmpty()) {
            // Player does not exist inside the database
            gosu = new Player();
            gosu.setAlias(alias);
            gosu.setUrlEncodedAlias(URLEncoder.encode(alias, "UTF-8"));
            gosu.setAccountId(gosuInfo.getLong("playerOrTeamId"));
            gosu.setPoint(2600 + gosuInfo.getInt("leaguePoints"));
            gosu.setRegion(region);
            gosu.setIsGosu(false);
            gosu.persist();
        } else {
            gosu = gosuCheck.get(0);
            gosu.setPoint(2600 + gosuInfo.getInt("leaguePoints"));
            gosu.merge();
        }
        return gosu;
    }
}
