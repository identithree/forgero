package com.sigmundgranaas.forgero.registry;

import com.sigmundgranaas.forgero.item.ForgeroToolItem;
import com.sigmundgranaas.forgero.item.ItemCollection;
import com.sigmundgranaas.forgero.item.ToolPartItem;
import com.sigmundgranaas.forgero.item.tool.ForgeroPickaxeItem;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class ItemRegistryImpl implements ItemRegistry {
    private static ItemRegistry INSTANCE;
    private final ItemCollection collection;

    public ItemRegistryImpl(ItemCollection collection) {
        this.collection = collection;
    }

    public static ItemRegistry getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ItemRegistryImpl(ItemCollection.INSTANCE);
        }
        return INSTANCE;
    }

    @Override
    public void registerToolPart(Item part) {
        Registry.register(Registry.ITEM, ((ToolPartItem) part).getIdentifier(), part);
    }

    @Override
    public void registerTool(Item tool) {
        Registry.register(Registry.ITEM, ((ForgeroToolItem) tool).getIdentifier(), tool);
    }


    @Override
    public void registerTools() {
        collection.INSTANCE.getTools().forEach(this::registerTool);
        List<Item> pickaxes = collection.INSTANCE.getTools().stream().filter(item -> item instanceof ForgeroPickaxeItem).collect(Collectors.toList());
        //ToolManagerImpl.tag(FabricToolTags.PICKAXES).register(new ModdedToolsVanillaBlocksToolHandler(pickaxes));

    }

    @Override
    public void registerToolParts() {
        collection.INSTANCE.getToolParts().forEach(this::registerToolPart);
    }
}
