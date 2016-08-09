package it.albertus.jface.preference.field;

import it.albertus.jface.JFaceResources;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public abstract class LocalizedListEditor extends ListEditor {

	private boolean localized; // Do not set any value here!
	private final boolean container;

	protected LocalizedListEditor() {
		super();
		container = false;
	}

	public LocalizedListEditor(final String name, final String labelText, final Composite parent) {
		this(name, labelText, parent, null);
	}

	public LocalizedListEditor(final String name, final String labelText, final Composite parent, final Integer horizontalSpan) {
		super(name, labelText, (horizontalSpan != null && horizontalSpan > 0) ? createContainer(parent, horizontalSpan) : parent);
		if (horizontalSpan != null && horizontalSpan > 0) {
			container = true;
		}
		else {
			container = false;
		}
	}

	protected static Composite createContainer(final Composite fieldEditorParent, final int horizontalSpan) {
		final Composite parent = new Composite(fieldEditorParent, SWT.NULL);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).span(horizontalSpan, 1).applyTo(parent);
		return parent;
	}

	@Override
	protected void checkParent(final Control control, final Composite parent) {
		super.checkParent(container ? control.getParent() : control, parent);
	}

	@Override
	public Composite getButtonBoxControl(final Composite parent) {
		final Composite buttonBox = super.getButtonBoxControl(parent);
		if (!localized) {
			final Button addButton = getAddButton();
			addButton.setText(JFaceResources.get("lbl.preferences.list.button.add"));

			final Button removeButton = getRemoveButton();
			removeButton.setText(JFaceResources.get("lbl.preferences.list.button.remove"));

			final Button upButton = getUpButton();
			upButton.setText(JFaceResources.get("lbl.preferences.list.button.up"));

			final Button downButton = getDownButton();
			downButton.setText(JFaceResources.get("lbl.preferences.list.button.down"));

			localized = true;
		}
		return buttonBox;
	}

}