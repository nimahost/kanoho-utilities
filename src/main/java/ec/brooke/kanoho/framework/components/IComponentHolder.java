package ec.brooke.kanoho.framework.components;

import java.util.Optional;

public interface IComponentHolder {

    /**
     * Fetches the instance of the provided {@link ComponentType} if it exists on this object
     * @param component The component to get the instance of
     * @return An optional containing the instance if it was found
     */
    <T> Optional<T> kanoho$get(ComponentType<T> component);

    /**
     * Sets the value of the provided {@link ComponentType} on this object
     * @param component The component to set the value of
     * @param value The value to set
     */
    <T> void kanoho$set(ComponentType<T> component, T value);

    /**
     * Checks whether the specified component exists on this holder
     * @param component The component to check for
     * @return True if the component exists
     */
    boolean kanoho$contains(ComponentType<?> component);

    /**
     * Removes the specified component from this holder
     * @param component The component to remove
     */
    void kanoho$remove(ComponentType<?> component);
}
