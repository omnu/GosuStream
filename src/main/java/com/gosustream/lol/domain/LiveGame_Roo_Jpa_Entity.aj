// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.LiveGame;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect LiveGame_Roo_Jpa_Entity {
    
    declare @type: LiveGame: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long LiveGame.id;
    
    @Version
    @Column(name = "version")
    private Integer LiveGame.version;
    
    public Long LiveGame.getId() {
        return this.id;
    }
    
    public void LiveGame.setId(Long id) {
        this.id = id;
    }
    
    public Integer LiveGame.getVersion() {
        return this.version;
    }
    
    public void LiveGame.setVersion(Integer version) {
        this.version = version;
    }
    
}
