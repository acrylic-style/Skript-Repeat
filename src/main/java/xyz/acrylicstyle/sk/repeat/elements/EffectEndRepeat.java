package xyz.acrylicstyle.sk.repeat.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.sk.repeat.util.EffectSection;

@SuppressWarnings("unused")
public class EffectEndRepeat extends Effect {
    static {
        Skript.registerEffect(EffectEndRepeat.class, "end repeat");
    }

    @Override
    protected void execute(@NotNull Event e) {
        if (!EffectRepeat.cancelledEvents.contains(e)) EffectRepeat.cancelledEvents.add(e);
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "End repeat";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        Node n = SkriptLogger.getNode();
        if (n == null) {
            Skript.error("Cannot get current node");
            return false;
        }
        Node current = n.getParent();
        EffectSection section = null;
        while (current != null) {
            section = EffectSection.map.get(current);
            if (section != null) break;
            current = current.getParent();
        }
        if (!(section instanceof EffectRepeat)) {
            Skript.error("End repeat effect cannot used outside of repeat block");
            return false;
        }
        return true;
    }
}
