package org.sopeco.persistence.dataset;

import java.io.Serializable;

import org.sopeco.persistence.dataset.util.ParameterType;
import org.sopeco.persistence.dataset.util.ParameterUtil;
import org.sopeco.persistence.entities.definition.ParameterDefinition;

/**
 * A value associated to a particular parameter.
 * 
 * @author Jens Happe, Dennis Westermann
 * 
 * @param <T>
 *            Type of the value.
 */
public class ParameterValue<T> implements Serializable, Comparable<ParameterValue<?>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 448079727442973952L;
	private ParameterDefinition parameter;
	private T value;

	protected ParameterValue(ParameterDefinition parameter, T value) {
		super();
		validate(parameter, value);
		this.parameter = parameter;
		this.value = value;
	}

	private void validate(ParameterDefinition parameter, Object value) {
		if (ParameterUtil.getTypeEnumeration(parameter.getType()).equals(ParameterType.DOUBLE)
				&& !(value instanceof Double)) {
			throw new IllegalArgumentException("Invalid value type for parameter " + parameter);
		} else if (ParameterUtil.getTypeEnumeration(parameter.getType()).equals(ParameterType.INTEGER)
				&& !(value instanceof Integer)) {
			throw new IllegalArgumentException("Invalid value type for parameter " + parameter);
		} else if (ParameterUtil.getTypeEnumeration(parameter.getType()).equals(ParameterType.STRING)
				&& !(value instanceof String)) {
			throw new IllegalArgumentException("Invalid value type for parameter " + parameter);
		} else if (ParameterUtil.getTypeEnumeration(parameter.getType()).equals(ParameterType.BOOLEAN)
				&& !(value instanceof Boolean)) {
			throw new IllegalArgumentException("Invalid value type for parameter " + parameter);
		}
	}

	public ParameterDefinition getParameter() {
		return parameter;
	}

	public T getValue() {
		return value;
	}

	public void setValue(Object value) {
		@SuppressWarnings("unchecked")
		T convertedValue = (T) ParameterValueFactory.convertValue(value,
				ParameterUtil.getTypeEnumeration(parameter.getType()));
		this.value = convertedValue;
	}

	@Override
	public int compareTo(ParameterValue<?> o) {
		if (value instanceof Integer && o.getValue() instanceof Integer) {
			return ((Integer) value).compareTo((Integer) o.getValue());
		} else if (value instanceof Double && o.getValue() instanceof Double) {
			if (Math.abs((Double) value - (Double) o.getValue()) < 0.0000000000000001) {
				return 0;
			} else if ((Double) value < (Double) o.getValue()) {
				return -1;
			} else {
				return 1;
			}
		} else if (value instanceof Boolean && o.getValue() instanceof Boolean) {
			return ((Boolean) value).compareTo((Boolean) o.getValue());

		} else if (value instanceof String && o.getValue() instanceof String) {
			return ((String) value).compareTo((String) o.getValue());

		} else {
			throw new IllegalArgumentException(
					"Comparison supported only for Double, Integer, Boolean and String ParameterValues! But value of "
							+ this.getParameter().getFullName() + " is of type " + this.getParameter().getType());
		}
	}

	public String getValueAsString() {

		if (value instanceof Double) {
			return Double.toString((Double) value);
		} else if (value instanceof Integer) {
			return Integer.toString((Integer) value);
		} else if (value instanceof Boolean) {
			return Boolean.toString((Boolean) value);
		} else if (value instanceof String) {
			return (String) value;
		}
		return "";
	}

	public double getValueAsDouble() {
		if (value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Integer) {
			return ((Integer) value).doubleValue();
		} else
			throw new IllegalStateException(
					"The function getValueAsDouble is supported only by Double and Integer ParameterValues! But value of "
							+ this.getParameter().getFullName() + " is of type " + this.getParameter().getType());
	}

	public boolean getValueAsBoolean() {
		if (value instanceof Boolean) {
			return (Boolean) value;
		} else
			throw new IllegalStateException(
					"The function getValueAsBoolean is supported only by Boolean ParameterValues! But value of "
							+ this.getParameter().getFullName() + " is of type " + this.getParameter().getType());
	}

	public int getValueAsInteger() {
		if (value instanceof Double) {
			return ((Double) value).intValue();
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else
			throw new IllegalStateException(
					"The function getValueAsInteger is supported only by Double and Integer ParameterValues! But value of "
							+ this.getParameter().getFullName() + " is of type " + this.getParameter().getType());
	}

	public ParameterValue<?> copy() {

		if (value instanceof Double) {
			return new ParameterValue<Double>(parameter, (Double) value);
		} else if (value instanceof Integer) {
			return new ParameterValue<Integer>(parameter, (Integer) value);
		} else if (value instanceof Boolean) {
			return new ParameterValue<Boolean>(parameter, (Boolean) value);
		} else if (value instanceof String) {
			return new ParameterValue<String>(parameter, (String) value);
		}
		return null;
	}

	// @SuppressWarnings("unchecked")
	// public boolean isDefaultValue() {
	// T defaultValue = null;
	// if (parameter.getParameterDefinition().getDefaultValue() == null){
	// if (value instanceof Double) {
	// defaultValue = (T)new Double(0.0);
	// } else if (value instanceof Integer) {
	// defaultValue = (T)new Integer(0);
	// } else if (value instanceof Boolean) {
	// defaultValue = (T)new Boolean(false);
	// } else if (value instanceof String) {
	// defaultValue = (T)"";
	// }
	//
	// } else {
	// defaultValue =
	// (T)ParameterValueFactory.convertValue(parameter.getParameterDefinition().getDefaultValue(),
	// parameter.getType());
	// }
	// return value.equals(defaultValue);
	// }
}
