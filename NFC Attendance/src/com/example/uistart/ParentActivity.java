package com.example.uistart;

import java.nio.charset.Charset;
import java.util.Locale;

import com.example.lib.HttpRequestUtils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;

public abstract class ParentActivity extends Activity {

	private NfcAdapter mAdapter;// mAdapter
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				"Message from NFC Reader :-)", Locale.ENGLISH, true) });
		
		new HttpRequestUtils();
	}

	@Override
	public void onNewIntent(Intent intent) {
		System.out.println("-->>onNewIntent");
		setIntent(intent);
		resolveIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			// nfc is closed in the device
			if (mAdapter.isEnabled() == false) {
				//
				// showWirelessSettingsDialog();
			}
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
			mAdapter.enableForegroundNdefPush(this, mNdefPushMessage);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
			mAdapter.disableForegroundNdefPush(this);
		}
	}

	private NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}
	/**
	 * detect RFID and get information
	 * call "buildTagViews" at the end to set views
	 * @param intent
	 */
	void resolveIntent(Intent intent) {
		String action = intent.getAction();
//		System.out.println("action = " + action);
		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
				|| NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			Parcelable[] rawMsgs = intent
					.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
			NdefMessage[] msgs;
			if (rawMsgs != null) {
				msgs = new NdefMessage[rawMsgs.length];
				for (int i = 0; i < rawMsgs.length; i++) {
					msgs[i] = (NdefMessage) rawMsgs[i];
				}
			} else {
				// Unknown tag type
				byte[] empty = new byte[0];
				byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
				Parcelable data = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				Tag tag = (Tag) data;
				// dumpTagData(getDec(tag.getId()));
				byte[] payload = dumpTagData(getDec(tag.getId())).getBytes();
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, id, payload);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			// Setup the views
			buildTagViews(msgs);
		}
	}

	public static long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	/**
	 * data transmission
	 * @param id
	 * @return
	 */
	protected abstract String dumpTagData(long id);

	/**
	 * view generation
	 * @param msgs
	 */
	protected abstract void buildTagViews(NdefMessage[] msgs);
}
