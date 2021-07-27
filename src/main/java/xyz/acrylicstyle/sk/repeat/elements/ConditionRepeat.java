package xyz.acrylicstyle.sk.repeat.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.sk.repeat.SkriptRepeat;
import xyz.acrylicstyle.sk.repeat.util.EffectSection;

import java.util.Objects;

@SuppressWarnings("unused")
public class ConditionRepeat extends EffectSection {
    static {
        Skript.registerCondition(ConditionRepeat.class, "repeat %number% times with %timespan% delay");
    }

    private Expression<Long> times;
    private Expression<Timespan> delay;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        times = (Expression<Long>) expressions[0];
        delay = (Expression<Timespan>) expressions[1];
        loadSection(true);
        return true;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "Repeat " + times.toString(event, b) + " times with delay of " + delay.toString(event, b) + " ticks";
    }

    @Override
    protected void execute(Event e) {
        Bukkit.getScheduler().runTaskLater(SkriptRepeat.instance, () -> {
            long s = Objects.requireNonNull(times.getSingle(e));
            for (long i = 0; i < s; i++) {
                runSection(e);
            }
        }, Objects.requireNonNull(delay.getSingle(e)).getTicks_i());
    }
}
