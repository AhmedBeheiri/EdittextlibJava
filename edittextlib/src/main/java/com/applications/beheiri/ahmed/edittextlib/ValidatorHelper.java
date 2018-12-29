package com.applications.beheiri.ahmed.edittextlib;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ValidatorHelper {
    private static List<ValidEditText> editTextsInContext = new ArrayList();
    private static Context mContext;
    private static List<EditTextModel> editTexts = new ArrayList<>();

    public static ValidatorHelper from(Context context) {
        ValidatorHelper o = new ValidatorHelper();
        mContext = context;
        editTextsInContext.clear();
        for (int i = 0; i < editTexts.size(); i++) {
            if (editTexts.get(i).getContext() == mContext) {
                editTextsInContext.add(editTexts.get(i).getValidEditText());
                Log.d("FOUND", "Found no" + i);
            }
        }

        return o;
    }

    public static ValidatorHelper addEdittextWithInstance(ValidEditText validEditText, Context context) {
        ValidatorHelper o = new ValidatorHelper();
        EditTextModel model = new EditTextModel();
        model.setContext(context);
        model.setValidEditText(validEditText);
        editTexts.add(model);
        return o;
    }


    private ValidatorHelper() {
        // Not allowed
    }

    static boolean isvalid() {
        int validvar = 0;
        for (int i = 0; i < editTextsInContext.size(); i++) {
            if (editTextsInContext.get(i).getError() != null || (editTextsInContext.get(i).getText().toString().isEmpty() && editTextsInContext.get(i).isRequired())) {
                validvar = 1;
                for (int j = 0; j < editTextsInContext.size(); j++) {
                    if (isNullOrEmpty(editTextsInContext.get(j).getText().toString()) && editTextsInContext.get(j).isRequired()) {
                        editTextsInContext.get(j).setError(mContext.getString(R.string.this_field_empty));
                    }
                }
                break;
            } else {
                validvar = 0;
            }

        }
        return validvar == 0;
    }

    private static boolean isNullOrEmpty(String string) {
        return TextUtils.isEmpty(string);
    }

}
