package net.tclproject.rpgstamina.mechanics.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.tclproject.rpgstamina.api.ExtendedPlayer;

import java.util.ArrayList;
import java.util.List;

public class Command extends CommandBase {
    @Override
    public String getCommandName() {
        return "stamina";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/stamina <set:add> <player> <value> or /stamina <get> <player>";
    }

    public void sendUsage(ICommandSender sender) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + getCommandUsage(sender)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {

        if ((args.length != 3 && !args[0].equals("get")) || (args.length != 2 && args[0].equals("get"))) {
            sendUsage(sender);
            return;
        }

        EntityPlayerMP target = getPlayer(sender, args[1]);
        ExtendedPlayer player = ExtendedPlayer.get(target);

        switch (args[0]) {
            case "set":
                try {
                    int value = Integer.parseInt(args[2]);
                    if (value < 0) value = 0;
                    if (value > player.getMaxStamina()) value = player.getMaxStamina();

                    player.setCurrentStamina(value);

                    sender.addChatMessage(new ChatComponentText("Successfully set the stamina of " + args[1] + " to " + value + "."));

                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "'" + args[2] + "' isn't a number."));
                    return;
                }

                break;

            case "add":
                try {
                    int original = player.getCurrentStamina();
                    int value = player.getCurrentStamina() + Integer.parseInt(args[2]);
                    if (value > player.getMaxStamina()) value = player.getMaxStamina();
                    if (value < 0) value = 0;

                    player.setCurrentStamina(value);

                    if (Integer.parseInt(args[2]) >= 0) sender.addChatMessage(new ChatComponentText("Successfully added " + value + " to the stamina of " + args[1] + "."));
                    else sender.addChatMessage(new ChatComponentText("Successfully substracted " + (original - value) + " from the stamina of " + args[1] + "."));

                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "'" + args[2] + "' isn't a number."));
                    return;
                }

                break;

            case "get":
                sender.addChatMessage(new ChatComponentText(args[1] + " currently has " + player.getCurrentStamina() + " stamina."));
                break;

            default:
                sendUsage(sender);
                break;
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] previousArgs) {
        if (previousArgs.length == 1) return getListOfStringsMatchingLastWord(previousArgs, "set", "add", "get");
        else if (previousArgs.length == 2) return getListOfStringsMatchingLastWord(previousArgs, MinecraftServer.getServer().getAllUsernames());
        else return new ArrayList<>();
    }
}
