package com.roamprocess1.roaming4world.stripepayment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.roamprocess1.roaming4world.R;

public class PaymentFormFragment extends Fragment implements PaymentForm {

	Button saveButton;
	EditText cardNumber, edtEmaildId;
	EditText cvc;
	Spinner monthSpinner;
	Spinner yearSpinner;
	String rechargeValue;
	public static String emailId = "";
	String a;
	int keyDel;
	private String type;
	private ImageView imgCard;

	public static final String AMERICAN_EXPRESS = "americanexpress";
	public static final String DISCOVER = "discover";
	public static final String JCB = "jcb";
	public static final String DINERS_CLUB = "dinersclub";
	public static final String VISA = "visa";
	public static final String MASTERCARD = "mastercard";
	public static final String UNKNOWN = "Unknown";

	public static final String[] PREFIXES_AMERICAN_EXPRESS = { "34", "37" };
	public static final String[] PREFIXES_DISCOVER = { "60", "62", "64", "65" };
	public static final String[] PREFIXES_JCB = { "35" };
	public static final String[] PREFIXES_DINERS_CLUB = { "300", "301", "302",
			"303", "304", "305", "309", "36", "38", "37", "39" };
	public static final String[] PREFIXES_VISA = { "4" };
	public static final String[] PREFIXES_MASTERCARD = { "50", "51", "52",
			"53", "54", "55" };

	public static final int MAX_LENGTH_STANDARD = 16;
	public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
	public static final int MAX_LENGTH_DINERS_CLUB = 14;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.payment_form_fragment, container,
				false);
		this.saveButton = (Button) view.findViewById(R.id.save);
		saveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				emailId = edtEmaildId.getText().toString();
				System.out.println("rechargeValue::::" + rechargeValue
						+ "Email Id:" + edtEmaildId.getText());
				saveForm(view);
			}
		});

		this.cardNumber = (EditText) view.findViewById(R.id.cardNumber);
		this.cvc = (EditText) view.findViewById(R.id.cvc);
		this.monthSpinner = (Spinner) view.findViewById(R.id.expMonth);
		this.yearSpinner = (Spinner) view.findViewById(R.id.expYear);
		this.edtEmaildId = (EditText) view.findViewById(R.id.edtEmailId);
		this.imgCard = (ImageView) view.findViewById(R.id.imgCardType);
		emailId = edtEmaildId.getText().toString();
		Bundle extras = getActivity().getIntent().getExtras();
		rechargeValue = extras.getString("paymentValue");

		saveButton.setText("Pay $" + rechargeValue + ".00");

		String[] yearArray = getResources().getStringArray(R.array.year_array);
		String[] monthArray = getResources()
				.getStringArray(R.array.month_array);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.spinneritem, yearArray);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		yearSpinner.setAdapter(dataAdapter);

		ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinneritem, monthArray);

		monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		monthSpinner.setAdapter(monthAdapter);

		cardNumber.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				boolean flag = true;

				String cardType = getType(normalizeCardNumber(s.toString()));

				if (cardType != null)
					imgCard.setImageResource(getResources().getIdentifier(
							"com.roamprocess1.roaming4world:drawable/"
									+ cardType, null, null));

				System.out.println("Card type :" + cardType);

				String eachBlock[] = cardNumber.getText().toString().split("-");
				for (int i = 0; i < eachBlock.length; i++) {
					if (eachBlock[i].length() > 4) {
						flag = false;
					}
				}
				if (flag) {

					cardNumber.setOnKeyListener(new View.OnKeyListener() {

						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {

							if (keyCode == KeyEvent.KEYCODE_DEL)
								keyDel = 1;
							return false;
						}
					});

					if (keyDel == 0) {

						if (((cardNumber.getText().length() + 1) % 5) == 0) {

							if (cardNumber.getText().toString().split("-").length <= 3) {
								cardNumber.setText(cardNumber.getText() + "-");
								cardNumber.setSelection(cardNumber.getText()
										.length());
							}
						}
						a = cardNumber.getText().toString();
					} else {
						a = cardNumber.getText().toString();
						keyDel = 0;
					}

				} else {
					cardNumber.setText(a);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		return view;
	}

	@Override
	public String getCardNumber() {
		return this.cardNumber.getText().toString().replaceAll("-", "");
	}

	@Override
	public String getCvc() {
		return this.cvc.getText().toString();
	}

	@Override
	public Integer getExpMonth() {
		return getInteger(this.monthSpinner);
	}

	@Override
	public Integer getExpYear() {
		return getInteger(this.yearSpinner);
	}

	public void saveForm(View button) {
		((PaymentActivity) getActivity()).saveCreditCard(this);
	}

	private Integer getInteger(Spinner spinner) {
		try {
			return Integer.parseInt(spinner.getSelectedItem().toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private String normalizeCardNumber(String number) {
		if (number == null) {
			return null;
		}
		return number.trim().replaceAll("\\s+|-", "");
	}

	public String getType(String number) {
		if (TextUtils.isBlank(type) && !TextUtils.isBlank(number)) {
			if (TextUtils.hasAnyPrefix(number, PREFIXES_AMERICAN_EXPRESS)) {
				return AMERICAN_EXPRESS;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_DISCOVER)) {
				return DISCOVER;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_JCB)) {
				return JCB;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_DINERS_CLUB)) {
				return DINERS_CLUB;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_VISA)) {
				return VISA;
			} else if (TextUtils.hasAnyPrefix(number, PREFIXES_MASTERCARD)) {
				return MASTERCARD;
			} else {
				return UNKNOWN;
			}
		}

		return type;
	}

}
