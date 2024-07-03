package chess;

public record Tuple<A, B>(A first, B second) {

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}

