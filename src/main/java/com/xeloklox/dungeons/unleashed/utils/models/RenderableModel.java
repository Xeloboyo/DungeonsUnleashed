package com.xeloklox.dungeons.unleashed.utils.models;

import com.xeloklox.dungeons.unleashed.gen.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.*;
import net.minecraft.client.util.math.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.json.*;
import org.mini2Dx.gdx.utils.*;

import java.util.*;

import static com.xeloklox.dungeons.unleashed.DungeonsUnleashed.MODID;

public class RenderableModel{
    ModelJson modelJson;
    SpriteIdentifier sprite;
    RenderLayer renderlayer;
    public TexturedModelData texturedModelData;
    public RegisteredEntityModelLayer modelLayer;
    ModelTransform pivot;
    public Array<Quad> quads = new Array<>(); // :D

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
            builder.uv(0,0);//todo Blockmodel ->  ModelData format UVs
            JSONArray from = element.getJSONArray("from");
            int farr[] = {from.getInt(0),from.getInt(1),from.getInt(2)};
            JSONArray to = element.getJSONArray("to");
            int tarr[] = {to.getInt(0),to.getInt(1),to.getInt(2)};
            builder.cuboid(farr[0]-pivot.pivotX, farr[1]-pivot.pivotY, farr[2]-pivot.pivotZ, tarr[0]-farr[0], tarr[1]-farr[1], tarr[2]-farr[2]);

            float [][] uvs = new float[6][4];
            JSONObject facesUV = element.getJSONObject("faces");
            for(int d=0;d<6;d++){
                Direction dr = Direction.byId(d);
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

    public void render(MatrixStack matrices,VertexConsumerProvider vertexConsumerProvider, int light, int overlay){
        MatrixStack.Entry entry = matrices.peek();
        VertexConsumer vc = vertexConsumerProvider.getBuffer(renderlayer);
        for(int i=0;i<quads.size;i++){
            renderQuad(entry, vc,quads.get(i),light,overlay);
        }
    }



    ///PAIN and suffering below


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


    public void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, Quad quad,int light, int overlay){
        renderQuad(entry,vertexConsumer,quad,light,overlay,1f,1f,1f,1f);
    }
    public void renderQuad(MatrixStack.Entry entry, VertexConsumer vertexConsumer, Quad quad,int light, int overlay, float red, float green, float blue, float alpha){
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


        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction){
            this.vertices = vertices;
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
