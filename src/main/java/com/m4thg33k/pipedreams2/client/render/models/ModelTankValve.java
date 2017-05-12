package com.m4thg33k.pipedreams2.client.render.models;

import com.m4thg33k.pipedreams2.core.enums.EnumFluidConnectionType;

import java.util.HashMap;
import java.util.Map;

public class ModelTankValve extends ModelBaseMultiModel {

    public static final String STR_DEFAULT = "default";
    public static final String STR_PUSH = "push";
    public static final String STR_PULL = "pull";
    public static final String STR_NONE = "none";

    public ModelTankValve()
    {
        super();

        Map<String, String> keys = new HashMap<String, String>();
        keys.put(STR_DEFAULT, "tank_valve");
        keys.put(STR_PUSH, "tank_valve_push");
        keys.put(STR_PULL, "tank_valve_pull");
        //// TODO: 5/11/2017 Add STR_NONE to list

        createModelsWithTextureName("tank_valve", "#valve_texture", keys);
    }

    public void renderModel(EnumFluidConnectionType type)
    {
        switch (type)
        {
            case PUSH:
                renderModel(STR_PUSH);
                break;


        }
    }

    public void renderModel(int data)
    {
        if (data < 0 || data >= EnumFluidConnectionType.values().length)
        {
            return;
        }
        this.renderModel(EnumFluidConnectionType.values()[data]);
    }
}
