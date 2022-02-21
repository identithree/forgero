package com.sigmundgranaas.forgero.core.toolpart.factory;

import com.sigmundgranaas.forgero.core.gem.BindingGem;
import com.sigmundgranaas.forgero.core.gem.Gem;
import com.sigmundgranaas.forgero.core.material.material.PrimaryMaterial;
import com.sigmundgranaas.forgero.core.toolpart.binding.Binding;
import com.sigmundgranaas.forgero.core.toolpart.binding.BindingState;
import com.sigmundgranaas.forgero.core.toolpart.binding.ToolPartBinding;

public class ToolPartBindingBuilder extends ToolPartBuilder {
    public ToolPartBindingBuilder(PrimaryMaterial primary) {
        super(primary);
    }

    public ToolPartBindingBuilder(ToolPartBinding toolPart) {
        super(toolPart);
    }

    @Override
    public ToolPartBuilder setGem(Gem newGem) {
        if (newGem instanceof BindingGem) {
            gem = newGem;
        }
        return this;
    }

    @Override
    public Binding createToolPart() {
        return new Binding(new BindingState(primary, secondary, (BindingGem) gem));
    }
}