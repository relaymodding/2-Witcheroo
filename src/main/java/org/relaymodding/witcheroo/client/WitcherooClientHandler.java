package org.relaymodding.witcheroo.client;


public class WitcherooClientHandler {

    public static void handlePacket(int entityId, boolean addition) {
        if (addition) {
            WitcherooClientRenderer.trackedFamiliars.add(entityId);
        }
        else WitcherooClientRenderer.trackedFamiliars.remove(entityId);
    }
}
