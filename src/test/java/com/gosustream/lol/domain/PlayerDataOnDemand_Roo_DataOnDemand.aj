// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.Player;
import com.gosustream.lol.domain.PlayerDataOnDemand;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.springframework.stereotype.Component;

privileged aspect PlayerDataOnDemand_Roo_DataOnDemand {
    
    declare @type: PlayerDataOnDemand: @Component;
    
    private Random PlayerDataOnDemand.rnd = new SecureRandom();
    
    private List<Player> PlayerDataOnDemand.data;
    
    public Player PlayerDataOnDemand.getNewTransientPlayer(int index) {
        Player obj = new Player();
        setAccountId(obj, index);
        setAlias(obj, index);
        setIsGosu(obj, index);
        setPoint(obj, index);
        setRegion(obj, index);
        setUrlEncodedAlias(obj, index);
        return obj;
    }
    
    public void PlayerDataOnDemand.setAccountId(Player obj, int index) {
        Long accountId = new Integer(index).longValue();
        obj.setAccountId(accountId);
    }
    
    public void PlayerDataOnDemand.setAlias(Player obj, int index) {
        String alias = "alias_" + index;
        obj.setAlias(alias);
    }
    
    public void PlayerDataOnDemand.setIsGosu(Player obj, int index) {
        Boolean isGosu = false;
        obj.setIsGosu(isGosu);
    }
    
    public void PlayerDataOnDemand.setPoint(Player obj, int index) {
        int point = index;
        if (point < 0) {
            point = 0;
        }
        obj.setPoint(point);
    }
    
    public void PlayerDataOnDemand.setRegion(Player obj, int index) {
        String region = "region_" + index;
        obj.setRegion(region);
    }
    
    public void PlayerDataOnDemand.setUrlEncodedAlias(Player obj, int index) {
        String urlEncodedAlias = "urlEncodedAlias_" + index;
        obj.setUrlEncodedAlias(urlEncodedAlias);
    }
    
    public Player PlayerDataOnDemand.getSpecificPlayer(int index) {
        init();
        if (index < 0) {
            index = 0;
        }
        if (index > (data.size() - 1)) {
            index = data.size() - 1;
        }
        Player obj = data.get(index);
        Long id = obj.getId();
        return Player.findPlayer(id);
    }
    
    public Player PlayerDataOnDemand.getRandomPlayer() {
        init();
        Player obj = data.get(rnd.nextInt(data.size()));
        Long id = obj.getId();
        return Player.findPlayer(id);
    }
    
    public boolean PlayerDataOnDemand.modifyPlayer(Player obj) {
        return false;
    }
    
    public void PlayerDataOnDemand.init() {
        int from = 0;
        int to = 10;
        data = Player.findPlayerEntries(from, to);
        if (data == null) {
            throw new IllegalStateException("Find entries implementation for 'Player' illegally returned null");
        }
        if (!data.isEmpty()) {
            return;
        }
        
        data = new ArrayList<Player>();
        for (int i = 0; i < 10; i++) {
            Player obj = getNewTransientPlayer(i);
            try {
                obj.persist();
            } catch (final ConstraintViolationException e) {
                final StringBuilder msg = new StringBuilder();
                for (Iterator<ConstraintViolation<?>> iter = e.getConstraintViolations().iterator(); iter.hasNext();) {
                    final ConstraintViolation<?> cv = iter.next();
                    msg.append("[").append(cv.getRootBean().getClass().getName()).append(".").append(cv.getPropertyPath()).append(": ").append(cv.getMessage()).append(" (invalid value = ").append(cv.getInvalidValue()).append(")").append("]");
                }
                throw new IllegalStateException(msg.toString(), e);
            }
            obj.flush();
            data.add(obj);
        }
    }
    
}
