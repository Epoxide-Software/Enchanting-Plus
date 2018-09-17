package net.darkhax.eplus.api.event;

import java.util.List;

import net.darkhax.eplus.gui.GuiAdvancedTable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * This event is fired on the client when the info box to the left of the advanced table gui is
 * rendered. This event can be used to add/remove content from this box.
 */
@SideOnly(Side.CLIENT)
public class InfoBoxEvent extends Event {

    private final GuiAdvancedTable gui;
    private final List<String> info;

    public InfoBoxEvent (GuiAdvancedTable gui, List<String> info) {

        super();
        this.gui = gui;
        this.info = info;
    }

    public GuiAdvancedTable getGui () {

        return this.gui;
    }

    public List<String> getInfo () {

        return this.info;
    }
}
