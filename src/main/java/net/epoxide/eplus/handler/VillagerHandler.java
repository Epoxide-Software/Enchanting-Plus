package net.epoxide.eplus.handler;

import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.VillagerRegistry;
import cpw.mods.fml.relauncher.Side;

import net.darkhax.bookshelf.lib.util.MathsUtils;
import net.darkhax.bookshelf.lib.util.Utilities;

import net.epoxide.eplus.item.ItemEnchantedScroll;

public class VillagerHandler {
    
    public static void initVillageHandler () {
        
        if (EPlusConfigurationHandler.allowVillagers) {
            
            VillagerRegistry reg = VillagerRegistry.instance();
            reg.registerVillagerId(EPlusConfigurationHandler.villagerID);
            reg.registerVillageTradeHandler(EPlusConfigurationHandler.villagerID, new VillageTradeHandler());
            reg.registerVillageCreationHandler(new VillageBuildingHandler());
            MapGenStructureIO.func_143031_a(VillagerHandler.ComponentArcaneLibrary.class, "eplus:ArcaneLibrary");
            
            if (FMLCommonHandler.instance().getSide().equals(Side.CLIENT))
                reg.registerVillagerSkin(EPlusConfigurationHandler.villagerID, new ResourceLocation("eplus", "textures/entity/villager/librarian.png"));
        }
    }
    
    public static class VillageTradeHandler implements VillagerRegistry.IVillageTradeHandler {
        
        @Override
        public void manipulateTradesForVillager (EntityVillager villager, MerchantRecipeList recipeList, Random random) {
            
            if (villager.getProfession() == EPlusConfigurationHandler.villagerID) {
                
                List<Enchantment> enchants = Utilities.getAvailableEnchantments();
                
                for (int count = 0; count <= 2; count++) {
                    
                    Enchantment ench = enchants.get(random.nextInt(enchants.size()));
                    
                    if (!ContentHandler.isBlacklisted(ench))
                        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, MathsUtils.nextIntInclusive(5, 9)), null, ItemEnchantedScroll.createScroll(ench)));
                }
                
                int bookCount = MathsUtils.nextIntInclusive(1, 3);
                recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, MathsUtils.nextIntInclusive(3, 5)), new ItemStack(Items.book, bookCount), new ItemStack(ContentHandler.blockEnchantmentBook, bookCount)));
            }
        }
    }
    
    public static class VillageBuildingHandler implements VillagerRegistry.IVillageCreationHandler {
        
        @Override
        public PieceWeight getVillagePieceWeight (Random random, int i) {
            
            return new PieceWeight(VillagerHandler.ComponentArcaneLibrary.class, 15, i + random.nextInt(4));
        }
        
        @Override
        public Class<?> getComponentClass () {
            
            return VillagerHandler.ComponentArcaneLibrary.class;
        }
        
        @Override
        public Object buildComponent (PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
            
            return ComponentArcaneLibrary.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
        }
    }
    
    public static class ComponentArcaneLibrary extends StructureVillagePieces.House1 {
        
        private int averageGroundLevel = -1;
        private boolean hasMadeChest = false;
        
        public ComponentArcaneLibrary() {
        
        }
        
        public ComponentArcaneLibrary(Start villagePiece, int par2, Random random, StructureBoundingBox structure, int par5) {
            
            super();
            this.coordBaseMode = par5;
            this.boundingBox = structure;
        }
        
        public static ComponentArcaneLibrary buildComponent (Start villagePiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
            
            StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 7, 6, 7, p4);
            return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null ? new ComponentArcaneLibrary(villagePiece, p5, random, structureboundingbox, p4) : null;
        }
        
        @Override
        public boolean addComponentParts (World world, Random random, StructureBoundingBox sbb) {
            
            if (this.averageGroundLevel < 0) {
                
                this.averageGroundLevel = this.getAverageGroundLevel(world, sbb);
                
                if (this.averageGroundLevel < 0)
                    return true;
                    
                this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + 4, 0);
            }
            
            this.fillWithBlocks(world, sbb, 1, 1, 1, 7, 5, 4, Blocks.air, Blocks.air, false);
            this.fillWithBlocks(world, sbb, 0, 0, 0, 8, 0, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 5, 0, 8, 5, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 6, 1, 8, 6, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 7, 2, 8, 7, 3, Blocks.stonebrick, Blocks.stonebrick, false);
            int i = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            int j = this.getMetadataWithOffset(Blocks.oak_stairs, 2);
            int k;
            int l;
            
            for (k = -1; k <= 2; ++k) {
                
                for (l = 0; l <= 8; ++l) {
                    
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, i, l, 6 + k, k, sbb);
                    this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, j, l, 6 + k, 5 - k, sbb);
                }
            }
            
            this.fillWithBlocks(world, sbb, 0, 1, 0, 0, 1, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 1, 1, 5, 8, 1, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 8, 1, 0, 8, 1, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 2, 1, 0, 7, 1, 0, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 2, 0, 0, 4, 0, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 2, 5, 0, 4, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 8, 2, 5, 8, 4, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 8, 2, 0, 8, 4, 0, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 0, 2, 1, 0, 4, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 1, 2, 5, 7, 4, 5, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 8, 2, 1, 8, 4, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 1, 2, 0, 7, 4, 0, Blocks.stonebrick, Blocks.stonebrick, false);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 4, 2, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 5, 2, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 6, 2, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 4, 3, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 5, 3, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 6, 3, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 0, 2, 2, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 0, 2, 3, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 0, 3, 2, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 0, 3, 3, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 8, 2, 2, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 8, 2, 3, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 8, 3, 2, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 8, 3, 3, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 2, 2, 5, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 3, 2, 5, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 5, 2, 5, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.glass, 0, 6, 2, 5, sbb);
            this.fillWithBlocks(world, sbb, 1, 4, 1, 7, 4, 1, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 1, 4, 4, 7, 4, 4, Blocks.stonebrick, Blocks.stonebrick, false);
            this.fillWithBlocks(world, sbb, 1, 3, 4, 7, 3, 4, Blocks.bookshelf, Blocks.bookshelf, false);
            this.placeBlockAtCurrentPosition(world, Blocks.stonebrick, 0, 7, 1, 4, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.oak_stairs, this.getMetadataWithOffset(Blocks.oak_stairs, 0), 7, 1, 3, sbb);
            k = this.getMetadataWithOffset(Blocks.oak_stairs, 3);
            
            if (!this.hasMadeChest) {
                
                this.hasMadeChest = true;
                this.generateStructureChestContents(world, sbb, random, 7, 1, 1, ContentHandler.eplusChest.getItems(random), ContentHandler.eplusChest.getCount(random));
            }
            
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 1, 0, sbb);
            this.placeBlockAtCurrentPosition(world, Blocks.air, 0, 1, 2, 0, sbb);
            this.placeDoorAtCurrentPosition(world, sbb, random, 1, 1, 0, this.getMetadataWithOffset(Blocks.wooden_door, 1));
            
            if (this.getBlockAtCurrentPosition(world, 1, 0, -1, sbb).getMaterial() == Material.air && this.getBlockAtCurrentPosition(world, 1, -1, -1, sbb).getMaterial() != Material.air)
                this.placeBlockAtCurrentPosition(world, Blocks.stone_stairs, this.getMetadataWithOffset(Blocks.stone_stairs, 3), 1, 0, -1, sbb);
                
            for (l = 0; l < 6; ++l) {
                
                for (int i1 = 0; i1 < 9; ++i1) {
                    
                    this.clearCurrentPositionBlocksUpwards(world, i1, 9, l, sbb);
                    this.func_151554_b(world, Blocks.stonebrick, 0, i1, -1, l, sbb);
                }
            }
            
            this.spawnVillagers(world, sbb, 3, 1, 3, 1);
            
            return true;
        }
        
        @Override
        protected int getVillagerType (int par1) {
            
            return EPlusConfigurationHandler.villagerID;
        }
        
        @Override
        protected void func_143012_a (NBTTagCompound par1NBTTagCompound) {
            
            super.func_143012_a(par1NBTTagCompound);
            par1NBTTagCompound.setBoolean("Chest", this.hasMadeChest);
        }
        
        @Override
        protected void func_143011_b (NBTTagCompound par1NBTTagCompound) {
            
            super.func_143011_b(par1NBTTagCompound);
            this.hasMadeChest = par1NBTTagCompound.getBoolean("Chest");
        }
    }
}