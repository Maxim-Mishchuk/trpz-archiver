package archiver_api.output;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public record Entity(String name, Date lastModifiedDate, byte[] bytes) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(name, entity.name) && Arrays.equals(bytes, entity.bytes);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name);
        result = 31 * result + Arrays.hashCode(bytes);
        return result;
    }

    @Override
    public String toString() {
        return "archiver_api.output.Entity{" +
                "name='" + name + '\'' +
                ", bytes=" + Arrays.toString(bytes) +
                '}';
    }

    public static Entity create(String name, Date lastModifiedDate, byte[] bytes) {
        return new Entity(name, lastModifiedDate, bytes);
    }

    public static Entity create(String name) {
        return new Entity(name, null, null);
    }

    public void accept(Visitor v) {
        v.visit(this);
    }
}
