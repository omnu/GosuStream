package com.gosustream.lol.web;

import java.net.URLEncoder;
import java.util.List;

import javax.persistence.NoResultException;

import com.gosustream.lol.domain.Constants;
import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.domain.LiveGameJson;
import com.gosustream.lol.domain.Player;
import com.gosustream.lol.domain.Region;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.roo.addon.web.mvc.controller.scaffold.RooWebScaffold;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/players")
@Controller
@RooWebScaffold(path = "players", formBackingObject = Player.class)
public class PlayerController {

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/updateGosu/{region}")
    public void updateGosu(@PathVariable("region") String region) throws Exception {
        // These code snippets use an open-source library.
        // http://unirest.io/java
        HttpResponse<JsonNode> response = Unirest
                .get("https://na.api.pvp.net/api/lol/" + region
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
}
