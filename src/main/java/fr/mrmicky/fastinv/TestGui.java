package fr.mrmicky.fastinv;

import fr.mrmicky.fastinv.components.GuiComponent;
import fr.mrmicky.fastinv.components.ScrollbarComponent;
import org.bukkit.Material;

/**
 * Created on 17/03/2025
 *
 * @author Preva1l
 */
public class TestGui extends PaginatedFastInv {
    private final InventoryScheme scheme = new InventoryScheme()
            .masks(
                    "U X X X X X X X X",
                    "S X I I I I I I X",
                    "S X I I I I I I X",
                    "S X I I I I I I X",
                    "D X X P X N X X X"
            )
            .bindPagination('I')
            .bindPagination('P')
            .bindNextPage('N')
            .bindComponent('S', ScrollbarComponent.class);

    public TestGui() {
        super(45, "Hello There");

        GuiComponent scrollbar = new ScrollbarComponent();

        for (int i = 1; i <= 15; i++) {
            new ItemBuilder(Material.IRON_SWORD)
                    .name("Fancy Sword " + i)
                    .add(scrollbar);
        }

        addComponent(scrollbar);
        
        placeScrollbarControls();

        nextPageItem(new ItemBuilder(Material.FEATHER).name("Next Page").build());
        previousPageItem(new ItemBuilder(Material.FEATHER).name("Previous Page").build());

        scheme.apply(this);
    }
    
    private void placeScrollbarControls() {
        new ItemBuilder(Material.ARROW)
                .name("Scroll Up")
                .handler(event -> getComponent(ScrollbarComponent.class).ifPresent(ScrollbarComponent::scrollUp))
                .bind(scheme, 'U');

        new ItemBuilder(Material.ARROW)
                .name("Scroll Down")
                .handler(event -> getComponent(ScrollbarComponent.class).ifPresent(ScrollbarComponent::scrollDown))
                .bind(scheme, 'D');
    }
}
