package com.m4thg33k.pipedreams2.client.render.models;

import com.m4thg33k.pipedreams2.client.render.TankItemOverrideHandler;
import com.m4thg33k.pipedreams2.tiles.TilePortTank;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class TankItemModel implements IPerspectiveAwareModel {

    private static ModelManager modelManager = null;
    private final IBakedModel baseModel;

    private String fluidName;
    private int size;

    protected double capacity = (double) TilePortTank.CAPACITY;

    public TankItemModel(IBakedModel baseModel)
    {
        this.baseModel = new TankItemBakedModel().getModel();
    }

    public IBakedModel handleItemState(ItemStack stack)
    {
        if (stack.hasTagCompound())
        {
            NBTTagCompound compound = stack.getTagCompound();
            fluidName = compound.getString("FluidName");

            int amount = compound.getInteger("Amount");

            size = 0;
            for (double x=0; x < amount/capacity; x += 0.25)
            {
                size += 1;
            }
        }

        return this;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        // copied from SG

        Matrix4f matrix = new Matrix4f();
        switch (cameraTransformType) {
            case FIRST_PERSON_RIGHT_HAND:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().firstperson_right);
                matrix.setTranslation(new javax.vecmath.Vector3f(-0.25f, 0.5f, -0.5f));
                matrix.setScale(1.0f);
                break;
            case FIRST_PERSON_LEFT_HAND:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().firstperson_left);
                break;
            case GUI:
//                matrix.setTranslation(new javax.vecmath.Vector3f(1.0f, 1.0f, 10.0f));
                matrix.mul(ForgeHooksClient.getMatrix(getItemCameraTransforms().gui));
//                matrix.setTranslation(new javax.vecmath.Vector3f(0.5f, 0.5f, 0.5f));
//                matrix.rotY(3.14159f/2f);
                matrix.rotX(1.5f);
                matrix.rotY(0.5f);
                matrix.rotZ(0.8f);
                matrix.setTranslation(new javax.vecmath.Vector3f(0.0f, 0.65f, 0.5f));
                matrix.setScale(0.85f);
                break;
            case HEAD:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().head);
                break;
            case THIRD_PERSON_RIGHT_HAND:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().thirdperson_right);
                matrix.rotX(1.2f);
                matrix.setTranslation(new javax.vecmath.Vector3f(0.25f, 0.0f, 0.6f));
                matrix.setScale(0.5f);
                break;
            case THIRD_PERSON_LEFT_HAND:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().thirdperson_left);
                matrix.rotX(1.2f);
                matrix.setTranslation(new javax.vecmath.Vector3f(-0.25f, 0.0f, 0.6f));
                matrix.setScale(0.5f);
                break;
            case GROUND:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().ground);
                matrix.setTranslation(new javax.vecmath.Vector3f(0.25f, 0.35f, 0.25f));
                matrix.setScale(matrix.getScale() * 0.5f);
                break;
            case FIXED:
                matrix = ForgeHooksClient.getMatrix(getItemCameraTransforms().fixed);
            default:
                break;
        }
        return Pair.of((IBakedModel) this, matrix);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        quads.addAll(baseModel.getQuads(state, side, rand));
        if (size != 0)
        {
            quads.addAll(SphereItemModels.getSphereFromFluidName(fluidName,size).getQuads(state, side, rand));
        }
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    private static ItemTransformVec3f thirdPersonLeft = null;
    private static ItemTransformVec3f thirdPersonRight = null;
    private static ItemTransformVec3f firstPersonLeft = null;
    private static ItemTransformVec3f firstPersonRight = null;

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        // copied straight from SG (removing unneeded katana info)

        // if (thirdPersonLeft == null) {
        Vector3f rotation = new Vector3f();
        Vector3f translation = new Vector3f();
        Vector3f scale = new Vector3f(1f, 1f, 1f);

        // Third Person
        rotation = new Vector3f(0f, (float) -Math.PI / 2f, (float) Math.PI * 7f / 36f); // (0, 90, -35)
        translation = new Vector3f(0f, 5.5f, 2.5f);
        translation.scale(0.0625f);
        // scale = new Vector3f(0.85f, 0.85f, 0.85f);
        thirdPersonRight = new ItemTransformVec3f(rotation, translation, scale);

        rotation = new Vector3f(0f, (float) Math.PI / 2f, (float) -Math.PI * 7f / 36f); // (0, 90, -35)
        // translation = new Vector3f(0f, 5.5f, 2.5f);
        // if (tool != null && tool.getItem() == ModItems.katana) {
        // translation.y += 2.0f;
        // }
        // translation.scale(0.0625f);
        // scale = new Vector3f(0.85f, 0.85f, 0.85f);
        thirdPersonLeft = new ItemTransformVec3f(rotation, translation, scale);

        // First Person
        // rotation = new Vector3f(0f, (float) -Math.PI * 3f / 4f, (float) Math.PI * 5f / 36f); // (0, -135, 25)
        rotation = new Vector3f(0f, (float) -Math.PI * 1f / 2f, (float) Math.PI * 5f / 36f);
        translation = new Vector3f(1.13f, 3.2f, 1.13f);
        translation.scale(0.0625f);
        scale = new Vector3f(0.68f, 0.68f, 0.68f);
        firstPersonRight = new ItemTransformVec3f(rotation, translation, scale);

        rotation = new Vector3f(0f, (float) Math.PI * 1f / 2f, (float) -Math.PI * 5f / 36f);
        firstPersonLeft = new ItemTransformVec3f(rotation, translation, scale);
        // }

        // Head and GUI are default.
        return new ItemCameraTransforms(thirdPersonLeft, thirdPersonRight, firstPersonLeft,
                firstPersonRight, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT,
                ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
    }

    @Override
    public ItemOverrideList getOverrides() {
        return TankItemOverrideHandler.INSTANCE;
    }
}
