package nova;

/**
 * Declares that an object can be matched by a keyword search.
 */
public interface Searchable {
    boolean matches(String keyword);
}
