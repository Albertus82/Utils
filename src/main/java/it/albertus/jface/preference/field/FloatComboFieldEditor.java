package it.albertus.jface.preference.field;

import org.eclipse.swt.widgets.Composite;

public class FloatComboFieldEditor extends NumberComboFieldEditor {

	private static final int DEFAULT_TEXT_LIMIT = 16;

	public FloatComboFieldEditor(final String name, final String labelText, final String[][] entryNamesAndValues, final Composite parent) {
		super(name, labelText, entryNamesAndValues, parent);
	}

	@Override
	protected int getDefaultTextLimit() {
		return DEFAULT_TEXT_LIMIT;
	}

	@Override
	protected boolean doCheckState() {
		try {
			final float number = Float.parseFloat(getValue());
			return checkValidRange(number);
		}
		catch (final NumberFormatException nfe) {/* Ignore */}
		return false;
	}

	@Override
	protected String cleanValue(String value) {
		value = super.cleanValue(value);
		try {
			value = Float.valueOf(value).toString();
		}
		catch (final Exception exception) {}
		return value;
	}

	@Override
	protected void cleanComboText() {
		final String oldText = getComboBoxControl().getText();
		String newText = oldText.trim();

		try {
			newText = getNameForValue(Float.valueOf(newText).toString());
		}
		catch (final Exception exception) {/* Ignore */}
		if (!newText.equals(oldText)) {
			getComboBoxControl().setText(newText);
		}
	}

	@Override
	public String getValue() {
		try {
			return Float.valueOf(super.getValue()).toString();
		}
		catch (final Exception exception) {
			return super.getValue();
		}
	}

	@Override
	protected void setValue(final String value) {
		try {
			super.setValue(Float.valueOf(value).toString());
		}
		catch (final Exception exception) {
			super.setValue(value);
		}
	}

	@Override
	protected String getNameForValue(final String value) {
		for (final String[] entry : getEntryNamesAndValues()) {
			String comboValue;
			try {
				comboValue = Float.valueOf(entry[1]).toString();
			}
			catch (final Exception e) {
				comboValue = entry[1];
			}
			if (value.equals(comboValue)) {
				return entry[0];
			}
		}
		return value; // Name not present in the array.
	}

	@Override
	protected String getDefaultValue() {
		String defaultValue = getPreferenceStore().getDefaultString(getPreferenceName());
		try {
			defaultValue = Float.toString(Float.parseFloat(defaultValue));
		}
		catch (final NumberFormatException nfe) {/* Ignore */}
		return defaultValue;
	}

	@Override
	protected NumberType getNumberType() {
		return NumberType.DECIMAL;
	}

	@Override
	public Float getMinValidValue() {
		return (Float) super.getMinValidValue();
	}

	@Override
	public void setMinValidValue(final Number min) {
		super.setMinValidValue(min != null ? min.floatValue() : null);
	}

	@Override
	public Float getMaxValidValue() {
		return (Float) super.getMaxValidValue();
	}

	@Override
	public void setMaxValidValue(final Number max) {
		super.setMaxValidValue(max != null ? max.floatValue() : null);
	}

	public Float getFloatValue() throws NumberFormatException {
		return Float.valueOf(getValue());
	}

}