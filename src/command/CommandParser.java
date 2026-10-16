package command;

import robot.Direction;

import java.util.HashMap;
import java.util.Map;

public class CommandParser {
    private final Map<String, CommandFactory> factories = new HashMap<>();

    public CommandParser() {
        factories.put("base", args -> new BaseCommand(Integer.parseInt(args[1]), Integer.parseInt(args[2])));
        factories.put("dir",  args -> new DirectionCommand(Direction.valueOf(args[1])));
        factories.put("mov",  args -> new MoveCommand(Integer.parseInt(args[1])));
        factories.put("drop", args -> new DropCommand());

        factories.put("startM", args -> new StartMCommand());
        factories.put("stopM", args -> new StopMCommand());
        factories.put("resM", args -> new ResMCommand());
        factories.put("replay", args -> new ReplayCommand());
    }

    public Command parse(String line) {
        String[] parts = line.trim().split("\\s+");
        String name = parts[0];
        CommandFactory factory = factories.get(name);
        if (factory == null) {
            throw new IllegalArgumentException("Commande inconnue : " + name);
        }
        return factory.create(parts);
    }
}
