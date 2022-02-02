package com.sigmundgranaas.forgero.core.tool.toolpart.strategy.simple;

import com.sigmundgranaas.forgero.core.material.material.SecondaryMaterial;
import com.sigmundgranaas.forgero.core.material.material.simple.SimplePrimaryMaterial;
import com.sigmundgranaas.forgero.core.material.material.simple.SimpleSecondaryMaterial;

public class SimpleDuoMaterialToolPartStrategy extends SimpleToolPartStrategy {
    protected final SimpleSecondaryMaterial secondaryMaterial;

    public SimpleDuoMaterialToolPartStrategy(SimplePrimaryMaterial primaryMaterial, SimpleSecondaryMaterial secondaryMaterial) {
        super(primaryMaterial);
        this.secondaryMaterial = secondaryMaterial;
    }

    @Override
    public SecondaryMaterial getSecondaryMaterial() {
        return secondaryMaterial;
    }
}
