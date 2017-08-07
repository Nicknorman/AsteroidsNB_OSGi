package dk.sdu.mmmi.cbse.playersystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.mmmi.cbse.common.services.IGamePluginService;
import dk.sdu.nkjel14.commonbullet.BulletSPI;
import org.openide.util.Lookup;
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

public class PlayerControlSystem implements IGamePluginService, IEntityProcessingService {
    private Entity player;
    private Lookup lookup = Lookup.getDefault();
    
    @Override
    public void start(GameData gameData, World world) {
        // Add entities to the world
        player = createPlayerShip(gameData);
        world.addEntity(player);
    }
    
    @Override
    public void stop(GameData gameData, World world) {
        // Remove entities
        world.removeEntity(player);
    }
    
    @Override
    public void process(GameData gameData, World world) {
        if (!world.getEntities().contains(player)) {
            // Respawn player
            player = createPlayerShip(gameData);
            world.addEntity(player);
        }
        
        handlePlayer(player, gameData, world);
        calcPlayer(player, gameData);
        setShape(player);
    }
    
    private void calcPlayer(Entity player, GameData gameData) {
        float dt = gameData.getDelta();
        float radians = player.getRadians();
        int rotationSpeed = player.getRotationSpeed();
        float dx = player.getDx();
        float dy = player.getDy();
        float acceleration = player.getAcceleration();
        float deacceleration = player.getDeacceleration();
        float maxSpeed = player.getMaxSpeed();
        
        if (gameData.getKeys().isDown(GameKeys.LEFT)) {
            radians += rotationSpeed * dt;
        } else if (gameData.getKeys().isDown(GameKeys.RIGHT)) {
            radians -= rotationSpeed * dt;
        }
        
        // Accelerating
        if (gameData.getKeys().isDown(GameKeys.UP)) {
            dx += (float)Math.cos(radians) * acceleration * dt;
            dy += (float)Math.sin(radians) * acceleration * dt;
        }
        
        // Decelaration
        float vec = (float)Math.sqrt(dx * dx + dy * dy);
        if (vec > 0) {
            dx -= (dx / vec) * deacceleration * dt;
            dy -= (dy / vec) * deacceleration * dt;
	}
        if(vec > maxSpeed) {
            dx = (dx / vec) * maxSpeed;
            dy = (dy / vec) * maxSpeed;
	}
        
        player.setRadians(radians);
        player.setDx(dx);
        player.setDy(dy);
        player.setAcceleration(acceleration);
        player.setDeacceleration(deacceleration);
    }
    
    private void handlePlayer(Entity player, GameData gameData, World world) {
        if (player.getIsHit()) {
            player.setIsHit(false);
        }
        
        if (player.getLife() <= 0) {
            world.removeEntity(player);
        }
        
        // Shoot
        if (gameData.getKeys().isPressed(GameKeys.SPACE)) {
            BulletSPI bulletSPI = lookup.lookup(BulletSPI.class);
            if (bulletSPI == null) {
                System.out.println("Player: No BulletSPI module found.");
            } else {
                bulletSPI.createBullet(player, world);
            }
        }
    }
    
    private Entity createPlayerShip(GameData gameData) {
        Entity playerShip = new Entity();

        playerShip.setPosition(gameData.getDisplayWidth() / 2, gameData.getDisplayHeight() / 2);

        playerShip.setMaxSpeed(300);
        playerShip.setAcceleration(200);
        playerShip.setDeacceleration(10);

        playerShip.setRadians(3.1415f / 2);
        playerShip.setRotationSpeed(3);
        playerShip.setRadius(10);
        
        playerShip.setLife(100);

        return playerShip;
    }
    
    private void setShape(Entity player) {
        float[] shapex = new float[4];
        float[] shapey = new float[4];
        float x = player.getX();
        float y = player.getY();
        float radians = player.getRadians();
        
        shapex[0] = x + (float)Math.cos(radians) * 8;
	shapey[0] = y + (float)Math.sin(radians) * 8;
		
	shapex[1] = x + (float)Math.cos(radians - 4 * 3.1415f / 5) * 8;
	shapey[1] = y + (float)Math.sin(radians - 4 * 3.1145f / 5) * 8;
		
	shapex[2] = x + (float)Math.cos(radians + 3.1415f) * 5;
	shapey[2] = y + (float)Math.sin(radians + 3.1415f) * 5;
		
	shapex[3] = x + (float)Math.cos(radians + 4 * 3.1415f / 5) * 8;
	shapey[3] = y + (float)Math.sin(radians + 4 * 3.1415f / 5) * 8;
        
        player.setShapeX(shapex);
        player.setShapeY(shapey);
    }
    
}
