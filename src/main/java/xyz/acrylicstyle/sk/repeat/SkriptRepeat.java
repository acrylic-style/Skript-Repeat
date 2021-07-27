package xyz.acrylicstyle.sk.repeat;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SkriptRepeat extends JavaPlugin {
    public static SkriptRepeat instance;

    private SkriptAddon addon;

    public SkriptRepeat() {
        instance = this;
    }

    @Override
    public void onEnable() {
        addon = Skript.registerAddon(this);
        try {
            addon.loadClasses("xyz.acrylicstyle.sk.repeat", "elements");
        } catch (IOException ex) {
            getLogger().warning("Failed to load addon");
            ex.printStackTrace();
        }
    }

    @NotNull
    public SkriptAddon getAddon() {
        return addon;
    }
}
