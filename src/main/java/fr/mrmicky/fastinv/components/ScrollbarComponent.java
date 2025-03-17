package fr.mrmicky.fastinv.components;

import fr.mrmicky.fastinv.FastInv;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A nice little scrollbar for a gui.
 *
 * @author Preva1l
 */
public class ScrollbarComponent extends GuiComponent {
    private FastInv inv;
    /**
     * Maps contents array index to gui slot.
     */
    private final Map<Integer, Integer> scrollbarSlots = new HashMap<>();

    public ScrollbarComponent() {
        this(new ArrayList<>());
    }

    public ScrollbarComponent(List<Integer> slots) {
        setSlots(slots);
    }

    public void scrollDown() {
        if (inv == null) throw new IllegalStateException("ScrollbarComponent has not been initialized");

        if (scrollbarSlots.containsKey(contents.size() - 1)) return;
        Map<Integer, Integer> newMappings = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : scrollbarSlots.entrySet()) {
            newMappings.put(entry.getKey() + 1, entry.getValue());
        }
        scrollbarSlots.clear();
        scrollbarSlots.putAll(newMappings);

        apply(inv);
    }

    public void scrollUp() {
        if (inv == null) throw new IllegalStateException("ScrollbarComponent has not been initialized");

        if (scrollbarSlots.containsKey(0)) return;
        Map<Integer, Integer> newMappings = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : scrollbarSlots.entrySet()) {
            newMappings.put(entry.getKey() - 1, entry.getValue());
        }
        scrollbarSlots.clear();
        scrollbarSlots.putAll(newMappings);

        apply(inv);
    }

    @Override
    public void setSlots(List<Integer> slots) {
        super.setSlots(slots);
        this.scrollbarSlots.clear();

        int i = 0;
        for (int num : slots) {
            this.scrollbarSlots.put(i, num);
            i++;
        }

        apply(inv);
    }

    @Override
    public void apply(FastInv inv) {
        int i = 0;
        for (ItemStack item : contents) {
            if (scrollbarSlots.containsKey(i)) {
                Consumer<InventoryClickEvent> handler = contentHandlers.get(i);
                int slot = scrollbarSlots.get(i);

                inv.setItem(slot, item, handler);
            }
            i++;
        }
    }
}
