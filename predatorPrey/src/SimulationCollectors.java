import java.util.stream.Collector;

public final class SimulationCollectors {
    private SimulationCollectors() {}

    private static final class Accumulator {
        long count;
        long sum;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;

        void add(int hunger) {
            count++;
            sum += hunger;
            min = Math.min(min, hunger);
            max = Math.max(max, hunger);
        }

        Accumulator merge(Accumulator other) {
            count += other.count;
            sum += other.sum;
            min = Math.min(min, other.min);
            max = Math.max(max, other.max);
            return this;
        }
    }

    public static Collector<Creature, ?, HungerSummary> hungerSummary() {
        return Collector.of(
            Accumulator::new,
            (acc, creature) -> acc.add(creature.getHunger()),
            Accumulator::merge,
            acc -> new HungerSummary(
                acc.count,
                acc.count == 0 ? 0 : acc.min,
                acc.count == 0 ? 0 : acc.max,
                acc.count == 0 ? 0 : (double) acc.sum / acc.count)
        );
    }
}