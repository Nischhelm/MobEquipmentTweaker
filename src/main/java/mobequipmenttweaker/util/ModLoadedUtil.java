package mobequipmenttweaker.util;

import net.minecraftforge.fml.common.Loader;

public class ModLoadedUtil {
    public static final String FISH_UNDEAD_RISING_MODID = "mod_lavacow";
    public static final String SPARTAN_WEAPONRY_MODID = "spartanweaponry";

    public static final LoadedContainer fur = new LoadedContainer(FISH_UNDEAD_RISING_MODID);
    public static final LoadedContainer spartanweaponry = new LoadedContainer(SPARTAN_WEAPONRY_MODID);

    public static class LoadedContainer{
        private Boolean isLoaded = null;
        private final String key;
        private LoadedContainer(String key){
            this.key = key;
        }
        public boolean isLoaded(){
            if(this.isLoaded == null) isLoaded = Loader.isModLoaded(key);
            return isLoaded;
        }
    }
}
