class Main2 {
    public static void main(String[] args) {
        int MAX = 744;
        String input = "186-520";
        String[] parts = input.split("-");
        int start = Integer.parseInt(parts[0]);
        int end = Integer.parseInt(parts[1]);

        java.util.Set<Integer> used = new java.util.HashSet<>();
        int past = 0;
        used.add(0);

        for (int n = 1; n <= MAX; n++) {
            int sub = past - n;
            if (sub >= 0 && !used.contains(sub)) {
                past = sub;
            } else {
                past = past + n;
            }
            used.add(past);
        }

        int bestHour = -1;
        int bestJumps = 0;

        for (int h = start; h <= end; h++) {
            if (!used.contains(h)) continue;

            int jumps = 0;
            int current = h;
            int step = h;

            while (current <= MAX) {
                current += step;
                step++;
                if (current <= MAX) jumps++;
            }

            if (jumps > bestJumps) {
                bestJumps = jumps;
                bestHour = h;
            }
        }

        System.out.println(bestHour + ", " + bestJumps);

    }
}
