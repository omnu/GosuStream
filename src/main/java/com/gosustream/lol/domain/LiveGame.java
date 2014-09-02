package com.gosustream.lol.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

import javax.validation.constraints.NotNull;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findLiveGamesByBroadcast" })
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
}
