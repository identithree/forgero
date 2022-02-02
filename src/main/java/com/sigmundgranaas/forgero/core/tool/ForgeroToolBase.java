package com.sigmundgranaas.forgero.core.tool;

import com.sigmundgranaas.forgero.core.identifier.ForgeroIdentifierFactory;
import com.sigmundgranaas.forgero.core.identifier.tool.ForgeroToolIdentifier;
import com.sigmundgranaas.forgero.core.material.material.PrimaryMaterial;
import com.sigmundgranaas.forgero.core.tool.toolpart.handle.ToolPartHandle;
import com.sigmundgranaas.forgero.core.tool.toolpart.head.ToolPartHead;
import net.minecraft.item.ToolMaterial;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ForgeroToolBase implements ForgeroTool {
    private final ToolPartHead head;
    private final ToolPartHandle handle;

    public ForgeroToolBase(ToolPartHead head, ToolPartHandle handle) {
        this.handle = handle;
        this.head = head;
    }

    @Override
    public @NotNull
    ToolPartHead getToolHead() {
        return head;
    }

    @Override
    public @NotNull
    PrimaryMaterial getPrimaryMaterial() {
        return head.getPrimaryMaterial();
    }

    @Override
    public @NotNull
    ToolPartHandle getToolHandle() {
        return handle;
    }

    @Override
    public @NotNull
    ForgeroToolIdentifier getIdentifier() {
        return (ForgeroToolIdentifier) ForgeroIdentifierFactory.INSTANCE.createForgeroIdentifier(getToolIdentifierString());
    }

    @Override
    public @NotNull
    String getShortToolIdentifierString() {
        return head.getPrimaryMaterial().getName() + "_" + head.getToolType().getToolName();
    }

    @Override
    public @NotNull
    String getToolIdentifierString() {
        return String.format("%s_%s", head.getPrimaryMaterial().getName(), getToolType().toString().toLowerCase(Locale.ROOT));
    }

    @Override
    public @NotNull
    ForgeroToolTypes getToolType() {
        return head.getToolType();
    }

    @Override
    public int getDurability() {
        return head.getDurability();
    }

    @Override
    public int getAttackDamage() {
        return 1;
    }

    @Override
    public float getAttackSpeed() {
        return 1f;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return 1f;
    }

    @Override
    public int getMiningLevel() {
        return head.getMiningLevel();
    }

    @Override
    public ToolMaterial getMaterial() {
        return (ToolMaterial) getPrimaryMaterial();
    }

    @Override
    public void createToolDescription(ToolDescriptionWriter writer) {
        writer.addHead(head);
        writer.addHandle(handle);
    }
}
