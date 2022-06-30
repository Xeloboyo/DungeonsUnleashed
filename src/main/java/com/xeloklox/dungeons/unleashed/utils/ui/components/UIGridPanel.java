package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.utils.ui.*;
import org.mini2Dx.gdx.utils.*;


public class UIGridPanel extends UIContainer{
    private int cellwidth,cellheight;
    public boolean fillX=true,fillY=true;
    Array<Array<UIComponent>> matrix = new Array<>();
    public UIGridPanel(String id, AnimatedScreen screen){
        super( id,  screen);
    }
    public static UIGridPanel create(String id, AnimatedScreen screen){
        return new UIGridPanel(id,screen);
    }
    public void onRemove(UIComponent u){
        for(int i =0;i<cellwidth;i++){
            for(int j =0;j<cellheight;j++){
                if(matrix.get(j).size<=i){ continue;}
                if(matrix.get(j).get(i)==u){
                    matrix.get(j).removeIndex(i);
                    if(matrix.get(j).isEmpty()){
                        matrix.removeIndex(j);
                    }
                }
            }
        }
        rebuild();
    }


    public UIGridPanel setFill(boolean fillX,boolean fillY){
        this.fillX=fillX;
        this.fillY=fillY;
        return this;
    }

    @Override
    public void add(UIComponent u){
        matrix.peek().add(u);
        cellwidth = Math.max(cellwidth,matrix.peek().size);
        super.add(u);
    }



    public void row(){
        matrix.add(new Array<>());
        cellheight = matrix.size;
    }

    @Override
    public void resetMinSize(){
        minw = 0;
        minh = 0;
        float columnWidths[] = new float[cellwidth];
        float rowHeights[] = new float[cellheight];
        for(int i =0;i<cellwidth;i++){
            for(int j =0;j<cellheight;j++){
                if(matrix.get(j).size<=i){ continue;}
                UIComponent u = matrix.get(j).get(i);
                columnWidths[i] = Math.max(columnWidths[i],u.minw+u.marginl+u.marginr);
                rowHeights[j] = Math.max(rowHeights[j],u.minh+u.margint+u.marginb);
            }
        }
        float totalmw = 0;
        for(int i =0;i<cellwidth;i++){ totalmw += columnWidths[i];}
        float totalmh = 0;
        for(int i =0;i<cellheight;i++){ totalmh += rowHeights[i];}
        minw=totalmw+padl+padr;
        minh=totalmh+padt+padb;
    }

    @Override
    public void rebuild(){
        float columnWidths[] = new float[cellwidth];
        float rowHeights[] = new float[cellheight];

        for(int i =0;i<cellwidth;i++){
            for(int j =0;j<cellheight;j++){
                if(matrix.get(j).size<=i){ continue;}
                UIComponent u = matrix.get(j).get(i);
                columnWidths[i] = Math.max(columnWidths[i],u.minw+u.marginl+u.marginr);
                rowHeights[j] = Math.max(rowHeights[j],u.minh+u.margint+u.marginb);
            }
        }
        float totalmw = 0;
        for(int i =0;i<cellwidth;i++){ totalmw += columnWidths[i];}
        float totalmh = 0;
        for(int i =0;i<cellheight;i++){ totalmh += rowHeights[i];}
        float leftx = w-totalmw-padl-padr;
        float lefty = h-totalmh-padb-padt;
        if(this.fillX){
            for(int i = 0; i < cellwidth; i++){
                columnWidths[i] += leftx / (float)cellwidth;
            }
        }else if(cellwidth>0){
            columnWidths[columnWidths.length-1] +=leftx;
        }
        if(this.fillY){
            for(int i = 0; i < cellheight; i++){
                rowHeights[i] += lefty / (float)cellheight;
            }
        }else if(cellheight>0){
            rowHeights[rowHeights.length-1] +=lefty;
        }
        float cx = padl;
        for(int i =0;i<cellwidth;i++){
            float cy = padt;
            for(int j =0;j<cellheight;j++){
                if(i<matrix.get(j).size){
                    UIComponent u = matrix.get(j).get(i);
                    u.x=cx;
                    u.y=cy;
                    u.fitInto(cx,cy,columnWidths[i],rowHeights[j]);
                }
                cy+=rowHeights[j];
            }
            cx+=columnWidths[i];
        }
        super.rebuild();
    }


}
