// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.LiveGame;
import com.gosustream.lol.domain.LiveGameDataOnDemand;
import com.gosustream.lol.domain.LiveGameIntegrationTest;
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

privileged aspect LiveGameIntegrationTest_Roo_IntegrationTest {
    
    declare @type: LiveGameIntegrationTest: @RunWith(SpringJUnit4ClassRunner.class);
    
    declare @type: LiveGameIntegrationTest: @ContextConfiguration(locations = "classpath*:/META-INF/spring/applicationContext*.xml");
    
    declare @type: LiveGameIntegrationTest: @Transactional;
    
    @Autowired
    LiveGameDataOnDemand LiveGameIntegrationTest.dod;
    
    @Test
    public void LiveGameIntegrationTest.testCountLiveGames() {
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", dod.getRandomLiveGame());
        long count = LiveGame.countLiveGames();
        Assert.assertTrue("Counter for 'LiveGame' incorrectly reported there were no entries", count > 0);
    }
    
    @Test
    public void LiveGameIntegrationTest.testFindLiveGame() {
        LiveGame obj = dod.getRandomLiveGame();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to provide an identifier", id);
        obj = LiveGame.findLiveGame(id);
        Assert.assertNotNull("Find method for 'LiveGame' illegally returned null for id '" + id + "'", obj);
        Assert.assertEquals("Find method for 'LiveGame' returned the incorrect identifier", id, obj.getId());
    }
    
    @Test
    public void LiveGameIntegrationTest.testFindAllLiveGames() {
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", dod.getRandomLiveGame());
        long count = LiveGame.countLiveGames();
        Assert.assertTrue("Too expensive to perform a find all test for 'LiveGame', as there are " + count + " entries; set the findAllMaximum to exceed this value or set findAll=false on the integration test annotation to disable the test", count < 250);
        List<LiveGame> result = LiveGame.findAllLiveGames();
        Assert.assertNotNull("Find all method for 'LiveGame' illegally returned null", result);
        Assert.assertTrue("Find all method for 'LiveGame' failed to return any data", result.size() > 0);
    }
    
    @Test
    public void LiveGameIntegrationTest.testFindLiveGameEntries() {
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", dod.getRandomLiveGame());
        long count = LiveGame.countLiveGames();
        if (count > 20) count = 20;
        int firstResult = 0;
        int maxResults = (int) count;
        List<LiveGame> result = LiveGame.findLiveGameEntries(firstResult, maxResults);
        Assert.assertNotNull("Find entries method for 'LiveGame' illegally returned null", result);
        Assert.assertEquals("Find entries method for 'LiveGame' returned an incorrect number of entries", count, result.size());
    }
    
    @Test
    public void LiveGameIntegrationTest.testFlush() {
        LiveGame obj = dod.getRandomLiveGame();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to provide an identifier", id);
        obj = LiveGame.findLiveGame(id);
        Assert.assertNotNull("Find method for 'LiveGame' illegally returned null for id '" + id + "'", obj);
        boolean modified =  dod.modifyLiveGame(obj);
        Integer currentVersion = obj.getVersion();
        obj.flush();
        Assert.assertTrue("Version for 'LiveGame' failed to increment on flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void LiveGameIntegrationTest.testMergeUpdate() {
        LiveGame obj = dod.getRandomLiveGame();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to provide an identifier", id);
        obj = LiveGame.findLiveGame(id);
        boolean modified =  dod.modifyLiveGame(obj);
        Integer currentVersion = obj.getVersion();
        LiveGame merged = obj.merge();
        obj.flush();
        Assert.assertEquals("Identifier of merged object not the same as identifier of original object", merged.getId(), id);
        Assert.assertTrue("Version for 'LiveGame' failed to increment on merge and flush directive", (currentVersion != null && obj.getVersion() > currentVersion) || !modified);
    }
    
    @Test
    public void LiveGameIntegrationTest.testPersist() {
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", dod.getRandomLiveGame());
        LiveGame obj = dod.getNewTransientLiveGame(Integer.MAX_VALUE);
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to provide a new transient entity", obj);
        Assert.assertNull("Expected 'LiveGame' identifier to be null", obj.getId());
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
        Assert.assertNotNull("Expected 'LiveGame' identifier to no longer be null", obj.getId());
    }
    
    @Test
    public void LiveGameIntegrationTest.testRemove() {
        LiveGame obj = dod.getRandomLiveGame();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to initialize correctly", obj);
        Long id = obj.getId();
        Assert.assertNotNull("Data on demand for 'LiveGame' failed to provide an identifier", id);
        obj = LiveGame.findLiveGame(id);
        obj.remove();
        obj.flush();
        Assert.assertNull("Failed to remove 'LiveGame' with identifier '" + id + "'", LiveGame.findLiveGame(id));
    }
    
}
