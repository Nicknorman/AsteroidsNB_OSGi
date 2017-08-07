/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.osgienemy;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.nkjel14.commonbullet.BulletSPI;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Nick
 */

public class EnemyControlSystem implements IGamePluginService, IEntityProcessingService {

    private static final int MAX_ENEMIES = 2;
    
    private BulletSPI bulletSPI;
    private List<Entity> enemies = new CopyOnWriteArrayList();
    private Random rand = new Random();

    
    
    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < MAX_ENEMIES; i++) {
            Entity enemy = createEnemy(gameData);
            enemies.add(enemy);
            world.addEntity(enemy);
        }
    }

    @Override
    public void stop(GameData gameData, World world) {
        for (Entity enemy : enemies) {
            world.removeEntity(enemy);
            enemies.remove(enemy);
        }
    }
    
    /**
     * OSGi DS injection 
     */
    public void setBulletSPI(BulletSPI bulletSPI) {
        System.out.println("OSGiEnemy: loaded BulletSPI");
        this.bulletSPI = bulletSPI;
    }
    
    public void removeBulletSPI(BulletSPI bulletSPI) {
        System.out.println("OSGiEnemy: unloaded BulletSPI");
        this.bulletSPI = null;
    }

    @Override
    public void process(GameData gameData, World world) {
        while (enemies.size() < MAX_ENEMIES) {
            Entity enemy = createEnemy(gameData);
            enemies.add(enemy);
            world.addEntity(enemy);
        }

        for (Entity enemy : enemies) {
            handleEnemy(enemy, world);
            calcEnemy(enemy, gameData);
            setShape(enemy);
        }
    }

    private void handleEnemy(Entity enemy, World world) {
        if (enemy.getIsHit()) {
            enemy.setIsHit(false);
        }

        if (enemy.getLife() <= 0) {
            world.removeEntity(enemy);
            enemies.remove(enemy);
        }
        
        // Shooting
        if (rand.nextInt(100) == 0) {
            if (bulletSPI == null) {
                System.out.println("Enemy: No BulletSPI module found.");
            } else {
                bulletSPI.createBullet(enemy, world);
            }
        }
    }

    private void calcEnemy(Entity enemy, GameData gameData) {
        float dt = gameData.getDelta();
        float radians = enemy.getRadians();
        int rotationSpeed = enemy.getRotationSpeed();
        float dx = enemy.getDx();
        float dy = enemy.getDy();
        float acceleration = enemy.getAcceleration();
        float deacceleration = enemy.getDeacceleration();
        float maxSpeed = enemy.getMaxSpeed();

        // Simple simulation of movement
        radians += rand.nextFloat() * (rand.nextFloat() - 0.5f) * Math.pow(rand.nextInt(5), 2) * rotationSpeed * dt;

        // Acceleration
        if (rand.nextInt(10) == 0) {
            dx += (float) Math.cos(radians) * acceleration * dt;
            dy += (float) Math.sin(radians) * acceleration * dt;
        }

        // Decelaration
        float vec = (float) Math.sqrt(dx * dx + dy * dy);
        if (vec > 0) {
            dx -= (dx / vec) * deacceleration * dt;
            dy -= (dy / vec) * deacceleration * dt;
        }
        if (vec > maxSpeed) {
            dx = (dx / vec) * maxSpeed;
            dy = (dy / vec) * maxSpeed;
        }

        enemy.setRadians(radians);
        enemy.setDx(dx);
        enemy.setDy(dy);
        enemy.setAcceleration(acceleration);
        enemy.setDeacceleration(deacceleration);        
    }

    private void setShape(Entity enemy) {
        float[] shapex = new float[4];
        float[] shapey = new float[4];
        float x = enemy.getX();
        float y = enemy.getY();
        float radians = enemy.getRadians();

        shapex[0] = x + (float) Math.cos(radians) * 8;
        shapey[0] = y + (float) Math.sin(radians) * 8;

        shapex[1] = x + (float) Math.cos(radians - 4 * 3.1415f / 5) * 8;
        shapey[1] = y + (float) Math.sin(radians - 4 * 3.1145f / 5) * 8;

        shapex[2] = x + (float) Math.cos(radians + 3.1415f) * 5;
        shapey[2] = y + (float) Math.sin(radians + 3.1415f) * 5;

        shapex[3] = x + (float) Math.cos(radians + 4 * 3.1415f / 5) * 8;
        shapey[3] = y + (float) Math.sin(radians + 4 * 3.1415f / 5) * 8;

        enemy.setShapeX(shapex);
        enemy.setShapeY(shapey);
    }

    private Entity createEnemy(GameData gameData) {
        Random random = new Random();

        Entity enemyShip = new Entity();

        // Spawn at top of screen
        enemyShip.setPosition((random.nextFloat() * (float) gameData.getDisplayWidth()), gameData.getDisplayHeight());

        enemyShip.setMaxSpeed(300);
        enemyShip.setAcceleration(200);
        enemyShip.setDeacceleration(10);

        enemyShip.setRadians(3.1415f * 1.5f);
        enemyShip.setRotationSpeed(3);
        enemyShip.setRadius(10);

        enemyShip.setLife(100);

        return enemyShip;
    }
}
