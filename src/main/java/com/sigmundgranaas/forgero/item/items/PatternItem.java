package com.sigmundgranaas.forgero.item.items;

import com.sigmundgranaas.forgero.core.pattern.Pattern;
import com.sigmundgranaas.forgero.core.property.Property;
import com.sigmundgranaas.forgero.core.property.attribute.Target;
import com.sigmundgranaas.forgero.item.adapter.DescriptionWriter;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PatternItem extends Item {
    private final Pattern pattern;

    public PatternItem(Settings settings, Pattern pattern) {
        super(settings);

        this.pattern = pattern;
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        new DescriptionWriter(tooltip).writePatternDescription(getPattern());
        new DescriptionWriter(tooltip).addToolPartProperties(Property.stream(pattern.getProperties(Target.createEmptyTarget())));
    }

    public Pattern getPattern() {
        return pattern;
    }
}
