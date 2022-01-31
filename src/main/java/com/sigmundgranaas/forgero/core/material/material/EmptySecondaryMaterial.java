package com.sigmundgranaas.forgero.core.material.material;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class EmptySecondaryMaterial implements SecondaryMaterial {
    @Override
    public int getRarity() {
        return 0;
    }

    @Override
    public String getName() {
        return "empty";
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public MaterialType getType() {
        return null;
    }

    @Override
    public List<String> getProperties() {
        return null;
    }

    @Override
    public @NotNull
    List<Identifier> getPaletteIdentifiers() {
        return Collections.emptyList();
    }

    @Override
    public @NotNull
    List<Identifier> getPaletteExclusionIdentifiers() {
        return Collections.emptyList();
    }

    @Override
    public int getDurability() {
        return 0;
    }

    @Override
    public int getLuck() {
        return 0;
    }

    @Override
    public int getGrip() {
        return 0;
    }

    @Override
    public String getIngredientAsString() {
        return "empty";
    }
}
