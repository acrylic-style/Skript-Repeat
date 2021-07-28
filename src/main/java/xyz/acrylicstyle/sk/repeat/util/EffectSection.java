package xyz.acrylicstyle.sk.repeat.util;

import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.log.HandlerList;
import ch.njol.skript.log.LogHandler;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

public abstract class EffectSection extends Condition {
    protected SectionNode section = null;
    private TriggerSection trigger = null;
    private boolean executeNext = true;
    private final transient Map<Event, Object> current = new WeakHashMap<>();
    private final transient Map<Event, Iterator<?>> currentIter = new WeakHashMap<>();

    public EffectSection() {
        Node n = SkriptLogger.getNode();
        if (!(n instanceof SectionNode)) return;
        String comment;
        try {
            Field field = Node.class.getDeclaredField("comment");
            field.setAccessible(true);
            comment = (String) field.get(n);
        } catch (ReflectiveOperationException ignore) {
            comment = "";
        }
        section = new SectionNode(Objects.requireNonNull(n.getKey()), comment, Objects.requireNonNull(n.getParent()), n.getLine());
        try {
            Field field = SectionNode.class.getDeclaredField("nodes");
            field.setAccessible(true);
            field.set(section, field.get(n));
            field.set(n, new ArrayList<Node>());
        } catch (ReflectiveOperationException ignore) {}
    }

    public static void stopLog(RetainingLogHandler logger) {
        logger.stop();
        HandlerList handler;
        try {
            Field field = SkriptLogger.class.getDeclaredField("handlers");
            field.setAccessible(true);
            handler = (HandlerList) field.get(null);
        } catch (ReflectiveOperationException ignore) {
            return;
        }
        if (handler == null)
            return;
        Iterator<LogHandler> it = handler.iterator();
        List<LogHandler> toStop = new ArrayList<>();
        while (it.hasNext()) {
            LogHandler l = it.next();
            if (l instanceof ParseLogHandler) {
                toStop.add(l);
            } else {
                break;
            }
        }
        toStop.forEach(LogHandler::stop);
        SkriptLogger.logAll(logger.getLog());
    }

    public void loadSection(boolean setNext) {
        if (section != null) {
            RetainingLogHandler errors = SkriptLogger.startRetainingLog();
            try {
                trigger = new TriggerSection(section) {
                    @Override
                    public @NotNull String toString(Event event, boolean b) {
                        return EffectSection.this.toString(event, b);
                    }

                    @Override
                    public TriggerItem walk(@NotNull Event event) {
                        return walk(event, true);
                    }
                };
                if (setNext) {
                    trigger.setNext(getNext());
                    setNext(null);
                }
            } finally {
                stopLog(errors);
            }
            section = null;
        }
    }

    @Override
    public boolean check(@NotNull Event e) {
        execute(e);
        if (executeNext && trigger != null)
            setNext(trigger.getNext());
        return !canExecute();
    }

    public boolean canExecute() {
        return section != null || trigger != null;
    }

    protected void runSection(Event e) {
        executeNext = false;
        TriggerItem.walk(trigger, e);
    }

    protected abstract void execute(Event e);
}
