package service;

import model.Employee;

/**
 * Represents the app session component used in the service layer.
 */
public final class AppSession {

    private static volatile Employee currentUser;

    /**
     * Creates a new AppSession instance.
     */
    private AppSession() {}

    /**
     * Returns current user.
     * @return resulting value produced by this method.
     */
    public static Employee getCurrentUser() {
        return currentUser;
    }

    /**
     * Updates current user.
     * @param employee input value needed by this method.
     */
    public static void setCurrentUser(Employee employee) {
        currentUser = employee;
    }

    /**
     * Handles clear.
     */
    public static void clear() {
        currentUser = null;
    }
}
