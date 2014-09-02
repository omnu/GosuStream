// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.Player;
import com.gosustream.lol.domain.PlayerDataOnDemand;
import com.gosustream.lol.domain.PlayerIntegrationTest;
import java.util.Iterator;
import java.util.List;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

privileged aspect PlayerIntegrationTest_Roo_IntegrationTest {
    
    declare @type: PlayerIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: PlayerIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: PlayerIntegrationTest: @Transactional;
    
    @Autowired
    PlayerDataOnDemand PlayerIntegrationTest.dod;
    
    @Test
    public void PlayerIntegrationTest.testCountPlayers() {
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", dod.getRandomPlayer());
        long count = Player.countPlayers();
        Assert.assertTrue("Counter for 'Player' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void PlayerIntegrationTest.testFindPlayer() {
        Player obj = dod.getRandomPlayer();
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Player' failed to provide an identifier", id);
        obj = Player.findPlayer(id);
        Assert.assertNotNull("Find method for 'Player' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'Player' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void PlayerIntegrationTest.testFindAllPlayers() {
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", dod.getRandomPlayer());
        long count = Player.countPlayers();
        Assert.assertTrue("Too expensive to perform a find all test for 'Player', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<Player> result = Player.findAllPlayers();
        Assert.assertNotNull("Find all method for 'Player' illegally returned null", result);
        Assert.assertTrue("Find all method for 'Player' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void PlayerIntegrationTest.testFindPlayerEntries() {
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", dod.getRandomPlayer());
        long count = Player.countPlayers();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<Player> result = Player.findPlayerEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'Player' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'Player' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void PlayerIntegrationTest.testFlush() {
        Player obj = dod.getRandomPlayer();
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Player' failed to provide an identifier", id);
        obj = Player.findPlayer(id);
        Assert.assertNotNull("Find method for 'Player' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyPlayer(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'Player' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PlayerIntegrationTest.testMergeUpdate() {
        Player obj = dod.getRandomPlayer();
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Player' failed to provide an identifier", id);
        obj = Player.findPlayer(id);
        boolean modified =  dod.modifyPlayer(obj);
        Integer currentVersion = obj.getVersion();
        Player merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'Player' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void PlayerIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", dod.getRandomPlayer());
        Player obj = dod.getNewTransientPlayer(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'Player' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'Player' identifier to be null", obj.getId());
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
        Assert.assertNotNull("Expected 'Player' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void PlayerIntegrationTest.testRemove() {
        Player obj = dod.getRandomPlayer();
        Assert.assertNotNull("Data on demand for 'Player' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'Player' failed to provide an identifier", id);
        obj = Player.findPlayer(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'Player' with identifier '" + id + "'", Player.findPlayer(id));
    }
    
}
