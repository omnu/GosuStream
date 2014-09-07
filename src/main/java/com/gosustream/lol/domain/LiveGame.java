package com.gosustream.lol.domain;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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

    @OneToMany(cascade = { javax.persistence.CascadeType.PERSIST, javax.persistence.CascadeType.MERGE, javax.persistence.CascadeType.REFRESH, javax.persistence.CascadeType.DETACH }, mappedBy = "liveGame")
    private Set<Player> players = new HashSet<Player>();

    public void prioritize() {
    }

    public static TypedQuery<LiveGame> findLiveGameByBroadcastAndOrderByPriority(boolean broadcastValue) {
        EntityManager em = LiveGame.entityManager();
        StringBuilder queryBuilder = new StringBuilder("SELECT o FROM LiveGame AS o WHERE o.broadcast = :broadcast ORDER BY o.priority DESC");
        TypedQuery<LiveGame> q = em.createQuery(queryBuilder.toString(), LiveGame.class);
        q.setParameter("broadcast", broadcastValue);
        q.setMaxResults(1);
        return q;
    }
}
