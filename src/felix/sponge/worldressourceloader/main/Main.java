package felix.sponge.worldressourceloader.main;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.common.resourcepack.SpongeURIResourcePack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@Plugin(id = "world_resource", name = "World Resource Loader", version = "1.0")
public class Main {
    private HashMap<String, String[]> resourcePacks;
    private HashMap<String, String> worlds;

    @Listener
    public void pluginLoad(GameInitializationEvent event) {
        worlds = new HashMap<>();
        //Load config
        File folder = new File("config" + File.pathSeparator + "worldResource");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (Objects.requireNonNull(folder.listFiles()).length == 0) {
            System.err.println("No config files for world resource");
            return;
        }
        File[] packs = folder.listFiles(pathname -> pathname.getName().endsWith(".rconf"));
        if (packs != null) {
            for (File pack : packs) {
                String name = pack.getName().replace(".rconf", "");
                String uri = "";
                ArrayList<String> worlds = new ArrayList<>();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(pack));
                    uri = reader.readLine();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        worlds.add(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (String world : worlds) {
                    resourcePacks.put(world, new String[]{name, uri});
                }
            }
        } else System.err.println("No config files for world resource");
    }


    @Listener
    public void onWorldChange(MoveEntityEvent.Teleport event){
        if (event.getTargetEntity() instanceof Player){
            if (!worlds.get(event.getTargetEntity().getUniqueId().toString()).equals(event.getTargetEntity().getWorld().getUniqueId().toString())){
                String worldID = event.getTargetEntity().getWorld().getUniqueId().toString();
                worlds.put(event.getTargetEntity().getUniqueId().toString(), worldID);
                if (resourcePacks.get(worldID) != null) {
                    try {
                        ((Player) event.getTargetEntity()).sendResourcePack(new SpongeURIResourcePack(resourcePacks.get(worldID)[0], resourcePacks.get(worldID)[1]));
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Listener
    public void onPlayerJoined(ClientConnectionEvent.Join event) {
        worlds.put(event.getTargetEntity().getUniqueId().toString(), event.getTargetEntity().getWorld().getUniqueId().toString());
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        worlds.remove(event.getTargetEntity().getUniqueId().toString());
    }

}
