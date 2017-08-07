/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.commonbullet;

import dk.sdu.mmmi.cbse.common.data.Entity;

/**
 *
 * @author Nick
 */
public class Bullet extends Entity {
    private Entity owner;
    
    public Bullet(Entity owner) {
        this.owner = owner;
    }
    
    public Entity getOwner() {
        return owner;
    }
}
