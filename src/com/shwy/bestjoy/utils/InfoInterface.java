package com.shwy.bestjoy.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
/**
 * 所有的解析对象都应该实现该接口，通常为了方便，我们使用该接口的默认实现类{@link InfoInterfaceImpl}.
 * @author chenkai
 *
 */
public interface InfoInterface {

	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion);
	
}
