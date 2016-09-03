package it.albertus.jface.preference.field;

import it.albertus.jface.JFaceMessages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UriListEditor extends LocalizedListEditor {

	public static final String URI_SPLIT_REGEX = "\\|";
	public static final char SEPARATOR = '|';

	private static final Pattern asciiPattern = Pattern.compile("^\\p{ASCII}+$");
	private static final Pattern uriPattern = Pattern.compile("^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?");

	private final Image[] images;

	public UriListEditor(final String name, final String labelText, final Composite parent, final Integer horizontalSpan, final Image[] images) {
		super(name, labelText, parent, horizontalSpan);
		this.images = images;
	}

	@Override
	protected String createList(final String[] uris) {
		final StringBuilder list = new StringBuilder();
		if (uris != null) {
			for (int index = 0; index < uris.length; index++) {
				list.append(uris[index].trim());
				if (index != uris.length - 1) {
					list.append(SEPARATOR);
				}
			}
		}
		return list.toString();
	}

	@Override
	protected String getNewInputObject() {
		final UriDialog uriDialog = new UriDialog(getShell());
		uriDialog.create();
		if (uriDialog.open() == Window.OK) {
			return uriDialog.getUri();
		}
		return null;
	}

	@Override
	protected String[] parseString(final String stringList) {
		if (stringList != null && !stringList.isEmpty()) {
			return stringList.trim().split(URI_SPLIT_REGEX);
		}
		else {
			return new String[] {};
		}
	}

	private boolean checkUri(final String uri) {
		if (uri == null || uri.isEmpty()) {
			return false;
		}
		if (!asciiPattern.matcher(uri).matches()) {
			return false;
		}
		final Matcher urlMatcher = uriPattern.matcher(uri);
		if (!urlMatcher.matches()) {
			return false;
		}
		if (urlMatcher.group(2) == null) {
			return false;
		}
		if (urlMatcher.group(4) == null) {
			return false;
		}
		if (urlMatcher.group(5) == null) {
			return false;
		}
		return true;
	}

	protected class UriDialog extends TitleAreaDialog {

		private static final int MAX_LENGTH = 2000;

		private Text textUri;
		private Button okButton;
		private Button cancelButton;
		private String uri;

		public UriDialog(final Shell parentShell) {
			super(parentShell);
		}

		@Override
		protected void configureShell(final Shell newShell) {
			super.configureShell(newShell);
			newShell.setText(JFaceMessages.get("lbl.preferences.uri.add.title"));
			if (images != null) {
				newShell.setImages(images);
			}
		}

		@Override
		public void create() {
			super.create();
			setTitle(JFaceMessages.get("lbl.preferences.uri.add.title"));
			setMessage(JFaceMessages.get("lbl.preferences.uri.add.message"), IMessageProvider.INFORMATION);
		}

		@Override
		protected Composite createDialogArea(final Composite parent) {
			final Composite area = (Composite) super.createDialogArea(parent);
			final Composite container = new Composite(area, SWT.NONE);
			GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
			GridLayoutFactory.swtDefaults().numColumns(2).equalWidth(false).applyTo(container);

			final Label labelName = new Label(container, SWT.NONE);
			labelName.setText(JFaceMessages.get("lbl.preferences.uri.add.address"));
			GridDataFactory.swtDefaults().applyTo(labelName);

			textUri = new Text(container, SWT.BORDER);
			GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(textUri);
			textUri.setTextLimit(MAX_LENGTH);
			textUri.addKeyListener(new TextKeyListener());

			return area;
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);

			okButton = getButton(IDialogConstants.OK_ID);
			okButton.setText(JFaceMessages.get("lbl.button.ok"));
			okButton.setEnabled(false);

			cancelButton = getButton(IDialogConstants.CANCEL_ID);
			cancelButton.setText(JFaceMessages.get("lbl.button.cancel"));
		}

		@Override
		protected boolean isResizable() {
			return true;
		}

		@Override
		protected void okPressed() {
			uri = textUri.getText().trim();
			super.okPressed();
		}

		public String getUri() {
			return uri;
		}

		private class TextKeyListener extends KeyAdapter {
			@Override
			public void keyReleased(final KeyEvent ke) {
				if (!checkUri(textUri.getText().trim())) {
					if (okButton.isEnabled()) {
						okButton.setEnabled(false);
					}
				}
				else {
					if (!okButton.isEnabled()) {
						okButton.setEnabled(true);
					}
				}
			}
		}
	}

}
