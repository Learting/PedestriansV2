package me.json.pedestrians.commands.subcommands;

import me.json.pedestrians.Messages;
import me.json.pedestrians.data.importing.ImportPathNetwork;
import me.json.pedestrians.objects.PlayerPedestrianEntity;
import me.json.pedestrians.objects.Skin;
import me.json.pedestrians.objects.framework.path.PathNetwork;
import me.json.pedestrians.objects.framework.pedestrian.Pedestrian;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import java.util.HashSet;
import java.util.Set;

public class SetPedsSubCommand implements ISubCommand<CommandSender> {

    private final Set<CommandSender> senders = new HashSet<>();

    @Override
    public void handle(CommandSender sender, String[] args) {

        if(!StringUtils.isNumeric(args[1])) {
            Messages.sendMessage(sender, Messages.WRONG_USAGE);
            return;
        }

        PathNetwork pathNetwork = PathNetwork.Registry.pathNetwork(args[0]);
        int pedestrians = Integer.parseInt(args[1]);

        if(pathNetwork != null) {

            pathNetwork.defaultPedestrians(pedestrians);
            updatePathNetwork(pathNetwork, pedestrians);
            Messages.sendMessage(sender, Messages.PEDESTRIANS_SET);

        } else {

            senders.add(sender);

            new ImportPathNetwork(args[0], p -> {

                senders.remove(sender);

                p.defaultPedestrians(pedestrians);
                updatePathNetwork(p, pedestrians);
                Messages.sendMessage(sender, Messages.PEDESTRIANS_SET);

            }, true).start();

        }

    }

    private void updatePathNetwork(PathNetwork pathNetwork, int pedestrians) {

        int delta = pedestrians - pathNetwork.pedestrians(Integer.MAX_VALUE).size();

        //Remove
        if(delta < 0) {
            pathNetwork.pedestrians(delta).forEach(p -> p.remove());
        }

        //add
        if(delta > 0) {

            for (int i = 0; i < delta; i++) {
                pathNetwork.addPedestrian(new Pedestrian(pathNetwork, new PlayerPedestrianEntity(Skin.Registry.randomSkin()), pathNetwork.randomNode()));
            }

        }

    }

    @Override
    public String commandName() {
        return "setpeds";
    }

    @Override
    public String[] args() {
        return new String[]{"name", "amount"};
    }
}
