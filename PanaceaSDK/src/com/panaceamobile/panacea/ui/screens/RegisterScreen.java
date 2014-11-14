package com.panaceamobile.panacea.ui.screens;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

import com.panaceamobile.panacea.sdk.PanaceaApplication;
import com.panaceamobile.panacea.sdk.PanaceaSDK;
import com.panaceamobile.panacea.sdk.exception.PMInvalidPhoneNumberException;

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
public final class RegisterScreen extends MainScreen {
	/**
	 * Creates a new MyScreen object
	 */

	private PhoneNumberView phoneNumberView;
	private VerifyView verifyView;
	private boolean allowResendMenuItem = false;
	private ButtonField registerButton;
	private ButtonField verifyButtonField;

	protected HorizontalFieldManager navigationStrip;
	protected VerticalFieldManager navigationStripContainer;
	protected BitmapField logo;
	private boolean verificationStep = false;
	private LabelField nextButton;
	private LabelField backButton;
	private LabelField registrationTitle;

	public RegisterScreen() {

		Background screenColor = BackgroundFactory
				.createSolidBackground(Color.WHITE);
		Manager backg = getMainManager();
		backg.setBackground(screenColor);
		setBackground(screenColor);
		// initialise top strip
		navigationStrip = new HorizontalFieldManager(Field.USE_ALL_WIDTH
				| Manager.HORIZONTAL_SCROLL | Field.FIELD_VCENTER) {

			protected void paint(Graphics graphics) {
				super.paint(graphics);
			};
		};
		navigationStrip.setBackground(BackgroundFactory
				.createLinearGradientBackground(Color.BLACK, Color.BLACK,
						Color.BLACK, Color.BLACK));

		navigationStripContainer = new VerticalFieldManager();
		navigationStripContainer.add(navigationStrip);
		logo = new BitmapField(
				((PanaceaApplication) UiApplication.getUiApplication())
						.getAppIcon(),
				Field.FIELD_VCENTER);
		logo.setPadding(5, 5, 5, 5);
		navigationStrip.add(logo);

		registrationTitle = new LabelField("Register", Field.FIELD_VCENTER) {
			public void paint(Graphics g) {
				g.setBackgroundColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.WHITE);
				g.clear();
				super.paint(g);
			}
		};
		registrationTitle.setPadding(5, 5, 5, 5);
		navigationStrip.add(registrationTitle);

		nextButton = new LabelField("NEXT", Field.FIELD_VCENTER
				| Field.FOCUSABLE) {
			boolean _inFocus = false;

			public void onFocus(int direction) {
				_inFocus = true;
				super.onFocus(direction);
				this.invalidate();
			}

			public void onUnfocus() {
				_inFocus = false;
				super.onUnfocus();
				this.invalidate();
			}

			public void paint(Graphics g) {
				g.setBackgroundColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.WHITE);
				if (_inFocus) {
					g.setBackgroundColor(Color.BLUE);
				} else {
					g.setBackgroundColor(Color.BLACK);
				}
				g.clear();
				super.paint(g);
			}

			public boolean navigationClick(int status, int time) {

				if (!verificationStep) {
					// check if there is a number
					if (phoneNumberView.mobileNumberTextField.getText().length() > 0) {
						sendPhoneNumber(phoneNumberView.getNumber());

						verificationStep = true;
						rebuildTopBar();

						showVerifyView();
					} else {
						Dialog.alert("Please enter a valid number");
					}
					return true;

				} else {
					PanaceaSDK.getInstance().registerVerification(
							verifyView.getVerificationCode());
					return true;

				}
			}
		};

		backButton = new LabelField("BACK", Field.FIELD_VCENTER
				| Field.FOCUSABLE) {
			boolean _inFocus = false;

			public void onFocus(int direction) {
				_inFocus = true;
				super.onFocus(direction);
				this.invalidate();
			}

			public void onUnfocus() {
				_inFocus = false;
				super.onUnfocus();
				this.invalidate();
			}

			public void paint(Graphics g) {
				g.setBackgroundColor(Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(Color.WHITE);
				if (_inFocus) {
					g.setBackgroundColor(Color.BLUE);
				} else {
					g.setBackgroundColor(Color.BLACK);
				}
				g.clear();
				super.paint(g);
			}

			public boolean navigationClick(int status, int time) {
				sendPhoneNumber(phoneNumberView.getNumber());

				if (verificationStep) {
					verificationStep = false;
					rebuildTopBar();

					showPhoneNumberView();
					return true;
				}

				return super.navigationClick(status, time);
			}
		};

		nextButton.setFont(nextButton.getFont().derive(Font.BOLD));
		backButton.setFont(backButton.getFont().derive(Font.BOLD));

		rebuildTopBar();

		setBanner(navigationStripContainer);

		showPhoneNumberView();

	}

	public void rebuildTopBar() {
		int nextIndex = -1;
		if (backButton.getIndex() != -1) {
			navigationStrip.delete(backButton);
		}
		if (nextButton.getIndex() != -1) {
			nextIndex = nextButton.getIndex();
			navigationStrip.delete(nextButton);
		}

		if (verificationStep) {

			nextButton.setPadding(0, 10, 0, 0);
			backButton.setPadding(
					0,
					10,
					0,
					Display.getWidth()
							- registrationTitle.getFont().getAdvance(
									registrationTitle.getText())
							- logo.getBitmapWidth()
							- nextButton.getFont().getAdvance(
									nextButton.getText())
							- backButton.getFont().getAdvance(
									backButton.getText()) - 40);

			navigationStrip.insert(backButton, nextIndex);
			navigationStrip.insert(nextButton, nextIndex + 1);
		} else {

			nextButton.setPadding(
					0,
					0,
					0,
					Display.getWidth()
							- registrationTitle.getFont().getAdvance(
									registrationTitle.getText())
							- logo.getBitmapWidth()
							- nextButton.getFont().getAdvance(
									nextButton.getText()) - 80);
			navigationStrip.add(nextButton);

		}

	}

	public void showVerifyView() {
		registrationTitle.setText("Verify");

		if (registerButton != null && registerButton.getManager() != null)
			registerButton.getManager().delete(registerButton);

		allowResendMenuItem = true;

		verificationStep = true;

		verifyView = new VerifyView();

		add(verifyView);
		if (phoneNumberView != null && phoneNumberView.getIndex() != -1) {
			delete(phoneNumberView);
		}
	}

	public void showPhoneNumberView() {
		registrationTitle.setText("Register");

		if (verifyButtonField != null && verifyButtonField.getManager() != null)
			verifyButtonField.getManager().delete(verifyButtonField);

		phoneNumberView = new PhoneNumberView();

		registerButton = new ButtonField("Register", ButtonField.CONSUME_CLICK);
		add(phoneNumberView);

		if (verifyView != null && verifyView.getIndex() != -1) {
			delete(verifyView);
		}

	}

	public boolean onSavePrompt() {
		return true;
	}

	protected void makeMenu(Menu menu, int instance) {
		super.makeMenu(menu, instance);
		if (allowResendMenuItem)
			menu.add(sendSms);
	}

	private MenuItem sendSms = new MenuItem("Resend SMS", 1, 10) {
		public void run() {
			sendPhoneNumber(phoneNumberView.getNumber());
		}
	};

	private void sendPhoneNumber(String currentPhoneNumber) {

		try {
			PanaceaSDK.getInstance().registerPhoneNumber(currentPhoneNumber);

		} catch (PMInvalidPhoneNumberException e) {
			// TODO Auto-generated catch block
			Dialog d = new Dialog(Dialog.OK, "Error: " + e.getMessage(), 0,
					null, 0);
			d.show();
		}
	}
}
