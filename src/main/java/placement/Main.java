package placement;

import placement.server.HttpApiServer;
import placement.ui.PlacementConsole;

/**
 * Main entry point for the PLACEMENT Management System.
 * Supports both terminal and web modes.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && (args[0].equalsIgnoreCase("--web") || args[0].equalsIgnoreCase("--server"))) {
            HttpApiServer.main(new String[0]);
            return;
        }
        new PlacementConsole().run();
    }
}
