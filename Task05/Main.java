import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Team> teams = new ArrayList<>();
        teams.add(new Team("Eagles", 9, 6));
        teams.add(new Team("Bisons", 9, 4));
        teams.add(new Team("Panthers", 9, 4));
        teams.add(new Team("Cobras", 6, 1));

        Leaderboard<Team> leaderboard = new Leaderboard<>(TEAM_COMPARATOR);
        leaderboard.addAll(teams);

        System.out.println(leaderboard.ranking());
        System.out.println("Bisons ranks above Panthers because they have the same points and goal difference, so the tie-breaker is the name in ascending order.");

        Team big = new Team("Alpha", Integer.MAX_VALUE, 0);
        Team small = new Team("Beta", Integer.MIN_VALUE, 0);
        Comparator<Team> faultyComparator = (left, right) -> left.points() - right.points();
        System.out.println("Faulty comparator example has extreme-value risk: " + faultyComparator.compare(big, small));
    }

    private static final Comparator<Team> TEAM_COMPARATOR = Comparator
            .comparingInt(Team::points)
            .reversed()
            .thenComparing(Comparator.comparingInt(Team::goalDifference).reversed())
            .thenComparing(Team::name);

    public record Team(String name, int points, int goalDifference) {
        @Override
        public String toString() {
            return name + "(" + points + "," + goalDifference + ")";
        }
    }
}
