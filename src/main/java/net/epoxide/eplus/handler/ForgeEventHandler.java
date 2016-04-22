package net.epoxide.eplus.handler;

import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemPickupEvent;

public final class ForgeEventHandler {
    
    @SubscribeEvent
    public void onItemCrafted (ItemCraftedEvent event) {
        
        // if (ItemStackUtils.isValidStack(event.crafting) &&
        // Block.getBlockFromItem(event.crafting.getItem()) instanceof BlockEnchantmentBook)
        // event.player.addStat(ContentHandler.achievementEnlightened);
    }
    
    @SubscribeEvent
    public void onItemPickedUp (ItemPickupEvent event) {
        
        // if (ItemStackUtils.isValidStack(event.pickedUp.getEntityItem()) &&
        // event.pickedUp.getEntityItem().getItem() instanceof ItemEnchantedScroll)
        // event.player.addStat(ContentHandler.achievementResearch);
    }
    
    @SubscribeEvent
    public void onMobDrops (LivingDropsEvent event) {
        
        // if (event.entityLiving instanceof EntityMob &&
        // MathsUtils.tryPercentage(ConfigurationHandler.scrollDrop * (event.lootingLevel +
        // 1)))
        // event.drops.add(new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX,
        // event.entityLiving.posY, event.entityLiving.lastTickPosZ,
        // ItemEnchantedScroll.createRandomScroll()));
    }
    
    @SubscribeEvent
    public void onTooltip (ItemTooltipEvent event) {
        
        /*
         * if (ConfigurationHandler.allowModifierTooltips &&
         * ItemStackUtils.isValidStack(event.itemStack)) {
         * 
         * for (ScrollModifier modifier : ContentHandler.modifiers) {
         * 
         * if (ItemStackUtils.areStacksSimilar(event.itemStack, modifier.stack)) {
         * 
         * event.toolTip.add(EnumChatFormatting.LIGHT_PURPLE +
         * StatCollector.translateToLocal("tooltip.eplus.modifier"));
         * 
         * if (modifier.speed != 0.00d) {
         * 
         * boolean isPositive = modifier.speed > 0.00d; event.toolTip.add(((isPositive) ?
         * EnumChatFormatting.GREEN : EnumChatFormatting.RED) +
         * StatCollector.translateToLocal("tooltip.eplus.speed") + ": " + ((isPositive) ? "+" :
         * "") + modifier.speed * 100 + "%"); }
         * 
         * if (modifier.stability != 0.00d) {
         * 
         * boolean isPositive = modifier.stability > 0.00d; event.toolTip.add(((isPositive) ?
         * EnumChatFormatting.GREEN : EnumChatFormatting.RED) +
         * StatCollector.translateToLocal("tooltip.eplus.stability") + ": " + ((isPositive) ?
         * "+" : "") + modifier.stability * 100 + "%"); } } } }
         * 
         * if (Minecraft.getMinecraft().currentScreen instanceof GuiModEnchantmentTable) if
         * ((!ConfigurationHandler.allowUnownedModifications &&
         * !EnchantHelper.hasRestriction(event.itemStack) &&
         * EnchantmentUtils.isStackEnchanted(event.itemStack)) ||
         * (ConfigurationHandler.secureItems && EnchantHelper.hasRestriction(event.itemStack)
         * && !EnchantHelper.isValidOwner(event.itemStack, event.entityPlayer)))
         * Utilities.wrapStringToListWithFormat(StatCollector.translateToLocal(
         * "tooltip.eplus.notowner"), 30, false, event.toolTip, EnumChatFormatting.RED);
         * 
         */
    }
}