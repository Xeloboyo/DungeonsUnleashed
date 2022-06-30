package com.xeloklox.dungeons.unleashed.utils.models;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.util.*;

import static com.xeloklox.dungeons.unleashed.ModInitClientServer.MODID;
import static com.xeloklox.dungeons.unleashed.utils.Utils.pixels;

public class RenderableModel{
    ModelJson modelJson;
    Func<Identifier,RenderLayer> renderlayer;
    Identifier texture;
    public TexturedModelData texturedModelData;
    public RegisteredEntityModelLayer modelLayer;
    ModelTransform pivot;
    public Array<Quad> quads = new Array<>(); // :D todo convert this to bones later.

    public ObjectMap<String,Bone> bonemap = new ObjectMap<>();
    public Array<Bone> topLevelBones = new Array<>(); // :D

    public RenderableModel(ModelJson modelJson,ModelTransform pivot){
        this(modelJson,pivot,RenderLayer::getEntityCutoutNoCull);
    }

    public RenderableModel(ModelJson modelJson, ModelTransform pivot, Func<Identifier,RenderLayer> renderlayer){
        this.pivot=pivot;
        this.modelJson = modelJson;
        modelLayer = new RegisteredEntityModelLayer(modelJson.getName(),()->{
            try{
                return this.get();
            }catch(JSONException e){
                System.err.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" + e.getLocalizedMessage()+ Arrays.toString(e.getStackTrace()));
            }
            return null;
        });
        try{
            texture = new Identifier(MODID,"textures/"+modelJson.config.getString("tex_0")+".png");
        }catch(JSONException e){ }
        this.renderlayer = renderlayer;
    }


    public TexturedModelData get() throws JSONException{
        modelJson.fillJSONObj();
        JSONObject model = modelJson.getJson();

        if(modelJson.isBedrock){
            return loadFromBedrockModel(model);
        }else{
            return loadFromJavaModel(model);
        }
    }

    public ObjectMap<String, BoneTranslationParameters> getParamMap(){
        ObjectMap<String, BoneTranslationParameters> map = new ObjectMap<>();
        bonemap.forEach(e->{
            map.put(e.key,new BoneTranslationParameters());
        });
        return map;
    }
    public Vector4f color = new Vector4f(1,1,1,1);


    public void setAlpha(float alpha){
        alpha = MathHelper.clamp(alpha,0,1);
        color.set(color.getX(),color.getY(),color.getZ(),alpha);
    }

    public void render(MatrixStack matrices,VertexConsumerProvider vertexConsumerProvider, int light, int overlay){
        VertexConsumer vc = vertexConsumerProvider.getBuffer(renderlayer.get(texture));
        if(modelJson.isBedrock){
            for(Bone b:topLevelBones){
                b.render(matrices,vc,light,overlay,color.getX(),color.getY(),color.getZ(),color.getW());
            }
        }else{
            MatrixStack.Entry entry = matrices.peek();
            for(int i = 0; i < quads.size; i++){
                renderQuad(entry, vc, quads.get(i), light, overlay,color.getX(),color.getY(),color.getZ(),color.getW());
            }
        }
    }
    public void render(MatrixStack matrices,VertexConsumerProvider vertexConsumerProvider, int light, int overlay, ObjectMap<String, BoneTranslationParameters> boneTranslationProvider){
        VertexConsumer vc = vertexConsumerProvider.getBuffer(renderlayer.get(texture));
        if(modelJson.isBedrock){
            for(Bone b:topLevelBones){
                b.render(matrices,vc,light,overlay,boneTranslationProvider,color.getX(),color.getY(),color.getZ(),color.getW());
            }
        }
    }
    static final Vec3f topLeftLight = (Vec3f)Util.make(new Vec3f(0.2F, -1.0F, -1.0F), Vec3f::normalize);
    static final Vec3f topDownLight = (Vec3f)Util.make(new Vec3f(0.2F, 0, 1.0F), Vec3f::normalize);

    public void render(MatrixStack matrices,ObjectMap<String, BoneTranslationParameters> boneTranslationProvider){
        matrices.push();
        matrices.translate(0.0D, 0.0D, 432.0D);
        matrices.scale(16,16,16);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion( -90));
        RenderSystem.setShaderLights(topLeftLight, topDownLight);
        RenderSystem.applyModelViewMatrix();
        EntityRenderDispatcher entityRenderDispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
        entityRenderDispatcher.setRenderShadows(false);
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
          RenderSystem.runAsFancy(() -> {
              render(matrices,immediate,15728880,OverlayTexture.DEFAULT_UV,boneTranslationProvider);
          });
        immediate.draw();
        entityRenderDispatcher.setRenderShadows(true);
        matrices.pop();
        DiffuseLighting.enableGuiDepthLighting();
    }

    public TexturedModelData loadFromBedrockModel(JSONObject modelOuter) throws JSONException{
        JSONObject model = modelOuter.getJSONArray("minecraft:geometry").getJSONObject(0);
        if(model==null){
            System.err.println(modelJson.getName()+" is empty bedrock model?????");
            return null;
        }
        JSONObject description = model.getJSONObject("description");
        int tw = description.has("texture_width")? description.getInt("texture_width"):16;
        int th = description.has("texture_height")? description.getInt("texture_height"):16;

        JSONArray bones = model.getJSONArray("bones");
        for(int i =0;i<bones.length();i++){
            JSONObject bonejson = bones.getJSONObject(i);

            float[] pivot = Utils.floatArray(bonejson.getJSONArray("pivot"));

            Bone bone = new Bone(bonejson.getString("name"),new Vec3f(pixels(pivot[0]),pixels(pivot[1]),pixels(pivot[2])));
            if(bonejson.has("rotation")){
                float[] rotation = Utils.floatArray(bonejson.getJSONArray("rotation"));
                bone.rotation.set(rotation[0],-rotation[1],-rotation[2]);
            }
            if(bonejson.has("cubes")){
                JSONArray cubes = bonejson.getJSONArray("cubes");
                for(int c =0;c<cubes.length();c++){
                    JSONObject cubejson = cubes.getJSONObject(c);
                    float[] pos = Utils.floatArray(cubejson.getJSONArray("origin"));
                    float[] size = Utils.floatArray(cubejson.getJSONArray("size"));
                    float[] rotation = {0,0,0};
                    if(cubejson.has("rotation")){
                        rotation = Utils.floatArray(cubejson.getJSONArray("rotation"));
                    }
                    float[] cubepivot = {0,0,0};
                    if(cubejson.has("pivot")){
                        cubepivot = Utils.floatArray(cubejson.getJSONArray("pivot"));
                    }
                    float [][] uvs = new float[6][4];
                    JSONObject facesUV = cubejson.getJSONObject("uv");
                    for(int d=0;d<6;d++){
                        Direction dr = Direction.byId(d);
                        if(!facesUV.has(dr.getName())){
                            continue;
                        }
                        JSONObject face = facesUV.getJSONObject(dr.getName());
                        JSONArray uv = face.getJSONArray("uv");
                        JSONArray uvsize = face.getJSONArray("uv_size");
                        if(d==Direction.EAST.getId() || d==Direction.WEST.getId()){ //
                            uvs[d][0] = uvsize.getInt(0) + uv.getInt(0);
                            uvs[d][2] = uv.getInt(0);
                            uvs[d][1] = uvsize.getInt(1) + uv.getInt(1);
                            uvs[d][3] = uv.getInt(1);
                        }else if( d==Direction.UP.getId() || d==Direction.DOWN.getId()){
                            uvs[d][2] = uvsize.getInt(0) +uv.getInt(0);//
                            uvs[d][0] = uv.getInt(0);
                            uvs[d][3] = uvsize.getInt(1) + uv.getInt(1);
                            uvs[d][1] = uv.getInt(1);
                        }else{
                            uvs[d][0] = uv.getInt(0);
                            uvs[d][2] = uvsize.getInt(0) + uv.getInt(0);
                            uvs[d][1] = uvsize.getInt(1) + uv.getInt(1);
                            uvs[d][3] = uv.getInt(1);
                        }

                    }
                    OrientedCuboid orientedCuboid = new OrientedCuboid(pos[0]-cubepivot[0],pos[1]-cubepivot[1],-((pos[2]+size[2])-cubepivot[2]),size[0],size[1],size[2],uvs,tw,th);
                    orientedCuboid.rotation.set(rotation[0],-rotation[1],-rotation[2]);
                    orientedCuboid.pivot.set(pixels(cubepivot[0]),pixels(cubepivot[1]),pixels(-cubepivot[2]));
                    bone.cubes.add(orientedCuboid);
                }
            }
            bonemap.put(bone.name,bone);
            if(bonejson.has("parent")){
                bonemap.get(bonejson.getString("parent")).children.add(bone);
            }else{
                this.topLevelBones.add(bone);
            }
        }

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartBuilder builder = new ModelPartBuilder();
        builder.uv(0,0);
        builder.cuboid(0,0,0,1,1,1);
        modelPartData.addChild("base",builder, ModelTransform.pivot(0,0,0));
        texturedModelData =TexturedModelData.of(modelData, tw, th);
        if(texturedModelData==null)
            System.err.println("TMD iS NULL");
        return texturedModelData;
    }

    public TexturedModelData loadFromJavaModel(JSONObject model) throws JSONException{
        float textureW=16, textureH=16;
        if(model.has("texture_size")){
            JSONArray texture_size = model.getJSONArray("texture_size");
            textureW = texture_size.getInt(0);
            textureH = texture_size.getInt(1);
        }

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartBuilder builder = new ModelPartBuilder();
        JSONArray elements = model.getJSONArray("elements");
        for(int i =0;i<elements.length();i++){
            JSONObject element = elements.getJSONObject(i);
            builder.uv(0,0);
            JSONArray from = element.getJSONArray("from");
            int farr[] = {from.getInt(0),from.getInt(1),from.getInt(2)};
            JSONArray to = element.getJSONArray("to");
            int tarr[] = {to.getInt(0),to.getInt(1),to.getInt(2)};
            builder.cuboid(farr[0]-pivot.pivotX, farr[1]-pivot.pivotY, farr[2]-pivot.pivotZ, tarr[0]-farr[0], tarr[1]-farr[1], tarr[2]-farr[2]);

            float [][] uvs = new float[6][4];
            JSONObject facesUV = element.getJSONObject("faces");
            for(int d=0;d<6;d++){
                Direction dr = Direction.byId(d);
                if(!facesUV.has(dr.getName())){
                    continue;
                }
                JSONObject face = facesUV.getJSONObject(dr.getName());
                JSONArray uv = face.getJSONArray("uv");
                uvs[d][0] = uv.getInt(0);
                uvs[d][1] = uv.getInt(1);
                uvs[d][2] = uv.getInt(2);
                uvs[d][3] = uv.getInt(3);
            }

            Cuboid cb = new Cuboid(farr[0]-pivot.pivotX, farr[1]-pivot.pivotY, farr[2]-pivot.pivotZ, tarr[0]-farr[0], tarr[1]-farr[1], tarr[2]-farr[2], uvs,textureW, textureH);
            quads.addAll(cb.sides);
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



    ///PAIN and suffering below

    public static class BoneTranslationParameters{
        public Vec3f rotation= new Vec3f(0,0,0);
        public Vec3f offset= new Vec3f(0,0,0);
        public Vec3f scale = new Vec3f(1,1,1);

        @Override
        public String toString(){
            return "BoneTranslationParameters{" +
            "rotation=" + rotation +
            ", offset=" + offset +
            ", scale=" + scale +
            '}';
        }
    }

    public static class Bone{
        public Vec3f pivot;
        public String name;
        public Bone parent;
        Array<Bone> children = new Array<>(false,4);
        Array<OrientedCuboid> cubes = new Array<>(false,4);

        public Vec3f rotation= new Vec3f(0,0,0);
        public Vec3f offset= new Vec3f(0,0,0);
        public Vec3f scale = new Vec3f(1,1,1);

        public Bone( String name,Vec3f pivot){
            this.pivot = pivot;
            this.name = name;
        }
        public void render(MatrixStack matrices, ObjectMap<String, BoneTranslationParameters> boneTranslationProvider){
            BoneTranslationParameters parameters = boneTranslationProvider.get(name);
            matrices.push();
            matrices.translate((parameters.offset.getX()+offset.getX()+pivot.getX())*16f, (parameters.offset.getY()+offset.getY()+pivot.getY())*16f,(parameters.offset.getZ()+offset.getZ()+pivot.getZ())*16f);
            matrices.scale(parameters.scale.getX()*scale.getX(),parameters.scale.getY()*scale.getY(),parameters.scale.getZ()*scale.getZ());
            matrices.multiply(Mathf.fromEulerDegXYZ(rotation.getX()+parameters.rotation.getX(),rotation.getY()+parameters.rotation.getY(),rotation.getZ()+parameters.rotation.getZ()));
            matrices.translate(-pivot.getX(), -pivot.getY(),-pivot.getZ());
            for(OrientedCuboid cube: cubes){
                cube.render(matrices);
            }
            for(Bone bone: children){
                bone.render(matrices,boneTranslationProvider);
            }
            matrices.pop();
        }
        public void render(MatrixStack matrices, VertexConsumer vc, int light, int overlay , ObjectMap<String, BoneTranslationParameters> boneTranslationProvider, float red, float green, float blue, float alpha){
            BoneTranslationParameters parameters = boneTranslationProvider.get(name);
            matrices.push();
            matrices.translate(parameters.offset.getX()+offset.getX()+pivot.getX(), parameters.offset.getY()+offset.getY()+pivot.getY(),parameters.offset.getZ()+offset.getZ()+pivot.getZ());
            matrices.scale(parameters.scale.getX()*scale.getX(),parameters.scale.getY()*scale.getY(),parameters.scale.getZ()*scale.getZ());
            matrices.multiply(Mathf.fromEulerDegXYZ(rotation.getX()+parameters.rotation.getX(),rotation.getY()+parameters.rotation.getY(),rotation.getZ()+parameters.rotation.getZ()));
            matrices.translate(-pivot.getX(), -pivot.getY(),-pivot.getZ());
            for(OrientedCuboid cube: cubes){
                cube.render(matrices,vc,light,overlay,red, green, blue, alpha);
            }
            for(Bone bone: children){
                bone.render(matrices,vc,light,overlay,boneTranslationProvider,red, green, blue, alpha);
            }
            matrices.pop();
        }
        public void render(MatrixStack matrices, VertexConsumer vc, int light, int overlay , float red, float green, float blue, float alpha){
            matrices.push();
            matrices.translate(offset.getX(), offset.getY(),offset.getZ());
            matrices.multiply(Quaternion.method_35823(rotation));
            for(OrientedCuboid cube: cubes){
                cube.render(matrices,vc,light,overlay,red, green, blue, alpha);
            }
            for(Bone bone: children){
                bone.render(matrices,vc,light,overlay,red, green, blue, alpha);
            }
            matrices.pop();
        }
    }

    public static class OrientedCuboid extends Cuboid{
        public Vec3f pivot = new Vec3f();
        public Vec3f rotation = new Vec3f(); //euler?

        public OrientedCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float[][] uvs, float textureWidth, float textureHeight){
            super(x, y, z, sizeX, sizeY, sizeZ, uvs, textureWidth, textureHeight);
        }

        public void render(MatrixStack matrices,VertexConsumer vc, int light, int overlay, float red, float green, float blue, float alpha){
            matrices.push();
            matrices.translate(pivot.getX(), pivot.getY(),pivot.getZ());
            matrices.multiply(Quaternion.method_35823(rotation));
            for(int i=0;i<sides.length;i++){
                renderQuad(matrices.peek(), vc,sides[i],light,overlay,red, green, blue, alpha);
            }
            matrices.pop();
        }
        public void render(MatrixStack matrices){
            matrices.push();
            matrices.translate(pivot.getX()*16f, pivot.getY()*16f,pivot.getZ()*16f);
            matrices.multiply(Quaternion.method_35823(rotation));
            for(int i=0;i<sides.length;i++){
                renderQuad(matrices.peek(), sides[i]);
            }
            matrices.pop();
        }
    }
    public static class Cuboid{
        final Quad[] sides = new Quad[6];
        public Cuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float[][] uvs,float textureWidth, float textureHeight) {
            float mx = x + sizeX;
            float my = y + sizeY;
            float mz = z + sizeZ;
            Vertex vertex = new Vertex(x, y, z, 0.0F, 0.0F);
            Vertex vertex2 = new Vertex(mx, y, z, 0.0F, 8.0F);
            Vertex vertex3 = new Vertex(mx, my, z, 8.0F, 8.0F);
            Vertex vertex4 = new Vertex(x, my, z, 8.0F, 0.0F);
            Vertex vertex5 = new Vertex(x, y, mz, 0.0F, 0.0F);
            Vertex vertex6 = new Vertex(mx, y, mz, 0.0F, 8.0F);
            Vertex vertex7 = new Vertex(mx, my, mz, 8.0F, 8.0F);
            Vertex vertex8 = new Vertex(x, my, mz, 8.0F, 0.0F);


            this.sides[0] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, uvs[0][0], uvs[0][1], uvs[0][2], uvs[0][3], textureWidth, textureHeight, false, Direction.DOWN);
            this.sides[1] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, uvs[1][0], uvs[1][1], uvs[1][2], uvs[1][3], textureWidth, textureHeight, false, Direction.UP);
            this.sides[2] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, uvs[2][0], uvs[2][1], uvs[2][2], uvs[2][3], textureWidth, textureHeight, false, Direction.NORTH);
            this.sides[3] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, uvs[3][0], uvs[3][1], uvs[3][2], uvs[3][3],  textureWidth, textureHeight, false, Direction.SOUTH);
            this.sides[4] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, uvs[4][0], uvs[4][1], uvs[4][2], uvs[4][3],  textureWidth, textureHeight, false, Direction.EAST);
            this.sides[5] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, uvs[5][0], uvs[5][1], uvs[5][2], uvs[5][3], textureWidth, textureHeight, false, Direction.WEST);
        }
    }


    public static void renderQuad(MatrixStack.Entry matrices,Quad quad){
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
         BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
         bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
         for(int i = 0; i < 4; ++i){
            bufferBuilder.vertex(matrices.getModel(), quad.vertices[i].pos.getX(), quad.vertices[i].pos.getY(), quad.vertices[i].pos.getZ()).texture(quad.vertices[i].u, quad.vertices[i].v).next();
         }
         bufferBuilder.end();
         BufferRenderer.draw(bufferBuilder);
    }

    public static void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, Quad quad,int light, int overlay){
        renderQuad(entry,vertexConsumer,quad,light,overlay,1f,1f,1f,1f);
    }
    public static void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, Quad quad,int light, int overlay, float red, float green, float blue, float alpha){
        if(quad.invisible){return;}
        Matrix4f matrix4f = entry.getModel();
        Matrix3f normal = entry.getNormal();
        Vec3f dirvec = quad.direction.copy();
        dirvec.transform(normal);
        float nx = dirvec.getX();
        float ny = dirvec.getY();
        float nz = dirvec.getZ();
        Vector4f vertexPos=new Vector4f();
        float[][] vertices = quad.calc;
        for(int index = 0; index < 4; ++index){
            vertexPos.set(vertices[index][0], vertices[index][1], vertices[index][2], 1.0F);
            vertexPos.transform(matrix4f);
            vertexConsumer.vertex(vertexPos.getX(), vertexPos.getY(), vertexPos.getZ(), red, green, blue, alpha, vertices[index][3], vertices[index][4], overlay, light, nx, ny, nz);
        }
    }

    static class Quad{
        public Vertex[] vertices;
        public Vec3f direction;
        public float[][] calc = new float[4][5]; // 4 * (x y z u v)
        boolean invisible = false;


        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction){
            this.vertices = vertices;
            if(u1 == u2 && v1 == v2){
                invisible = true;
            }
            vertices[0] = vertices[0].setUV(u2 / squishU, v1 / squishV);
            vertices[1] = vertices[1].setUV(u1 / squishU, v1 / squishV);
            vertices[2] = vertices[2].setUV(u1 / squishU, v2 / squishV);
            vertices[3] = vertices[3].setUV(u2 / squishU, v2 / squishV );
            if(flip){
                int i = vertices.length;

                for(int j = 0; j < i / 2; ++j){
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }

            this.direction = direction.getUnitVector();
            if(flip){
                this.direction.multiplyComponentwise(-1.0F, 1.0F, 1.0F);
            }
            recalc();
        }

        public void setVertices(Vertex[] vertices){
            this.vertices = vertices;
            recalc();
        }

        public void recalc(){
            for(int index = 0; index < vertices.length; ++index){
                Vertex vertex = vertices[index];
                float i = vertex.pos.getX() * 0.0625f;
                float j = vertex.pos.getY() * 0.0625f;
                float k = vertex.pos.getZ() * 0.0625f;
                calc[index][0] = i;
                calc[index][1] = j;
                calc[index][2] = k;
                calc[index][3] = vertex.u;
                calc[index][4] = vertex.v;
            }
        }
    }

    static class Vertex{
        public final Vec3f pos;
        public float u;
        public float v;

        public Vertex(float x, float y, float z, float u, float v){
            this(new Vec3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v){
            return new Vertex(this.pos, u, v);
        }

        public Vertex setUV(float u, float v){
            this.u=u;
            this.v=v;
            return this;
        }

        public Vertex(Vec3f pos, float u, float v){
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }






}
