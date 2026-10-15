package robot;

import command.Command;
import command.CommandParser;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Robot implements BoardElement {

    private Position position;
    @Setter private Direction direction;
    @Setter private Board plateau;
    private DroppedObject objetPorte;
    private final CommandParser parser = new CommandParser();

    public Robot(Board plateau) {
        this(new Position(0, 0), Direction.EAST, plateau);
    }

    public Robot(Position position, Direction direction, Board plateau) {
        this.plateau = plateau;
        this.direction = direction;
        setPosition(position);
    }

    public void setPosition(Position newPosition) {
        if (this.plateau != null && this.position != null) {
            this.plateau.remove(this.position);
        }
        this.position = newPosition;
        if (this.plateau != null && this.position != null) {
            this.plateau.put(this.position, this);
        }
    }

    public void pickUp(DroppedObject obj) {
        this.objetPorte = obj;
    }

    public void drop(){
        if (this.objetPorte != null && this.plateau != null && this.position != null) {

            this.plateau.put(this.position, this.objetPorte);
            this.objetPorte = null;
        }
    }


    public void move(int distance) {
        if (this.position == null) return;

        Position nextPosition = switch (direction) {
            case Direction.NORTH -> new Position(position.getX(), position.getY() + distance);
            case Direction.SOUTH -> new Position(position.getX(), position.getY() - distance);
            case Direction.EAST  -> new Position(position.getX() + distance, position.getY());
            case Direction.WEST  -> new Position(position.getX() - distance, position.getY());
        };

        setPosition(nextPosition);
    }

    public void execute(String script) {
        if (script == null || script.isBlank()) return;

        String[] lines = script.trim().split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            Command command = parser.parse(trimmed);
            command.execute(this);
        }
    }
}