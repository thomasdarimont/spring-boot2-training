package quiz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameTests {

    @Test
    public void normalGameShouldNotReportPerfectScore() {

        Game game = new Game(1, 100);
        game.setDraw(32);

        game.guess(50);
        assertEquals(Game.Hint.TOO_HIGH, game.getHint());

        game.guess(70);
        assertEquals(Game.Hint.TOO_HIGH, game.getHint());

        game.guess(60);
        assertEquals(Game.Hint.TOO_HIGH, game.getHint());

        game.guess(30);
        assertEquals(Game.Hint.TOO_LOW, game.getHint());

        game.guess(30);
        assertEquals(Game.Hint.TOO_LOW, game.getHint());

        game.guess(31);
        assertEquals(Game.Hint.TOO_LOW, game.getHint());

        game.guess(32);
        assertEquals(Game.Hint.MATCH, game.getHint());
        assertTrue(game.isGameOver(), "Game should be over");
        assertFalse(game.isPerfect(), "Game should not be perfect");
        assertEquals(7, game.getTries());
    }

    @Test
    public void perfectGameShouldReportPerfectScore() {

        Game game = new Game(1, 100);
        game.setDraw(32);

        game.guess(50);
        assertEquals(Game.Hint.TOO_HIGH, game.getHint());

        game.guess(30);
        assertEquals(Game.Hint.TOO_LOW, game.getHint());

        game.guess(31);
        assertEquals(Game.Hint.TOO_LOW, game.getHint());

        game.guess(32);
        assertEquals(Game.Hint.MATCH, game.getHint());
        assertTrue(game.isGameOver(), "Game should be over");
        assertTrue(game.isPerfect(), "Game should be perfect");
        assertEquals(4, game.getTries());
    }
}
