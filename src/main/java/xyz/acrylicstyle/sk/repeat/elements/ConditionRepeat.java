package xyz.acrylicstyle.sk.repeat.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.variables.Variables;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.sk.repeat.SkriptRepeat;
import xyz.acrylicstyle.sk.repeat.util.EffectSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class ConditionRepeat extends EffectSection {
    public static final List<Event> cancelledEvents = new ArrayList<>();

    static {
        Skript.registerCondition(ConditionRepeat.class, "repeat %number% times with %timespan% delay");
    }

    private Expression<Long> times;
    private Expression<Timespan> delay;
    public boolean cancel = false;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        if (!canExecute()) {
            Skript.error("Repeat condition doesn't have any content!");
            return false;
        }
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
        AtomicReference<Object> o = new AtomicReference<>(Variables.removeLocals(e));
        long s = Objects.requireNonNull(times.getSingle(e));
        for (long i = 0; i < s; i++) {
            long d = Objects.requireNonNull(delay.getSingle(e)).getTicks_i();
            boolean last = i == s - 1;
            Bukkit.getScheduler().runTaskLater(SkriptRepeat.instance, () -> {
                if (!cancelledEvents.contains(e)) {
                    if (o.get() != null) {
                        Variables.setLocalVariables(e, o.get());
                    }
                    runSection(e);
                    o.set(Variables.removeLocals(e));
                }
                if (last) {
                    o.set(null);
                    cancelledEvents.remove(e);
                }
            }, d * i);
        }
        Variables.setLocalVariables(e, o.get());
    }
}
