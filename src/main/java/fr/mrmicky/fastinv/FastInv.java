/*
 * This file is part of FastInv, licensed under the MIT License.
 *
 * Copyright (c) 2018-2021 MrMicky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.mrmicky.fastinv;

import fr.mrmicky.fastinv.components.GuiComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * Lightweight and easy-to-use inventory API for Bukkit plugins.
 * The project is on <a href="https://github.com/MrMicky-FR/FastInv">GitHub</a>.
 *
 * @author MrMicky
 * @version 3.1.1
 */
public class FastInv implements InventoryHolder, ButtonContainer {

    private final Map<Integer, Consumer<InventoryClickEvent>> itemHandlers = new HashMap<>();
    private final Map<Class<? extends GuiComponent>, GuiComponent> components = new HashMap<>();
    private final List<Consumer<InventoryOpenEvent>> openHandlers = new ArrayList<>();
    private final List<Consumer<InventoryCloseEvent>> closeHandlers = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> clickHandlers = new ArrayList<>();
    private final List<Consumer<InventoryDragEvent>> dragHandlers = new ArrayList<>();

    private final Inventory inventory;

    private Predicate<Player> closeFilter;

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size a multiple of 9 as the size of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int)
     */
    public FastInv(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  a multiple of 9 as the size of the inventory
     * @param title the title (name) of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int, String)
     */
    public FastInv(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type the type of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType)
     */
    public FastInv(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  the type of the inventory
     * @param title the title of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType, String)
     */
    public FastInv(InventoryType type, String title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public FastInv(Function<FastInv, Inventory> inventoryFunction) {
        Objects.requireNonNull(inventoryFunction, "inventoryFunction");
        Inventory inv = inventoryFunction.apply(this);

        if (inv.getHolder() != this) {
            throw new IllegalStateException("Inventory holder is not FastInv, found: " + inv.getHolder());
        }

        this.inventory = inv;
    }

    /**
     * Called when the inventory is opened.
     *
     * @param event the InventoryOpenEvent that triggered this method
     */
    protected void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Called when the inventory is clicked.
     *
     * @param event the InventoryClickEvent that triggered this method
     */
    protected void onClick(InventoryClickEvent event) {
    }

    /**
     * Called when the player drags an item in their cursor across the inventory.
     *
     * @param event the InventoryDragEvent that triggered this method
     */
    protected void onDrag(InventoryDragEvent event) {
    }

    /**
     * Called when the inventory is closed.
     *
     * @param event the InventoryCloseEvent that triggered this method
     */
    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addItem(ItemStack item, Consumer<InventoryClickEvent> handler) {
        int slot = this.inventory.firstEmpty();
        if (slot >= 0) {
            setItem(slot, item, handler);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setItem(int slot, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.inventory.setItem(slot, item);

        if (handler != null) {
            this.itemHandlers.put(slot, handler);
        } else {
            this.itemHandlers.remove(slot);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeItem(int slot) {
        this.inventory.clear(slot);
        this.itemHandlers.remove(slot);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearItems() {
        this.inventory.clear();
        this.itemHandlers.clear();
    }

    @Override
    public void addContent(ItemStack item, Consumer<InventoryClickEvent> handler) {
        throw new IllegalStateException("FastInv does not support addContent");
    }

    @Override
    public void addContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        throw new IllegalStateException("FastInv does not support addContent");
    }

    @Override
    public void setContent(int index, ItemStack item, Consumer<InventoryClickEvent> handler) {
        throw new IllegalStateException("FastInv does not support setContent");
    }

    @Override
    public void clearContent() {
        throw new IllegalStateException("FastInv does not support clearContent");
    }

    /**
     * Get a component attached to this gui by the class.
     *
     * @param componentClass the class of the component.
     * @return the component instance, or empty.
     */
    public <T extends GuiComponent> Optional<T> getComponent(Class<T> componentClass) {
        GuiComponent component = this.components.get(componentClass);
        if (component == null) return Optional.empty();
        return Optional.of(componentClass.cast(component));
    }

    /**
     * Add a component to this gui.
     *
     * @param component the component to add.
     */
    public void addComponent(GuiComponent component) {
        components.put(component.getClass(), component);
        component.apply(this);
    }

    /**
     * Add a close filter to prevent players from closing the inventory.
     * To prevent a player from closing the inventory the predicate should return {@code true}.
     *
     * @param closeFilter The close filter
     */
    public void setCloseFilter(Predicate<Player> closeFilter) {
        this.closeFilter = closeFilter;
    }

    /**
     * Add a handler that will be called when the inventory is opened.
     *
     * @param openHandler the handler to add
     */
    public void addOpenHandler(Consumer<InventoryOpenEvent> openHandler) {
        this.openHandlers.add(openHandler);
    }

    /**
     * Add a handler that will be called when the inventory is closed.
     *
     * @param closeHandler the handler to add
     */
    public void addCloseHandler(Consumer<InventoryCloseEvent> closeHandler) {
        this.closeHandlers.add(closeHandler);
    }

    /**
     * Add a handler that will be called when an item is clicked.
     *
     * @param clickHandler the handler to add
     */
    public void addClickHandler(Consumer<InventoryClickEvent> clickHandler) {
        this.clickHandlers.add(clickHandler);
    }

    /**
     * Add a handler that will be called when the player drags an item in their cursor across the inventory.
     *
     * @param dragHandler the handler to add
     */
    public void addDragHandler(Consumer<InventoryDragEvent> dragHandler) {
        this.dragHandlers.add(dragHandler);
    }

    /**
     * Open the inventory to the given player.
     *
     * @param player the player to open the inventory to
     */
    public void open(Player player) {
        Objects.requireNonNull(player, "player").openInventory(this.inventory);
    }

    /**
     * Get the borders of this inventory. If the inventory size is under 27, all slots are returned.
     *
     * @return the inventory borders slots
     */
    public int[] getBorders() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> size < 27 || i < 9
                || i % 9 == 0 || (i - 8) % 9 == 0 || i > size - 9).toArray();
    }

    /**
     * Get the corners of this inventory.
     *
     * @return the inventory corners slots
     */
    public int[] getCorners() {
        int size = this.inventory.getSize();
        return IntStream.range(0, size).filter(i -> i < 2 || (i > 6 && i < 10)
                || i == 17 || i == size - 18
                || (i > size - 11 && i < size - 7) || i > size - 3).toArray();
    }

    /**
     * Get the underlying Bukkit inventory.
     *
     * @return the Bukkit inventory
     */
    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }

    void handleOpen(InventoryOpenEvent e) {
        onOpen(e);

        this.openHandlers.forEach(c -> c.accept(e));
    }

    boolean handleClose(InventoryCloseEvent e) {
        onClose(e);

        this.closeHandlers.forEach(c -> c.accept(e));

        return this.closeFilter != null && this.closeFilter.test((Player) e.getPlayer());
    }

    void handleClick(InventoryClickEvent e) {
        onClick(e);

        this.clickHandlers.forEach(c -> c.accept(e));

        Consumer<InventoryClickEvent> clickConsumer = this.itemHandlers.get(e.getRawSlot());

        if (clickConsumer != null) {
            clickConsumer.accept(e);
        }
    }

    void handleDrag(InventoryDragEvent e) {
        onDrag(e);

        this.dragHandlers.forEach(c -> c.accept(e));
    }
}
