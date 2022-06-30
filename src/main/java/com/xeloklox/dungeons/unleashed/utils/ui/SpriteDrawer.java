package com.xeloklox.dungeons.unleashed.utils.ui;

import com.mojang.blaze3d.systems.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.VertexFormat.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;

import java.util.function.*;

public class SpriteDrawer{
    BufferBuilder bufferBuilder;
    boolean drawing = false;
    MatrixStack matrixStack;
    int c_texture = -1;
    public ScissorStack scissor;
    AnimatedScreen screen;
    VertexFormat.DrawMode drawMode = VertexFormat.DrawMode.QUADS;

    public SpriteDrawer(MatrixStack matrixStack,AnimatedScreen screen){
        this.matrixStack = matrixStack;
        scissor = new ScissorStack(screen);
        this.screen=screen;
    }

    public void reset(MatrixStack matrixStack){
        end();
        this.matrixStack=matrixStack;
    }
    public void resetTex(){
        interrupt(()->c_texture=-1);
    }

    public void begin(){
        if(drawing){
            return;
        }

        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        bufferBuilder= Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(drawMode, VertexFormats.POSITION_COLOR_TEXTURE);

        drawing = true;
    }

    public void setDrawMode(DrawMode drawMode){
        if(this.drawMode.equals(drawMode)){return;}
        end();
        this.drawMode = drawMode;
        begin();
    }

    public void end(){
        if(!drawing){
            return;
        }
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        bufferBuilder = null;
        drawing = false;
    }

    public BufferBuilder getBufferBuilder(){
        return bufferBuilder;
    }

    public void interrupt(Runnable runnable){
        if(drawing){
            end();
            runnable.run();
            begin();
        }else{
            runnable.run();
        }
    }

    public MatrixStack getMatrixStack(){
        return matrixStack;
    }

    public void setShader(Supplier<Shader> shader){
        interrupt(()->RenderSystem.setShader(shader));
    }
    public void setTexture(int id){
        if(c_texture==id){return;}
        interrupt(()->{
            RenderSystem.setShaderTexture(0, id);
            c_texture = id;
        });
    }
    public void rotate(float angle){
            getMatrixStack().multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(angle));
        }

    public void drawItem(ItemStack stack, float x, float y, float z){
        int tex = RenderSystem.getShaderTexture(0);
        end();
        screen.drawItem(getMatrixStack(),stack,x,y,z,stack.getCount()>1?stack.getCount()+"":"");
        RenderSystem.setShaderTexture(0,tex);
        RenderSystem.enableBlend();
        begin();
    }


    public float[] color = {1,1,1,1};

    public void setColor(float r,float g, float b ,float a){
        this.color[0]=r;
        this.color[1]=g;
        this.color[2]=b;
        this.color[3]=a;
    }
    public void setAlpha(float a){
        this.color[3]=a;
    }
    public void setColor(float a){
        setColor(a,a,a,1.0f);
    }
    public void resetColor(){
        setColor(1,1,1,1);
    }
    public void setColor(Vector4f a){
        setColor(a.getX(),a.getY(),a.getZ(),a.getW());
    }
    public void setColor(float g,float a){
        setColor(g,g,g,a);
    }
    public void setColor(float r,float g, float b ){
        setColor(r,g,b,1.0f);
    }

    private Vec2f initVertexPolygon = new Vec2f(0,0); Vec2f texLocPolygon = new Vec2f(0,0);
    private Vec2f pinitVertexPolygon = new Vec2f(0,0); Vec2f ptexLocPolygon = new Vec2f(0,0);
    private TextureRegion polygontex;
    private boolean initVertex = false;
    private boolean initpVertex = false;
    public void beginPolygon(TextureRegion tr){
        setDrawMode(DrawMode.TRIANGLES);
        polygontex = tr;
        setTexture(tr.getTexid());
    }
    public void polygonVertex(float x, float y, float u, float v){
        if(!initVertex){
            initVertexPolygon = new Vec2f(x,y);
            texLocPolygon = new Vec2f(u/polygontex.w,v/polygontex.h);
            initVertex = true;
            return;
        }
        if(!initpVertex){
            pinitVertexPolygon = new Vec2f(x,y);
            ptexLocPolygon = new Vec2f(u/polygontex.w,v/polygontex.h);
            initpVertex = true;
            return;
        }
        setTexture(polygontex.getTexid());
        vertex(initVertexPolygon.x,initVertexPolygon.y,texLocPolygon.x,texLocPolygon.y);
        vertex(pinitVertexPolygon.x,pinitVertexPolygon.y,ptexLocPolygon.x,ptexLocPolygon.y);
        vertex(x,y,u/polygontex.w,v/polygontex.h);
        pinitVertexPolygon = new Vec2f(x,y);
        ptexLocPolygon = new Vec2f(u/polygontex.w,v/polygontex.h);
    }
    private void vertex(float x, float y, float u, float v){
        bufferBuilder.vertex(getMatrixStack().peek().getModel(), x, y, 0).color(color[0],color[1],color[2],color[3]).texture(u, v).next();
    }
    public void endPolygon(){
        initVertex = false;
        initpVertex = false;
    }
    public void drawLine(float x, float y, float x2, float y2, float w){
        drawLine(UITextures.blanksquare,x,y,x2,y2,w);
    }
    public void drawLine(TextureRegion tr , float x, float y, float x2, float y2, float w){
        setDrawMode(DrawMode.QUADS);
        setTexture(tr.getTexid());
        float dx = x2-x;
        float dy = y2-y;
        float d = MathHelper.sqrt(dx*dx+dy+dy);
        dx*=w/d;
        dy*=w/d;
        vertex(x2-dy,y2+dx,tr.u, tr.v2);
        vertex(x2+dy,y2-dx,tr.u2, tr.v2);
        vertex(x+dy,y-dx,tr.u2, tr.v);
        vertex(x-dy,y+dx,tr.u, tr.v);

    }

    public void drawRect(float x, float y, float w, float h){
        draw(UITextures.blanksquare,x,y,    w,1);
        draw(UITextures.blanksquare,x,y+h-1,w,1);
        draw(UITextures.blanksquare,x,y,    1,h);
        draw(UITextures.blanksquare,x+w-1,y,1,h);
    }
    public void clipRelative(float x, float y, float w, float h){
        Vector4f p = new Vector4f(x,y,0,1);
        p.transform(getMatrixStack().peek().getModel());
        Vector4f p2 = new Vector4f(x+w,y+h,0,1);
        p2.transform(getMatrixStack().peek().getModel());
        interrupt(()->scissor.clip((int)p.getX(),(int)p.getY(),(int)(p2.getX()-p.getX()),(int)(p2.getY()-p.getY())));
    }
    public void clip(float x, float y, float w, float h){
        interrupt(()->scissor.clip((int)x + screen.ox,(int)y + screen.oy,(int)w,(int)h));
    }
    public void unclip(){
        interrupt(()->scissor.unclip());
    }
    public void drawCentered(TextureRegion tr, float x, float y){
        draw(tr,x-tr.w*0.5f,y-tr.h*0.5f,tr.w,tr.h);
    }
    public void draw(TextureRegion tr, float x, float y, float w, float h){
        setDrawMode(DrawMode.QUADS);
        setTexture(tr.getTexid());
        Matrix4f matrix = getMatrixStack().peek().getModel();
        bufferBuilder.vertex(matrix, x, y+h, 0).color(color[0],color[1],color[2],color[3]).texture(tr.u, tr.v2).next();
        bufferBuilder.vertex(matrix, x+w, y+h, 0).color(color[0],color[1],color[2],color[3]).texture(tr.u2, tr.v2).next();
        bufferBuilder.vertex(matrix, x+w, y, 0).color(color[0],color[1],color[2],color[3]).texture(tr.u2, tr.v).next();
        bufferBuilder.vertex(matrix, x, y, 0).color(color[0],color[1],color[2],color[3]).texture(tr.u, tr.v).next();
    }
    public void drawSection(TextureRegion tr, float x, float y, float rx, float ry,float rw, float rh){
        setDrawMode(DrawMode.QUADS);
        float au1 = MathHelper.clamp(Mathf.lerp(rx/tr.w,tr.u,tr.u2),tr.u,tr.u2);
        float au2 = MathHelper.clamp(Mathf.lerp((rx+rw)/tr.w,tr.u,tr.u2),tr.u,tr.u2);
        float av1 = MathHelper.clamp(Mathf.lerp(ry/tr.h,tr.v,tr.v2),tr.v,tr.v2);
        float av2 = MathHelper.clamp(Mathf.lerp((ry+rh)/tr.h,tr.v,tr.v2),tr.v,tr.v2);
        setTexture(tr.getTexid());
        Matrix4f matrix = getMatrixStack().peek().getModel();
        bufferBuilder.vertex(matrix, x, y+rh, 0).color(color[0],color[1],color[2],color[3]).texture(au1, av2).next();
        bufferBuilder.vertex(matrix, x+rw, y+rh, 0).color(color[0],color[1],color[2],color[3]).texture(au2, av2).next();
        bufferBuilder.vertex(matrix, x+rw, y, 0).color(color[0],color[1],color[2],color[3]).texture(au2, av1).next();
        bufferBuilder.vertex(matrix, x, y, 0).color(color[0],color[1],color[2],color[3]).texture(au1, av1).next();
    }

}
