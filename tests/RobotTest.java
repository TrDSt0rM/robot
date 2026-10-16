import robot.*;
import command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RobotTest {

    private Board board;
    private Robot robot;

    @BeforeEach
    void setUp() {
        board = new Board();
        robot = new Robot(board);
    }

    // ==========================================
    // TESTS ÉTAPE 1 : Modéliser le monde du robot
    // ==========================================

    @Test
    @DisplayName("Le robot doit démarrer avec les valeurs par défaut spécifiées")
    void testInitialState() {
        assertEquals(new Position(0, 0), robot.getPosition());
        assertEquals(Direction.EAST, robot.getDirection()); // Orienté vers l'Est par défaut[cite: 2]
        assertSame(robot, board.getAt(new Position(0, 0)));
    }

    @Test
    @DisplayName("Déplacement et test officiel de l'énoncé")
    void testDeplacementEnonce() {
        // Test demandé explicitement dans l'Étape 1[cite: 2]
        robot.setPosition(new Position(4, 1));
        robot.move(10);

        assertEquals(new Position(14, 1), robot.getPosition());
        assertSame(robot, board.getAt(new Position(14, 1)));
    }

    @Test
    @DisplayName("Vérifier la restauration du terrain lorsque le robot quitte une case")
    void testRestaurationTerrainApresDeplacement() {
        Position depart = new Position(2, 3);
        robot.setPosition(depart);

        // Au départ, le robot occupe la case
        assertSame(robot, board.getAt(depart));

        // Le robot avance vers l'EST
        robot.move(5);
        Position arrivee = new Position(7, 3);

        // La nouvelle case contient le robot
        assertSame(robot, board.getAt(arrivee));

        // L'ancienne case a restauré son terrain
        BoardElement elementAncienneCase = board.getAt(depart);
        assertNotSame(robot, elementAncienneCase);
        assertTrue(elementAncienneCase instanceof Terrain, "L'ancienne case doit être redevenue un Terrain");
    }

    @Test
    @DisplayName("Déplacements dans les 4 directions cardinales")
    void testMoveDansToutesLesDirections() {
        robot.setPosition(new Position(0, 0));

        // NORTH (y + distance)
        robot.setDirection(Direction.NORTH);
        robot.move(4);
        assertEquals(new Position(0, 4), robot.getPosition());
        assertSame(robot, board.getAt(new Position(0, 4)));

        // WEST (x - distance)
        robot.setDirection(Direction.WEST);
        robot.move(2);
        assertEquals(new Position(-2, 4), robot.getPosition());
        assertSame(robot, board.getAt(new Position(-2, 4)));

        // SOUTH (y - distance)
        robot.setDirection(Direction.SOUTH);
        robot.move(3);
        assertEquals(new Position(-2, 1), robot.getPosition());
        assertSame(robot, board.getAt(new Position(-2, 1)));

        // EAST (x + distance)
        robot.setDirection(Direction.EAST);
        robot.move(5);
        assertEquals(new Position(3, 1), robot.getPosition());
        assertSame(robot, board.getAt(new Position(3, 1)));
    }

    @Test
    @DisplayName("Le changement manuel de position avec setPosition libère l'ancienne case")
    void testSetPositionRestaureAncienneCase() {
        Position pos1 = new Position(1, 1);
        Position pos2 = new Position(8, 8);

        robot.setPosition(pos1);
        assertSame(robot, board.getAt(pos1));

        robot.setPosition(pos2);
        assertSame(robot, board.getAt(pos2));

        // pos1 doit de nouveau être un Terrain[cite: 2]
        assertTrue(board.getAt(pos1) instanceof Terrain);
        assertNotSame(robot, board.getAt(pos1));
    }

    // ==========================================
    // TESTS ÉTAPE 2 & 3 : Exécuter des scripts
    //      et Introduire le patron Command
    // ==========================================

    @Test
    @DisplayName("Étape 2 - Test officiel du script complet menant à (9, 4)")
    void testExecuteScriptOfficiel() {
        String script = """
                base 4 1
                dir EAST
                mov 2
                mov 3
                dir NORTH
                mov 3
                """; // Script exact de l'étape 2

        robot.execute(script);

        // Après exécution, le robot doit se trouver en position (9, 4)
        assertEquals(new Position(9, 4), robot.getPosition());
        assertEquals(Direction.NORTH, robot.getDirection());
        assertSame(robot, board.getAt(new Position(9, 4)));
    }

    @Test
    @DisplayName("Étape 2 - Test individuel des commandes textuelles")
    void testExecuteCommandesIndividuelles() {
        // Test de base
        robot.execute("base 2 5");
        assertEquals(new Position(2, 5), robot.getPosition());

        // Test dir
        robot.execute("dir SOUTH");
        assertEquals(Direction.SOUTH, robot.getDirection());

        // Test mov
        robot.execute("mov 4");
        assertEquals(new Position(2, 1), robot.getPosition());
    }

    @Test
    @DisplayName("Étape 2 - Test de la commande drop")
    void testExecuteDrop() {
        DroppedObject objet = new DroppedObject("Balise");
        robot.pickUp(objet);
        robot.execute("base 3 3");

        assertEquals(objet, robot.getObjetPorte());

        robot.execute("drop");

        assertNull(robot.getObjetPorte());
    }

    // ==========================================
    // TESTS ÉTAPE 4 : Enregistrer et rejouer
    // ==========================================

    @Test
    @DisplayName("Étape 4 - Scénario officiel de l'énoncé pour le replay")
    void testReplayScenarioEnonce() {
        robot.execute("""
                base 4 1
                dir EAST
                mov 2
                mov 3
                dir NORTH
                mov 3
                """);
        assertEquals(new Position(9, 4), robot.getPosition());

        robot.setPosition(new Position(5, 1));
        robot.setDirection(Direction.EAST);

        robot.resetHistory();
        robot.execute("""
                mov 2
                mov 3
                dir NORTH
                mov 3
                """);

        robot.stopRecording();
        robot.setPosition(new Position(5, 1));
        robot.setDirection(Direction.EAST);
        robot.startRecording();

        robot.execute("replay");
        assertEquals(new Position(10, 4), robot.getPosition());
    }

    @Test
    @DisplayName("Étape 4 - Vérifier que les commandes de contrôle ne sont pas enregistrées")
    void testCommandesDeControleNonEnregistrees() {
        robot.resetHistory();

        robot.execute("""
                startM
                mov 3
                resM
                mov 2
                replay
                stopM
                """);

        assertEquals(1, robot.getHistory().size());
        assertTrue(robot.getHistory().get(0) instanceof MoveCommand);
    }

    @Test
    @DisplayName("Étape 4 - Vérifier que stopM empêche les commandes suivantes d'être enregistrées")
    void testStopMEmpecheEnregistrement() {
        robot.resetHistory();

        robot.execute("""
                mov 2
                stopM
                mov 5
                dir NORTH
                startM
                mov 1
                """);

        assertEquals(2, robot.getHistory().size());
        assertEquals(new Position(7, 1), robot.getPosition());
    }

    @Test
    @DisplayName("Étape 4 - Vérifier que resM vide l'historique")
    void testResMVideHistorique() {
        robot.execute("""
                mov 3
                dir SOUTH
                mov 1
                """);
        assertFalse(robot.getHistory().isEmpty());

        robot.execute("resM"); // Effacement de l'historique

        assertTrue(robot.getHistory().isEmpty(), "L'historique doit être complètement vide après resM");
    }

    @Test
    @DisplayName("Étape 4 - Vérifier que les commandes sont enregistrées dans le bon ordre")
    void testOrdreDesCommandesEnregistrees() {
        robot.resetHistory();

        robot.execute("""
                mov 4
                dir NORTH
                mov 1
                """);

        List<Command> history = robot.getHistory();
        assertEquals(3, history.size());
        assertTrue(history.get(0) instanceof MoveCommand);
        assertTrue(history.get(1) instanceof DirectionCommand);
        assertTrue(history.get(2) instanceof MoveCommand);
    }
}