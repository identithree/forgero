package com.sigmundgranaas.forgero.core.gem;

public class EmptyGem extends ForgeroGem implements HeadGem, HandleGem, BindingGem {
    public EmptyGem(int gemLevel, String identifier) {
        super(gemLevel, identifier);
    }

    public static EmptyGem createEmptyGem() {
        return new EmptyGem(1, "empty_gem");
    }

    @Override
    public float applyAttackDamage(float currentDamage) {
        return currentDamage;
    }

    @Override
    public float applyMiningSpeedMultiplier(float currentMiningSpeed) {
        return currentMiningSpeed;
    }

    @Override
    public int applyDurability(int currentDurability) {
        return currentDurability;
    }

    @Override
    public ForgeroGem createNewGem(int level, String Identifier) {
        return new EmptyGem(level, Identifier);
    }

    @Override
    public boolean equals(Gem newGem) {
        return newGem instanceof EmptyGem;
    }

    @Override
    public GemTypes getType() {
        return GemTypes.EMPTY;
    }

    @Override
    public Gem createGem(int level) {
        return createEmptyGem();
    }

}