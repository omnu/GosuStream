package com.gosustream.lol.domain;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class Player {

    /**
     */
    @NotNull
    private String alias;

    /**
     */
    @NotNull
    private String region;

    /**
     */
    @NotNull
    @Min(0L)
    private int point;
}
