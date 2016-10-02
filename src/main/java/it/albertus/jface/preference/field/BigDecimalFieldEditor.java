package it.albertus.jface.preference.field;

import it.albertus.jface.listener.BigDecimalVerifyListener;
import it.albertus.util.Configured;

import java.math.BigDecimal;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class BigDecimalFieldEditor extends AbstractDecimalFieldEditor<BigDecimal> {

	public BigDecimalFieldEditor(final String name, final String labelText, final Composite parent) {
		super(name, labelText, parent);
		getTextControl().addVerifyListener(new BigDecimalVerifyListener(new Configured<Boolean>() {
			@Override
			public Boolean getValue() {
				return getMinValidValue() == null || getMinValidValue().compareTo(BigDecimal.ZERO) < 0;
			}
		}));
		getTextControl().addFocusListener(new BigDecimalFocusListener());
	}

	@Override
	protected boolean doCheckState() {
		final Text text = getTextControl();
		try {
			final BigDecimal number = new BigDecimal(text.getText());
			if (checkValidRange(number)) {
				return true;
			}
		}
		catch (final NumberFormatException nfe) {/* Ignore */}
		return false;
	}

	@Override
	protected void doLoad() {
		super.doLoad();
		final Text text = getTextControl();
		if (text != null) {
			String value;
			try {
				value = new BigDecimal(getPreferenceStore().getString(getPreferenceName())).toString();
			}
			catch (final NumberFormatException nfe) {
				value = "";
			}
			text.setText(value);
			oldValue = value;
		}
		updateFontStyle();
	}

	@Override
	protected void doStore() throws NumberFormatException {
		final Text text = getTextControl();
		if (text != null) {
			if (text.getText().isEmpty() && isEmptyStringAllowed()) {
				getPreferenceStore().setValue(getPreferenceName(), "");
			}
			else {
				final BigDecimal value = new BigDecimal(text.getText());
				getPreferenceStore().setValue(getPreferenceName(), value.toString());
			}
		}
	}

	@Override
	protected String getDefaultValue() {
		final String defaultValue = super.getDefaultValue();
		try {
			new BigDecimal(defaultValue);
			return defaultValue;
		}
		catch (final NumberFormatException nfe) {
			return "";
		}
	}

	@Override
	public BigDecimal getNumberValue() throws NumberFormatException {
		return new BigDecimal(getStringValue());
	}

	protected class BigDecimalFocusListener extends FocusAdapter {
		@Override
		public void focusLost(final FocusEvent fe) {
			final Text text = (Text) fe.widget;
			final String oldText = text.getText();
			try {
				final String newText = new BigDecimal(oldText).toString();
				if (!oldText.equals(newText)) {
					text.setText(newText);
				}
				valueChanged();
			}
			catch (final Exception e) {}
		}
	}

}
