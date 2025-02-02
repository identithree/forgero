package com.sigmundgranaas.forgero.item.implementation;

import com.sigmundgranaas.forgero.core.ForgeroRegistry;
import com.sigmundgranaas.forgero.core.data.v1.pojo.ModelPojo;
import com.sigmundgranaas.forgero.core.data.v1.pojo.PropertyPojo;
import com.sigmundgranaas.forgero.core.gem.EmptyGem;
import com.sigmundgranaas.forgero.core.gem.Gem;
import com.sigmundgranaas.forgero.core.material.material.PrimaryMaterial;
import com.sigmundgranaas.forgero.core.property.*;
import com.sigmundgranaas.forgero.core.property.active.ActivePropertyBuilder;
import com.sigmundgranaas.forgero.core.property.active.BreakingDirection;
import com.sigmundgranaas.forgero.core.property.attribute.AttributeBuilder;
import com.sigmundgranaas.forgero.core.property.passive.PassivePropertyBuilder;
import com.sigmundgranaas.forgero.core.property.passive.PassivePropertyType;
import com.sigmundgranaas.forgero.core.schematic.HeadSchematic;
import com.sigmundgranaas.forgero.core.schematic.Schematic;
import com.sigmundgranaas.forgero.core.tool.ForgeroTool;
import com.sigmundgranaas.forgero.core.tool.ForgeroToolTypes;
import com.sigmundgranaas.forgero.core.tool.ForgeroToolWithBinding;
import com.sigmundgranaas.forgero.core.tool.factory.ForgeroToolFactory;
import com.sigmundgranaas.forgero.core.toolpart.ForgeroToolPart;
import com.sigmundgranaas.forgero.core.toolpart.ForgeroToolPartTypes;
import com.sigmundgranaas.forgero.core.toolpart.binding.ToolPartBinding;
import com.sigmundgranaas.forgero.core.toolpart.factory.ForgeroToolPartFactory;
import com.sigmundgranaas.forgero.core.toolpart.factory.ToolPartBuilder;
import com.sigmundgranaas.forgero.core.toolpart.handle.ToolPartHandle;
import com.sigmundgranaas.forgero.core.toolpart.head.ToolPartHead;
import com.sigmundgranaas.forgero.core.util.ForgeroDefaults;
import com.sigmundgranaas.forgero.item.ForgeroToolItem;
import com.sigmundgranaas.forgero.item.NBTFactory;
import com.sigmundgranaas.forgero.item.ToolPartItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static com.sigmundgranaas.forgero.core.identifier.Common.ELEMENT_SEPARATOR;

public class NBTFactoryImpl implements NBTFactory {

    public static NBTFactory INSTANCE;
    private final Map<String, ForgeroTool> toolCache;

    public NBTFactoryImpl() {
        this.toolCache = new HashMap<>();
    }

    public static NBTFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NBTFactoryImpl();
        }
        return INSTANCE;
    }

    public static String createGemNbtString(Gem gem) {
        return String.format("%s%s%s", gem.getLevel(), ELEMENT_SEPARATOR, gem.getStringIdentifier());
    }

    @Override
    public @NotNull
    ForgeroToolPart createToolPartFromNBT(@NotNull NbtCompound compound) {
        String primaryMaterialString = compound.getString(ToolPartItem.PRIMARY_MATERIAL_IDENTIFIER);
        PrimaryMaterial primary = ForgeroRegistry.MATERIAL.getPrimaryMaterial(primaryMaterialString).orElse(ForgeroDefaults.getDefaultPrimaryMaterial());
        String secondaryMaterialString = compound.getString(ToolPartItem.SECONDARY_MATERIAL_IDENTIFIER);

        String gemString = compound.getString(NBTFactory.GEM_NBT_IDENTIFIER);


        String toolPartTypeIdentifier = compound.getString(NBTFactory.TOOL_PART_TYPE_NBT_IDENTIFIER);
        ForgeroToolPartTypes toolPartTypes = ForgeroToolPartTypes.valueOf(toolPartTypeIdentifier.toUpperCase(Locale.ROOT));
        String patternIdentifier;
        ForgeroToolTypes toolType = ForgeroToolTypes.PICKAXE;
        if (compound.contains(NBTFactory.TOOL_PART_HEAD_TYPE_NBT_IDENTIFIER)) {
            String toolTypeIdentifier = compound.getString(NBTFactory.TOOL_PART_HEAD_TYPE_NBT_IDENTIFIER);
            toolType = ForgeroToolTypes.valueOf(toolTypeIdentifier.toUpperCase(Locale.ROOT));
        }

        if (compound.contains(NBTFactory.SCHEMATIC_NBT_IDENTIFIER)) {
            patternIdentifier = compound.getString(NBTFactory.SCHEMATIC_NBT_IDENTIFIER);
        } else {
            if (toolPartTypes == ForgeroToolPartTypes.HEAD) {
                patternIdentifier = String.format("%s%s%spattern_default", toolType.getToolName(), toolPartTypeIdentifier.toLowerCase(Locale.ROOT), ELEMENT_SEPARATOR);
            } else {
                patternIdentifier = String.format("%s%spattern_default", toolPartTypeIdentifier.toLowerCase(Locale.ROOT), ELEMENT_SEPARATOR);
            }

        }

        Schematic pattern = ForgeroRegistry.SCHEMATIC.getResource(patternIdentifier).get();

        ToolPartBuilder builder = switch (toolPartTypes) {
            case HANDLE -> ForgeroToolPartFactory.INSTANCE.createToolPartHandleBuilder(primary, pattern);
            case BINDING -> ForgeroToolPartFactory.INSTANCE.createToolPartBindingBuilder(primary, pattern);
            case HEAD -> ForgeroToolPartFactory.INSTANCE.createToolPartHeadBuilder(primary, (HeadSchematic) pattern);
        };
        if (!secondaryMaterialString.equals("empty")) {
            builder.setSecondary(ForgeroRegistry.MATERIAL.getSecondaryMaterial(secondaryMaterialString).get());
        }


        if (!gemString.equals("")) {
            builder.setGem(getGemFromNbtString(gemString));
        }


        return builder.createToolPart();
    }

    @Override
    public @NotNull
    ForgeroTool createToolFromNBT(@NotNull ForgeroToolItem baseTool, @NotNull NbtCompound compound) {
        NbtCompound toolCompound;
        if (compound.contains(NBTFactory.FORGERO_TOOL_NBT_IDENTIFIER)) {
            toolCompound = compound.getCompound(NBTFactory.FORGERO_TOOL_NBT_IDENTIFIER);
        } else {
            toolCompound = compound;
        }
        assert toolCompound != null;

        Optional<String> hash = Optional.of(toolCompound).map(toolCompounds -> toolCompounds.getString(NBTFactory.HASH_NBT_IDENTIFIER));
        if (hash.isPresent() && toolCache.containsKey(hash.get())) {
            return toolCache.get(hash.get());
        }
        ToolPartHead head;
        ToolPartHandle handle;

        if (toolCompound.contains(ToolPartItem.HEAD_IDENTIFIER)) {
            head = (ToolPartHead) createToolPartFromNBT(toolCompound.getCompound(ToolPartItem.HEAD_IDENTIFIER));
        } else {
            head = baseTool.getHead();
        }

        if (toolCompound.contains(ToolPartItem.HANDLE_IDENTIFIER)) {
            handle = (ToolPartHandle) createToolPartFromNBT(toolCompound.getCompound(ToolPartItem.HANDLE_IDENTIFIER));
        } else {
            handle = baseTool.getHandle();
        }

        Optional<ToolPartBinding> binding = Optional.empty();
        if (toolCompound.contains(ToolPartItem.BINDING_IDENTIFIER)) {
            binding = Optional.of((ToolPartBinding) createToolPartFromNBT(toolCompound.getCompound(ToolPartItem.BINDING_IDENTIFIER)));
        }
        ForgeroTool tool;
        if (binding.isPresent()) {
            tool = ForgeroToolFactory.INSTANCE.createForgeroTool(head, handle, binding.get());
        } else {
            tool = ForgeroToolFactory.INSTANCE.createForgeroTool(head, handle);
        }
        return tool;
    }

    @Override
    public @NotNull
    NbtCompound createNBTFromTool(@NotNull ForgeroTool baseTool) {
        NbtCompound baseCompound = new NbtCompound();
        baseCompound.put(HEAD_NBT_IDENTIFIER, createNBTFromToolPart(baseTool.getToolHead()));
        baseCompound.put(HANDLE_NBT_IDENTIFIER, createNBTFromToolPart(baseTool.getToolHandle()));
        if (baseTool instanceof ForgeroToolWithBinding) {
            baseCompound.put(BINDING_NBT_IDENTIFIER, createNBTFromToolPart(((ForgeroToolWithBinding) baseTool).getBinding()));
        }
        String hash = UUID.randomUUID().toString();
        baseCompound.putString(HASH_NBT_IDENTIFIER, hash);
        toolCache.put(hash, baseTool);
        return baseCompound;
    }

    @Override
    public @NotNull
    NbtCompound createNBTFromToolPart(@NotNull ForgeroToolPart toolPart) {
        NbtCompound baseCompound = new NbtCompound();
        baseCompound.putString(PRIMARY_MATERIAL_NBT_IDENTIFIER, toolPart.getPrimaryMaterial().getResourceName());
        baseCompound.putString(SECONDARY_MATERIAL_NBT_IDENTIFIER, toolPart.getSecondaryMaterial().getResourceName());
        baseCompound.putString(GEM_NBT_IDENTIFIER, createGemNbtString(toolPart.getGem()));
        baseCompound.putString(TOOL_PART_TYPE_NBT_IDENTIFIER, toolPart.getToolPartType().toString());
        baseCompound.putString(TOOL_PART_IDENTIFIER, toolPart.getToolPartIdentifier());
        baseCompound.putString(SCHEMATIC_NBT_IDENTIFIER, toolPart.getSchematic().getSchematicIdentifier());
        if (toolPart.getToolPartType() == ForgeroToolPartTypes.HEAD) {
            baseCompound.putString(TOOL_PART_HEAD_TYPE_NBT_IDENTIFIER, ((ToolPartHead) toolPart).getToolType().toString());
        }
        return baseCompound;
    }

    @Override
    public @NotNull
    Gem createGemFromNbt(@NotNull NbtCompound compound) {
        String gemString = compound.getString(NBTFactory.GEM_NBT_IDENTIFIER);
        if (!gemString.equals("")) {
            return getGemFromNbtString(gemString);
        } else {
            return EmptyGem.createEmptyGem();
        }
    }

    @Override
    public @NotNull
    NbtCompound createNBTFromGem(@NotNull Gem gem, NbtCompound compound) {
        compound.putString(NBTFactoryImpl.GEM_NBT_IDENTIFIER, createGemNbtString(gem));
        compound.putFloat("CustomModelData", gem.getLevel());
        return compound;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public @NotNull Optional<Schematic> createSchematicFromNbt(@NotNull NbtCompound compound) {
        if (compound.contains(SCHEMATIC_NBT_IDENTIFIER)) {
            if (compound.get(SCHEMATIC_NBT_IDENTIFIER).getType() == NbtElement.STRING_TYPE) {
                return ForgeroRegistry.SCHEMATIC.getResource(compound.getString(SCHEMATIC_NBT_IDENTIFIER));
            } else if (compound.get(SCHEMATIC_NBT_IDENTIFIER).getType() == NbtElement.COMPOUND_TYPE) {
                NbtCompound schematicCompound = compound.getCompound(SCHEMATIC_NBT_IDENTIFIER);
                ForgeroToolPartTypes type = ForgeroToolPartTypes.valueOf(schematicCompound.getString("Type"));
                String name = schematicCompound.getString("Name");
                NbtCompound modelCompound = schematicCompound.getCompound("Model");
                ModelPojo model = new ModelPojo();
                model.primary = modelCompound.getString("Primary");
                model.secondary = modelCompound.getString("Secondary");
                model.gem = modelCompound.getString("Gem");
                boolean unique = schematicCompound.getBoolean("Unique");
                int materialCount = schematicCompound.getInt("MaterialCount");
                List<Property> properties = createPropertiesFromNbt(schematicCompound.getCompound("Properties"));

                if (type == ForgeroToolPartTypes.HEAD) {
                    ForgeroToolTypes toolType = ForgeroToolTypes.valueOf(schematicCompound.getString("ToolType"));
                    return Optional.of(new HeadSchematic(type, name, properties, toolType, null, materialCount, unique));
                }
                return Optional.of(new Schematic(type, name, properties, null, materialCount, unique));
            }
        }
        return Optional.empty();
    }

    @Override
    public @NotNull List<Property> createPropertiesFromNbt(@NotNull NbtCompound compound) {
        if (compound.contains(NBTFactory.PROPERTY_IDENTIFIER)) {
            var properties = new ArrayList<Property>();
            var propertyCompound = compound.getCompound(NBTFactory.PROPERTY_IDENTIFIER);
            if (propertyCompound.contains(NBTFactory.ATTRIBUTES_IDENTIFIER)) {
                NbtList list = propertyCompound.getList(ATTRIBUTES_IDENTIFIER, NbtElement.COMPOUND_TYPE);
                properties.addAll(createPropertyFromNbt(list, comp -> createAttributeFromNbt(compound)));
            }
            if (propertyCompound.contains(NBTFactory.ACTIVE_IDENTIFIER)) {
                NbtList list = propertyCompound.getList(ACTIVE_IDENTIFIER, NbtElement.COMPOUND_TYPE);
                properties.addAll(createPropertyFromNbt(list, comp -> createActivePropertyFromNbt(compound)));
            }
            if (propertyCompound.contains(NBTFactory.PASSIVE_IDENTIFIER)) {
                NbtList list = propertyCompound.getList(PASSIVE_IDENTIFIER, NbtElement.COMPOUND_TYPE);
                properties.addAll(createPropertyFromNbt(list, comp -> createPassivePropertyFromNbt(compound)));
            }
            return properties;
        }
        return Collections.emptyList();
    }

    private Property createPassivePropertyFromNbt(NbtCompound compound) {
        var pojo = new PropertyPojo.Passive();
        pojo.type = PassivePropertyType.valueOf(compound.getString("Type"));
        pojo.tag = compound.getString("Tag");
        return PassivePropertyBuilder.createPassivePropertyFromPojo(pojo);
    }

    private Property createActivePropertyFromNbt(NbtCompound compound) {
        var pojo = new PropertyPojo.Active();
        pojo.type = ActivePropertyType.valueOf(compound.getString("Type"));
        pojo.tag = compound.getString("Tag");
        pojo.depth = compound.getInt("Depth");
        pojo.description = compound.getString("Description");
        pojo.direction = BreakingDirection.valueOf("Direction");
        pojo.pattern = compound.getList("Pattern", NbtElement.STRING_TYPE).stream().map(NbtElement::asString).toList().toArray(new String[0]);
        return ActivePropertyBuilder.createAttributeFromPojo(pojo);
    }

    private Collection<Property> createPropertyFromNbt(NbtList list, Function<NbtCompound, Property> converter) {
        var properties = new ArrayList<Property>();
        for (int i = 0; i < list.size(); i++) {
            properties.add(converter.apply(list.getCompound(i)));
        }
        return properties;
    }

    private Attribute createAttributeFromNbt(NbtCompound compound) {
        var pojo = new PropertyPojo.Attribute();
        pojo.value = compound.getFloat("Value");
        pojo.operation = NumericOperation.valueOf(compound.getString("Operation"));
        pojo.order = CalculationOrder.valueOf(compound.getString("Order"));
        pojo.type = AttributeType.valueOf(compound.getString("Type"));
        if (compound.contains("Condition")) {
            var conditionCompound = compound.getCompound("Condition");
            pojo.condition = new PropertyPojo.Condition();
            pojo.condition.target = TargetTypes.valueOf(conditionCompound.getString("target"));
            NbtList list = conditionCompound.getList("Tag", NbtElement.STRING_TYPE);
            pojo.condition.tag = new ArrayList<>(list.stream().map(NbtElement::asString).toList());
        }
        return AttributeBuilder.createAttributeFromPojo(pojo);
    }


    @Override
    public @NotNull NbtCompound createNbtFromProperties(@NotNull PropertyPojo properties) {
        NbtCompound compound = new NbtCompound();
        if (properties.passiveProperties.size() > 0) {
            NbtList list = new NbtList();
            properties.passiveProperties.forEach(passive -> list.add(createPassiveNbtCompound(passive)));
            compound.put(PASSIVE_IDENTIFIER, list);
        }
        if (properties.active.size() > 0) {
            NbtList list = new NbtList();
            properties.active.forEach(active -> list.add(createActiveNbtCompound(active)));
            compound.put(ACTIVE_IDENTIFIER, list);
        }
        if (properties.attributes.size() > 0) {
            NbtList list = new NbtList();
            properties.attributes.forEach(attribute -> list.add(createAttributeNbtCompound(attribute)));
            compound.put(ATTRIBUTES_IDENTIFIER, list);
        }

        return compound;
    }

    private NbtElement createAttributeNbtCompound(PropertyPojo.Attribute attribute) {
        NbtCompound compound = new NbtCompound();
        compound.putString("Type", attribute.type.toString());
        compound.putFloat("Value", attribute.value);
        compound.putString("Operation", attribute.operation.toString());
        compound.putString("Order", attribute.order.toString());
        if (attribute.condition != null) {
            NbtCompound condition = new NbtCompound();
            compound.putString("Target", attribute.condition.target.toString());
            NbtList list = new NbtList();
            attribute.condition.tag.forEach(tag -> list.add(NbtString.of(tag)));
            compound.put("Tag", list);
            compound.put("Condition", condition);
        }
        return compound;
    }

    private NbtElement createActiveNbtCompound(PropertyPojo.Active active) {
        NbtCompound compound = new NbtCompound();
        compound.putString("Tag", active.tag);
        compound.putString("Type", active.type.toString());
        applyIfNotNull(active.depth, () -> compound.putInt("Depth", active.depth));
        applyIfNotNull(active.description, () -> compound.putString("Description", active.description));
        var list = new NbtList();
        Arrays.stream(active.pattern).forEach(string -> list.add(NbtString.of(string)));
        applyIfNotNull(active.pattern, () -> compound.put("Pattern", list));
        applyIfNotNull(active.direction, () -> compound.putString("Direction", active.direction.toString()));
        return compound;
    }

    public void applyIfNotNull(@Nullable Object test, Runnable action) {
        if (test != null) {
            action.run();
        }
    }

    private NbtElement createPassiveNbtCompound(PropertyPojo.Passive passive) {
        NbtCompound compound = new NbtCompound();
        compound.putString("Tag", passive.tag);
        compound.putString("Type", passive.type.toString());
        return compound;
    }

    @Override
    public @NotNull NbtCompound createNBTFromSchematic(@NotNull Schematic schematic) {
        NbtCompound schematicCompound = new NbtCompound();
        schematicCompound.putString("Name", schematic.getResourceName());
        schematicCompound.putInt("MaterialCount", schematic.getMaterialCount());
        NbtCompound modelCompound = new NbtCompound();
        modelCompound.putString("Primary", schematic.getModelContainer().getModel().primary());
        modelCompound.putString("Secondary", schematic.getModelContainer().getModel().secondary());
        modelCompound.putString("Gem", schematic.getModelContainer().getModel().gem());
        schematicCompound.put("Model", modelCompound);
        schematicCompound.put("Property", createNbtFromProperties(Property.pojo(schematic.getProperties())));
        if (schematic instanceof HeadSchematic headSchematic) {
            schematicCompound.putString("ToolType", headSchematic.getToolType().toString());
        }
        schematicCompound.putString("Type", schematic.getType().toString());
        return schematicCompound;
    }

    Gem getGemFromNbtString(String nbtGem) {
        String[] elements = nbtGem.split(ELEMENT_SEPARATOR);
        if (elements.length < 3) {
            return EmptyGem.createEmptyGem();
        }
        Gem gem = ForgeroRegistry.GEM.getResource(String.format("%s%s%s", elements[1], ELEMENT_SEPARATOR, elements[2])).orElse(EmptyGem.createEmptyGem());
        return gem.createGem(Integer.parseInt(elements[0]));
    }
}

