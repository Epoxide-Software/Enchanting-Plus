package eplus.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import eplus.common.ContainerEnchanting;
import eplus.common.EnchantingPlus;
import eplus.common.EnchantmentItemData;
import eplus.common.ItemPocketEnchanter;
import eplus.common.localization.LocalizationHelper;
import eplus.common.packet.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class GuiEnchantmentPlus extends GuiContainer {

    public ArrayList<GuiIcon> icons;
    public ArrayList<GuiEnchantmentItem> enchantmentItems;
    public ArrayList<GuiDisenchantmentItem> disenchantmentItems;
    public ArrayList<Enchantment> possibleEnchantments;
    public ArrayList<EnchantmentItemData> possibleDisenchantments;

    public boolean allowDisenchanting = EnchantingPlus.allowDisenchanting;
    public boolean allowRepair = EnchantingPlus.allowRepair;
    public boolean allowTransfer = EnchantingPlus.allowTransfer;



    public float eScroll;
    public float dScroll;

    public int eIndex;
    public int dIndex;

    public boolean clicked;
    public boolean isEScrolling;
    public boolean isDScrolling;

    public EntityClientPlayerMP player;

    private String textToDisplay = "";
    private int textToDisplayX;
    private int textToDisplayY;
    
    public GuiEnchantmentPlus(EntityPlayer player, World world, int x, int y, int z) {
        super(new ContainerEnchanting(player, world, x, y, z));
        this.player = (EntityClientPlayerMP) player;
        xSize = 209;
        ySize = 238;
        icons = new ArrayList();
        possibleEnchantments = new ArrayList();
        possibleDisenchantments = new ArrayList();
        enchantmentItems = new ArrayList();
        disenchantmentItems = new ArrayList();
    }

    public void drawScreen(int var1, int var2, float var3)
    {
        int var4 = var1 - guiLeft;
        int var5 = var2 - guiTop;
        if (!clicked && Mouse.isButtonDown(0)) {
            if (var4 >= 180 && var4 <= 192) {
                if (var5 >= 16 && var5 <= 88 && enchantmentItems.size() > 4) {
                    isEScrolling = true; // up scroll
                } else if (var5 >= 90 && var5 <= 144 && disenchantmentItems.size() > 3) {
                    isDScrolling = true; // down scroll
                }
            }
            for (GuiEnchantmentItem item : enchantmentItems) {
                if (item.isMouseOver(var1, var2) && item.isSlider) {
                    item.sliding = true;
                }
            }
        }
        if (!Mouse.isButtonDown(0)) {
            isEScrolling = false;
            isDScrolling = false;
            for (GuiEnchantmentItem item : enchantmentItems) {
                item.sliding = false;
            }
            selectEnchantments();
        }
        clicked = Mouse.isButtonDown(0);
        if (isEScrolling) {
            eScroll = ((float) (var5 - 6) - 16) / (float) 57;
            if (eScroll > 1) {
                eScroll = 1;
            }
            if (eScroll < 0) {
                eScroll = 0;
            }
            scrollEnchantment(eScroll);
        }
        if (isDScrolling) {
            dScroll = ((float) (var5 - 6) - 90) / (float) 39;
            if (dScroll > 1) {
                dScroll = 1;
            }
            if (dScroll < 0) {
                dScroll = 0;
            }
            scrollDisenchantment(dScroll);
        }
        // slider adjust of each enchantment
        for (GuiEnchantmentItem item : enchantmentItems) {
            item.draw = !(item.yPos < guiTop + 16 || item.yPos > guiTop + 87);
            if (item.sliding) {
                item.scroll(var4 - 39);
            }
        }
        for (GuiDisenchantmentItem item : disenchantmentItems) {
            item.draw = !(item.yPos < guiTop + 90 || item.yPos > guiTop + 143);
        }
        
        super.drawScreen(var1, var2, var3);
        if (textToDisplay != "") {// modified by Slash
        	drawTooltip(getStringLines(textToDisplay), textToDisplayX, textToDisplayY); // modified by Slash
        	textToDisplay = "";
        }
    }

    public void initGui()
    {
        super.initGui();
        GuiIcon.startingX = guiLeft;
        GuiIcon.startingY = guiTop;
        GuiEnchantmentItem.startingX = guiLeft;
        GuiEnchantmentItem.startingY = guiTop;
        GuiDisenchantmentItem.startingX = guiLeft;
        GuiDisenchantmentItem.startingY = guiTop;
        icons.clear();
        icons.add(new GuiIcon("Enchant", 0, 11, 77).setButton().setInfo(LocalizationHelper.getLocalString("gui.icon.enchant.description")));
        if (this.allowDisenchanting) // modified by Slash
        	icons.add(new GuiIcon("Disenchant", 1, 11, 94).setButton().setInfo(LocalizationHelper.getLocalString("gui.icon.disenchant.description")));
        icons.add(new GuiIcon("Bookshelves", 6, 11, 8).setInfo(LocalizationHelper.getLocalString("gui.icon.bookshelves.description")));
        if (this.allowRepair)
            icons.add(new GuiIcon("Repair", 2, 11, 111).setButton().setInfo(LocalizationHelper.getLocalString("gui.icon.repair.description")));
        if (this.allowTransfer)
            icons.add(new GuiIcon("Transfer", 3, 11, 128).setButton().setInfo(LocalizationHelper.getLocalString("gui.icon.transfer.description")));
        enchantmentItems.clear();

        checkItems();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.func_98187_b("/eplus/enchant" + EnchantingPlus.getTranslatedTextureIndex() + ".png");
        GL11.glDisable(GL11.GL_LIGHTING);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        this.drawTexturedModalRect(guiLeft + 180, guiTop + 16 + (int) (57 * eScroll), 0 + enchantmentItems.size() > 4 ? 0 : 12, 238, 12, 15);
        this.drawTexturedModalRect(guiLeft + 180, guiTop + 90 + (int) (39 * dScroll), 0 + disenchantmentItems.size() > 3 ? 0 : 12, 238, 12, 15);
        
        drawPlayerXPLevel(player.experienceLevel); // created by Slash
        
        for (GuiEnchantmentItem item : enchantmentItems) {
            item.draw(mc, var2, var3);
        }
        for (GuiDisenchantmentItem item : disenchantmentItems) {
            item.draw(mc, var2, var3);
        }
        for (GuiIcon icon : icons) {
            icon.draw(mc, var2, var3);
            /*if (icon.isMouseOver(var2, var3)) { // modified by Slash
                drawIconString(icon);
            }*/
        }
        // System.out.println((var2 - guiLeft)+"."+(var3 - guiTop));
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        for (GuiIcon icon : icons) {
            if (icon.isMouseOver(var2, var3)) {
                //drawGradientRect(guiLeft - 100, guiTop, guiLeft - 4, guiTop + ySize, -2130706433, -2130706433); // modified by Slash
            	//mc.fontRenderer.drawSplitString(getInfo(icon), guiLeft - 96, guiTop + 4, 92, 0x444444); // modified by Slash
            	textToDisplay = getInfo(icon);
            	textToDisplayX=var2;
            	textToDisplayY=var3;
            }
        }
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }
    
    // created by Slash
    protected void drawTooltip(ArrayList<String> var4, int x, int y)
    {
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (!var4.isEmpty())
        {
            int var5 = 0;
            int var6;
            int var7;

            for (var6 = 0; var6 < var4.size(); ++var6)
            {
                var7 = this.fontRenderer.getStringWidth((String)var4.get(var6));

                if (var7 > var5)
                {
                    var5 = var7;
                }
            }

            var6 = x + 12;
            var7 = y - var4.size() * this.fontRenderer.FONT_HEIGHT; // modified by Slash
            if (var7 < 12) var7 = 12; // modified by Slash
            
            int var9 = 8;

            if (var4.size() > 1)
            {
                var9 += 2 + (var4.size() - 1) * 10;
            }

            if (this.guiTop + var7 + var9 + 6 > this.height)
            {
                var7 = this.height - var9 - this.guiTop - 6;
            }

            this.zLevel = 300.0F;
            itemRenderer.zLevel = 300.0F;
            
            int var10 = -267386864;
            this.drawGradientRect(var6 - 3, var7 - 4, var6 + var5 + 3, var7 - 3, var10, var10);
            this.drawGradientRect(var6 - 3, var7 + var9 + 3, var6 + var5 + 3, var7 + var9 + 4, var10, var10);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 - 4, var7 - 3, var6 - 3, var7 + var9 + 3, var10, var10);
            this.drawGradientRect(var6 + var5 + 3, var7 - 3, var6 + var5 + 4, var7 + var9 + 3, var10, var10);
            int var11 = 1347420415;
            int var12 = (var11 & 16711422) >> 1 | var11 & -16777216;
            this.drawGradientRect(var6 - 3, var7 - 3 + 1, var6 - 3 + 1, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 + var5 + 2, var7 - 3 + 1, var6 + var5 + 3, var7 + var9 + 3 - 1, var11, var12);
            this.drawGradientRect(var6 - 3, var7 - 3, var6 + var5 + 3, var7 - 3 + 1, var11, var11);
            this.drawGradientRect(var6 - 3, var7 + var9 + 2, var6 + var5 + 3, var7 + var9 + 3, var12, var12);

            for (int var13 = 0; var13 < var4.size(); ++var13)
            {
                String var14 = (String)var4.get(var13);

                if (var13 == 0)
                {
                    var14 = "\u00a73" + var14;
                }
                else
                {
                    var14 = "\u00a77" + var14;
                }

                this.fontRenderer.drawStringWithShadow(var14, var6, var7, -1);

                if (var13 == 0)
                {
                    var7 += 2;
                }

                var7 += 10;
            }

            this.zLevel = 0.0F;
            itemRenderer.zLevel = 0.0F;
        }
    }
    
    @Override
    protected void handleMouseClick(Slot par1Slot, int par2, int par3, int par4)
    {
    	// created by Slash
    	boolean check = false;
        if (par1Slot != null) {
            // when user click on slot 0 or slot 1, need to checkitems
            // when user click any other slot but with shift, need to checkitems only if slot1 is empty
        	if (par1Slot.slotNumber < 2) check = true; 
        	if (par1Slot.slotNumber > 1 & par4 == 1) { // trying to put something from inventory do slot 1 or 2
        		if (!inventorySlots.getSlot(0).getHasStack() || !inventorySlots.getSlot(1).getHasStack())
        			check = true;
        	}
        } // by Slash
    	
        super.handleMouseClick(par1Slot, par2, par3, par4);
    	if (check) checkItems(); // modified by Slash
    	
    }

    public boolean setStack(ItemStack var2)
    {
        if (getContainer().canSetStack(var2)) {
            return false;
        }
        if (inventorySlots.getSlot(0).getStack() == null) {
            inventorySlots.getSlot(0).putStack(var2);
            return true;
        } else if (inventorySlots.getSlot(1).getStack() == null) {
            inventorySlots.getSlot(1).putStack(var2);
            return true;
        } else {
            return false;
        }
    }

    public void checkItems()
    {
        getContainer().checkItems(this);
    }

    public void selectEnchantments()
    {
        ArrayList var1 = new ArrayList();
        boolean var2[] = new boolean[enchantmentItems.size()];

        for (int i = 0; i < var2.length; i++) {
            var2[i] = true;
        }

        for (int j = 0; j < enchantmentItems.size(); j++) {
            GuiEnchantmentItem var3 = (GuiEnchantmentItem) enchantmentItems.get(j);

            if (var3.level <= 0) {
                continue;
            }

            for (int l = 0; l < enchantmentItems.size(); l++) {
                boolean flag1 = var3.type.canApplyTogether(((GuiEnchantmentItem) enchantmentItems.get(l)).type);
                boolean flag2 = ((GuiEnchantmentItem) enchantmentItems.get(l)).type.canApplyTogether(var3.type);

                if (l != j && (!flag1 || !flag2)) {
                    var2[l] = false;
                }
            }

            var1.add(new EnchantmentData(var3.type, var3.level));
        }

        for (int k = 0; k < var2.length; k++) {
            ((GuiEnchantmentItem) enchantmentItems.get(k)).enabled = var2[k];
        }
        boolean var3 = false;
        boolean var4 = false;
        for (GuiEnchantmentItem item : enchantmentItems) {
            if (item.level > 0) {
                var3 = true;
            }
        }
        for (GuiDisenchantmentItem item : disenchantmentItems) {
            if (item.level > 0) {
                var4 = true;
            }
        }
        getIcon("Enchant").enabled = var3 && canPurchase(getEnchantmentCost());
        //getIcon("Disenchant").enabled = var4 && getDisenchantmentCost() > 0;
        if (this.allowDisenchanting) // modified by Slash
        	getIcon("Disenchant").enabled = var4;
    }

    public GuiIcon getIcon(String s)
    {
        for (GuiIcon icon : icons) {
            if (icon.id.equals(s)) {
                return icon;
            }
        }
        return null;
    }

    private String getCostString(GuiIcon var1){
    	String result = "";
    	String no = "\u00a74"; // red
    	String yes = "\u00a79"; // indigo
    	
        if (var1.id.equals("Enchant")) {
            result = "\u00a77" + LocalizationHelper.getLocalString("gui.cost") + ": " + (canPurchase(getEnchantmentCost()) ? yes : no) + String.valueOf(getEnchantmentCost());
        }
        if (var1.id.equals("Disenchant")) {
        	result = "\u00a77" + LocalizationHelper.getLocalString("gui.cost") + ": " + yes + String.valueOf(getDisenchantmentCost());
        }
        if (var1.id.equals("Bookshelves")) {
        	result = "\u00a77" + LocalizationHelper.getLocalString("gui.bookshelves") + ": " + String.valueOf(getContainer().bookshelves) + " / " + String.valueOf(EnchantingPlus.maxBookShelves);
        }
        if (var1.id.equals("Repair")) {
        	result = "\u00a77" + LocalizationHelper.getLocalString("gui.cost") + ": " + (canPurchase(getRepairCost()) ? yes : no) + String.valueOf(getRepairCost());
        }
        if (var1.id.equals("Transfer")) {
        	result = "\u00a77" + LocalizationHelper.getLocalString("gui.cost") + ": " + (canPurchase(getTransferCost()) ? yes : no) + String.valueOf(getTransferCost());
        }
        return result;
    }
    
    public void drawPlayerXPLevel(int var1) // created by Slash
    {
    	String text = "\u00a7b" + LocalizationHelper.getLocalString("gui.playerlevel") + ": " + String.valueOf(var1);

    	this.drawCreativeTabHoveringText(text,  (int)(guiLeft + xSize/2 - mc.fontRenderer.getStringWidth(text)/2), (int)(guiTop+12));
    }
    
    public boolean canPurchase(int var1)
    {
        Minecraft client = FMLClientHandler.instance().getClient();
        PlayerControllerMP playerController = client.playerController;
        EntityClientPlayerMP thePlayer = client.thePlayer;

        int maxLevel = (int)((float)getContainer().bookshelves / (float)EnchantingPlus.maxBookShelves * 30F); // created by Slash
    	if (getContainer().bookshelves>=EnchantingPlus.maxBookShelves) maxLevel = mc.thePlayer.experienceLevel; // created by Slash
    	if (!EnchantingPlus.needBookShelves || thePlayer.inventory.getCurrentItem().getItem() instanceof ItemPocketEnchanter)
            maxLevel = var1 + 1; // created by Slash
    	
        return playerController.isInCreativeMode() || thePlayer.experienceLevel >= var1 && var1 > 0 && var1 <= maxLevel;
    }

    public ArrayList<EnchantmentItemData> readItem(ItemStack var1)
    {
        if (var1 == null) {
            return new ArrayList();
        }
        if (!var1.isItemEnchanted()) {
            return new ArrayList();
        }
        ArrayList<EnchantmentItemData> list = new ArrayList();
        for (int var2 = 0; var2 < var1.getEnchantmentTagList().tagCount(); var2++) {
            EnchantmentItemData var3;
            NBTTagCompound var4 = (NBTTagCompound) var1.getEnchantmentTagList().tagAt(var2);
            EnchantmentData var5 = new EnchantmentData(Enchantment.enchantmentsList[var4.getShort("id")], var4.getShort("lvl"));
            if (var4.hasKey("bs")) {

                if (var4.getTag("bs").getId() == 2) {
                    var3 = new EnchantmentItemData(var5, (int) var4.getShort("bs"));
                } else if (var4.getTag("bs").getId() == 1) {
                    var3 = new EnchantmentItemData(var5, (int) var4.getByte("bs"));
                } else {
                    var3 = null;
                }
            } else {
                var3 = new EnchantmentItemData(var5, getContainer().bookshelves);
            }
            list.add(var3);
        }
        return list;
    }

    public ContainerEnchanting getContainer()
    {
        return (ContainerEnchanting) inventorySlots;
    }

    public void mouseClicked(int var1, int var2, int var3)
    {
        super.mouseClicked(var1, var2, var3);
        if (var3 == 0) {
            for (GuiEnchantmentItem item : enchantmentItems) {
                if (item.isMouseOver(var1, var2) && !item.isSlider) {
                    item.press();
                }
            }
            for (GuiDisenchantmentItem item : disenchantmentItems) {
                if (item.isMouseOver(var1, var2)) {
                    item.press();
                }
            }
            for (GuiIcon icon : icons) {
                if (icon.isMouseOver(var1, var2) && icon.canClick()) {
                    pressIcon(icon);
                }
            }
        }
    }

    public void handleMouseInput()
    {
        super.handleMouseInput();
        int var1 = Mouse.getDWheel();
        int var2 = (Mouse.getEventX() * this.width / this.mc.displayWidth) - guiLeft;
        int var3 = (this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1) - guiTop;
        if (var1 != 0) {
            if (isMouseOverSection(true, var2, var3)) {
                if (var1 > 0) {
                    if (eIndex > 0) {
                        eIndex--;
                        for (GuiEnchantmentItem item : enchantmentItems) {
                            item.yPos += 18;
                        }
                    }
                }
                if (var1 < 0) {
                    if (enchantmentItems.size() - eIndex > 4) {
                        eIndex++;
                        for (GuiEnchantmentItem item : enchantmentItems) {
                            item.yPos -= 18;
                        }
                    }
                }
                eScroll = ((float) eIndex / (enchantmentItems.size() - 4));
                if (eScroll > 1) {
                    eScroll = 1;
                }
                if (eScroll < 0) {
                    eScroll = 0;
                }
            } else if (isMouseOverSection(false, var2, var3)) {
                if (var1 > 0) {
                    if (dIndex > 0) {
                        dIndex--;
                        for (GuiDisenchantmentItem item : disenchantmentItems) {
                            item.yPos += 18;
                        }
                    }
                }
                if (var1 < 0) {
                    if (possibleDisenchantments.size() - dIndex > 3) {
                        dIndex++;
                        for (GuiDisenchantmentItem item : disenchantmentItems) {
                            item.yPos -= 18;
                        }
                    }
                }
                dScroll = ((float) dIndex / (disenchantmentItems.size() - 3));
                if (dScroll > 1) {
                    dScroll = 1;
                }
                if (dScroll < 0) {
                    dScroll = 0;
                }
            }
        }
    }

    public void onGuiClosed()
    {
    }

    public boolean isMouseOverSection(boolean var1, int var2, int var3)
    {
        if (var1) {
            if (enchantmentItems.size() > 4) {
                return var2 >= 35 && var2 <= 191 && var3 >= 16 && var3 <= 87;
            } else {
                return false;
            }
        } else {
            if (disenchantmentItems.size() > 3) {
                return var2 >= 35 && var2 <= 191 && var3 >= 90 && var3 <= 143;
            } else {
                return false;
            }
        }
    }

    // created by Slash
    public ArrayList getStringLines(String text) {
    	ArrayList<String> list = new ArrayList<String>();
    	text += "\n";
    	
    	while (!text.isEmpty()) {
    		list.add(text.substring(0, text.indexOf('\n')));
    		text = text.substring(text.indexOf('\n')+1);
    	}
    	
    	return list;
    }
    
    public String getInfo(GuiIcon var1)
    {
        if (var1 == null) {
            return "";
        }
        String var2 = "";
        var2 += var1.translated();
        var2 += "\n";
        if (var1.isButton) {
            var2 += LocalizationHelper.getLocalString("gui.canuse")+ ": " + var1.getTranslatedEnabled();
            var2 += "\n";
            var2 += getCostString(var1);
            var2 += "\n";
        }
        else{
            var2 += getCostString(var1);
            var2 += "\n";
        }
        	
        var2 += "\u00a77" + var1.info;
        return var2;
    }

    public void pressIcon(GuiIcon var1)
    {
        if (var1.id.equals("Enchant")) {
            ArrayList<EnchantmentData> var2 = new ArrayList();
            for (GuiEnchantmentItem var3 : enchantmentItems) {
                if (var3.level > 0) {
                    var2.add(new EnchantmentData(var3.type, var3.level));
                }
            }
            if (var2 != null || var2.isEmpty()) {
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    DataOutputStream data = new DataOutputStream(bytes);

                    data.write(getEnchantmentCost() / 128);
                    data.write(getEnchantmentCost());
                    data.write(var2.size());

                    for (int i = 0; i < var2.size(); i++) {
                        data.write(var2.get(i).enchantmentobj.effectId);
                        data.write(var2.get(i).enchantmentLevel);
                    }

                    Packet250CustomPayload packet = PacketBase.createPacket(1, bytes.toByteArray());

                    PacketDispatcher.sendPacketToServer(packet);
                    bytes.close();
                    data.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        //if (var1.id.equals("Disenchant")) { // modified by Slash
        if (var1.id.equals("Disenchant") && this.allowDisenchanting) {
            ArrayList<EnchantmentData> var2 = new ArrayList();
            for (GuiDisenchantmentItem var3 : disenchantmentItems) {
                if (var3.level > 0) {
                    var2.add(new EnchantmentData(var3.type, var3.level));
                }
            }
            if (var2 != null || var2.isEmpty()) {
                try {
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    DataOutputStream data = new DataOutputStream(bytes);

                    data.write(getDisenchantmentCost() / 128);
                    data.write(getDisenchantmentCost());
                    data.write(var2.size());

                    for (int i = 0; i < var2.size(); i++) {
                        data.write(var2.get(i).enchantmentobj.effectId);
                        data.write(var2.get(i).enchantmentLevel);
                    }

                    Packet250CustomPayload packet = PacketBase.createPacket(2, bytes.toByteArray());

                    PacketDispatcher.sendPacketToServer(packet);
                    bytes.close();
                    data.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (var1.id.equals("Repair") && this.allowRepair) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream data = new DataOutputStream(bytes);

                data.write(getRepairCost() / 128);
                data.writeInt(getRepairCost());

                Packet250CustomPayload packet = PacketBase.createPacket(3, bytes.toByteArray());

                PacketDispatcher.sendPacketToServer(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (var1.id.equals("Transfer") && this.allowTransfer) {
            try {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                DataOutputStream data = new DataOutputStream(bytes);

                data.write(getTransferCost() / 128);
                data.writeInt(getTransferCost());

                Packet250CustomPayload packet = PacketBase.createPacket(4, bytes.toByteArray());

                PacketDispatcher.sendPacketToServer(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sync();
        checkItems();
    }

    public void sync()
    {
    }

    public void scrollEnchantment(float var1)
    {
        int var2 = enchantmentItems.size() - 4;
        int var3 = (int) Math.floor((double) (var1 * (float) var2));
        for (GuiEnchantmentItem item : enchantmentItems) {
            if (var3 > eIndex) {
                item.yPos -= 18 * (var3 - eIndex);
            } else if (var3 < eIndex) {
                item.yPos += 18 * (eIndex - var3);
            }
        }
        eIndex = var3;
    }

    public void scrollDisenchantment(float var1)
    {
        int var2 = disenchantmentItems.size() - 3;
        int var3 = (int) Math.floor((double) (var1 * (float) var2));
        for (GuiDisenchantmentItem item : disenchantmentItems) {
            if (var3 > dIndex) {
                item.yPos -= 18 * (var3 - dIndex);
            } else if (var3 < dIndex) {
                item.yPos += 18 * (dIndex - var3);
            }
        }
        dIndex = var3;
    }

    public int getEnchantmentCost()
    {
        ItemStack var2 = inventorySlots.getSlot(0).getStack();
        if (var2 == null) {
            return 0;
        }

        int var3 = 0;
        ArrayList<EnchantmentData> var1 = new ArrayList();
        for (GuiEnchantmentItem var4 : enchantmentItems) {
            if (var4.level > 0) {
                var1.add(new EnchantmentData(var4.type, var4.level));
            }
        }
        if (var1.size() == 0) {
            return 0;
        }
        for (EnchantmentData var4 : var1) {
            int var5 = (int) EnchantingPlus.enchantFactor * getBaseCost(inventorySlots.getSlot(0).getStack(), var4);
            double var6 = (double) getContainer().bookshelves / 64D;
            var3 += var5 - (int) ((double) var5 * var6);
        }
        double var4 = (double) var2.getItem().getItemEnchantability() / 96D;
        var3 -= (int) ((double) var3 * var4);
        return Math.max(1, var3);
    }

    public int getDisenchantmentCost()
    {
        ItemStack var1 = inventorySlots.getSlot(0).getStack();
        if (var1 == null) {
            return 0;
        }
        int var2 = 0;
        ArrayList<EnchantmentData> var3 = new ArrayList();
        int aint[] = new int[Enchantment.enchantmentsList.length];
        for (GuiDisenchantmentItem var4 : disenchantmentItems) {
            if (var4.level > 0) {
                var3.add(new EnchantmentData(var4.type, var4.level));
                aint[var4.type.effectId] = var4.shelves;
            }
        }
        if (var3.size() == 0) {
            return 0;
        }
        for (EnchantmentData var4 : var3) {
            int var5 = (int) EnchantingPlus.disenchantFactor * getBaseCost(inventorySlots.getSlot(0).getStack(), var4);
            double var6 = (double) aint[var4.enchantmentobj.effectId] / 64D;
            var2 += var5 - (int) ((double) var5 * var6);
        }
        double var4 = (double) var1.getItem().getItemEnchantability() / 96D;
        var2 -= (int) ((double) var2 * var4);
        return Math.max(1, var2 - (int) ((double) var2 * .1D));
    }

    public int getRepairCost()
    {
        ItemStack var1 = inventorySlots.getSlot(0).getStack();
        if (var1 == null || !var1.isItemDamaged()) {
            return 0;
        }
        int var2 = 0;
        ArrayList<EnchantmentData> var3 = new ArrayList();
        int aint[] = new int[Enchantment.enchantmentsList.length];
        for (EnchantmentItemData var4 : readItem(var1)) {
            var3.add(var4);
            aint[var4.enchantmentobj.effectId] = var4.shelves;
        }
        if (var3.size() == 0) {
            return 1;
        }
        for (EnchantmentData var4 : var3) {
            int var5 = (int) EnchantingPlus.repairFactor * getBaseCost(inventorySlots.getSlot(0).getStack(), var4);
            double var6 = (double) aint[var4.enchantmentobj.effectId] / 64D;
            var2 += var5 - (int) ((double) var5 * var6);
        }
        double var4 = (double) var1.getItem().getItemEnchantability() / 96D;
        var2 -= (int) ((double) var2 * var4);
        var2 = (int) ((double) var2 * .3D);
        double var5 = (double) var1.getItemDamage() / (double) var1.getMaxDamage();
        var2 += (int) ((double) var2 * var5);
        return Math.max(1, var2);
    }

    public int getTransferCost()
    {
        ItemStack var1 = inventorySlots.getSlot(1).getStack();
        if (var1 == null) {
            return 0;
        }
        int var2 = 0;
        ArrayList<EnchantmentData> var3 = new ArrayList();
        int aint[] = new int[Enchantment.enchantmentsList.length];
        for (EnchantmentItemData var4 : readItem(var1)) {
            var3.add(var4);
            aint[var4.enchantmentobj.effectId] = var4.shelves;
        }
        if (var3.size() == 0) {
            return 0;
        }
        for (EnchantmentData var4 : var3) {
            int var5 = (int) EnchantingPlus.transferFactor * getBaseCost(inventorySlots.getSlot(0).getStack(), var4);
            double var6 = (double) aint[var4.enchantmentobj.effectId] / 64D;
            var2 += var5 - (int) ((double) var5 * var6);
        }
        double var4 = (double) var1.getItem().getItemEnchantability() / 96D;
        var2 -= (int) ((double) var2 * var4);
        return Math.max(1, (int) ((double) var2 * .1D));
    }

    public int getBaseCost(ItemStack var1, EnchantmentData var2)
    {
        if (var1 == null) {
            return 0;
        }
        int var3 = 0;
        int var4 = (var2.enchantmentobj.getMinEnchantability(var2.enchantmentLevel) + var2.enchantmentobj.getMaxEnchantability(var2.enchantmentLevel)) / 2;
        var3 = (int) ((float) var3 + (float) var4 * ((float) var2.enchantmentLevel / (float) (var2.enchantmentobj.getMaxLevel() + 3)));
        return Math.max(1, var3);
    }
}