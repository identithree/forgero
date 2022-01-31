package com.sigmundgranaas.forgero.core.tool.toolpart;

import com.sigmundgranaas.forgero.Constants;
import com.sigmundgranaas.forgero.core.identifier.tool.ForgeroMaterialIdentifierImpl;
import com.sigmundgranaas.forgero.core.material.MaterialCollection;
import com.sigmundgranaas.forgero.core.material.material.PrimaryMaterial;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PickaxeHeadTest {
    public static PickaxeHead createDefaultPickaxeHead() {
        return new PickaxeHead((PrimaryMaterial) MaterialCollection.INSTANCE.getMaterial(new ForgeroMaterialIdentifierImpl(Constants.IRON_IDENTIFIER_STRING)));
    }


    @Test
    void getToolTypeName() {
        Assertions.assertEquals("pickaxe", createDefaultPickaxeHead().getToolTypeName());
    }

    @Test
    void getToolPartName() {
        Assertions.assertEquals("pickaxehead", createDefaultPickaxeHead().getToolPartName());
    }
}