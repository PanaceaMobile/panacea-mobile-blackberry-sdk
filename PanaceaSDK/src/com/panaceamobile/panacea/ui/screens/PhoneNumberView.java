package com.panaceamobile.panacea.ui.screens;

import java.util.Enumeration;
import java.util.Hashtable;

import net.rim.device.api.collection.util.BasicFilteredList;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.AutoCompleteField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.text.TextFilter;

import com.panaceamobile.panacea.sdk.PMUtils;

public class PhoneNumberView extends VerticalFieldManager implements
		FieldChangeListener {

	public EditField mobileNumberTextField;
	private ObjectChoiceField countryChoice;
	private Hashtable countryCodes;
	private EditField countryCodeTextField;
	private AutoCompleteField countryAutoCompleteField;

	public PhoneNumberView() {
		super(Field.USE_ALL_WIDTH);
		countryCodes = PMUtils.getCountryCodes();
		Enumeration countries = countryCodes.keys();
		String choices[] = new String[countryCodes.size()];
		for (int i = 0; i < countryCodes.size(); i++) {
			choices[i] = (String) countries.nextElement();
		}

		Background screenColor = BackgroundFactory
				.createSolidBackground(Color.WHITE);
		setBackground(screenColor);

		BasicFilteredList filterList = new BasicFilteredList();
		filterList.addDataSet(1, choices, "countries",
				BasicFilteredList.COMPARISON_IGNORE_CASE);

		countryAutoCompleteField = new AutoCompleteField(filterList) {

			// to get around the background being drawn all wonky
			boolean isFocused = false;

			protected void onFocus(int direction) {
				this.isFocused = true;
				invalidate();

				super.onFocus(direction);
			}

			protected void onUnfocus() {
				this.isFocused = false;
				invalidate();
				super.onUnfocus();
			}

			protected void paint(Graphics graphics) { // have to override
				// this to get rid of the annoying background
				if (this.isFocused) {
					// draw the selection marker if needed
					graphics.setColor(Color.BLUE);
					graphics.fillRect(
							countryAutoCompleteField
									.getEditField()
									.getFont()
									.getAdvance(
											countryAutoCompleteField.getEditField()
													.getText()), 0, 5, this
									.getHeight());
				}

				// draw the text again
				graphics.setColor(Color.BLACK);
				graphics.drawText(countryAutoCompleteField.getEditField().getText(),
						0, 0);
			}

			public void fieldChanged(Field field, int context) {
				countryCodeTextField.setText((String) countryCodes
						.get(countryAutoCompleteField.getEditField().getText()));
				super.fieldChanged(field, context);
			}

		};

		XYEdges noBorder = new XYEdges(countryAutoCompleteField.getTop(),
				countryAutoCompleteField.getLeft() + countryAutoCompleteField.getWidth(),
				countryAutoCompleteField.getTop() + countryAutoCompleteField.getHeight(),
				countryAutoCompleteField.getLeft());
		int lineStyle = Border.STYLE_TRANSPARENT;
		Border roundedBorder = BorderFactory.createRoundedBorder(noBorder,
				Color.WHITE, lineStyle);
		countryAutoCompleteField.getEditField().setBorder(roundedBorder);
		countryAutoCompleteField.setPadding(10, 10, 10, 10);
		countryAutoCompleteField.getEditField().setText("South Africa");
		add(countryAutoCompleteField);

		LabelField l = new LabelField("", LabelField.USE_ALL_WIDTH
				| DrawStyle.HCENTER);
		;

		l.setText("Please enter your mobile number to receive push notifications");
		l.setPadding(5, 5, 5, 5);
		l.setFont(l.getFont().derive(Font.PLAIN, 8, Ui.UNITS_pt));

		HorizontalFieldManager f = new HorizontalFieldManager();

		countryCodeTextField = new EditField("+", "27", 3, TextField.NO_NEWLINE
				| TextField.NON_FOCUSABLE) {
			protected void layout(int width, int height) {
				super.layout(width, height);
				setExtent(getFont().getAdvance("XXXXXX") + 4, getFont()
						.getHeight(Ui.UNITS_px) + 4);
			}
		};
		countryCodeTextField.setFont(l.getFont().derive(Font.PLAIN, 8,
				Ui.UNITS_pt));
		countryCodeTextField.setPadding(4, 4, 4, 4);

		mobileNumberTextField = new EditField("", "", 20, TextField.NO_NEWLINE
				| TextField.EDITABLE | Field.USE_ALL_WIDTH
				| Field.FIELD_HCENTER);
		mobileNumberTextField.setFont(l.getFont().derive(Font.PLAIN, 8,
				Ui.UNITS_pt));
		mobileNumberTextField.setMargin(2, 2, 2, 2);
		mobileNumberTextField.setPadding(2, 2, 2, 2);
		XYEdges xyEdge = new XYEdges(0, 0, 2, 0);
		XYEdges xyEdgeColors = new XYEdges(0x00dddddd, 0x00dddddd, 0x00dddddd,
				0x00dddddd);
		Border aBorder = BorderFactory.createSimpleBorder(xyEdge, xyEdgeColors,
				Border.STYLE_SOLID);
		mobileNumberTextField.setBorder(aBorder);
		mobileNumberTextField.setFilter(TextFilter.get(TextFilter.NUMERIC));
		f.add(countryCodeTextField);

		f.add(mobileNumberTextField);

		add(f);
		add(l);
	}

	public String getNumber() {
		return countryCodeTextField.getText() + mobileNumberTextField.getText();
	}

	public void fieldChanged(Field field, int context) {
		// change the country code dynamically as the country changes
		if (field == countryAutoCompleteField) {
			countryCodeTextField.setText((String) countryCodes
					.get(countryAutoCompleteField.getEditField().getText()));
		}

	}

}
