package com.m4thg33k.pipedreams2.core.lib.registry;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.List;

public interface IRegObject {

    void addRecipes();
    void addOreDict();

    String getName();
    String getFullName();

    List<ModelResourceLocation> getVariants();
    boolean registerModels();
}
