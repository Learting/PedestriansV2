package me.json.pedestrians.ui.tasks;

import me.json.pedestrians.Messages;
import me.json.pedestrians.entities.NodeClientEntity;
import me.json.pedestrians.objects.framework.path.connection.Connection;
import me.json.pedestrians.objects.framework.path.connection.ConnectionHandler.ConnectionHandlerType;
import me.json.pedestrians.ui.EditorView;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectTask implements ITask{

    private final List<NodeClientEntity> selectedNodeEntities = new ArrayList<>();
    private EditorView editorView;
    private ConnectionHandlerType connectionHandlerType = ConnectionHandlerType.DIRECT_CONNECTION_HANDLER;

    @Override
    public void init(EditorView editorView) {
        this.editorView = editorView;
    }

    @Override
    public void stop() {
        selectedNodeEntities.forEach(e -> e.glowing(false));
    }

    @Override
    public void onRightClickNode(NodeClientEntity node) {
        handleNodeClick(node);
    }

    @Override
    public void onLeftClickNode(NodeClientEntity node) {
        handleNodeClick(node);
    }

    @Override
    public void onRightClick() {
        handleClick();
    }

    @Override
    public void onLeftClick() {
        handleClick();
    }

    @Override
    public boolean scrollLock() {
        return selectedNodeEntities.size()==2;
    }

    @Override
    public void onScroll(int scrollDirection) {

        List<ConnectionHandlerType> connectionHandlerTypes = Arrays.asList(ConnectionHandlerType.values());
        int index = connectionHandlerTypes.indexOf(connectionHandlerType);
        int nextI = index + 1 >= connectionHandlerTypes.size() ? 0 : index + 1;
        connectionHandlerType = connectionHandlerTypes.get(nextI);

        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CONNECTION_TYPE_SELECT,connectionHandlerType.name())));
    }

    @Override
    public void render() {

    }

    private void handleClick() {

        if(selectedNodeEntities.size() != 2) return;

        selectedNodeEntities.get(0).node().registerConnectedNode(selectedNodeEntities.get(1).node(), new Connection(connectionHandlerType.connectionHandler()));
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.CONNECTION_CREATED)));

        //Reset
        selectedNodeEntities.forEach(e -> e.glowing(false));
        selectedNodeEntities.clear();

    }

    private void handleNodeClick(NodeClientEntity nodeEntity) {

        if(selectedNodeEntities.size() > 1) return;
        if(selectedNodeEntities.contains(nodeEntity)) return;

        selectedNodeEntities.add(nodeEntity);
        nodeEntity.glowing(true);
        editorView.player().spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(String.format(Messages.NODE_SELECTED,nodeEntity.node().id())));

    }

}
