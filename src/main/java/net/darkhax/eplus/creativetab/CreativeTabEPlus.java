package net.darkhax.eplus.creativetab;

import net.darkhax.eplus.libs.Content;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabEPlus extends CreativeTabs {

    public CreativeTabEPlus () {

        super("eplus");
    }

	@Override
	public ItemStack createIcon() {
		
		return new ItemStack(Content.blockAdvancedTable);
	}
}
