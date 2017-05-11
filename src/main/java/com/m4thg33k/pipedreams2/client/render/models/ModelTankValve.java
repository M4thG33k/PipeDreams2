package com.m4thg33k.pipedreams2.client.render.models;

import java.util.HashMap;
import java.util.Map;

public class ModelTankValve extends ModelBaseMultiModel {

    public static final String DEFAULT = "default";
    public static final String PUSH = "push";
    public static final String PULL = "pull";
    public static final String NONE = "none";

    public ModelTankValve()
    {
        super();

        Map<String, String> keys = new HashMap<String, String>();
        keys.put(DEFAULT, "tank_valve");
        keys.put(PUSH, "tank_valve_push");
        keys.put(PULL, "tank_valve_pull");
        //// TODO: 5/11/2017 Add NONE to list

        createModelsWithTextureName("tank_valve", "#valve_texture", keys);
    }

    public void renderModel(int data)
    {
        switch (data)
        {
            case 1:
                renderModel(PUSH);
                break;
            case 2:
                renderModel(PULL);
                break;
            default:
                renderModel(DEFAULT);
        }
    }
}
