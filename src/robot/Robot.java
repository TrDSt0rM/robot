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

    /**
     * Get the history of executed commands
     * @return An unmodifiable list of executed commands
     */
    public List<Command> getHistory() {
        return Collections.unmodifiableList(this.history);
    }

    /**
     * Set the position of the robot on the board
     * @param newPosition The new position of the robot
     */
    public void setPosition(Position newPosition) {
        if (this.plateau != null && this.position != null) {
            this.plateau.remove(this.position);
        }
        this.position = newPosition;
        if (this.plateau != null && this.position != null) {
            this.plateau.put(this.position, this);
        }
    }

    /**
     * Pick up an object from the board
     * @param obj The object to pick up
     */
    public void pickUp(DroppedObject obj) {
        this.objetPorte = obj;
    }

    /**
     * Drop the object carried by the robot on the board
     */
    public void drop(){
        if (this.objetPorte != null && this.plateau != null && this.position != null) {

            this.plateau.put(this.position, this.objetPorte);
            this.objetPorte = null;
        }
    }

    /**
     * Move the robot in the current direction by a certain distance
     * @param distance The distance to move the robot
     */
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
    /**
     * Start recording commands
     */
    public void startRecording() {
        this.recording = true;
    }

    /**
     * Stop recording commands
     */
    public void stopRecording() {
        this.recording = false;
    }

    /**
     * Reset the history of executed commands
     */
    public void resetHistory() {
        this.history.clear();
    }

    /**
     * Record a command in the history if recording is enabled and the command is recordable
     * @param command The command to record
     */
    public void recordCommand(Command command) {
        if (this.recording && command.isRecordable()) {
            this.history.add(command);
        }
    }

    /**
     * Replay the commands in the history in order, without recording them
     * again
     */
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

    /**
     * Revert the robot to its previous state by executing the inverse of each
     * command in the history in reverse order
     */
    public void goBack() {
        List<Command> commandsToInvert = new ArrayList<>(this.history);

        boolean wasRecording = this.recording;
        this.recording = false;

        try {
            for (int i = commandsToInvert.size() - 1; i >= 0; i--) {
                Command command = commandsToInvert.get(i);
                Command inverseCmd = command.inverse();
                inverseCmd.execute(this);
            }
        } finally {
            this.recording = wasRecording;
        }
    }

    /**
     * Optimize the path by merging consecutive commands that can be merged
     * @return A list of optimized commands
     */
    public List<Command> optimizePath() {
        List<Command> optimized = new ArrayList<>(this.history);
        boolean merged = true;

        while (merged) {
            merged = false;
            for (int i = 0; i < optimized.size() - 1; i++) {
                Command current = optimized.get(i);
                Command next = optimized.get(i + 1);

                List<Command> result = current.mergeWith(next);
                if (result.size() == 1) {
                    optimized.remove(i + 1);
                    optimized.set(i, result.get(0));
                    merged = true;
                    break;
                }
            }
        }
        return optimized;
    }

    /**
     * Execute a script of commands, each command on a new line. Empty lines
     * are ignored.
     * @param script The script to execute
     */
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