package com.manojbhadane.lib_formcontrol;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manojbhadane.lib_formcontrol.adapter.AutoCompleteAdapter;

import java.util.ArrayList;

public class CustomInputLayout extends RelativeLayout implements CustomInputLayoutContract.View {

    public static final int TYPE_INPUTBOX = 1;
    public static final int TYPE_DROPDOWN = 2;
    public static final int TYPE_DATE_PICKER = 3;
    public static final int TYPE_TIME_PICKER = 4;
    public static final int TYPE_DATETIME_PICKER = 5;

    public static final int INPUTTYPE_TEXT = 1;
    public static final int INPUTTYPE_PHONE = 2;
    public static final int INPUTTYPE_NUMBER = 3;
    public static final int INPUTTYPE_EMAIL = 4;

    private static final String TAG = CustomInputLayout.class.getName();

    private Context mContext;
    private OnTouchListener mOnTouchListener;
    private AutoCompleteAdapter mAdapterAutocomplete;
    private CustomInputLayoutContract.Presenter mPresenter;

    private int mComponentType, mInputType, maxLength;
    private boolean isMandatory, isTagPrimary, isMultiline;
    private String mLabel = "", mValue;

    private TextView txtLabel, txtMandatory, txtLengthDesc;
    private RelativeLayout layInputBox, laySpinner, layLabel;
    private EditText edtInputbox;
    private ImageView iconCancel, iconDropDown;
    private InstantAutoCompleteTextView autoCompleteTxt;

    private DatePickerDialog datePickerDialog;

    public CustomInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        final LayoutInflater mInflater = LayoutInflater.from(context);
//        mDataBinding = DataBindingUtil.inflate(mInflater, R.layout.custom_input_layout, this, true);
        View v = mInflater.inflate(R.layout.custom_input_layout, this, true);

        mPresenter = new CustomInputLayoutPresenterImpl(this);

        txtLabel = (TextView) v.findViewById(R.id.txtLabel);
        txtMandatory = (TextView) v.findViewById(R.id.txtMandatory);
        txtLengthDesc = (TextView) v.findViewById(R.id.txtLengthDesc);

        layInputBox = (RelativeLayout) v.findViewById(R.id.layInputBox);
        laySpinner = (RelativeLayout) v.findViewById(R.id.laySpinner);
        layLabel = (RelativeLayout) v.findViewById(R.id.layLabel);

        edtInputbox = (EditText) v.findViewById(R.id.edtInputbox);

        iconCancel = (ImageView) v.findViewById(R.id.iconCancel);
        iconDropDown = (ImageView) v.findViewById(R.id.iconDropDown);

        autoCompleteTxt = (InstantAutoCompleteTextView) v.findViewById(R.id.autoCompleteTxt);

        /**
         * Get Attributes
         */
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomInputLayout);
        mLabel = a.getString(R.styleable.CustomInputLayout_cil_label);
        String hint = a.getString(R.styleable.CustomInputLayout_cil_hint);
        String value = a.getString(R.styleable.CustomInputLayout_cil_value);
        final int maxLines = a.getInt(R.styleable.CustomInputLayout_cil_maxLines, 1);
        maxLength = a.getInt(R.styleable.CustomInputLayout_cil_maxLength, 1000);
        mComponentType = a.getInt(R.styleable.CustomInputLayout_cil_component, 1);
        isMandatory = a.getBoolean(R.styleable.CustomInputLayout_cil_isMandatory, false);
        isMultiline = a.getBoolean(R.styleable.CustomInputLayout_cil_isMultiLine, false);
        mInputType = a.getInt(R.styleable.CustomInputLayout_cil_inputType, 1);

        /**
         * Set values
         */
//        edtInputbox.setTypeface(BaseApplication.getLatoItalicTypeFace());

        txtLabel.setText(mLabel);
        sethint(hint);
        edtInputbox.setText(value);
        edtInputbox.setMaxLines(maxLines);
        txtMandatory.setVisibility(isMandatory ? VISIBLE : GONE);

        setInputType(mInputType);
//        setTagEnable(isTagEnabled);
//        setTagPrimary(isTagPrimary, null);
        setComponentType(mComponentType);
        setMaxLength(maxLength);
        if (isMultiline) {
            setMultiline(true, 300);
        }

        /**
         * Visibility checks
         */

        if (mLabel != null)
            if (mLabel.length() > 0)
                layLabel.setVisibility(VISIBLE);

        /**
         * Listeners
         */
        edtInputbox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mValue = s.toString();
                if (mOnTouchListener != null)
                    mOnTouchListener.onTouch();

                iconCancel.setVisibility(s.length() > 0 ? VISIBLE : GONE);

                if (mInputType == INPUTTYPE_EMAIL) {
                    if (mPresenter.isValidEmail(s))
                        edtInputbox.setTextColor(mContext.getResources().getColor(R.color.black));
                    else
                        edtInputbox.setTextColor(mContext.getResources().getColor(R.color.red));
                }

                if (isMultiline) {
                    int rem = maxLength - s.length();
                    txtLengthDesc.setText(rem + "/" + maxLength + " Characters Only");
                    iconCancel.setVisibility(GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        iconCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                edtInputbox.setText("");
            }
        });

        autoCompleteTxt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTxt.showDropDown();
            }
        });

        disableDropDownTextSelection();
    }

    public float convertDpToPixel(float dp) {
        Resources resources = mContext.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

//    public CustomInputLayout setTagEnable(boolean isEnable) {
//        isTagEnabled = isEnable;
//        txtTag.setVisibility(isTagEnabled ? VISIBLE : GONE);
//        return this;
//    }
//
//    public CustomInputLayout setTagPrimary(final boolean isPrimary, final OnTagChangeListener listener) {
//        this.isTagPrimary = isPrimary;
//        if (this.isTagPrimary)
//            txtTag.setText("Untag primary");
//        else txtTag.setText("Tag primary");
//
//        txtTag.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setTagPrimary(isTagPrimary ? false : true, listener);
//                if (listener != null)
//                    listener.onTagChange(isTagPrimary);
//            }
//        });
//
//        return this;
//    }

//    public boolean getIsTagPrimary() {
//        return isTagPrimary;
//    }

    public CustomInputLayout setInputType(int inputType) {
        mInputType = inputType;
        if (mComponentType == TYPE_INPUTBOX) {
            if (mInputType == INPUTTYPE_TEXT) {
                edtInputbox.setInputType(InputType.TYPE_CLASS_TEXT);
            } else if (mInputType == INPUTTYPE_PHONE) {
                edtInputbox.setInputType(InputType.TYPE_CLASS_PHONE);
            } else if (mInputType == INPUTTYPE_NUMBER) {
                edtInputbox.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else if (mInputType == INPUTTYPE_EMAIL) {
                edtInputbox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            }
        }
        return this;
    }

    public CustomInputLayout setComponentType(int componentType) {
        mComponentType = componentType;
        switch (componentType) {

            case TYPE_INPUTBOX:
                layLabel.setVisibility(VISIBLE);
                layInputBox.setVisibility(VISIBLE);
                break;

            case TYPE_DROPDOWN:
                layLabel.setVisibility(VISIBLE);
                laySpinner.setVisibility(VISIBLE);
                break;

            default:
                layInputBox.setVisibility(VISIBLE);
                break;
        }
        return this;
    }

    /**
     * Drop down list
     *
     * @param items
     * @param listener
     * @return
     */
    public CustomInputLayout setSpinner(ArrayList<String> items, final SpinnerSelectionListener listener) {
        laySpinner.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        autoCompleteTxt.setShowAlways(true);
//        autoCompleteTxt.setTypeface(BaseApplication.getLatoItalicTypeFace());
        mAdapterAutocomplete = new AutoCompleteAdapter(mContext, R.layout.custom_input_layout, R.id.txtAutocomplete, items, new AutoCompleteAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(String item) {
                listener.onSpinnerItemSelected(item);
                mValue = item;
                if (mOnTouchListener != null)
                    mOnTouchListener.onTouch();

                autoCompleteTxt.setText(item);
                autoCompleteTxt.setSelection(item.length());
                autoCompleteTxt.dismissDropDown();
            }
        });
        autoCompleteTxt.setAdapter(mAdapterAutocomplete);
        return this;
    }

    public CustomInputLayout setSpinner(ArrayList<String> items) {
        laySpinner.setVisibility(VISIBLE);

        layInputBox.setVisibility(GONE);
        autoCompleteTxt.setShowAlways(true);
//        autoCompleteTxt.setTypeface(BaseApplication.getLatoItalicTypeFace());
        mAdapterAutocomplete = new AutoCompleteAdapter(mContext, R.layout.custom_input_layout, R.id.txtAutocomplete, items, new AutoCompleteAdapter.ItemSelectedListener() {
            @Override
            public void onItemSelected(String item) {
                mValue = item;
                if (mOnTouchListener != null)
                    mOnTouchListener.onTouch();

                autoCompleteTxt.setText(item);
                autoCompleteTxt.setSelection(item.length());
                autoCompleteTxt.dismissDropDown();
            }
        });
        autoCompleteTxt.setAdapter(mAdapterAutocomplete);
        return this;
    }

    public CustomInputLayout setLabel(String label) {
        if (mLabel != null)
            if (mLabel.length() > 0)
                layLabel.setVisibility(VISIBLE);
        txtLabel.setText(label);
        return this;
    }

    public CustomInputLayout setLabelVisibility(boolean shouldShow) {
        txtLabel.setVisibility(shouldShow ? VISIBLE : GONE);
        return this;
    }

    public CustomInputLayout sethint(String hint) {
        if (mComponentType == TYPE_INPUTBOX)
            edtInputbox.setHint(hint);
        if (mComponentType == TYPE_DROPDOWN)
            autoCompleteTxt.setHint(hint);
        return this;
    }

    public CustomInputLayout setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (mComponentType == TYPE_INPUTBOX) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(maxLength);
            edtInputbox.setFilters(filterArray);
        }
        if (mComponentType == TYPE_DROPDOWN) {
            InputFilter[] filterArray = new InputFilter[1];
            filterArray[0] = new InputFilter.LengthFilter(maxLength);
            autoCompleteTxt.setFilters(filterArray);
        }
        return this;
    }

    public CustomInputLayout setMultiline(boolean isMultiline, int maxLength) {
        setMaxLength(maxLength);
        this.isMultiline = isMultiline;
        txtLengthDesc.setVisibility(VISIBLE);
        iconCancel.setVisibility(GONE);

        final LayoutParams lparams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 180); // Width , height
        getInputBox().setLayoutParams(lparams);
        getInputBox().setSingleLine(false);
        getInputBox().setGravity(Gravity.LEFT | Gravity.TOP);
        getInputBox().setPadding(10, 10, 10, 10);
        return this;
    }

    public CustomInputLayout setMandatory(boolean isMandatory) {
        this.isMandatory = isMandatory;
        txtMandatory.setVisibility(isMandatory ? VISIBLE : GONE);
        return this;
    }

    public CustomInputLayout setMaxLines(int maxLines) {
        edtInputbox.setMaxLines(maxLines);
        return this;
    }

    public EditText getInputBox() {
        return edtInputbox;
    }

    public EditText getEditText() {
        return edtInputbox;
    }

    public String getValue() {
        if (mComponentType == TYPE_INPUTBOX) {
            if (isMandatory && edtInputbox.getText().toString().length() == 0) {
                Toast.makeText(mContext, txtLabel.getText() + " should not be empty", Toast.LENGTH_SHORT).show();
                return "";
            }
//            if (mInputType == INPUTTYPE_EMAIL) {
//                if (mPresenter.isValidEmail(edtInputbox.getText().toString()))
//                    return edtInputbox.getText().toString();
//                else
//                    showMessage("Please enter valid email address");
//            }
            return edtInputbox.getText().toString();
        } else if (mComponentType == TYPE_DROPDOWN) {
            if (isMandatory && autoCompleteTxt.getText().toString().length() == 0) {
                Toast.makeText(mContext, autoCompleteTxt.getText() + " should not be empty", Toast.LENGTH_SHORT).show();
                return "";
            }
            return autoCompleteTxt.getText().toString();
        }

        return "";
    }

    public CustomInputLayout setValue(String value) {
        if (mComponentType == TYPE_INPUTBOX) {
            mValue = value;
            edtInputbox.setText(value);
        } else if (mComponentType == TYPE_DROPDOWN) {
            mValue = value;
            autoCompleteTxt.setText(value);
        }
        return this;
    }

    public AutoCompleteTextView getAutoCompleteTextview() {
        return autoCompleteTxt;
    }

    public void setAutoCompleteShowAlways(boolean show) {
        autoCompleteTxt.setShowAlways(show);
    }

    private void showMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void disableDropDownTextSelection() {
        autoCompleteTxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });

        autoCompleteTxt.setLongClickable(false);
        autoCompleteTxt.setTextIsSelectable(false);
    }

    public CustomInputLayout showDropDown() {
        if (mComponentType == TYPE_DROPDOWN) {
            autoCompleteTxt.showDropDown();
        }
        return this;
    }

    public CustomInputLayout disableAutoCompleteSearch() {

        if (mAdapterAutocomplete != null)
            mAdapterAutocomplete.disableFilter(true);

        autoCompleteTxt.setLongClickable(false);
        autoCompleteTxt.setTextIsSelectable(false);
        autoCompleteTxt.setFocusable(false);

        autoCompleteTxt.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                return false;
            }

            public void onDestroyActionMode(ActionMode actionMode) {
            }
        });
        return this;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    public interface SpinnerSelectionListener {
        public void onSpinnerItemSelected(String item);
    }

    public interface OnTouchListener {
        public void onTouch();
    }

    public interface OnTagChangeListener {
        public void onTagChange(boolean isPrimary);
    }
}
