/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.sdu.nkjel14.movementsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.World;
import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Nick
 */
@ServiceProvider(service = IEntityProcessingService.class)
public class MoveSystem implements IEntityProcessingService {

    @Override
    public void process(GameData gameData, World world) {
        for (Entity entity : world.getEntities()) {
            entity.setX(entity.getX() + entity.getDx() * gameData.getDelta());
            entity.setY(entity.getY() + entity.getDy() * gameData.getDelta());
            
            // wrap
            if(entity.getX() < 0) entity.setX(gameData.getDisplayWidth());
            if(entity.getX() > gameData.getDisplayWidth()) entity.setX(0);
            if(entity.getY() < 0) entity.setY(gameData.getDisplayHeight());
            if(entity.getY() > gameData.getDisplayHeight()) entity.setY(0); 
        }
    }
}
