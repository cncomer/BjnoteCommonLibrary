package com.shwy.bestjoy.model;

import android.text.TextUtils;

import com.shwy.bestjoy.ComApplication;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.NetworkRequestHelper;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.PageInfo;
import com.shwy.bestjoy.utils.ServiceResultObject;
import com.shwy.bestjoy.utils.UrlEncodeStringBuilder;

import org.json.JSONObject;

import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

/**
 * Created by bestjoy on 16/6/27.
 */
public class CommonExternalizeableObject implements Externalizable {

    private static final String TAG = "CommonExternalizeableObject";
    protected File cacheFile = null;
    /**是否有缓存数据*/
    protected boolean isCachedData = false;
    /**原始内容*/
    public String srcContent = "";

//    public static List<CommonExternalizeableObject> initCommonExternalizeableObject = new LinkedList<>();


    public static final String VERSION = "CommonExternalizeableObject_v2";

    public static interface LoadExternalizeableObjectCallback {
        public void loadFinished(ServiceResultObject serviceResultObject);
    }

    public CommonExternalizeableObject(String filePath) {
        cacheFile = new File(filePath);
    }

//    public void registerInstance() {
//        synchronized (initCommonExternalizeableObject) {
//            if (!initCommonExternalizeableObject.contains(this)) {
//                initCommonExternalizeableObject.add(this);
//            }
//        }
//    }
//
//    public void unregisterInstance() {
//        synchronized (initCommonExternalizeableObject) {
//            if (initCommonExternalizeableObject.contains(this)) {
//                initCommonExternalizeableObject.remove(this);
//            }
//        }
//    }
//
//    public static void clearAllInstance() {
//        synchronized (initCommonExternalizeableObject) {
//            for(CommonExternalizeableObject commonExternalizeableObject : initCommonExternalizeableObject) {
//                commonExternalizeableObject.clear();
//            }
//            initCommonExternalizeableObject.clear();
//        }
//    }


    public void clear() {
        srcContent = "";
        isCachedData = false;
        if (cacheFile.exists()) {
            cacheFile.delete();
            DebugUtils.logD("CommonExternalizeableObject", "clear delete cached file " + cacheFile.getAbsolutePath());
        }
    }

    public File getCacheFile() {
        return cacheFile;
    }

    public boolean isCachedData() {
        return isCachedData;
    }

    public void save() throws IOException {
        DebugUtils.logD(TAG, "save " + cacheFile.getName());
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(cacheFile));
        writeExternal(os);
        os.close();
    }


    static final int BLOCK_SIZE = 2000;
    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {

        if (objectInput.readBoolean()) {
            String versionStr = objectInput.readUTF();
            int number = 1;
            if (VERSION.equals(versionStr)) {
                number = objectInput.readInt();
                final StringBuilder sb = new StringBuilder(number * BLOCK_SIZE);
                for (int i = 0; i <number; i ++) {
                    sb.append(objectInput.readUTF());
                }
                srcContent = sb.toString();
            } else{
                srcContent = versionStr;
            }

        }

    }


    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        boolean hasValue = !TextUtils.isEmpty(srcContent);
        objectOutput.writeBoolean(hasValue);
        if (hasValue) {
//            int len = srcContent.length();
//            if (len > Short.MAX_VALUE) {
//
//            }
            objectOutput.writeUTF(VERSION);

            int len = srcContent.length();
            if (len > BLOCK_SIZE) {
                int number = len / BLOCK_SIZE;
                if (0 <len % BLOCK_SIZE) number ++;
                final String [] ar = new String [number];
                for (int i = 0; i <number; i ++) {
                    final int beginIndex = i * BLOCK_SIZE;
                    final int lastInd = beginIndex + BLOCK_SIZE;
                    if (lastInd> len) {
                        ar[i] = srcContent.substring (beginIndex, len);
                    } else {
                        ar[i] = srcContent.substring (beginIndex, lastInd);
                    }
                }

                objectOutput.writeInt(number);
                for (int i = 0; i <number; i ++)
                    objectOutput.writeUTF(ar[i]);
            } else {
                objectOutput.writeInt(1);
                objectOutput.writeUTF(srcContent);
            }
        }


    }



    public void init() {
        if (cacheFile.exists()) {
            try {
                readExternal(new ObjectInputStream(new FileInputStream(cacheFile)));
                initContentValues();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void initContentValues() {
        isCachedData = true;
    }


    public static class Query {

        public String qServiceUrl;

        public PageInfo mPageInfo;

        public JSONObject mExtraQuery;

        public boolean resultIsArray;

        public Query() {}

        public String buildQueryUrl() {
            if (mExtraQuery == null) {
                return qServiceUrl;
            } else {
                UrlEncodeStringBuilder sb = new UrlEncodeStringBuilder(qServiceUrl);
                sb.append("para=").appendUrlEncodedString(mExtraQuery.toString());
                return sb.toString();
            }

        }

    }



    public static void loadExternalizeableObjectCallbackAsync(final CommonExternalizeableObject commonExternalizeableObject, final Query query, final LoadExternalizeableObjectCallback callback) {
        NetworkRequestHelper.requestAsync(new NetworkRequestHelper.IRequestRespond() {
            @Override
            public void onRequestEnd(Object result) {

                if (result instanceof ServiceResultObject) {
                    ServiceResultObject resultObject = (ServiceResultObject) result;
                    callback.loadFinished(resultObject);
                } else if (result instanceof CommonExternalizeableObject) {
                    if (query.resultIsArray) {
                        callback.loadFinished(ServiceResultObject.parseArray(commonExternalizeableObject.srcContent));
                    } else {
                        callback.loadFinished(ServiceResultObject.parse(commonExternalizeableObject.srcContent));
                    }

                }
            }

            @Override
            public void onRequestStart() {

            }

            @Override
            public void onRequestCancelled() {

            }

            @Override
            public Object doInBackground() {
                commonExternalizeableObject.init();
                if (commonExternalizeableObject.isCachedData) {
                    return commonExternalizeableObject;
                }

                ServiceResultObject resultObject = new ServiceResultObject();
                try {
                    if (query.resultIsArray) {
                        resultObject = NetworkUtils.getArrayServiceResultObjectFromUrl(query.buildQueryUrl(), null);
                    } else {
                        resultObject = NetworkUtils.getServiceResultObjectFromUrl(query.buildQueryUrl(), null);
                    }
                    if (resultObject.isOpSuccessfully()) {
                        commonExternalizeableObject.setContent(resultObject.mRawString);
                        commonExternalizeableObject.save();
                        commonExternalizeableObject.initContentValues();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resultObject.mStatusCode = -1;
                    resultObject.mStatusMessage = ComApplication.getInstance().getGeneralErrorMessage(e);
                }
                return resultObject;
            }
        });

    }

    public void setContent(String content) {
        srcContent = content;
    }

    public String toString() {
        return srcContent;
    }

}
