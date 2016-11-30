package com.shwy.bestjoy.contacts.backup.pim.vcard;

/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentProviderOperation;
import android.content.ContentProviderOperation.Builder;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.AggregationExceptions;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

/**
 * <P>
 * {@link VCardEntryHandler} implementation which commits the entry to ContentResolver.
 * </P>
 * <P>
 * Note:<BR />
 * Each vCard may contain big photo images encoded by BASE64,
 * If we store all vCard entries in memory, OutOfMemoryError may be thrown.
 * Thus, this class push each VCard entry into ContentResolver immediately.
 * </P>
 */
public class VCardEntryCommitter implements VCardEntryHandler {
    public static String LOG_TAG = "VCardEntryComitter";

    private final ContentResolver mContentResolver;
    private long mTimeToCommit;
    private ArrayList<Uri> mCreatedUris = new ArrayList<Uri>();
    private ArrayList<Uri> mJoinUris = new ArrayList<Uri>();
//add,SPRD_APP_HQ00188009,begin
    private final int MAX_OP_ONE_TIME = 40;
    private int mIndex = 0;
    private int totalContactsNum = 0;
    private static final String RAW_CONTACTS_URI = "content://com.android.contacts/raw_contacts";
    private boolean mBatchPushToDB = false;
    private boolean ifCombinateAllFlag = false;
    
    private ArrayList<ContentProviderOperation> mOperationList;
    public ArrayList<ContentProviderOperation>	getOperationList(){
    	return mOperationList;
    }   
//add,SPRD_APP_HQ00188009,end
//modify,SPRD_APP_HQ00188009,begin
    public VCardEntryCommitter(ContentResolver resolver) {
        mContentResolver = resolver;
    }
    public VCardEntryCommitter(ContentResolver resolver,boolean bBatchPushToDB,int entryCount) {
    	mContentResolver = resolver;
    	mOperationList = new ArrayList<ContentProviderOperation>();
    	totalContactsNum = entryCount;
    	mBatchPushToDB = bBatchPushToDB;
    }
    public void setCombinateAllThreadHandler(boolean ifCombinateAllFlag){
    	this.ifCombinateAllFlag = ifCombinateAllFlag;
    }  
//modify,SPRD_APP_HQ00188009,end

    public void onStart() {
//add,SPRD_APP_HQ00188009,begin   	
    	mOperationList.clear();
    	mIndex = 0;
//add,SPRD_APP_HQ00188009,end
    }

    public void onEnd() {
        if (VCardConfig.showPerformanceLog()) {
            Log.d(LOG_TAG, String.format("time to commit entries: %d ms", mTimeToCommit));
        }
//add,SPRD_APP_HQ00188009,begin   	
    	if(mOperationList.size() != 0){
    		try{
    			ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, mOperationList);
    			if(results != null && results.length > 0 && results[0] != null){
    				/*****************/
    				for(int i = 0; i < results.length; i++) {
                        if(results[i].uri.toString().startsWith(RAW_CONTACTS_URI)) {
                            mCreatedUris.add(results[i].uri);
                        }
                    }
    				/*****************/
    				ArrayList<ContentProviderOperation> JoinOperationList = 
    					new ArrayList<ContentProviderOperation> ();
    					final int diffSize = mOperationList.size();
    					for (int i = 0; i < diffSize; i++) {
    						ContentProviderOperation operation = mOperationList.get(i);
    						if (isTYPE_INSERTForContentProviderOperation(operation)
    							&& operation.getUri().getEncodedPath().contains(
    								RawContacts.CONTENT_URI.getEncodedPath())) {
    							long rawContactId = ContentUris.parseId(results[i].uri);    							
    							if (rawContactId != -1) {
    								final Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI,rawContactId);
    								
    								// convert the raw contact URI to a contact
    								// URI
    								final Uri contactLookupUri = RawContacts.getContactLookupUri(mContentResolver,rawContactUri);
    								if(contactLookupUri != null){
    									long contactId = ContentUris.parseId(contactLookupUri);
    									int sameContactId = getFirstContactID(mContentResolver,	(int) contactId);
    									if (sameContactId > -1) {
    										if(ifCombinateAllFlag){
    											getjoinAggregateOperationList(mContentResolver,JoinOperationList, contactId,sameContactId);
    									    }
    									} 
    								}
    							}
    						}
    					}
    					try {
    						if(JoinOperationList.size()>0){
    							mContentResolver.applyBatch(ContactsContract.AUTHORITY, JoinOperationList);
    							JoinOperationList.clear();
    							Log.d(LOG_TAG,"contacts Joined");
    						}

    						} catch (RemoteException e) {
    							Log.e(LOG_TAG, "Failed to apply aggregation exception batch", e);    						
    						} catch (OperationApplicationException e) {
    							Log.e(LOG_TAG, "Failed to apply aggregation exception batch", e);
    						}
    			}
    			if(results == null || results.length == 0 || results[0] == null){
    				Log.e(LOG_TAG, "results is null or it's length is 0");
    			}
    		}catch(OperationApplicationException e){
    			Log.e(LOG_TAG, String.format("%s: %s", e.toString(), e
    					.getMessage()));
    		} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		mIndex = 0; 
    		mOperationList.clear();
    	}
//add,SPRD_APP_HQ00188009,end
    }
    public final static int TYPE_INSERT = 1;
    private boolean isTYPE_INSERTForContentProviderOperation(ContentProviderOperation operation) {
//    	operation.getType() == ContentProviderOperation.TYPE_INSERT
    	try {
			Field mTypeField = ContentProviderOperation.class.getField("mType");
			mTypeField.setAccessible(true);
			int type = (Integer) mTypeField.get(operation);
			return type == TYPE_INSERT;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
    }
    
    public void onEntryCreated(final VCardEntry contactStruct) {
        long start = System.currentTimeMillis();
//add,SPRD_APP_HQ00188009,begin
        contactStruct.ifCombinateAllFlag = this.ifCombinateAllFlag;
        if (!mBatchPushToDB) {
            mCreatedUris.add(contactStruct.pushIntoContentResolver(mContentResolver));
            mTimeToCommit += System.currentTimeMillis() - start;
    		} else {
    			int j = 0;
    			j = contactStruct.pushIntoOperationList(mContentResolver,this.mOperationList, mIndex);
    			
    			mIndex = mIndex + j;
    			if (mIndex > MAX_OP_ONE_TIME) {
    				Log.d(LOG_TAG,"mIndex = " + mIndex);
    				try {
    				ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY,mOperationList);

    				// the first result is always the raw_contact. return it's
    				// uri so
    				// that it can be found later. do null checking for badly
    				// behaving
    				// ContentResolvers
    				
    				if (results != null && results.length > 0 && results[0] != null) {
    					ArrayList<ContentProviderOperation> JoinOperationList = 
    							new ArrayList<ContentProviderOperation> ();
    					final int diffSize = mOperationList.size();
    					/*****************/
        				for(int i = 0; i < results.length; i++) {
                            if(results[i].uri.toString().startsWith(RAW_CONTACTS_URI)) {
                                mCreatedUris.add(results[i].uri);
                            }
                        }
        				/*****************/
    					for (int i = 0; i < diffSize; i++) {
    						ContentProviderOperation operation = mOperationList.get(i);
    						if (isTYPE_INSERTForContentProviderOperation(operation)
    							&& operation.getUri().getEncodedPath().contains(RawContacts.CONTENT_URI.getEncodedPath())) {
    								long rawContactId = ContentUris.parseId(results[i].uri);
    								if (rawContactId != -1) {
    									final Uri rawContactUri = ContentUris.withAppendedId(RawContacts.CONTENT_URI,rawContactId);   							   									
    									// convert the raw contact URI to a contact
    									// URI
    									final Uri contactLookupUri = RawContacts.getContactLookupUri(mContentResolver,rawContactUri);
    									if(contactLookupUri != null){
    										long contactId = ContentUris.parseId(contactLookupUri); 
    										int sameContactId = getFirstContactID(mContentResolver,(int) contactId);
    										if (sameContactId > -1) {
    											if(ifCombinateAllFlag){
    												getjoinAggregateOperationList(mContentResolver,JoinOperationList, contactId,sameContactId);
    											}
    										} 
    									} 									
    								} 
    							}
    					}
    					try {
    						if(JoinOperationList.size()>0){
    							mContentResolver.applyBatch(ContactsContract.AUTHORITY, JoinOperationList); 
    							JoinOperationList.clear();
    							Log.d(LOG_TAG,"contacts Joined");
    						}
    					} catch (RemoteException e) {
    						Log.e(LOG_TAG, "Failed to apply aggregation exception batch", e);
    						// Toast.makeText(this, "merged failed", Toast.LENGTH_LONG).show();
    					} catch (OperationApplicationException e) {
    						Log.e(LOG_TAG, "Failed to apply aggregation exception batch", e);
    						//Toast.makeText(this, "Failed to apply aggregation exception batch", Toast.LENGTH_LONG).show();
    					}
    				}
    					if (totalContactsNum > MAX_OP_ONE_TIME) {
    						totalContactsNum = totalContactsNum - MAX_OP_ONE_TIME;
    					}
    					if (results == null || results.length == 0 || results[0] == null) {
    						Log.e(LOG_TAG, "results is null or it's length is 0");
    					} else {
    					//: results[0].uri;
    					}
    					mIndex = 0; 
    					mOperationList.clear();
    				} catch (RemoteException e) {
    					Log.e(LOG_TAG, String.format("%s: %s", e.toString(), e.getMessage()));
    				} catch (OperationApplicationException e) {
    					Log.e(LOG_TAG, String.format("%s: %s", e.toString(), e.getMessage()));
    				}
    			}
    		}
//add,SPRD_APP_HQ00188009,end
//		if (contactStruct.isValid()) {
//			mCreatedUris.add(contactStruct
//					.pushIntoContentResolver(mContentResolver));
//		}
//        mTimeToCommit += System.currentTimeMillis() - start;
    }

    /**
     * Returns the list of created Uris. This list should not be modified by the caller as it is
     * not a clone.
     */
   public ArrayList<Uri> getCreatedUris() {
        return mCreatedUris;
    }
   public static final String RawContacts_DISPLAY_NAME_PRIMAR = "display_name";
   public static final String RawContacts_NAME_VERIFIED = "name_verified";
//add,SPRD_APP_HQ00188009,begin
   static final String[] RAW_CONTACTS_PROJECTION = new String[] {
       RawContacts._ID, // 0
       RawContacts.CONTACT_ID, // 1
       RawContacts.ACCOUNT_TYPE, // 2
//       RawContacts.DISPLAY_NAME_PRIMARY,// 3
       //RawContacts.DISPLAY_NAME_PRIMARY杩��浣跨�瀵瑰����绗�覆�夸唬
       RawContacts_DISPLAY_NAME_PRIMAR,
   };
   private interface JoinContactQuery {
       String[] PROJECTION = {
               RawContacts._ID,
               RawContacts.CONTACT_ID,
//               RawContacts.NAME_VERIFIED,
               RawContacts_NAME_VERIFIED,
       };

       String SELECTION = RawContacts.CONTACT_ID + "=? OR " + RawContacts.CONTACT_ID + "=?";

       int _ID = 0;
       int CONTACT_ID = 1;
       int NAME_VERIFIED = 2;
   }
   private int  getFirstContactID(ContentResolver resolver,int contactId)
   {
      Cursor c = resolver.query(RawContacts.CONTENT_URI, RAW_CONTACTS_PROJECTION, 
      	 RawContacts.CONTACT_ID+"="+contactId, null, null);
      if(null == c) return -1;
       c.moveToNext();
       String displayName = c.getString(c.getColumnIndex(RawContacts_DISPLAY_NAME_PRIMAR));
       c.close();
	   if(displayName != null){
       displayName = displayName.replace("'", "''");//replace "'" with "''" when query,or it will fatal exception if include "'" in the contact.
	   }
       Cursor cursorSameName = resolver.query(RawContacts.CONTENT_URI, RAW_CONTACTS_PROJECTION, 
    		   RawContacts_DISPLAY_NAME_PRIMAR+"= '"+displayName+"' AND "+RawContacts.CONTACT_ID+"!="+contactId/*+" AND "+RawContacts.SIM_INDEX+"==0"*/, null, null);
			
      if(null != cursorSameName){
      	while(cursorSameName.moveToNext()){
      		int destContactId =  cursorSameName.getInt(c.getColumnIndex(RawContacts.CONTACT_ID));
      		cursorSameName.close();
      		return destContactId;
      	}
      	cursorSameName.close();
      }
      return -1;
   }
   private void getjoinAggregateOperationList(ContentResolver resolver,ArrayList<ContentProviderOperation> operations,
			final long contactId,final long destContactId) {
			// Load raw contact IDs for all raw contacts involved - currently edited and selected
			// in the join UIs
			Log.d(LOG_TAG,"joinAggregate: contactId = "+contactId+"destContactId ="+destContactId);
			Cursor c = resolver.query(RawContacts.CONTENT_URI,
			JoinContactQuery.PROJECTION,
			JoinContactQuery.SELECTION,
			new String[]{String.valueOf(contactId), String.valueOf(destContactId)}, null);
			
			long rawContactIds[];
			long verifiedNameRawContactId = -1;
			try {
				rawContactIds = new long[c.getCount()];
				for (int i = 0; i < rawContactIds.length; i++) {
					c.moveToNext();
					long rawContactId = c.getLong(JoinContactQuery._ID);
					rawContactIds[i] = rawContactId;
					if (c.getLong(JoinContactQuery.CONTACT_ID) == destContactId) {
						if (verifiedNameRawContactId == -1 || c.getInt(JoinContactQuery.NAME_VERIFIED) != 0){
							verifiedNameRawContactId = rawContactId;
						}
					}
				}
			} finally {
				c.close();
			}
			
			// For each pair of raw contacts, insert an aggregation exception
			// ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
			for (int i = 0; i < rawContactIds.length; i++) {
				for (int j = 0; j < rawContactIds.length; j++) {
					if (i != j) {
						buildJoinContactDiff(operations, rawContactIds[i], rawContactIds[j]);
					}
				}
			}			
			// Mark the original contact as "name verified" to make sure that the contact
			// display name does not change as a result of the join
			Builder builder = ContentProviderOperation.newUpdate(
				ContentUris.withAppendedId(RawContacts.CONTENT_URI, verifiedNameRawContactId));
			builder.withValue(RawContacts_NAME_VERIFIED, 1);
			operations.add(builder.build());
		}
   private void buildJoinContactDiff(ArrayList<ContentProviderOperation> operations,
           long rawContactId1, long rawContactId2) {
       Builder builder =
               ContentProviderOperation.newUpdate(AggregationExceptions.CONTENT_URI);
       builder.withValue(AggregationExceptions.TYPE, AggregationExceptions.TYPE_KEEP_TOGETHER);
       builder.withValue(AggregationExceptions.RAW_CONTACT_ID1, rawContactId1);
       builder.withValue(AggregationExceptions.RAW_CONTACT_ID2, rawContactId2);
       operations.add(builder.build());
   }
//add,SPRD_APP_HQ00188009,end
}