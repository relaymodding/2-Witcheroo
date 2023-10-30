package org.relaymodding.witcheroo.events;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.relaymodding.witcheroo.capabilities.Capabilities;
import org.relaymodding.witcheroo.registries.WitcherooRegistries;

import java.awt.*;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class WitchStaffGUIEvents extends Gui {
	private static Minecraft mc;

	public WitchStaffGUIEvents(Minecraft mc, ItemRenderer itemRenderer){
		super(mc, itemRenderer);
		WitchStaffGUIEvents.mc = mc;
	}
	
	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void renderOverlay(RenderGuiOverlayEvent.Post e) {
		Player player = mc.player;

		if (player == null) return;
		if (!player.getMainHandItem().getItem().equals(WitcherooRegistries.WITCH_STAFF_OBJECT.get())) return;

		player.getCapability(Capabilities.WITCH_CAPABILITY).ifPresent(witch -> { // TODO networking

			GuiGraphics guiGraphics = e.getGuiGraphics();

			PoseStack poseStack = guiGraphics.pose();
			poseStack.pushPose();

			Component manaComponent = Component.translatable("witcheroo.notices.mana_total", witch.getMana(), witch.getMaxMana()); // TODO
			Color colour = new Color(255, 255, 255, 255);

			Font font = mc.font;

			Window window = mc.getWindow();
			int width = window.getGuiScaledWidth();
			int height = window.getGuiScaledHeight();

			int stringWidth = font.width(manaComponent);
			int x = (width/2) - (stringWidth/2);
			int y = height - 33;

			guiGraphics.drawString(font, manaComponent, x, y, colour.getRGB(), true);

			poseStack.popPose();
		});
	}
}