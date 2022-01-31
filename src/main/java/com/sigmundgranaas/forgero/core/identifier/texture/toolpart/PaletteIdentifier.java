package com.sigmundgranaas.forgero.core.identifier.texture.toolpart;

import com.sigmundgranaas.forgero.core.identifier.texture.TextureIdentifier;
import com.sigmundgranaas.forgero.core.identifier.tool.ForgeroMaterialIdentifier;

public record PaletteIdentifier(
        ForgeroMaterialIdentifier material) implements TextureIdentifier {

    @Override
    public String getFileNameWithExtension() {
        return getFileNameWithoutExtension() + ".png";
    }

    @Override
    public String getFileNameWithoutExtension() {
        return getIdentifier() + "_palette";
    }

    @Override
    public String getIdentifier() {
        return material.getName();
    }


}
