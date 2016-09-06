package com.shwy.bestjoy.utils;

import android.content.Context;

import com.shwy.bestjoy.ComApplication;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

public class FeedbackHelper {
	private Context mContext;
	public static final FeedbackHelper INSTANCE = new FeedbackHelper();

	private FeedbackHelper(){};
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public static FeedbackHelper getInstance() {
		return INSTANCE;
	}
	
	public void feedbackAsync(AbstractFeedbackObject feddbackObject) {
		new FeedbackAsyncTask(feddbackObject).execute();
	}
	
	private class FeedbackAsyncTask extends AsyncTaskCompat<Void, Void, ServiceResultObject> {
		private AbstractFeedbackObject _abstractFeedbackObject;
		private FeedbackAsyncTask(AbstractFeedbackObject abstractFeedbackObject) {
			_abstractFeedbackObject = abstractFeedbackObject;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			_abstractFeedbackObject.onFeedbackStart();
		}

		@Override
		protected ServiceResultObject doInBackground(Void... params) {
			InputStream is = null;
			ServiceResultObject serviceResultObject = new ServiceResultObject();
			try {
				serviceResultObject = _abstractFeedbackObject.parse(_abstractFeedbackObject.openConnection());
			} catch(Exception e) {
				serviceResultObject.mStatusMessage = ComApplication.getInstance().getGeneralErrorMessage(e);
			} finally {
				NetworkUtils.closeInputStream(is);
			}
			return serviceResultObject;
		}

		@Override
		protected void onPostExecute(ServiceResultObject result) {
			super.onPostExecute(result);
			_abstractFeedbackObject.onFeedbackEnd(result);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			_abstractFeedbackObject.onFeedbackonCancelled();
		}
		
	}
	
	
	public interface IFeedbackObject {
		
	}
	
    public static abstract class AbstractFeedbackObject implements IFeedbackObject{
        /***
         * 用户的意见反馈后的回调
         * @param serviceResultObject
         */
        public abstract void onFeedbackEnd(ServiceResultObject serviceResultObject);
        public abstract void onFeedbackStart();
        public abstract void onFeedbackonCancelled();
        public abstract InputStream openConnection() throws ClientProtocolException, IOException, JSONException, Exception;
        public ServiceResultObject parse(InputStream is) {
        	return ServiceResultObject.parse(NetworkUtils.getContentFromInput(is));
        }
		public Object parseGernal(InputStream is) {
			return null;
		}
	}
	
}
