package fr.mrmicky.fastinv;

import fr.mrmicky.fastinv.components.GuiComponent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Anything that contains buttons. Eg: {@link FastInv} or {@link GuiComponent}
 *
 * @author Preva1l
 */
public interface ButtonContainer {
    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot, with no click handler.
     *
     * @param item the item to add
     */
    default void addItem(ItemStack item) {
        addItem(item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on the first empty slot with a click handler.
     *
     * @param item    the item to add.
     * @param handler the click handler associated to this item
     */
    void addItem(ItemStack item, Consumer<InventoryClickEvent> handler);

    /**
     * Add an {@link ItemStack} to the inventory on a specific slot, with no click handler.
     *
     * @param slot The slot where to add the item.
     * @param item The item to add.
     */
    default void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on specific slot with a click handler.
     *
     * @param slot    the slot where to add the item
     * @param item    the item to add.
     * @param handler the click handler associated to this item
     */
    void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler);

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots, with no click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     The item to add.
     */
    default void setItems(int slotFrom, int slotTo, ItemStack item) {
        setItems(slotFrom, slotTo, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on a range of slots with a click handler.
     *
     * @param slotFrom starting slot (inclusive) to put the item in
     * @param slotTo   ending slot (exclusive) to put the item in
     * @param item     the item to add
     * @param handler  the click handler associated to these items
     */
    default void setItems(int slotFrom, int slotTo, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int i = slotFrom; i < slotTo; i++) {
            setItem(i, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the slots where to add the item
     * @param item  the item to add
     */
    default void setItems(int[] slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiples slots with a click handler.
     *
     * @param slots   the slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    default void setItems(int[] slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (int slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots, with no click handler.
     *
     * @param slots the list of slots where to add the item
     * @param item  the item to add
     */
    default void setItems(Iterable<Integer> slots, ItemStack item) {
        setItems(slots, item, null);
    }

    /**
     * Add an {@link ItemStack} to the inventory on multiple slots with a click handler.
     *
     * @param slots   the list of slots where to add the item
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    default void setItems(Iterable<Integer> slots, ItemStack item, Consumer<InventoryClickEvent> handler) {
        for (Integer slot : slots) {
            setItem(slot, item, handler);
        }
    }

    /**
     * Remove an {@link ItemStack} from the inventory.
     *
     * @param slot the slot from where to remove the item
     */
    void removeItem(int slot);

    /**
     * Remove multiples {@link ItemStack} from the inventory.
     *
     * @param slots the slots from where to remove the items
     */
    default void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    /**
     * Clear all items from the inventory and remove the click handlers.
     */
    void clearItems();

    /**
     * Add an item to the paginated content with no click handler, the item will be added to the next available slot.
     *
     * @param item the item to add
     */
    default void addContent(ItemStack item) {
        addContent(item, null);
    }

    /**
     * Add an item to the paginated content with a click handler, the item will be added to the next available slot.
     *
     * @param item    the item to add
     * @param handler the click handler associated to this item
     */
    void addContent(ItemStack item, Consumer<InventoryClickEvent> handler);

    /**
     * Add a list of items to the paginated content with no click handler, the items will be added to the next available slots.
     *
     * @param content the list of items to add
     */
    default void addContent(Collection<ItemStack> content) {
        addContent(content, Collections.nCopies(content.size(), null));
    }

    /**
     * Add a list of items to the paginated content with click handlers, the items will be added to the next available slots.
     * The list of click handlers must have the same size as the list of items.
     *
     * @param content  the list of items to add
     * @param handlers the list of click handlers associated to the items
     */
    void addContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers);

    /**
     * Set the item at the specified index of the paginated content, with no click handler.
     *
     * @param index the slot index
     * @param item  the item to set
     */
    default void setContent(int index, ItemStack item) {
        setContent(index, item, null);
    }

    /**
     * Set the item at the specified index of the paginated content, with a click handler.
     *
     * @param index   the slot index
     * @param item    the item to set
     * @param handler the click handler associated to this item
     */
    void setContent(int index, ItemStack item, Consumer<InventoryClickEvent> handler);

    /**
     * Set the list of items as the paginated content, with no click handler. The previous content will be cleared.
     *
     * @param content the list of items to set
     */
    default void setContent(List<ItemStack> content) {
        clearContent();
        addContent(content);
    }

    /**
     * Set the list of items as the paginated content, with click handlers. The previous content will be cleared.
     * The list of click handlers must have the same size as the list of items.
     *
     * @param content  the list of items to set
     * @param handlers the list of click handlers associated to the items
     */
    default void setContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(handlers, "handlers");

        if (content.size() != handlers.size()) {
            throw new IllegalArgumentException("The content and handlers lists must have the same size");
        }

        clearContent();
        addContent(content, handlers);
    }

    /**
     * Clear the paginated content and the associated click handlers.
     */
    void clearContent();
}
