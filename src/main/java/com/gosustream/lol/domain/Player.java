package com.gosustream.lol.domain;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@Table(uniqueConstraints={@UniqueConstraint(columnNames = {"alias" , "region"})})
@RooJpaActiveRecord(finders = { "findPlayersByRegionAndAlias", "findPlayersByIsGosuAndRegion", "findPlayersByIsGosu" })
public class Player {

    /**
     */
    @NotNull
    private String alias;

    /**
     */
    @NotNull
    private String urlEncodedAlias;

    @NotNull
    private long accountId;

    /**
     */
    @NotNull
    private String region;

    /**
     */
    @ManyToOne
    private LiveGame liveGame;

    /**
     */
    @NotNull
    @Min(0L)
    private int point;

    @NotNull
    private Boolean isGosu = false;
}
