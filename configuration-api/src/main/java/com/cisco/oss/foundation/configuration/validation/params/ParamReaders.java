package com.cisco.oss.foundation.configuration.validation.params;

import com.cisco.oss.foundation.configuration.ConfigUtil;
import com.cisco.oss.foundation.configuration.validation.exceptions.GeneralConfigException;
import org.apache.commons.configuration.Configuration;

import java.util.*;

/**
 * Created By: kgreen
 * Date-Time: 10/3/13 8:38 AM
 */
public class ParamReaders {

    public final static Class SET_CLASS = LinkedHashSet.class;

    private final static ParamReaders instance = new ParamReaders();

    private final static KeysComparator keysComparator = new KeysComparator();
    private final static Object[] emptyObjectArray = new Object[0];
    private final static Class[] emptyClassArray = new Class[0];


    /** readers instances **/
    public final static StringReader stringReader = new StringReader();
    public final static LongReader longReader = new LongReader();
    public final static IntReader intReader = new IntReader();
    public final static BooleanReader booleanReader = new BooleanReader();
    public final static FloatReader floatReader = new FloatReader();
    public final static DoubleReader doubleReader = new DoubleReader();
    public final static ShortReader shortReader = new ShortReader();
    public final static ByteReader byteReader = new ByteReader();

    public final static SetReader<String> stringSetReader = new SetReader(SET_CLASS, stringReader);
    public final static SetReader<Long> longSetReader = new SetReader(SET_CLASS, longReader);
    public final static SetReader<Integer> intSetReader = new SetReader(SET_CLASS, intReader);
    public final static SetReader<Boolean> booleanSetReader = new SetReader(SET_CLASS, booleanReader);
    public final static SetReader<Float> floatSetReader = new SetReader(SET_CLASS, floatReader);
    public final static SetReader<Double> doubleSetReader = new SetReader(SET_CLASS, doubleReader);
    public final static SetReader<Short> shortSetReader = new SetReader(SET_CLASS, shortReader);
    public final static SetReader<Byte> byteSetReader = new SetReader(SET_CLASS, byteReader);

    public final static ListReader<String> stringListReader = new ListReader(stringReader);
    public final static ListReader<Long> longListReader = new ListReader(longReader);
    public final static ListReader<Integer> intListReader = new ListReader(intReader);
    public final static ListReader<Boolean> booleanListReader = new ListReader(booleanReader);
    public final static ListReader<Float> floatListReader = new ListReader(floatReader);
    public final static ListReader<Double> doubleListReader = new ListReader(doubleReader);
    public final static ListReader<Short> shortListReader = new ListReader(shortReader);
    public final static ListReader<Byte> byteListReader = new ListReader(byteReader);

    private Configuration configuration;
    //==================================================================================================================

    /**
     * a reader interface for reading from the configuration
     */
    public static interface ParamReader<T> {
        public T readValue(String name) ;
        public T readValue(String name, T defValue) ;
    }
    //==================================================================================================================

    private ParamReaders () {}
    //==================================================================================================================

    public static ParamReaders getInstance () {
        return instance;
    }
    //==================================================================================================================

    /**
     * reader implementations
     */
    public static class LongReader implements ParamReader<Long> {
        @Override
        public Long readValue(String name) {
            return config().getLong(name);
        }

        @Override
        public Long readValue(String name, Long defValue) {
            return config().getLong(name, defValue);
        }
    }
    //==================================================================================================================

    public static class IntReader implements ParamReader<Integer> {
        @Override
        public Integer readValue(String name) {
            return config().getInt(name);
        }

        @Override
        public Integer readValue(String name, Integer defValue) {
            return config().getInt(name, defValue);
        }
    }
    //==================================================================================================================

    public static class StringReader implements ParamReader<String> {
        @Override
        public String readValue(String name) {
            return config().getString(name);
        }

        @Override
        public String readValue(String name, String defValue) {
            return config().getString(name, defValue);
        }
    }
    //==================================================================================================================

    public static class BooleanReader implements ParamReader<Boolean> {
        @Override
        public Boolean readValue(String name) {
            return config().getBoolean(name);
        }

        @Override
        public Boolean readValue(String name, Boolean defValue) {
            return config().getBoolean(name, defValue);
        }
    }
    //==================================================================================================================

    public static class FloatReader implements ParamReader<Float> {
        @Override
        public Float readValue(String name) {
            return config().getFloat(name);
        }

        @Override
        public Float readValue(String name, Float defValue) {
            return config().getFloat(name, defValue);
        }
    }
    //==================================================================================================================

    public static class DoubleReader implements ParamReader<Double> {
        @Override
        public Double readValue(String name) {
            return config().getDouble(name);
        }

        @Override
        public Double readValue(String name, Double defValue) {
            return config().getDouble(name, defValue);
        }
    }
    //==================================================================================================================

    public static class ShortReader implements ParamReader<Short> {
        @Override
        public Short readValue(String name) {
            return config().getShort(name);
        }

        @Override
        public Short readValue(String name, Short defValue) {
            return config().getShort(name, defValue);
        }
    }
    //==================================================================================================================

    public static class ByteReader implements ParamReader<Byte> {
        @Override
        public Byte readValue(String name) {
            return config().getByte(name);
        }

        @Override
        public Byte readValue(String name, Byte defValue) {
            return config().getByte(name, defValue);
        }
    }
    //==================================================================================================================

    public static class SetReader<V> implements ParamReader<Set<V>> {

        private Class<? extends Set<V>> set;
        private ParamReader<V> reader;

        public SetReader (Class<? extends Set<V>> set, ParamReader<V> reader) {
            this.set = set;
            this.reader = reader;
        }

        @Override
        public Set<V> readValue(String name) {

            Set<V> valueSet = readValue(name, null);
            if (valueSet == null) {
                valueSet = factorSetInstance();
            }

            return valueSet;
        }

        @Override
        public Set<V> readValue(String name, Set defValue) {

            // get map of all values as are inserted by they configured order
            TreeMap<String, V> map = getValuesMap(name, reader);

            if (map == null || map.size() == 0) {
                return defValue;
            }

            Set<V> valueSet = factorSetInstance();
            valueSet.addAll(map.values());

            return valueSet;

        }

        private Set<V> factorSetInstance() {

            try {
                return set.newInstance();
            } catch (Exception e) {
                GeneralConfigException.convertToRuntimeException(e);
            }

            return null;
        }
    }
    //==================================================================================================================

    public static class ListReader<V> implements ParamReader<List<V>> {

        private ParamReader<V> reader;

        public ListReader (ParamReader<V> reader) {
            this.reader = reader;
        }

        @Override
        public List<V> readValue(String name) {

            List list = readValue(name, null);
            if (list == null) {
                list = new ArrayList();
            }

            return list;
        }

        @Override
        public List<V> readValue(String name, List defValue) {

            // get map of all values as are inserted by they configured order
            TreeMap<String, V> map = getValuesMap(name, reader);

            if (map == null || map.size() == 0) {
                return defValue;
            }

            Collection<V> values = map.values();
            if (values instanceof List) {
                return (List<V>)values;
            }

            ArrayList<V> list = new ArrayList(values.size());
            list.addAll(values);

            return list;
        }

    }
    //==================================================================================================================

    /**
     * this ParamReader is used for complex types where the configuration value is extracted by invoking a specific method
     */
    public static class MethodReader<T> implements ParamReader<T> {

        private Object invoker;
        private String methodName;
        private Object[] args;
        private Class[] argsClass;

        /**
          * <invoker> can be an instance OR a Class reference. In order to activate a STATIC method, pass the class reference.
          */
        public MethodReader (Object invoker, String methodName) {
            this(invoker, methodName, emptyObjectArray);
        }

        public MethodReader (Object invoker, String methodName, Object ... args) {
            this.invoker = invoker;
            this.methodName = methodName;
            this.args = args;

            if (args.length == 0) {
                this.argsClass = emptyClassArray;
            } else {
                this.argsClass = new Class[args.length];
                for (int i=0; i<args.length; i++) {
                    this.argsClass[i] = args[i].getClass();
                }
            }
        }

        @Override
        public T readValue(String name) {

            // invoke method to get value
            try {
                Class clazz = invoker.getClass();
                Object instance = invoker;

                if (invoker.getClass() == Class.class) {
                    clazz = (Class)invoker;
                    instance = null;
                }

                return (T)clazz.getMethod(methodName, argsClass).invoke(instance, args);

            } catch (Throwable e) {
                GeneralConfigException.convertToRuntimeException(e); // throw exception as Runtime exception
            }
            return null; // should not get here
        }

        @Override
        public T readValue(String name, T defValue) {

            T value = readValue(name);
            if (value == null) {
                return defValue;
            }
            return value;
        }
    }
    //==================================================================================================================

    public static class KeysComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {

            if (o1==o2 || o1.equals(o2)) {
                return 0;
            }

			// get if keys represent arrays. If so, compare the keys by their array index
            int indexOf1 = o1.lastIndexOf(".");
            if (indexOf1 < 0) {
                return o1.compareTo(o2);
            }

            int indexOf2 = o2.lastIndexOf(".");
            if (indexOf2 < 0) {
                return o1.compareTo(o2);
            }

            indexOf1++;
            indexOf2++;

            if (ConfigUtil.isNumeric(o1, indexOf1, -1) &&
                ConfigUtil.isNumeric(o2, indexOf2, -1)) {

                Comparable n1 = ConfigUtil.toNumber(o1, indexOf1, -1);
                Comparable n2 = ConfigUtil.toNumber(o2, indexOf2, -1);

                if (n1 == null || n2 == null) {
                    return o1.compareTo(o2);
                }

                return n1.compareTo(n2);
            }

            return o1.compareTo(o2);
        }
    }
    //==================================================================================================================
    //==================================================================================================================

    public void setConfiguration (Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the configuration
     */
    private static Configuration config() {
        return instance.configuration;
    }
    //==================================================================================================================

    /**
     * return a map with all keys and values where the entries are sorted by the configured order
     */
    private static<V> TreeMap<String, V> getValuesMap (String prefix, ParamReader<V> reader) {

        TreeMap<String, V> map = null;

        Iterator iter = config().getKeys(prefix);
        while(iter.hasNext()){
            String key = iter.next().toString();

            if (map == null) {
                map = new TreeMap(keysComparator); // make sure that all keys are inserted by their configured order
            }

            map.put(key, reader.readValue(key));
        }

        return map;
    }
    //==================================================================================================================

}
