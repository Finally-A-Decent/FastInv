package fr.mrmicky.fastinv;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Extension of {@link FastInv} to easily create paginated inventories.
 */
public class PaginatedFastInv extends FastInv {

    private final List<ItemStack> contentItems = new ArrayList<>();
    private final List<Consumer<InventoryClickEvent>> contentHandlers = new ArrayList<>();

    private List<Integer> contentSlots;
    private int page = 1;

    private IntFunction<ItemStack> previousPageItem;
    private IntFunction<ItemStack> nextPageItem;
    private int previousPageSlot = -1;
    private int nextPageSlot = -1;

    /**
     * Create a new FastInv with a custom size.
     *
     * @param size a multiple of 9 as the size of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int)
     */
    public PaginatedFastInv(int size) {
        this(owner -> Bukkit.createInventory(owner, size));
    }

    /**
     * Create a new FastInv with a custom size and title.
     *
     * @param size  a multiple of 9 as the size of the inventory
     * @param title the title (name) of the inventory
     * @see Bukkit#createInventory(InventoryHolder, int, String)
     */
    public PaginatedFastInv(int size, String title) {
        this(owner -> Bukkit.createInventory(owner, size, title));
    }

    /**
     * Create a new FastInv with a custom type.
     *
     * @param type the type of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType)
     */
    public PaginatedFastInv(InventoryType type) {
        this(owner -> Bukkit.createInventory(owner, type));
    }

    /**
     * Create a new FastInv with a custom type and title.
     *
     * @param type  the type of the inventory
     * @param title the title of the inventory
     * @see Bukkit#createInventory(InventoryHolder, InventoryType, String)
     */
    public PaginatedFastInv(InventoryType type, String title) {
        this(owner -> Bukkit.createInventory(owner, type, title));
    }

    public PaginatedFastInv(Function<PaginatedFastInv, Inventory> inventoryFunction) {
        super(inv -> inventoryFunction.apply((PaginatedFastInv) inv));

        this.contentSlots = IntStream.range(0, Math.max(9, getInventory().getSize() - 9))
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContent(ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contentItems.add(item);
        this.contentHandlers.add(handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContent(Collection<ItemStack> content, Collection<Consumer<InventoryClickEvent>> handlers) {
        this.contentItems.addAll(content);
        this.contentHandlers.addAll(handlers);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setContent(int index, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.contentItems.set(index, item);
        this.contentHandlers.set(index, handler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearContent() {
        this.contentItems.clear();
        this.contentHandlers.clear();
    }

    /**
     * Replace the inventory items with the content of the previous page.
     * To open the inventory itself, use {@link #open(Player)}.
     */
    public void openPrevious() {
        openPage(this.page - 1);
    }

    /**
     * Replace the inventory items with the content of the next page.
     * To open the inventory itself, use {@link #open(Player)}.
     */
    public void openNext() {
        openPage(this.page + 1);
    }

    /**
     * Replace the inventory items with the content of the specified page.
     * To open the inventory itself, use {@link #open(Player)}.
     *
     * @param page the page to open
     */
    public void openPage(int page) {
        int lastPage = lastPage();

        this.page = Math.max(1, Math.min(page, lastPage));

        int index = this.contentSlots.size() * (this.page - 1);

        for (int slot : this.contentSlots) {
            if (index >= this.contentItems.size()) {
                removeItem(slot);
                continue;
            }

            setItem(slot, contentItems.get(index), contentHandlers.get(index++));
        }

        if (this.page > 1 && this.previousPageItem != null) {
            setItem(this.previousPageSlot, this.previousPageItem.apply(this.page - 1), e -> openPrevious());
        } else if (this.previousPageSlot >= 0) {
            removeItem(this.previousPageSlot);
        }

        if (this.page < lastPage && this.nextPageItem != null) {
            setItem(this.nextPageSlot, this.nextPageItem.apply(this.page + 1), e -> openNext());
        } else if (this.nextPageSlot >= 0) {
            removeItem(this.nextPageSlot);
        }

        onPageChange(page);
    }

    /**
     * Specify the slots of the inventory that will be used to display the paginated content.
     *
     * @param contentSlots the slots of the inventory to use
     */
    public void setContentSlots(List<Integer> contentSlots) {
        this.contentSlots = Objects.requireNonNull(contentSlots, "contentSlots");
    }

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void previousPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.previousPageSlot = slot;
        this.previousPageItem = item;
    }

    /**
     * Set the item to open the previous page.
     *
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void previousPageItem(IntFunction<ItemStack> item) {
        this.previousPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the previous page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void previousPageItem(int slot, ItemStack item) {
        previousPageItem(slot, page -> item);
    }

    /**
     * Set the item to open the previous page.
     *
     * @param item the item to set
     */
    public void previousPageItem(ItemStack item) {
        previousPageItem(page -> item);
    }

    /**
     * Set the slot at which the previous page item will be placed.
     *
     * @param slot the slot to set the item
     */
    public void previousPageSlot(int slot) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.previousPageSlot = slot;
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void nextPageItem(int slot, IntFunction<ItemStack> item) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.nextPageSlot = slot;
        this.nextPageItem = item;
    }

    /**
     * Set the item to open the next page.
     *
     * @param item a function to get the item to set, with the page the item opens as parameter
     */
    public void nextPageItem(IntFunction<ItemStack> item) {
        this.nextPageItem = item;
    }

    /**
     * Set the item at the specified inventory slot to open the next page.
     *
     * @param slot the inventory to set the item
     * @param item the item to set
     */
    public void nextPageItem(int slot, ItemStack item) {
        nextPageItem(slot, page -> item);
    }

    /**
     * Set the item to open the next page.
     *
     * @param item the item to set
     */
    public void nextPageItem(ItemStack item) {
        nextPageItem(page -> item);
    }

    /**
     * Set the slot at which the next page item will be placed.
     *
     * @param slot the slot to set the item
     */
    public void nextPageSlot(int slot) {
        if (slot < 0 || slot >= getInventory().getSize()) {
            throw new IllegalArgumentException("Invalid slot: " + slot);
        }

        this.previousPageSlot = slot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(Player player) {
        openPage(this.page);

        super.open(player);
    }

    /**
     * Called when the page is changed.
     *
     * @param page the new page
     */
    protected void onPageChange(int page) {
    }

    /**
     * Return the index of the current page. The first page is 1.
     *
     * @return the index of the current page, starting at 1
     */
    public int currentPage() {
        return this.page;
    }

    /**
     * Return the index of the last page. The index of the first page is 1.
     *
     * @return the index of the last page, starting at 1
     */
    public int lastPage() {
        int last = this.contentItems.size() / this.contentSlots.size();
        int remaining = this.contentItems.size() % this.contentSlots.size();

        return remaining == 0 ? last : last + 1;
    }

    /**
     * Return if the current page is the first page.
     *
     * @return true if the current page is the first page
     * @see #currentPage()
     */
    public boolean isFirstPage() {
        return this.page == 1;
    }

    /**
     * Return if the current page is the last page.
     *
     * @return true if the current page is the last page
     * @see #currentPage()
     */
    public boolean isLastPage() {
        return this.page == lastPage();
    }
}
