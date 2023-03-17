package me.json.pedestrians.entities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import me.json.pedestrians.Main;
import me.json.pedestrians.objects.framework.path.Node;
import me.json.pedestrians.utils.RotationUtil;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class NodeClientEntity extends ClientEntity{

    private final Node node;
    private final Player player;

    public NodeClientEntity(Location location, Node node, Player player) {
        super(location);

        this.node = node;
        this.player = player;
    }

    public Node node() {
        return node;
    }

    @Override
    protected PacketContainer[] spawnPackets(Player player) {

        PacketContainer[] packets = new PacketContainer[4];

        //0.
        packets[0] = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        packets[0].getIntegers().write(0, this.entityID);
        packets[0].getUUIDs().write(0, UUID.randomUUID());
        packets[0].getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);
        packets[0].getDoubles().write(0, location.getX());
        packets[0].getDoubles().write(1, location.getY()-1.45);
        packets[0].getDoubles().write(2, location.getZ());
        packets[0].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[0].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));

        //1.
        packets[1] = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

        Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage("Node: "+node.id())[0].getHandle());
        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0b00100000);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)),opt);
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)),true);

        //(1.19.3 shit ugh)
        List<WrappedDataValue> wrappedDataValues = new ArrayList<>();
        for (WrappedWatchableObject watchableObject : dataWatcher.getWatchableObjects()) {

            if (watchableObject == null) continue;

            WrappedDataWatcher.WrappedDataWatcherObject watcherObject = watchableObject.getWatcherObject();
            wrappedDataValues.add(new WrappedDataValue(watcherObject.getIndex(), watcherObject.getSerializer(), watchableObject.getRawValue()));
        }

        packets[1].getIntegers().write(0, entityID);
        packets[1].getDataValueCollectionModifier().write(0, wrappedDataValues);

        //2.
        packets[2] = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);

        List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = new ArrayList<>();
        list.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, Main.editorViewInventory().nodeArrowHead()));

        packets[2].getIntegers().write(0, entityID);
        packets[2].getSlotStackPairLists().write(0, list);

        //3.
        packets[3] = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
        packets[3].getIntegers().write(0, entityID);
        packets[3].getDoubles().write(0, location.getX());
        packets[3].getDoubles().write(1, location.getY()-1.45);
        packets[3].getDoubles().write(2, location.getZ());
        packets[3].getBytes().write(0, RotationUtil.floatToByte(location.getYaw()));
        packets[3].getBytes().write(1, RotationUtil.floatToByte(location.getPitch()));
        packets[3].getBooleans().write(0, true);

        //
        return packets;
    }

    @Override
    protected PacketContainer[] movePackets(short dx, short dy, short dz) {
        return new PacketContainer[0];
    }

    @Override
    protected boolean mayView(Player player) {
        return player.equals(this.player);
    }

}
