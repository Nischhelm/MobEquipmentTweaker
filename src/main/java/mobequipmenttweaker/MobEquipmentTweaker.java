package mobequipmenttweaker;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = MobEquipmentTweaker.MODID,
        version = MobEquipmentTweaker.VERSION,
        name = MobEquipmentTweaker.NAME,
        dependencies = "required-after:fermiumbooter@[1.3.0,);"
)
public class MobEquipmentTweaker {
    public static final String MODID = "mobequipmenttweaker";
    public static final String VERSION = "1.0.0";
    public static final String NAME = "Mob Equipment Tweaker";
    public static final Logger LOGGER = LogManager.getLogger();
    public static boolean completedLoading = false;

	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        completedLoading = true;
    }
}
