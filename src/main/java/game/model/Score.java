package game.model;

public class Score implements Comparable<Score> {
    private String playerName;
    private int deathCount;

    public Score(String playerName, int deathCount) {
        this.playerName = playerName;
        this.deathCount = deathCount;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.deathCount, other.deathCount); // Menor deathCount es mejor
    }

    @Override
    public String toString() {
        return playerName + ": " + deathCount + " muertes";
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getDeathCount() {
        return deathCount;
    }
}