package gov.nist.beacon.commands;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.stereotype.Component;

import static org.springframework.shell.support.util.OsUtils.LINE_SEPARATOR;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {
    @Override
    public String getBanner() {
        StringBuilder sb = new StringBuilder();
        sb.append("          ######                                                     ").append(LINE_SEPARATOR);
        sb.append("          #     # ######   ##    ####   ####  #    #                 ").append(LINE_SEPARATOR);
        sb.append("          #     # #       #  #  #    # #    # ##   #                 ").append(LINE_SEPARATOR);
        sb.append("          ######  #####  #    # #      #    # # #  #                 ").append(LINE_SEPARATOR);
        sb.append("          #     # #      ###### #      #    # #  # #                 ").append(LINE_SEPARATOR);
        sb.append("          #     # #      #    # #    # #    # #   ##                 ").append(LINE_SEPARATOR);
        sb.append("          ######  ###### #    #  ####   ####  #    #                 ").append(LINE_SEPARATOR);
        sb.append(LINE_SEPARATOR);
        sb.append("  #####                                                              ").append(LINE_SEPARATOR);
        sb.append(" #     # #    # #    # #    #   ##   #####  # ###### ###### #####    ").append(LINE_SEPARATOR);
        sb.append(" #       #    # ##  ## ##  ##  #  #  #    # #     #  #      #    #   ").append(LINE_SEPARATOR);
        sb.append("  #####  #    # # ## # # ## # #    # #    # #    #   #####  #    #   ").append(LINE_SEPARATOR);
        sb.append("       # #    # #    # #    # ###### #####  #   #    #      #####    ").append(LINE_SEPARATOR);
        sb.append(" #     # #    # #    # #    # #    # #   #  #  #     #      #   #    ").append(LINE_SEPARATOR);
        sb.append("  #####   ####  #    # #    # #    # #    # # ###### ###### #    #   ").append(LINE_SEPARATOR);
        return sb.toString();
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String getWelcomeMessage() {
        return "Welcome to Beacon Summarizer CLI";
    }

    @Override
    public String getProviderName() {
        return "Beacon Summarizer CLI";
    }
}