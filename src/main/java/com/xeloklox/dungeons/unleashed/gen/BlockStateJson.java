package com.xeloklox.dungeons.unleashed.gen;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.gen.BlockStateBuilder.*;
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
    Array<ModelJson> generatedJSONModels = new Array<>();

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
    public void pregenerate(){
        try{
            // generated and missing models are processed here.
            System.out.println("[ "+block.id+" ] -------------------------");
            for(String model : modelsToCheck){
                if(model.startsWith("@@")){
                    JSONObject config = new JSONObject(model.substring(2));
                    String templateName = config.getString("template");
                    if(!templateName.equals("dynamic")){
                        JSONObject template = BlockModelPresetBuilder.getTemplate(templateName);
                        generatedJSONModels.add(new ModelJson("block/" + config.getString("name"), template, config));
                    }
                    formatted.put(model, MODID + ":block/" + config.getString("name"));

                }else{
                    File file = new File(Paths.blockModel + model + ".json");
                    if(!file.exists()){
                        System.out.println("[WARNING] Unable to find model " + file + ", replacing with placeholder...");
                        JSONObject config = new JSONObject(BlockModelPresetBuilder.allSidesSame(model, "default").substring(2));
                        JSONObject template = BlockModelPresetBuilder.getTemplate(config.getString("template"));
                        generatedJSONModels.add(new ModelJson("block/"+model, template, config));
                    }
                    formatted.put(model, MODID + ":block/" + model);
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void fillJSONObj(){
        try{

            if(stateConfig.multipart){
                JSONArray parts = new JSONArray();
                stateConfig.map.forEach(e -> {
                    ModelMultipart multipart = (ModelMultipart)e.value;
                    try{
                        JSONObject part = new JSONObject();
                        JSONObject modelcpy = new JSONObject(multipart.apply.data, JSONObject.getNames(multipart.apply.data));
                        modelcpy.put("model", formatted.get(modelcpy.getString("model")));
                        if(!multipart.conditions.isEmpty()){
                            JSONObject conditions = new JSONObject();
                            if(multipart.or){
                                JSONArray conditionList = new JSONArray();
                                for(var cond:multipart.conditions){
                                    conditionList.put(cond.getJson());
                                }
                                conditions.put("OR",conditionList);
                            }else{
                                conditions = multipart.conditions.peek().getJson();
                            }
                            part.put("when",conditions);
                        }

                        part.put("apply",modelcpy);
                        parts.put(part);
                    }catch(JSONException ignored){ }
                });
                json.put("multipart", parts);
            }else{
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
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public void postGenerate(){

    }
}
