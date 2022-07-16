import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class OthelloTest {
    OthelloInterface o = Othello.of(new int[64]);
    Othello ok = Othello.of(new int[64]);

    @Test
    void testNewGame() {
        System.out.println("[testNewGame]: Anfangszustand -> " + o);
        assert Arrays.stream(o.getBoard()).allMatch(n -> ((n == 0)));

        o = o.newGame();

        System.out.println("[testNewGame]: Zustand nach dem Aufruf von der newGame-Methode -> " + o);
        assert Arrays.stream(o.getBoard()).allMatch(n -> ((n == 0) || (n == 1) || (n == -1)));
        assertAll( () -> assertEquals(o.getBoard()[27], -1),
                () -> assertEquals(o.getBoard()[28], 1),
                () -> assertEquals(o.getBoard()[35], 1),
                () -> assertEquals(o.getBoard()[36], -1) );
    }

    @Test
    void testPlay() {
        o = o.newGame();

        System.out.println("[testPlay]: Anfangszustand -> " + o);
        assertEquals(o.getBoard()[27], -1);

        o = o.play(Move.of(3, 2, 1));

        System.out.println("[testPlay]: Zustand nach dem manuellen Aufruf der play-Methode von Player 1 mit einem Zug in Reihe 3, Spalte 2 -> " + o);
        assertEquals(o.getBoard()[27], 1);
    }

    @Test
    void testIsValid() {
        o = o.newGame();
        OthelloInterface temp = o;

        System.out.println("[testIsValid]: Anfangszustand -> " + o);

        assert o.isValid(Move.of(2, 4, -1), false);
        temp = o.play(Move.of(2, 4, -1));
        System.out.println("[testIsValid]: der Zug von Spieler -1 in Reihe 2, Spalte 4 ist gültig -> " + temp);

        assert o.isValid(Move.of(3, 5, -1), false);
        temp = o.play(Move.of(3, 5, -1));
        System.out.println("[testIsValid]: der Zug von Spieler -1 in Reihe 3, Spalte 5 ist gültig -> " + temp);

        assert o.isValid(Move.of(5, 3, -1), false);
        temp = o.play(Move.of(5, 3, -1));
        System.out.println("[testIsValid]: der Zug von Spieler -1 in Reihe 5, Spalte 3 ist gültig -> " + temp);

        assert o.isValid(Move.of(4, 2, -1), false);
        temp = o.play(Move.of(4, 2, -1));
        System.out.println("[testIsValid]: der Zug von Spieler -1 in Reihe 4, Spalte 2 ist gültig -> " + temp);


        assert o.isValid(Move.of(2, 3, 1), false);
        temp = o.play(Move.of(2, 3, 1));
        System.out.println("[testIsValid]: der Zug von Spieler 1 in Reihe 2, Spalte 3 ist gültig -> " + temp);

        assert o.isValid(Move.of(3, 2, 1), false);
        temp = o.play(Move.of(3, 2, 1));
        System.out.println("[testIsValid]: der Zug von Spieler 1 in Reihe 3, Spalte 2 ist gültig -> " + temp);

        assert o.isValid(Move.of(5, 4, 1), false);
        temp = o.play(Move.of(5, 4, 1));
        System.out.println("[testIsValid]: der Zug von Spieler 1 in Reihe 5, Spalte 4 ist gültig -> " + temp);

        assert o.isValid(Move.of(4, 5, 1), false);
        temp = o.play(Move.of(4, 5, 1));
        System.out.println("[testIsValid]: der Zug von Spieler 1 in Reihe 4, Spalte 5 ist gültig -> " + temp);
    }

    @Test
    void testUndo() {
        o = o.newGame();
        OthelloInterface temp = o.play(Move.of(2, 4, -1));
        System.out.println("[testUndo]: Anfangszustand -> " + o);

        o = o.play(Move.of(2, 4, -1), Move.of(2, 5, 1));
        System.out.println("[testUndo]: Zustand nach 2 mal gespielt mit Zug von dem Spieler -1 in Reihe 2 Spalte 4 und\n" +
                "von dem Spieler 1 in Reihe 2 Spalte 5 -> " + o);

        assertEquals(o.getScore()[0], 3);
        assertEquals(o.getScore()[1], 3);

        o = o.undo();
        System.out.println("[testUndo]: Zustand nach dem Aufruf der undo-Methode -> " + o);

        assert Arrays.equals(temp.getBoard(), o.getBoard());
        System.out.println("[testUndo]: Zustand ist dasselbe, als ob der letzter Zug (von Spieler 1 in Reihe 2 Spalte 5)\n" +
                "war gar nicht gemacht");

        assertAll( () -> assertEquals(o.getScore()[0], 4),
                () -> assertEquals(o.getScore()[1], 1),
                () -> assertEquals(o.getBoard()[20], -1),
                () -> assertEquals(o.getBoard()[27], -1),
                () -> assertEquals(o.getBoard()[28], -1),
                () -> assertEquals(o.getBoard()[35], 1),
                () -> assertEquals(o.getBoard()[36], -1) );
    }

    @Test
    void testGameOver() {
        o = o.newGame();

        assert !o.isGameOver();
        System.out.println("[testGameOver]: Spielbrett hat noch freie Plätze, deswegen ist das Spiel noch nicht beendet -> " + o);

        for (int i = 0; i < 32; i++) {
            o.getBoard()[i] = -1;
        }
        for (int i = 32; i <= 63; i++) {
            o.getBoard()[i] = 1;
        }

        assert o.isGameOver();
        System.out.println("[testGameOver]: Spielbrett hat keine freie Plätze, deswegen ist das Spiel beendet -> " + o);
    }

    @Test
    void testFlip() {
        ok = ok.newGame();

        System.out.println("[testFlip]: Anfangszustand -> " + ok);

        assert ok.checkedList.isEmpty();

        ok.isValid(Move.of(2, 4, -1), true);
        assert ok.board[28] == 1;
        assertEquals(28, ok.checkedList.get(0));
        ok.flip(ok);
        assert ok.board[28] == -1;

        ok.board[21] = 1;
        ok.board[20] = -1;
        ok.isValid(Move.of(2, 3, 1), true);
        assert ok.board[20] == -1;
        assert ok.board[27] == -1;
        assertEquals(20, ok.checkedList.get(0));
        assertEquals(27, ok.checkedList.get(1));
        ok.flip(ok);
        assert ok.board[20] == 1;
        assert ok.board[27] == 1;
    }

    @Test
    void testBestMethode() {
        ok = ok.newGame();

        ok.board[0] = 1;
        for (int i = 1; i < 7; i++) {
            ok.board[i] = -1;
        }
        ok.board[63] = 1;
        for (int i = 15; i < 63; i = i + 8) {
            ok.board[i] = -1;
        }
        ok.board[56] = 1;
        for (int i = 2; i < 8; i++) {
            int j = i * 7;
            ok.board[j] = -1;
        }
        for (int i = 0; i < 8; i++) {
            int j = i * 8;
            ok.board[j] = 1;
        }
        for (int i = 57; i < 63; i++) {
            ok.board[i] =1;
        }
        for (int i = 9; i < 14; i++) {
            ok.board[i] = -1;
        }
        for (int i = 17; i < 23; i++) {
            ok.board[i] = -1;
        }
        for (int i = 25; i < 30; i++) {
            ok.board[i] = -1;
        }
        ok.board[33] = -1;
        ok.board[34] = -1;
        ok.board[41] = -1;
        ok.board[38] = -1;
        ok.board[46] = -1;
        ok.board[50] = -1;
        ok.board[51] = -1;
        for (int i = 52; i < 55; i++) {
            ok.board[i] = 1;
        }
        for (int i = 43; i < 46; i++) {
            ok.board[i] = 1;
        }
        ok.board[36] = 1;
        ok.board[37] = 1;

        System.out.println("[testBestMethode]: Anfangszustand -> " + o);

        ok.player = 1;

        Othello temp = ok.play(ok.bestMove());
        ok = ok.play(Move.of(3, 6, 1));

        System.out.println("[testBestMethode]: spielen lassen mit der bestMove-Methode -> " + temp);
        System.out.println("[testBestMethode]: manuell spielen lassen -> " + ok);
        assert Arrays.equals(temp.board, ok.getBoard());

        System.out.println("[testBestMethode]: der Gewinner des Spiels ist der 1");
        assert ok.getWinner() == 1;

        System.out.println("[testBestMethode]: der Gegner darf keinen neuen Zug machen");
        assert (ok.generateValidMove(ok.player)).isEmpty();
    }

}

