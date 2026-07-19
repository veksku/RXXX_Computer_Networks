package r01jan2026.z02;

public class GlobalStats {

    private long count = 0;
    private long sum = 0;
    private int min = Integer.MAX_VALUE;
    private int max = Integer.MIN_VALUE;

    public void update(int[] numbers) {
        for (int n : numbers) {
            count++;
            sum += n;
            if (n < min) min = n;
            if (n > max) max = n;
        }
    }

    public String snapshot() {
        if (count == 0) {
            return "nema podataka";
        }
        double avg = (double) sum / count;
        return String.format("min=%d max=%d avg=%.2f (na osnovu %d brojeva)", min, max, avg, count);
    }
}