package eplus.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

/**
 * @user odininon
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

public class EnchantPacket extends BasePacket {

    private ArrayList<EnchantmentData> arrayList = new ArrayList<EnchantmentData>();

    public EnchantPacket() {}

    public EnchantPacket(ArrayList<EnchantmentData> list) {
        this.arrayList = list;
    }

    @Override
    public void write(ByteArrayDataOutput output) {
        output.writeInt(arrayList.size());
        for(EnchantmentData enchantmentData : arrayList) {
            output.writeInt(enchantmentData.enchantmentobj.effectId);
            output.writeInt(enchantmentData.enchantmentLevel);
        }
    }

    @Override
    public void read(ByteArrayDataInput input) {
        ArrayList<EnchantmentData> temp = new ArrayList<EnchantmentData>();
        int size = input.readInt();

        for(int i = 0; i < size; i++){
            temp.add(new EnchantmentData(input.readInt(), input.readInt()));
        }
        this.arrayList = temp;
    }

    @Override
    public void execute(EntityPlayer player, Side side) {
    }
}
