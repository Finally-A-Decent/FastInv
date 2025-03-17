package fr.mrmicky.fastinv;

//import fr.mrmicky.fastinv.components.GuiComponent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class InventoryScheme {

    private final List<String> masks = new ArrayList<>();
    private final Map<Character, ItemStack> items = new HashMap<>();
    private final Map<Character, Consumer<InventoryClickEvent>> handlers = new HashMap<>();
    //private final Map<Character, Class<? extends GuiComponent>> componentChars = new HashMap<>();
    private char paginationChar;
    private char nextPageChar;
    private char previousPageChar;

    /**
     * Add a mask to this scheme including all sort of characters.
     * For example: "110101011"
     * Replaces all spaces with nothing for styling.
     *
     * @param mask a 9 characters mask
     * @return this scheme instance
     */
    public InventoryScheme mask(String mask) {
        Objects.requireNonNull(mask);
        mask = mask.replace(" ", "");
        this.masks.add(mask.length() > 9 ? mask.substring(0, 10) : mask);

        return this;
    }

    /**
     * Add multiples masks to this scheme including all sort of characters.
     * For example: "111111111", "110101011", "111111111"
     *
     * @param masks multiple 9-characters masks
     * @return this scheme instance
     */
    public InventoryScheme masks(String... masks) {
        for (String mask : Objects.requireNonNull(masks)) {
            mask(mask);
        }
        return this;
    }

    /**
     * Bind character to the corresponding item in the inventory.
     *
     * @param character the associated character in the mask
     * @param item      the item to use for this character
     * @param handler   consumer for the item
     * @return this scheme instance
     */
    public InventoryScheme bindItem(char character, ItemStack item, Consumer<InventoryClickEvent> handler) {
        this.items.put(character, Objects.requireNonNull(item));

        if (handler != null) {
            this.handlers.put(character, handler);
        }
        return this;
    }

    /**
     * Bind character to the corresponding item in the inventory.
     *
     * @param character the associated character in the mask
     * @param item      the item to use for this character
     * @return this scheme instance
     */
    public InventoryScheme bindItem(char character, ItemStack item) {
        return this.bindItem(character, item, null);
    }

    /**
     * Bind character for pagination content.
     *
     * @param character the associated character in the mask
     * @return this scheme instance
     */
    public InventoryScheme bindPagination(char character) {
        this.paginationChar = character;
        return this;
    }

    /**
     * Bind character for pagination next page item.
     *
     * @param character the associated character in the mask
     * @return this scheme instance
     */
    public InventoryScheme bindNextPage(char character) {
        this.nextPageChar = character;
        return this;
    }

    /**
     * Bind character for pagination previous page item.
     *
     * @param character the associated character in the mask
     * @return this scheme instance
     */
    public InventoryScheme bindPreviousPage(char character) {
        this.previousPageChar = character;
        return this;
    }

    /**
     * Bind character for a components content.
     *
     * @param character the associated character in the mask
     * @param component the component to bind to
     * @return this scheme instance
     */
//    public InventoryScheme bindComponent(char character, Class<? extends GuiComponent> component) {
//        this.componentChars.put(character, component);
//        return this;
//    }

    /**
     * Unbind any item from this character.
     *
     * @param character the character to unbind
     * @return this scheme instance
     */
    public InventoryScheme unbindItem(char character) {
        this.items.remove(character);
        this.handlers.remove(character);
        return this;
    }

    /**
     * Apply the current inventory scheme to the FastInv instance.
     *
     * @param inv the FastInv instance to apply this scheme to
     */
    public void apply(FastInv inv) {
        List<Integer> paginationSlots = new ArrayList<>();
        //Map<Class<? extends GuiComponent>, List<Integer>> componentSlots = new HashMap<>();

        for (int line = 0; line < this.masks.size(); line++) {
            String mask = this.masks.get(line);

            for (int slot = 0; slot < mask.length(); slot++) {
                char c = mask.charAt(slot);

                if (c == this.paginationChar) {
                    paginationSlots.add(9 * line + slot);
                    continue;
                }

                if (c == this.nextPageChar && inv instanceof PaginatedFastInv) {
                    ((PaginatedFastInv) inv).nextPageSlot(9 * line + slot);
                    continue;
                }

                if (c == this.previousPageChar && inv instanceof PaginatedFastInv) {
                    ((PaginatedFastInv) inv).previousPageSlot(9 * line + slot);
                    continue;
                }

//                if (this.componentChars.containsKey(c)) {
//                    Class<? extends GuiComponent> componentClass = this.componentChars.get(c);
//                    int add = 9 * line + slot;
//
//                    componentSlots.compute(componentClass, (k, v) -> {
//                        if (v == null) v = new ArrayList<>();
//                        v.add(add);
//                        return v;
//                    });
//                    continue;
//                }

                ItemStack item = this.items.get(c);
                Consumer<InventoryClickEvent> handler = this.handlers.get(c);

                if (item != null) {
                    inv.setItem(9 * line + slot, item, handler);
                }
            }
        }

        if (inv instanceof PaginatedFastInv && !paginationSlots.isEmpty()) {
            ((PaginatedFastInv) inv).setContentSlots(paginationSlots);
        }

//        for (Map.Entry<Class<? extends GuiComponent>, List<Integer>> entry : componentSlots.entrySet()) {
//            inv.getComponent(entry.getKey())
//                    .ifPresent(component -> component.setSlots(entry.getValue()));
//        }
    }
}
