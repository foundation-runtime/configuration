package com.cisco.oss.foundation.configuration.validation.params;

import com.cisco.oss.foundation.configuration.ConfigUtil;
import com.cisco.oss.foundation.configuration.validation.exceptions.ValidationConfigException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Created By: kgreen
 * Date-Time: 10/3/13 8:48 AM
 */
public class ParamValidators {

    // map with all instances of validators
    private final static ConcurrentHashMap<Class, List<ParamValidator>> validatorsMap = getValidatorsMap();
    //==================================================================================================================
    //==================================================================================================================

    public static class ParamValidator<T> {

        public static ParamValidator instance(boolean required) {
            return getValidator(ParamValidator.class, required);
        }

        public static ParamValidator instance(Param<Boolean> param) {
            return getValidator(ParamValidator.class, param);
        }
        //------------------------------------------------------------------------------------------

        protected boolean required;
        //------------------------------------------------------------------------------------------

        private ParamValidator (boolean required) {
            this.required = required;
        }
        //------------------------------------------------------------------------------------------

        public void validate(String name, T value) {
            validateAndReturnIfEmpty(name, value);
        }
        //------------------------------------------------------------------------------------------

        protected boolean validateAndReturnIfEmpty(String name, T value) {

            if (ConfigUtil.isEmpty(value)) {

                if (required) {
                    throw new ValidationConfigException("Required parameter value is empty: " + name);
                }
                return true;
            }

            return false;
        }
        //------------------------------------------------------------------------------------------

        public boolean isRequired () {
            return required;
        }
        //------------------------------------------------------------------------------------------

    }
    //==================================================================================================================
    //==================================================================================================================

    /**
     * reader implementations
     */

    public static class ParamValidatorWrapper<T> extends ParamValidator<T> {

        private Class validatorType;
        private Param ifParam;
        private Object ifValue;
        private boolean byBooleanParam = false;
        //------------------------------------------------------------------------------------------

        private ParamValidatorWrapper (Class validatorType, Param<Boolean> param) {
            super(false);
            this.validatorType = validatorType;
            this.ifParam = param;
            this.byBooleanParam = true; // indicate that validator should be set by the value of the given Boolean parameter
        }

        private ParamValidatorWrapper (Class<T> validatorType, Param<T> ifParam, T ifValue, boolean required) {
            super(required);
            this.validatorType = validatorType;
            this.required = required;
            this.ifValue = ifValue;
            this.ifParam = ifParam;
        }
        //------------------------------------------------------------------------------------------

        @Override
        public void validate(String name, T value) {

            ParamValidator validator = null;

            // get a validator instance according to if the given Boolean parameter value is enabled/disabled
            if (byBooleanParam) {
                validator = getValidator(validatorType, (Boolean)ifParam.getValue());

            // validate given <param> only if <ifParam> value equals the given <ifValue>
            } else if (ConfigUtil.equalValues(ifParam.getValue(), ifValue)) {
                validator = getValidator(validatorType, required);
            }

            // validate value
            if (validator != null) {
                validator.validate(name, value);
            }

        }
        //------------------------------------------------------------------------------------------

    }
    //==================================================================================================================
    //==================================================================================================================

    public static class FileValidator extends ParamValidator<String> {

        public static ParamValidator instance(boolean required) {
            return getValidator(FileValidator.class, required);
        }

        public static ParamValidator instance(Param<Boolean> param) {
            return getValidator(FileValidator.class, param);
        }
        //------------------------------------------------------------------------------------------

        private FileValidator (boolean required) {
            super(required);
        }
        //------------------------------------------------------------------------------------------

        @Override
        public void validate(String name, String value) {

            if (validateAndReturnIfEmpty(name, value)) {
                return;
            }

            URL url = ConfigUtil.getResource(value);
            if (url == null) {
                throw new ValidationConfigException("File '" + value + "' cannot be found (Parameter: " + name + ")");
            }
        }
        //------------------------------------------------------------------------------------------

    }
    //==================================================================================================================
    //==================================================================================================================

    public static class URLValidator extends ParamValidator<String> {

        public static ParamValidator instance(boolean required) {
            return getValidator(URLValidator.class, required);
        }

        public static ParamValidator instance(Param<Boolean> param) {
            return getValidator(URLValidator.class, param);
        }
        //------------------------------------------------------------------------------------------

        private URLValidator (boolean required) {
            super(required);
        }
        //------------------------------------------------------------------------------------------

        @Override
        public void validate(String name, String value) {

            if (validateAndReturnIfEmpty(name, value)) {
                return;
            }

            try {
                URL url = new URL(value);
            } catch (MalformedURLException e) {
                throw new ValidationConfigException("'" + value + "' is not a valid URL (Parameter: " + name + ")");
            }

        }
        //------------------------------------------------------------------------------------------

    }
    //==================================================================================================================
    //==================================================================================================================

    /**
     * a validator for verifying that a given string is a valid hexadecimal value
     */
    public static class HEXValidator extends ParamValidator<String> {

        // maximum length of hexadecimal value or a negative value if length is not to be verified
        private int maxLength;

        // indicates if the <maxLength> parameter refers to the hexadecimal in its bits representation (true) or to its
        //  number of characters (false).
        private boolean lengthInBits;

        // indicates if the <maxLength> parameter should be exactly the size of the hexadecimal value
        private boolean exactLength;

        private double maxValue = -1;
        //------------------------------------------------------------------------------------------

        public static ParamValidator instance(boolean required) {
            return getValidator(HEXValidator.class, required);
        }

        public static ParamValidator instance(Param<Boolean> param) {
            return getValidator(HEXValidator.class, param);
        }
        //------------------------------------------------------------------------------------------

        private HEXValidator (boolean required) {
            this(required, -1, false, false);
        }

        public HEXValidator (boolean required, int maxLength, boolean lengthInBits, boolean exactLength) {
            super(required);

            this.maxLength = maxLength;
            this.lengthInBits = lengthInBits;
            this.exactLength = exactLength;
        }

        public HEXValidator (boolean required, double maxValue) {
            this(required);

            this.maxValue = maxValue;
        }
        //------------------------------------------------------------------------------------------

        @Override
        public void validate(String name, String value) {

            if (validateAndReturnIfEmpty(name, value)) {
                return;
            }

            String errMsg = null;

            // validate the hexadecimal value
            if (maxValue > -1) {
                errMsg = validateHexValue(value, maxValue);

            } else {
                errMsg = validateHexValue(value, maxLength, lengthInBits, exactLength);
            }

            if (errMsg != null) {
                throw new ValidationConfigException(errMsg + " (Parameter: " + name + ")");
            }

        }
        //------------------------------------------------------------------------------------------

        /**
         * validates that the given string is a valid hexadecimal string
         *
         * @param hexValue the string value to check
         * @param maxLength maximum length of hexadecimal value or a negative value if length is not verified
         * @param lengthInBits indicates if the <maxLength> parameter refers to the hexadecimal in its bits representation
         *                     (true) or to its number of characters (false)
         * @param exactLength indicates if the <maxLength> parameter should be exactly the size of the hexadecimal value
         *
         * @return <null> if validation is OK or the exact error reason if validation is failed
         */
        public static String validateHexValue (String hexValue, int maxLength, boolean lengthInBits, boolean exactLength) {

            // get if value is hexadecimal
            if (! ConfigUtil.isHexadecimal(hexValue)) {
                return "'" + hexValue + "' is not a valid hexadecimal value.";
            }

            // if indicated NOT to check maximum length - exit
            if (maxLength < 0) {
                return null;
            }

            // validate hexadecimal value length in bits
            if (lengthInBits) {

                byte[] bytes = null;
                try {
                    bytes = Hex.decodeHex(hexValue.toCharArray());
                } catch (DecoderException e) {
                    return "'" + hexValue + "' hexadecimal value cannot be converted to bits.";
                }

                int bitsLen = Byte.SIZE * bytes.length;
                if (exactLength && bitsLen != maxLength) {
                    return "'" + hexValue + "' hexadecimal length should be exactly " + maxLength + " bits.";
                }

                if (! exactLength && bitsLen > maxLength) {
                    return "'" + hexValue + "' exceeds number of hexadecimal bits ["  + maxLength + "].";
                }

                // validate hexadecimal value length in exact number of characters
            } else if (exactLength) {

                if (hexValue.length() != maxLength) {
                    return "'" + hexValue + "' hexadecimal length should be exactly " + maxLength + " characters";
                }

                // validate hexadecimal value maximum allowed number of characters
            } else if (hexValue.length() > maxLength) {
                return "'" + hexValue + "' exceeds number of hexadecimal characters ["  + maxLength + "].";

            }

            return null;
        }

        public static String validateHexValue (String hexValue) {
            return validateHexValue(hexValue, -1, false, false);
        }

        public static String validateHexValue (String hexValue, double maxValue) {

            String errMsg = validateHexValue(hexValue);
            if (errMsg != null) {
                return errMsg;
            }

            BigInteger longValue = new BigInteger(hexValue, 16);
            if (longValue.doubleValue() > maxValue) {
                return "'" + longValue + "' exceeds maximum value ["  + new BigDecimal(maxValue).toString() + "].";
            }

            return null;
        }
        //------------------------------------------------------------------------------------------

    }
    //==================================================================================================================
    //==================================================================================================================

    public static class ParamListValidator<T, E extends Collection<T>> extends ParamValidator<E> {

        private ParamValidator<T> validator;

        public ParamListValidator (ParamValidator<T> validator) {
            this(validator, validator.required);
        }

        public ParamListValidator (ParamValidator<T> validator, boolean required) {
            super(required);
            this.validator = validator;
        }

        @Override
        public void validate(String name, E list) {

            if (validateAndReturnIfEmpty(name, list)) {
                return;
            }

            // validate each item in the list
            for (T value : list) {
                validator.validate(name, value);
            }

        }

    }
    //==================================================================================================================
    //==================================================================================================================

    /**
     * build a map of all Validator implementations by their class type and required value
     */
    private static ConcurrentHashMap<Class, List<ParamValidator>> getValidatorsMap() {

        // use <ConcurrentHashMap> in case validators are added while initializing parameters through <getValidator> method.
        ConcurrentHashMap<Class, List<ParamValidator>> map = new ConcurrentHashMap();
        addValidators(map, ParamValidator.class,    new ParamValidator(true),   new ParamValidator(false)); // default validator
        addValidators(map, FileValidator.class,     new FileValidator(true),    new FileValidator(false));
        addValidators(map, URLValidator.class,      new URLValidator(true),     new URLValidator(false));
        addValidators(map, HEXValidator.class,      new HEXValidator(true),     new HEXValidator(false));

        return map;
    }
    //==================================================================================================================

    private static<T extends ParamValidator> void addValidators (ConcurrentHashMap<Class, List<ParamValidator>> map, Class<T> clazz,
                                                                 T requiredValidator, T noneRequiredValidator) {

        // use <CopyOnWriteArrayList> in case validators are added while initializing parameters through <getValidator> method.
        List validators = new CopyOnWriteArrayList(new ParamValidator[]{requiredValidator, noneRequiredValidator});
        map.put(clazz, validators);
    }
    //==================================================================================================================

    /**
     * wrap a validator for the given class type and according to the configured value og the given parameter.
     * Meaning, the relevant validator will be created according to the the current value of <param>.
     */
    public static<E, T extends ParamValidator<E>> T getValidator(Class<T> clazz, Param<Boolean> param) {

        // get if such validator was already initialized
        List<ParamValidator> validators = validatorsMap.get(clazz);
        if (validators == null) {
            addValidators(validatorsMap, clazz, null, null);
        }

        // start searching from index 2 - from this index and on, the list preserves <ParamValidatorWrapper> specific instances
        for (int i=2; i<validators.size(); i++) {
            ParamValidator validator = validators.get(i);

            if (validator.getClass() == ParamValidatorWrapper.class &&
                ((ParamValidatorWrapper)validator).ifParam == param) {

                return (T)validator;
            }

        }

        T validator = (T)new ParamValidatorWrapper<E>(clazz, param);
        validators.add(validator);

        return validator;
    }
    //==================================================================================================================

    /**
     * return the relevant Validator by the given class type and required indication
     */
    public static<T extends ParamValidator> T getValidator(Class<T> clazz, boolean required) {

        List<ParamValidator> validators = validatorsMap.get(clazz);
        if (validators != null) {

            if (required) {
                return (T)validators.get(0);
            }

            return (T)validators.get(1);
        }

        return null;
    }
    //==================================================================================================================

}
