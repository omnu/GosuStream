package com.gosustream.lol.domain;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findLiveGamesByBroadcast", "findLiveGamesByGameId" })
public class LiveGame {

    /**
     */
    @NotNull
    private String gameId;

    /**
     */
    @NotNull
    private String observerKey;

    /**
     */
    @NotNull
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-mm-ss Z")
    private Date startTime = new Date();

    /**
     */
    @NotNull
    private int priority = 0;

    /**
     */
    @NotNull
    private Boolean broadcast = false;
    
    public void prioritize() {
        
    }
    
    public static TypedQuery<LiveGame> findLiveGameByBroadcastAndOrderByPriority() {
        EntityManager em = LiveGame.entityManager();
        StringBuilder queryBuilder = new StringBuilder("SELECT o FROM LiveGame AS o WHERE o.broadcast = FALSE ORDER BY o.priority DESC");
        TypedQuery<LiveGame> q = em.createQuery(queryBuilder.toString(), LiveGame.class);
        q.setMaxResults(1);
        return q;
    }
}
