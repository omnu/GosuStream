// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.gosustream.lol.domain;

import com.gosustream.lol.domain.Player;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

privileged aspect Player_Roo_Jpa_Entity {
    
    declare @type: Player: @Entity;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long Player.id;
    
    @Version
    @Column(name = "version")
    private Integer Player.version;
    
    public Long Player.getId() {
        return this.id;
    }
    
    public void Player.setId(Long id) {
        this.id = id;
    }
    
    public Integer Player.getVersion() {
        return this.version;
    }
    
    public void Player.setVersion(Integer version) {
        this.version = version;
    }
    
}
