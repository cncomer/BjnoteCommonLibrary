package com.shwy.bestjoy.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;

import com.shwy.bestjoy.utils.SecurityUtils.SecurityKeyValuesObject;

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
	
	private class FeedbackAsyncTask extends AsyncTask<Void, Void, ServiceResultObject> {
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
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				serviceResultObject.mStatusMessage = e.getMessage();
			} catch (IOException e) {
				e.printStackTrace();
				serviceResultObject.mStatusMessage = e.getMessage();
			} catch(JSONException e) {
				serviceResultObject.mStatusMessage = e.getMessage();
			} catch(Exception e) {
				serviceResultObject.mStatusMessage = e.getMessage();
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
		
	}
	
}
