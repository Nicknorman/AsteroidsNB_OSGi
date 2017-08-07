/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author Nick
 */
@ServiceProviders(value = {
    @ServiceProvider(service = IEntityProcessingService.class),
    @ServiceProvider(service = IGamePluginService.class)
})
public class AsteroidControlSystem implements IGamePluginService, IEntityProcessingService {
    private final int NUM_ASTEROIDS = 2;
    private Random rand = new Random();
    private List<Entity> asteroids = new CopyOnWriteArrayList();
    
    public AsteroidControlSystem() {
        rand = new Random();
    }
    
    @Override
    public void start(GameData gameData, World world) {
        for (int i = 0; i < NUM_ASTEROIDS; i++) {
            Entity asteroid = createParentAsteroid(gameData);
            world.addEntity(asteroid);
            asteroids.add(asteroid);
        }
    }
    
    @Override
    public void stop(GameData gameData, World world) {
        for (Entity asteroid : asteroids) {
            world.removeEntity(asteroid);
            asteroids.remove(asteroid);
        }
    }
    
    @Override
    public void process(GameData gameData, World world) {
        // Split any asteroid first before handling them
        for (Entity asteroid : asteroids) {
            if (asteroid.getIsHit()) {
                splitAsteroid(asteroid, world);
            }
        }
        
        for (Entity asteroid : asteroids) {
            calcAsteroid(asteroid, gameData);
            setShape(asteroid);
            handleCollision(asteroid, world);
        }
    }
    
    private void splitAsteroid(Entity asteroid, World world) { 
        // Split asteroid in 2
        if (asteroid.getRadius() > 2) {
            float radius = asteroid.getRadius() * 0.6f;
            float x = asteroid.getX();
            float y = asteroid.getY();
            float dx = asteroid.getDy();
            float dy = asteroid.getDx();
            float radians = asteroid.getRadians();
            
            Entity ast1 = createSplitAsteroid(radius, x - radius, y, dx*-1, dy*-1, radians + 3.1415f / 2);
            Entity ast2 = createSplitAsteroid(radius, x + radius, y, dx, dy, radians - 3.1415f / 2);
            world.addEntity(ast1);
            world.addEntity(ast2);
            asteroids.add(ast1);
            asteroids.add(ast2);
        }
        
        world.removeEntity(asteroid);
        asteroids.remove(asteroid);
    }
    
    private void calcAsteroid(Entity asteroid, GameData gameData) {
        // Rotating
        asteroid.setRadians(asteroid.getRadians() + asteroid.getRotationSpeed() * gameData.getDelta());
    }
    
    private void handleCollision(Entity asteroid, World world) {
        for (Entity entity : world.getEntities()) {
            float distance = (float)Math.sqrt(Math.pow(entity.getX() - asteroid.getX(), 2)
                + Math.pow(entity.getY() - asteroid.getY(), 2));
            if (distance < entity.getRadius() + asteroid.getRadius()) {
                entity.setLife(0);
            }
        }
    }
    
    private Entity createSplitAsteroid(float radius, float x, float y, float dx, 
            float dy, float radians) {
        Entity asteroid = new Entity();
        
        asteroid.setPosition(x, y);
        
        asteroid.setRadians(radians);
        asteroid.setRotationSpeed(rand.nextInt(2) + 1);
        
        asteroid.setDx(dx);
        asteroid.setDy(dy);
        asteroid.setRadius(radius);
        
        return asteroid;
    }
    
    private Entity createParentAsteroid(GameData gameData) {
        Entity asteroid = new Entity();
        
        asteroid.setPosition(rand.nextFloat()*(float)gameData.getDisplayWidth(), rand.nextFloat()*(float)gameData.getDisplayHeight());
        
        asteroid.setRadians(rand.nextFloat()*3.1415f*2);
        asteroid.setRotationSpeed(rand.nextInt(2) + 1);
        
        asteroid.setDx((rand.nextFloat() - 0.5f)*50);
        asteroid.setDy((rand.nextFloat() - 0.5f)*50);
        asteroid.setRadius(40);
        
        return asteroid;
    }
    
    private void setShape(Entity asteroid) {
        float[] shapex = new float[5];
        float[] shapey = new float[5];
        float x = asteroid.getX();
        float y = asteroid.getY();
        float radians = asteroid.getRadians();
        float radius = asteroid.getRadius();
        
        shapex[0] = x + (float)Math.cos(radians) * radius;
	shapey[0] = y + (float)Math.sin(radians) * radius;
		
	shapex[1] = x + (float)Math.cos(radians - 2 * 3.1415f / 5) * radius;
	shapey[1] = y + (float)Math.sin(radians - 2 * 3.1145f / 5) * radius;
		
	shapex[2] = x + (float)Math.cos(radians - 4 * 3.1415f / 5) * radius;
	shapey[2] = y + (float)Math.sin(radians - 4 * 3.1415f / 5) * radius;
		
	shapex[3] = x + (float)Math.cos(radians + 4 * 3.1415f / 5) * radius;
	shapey[3] = y + (float)Math.sin(radians + 4 * 3.1415f / 5) * radius;
        
        shapex[4] = x + (float)Math.cos(radians + 2 * 3.1415f / 5) * radius;
	shapey[4] = y + (float)Math.sin(radians + 2 * 3.1415f / 5) * radius;
        
        asteroid.setShapeX(shapex);
        asteroid.setShapeY(shapey);
    }
}
