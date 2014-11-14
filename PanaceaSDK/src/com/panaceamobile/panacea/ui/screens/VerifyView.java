package com.panaceamobile.panacea.ui.screens;

import com.panaceamobile.panacea.sdk.PanaceaSDK;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.ui.text.TextFilter;

public class VerifyView extends VerticalFieldManager {
	private EditField verifiedTextField;

	public VerifyView() {
		LabelField l = new LabelField(
				"SMS sent. Please enter the verification code received",
				LabelField.USE_ALL_WIDTH | DrawStyle.HCENTER);

		l.setPadding(5, 5, 0, 5);
		l.setFont(l.getFont().derive(Font.PLAIN, 8, Ui.UNITS_pt));

		verifiedTextField = new EditField("", "", 5, TextField.NO_NEWLINE) {
			public void layout(int width, int height) {
				super.layout(width, height);
				setExtent(getFont().getAdvance("XXXXXXXXXX"), getFont()
						.getHeight() + 5);
			}
			protected void fieldChangeNotify(int context) {
				if (this.getText().length() == 5)
					PanaceaSDK.getInstance().registerVerification(
							getVerificationCode());
				super.fieldChangeNotify(context);
			}
		};
		verifiedTextField.setPadding(2, 2, 2, 2);
		XYEdges xyEdge = new XYEdges(0, 0, 2, 0);
		XYEdges xyEdgeColors = new XYEdges(0x00dddddd, 0x00dddddd, 0x00dddddd,
				0x00dddddd);
		Border aBorder = BorderFactory.createSimpleBorder(xyEdge, xyEdgeColors,
				Border.STYLE_SOLID);
		verifiedTextField.setBorder(aBorder);
		verifiedTextField.setFont(l.getFont()
				.derive(Font.PLAIN, 8, Ui.UNITS_pt));
		verifiedTextField.setFilter(TextFilter.get(TextFilter.NUMERIC));

		add(verifiedTextField);
		add(l);
	}

	public String getVerificationCode() {
		return verifiedTextField.getText();
	}
}
