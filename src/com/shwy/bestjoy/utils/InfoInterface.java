package com.shwy.bestjoy.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
/**
 * ���еĽ�������Ӧ��ʵ�ָýӿڣ�ͨ��Ϊ�˷��㣬����ʹ�øýӿڵ�Ĭ��ʵ����{@link InfoInterfaceImpl}.
 * @author chenkai
 *
 */
public interface InfoInterface {

	public boolean saveInDatebase(ContentResolver cr, ContentValues addtion);
	
}
