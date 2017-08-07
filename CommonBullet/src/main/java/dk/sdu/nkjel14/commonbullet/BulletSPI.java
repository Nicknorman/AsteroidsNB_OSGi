/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.commonbullet;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.World;

/**
 *
 * @author Nick
 */
public interface BulletSPI {
    /**
     * Service implementor makes sure to add the bullet to World
     * @param owner
     * @param world
     * @return 
     */
    Entity createBullet(Entity owner, World world);
}
