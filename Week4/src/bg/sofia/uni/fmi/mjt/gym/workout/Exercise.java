package bg.sofia.uni.fmi.mjt.gym.workout;

import java.util.Objects;

public record Exercise(String name, int sets, int repetitions) {

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;

        if (o == null) return false;

        if (!(o instanceof Exercise e)) return false;

        return e.name().equals(name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
