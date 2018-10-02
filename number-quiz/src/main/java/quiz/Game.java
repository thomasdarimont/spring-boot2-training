package quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    private boolean gameOver;

    private boolean perfect;

    private Hint hint;

    private int draw;

    private int min;

    private int max;

    private List<Integer> guesses = new ArrayList<>();

    public Game(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getTries() {
        return guesses.size();
    }

    public void guess(int guess) {

        guesses.add(guess);

        this.hint = rate(guess);

        if (Hint.MATCH.equals(this.hint)) {
            this.gameOver = true;
            this.perfect = guesses.size() < Math.ceil(Math.log(max - min) / Math.log(2));
        }
    }

    private Hint rate(int guess) {

        if (guess < draw) {
            return Hint.TOO_LOW;
        }

        if (guess > draw) {
            return Hint.TOO_HIGH;
        }

        return Hint.MATCH;
    }

    public void start() {
        this.draw = ThreadLocalRandom.current().nextInt(min, max);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isPerfect() {
        return perfect;
    }

    public Hint getHint() {
        return hint;
    }

    public int getDraw() {
        return draw;
    }

    void setDraw(int draw) {
        this.draw = draw;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public List<Integer> getGuesses() {
        return guesses;
    }

    enum Hint {

        TOO_LOW,

        TOO_HIGH,

        MATCH,
    }
}
