package de.netzkronehd.hitboxutils.velocity.commands;

import de.netzkronehd.hitboxutils.message.HexColor;
import de.netzkronehd.hitboxutils.utils.Utils;
import de.netzkronehd.hitboxutils.velocity.HitBoxUtils;
import de.netzkronehd.hitboxutils.velocity.player.HitBoxPlayer;

import java.awt.*;
import java.util.List;

public class GradientColorCommand extends HitBoxCommand {

    private final String testMessage;

    public GradientColorCommand(HitBoxUtils hitBoxUtils) {
        super(hitBoxUtils, "gradientcolor");
        this.testMessage = "This is a test message to show the gradient of colors!";
    }

    @Override
    public void onExecute(HitBoxPlayer hp, String[] args) {
        if(!hasCommandPermission(hp)) return;
        if (args.length <= 1) {
            sendHelp(hp);
            return;
        }

        for (String arg : args) {
            if(arg.matches(HexColor.DEFATLT_HEX_PATTERN.pattern())) continue;
            hp.sendMessage("The color '§e"+arg+"§7' does not match§e "+HexColor.DEFATLT_HEX_PATTERN.pattern()+"§7.");
            return;
        }

        final List<Color> colors = Utils.getGradientColors(testMessage.length(), args);

        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < testMessage.length(); i++) {
            final String hex = "&" + String.format("#%02x%02x%02x", colors.get(i).getRed(), colors.get(i).getGreen(), colors.get(i).getBlue());
            sb.append(hex).append(testMessage.charAt(i));
        }
        hp.sendMessageColored(sb.toString());

    }

    @Override
    public void sendHelp(HitBoxPlayer hp) {
        hp.sendUsage(getSimpleName()+"§8 <§eColors§8>");
    }
}
