package com.example.uistart;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.example.lib.*;
import com.example.uistart.R;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TakeAttendance extends Activity {

	private static final DateFormat TIME_FORMAT = SimpleDateFormat
			.getDateTimeInstance();
	private LinearLayout CardContent;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private NdefMessage mNdefPushMessage;
//	private JSONArray reg, students, courses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attendanceviewer);
		CardContent = (LinearLayout) findViewById(R.id.attendanceList);
//		resolveIntent(getIntent());

		//get broadcasted NFC detected 
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mNdefPushMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				"Message from NFC Reader :-)", Locale.ENGLISH, true) });
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			// nfc is closed in the device
			if (mAdapter.isEnabled() == false) {
//				 showWirelessSettingsDialog();
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

	private long getDec(byte[] bytes) {
		long result = 0;
		long factor = 1;
		for (int i = 0; i < bytes.length; ++i) {
			long value = bytes[i] & 0xffl;
			result += value * factor;
			factor *= 256l;
		}
		return result;
	}

	void resolveIntent(Intent intent) {
		String action = intent.getAction();
		System.out.println("action = " + action);
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
				Parcelable tag = intent
						.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				byte[] payload = dumpTagData(tag).getBytes();
				NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN,
						empty, id, payload);
				NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
				msgs = new NdefMessage[] { msg };
			}
			// Setup the views
			buildTagViews(msgs);
		}
	}

	void buildTagViews(NdefMessage[] msgs) {
		if (msgs == null || msgs.length == 0) {
			return;
		}
		LayoutInflater inflater = LayoutInflater.from(this);
		LinearLayout content = CardContent;

		// Parse the first message in the list
		// Build views for all of the sub records
		Date now = new Date();
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
		final int size = records.size();
		for (int i = 0; i < size; i++) {
			TextView timeView = new TextView(this);
			timeView.setText(TIME_FORMAT.format(now));
			content.addView(timeView, 0);
			ParsedNdefRecord record = records.get(i);
			content.addView(record.getView(this, inflater, content, i), 1 + i);
			content.addView(
					inflater.inflate(R.layout.carddivider, content, false),
					2 + i);
		}
	}

	private String dumpTagData(Parcelable p) {
		StringBuilder sb = new StringBuilder();
		Tag tag = (Tag) p;
		final byte[] id = tag.getId();
		// sb.append("Tag ID (hex): ").append(getHex(id)).append("\n");
		sb.append("Tag ID (dec): ").append(getDec(id)).append("\n");
		// TODO
		new AsyncTask<String, Void, String>() {
			/*
			 * Tryout for demo transmit the RFID id in decimal number to
			 * database UID is getDec(id) cid is the date of the test that I
			 * hard code in.
			 */

			@Override
			protected String doInBackground(String... params) {

				new HttpRequestUtils();
				return JsonParser.AttendanceCreate(String.valueOf(getDec(id)),
						"test0323");// 0311
			}

			@Override
			protected void onPostExecute(String result) {
				System.out.println(result);// 0311
				super.onPostExecute(result);
			}
		}.execute();
		// }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

		// sb.append("ID (reversed): ").append(getReversed(id)).append("\n");

		// String prefix = "android.nfc.tech.";
		// sb.append("Technologies: ");
		// for (String tech : tag.getTechList()) {
		// sb.append(tech.substring(prefix.length()));
		// sb.append(", ");
		// }
		// sb.delete(sb.length() - 2, sb.length());
		// for (String tech : tag.getTechList()) {
		// if (tech.equals(MifareClassic.class.getName())) {
		// sb.append('\n');
		// MifareClassic mifareTag = MifareClassic.get(tag);
		// String type = "Unknown";
		// switch (mifareTag.getType()) {
		// case MifareClassic.TYPE_CLASSIC:
		// type = "Classic";
		// break;
		// case MifareClassic.TYPE_PLUS:
		// type = "Plus";
		// break;
		// case MifareClassic.TYPE_PRO:
		// type = "Pro";
		// break;
		// }
		// sb.append("Mifare Classic type: ");
		// sb.append(type);
		// sb.append('\n');
		//
		// sb.append("Mifare size: ");
		// sb.append(mifareTag.getSize() + " bytes");
		// sb.append('\n');
		//
		// sb.append("Mifare sectors: ");
		// sb.append(mifareTag.getSectorCount());
		// sb.append('\n');
		//
		// sb.append("Mifare blocks: ");
		// sb.append(mifareTag.getBlockCount());
		// }

		// if (tech.equals(MifareUltralight.class.getName())) {
		// sb.append('\n');
		// MifareUltralight mifareUlTag = MifareUltralight.get(tag);
		// String type = "Unknown";
		// switch (mifareUlTag.getType()) {
		// case MifareUltralight.TYPE_ULTRALIGHT:
		// type = "Ultralight";
		// break;
		// case MifareUltralight.TYPE_ULTRALIGHT_C:
		// type = "Ultralight C";
		// break;
		// }
		// sb.append("Mifare Ultralight type: ");
		// sb.append(type);
		// }
		// }

		return sb.toString();
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

	@Override
	public void onNewIntent(Intent intent) {
		System.out.println("-->>onNewIntent");
		setIntent(intent);
		resolveIntent(intent);
	}
}
