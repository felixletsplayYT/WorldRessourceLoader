package felix.sponge.worldressourceloader.main;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

@Plugin(id = "world_resource", name = "World Resource Loader", version = "1.0")
public class Main {
    HashMap<String, ArrayList<String>> ressourcePacks;
    HashMap<String, String> worlds;

    @Listener
    public void onWorldChange(MoveEntityEvent.Teleport event){
        if (event.getTargetEntity() instanceof Player){
            if (!worlds.get(event.getTargetEntity().getUniqueId().toString()).equals(event.getTargetEntity().getWorld().getUniqueId().toString())){
                worlds.put(event.getTargetEntity().getUniqueId().toString(), event.getTargetEntity().getWorld().getUniqueId().toString());
            }
        }
    }

}
