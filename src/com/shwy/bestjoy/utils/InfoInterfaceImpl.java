package com.shwy.bestjoy.utils;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;

public class InfoInterfaceImpl implements InfoInterface {
	private static final String TAG = "InfoInterfaceImpl";

	@Override
	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion) {
		return false;
	}
	

	public static final void beginDocument(XmlPullParser parser,
			String firstElementName) throws XmlPullParserException, IOException {
		int type;
		while ((type = parser.next()) != XmlPullParser.START_TAG
				&& type != XmlPullParser.END_DOCUMENT) {
			;
		}
		if (type != XmlPullParser.START_TAG) {
			throw new XmlPullParserException("No start tag found");
		}

		if (!parser.getName().equals(firstElementName)) {
			throw new XmlPullParserException("Unexpected start tag: found "
					+ parser.getName() + ", expected " + firstElementName);
		}
	}

	public static final void nextStartElement(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		int type;
		while ((type = parser.next()) != XmlPullParser.START_TAG
				&& type != XmlPullParser.END_DOCUMENT) {
			;
		}
	}

	public static final String nextTextElement(XmlPullParser parser)
			throws XmlPullParserException, IOException {
//		int type;
//		while ((type = parser.next()) != XmlPullParser.TEXT
//				&& type != XmlPullParser.END_DOCUMENT) {
//			;
//		}
//		return parser.getText();
		String text = parser.nextText();
		DebugUtils.logExchangeBCParse(TAG, "Start Text "+ text);
		return text.trim();
	}

}
