package fr.mrmicky.fastinv.components;

import fr.mrmicky.fastinv.ButtonContainer;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A reusable element that can be added to any gui.
 * <p>
 *     Gui components work similar to how the web framework React uses components.
 *     You can create an instance of a component, edit its properties and add them to a gui.
 *     Components can be re-accessed and edited via {@link FastInv#getComponent(Class)}
 * </p>
 *
 * @author Preva1l
 * @apiNote Only one of each component can be added to each gui.
 */
public abstract class GuiComponent implements ButtonContainer {
    protected final List<Integer> slots;

    protected final List<ItemStack> contents = new ArrayList<>();
    protected final List<Consumer<InventoryClickEvent>> contentHandlers = new ArrayList<>();

    protected GuiComponent() {
        this.slots = new ArrayList<>();
    }

    public void setSlots(List<Integer> slots) {
        this.slots.clear();
        this.slots.addAll(slots);
    }

    /**
     * Add an item to the contents of the component.
     */
    @Override
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        addContent(item, handler);
    }

    /**
     * Set the item of at a specific index in the component.
     */
    @Override
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        setContent(slot, item, handler);
    }

    /**
     * Remove an item from the contents of the component at a certain index.
     */
    @Override
    public void removeItem(int slot) {
        this.contents.remove(slot);
        this.contentHandlers.remove(slot);
    }

    /**
     * Clear the contents of the component.
     */
    @Override
    public void clearItems() {
        clearContent();
    }

    /**
     * Add an item to the components contents.
     */
    @Override
    public void addContent(ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contents.add(item);
        this.contentHandlers.add(handler);
    }

    /**
     * Add a collection of items to the components contents.
     */
    @Override
    public void addContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(handlers, "handlers");

        if (content.size() != handlers.size()) {
            throw new IllegalArgumentException("The content and handlers lists must have the same size");
        }

        this.contents.addAll(content);
        this.contentHandlers.addAll(handlers);
    }

    /**
     * Set the content at a certain index for this component.
     */
    @Override
    public void setContent(int index, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contents.set(index, item);
        this.contentHandlers.set(index, handler);
    }

    /**
     * Clear the contents of the component.
     */
    @Override
    public void clearContent() {
        this.contents.clear();
        this.contentHandlers.clear();
    }

    public void removeItem(ItemStack item) {
        int i = 0;
        for (ItemStack content : new ArrayList<>(contents)) {
            if (content.equals(item)) {
                contents.remove(i);
                contentHandlers.remove(i);
                return;
            }
            i++;
        }
    }

    /**
     * Processes and adds the items to the provided gui.
     * <p>
     *     This is called by the gui when adding the component via {@link FastInv#addComponent(GuiComponent)}.
     *     But it may also be called to refresh the components buttons at a later time.
     *     There is no need to run this method when using methods provided by the overriding component as they will update themselves accordingly.
     * </p>
     *
     * @param inv the inventory to apply the component to.
     */
    public abstract void apply(FastInv inv);
}
