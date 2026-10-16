package robot;

import command.Command;
import command.CommandParser;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Robot implements BoardElement {

    /**
     * Actual position on the board
     */
    private Position position;

    /**
     * Actual direction of the robot
     */
    @Setter private Direction direction;

    /**
     * Actual board where the robot is
     */
    @Setter private Board plateau;

    /**
     * Actual object carried by the robot
     */
    private DroppedObject objetPorte;

    /**
     * Parser used to parse commands
     */
    private final CommandParser parser = new CommandParser();

    /**
     * History of executed commands
     */
    private final List<Command> history = new ArrayList<>();

    /**
     * Flag to indicate if the robot is recording commands or not
     */
    private boolean recording = true;

    /**
     * Constructor of the robot
     * @param plateau The board where the robot is
     */
    public Robot(Board plateau) {
        this(new Position(0, 0), Direction.EAST, plateau);
    }

    /**
     * Constructor of the robot
     * @param position The initial position of the robot
     * @param direction The initial direction of the robot
     * @param plateau The board where the robot is
     */
    public Robot(Position position, Direction direction, Board plateau) {
        this.plateau = plateau;
        this.direction = direction;
        setPosition(position);
    }

    public List<Command> getHistory() {
        return Collections.unmodifiableList(this.history);
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

    // ========== Command execution ==========
    public void startRecording() {
        this.recording = true;
    }

    public void stopRecording() {
        this.recording = false;
    }

    public void resetHistory() {
        this.history.clear();
    }

    public void recordCommand(Command command) {
        if (this.recording && command.isRecordable()) {
            this.history.add(command);
        }
    }

    public void replay() {
        List<Command> commandsToReplay = new ArrayList<>(this.history);
        boolean wasRecording = this.recording;
        this.recording = false;

        try {
            for (Command command : commandsToReplay) {
                command.execute(this);
            }
        } finally {
            this.recording = wasRecording;
        }
    }

    public void execute(String script) {
        if (script == null || script.isBlank()) return;

        String[] lines = script.trim().split("\\R");
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            Command command = parser.parse(trimmed);
            recordCommand(command);
            command.execute(this);
        }
    }
}