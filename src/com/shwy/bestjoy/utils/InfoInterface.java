package com.shwy.bestjoy.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
/**
 * {@link InfoInterfaceImpl}.
 * @author chenkai
 *
 */
public interface InfoInterface {

	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion);
	
}
