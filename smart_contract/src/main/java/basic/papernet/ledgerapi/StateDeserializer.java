package basic.papernet.ledgerapi;

@FunctionalInterface
public interface StateDeserializer {
    State deserialize(byte[] buffer);
}
