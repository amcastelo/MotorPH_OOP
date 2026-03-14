package dao;

import java.util.List;

/**
 * Defines the file loader contract used in the dao layer.
 */
public interface FileLoader<T> {
    List<T> loadFile();
}
