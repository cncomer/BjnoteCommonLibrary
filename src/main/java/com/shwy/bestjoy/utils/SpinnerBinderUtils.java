package com.shwy.bestjoy.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.shwy.bestjoy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 建议使用单例模式
 * Created by bestjoy on 16/6/27.
 */
public class SpinnerBinderUtils {
    private Context mContext;
    private int mResource, mDropDownResource;

    public String defaultValue = "";
    public String defaultEmptyValue = "";
    public SpinnerBinderUtils(Context context){
        mContext = context;
        mResource = android.R.layout.simple_spinner_item;
        mDropDownResource = android.R.layout.simple_spinner_dropdown_item;

        defaultValue = context.getString(R.string.spinner_item_default_value);
        defaultEmptyValue = context.getString(R.string.spinner_item_empty_value);
    }

    public SpinnerBinderUtils() {

    }

    private static final SpinnerBinderUtils INSTANCE = new SpinnerBinderUtils();
    public void setContext(Context context) {
        mContext = context;
        setResouceLayout(android.R.layout.simple_spinner_item);
        setDropDownResourceLayout(android.R.layout.simple_spinner_dropdown_item);
        defaultValue = context.getString(R.string.spinner_item_default_value);
        defaultEmptyValue = context.getString(R.string.spinner_item_empty_value);
    }

    public static SpinnerBinderUtils getInstance() {
        return INSTANCE;
    }

    public void setResouceLayout(int resouceLayoutId) {
        mResource = resouceLayoutId;
    }

    public void setDropDownResourceLayout(int dropDownResourceId) {
        mDropDownResource = dropDownResourceId;
    }

    public boolean checkEmptyValue(String value) {
        return defaultEmptyValue.equals(value) || defaultValue.equals(value);
    }

    public boolean check(PolicyObject policyObject) {
        return policyObject != null && !checkEmptyValue(policyObject.mCode);
    }

    public void setLayoutResource(int resource, int dropDownResource) {
        mResource = resource;
        mDropDownResource = dropDownResource;
    }

    public static String getSpinnerCodeName(Spinner spinner) {
        PolicyObject select = getSpinnerPolicyObject(spinner);
        return select!=null?select.mCodeName:null;
    }

    /**
     * 获取选中位置的PolicyObject对象
     * @param spinner
     * @return
     */
    public static PolicyObject getSpinnerPolicyObject(Spinner spinner) {
        return getSpinnerPolicyObject(spinner, spinner.getSelectedItemPosition());
    }
    /**
     * 获取指定位置的PolicyObject对象
     * @param spinner
     * @param position
     * @return
     */
    public static PolicyObject getSpinnerPolicyObject(Spinner spinner, int position) {
        SpinnerAdapter spinnerAdapter = getSpinnerAdapter(spinner);
        if (spinnerAdapter != null) {
            return spinnerAdapter.getPolicyObject(position);
        }
        return null;
    }


    public void requireSpinner(Spinner spinner, PolicyObject[] policyObject, String currentCode) {
        SpinnerAdapter spinnerAdapter = getSpinnerAdapter(spinner);
        if (spinnerAdapter != null) {
            spinnerAdapter.changeData(policyObject);
        } else {
            spinnerAdapter = new SpinnerAdapter(mContext, mResource, policyObject);
            spinnerAdapter.setDropDownViewResource(mDropDownResource);
            spinner.setAdapter(spinnerAdapter);
        }

        int index = spinnerAdapter.findCodePosition(currentCode);
        if (index > -1) {
            spinner.setSelection(index);
        } else {
            spinner.setSelection(0);
        }
    }

    public static void setSelection(Spinner spinner, String code) {
        SpinnerAdapter spinnerAdapter = getSpinnerAdapter(spinner);
        if (spinnerAdapter != null) {
            int index =  spinnerAdapter.findCodePosition(code);
            if (index > -1) {
                spinner.setSelection(index);
            }
        }

    }

    public static SpinnerAdapter getSpinnerAdapter(Spinner spinner) {
        if (spinner.getAdapter() != null && spinner.getAdapter() instanceof SpinnerAdapter) {
            SpinnerAdapter spinnerAdapter = (SpinnerAdapter) spinner.getAdapter();
            return spinnerAdapter;
        }
        return null;
    }



    public static class SpinnerAdapter extends ArrayAdapter {

        private PolicyObject[] _policyObjects;
        public SpinnerAdapter(Context context, int resource, PolicyObject[] policyObjects) {
            this(context, resource, android.R.id.text1, policyObjects);
        }
        public SpinnerAdapter(Context context, int resource, int textViewResourceId, PolicyObject[] policyObjects) {
            super(context, resource, textViewResourceId, policyObjects);
            _policyObjects=policyObjects;
        }
        @Override
        public int getCount() {
            if (_policyObjects != null) {
                return _policyObjects.length;
            }
            return 0;
        }

        public void commitFilterCode(String filterCode) {
            if (getCount() > 0) {
                List<PolicyObject> filterPolicyObjects = new ArrayList<>(getCount());
                for(PolicyObject policyObject: _policyObjects) {
                    if ((!TextUtils.isEmpty(policyObject.mFilterCode)
                            && policyObject.mFilterCode.contains(filterCode))
                            || TextUtils.isEmpty(policyObject.mCode)) {
                        filterPolicyObjects.add(policyObject);
                    }
                }
                if (filterPolicyObjects.size() > 0) {
                    PolicyObject[] filterPolicyObject = new PolicyObject[0];
                    filterPolicyObject = filterPolicyObjects.toArray(filterPolicyObject);
                    changeData(filterPolicyObject);
                }
            }
        }

        public void changeData(PolicyObject[] policyObjects) {
            _policyObjects = policyObjects;
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view =  super.getView(position, convertView, parent);
            bindView(position, view);
            return view;
        }

        public void bindView(int position, View convertView) {
            convertView.setTag(getPolicyObject(position));
        }

        @Override
        public String getItem(int position) {
            return _policyObjects[position].mCodeName;
        }

        public PolicyObject getPolicyObject(int position) {
            return _policyObjects[position];
        }

        public PolicyObject getPolicyObject(String code) {
            int index = findCodePosition(code);
            if (index > 0) {
                return _policyObjects[index];
            }
            return null;
        }

        public String getItemCode(int position) {
            return _policyObjects[position].mCode;
        }

        public int findCodePosition(String code) {
            if (TextUtils.isEmpty(code)) {
                return -1;
            }
            int count = getCount();
            int findPos = -1;
            if (count>0) {
                for(int index=0; index<count;index++) {
                    if (getItemCode(index).equals(code)) {
                        findPos = index;
                        break;
                    }
                }
            }
            return findPos;
        }
    }



    public static class PolicyObject {
        public String mCodeName="";
        public String mCode = "";
        public String mUpCode = "";
        public String mFilterCode = "";
        public long mId = -1;
        public String mData1, mData2, mData3, mData4, mData5;
        public Object mExtraObject;

        public static PolicyObject newPolicyObject(String upcode, String code, String codeName) {
            PolicyObject policyObject = new PolicyObject();
            policyObject.mId = -1;
            policyObject.mCodeName = codeName;
            policyObject.mCode = code;
            policyObject.mUpCode = upcode;
            return policyObject;
        }

        @Override
        public String toString() {
            return mCodeName;
        }


        public String toFrendlyString() {
            return mUpCode+"-" + mCode + "-" + mCodeName;
        }

    }
}
