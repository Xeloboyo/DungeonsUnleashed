package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.io.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

// this is only run in preprocessing.
public class BlockStateJson extends JsonConfiguration{
    RegisteredBlock block;
    BlockStateBuilder stateConfig;
    private final Array<String> modelsToCheck = new Array<>();
    private final ObjectMap<String, String> formatted = new ObjectMap<>();
    Array<BlockModelJson> generatedModels = new Array<>();

    public BlockStateJson(RegisteredBlock block, BlockStateBuilder preset){
        super(Paths.blockState + block.id + ".json", new JSONObject());
        this.block = block;
        this.stateConfig = preset;
        stateConfig.map.forEach(e -> {
            e.value.eachModelVariant(model -> {
                modelsToCheck.add(model.getModel());
            });
        });

    }

    @Override
    public void fillJSONObj(){
        try{
            // generated and missing models are processed here.
            for(String model : modelsToCheck){
                if(model.startsWith("@@")){
                    JSONObject config = new JSONObject(model.substring(2));
                    JSONObject template = BlockModel.getTemplate(config.getString("template"));
                    generatedModels.add(new BlockModelJson(config.getString("name"), template, config));
                    formatted.put(model, MODID + ":block/" + config.getString("name"));

                }else{
                    File file = new File(Paths.blockModel + model + ".json");
                    if(!file.exists()){
                        System.out.println("[WARNING] Unable to find model " + file + ", replacing with placeholder...");
                        JSONObject config = new JSONObject(BlockModel.allSidesSame(model, "default").substring(2));
                        JSONObject template = BlockModel.getTemplate(config.getString("template"));
                        generatedModels.add(new BlockModelJson(model, template, config));
                    }
                    formatted.put(model, MODID + ":block/" + model);
                }
            }

            JSONObject variants = new JSONObject();
            stateConfig.map.forEach(e -> {
                JSONArray variant = new JSONArray();
                e.value.eachModelVariant(model -> {
                    try{
                        JSONObject modelcpy = new JSONObject(model.data, JSONObject.getNames(model.data));
                        modelcpy.put("model", formatted.get(modelcpy.getString("model")));
                        variant.put(modelcpy);
                    }catch(JSONException e2){
                    }
                });
                try{
                    variants.put(e.key, variant);
                }catch(JSONException e2){
                }
            });
            json.put("variants", variants);
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void postGenerate(){

    }
}
