package com.xeloklox.dungeons.unleashed.utils.ui;

import com.xeloklox.dungeons.unleashed.utils.lambda.*;
import net.minecraft.util.math.*;

public class MarchingSquaresBackground{
    TextureRegion tr;
    RegionNineSlice slice;

    float [][] grid;
    float originx,originy;
    float gw,gh;
    float totalw,totalh;

    public VertexFormatter using;

    static final VertexFormatter defaultFormatter = new VertexFormatter(){
        @Override
        public void vertex(MarchingSquaresBackground mb,SpriteDrawer sb, float x, float y){
            sb.polygonVertex(x*mb.gw,y*mb.gh,x*mb.gw,y*mb.gh);
        }
    };
    static float[] g = new float[2];
    static final VertexFormatter NineSliceFormatter = new VertexFormatter(){
        @Override
        public void vertex(MarchingSquaresBackground mb,SpriteDrawer sb, float x, float y){
            mb.slice.getUV(x*mb.gw,y*mb.gh,mb.totalw,mb.totalh,g);
            sb.polygonVertex(x*mb.gw,y*mb.gh,g[0]*mb.tr.w,g[1]*mb.tr.h);
        }
    };


    public MarchingSquaresBackground(TextureRegion tr, int sizew,int sizeh){
        this.tr = tr;
        grid = new float[sizew][sizeh];
        gw = tr.w/sizew;
        gh = tr.h/sizeh;
        totalw = gw*sizew;
        totalh = gh*sizeh;
        using = defaultFormatter;
    }
    public MarchingSquaresBackground(RegionNineSlice tr,float w, float h, int sizew,int sizeh){
        this.tr = tr.baseSlice;
        grid = new float[sizew][sizeh];
        gw = w/sizew;
        gh = h/sizeh;
        totalw = w;
        totalh = h;
        slice = tr;
        using = NineSliceFormatter;
    }
    public void resizeLarger(float w, float h){
        if(w>totalw || h>totalh){
            float[][] newgrid = new float[Math.max(grid.length,(int)Math.ceil(w/gw + 1))][Math.max(grid[0].length,(int)Math.ceil(h/gh  + 1))];
            for(int i = 0;i<grid.length;i++){
                for(int j = 0;j<grid[0].length;j++){
                    newgrid[i][j] = grid[i][j];
                }
            }
            grid = newgrid;
        }
        this.totalw = w;
        this.totalh = h;
    }

    public void draw(SpriteDrawer sb,float x,float y,float w,float h){
        sb.getMatrixStack().push();
        sb.getMatrixStack().translate(-originx,-originy, 0);
        int sx = MathHelper.clamp((int)((x+originx)/gw),0,grid.length-2);
        int sy = MathHelper.clamp((int)((y+originy)/gh),0,grid[0].length-2);
        int sx2 = MathHelper.clamp((int)((x+originx+w)/gw),0,grid.length-2);
        int sy2 = MathHelper.clamp((int)((y+originy+h)/gh),0,grid[0].length-2);
        for(int i = sx;i<=sx2;i++){
            for(int j = sy;j<=sy2;j++){
                drawCell(sb,i,j);
            }
        }
        sb.getMatrixStack().pop();
    }
    public interface TileAffector{
        float get(float rx,float ry, float original);
    }
    public void affectAll(TileAffector a,float x,float y){
        float ax = (x+originx)/gw;
        float ay = (y+originy)/gh;
        for(int i = 0;i<grid.length;i++){
            for(int j = 0;j<grid[0].length;j++){
                grid[i][j] =  a.get(i - ax, j - ay,grid[i][j]);
            }
        }
    }
    public void affectArea(TileAffector a,float x,float y, float  r){
        int sx = MathHelper.clamp((int)((x+originx)/gw -r),0,grid.length-1);
        int sy = MathHelper.clamp((int)((y+originy)/gh -r),0,grid[0].length-1);
        int sx2 = MathHelper.clamp((int)((x+originx)/gw +r),0,grid.length-1);
        int sy2 = MathHelper.clamp((int)((y+originy)/gh +r),0,grid[0].length-1);
        float ax = (x+originx)/gw;
        float ay = (y+originy)/gh;
        for(int i = sx;i<=sx2;i++){
            for(int j = sy;j<=sy2;j++){
                grid[i][j] =  a.get(i - ax, j - ay,grid[i][j]);
            }
        }

    }


    static final int FORWARD = 1;
    static final int BACK = 0;
    static final float dirs[][][] = {
        {{0,-1},{1,0}},
        {{-1,0},{0,-1}},
        {{0,1},{-1,0}},
        {{1,0},{0,1}},
    };
    static final float cellpos[][] = {
        {0,1},
        {1,1},
        {1,0},
        {0,0},
    };

    public void drawCell(SpriteDrawer sb, int x,int y){
        float[] corner = {grid[x][y+1],grid[x+1][y+1],grid[x+1][y],grid[x][y]};
        if(corner[0]<1 && corner[1]<1 && corner[2]<1 && corner[3]<1 ){
           return;
        }
        sb.beginPolygon(this.tr);
        for(int i=0;i<4;i++){
            int prev = (i+3)%4;
            int next = (i+1)%4;
            if(corner[i]<1){continue;}
            if(corner[prev]<1){
                float prop = (corner[i]-1)/(corner[i]-1 + (1-corner[prev]));
                vertex(sb,cellpos[i][0]+x + dirs[i][BACK][0]*prop,cellpos[i][1]+y + dirs[i][BACK][1]*prop);
            }
            vertex(sb,cellpos[i][0]+x,cellpos[i][1]+y);
            if(corner[next]<1){
                float prop = (corner[i]-1)/(corner[i]-1 + (1-corner[next]));
                vertex(sb,cellpos[i][0]+x + dirs[i][FORWARD][0]*prop,cellpos[i][1]+y + dirs[i][FORWARD][1]*prop);
            }
        }
        sb.endPolygon();

    }

    private void vertex(SpriteDrawer sb,float x,float y){
        using.vertex(this,sb,x,y);
    }


    public void setOrigin(float originx,float originy){
        this.originx = originx;
        this.originy = originy;
    }
     interface VertexFormatter{
         void vertex(MarchingSquaresBackground mb,SpriteDrawer sb,float x,float y);
     }

    public float getTotalWidth(){
        return totalw;
    }

    public float getTotalHeight(){
        return totalh;
    }

    public float getGridWidth(){
        return gw;
    }

    public float getGridHeight(){
        return gh;
    }
}
