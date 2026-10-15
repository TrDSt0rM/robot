package robot;

import java.util.HashMap;
import java.util.Map;

public class Board {
    private final Map<Position, Terrain> terrains;
    private final Map<Position, BoardElement> occupants;

    public Board() {
        terrains = new HashMap<>();
        occupants = new HashMap<>();
    }

    public BoardElement getAt(Position position) {
        if (occupants.containsKey(position)) {
            return occupants.get(position);
        }
        // Si aucun occupant, renvoie le terrain existant (ou un terrain par défaut)
        return terrains.computeIfAbsent(position, pos -> new Terrain());
    }

    public void put(Position position, BoardElement element) {
        occupants.put(position, element);
    }

    public void remove(Position position) {
        occupants.remove(position);
    }
}
