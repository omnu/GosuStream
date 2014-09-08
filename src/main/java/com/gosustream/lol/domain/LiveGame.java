package com.gosustream.lol.domain;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findLiveGamesByBroadcast", "findLiveGamesByGameId", "findLiveGamesByStreamId" })
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

    /**
     */
    private String streamId;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "liveGame")
    private Set<Player> players = new HashSet<Player>();

    public void prioritize() {
    }

    public static TypedQuery<LiveGame> findLiveGameByBroadcastAndRegionAndOrderByPriority(boolean broadcastValue, String region) {
        EntityManager em = LiveGame.entityManager();
        StringBuilder queryBuilder = new StringBuilder();
        if(region == null) {
            queryBuilder.append("SELECT o FROM LiveGame AS o WHERE o.broadcast = :broadcast ORDER BY o.priority DESC");
        } else {
            queryBuilder.append("SELECT o FROM LiveGame AS o WHERE o.broadcast = :broadcast AND o.region = :region ORDER BY o.priority DESC");
        }
        TypedQuery<LiveGame> q = em.createQuery(queryBuilder.toString(), LiveGame.class);
        q.setParameter("broadcast", broadcastValue);
        if(region != null) {
            q.setParameter("region", region);
        }
        q.setMaxResults(1);
        return q;
    }
}
