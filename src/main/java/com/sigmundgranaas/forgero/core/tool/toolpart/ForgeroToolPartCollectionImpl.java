package com.sigmundgranaas.forgero.core.tool.toolpart;

import com.sigmundgranaas.forgero.core.material.MaterialCollection;
import com.sigmundgranaas.forgero.core.tool.toolpart.factory.ForgeroToolPartFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
public class ForgeroToolPartCollectionImpl implements ForgeroToolPartCollection {
    public static ForgeroToolPartCollection INSTANCE;
    private final List<ForgeroToolPart> toolParts;

    public ForgeroToolPartCollectionImpl(List<ForgeroToolPart> toolParts) {
        this.toolParts = toolParts;
    }

    public static ForgeroToolPartCollection getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ForgeroToolPartCollectionImpl(ForgeroToolPartFactory.INSTANCE.createBaseToolParts(MaterialCollection.INSTANCE));
        }
        return INSTANCE;
    }


    @Override
    public @NotNull
    List<ForgeroToolPart> getToolParts() {
        return toolParts;
    }

    @Override
    public @NotNull
    List<ToolPartHandle> getHandles() {
        return getToolPartsSubtype(ToolPartHandle.class);
    }

    @Override
    public @NotNull
    List<ToolPartBinding> getBindings() {
        return getToolPartsSubtype(ToolPartBinding.class);

    }

    @Override
    public @NotNull
    List<ToolPartHead> getHeads() {
        return getToolPartsSubtype(ToolPartHead.class);

    }

    private @NotNull <T> List<T> getToolPartsSubtype(Class<T> type) {
        return toolParts.stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }
}
