/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.osgibullet;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.nkjel14.commonbullet.Bullet;
import dk.sdu.nkjel14.commonbullet.BulletSPI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Nick
 */
public class BulletControlSystem implements IGamePluginService, IEntityProcessingService, BulletSPI {

    private List<Bullet> bullets = new CopyOnWriteArrayList();

    @Override
    public void start(GameData gameData, World world) {
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Bullet bullet : bullets)
            world.removeEntity(bullet);
    }
    
    @Override
    public void process(GameData gameData, World world) {
        // Handle all bullets
        for (Bullet bullet : bullets) {
            handleCollision(bullet, gameData, world);
            handleBullet(gameData, world, bullet);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entity createBullet(Entity owner, World world) {
        Bullet bullet = createBulletEntity(owner);
        bullets.add(bullet);
        world.addEntity(bullet);
        return bullet;
    }

    private void handleBullet(GameData gameData, World world, Bullet bullet) {
        bullet.reduceExpiration(gameData.getDelta());
        if (bullet.getExpiration() <= 0.0f || bullet.getIsHit()) {
            bullets.remove(bullet);
            world.removeEntity(bullet);
        }
    }

    private void handleCollision(Bullet bullet, GameData gameData, World world) {
        for (Entity entity : world.getEntities()) {
            // Ignore cases
            if (bullet == entity || bullet.getOwner() == entity) {
                    continue;
            }
            
            // Calculate distance between bullet and entity
            float distance = (float) Math.sqrt(Math.pow(bullet.getX() - entity.getX(), 2)
                    + Math.pow(bullet.getY() - entity.getY(), 2));

            if (distance < bullet.getRadius() + entity.getRadius()) {
                entity.setIsHit(true);
                entity.setLife(entity.getLife() - 50);
                bullet.setIsHit(true);
            }
        }
    }

    private Bullet createBulletEntity(Entity owner) {
        Bullet bullet = new Bullet(owner);

        bullet.setX(owner.getX());
        bullet.setY(owner.getY());
        bullet.setRadians(owner.getRadians());

        float speed = 350;
        bullet.setDx((float) Math.cos(bullet.getRadians()) * speed);
        bullet.setDy((float) Math.sin(bullet.getRadians()) * speed);

        bullet.setRadius(1);

        bullet.setExpiration(5.0f);

        return bullet;
    }
}
