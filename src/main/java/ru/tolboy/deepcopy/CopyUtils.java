package ru.tolboy.deepcopy;

import org.tinylog.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CopyUtils {
    private static final Map<Object, Object> cache = new IdentityHashMap<>();
    private static final Lock WRITE_LOCK = new ReentrantReadWriteLock().writeLock();
    private static final String LOGGER_INFO_STR = "deepCopyInternal -> class {}";
    private static final String UNSUPPORTED_EXC_STR = "Cloning Interface|Synthetic|Annotation types is not supported yet";

    /**
     * deepCopy method used to copy original object deeply
     *
     * @param obj - original object to copy
     * @return copied object
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(T obj) {
        // Lock the source object to prevent access to it while copying
        WRITE_LOCK.lock();
        try {
            if (cache.containsKey(obj)) {
                return (T) cache.get(obj);
            }
            T copy = deepCopyInternal(obj);
            cache.put(obj, copy);
            Logger.info("deepCopy -> cache size {}", cache.size());
            return copy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            cache.clear();
            WRITE_LOCK.unlock();
            Logger.info("deepCopy finally -> cache size {} (cleared), WRITE_LOCK released", cache.size());
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T deepCopyInternal(T obj) throws Exception {
        if (obj == null) {
            return null;
        }
        // If object is already in cache, return it from cache
        if (cache.containsKey(obj)) {
            return (T) cache.get(obj);
        }
        Class<?> clazz = obj.getClass();

        if (clazz.isInterface() || clazz.isAnnotation() || clazz.isSynthetic()) {
            throw new UnsupportedOperationException(UNSUPPORTED_EXC_STR);
        }
        if (clazz.isPrimitive() || clazz.isEnum() || clazz == String.class) {
            Logger.info(LOGGER_INFO_STR, clazz.getSimpleName());
            return obj;
        }
        if (Number.class.isAssignableFrom(clazz)) {
            return createNumberWrapper((Class<T>) clazz, obj);
        }
        if (obj instanceof Collection) {
            Logger.info(LOGGER_INFO_STR, clazz.getSimpleName());
            return (T) deepCopyCollection((Collection<?>) obj, (Collection<Object>) createCollectionOrDefault(clazz));
        }
        if (obj instanceof Map) {
            Logger.info(LOGGER_INFO_STR, clazz.getSimpleName());
            return (T) deepCopyMap((Map<Object, Object>) obj, (Map<Object, Object>) createMapOrDefault(clazz));
        }
        if (clazz.isArray()) {
            Logger.info(LOGGER_INFO_STR, clazz.getSimpleName());
            return (T) copyArray(obj);
        }
        Logger.info(LOGGER_INFO_STR, clazz.getSimpleName());
        return createCustomInstance((Class<T>) clazz, obj);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createNumberWrapper(Class<T> clazz, T value) {
        // We used default deprecated constructors (instead of valueOf) because it is guaranteed to create new instances
        if (clazz == Byte.class) {
            return (T) new Byte((byte) value);
        } else if (clazz == Short.class) {
            return (T) new Short((short) value);
        } else if (clazz == Integer.class) {
            return (T) new Integer((int) value);
        } else if (clazz == Long.class) {
            return (T) new Long((long) value);
        } else if (clazz == Float.class) {
            return (T) new Float((float) value);
        } else if (clazz == Double.class) {
            return (T) new Double((float) value);
        } else {
            throw new IllegalArgumentException("Unsupported wrapper class: " + clazz);
        }
    }

    private static Collection<?> createCollectionOrDefault(Class<?> clazz) {
        // lists
        if (clazz == ArrayList.class) return new ArrayList<>();
        if (clazz == LinkedList.class) return new LinkedList<>();
        // sets
        if (clazz == HashSet.class) return new HashSet<>();
        if (clazz == LinkedHashSet.class) return new LinkedHashSet<>();
        if (clazz == TreeSet.class) return new TreeSet<>();
        // default
        if (List.class.isAssignableFrom(clazz)) return new ArrayList<>(); // default for immutable lists (List.of...)
        if (Set.class.isAssignableFrom(clazz)) return new HashSet<>(); // default for immutable sets (Set.of...)
        throw new IllegalArgumentException("Unsupported collection class: " + clazz);
    }

    private static Map<?, ?> createMapOrDefault(Class<?> clazz) {
        if (clazz == HashMap.class) return new HashMap<>();
        if (clazz == TreeMap.class) return new TreeMap<>();
        if (Map.class.isAssignableFrom(clazz)) return new HashMap<>(); // default for immutable maps (Map.of...)
        throw new IllegalArgumentException("Unsupported map class: " + clazz);
    }

    private static Object copyArray(Object obj) throws Exception {
        Class<?> clazz = obj.getClass().getComponentType();
        if (clazz.isPrimitive()) {
            if (clazz == int.class) {
                return Arrays.copyOf((int[]) obj, ((int[]) obj).length);
            } else if (clazz == long.class) {
                return Arrays.copyOf((long[]) obj, ((long[]) obj).length);
            } else if (clazz == float.class) {
                return Arrays.copyOf((float[]) obj, ((float[]) obj).length);
            } else if (clazz == double.class) {
                return Arrays.copyOf((double[]) obj, ((double[]) obj).length);
            } else if (clazz == boolean.class) {
                return Arrays.copyOf((boolean[]) obj, ((boolean[]) obj).length);
            } else if (clazz == byte.class) {
                return Arrays.copyOf((byte[]) obj, ((byte[]) obj).length);
            } else if (clazz == short.class) {
                return Arrays.copyOf((short[]) obj, ((short[]) obj).length);
            } else if (clazz == char.class) {
                return Arrays.copyOf((char[]) obj, ((char[]) obj).length);
            }
        } else {
            Object[] origObjArray = (Object[]) obj;
            return deepCopyObjectArray(origObjArray, Arrays.copyOf(origObjArray, origObjArray.length));
        }
        return null;
    }

    /**
     * Try to get default constructor. If not:
     * Obtain all constructors of the class using getDeclaredConstructors() and find the
     * constructor with max parameters. If we found, we obtain the values of the
     * fields using reflection and pass them as arguments to the constructor using newInstance().
     * <p>
     * PLEASE NOTE: this approach assumes that the fields of the object correspond to the arguments of the constructor,
     * which may not always be the case. Additionally, this approach may not work for classes with complex constructors,
     * such as those with nested or non-static inner classes. Therefore, this approach should be used with caution and
     * only when other options, such as serializing and deserializing or cloning the object, are not possible.
     *
     * @param originalClass  - representation of original object
     * @param originalObject - original object
     * @param <T>            - any type
     * @return - new object of the same type as the original one
     */
    private static <T> T createCustomInstance(Class<T> originalClass, T originalObject) throws Exception {
        T copiedObject = null;
        Object[] args;
        Field[] originalFields = originalClass.getDeclaredFields();

        try {
            // Try to get default constructor
            copiedObject = originalClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            // Get custom constructor with maximum parameters
            Constructor<?> maxParamsConstructor = Arrays.stream(originalClass.getDeclaredConstructors())
                    .max(Comparator.comparingInt(Constructor::getParameterCount))
                    .orElseThrow(NoSuchElementException::new);

            maxParamsConstructor.setAccessible(true);
            Class<?>[] parameterTypes = maxParamsConstructor.getParameterTypes();

            args = new Object[parameterTypes.length];
            // Add parameters
            for (int i = 0; i < originalFields.length; i++) {
                originalFields[i].setAccessible(true);
                Object originalValue = originalFields[i].get(originalObject);
                args[i] = originalValue;
            }
            // Create instance using args had been filled above
            copiedObject = originalClass.cast(maxParamsConstructor.newInstance(args));
        }
        // Add fields to the copied object
        for (Field field : originalFields) {
            field.setAccessible(true);
            Object originalValue = field.get(originalObject);
            if (originalValue != null) {
                field.set(copiedObject, deepCopyInternal(originalValue));
            }
        }
        cache.put(originalObject, copiedObject);

        return copiedObject;
    }

    private static Collection<?> deepCopyCollection(Collection<?> collection, Collection<Object> copy) throws Exception {
        for (Object element : collection) {
            // Recursively copy each element and add it to the target collection
            copy.add(deepCopyInternal(element));
        }
        cache.put(collection, copy);
        return copy;
    }

    private static Object[] deepCopyObjectArray(Object[] array, Object[] copy) throws Exception {
        for (int i = 0; i < Array.getLength(array); i++) {
            // Recursively copy each element and add it to the target array
            Array.set(copy, i, deepCopyInternal(array[i]));
        }
        cache.put(array, copy);
        return copy;
    }

    private static <K, V> Map<K, V> deepCopyMap(Map<K, V> map, Map<K, V> copy) {
        map.forEach((key, value) -> {
            try {
                // Recursively copy each key and value and put them to the target map
                copy.put(deepCopyInternal(key), deepCopyInternal(value));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        cache.put(map, copy);
        return copy;
    }

}