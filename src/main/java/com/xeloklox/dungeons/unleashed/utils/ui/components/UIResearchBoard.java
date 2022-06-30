package com.xeloklox.dungeons.unleashed.utils.ui.components;

import com.xeloklox.dungeons.unleashed.*;
import com.xeloklox.dungeons.unleashed.utils.*;
import com.xeloklox.dungeons.unleashed.utils.animation.Interpolations.*;
import com.xeloklox.dungeons.unleashed.utils.block.entity.*;
import com.xeloklox.dungeons.unleashed.utils.ui.*;
import com.xeloklox.dungeons.unleashed.utils.ui.components.UIFlexContainer.*;
import com.xeloklox.dungeons.unleashed.utils.ui.particles.*;
import net.fabricmc.fabric.api.client.networking.v1.*;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.*;
import net.minecraft.screen.slot.*;
import net.minecraft.text.*;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import org.mini2Dx.gdx.utils.*;

public class UIResearchBoard extends UIDragPanContainer{
    public boolean drawRunes = true;
    HiddenInventory hiddenInventory;
    ObjectMap<String, ResearchItemUI> researchItems = new ObjectMap<>();
    MarchingSquaresBackground bg;
    MarchingSquaresBackground defaultbg;
    UIParticleSystem particles = new UIParticleSystem();

    public final float itemsZ = 20;
    public final float focusedWindowZ = 10;
    public final float defaultZ = 1;

    UIWindow front = null;
    Array<ProgressLine> linesegs = new Array<>();
    ///animation
    boolean decaying = false;



    public UIResearchBoard(String id, AnimatedScreen screen,HiddenInventory hiddenInventory, MarchingSquaresBackground bg){
        super(id, screen);
        this.hiddenInventory=hiddenInventory;
        this.bg=bg;
    }

    public static UIResearchBoard create(String id, AnimatedScreen screen,HiddenInventory hiddenInventory, MarchingSquaresBackground bg){
        return new UIResearchBoard(id, screen,hiddenInventory,bg);
    }

    @Override
    public void resetIdealSize(){

    }

    public void recalcItemProg(){
        ObjectMap.Entries<String, ResearchItemUI> iterator = new ObjectMap.Entries<> (researchItems);
        for(var ri:iterator){
            ResearchItemUI ritem = ri.value;
            ritem.progress=0;
            if(ritem.window.disabled || ritem.window.minimised || !ritem.appeared  || ritem.unlocked){
                continue;
            }
            var con = ritem.window.children.get(UISimpleContainer.class,"itemdepowrap");
            if(con!=null){
                Vec2f pos = con.getAbsolutePos(false).add(new Vec2f(-x,-y));
                Array<ItemStack> remaining = new Array<>(false,ritem.item.itemRequirement.size);
                float total = 0;
                for(var i: ritem.item.itemRequirement){
                    ItemStack itemStack = new ItemStack(Registry.ITEM.get(i.getItem()),i.getAmount());
                    remaining.add(itemStack);
                    total += itemStack.getCount();
                }
                IntArray slots = new IntArray();
                for(var c:children.components){
                    if(c instanceof UILooseItem uiLooseItem){
                        if(c.x>pos.x && c.x+c.w<pos.x+con.w && c.y>pos.y && c.y+c.h<pos.y+con.h){
                            for(int i=0;i<remaining.size;i++){
                                if(uiLooseItem.stack.getItem().equals(remaining.get(i).getItem())){
                                    remaining.get(i).decrement(uiLooseItem.stack.getCount());
                                    if(remaining.get(i).isEmpty()){
                                        remaining.removeIndex(i);
                                    }
                                    slots.add(uiLooseItem.id);
                                    break;
                                }
                            }
                        }
                    }
                }
                float left = 0;
                for(var i: remaining){
                    left += i.getCount();
                }
                ritem.progress=1f-(left/total);
                if(left==0){
                    if(!ritem.goingToAdvance){
                        ritem.beginAdvance(slots);
                    }
                }else if(ritem.goingToAdvance){
                    ritem.goingToAdvance=false;
                }
            }
        }
    }

    @Override
    public void onChildFocused(UIComponent c){
        if(c instanceof UIWindow window){
            if(front!=null){
                front.setZ(defaultZ);
            }
            front = window;
            c.setZ(focusedWindowZ);
        }
    }

    @Override
    public void drawPannedBg(SpriteDrawer sb){
        if(drawRunes){
            float spacing = 50;
            for(int sx = (int)Math.floor(-panx/spacing); sx*spacing<-panx+w; sx++){
                for(int sy = (int)Math.floor(-pany/spacing); sy*spacing<-pany+h; sy++){
                    int rune = Math.abs(sx+sy)%UITextures.insetrunes.length;
                    sb.draw(UITextures.insetrunes[rune],sx*spacing-3,sy*spacing-3,7,7);
                }
            }
        }
        if(defaultbg!=null){
            defaultbg.draw(sb, -panx, -pany, w, h);
        }
        bg.draw(sb,-panx,-pany,w,h);
        for(var line:linesegs){
            line.draw(sb);
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button){
        boolean b =  super.mouseClicked(mouseX, mouseY, button);
        recalcItemProg();
        return b;
    }

    @Override
    public void onClickSurface(float mouseX, float mouseY, int button){
        int slot = hiddenInventory.freeSlot();
        if(button==1 && slot!=-1 && !handler.getCursorStack().isEmpty()){
            add(new UILooseItem("item" + (itemuiid++), new ItemStack(handler.getCursorStack().getItem(),1), 15, slot)
            .setPositioning((float)mouseX - panx - 8, (float)mouseY - pany + 15 - 8).setZ(itemsZ));
            handler.getCursorStack().decrement(1);
            handler.screen.getClient().interactionManager.clickSlot(handler.getScreenHandler().syncId, slot, button, SlotActionType.PICKUP, handler.screen.getClient().player);
        }
    }

    private int itemuiid = 0;
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button){
        boolean releasedChild =  super.mouseReleased(mouseX, mouseY, button);
        int slot = hiddenInventory.freeSlot();
        if(!releasedChild && slot!=-1 && !handler.getCursorStack().isEmpty() && button==0){
            add(new UILooseItem("item" + (itemuiid++), handler.getCursorStack(), 15, slot)
            .setPositioning((float)mouseX - panx - 8, (float)mouseY - pany + 15 - 8).setZ(itemsZ));
            handler.setCursorStack(ItemStack.EMPTY);
            handler.screen.getClient().interactionManager.clickSlot(handler.getScreenHandler().syncId, slot, button, SlotActionType.PICKUP, handler.screen.getClient().player);
            recalcItemProg();
            return true;
        }
        recalcItemProg();
        return releasedChild;
    }

    public void clearItems(){
        for(var c:children.components){
            if(c instanceof UILooseItem u){
                u.directRemove();
            }
        }
        recalcItemProg();
    }

    public void add(ResearchItemUI researchItemUI, float x, float y){
        researchItems.put(researchItemUI.item.name, researchItemUI);
        researchItemUI.generateUI(this.children.screen);
        researchItemUI.researchBoard=this;
        researchItemUI.node.setPositioning(x,y);
        add(researchItemUI.node);
        researchItemUI.window.disabled=true;
        researchItemUI.window.closing=true;
        add(researchItemUI.window);
        if(researchItemUI.item.children.isEmpty()){
           // researchItemUI.appear();
        }
    }

    public void tabletUpdated(ItemStack tablet){
        System.out.println("Tablet: "+tablet.toString());
        if(tablet.isEmpty() || !tablet.getItem().equals(ModItems.TABLET_OF_KNOWLEDGE.get())){
            decaying = true;
            for(var line : linesegs){
                line.decay=true;
            }
            for(var c:researchItems){
                var item = c.value;
                item.disappear();
            }
            System.out.println("Tablet was empty....");
        }else{
            bg.affectAll((rx, ry, original) -> Mathf.getRandFromPoint((int)rx,(int)ry),0,0);
            System.out.println("Tablet wasnt empty....");
            decaying = false;
            var researched = tablet.getSubNbt("research");
            for(String s:researched.getKeys()){
                for(ResearchItem r:ResearchItem.researchItems){
                    if(r.getNameID().equals(s)){
                        int level = researched.getInt(s);
                        var unlocked = researchItems.get(r.getName());
                        if(unlocked!=null){
                            System.out.println("Unlocked:"+unlocked.item.name);
                            unlocked.appear();
                            unlocked.advance(level);
                        }
                        break;
                    }
                }
            }
        }
    }

    public void addRelative(String relative, ResearchItemUI researchItemUI, float x, float y){
        add(researchItemUI,researchItems.get(relative).node.left + x,researchItems.get(relative).node.top + y);
    }


    float t = 0;
    @Override
    public void update(){
        super.update();
        if(decaying){
            bg.affectAll((rx, ry, original) -> Mathf.lerpTowards(original,Mathf.getRandFromPoint((int)rx,(int)ry),0.1f),0,0);
            defaultbg.affectAll((rx, ry, original) -> {
                float dis = 1f/(rx*rx+ry*ry);
                return original+dis;
            },0,0);
        }else{
            defaultbg.affectAll((rx, ry, original) -> Mathf.lerpTowards(original,Mathf.getRandFromPoint((int)rx,(int)ry),0.2f),0,0);
        }


        for(var line : linesegs){
            line.update();
        }
        for(int i=0;i<linesegs.size;i++){
           if(linesegs.get(i).decay && linesegs.get(i).progress<=0){
                linesegs.removeIndex(i);
                i--;
           }
        }
        t++;
        for(var c:researchItems){
            var item = c.value;
            if(!item.window.disabled){
                item.waitbar.setProgress(Mathf.lerpTowards(item.waitbar.getProgress(), c.value.progress, 0.1f));
                if(item.goingToAdvance){
                    item.advancedelay-=1;
                    if(item.advancedelay<=0){
                        recalcItemProg();
                        //making sure
                        if(!item.goingToAdvance){
                            continue;
                        }
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(item.item.id);
                        buf.writeShort(item.slots.size);
                        for(int i=0;i<item.slots.size;i++){
                            buf.writeShort(item.slots.get(i));
                        }
                        ClientPlayNetworking.send(ModResearch.researchItem.getIdentifier(), buf);
                        //// blah blah blah
                        item.goingToAdvance = false;
                        item.advance(1);
                    }
                }
            }
        }
    }

    public void setDefaultbg(MarchingSquaresBackground defaultbg){
        this.defaultbg = defaultbg;
    }

    public static class ResearchItemUI{
        public ResearchItem item;
        public boolean unlocked = false;
        public boolean appeared = false;
        public UIWindow window;
        public UIButton node;
        public UIFancyBar waitbar;
        public UIResearchProgressBar stagebar;
        UIResearchBoard researchBoard;
        public float progress = 0;

        public boolean goingToAdvance = false;
        public float advancedelay = 0;
        public IntArray slots = new IntArray();
        public boolean fullyResearched = false;


        public ResearchItemUI(ResearchItem item){
            this.item=item;
        }
        public void disappear(){

            if(appeared){
                researchBoard.children.addAnimation(50, SingularInterpolateType.EXPONENTIAL2, time -> {
                    if(time==1){
                        node.disabled = true;
                    }
                    node.shrink = time;
                });
            }
            window.closing=true;
            stagebar.stage=0;
            goingToAdvance = false;
            if(item.preUnlockName!=null){
                window.children.get(UIText.class,"title").text = new TranslatableText(item.preUnlockName.getNameID());
            }
            window.children.get(UIText.class,"help").setText(new LiteralText("drop items here"));
            UIFlexContainer ufc = window.children.get(UIFlexContainer.class,"itemdepo");
            if(ufc.children.get(UIImage.class, "check") != null){
                ufc.remove(ufc.children.get(UIImage.class, "check"));
            }
            fullyResearched = false;
            node.upTexture = UITextures.defaultButtonUp;
            node.downTexture = UITextures.defaultButtonDown;
            UIFlexContainer imageStrip = window.children.get(UIFlexContainer.class,"imagestrip");
            if(imageStrip.children.get(UIImage.class,"post")!=null){
                imageStrip.remove(imageStrip.children.get(UIImage.class,"post"));
            }
            node.setIcon(item.iconFaded);
            window.children.get(UIImage.class,"icon").region = item.iconFaded;
            window.rebuild();
            //todo complete this
            unlocked = false;
            appeared = false;
        }
        public void appear(){
            if(appeared){return;}
            appeared = true;
            researchBoard.children.addAnimation(50,SingularInterpolateType.EXPONENTIAL2,time->{
                node.disabled = false;
                node.shrink = 1f-time;
            });
            researchBoard.children.addAnimation(50,SingularInterpolateType.EXPONENTIAL2,time->{
                float rad =5;
                researchBoard.bg.affectArea((rx, ry, original) -> {
                    float sped = time*(1f-time);
                    float dis = 1f-(MathHelper.sqrt(rx*rx+ry*ry)/rad);
                    dis = Math.max(dis,0);
                    return original + sped*0.4f*dis*dis;
                }, node.x+node.w*0.5f, node.y+node.h*0.5f, rad);
            });
            if(!item.children.isEmpty()){
                for(var child: item.children){
                    ResearchItemUI childui = researchBoard.researchItems.get(child.getName());
                    if(childui==null){
                        return;
                    }
                    for(int i =0;i<10;i++){
                        float tt = i/10f;
                        researchBoard.linesegs.add(new ProgressLine(node.mx(),node.my(),childui.node.mx(), childui.node.my(), tt,tt+0.1f ,(1-tt)*100));
                    }
                }
            }
        }
        public void beginAdvance(IntArray slots){
            goingToAdvance = true;
            advancedelay = 40;
            this.slots=slots;
        }
        public void advance(int forward){
            if(unlocked || forward==0){return;}
            unlocked = true;
            stagebar.stage += forward;
            ///change item drop zone text
            window.handler.addAnimation(1,SingularInterpolateType.EXPONENTIAL2,time->{
                window.children.get(UIText.class,"help").setText(new LiteralText("yey"));
            });
            // add post research splash image and check mark
            window.handler.addAnimation(60,SingularInterpolateType.EXPONENTIAL2,time->{
                UIFlexContainer ufc = window.children.get(UIFlexContainer.class,"itemdepo");
                if(stagebar.stage>=stagebar.totalstages){
                    if(ufc.children.get(UIImage.class, "check") == null){
                        ufc.add(UIImage.create("check", UITextures.completeCheck).setColor(new Vector4f(0.2f, 0.4f, 0.2f, 1.0f)));
                    }
                    UIImage check = ufc.children.get(UIImage.class, "check");
                    check.setMinimumSize(time * 17, time * 17);
                }

                UIFlexContainer imageStrip = window.children.get(UIFlexContainer.class,"imagestrip");
                if(imageStrip.children.get(UIImage.class,"post")==null){
                    imageStrip.add(UIImage.create("post",item.splash).setBack(UITextures.dropShadow).setStickytape(true).setRotation(Mathf.randFloat(-5,5f)));
                }
                imageStrip.children.get(UIImage.class,"post").rollup=1f-time;
                imageStrip.children.get(UIImage.class,"post").setMinimumSize(48,time*48f);
                imageStrip.recalcSize();
                window.rebuild();
            });
            ///re-set icons
            node.setIcon(item.icon);
            window.children.get(UIImage.class,"icon").region = item.icon;

            //bg growth effect
            researchBoard.children.addAnimation(200,SingularInterpolateType.LINEAR,time->{
                float rad =20;
                researchBoard.bg.affectArea((rx, ry, original) -> {
                    float sped = time*(1f-time);
                    float dis = 1f-(MathHelper.sqrt(rx*rx+ry*ry)/rad);
                    dis = Math.max(dis,0);
                    return original + sped*0.1f*dis*dis;
                }, node.x+node.w*0.5f, node.y+node.h*0.5f, rad);
            });
            // update title
            if(item.preUnlockName!=null){
                window.children.get(UIText.class,"title").text = new TranslatableText(item.getNameID());
            }
            //make children appear
            for(var parent:item.parent){
                ResearchItemUI parentui = researchBoard.researchItems.get(parent.getName());
                if(parentui!=null){
                    if(parentui.appeared){continue;}
                    boolean shouldAppear = true;
                    for(var children: parentui.item.children){
                        ResearchItemUI childui = researchBoard.researchItems.get(children.getName());
                        if(childui!=null){
                            shouldAppear &= childui.appeared;
                        }
                    }
                    if(shouldAppear){
                        parentui.appear();
                    }
                }
            }
            if(stagebar.stage>= stagebar.totalstages){
                fullyResearched = true;
                node.upTexture = UITextures.fancyButtonUp;
                node.downTexture = UITextures.fancyButtonDown;
            }

        }

        public void generateUI(AnimatedScreen screen){
            node = UIButton.create(item.name+" node").setIcon(item.iconFaded).setOnClicked(b->{
                window.setPositioning(node.x,node.y+30);
                window.disabled=false;
                window.closing=false;
                window.parent.rebuild();
            });
            node.pad(2);
            node.disabled=true;
            node.shrink = 1;

            UIGridPanel header = (UIGridPanel)UIGridPanel.create("header",screen)
                .setFill(false,true)
                .resize(110,24);

            header.row();
            header.add(UIImage.create("icon",item.iconFaded)
                .setBack(UITextures.panel)
                .resize(16,16)
                .setMinimumSize(16,16)
                .setPositioning(0,DISABLE,0,DISABLE)
                .marginBottom(8));
            header.add(UIText.create("title",new TranslatableText(item.preUnlockName!=null?item.preUnlockName.getNameID():item.getNameID())));

            stagebar = new UIResearchProgressBar("stagebar",item.maxLevels);

            UIBoundedPannableContainer pannableContainer = (UIBoundedPannableContainer)UIBoundedPannableContainer.create("textpan",screen).resize(50,100).setMinimumSize(30,80);
            UIFlexContainer textColumn = (UIFlexContainer)UIFlexContainer.create("imagestrip",screen).setDirection(FlexDirection.COLUMN).pad(0).padBottom(50);
            textColumn.add(UIMultilineText.create("text1",new TranslatableText(item.initialDesc.id)).setScale(0.5f).setMinimumSize(50,30));
            textColumn.resetMinSize();
            pannableContainer.add(textColumn);

            UIFlexContainer imageStrip = (UIFlexContainer)UIFlexContainer.create("imagestrip",screen).setDirection(FlexDirection.COLUMN).pad(0);
            imageStrip.addFlex(UIImage.create("image1",item.splashFaded)
               .setBack(UITextures.dropShadow)
               .resize(48,48)
               .setMinimumSize(48,48));

            UIFlexContainer body = (UIFlexContainer)UIFlexContainer.create("body",screen)
                .setBg(UITextures.parchment)
                .resize(110,80);
            body.addFlex(imageStrip).setAlign(FlexItemAlign.START);
            body.addFlex(pannableContainer)
                .setFlexgrow(1);
            body.addFlex(UISlider.create("slider")
                .setValues(0,0,1));
            pannableContainer.useSliderVert(body.getChildHandler().get(UISlider.class,"slider"));


            UIFlexContainer itemRequirement  = (UIFlexContainer)UIFlexContainer.create("itemreq",screen)
                .setBg(UITextures.parchment)
                .resize(110,48)
                .pad(0)
                .marginBottom(5);
            itemRequirement.justify = FlexJustify.CENTER;
            for(var i: item.itemRequirement){
                ItemStack itemStack = new ItemStack(Registry.ITEM.get(i.getItem()),i.getAmount());
                itemRequirement.add(new UIItemIcon("icon-"+i.getItem().toString(),itemStack));
            }
            UISimpleContainer itemDepoWrapper = (UISimpleContainer)UISimpleContainer.create("itemdepowrap", screen).pad(0);

            UIFlexContainer itemDeposit = (UIFlexContainer)UIFlexContainer.create("itemdepo",screen).resize(110,48);
            itemDeposit.setBg(UITextures.selectionInset);
            itemDeposit.setItemAlign(FlexItemAlign.CENTER);
            itemDeposit.justify = FlexJustify.CENTER;
            itemDeposit.addFlex(UIText.create("help",new LiteralText("drag items here")).setScale(0.5f).margin(10));

            waitbar = new UIFancyBar(UITextures.beveled,"bar");
            waitbar.fadeIn = true;
            waitbar.setColor(0.3f,0.6f,0.5f,1.0f);
            itemDepoWrapper.add(waitbar);
            itemDepoWrapper.add(itemDeposit);

            UIGridPanel container = (UIGridPanel)UIGridPanel.create("test",screen)
                .setFill(true,false)
                .resize(140,200);
            container.row();
            container.add(header);
            container.row();
            container.add(stagebar);
            container.row();
            container.add(body);
            container.row();
            container.add(UIText.create("instruction",new TranslatableText(item.instructions.id)).setScale(0.5f));
            container.row();
            container.add(itemRequirement);
            container.row();
            container.add(itemDepoWrapper);

            UIWindow window = new UIWindow(item.name+" window",screen);
            window.resize(160,200);
            window.setOnMinimise(w->{
                if(w.minimised){
                    w.parent.handler.addAnimation(40, SingularInterpolateType.EXPONENTIAL2, time->{
                        container.padTop(5f*(1f-time));
                        header.padTop(5f*(1f-time));
                    });
                }else{
                    w.parent.handler.addAnimation(40, SingularInterpolateType.EXPONENTIAL2, time->{
                        container.padTop(5*time);
                        header.padTop(5f*time);
                    });
                }
            });
            window.add(container);
            window.initialSize();
            this.window=window;
        }
    }

    public static class ProgressLine{
        float x,y;
        float x2,y2;

        float tx,ty;
        float tx2,ty2;

        float progress=0, lerp=0;
        float delay =0;
        boolean decay = false;
        //actually a segment of a line, t and t2 are parametric.
        public ProgressLine(float lx, float ly, float lx2, float ly2, float t,float t2,float delay){
            float dx = lx2-lx;
            float dy = ly2-ly;
            float d = MathHelper.sqrt(dx*dx+dy*dy);
            tx = lx+dx*t;
            tx2 = lx+dx*t2;
            ty = ly+dy*t;
            ty2 = ly+dy*t2;
            dx/=d;
            dy/=d;
            float offset = Mathf.randFloat(-15,15);
            float offset2 = offset+Mathf.randFloat(-5,5);
            x = tx+dy*offset;
            y = ty-dx*offset;
            x2 = tx2+dy*offset2;
            y2 = ty2-dx*offset2;
            float mx = (x+x2)*0.5f;
            float my = (y+y2)*0.5f;
            x = Mathf.lerp(Mathf.randFloat(1),x,mx);
            x2 = Mathf.lerp(Mathf.randFloat(1),x2,mx);
            y = Mathf.lerp(Mathf.randFloat(1),y,my);
            y2 = Mathf.lerp(Mathf.randFloat(1),y2,my);
            this.delay=delay;
        }

        void update(){
            delay-=1;
            if(delay>0){
                return;
            }
            progress+=decay?-0.01:0.01;
            progress = MathHelper.clamp(progress,0,1);
            updatelerp();
        }

        public void updatelerp(){
            lerp = SingularInterpolateType.EXPONENTIAL2.interpolate(0,1,progress,0.2f);
        }

        void draw(SpriteDrawer sb){

            if(progress==1){
                sb.resetColor();
                sb.drawLine(UITextures.whitebar,tx,ty,tx2,ty2,3);
                return;
            }
            float v = Math.min(1,lerp*2);
            sb.setAlpha(v);
            sb.drawLine(UITextures.whitebar,
                Mathf.lerp(lerp,x,tx),
                Mathf.lerp(lerp,y,ty),
                Mathf.lerp(lerp,x2,tx2),
                Mathf.lerp(lerp,y2,ty2),
            v*3);
            sb.resetColor();
        }
    }
}
