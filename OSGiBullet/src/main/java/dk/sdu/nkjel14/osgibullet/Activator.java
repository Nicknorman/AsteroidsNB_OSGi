package dk.sdu.nkjel14.osgibullet;

import dk.sdu.mmmi.cbse.common.services.IEntityProcessingService;
import dk.sdu.nkjel14.commonbullet.BulletSPI;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        BulletControlSystem bulletProcessor = new BulletControlSystem();
        context.registerService(BulletSPI.class.getName(), bulletProcessor, null);
        context.registerService(IEntityProcessingService.class.getName(), bulletProcessor, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

}
