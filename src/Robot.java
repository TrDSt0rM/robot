import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
public class Robot implements BoardElement {

    private Position position;
    @Setter private Direction direction;
    @Setter private Board plateau;
    private DroppedObject objetPorte;

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
            case NORTH -> new Position(position.getX(), position.getY() + distance);
            case SOUTH -> new Position(position.getX(), position.getY() - distance);
            case EAST  -> new Position(position.getX() + distance, position.getY());
            case WEST  -> new Position(position.getX() - distance, position.getY());
        };

        setPosition(nextPosition);
    }

    public void execute(String script) {
        if (script == null || script.isBlank()) return;

        // Découpage ligne par ligne (prend en compte \n et \r\n)
        String[] lines = script.trim().split("\\R");

        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // Découpage des éléments sur la ligne
            String[] parts = trimmed.split("\\s+");
            String command = parts[0];

            switch (command) {
                case "base" -> {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    setPosition(new Position(x, y));
                }
                case "dir" -> {
                    setDirection(Direction.valueOf(parts[1]));
                }
                case "mov" -> {
                    int distance = Integer.parseInt(parts[1]);
                    move(distance);
                }
                case "drop" -> {
                    drop();
                }
                default -> throw new IllegalArgumentException("Commande inconnue : " + command);
            }
        }
    }
}