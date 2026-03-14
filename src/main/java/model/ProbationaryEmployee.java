package model;

import java.util.EnumSet;

/**
 * Represents the probationary employee component used in the model layer.
 */
public class ProbationaryEmployee extends Employee {

    /**
     * Creates a new ProbationaryEmployee instance.
     * @param data input value needed by this method.
     */
    public ProbationaryEmployee(String[] data) {
        super(data);
    }

    
    /**
     * Handles permissions.
     * @return resulting value produced by this method.
     */
    @Override
    protected EnumSet<Permission> permissions() {
        return EnumSet.of(Permission.VIEW_EMPLOYEES);
    }
}
