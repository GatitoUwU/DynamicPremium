package im.thatneko.dynamicpremium.commons.utils;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
public interface ReturnableCallback<T, R> {
    R call(T t) throws Exception;
}
