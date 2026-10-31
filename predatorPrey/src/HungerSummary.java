public record HungerSummary(long count, int min, int max, double average) {
    @Override
    public String toString() {
        return String.format("n=%d min=%d max=%d avg=%.0f", count, min, max, average);
    }
}