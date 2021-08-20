package com.xeloklox.dungeons.unleashed.utils;

import com.xeloklox.dungeons.unleashed.gen.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.util.*;
import org.json.*;

import java.util.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class RenderableModel{
    ModelJson modelJson;
    SpriteIdentifier sprite;
    RenderLayer renderlayer;
    public TexturedModelData texturedModelData;
    public RegisteredEntityModelLayer modelLayer;
    ModelTransform pivot;
    ModelPart modelPart;

    public RenderableModel(ModelJson modelJson,ModelTransform pivot){
        this.pivot=pivot;
        this.modelJson = modelJson;
        modelLayer = new RegisteredEntityModelLayer(modelJson.getName(),()->{
            try{
                return this.get();
            }catch(JSONException e){ }
            return null;
        });
        try{
            renderlayer = RenderLayer.getEntityCutoutNoCull(new Identifier(MODID,"textures/"+modelJson.config.getString("tex_0")+".png"));
        }catch(JSONException e){ }
    }
     public void init(Context context){
        modelPart = context.getLayerModelPart(modelLayer.get());
     }


    public TexturedModelData get() throws JSONException{
        JSONObject model = modelJson.getJson();
        JSONObject textures = model.getJSONObject("textures");
        Iterator<String> it = textures.keys();
        while(it.hasNext()){
            String p = it.next();
            if(p.equals("particle")){continue;}
            sprite= new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE,new Identifier(MODID,textures.getString(p)));
            break;
        }


        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartBuilder builder = new ModelPartBuilder();
        JSONArray elements = model.getJSONArray("elements");
        //.uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F)
        for(int i =0;i<elements.length();i++){
            JSONObject element = elements.getJSONObject(i);
            builder.uv(0,0);//todo Blockmodel ->  ModelData format UVs
            JSONArray from = element.getJSONArray("from");
            int farr[] = {from.getInt(0),from.getInt(1),from.getInt(2)};
            JSONArray to = element.getJSONArray("to");
            int tarr[] = {to.getInt(0),to.getInt(1),to.getInt(2)};
            builder.cuboid(farr[0]-pivot.pivotX, farr[1]-pivot.pivotY, farr[2]-pivot.pivotZ, tarr[0]-farr[0], tarr[1]-farr[1], tarr[2]-farr[2]);

        }
        modelPartData.addChild("base",builder, ModelTransform.pivot(0,0,0));

        if(model.has("texture_size")){
            JSONArray texture_size = model.getJSONArray("texture_size");
            texturedModelData = TexturedModelData.of(modelData, texture_size.getInt(0), texture_size.getInt(1));
        }else{
            texturedModelData =TexturedModelData.of(modelData, 16, 16);;
        }
        return texturedModelData;
    }

    public void render(MatrixStack matrices,VertexConsumerProvider vertexConsumerProvider, int light, int overlay){
        modelPart.render(matrices,vertexConsumerProvider.getBuffer(renderlayer),light,overlay);
    }


}
