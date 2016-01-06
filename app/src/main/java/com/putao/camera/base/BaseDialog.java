package com.putao.camera.base;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.putao.camera.R;


public class BaseDialog extends DialogFragment implements DialogInterface.OnKeyListener {
    public void show(FragmentManager fm) {
        show(fm, "BaseDialog");
    }

    public void show(FragmentManager fm, Bundle bundle) {
        if (bundle != null) {
            setArguments(bundle);
        }
        show(fm, "BaseDialog");
    }

    public void show(FragmentTransaction ft, Bundle bundle) {
        setArguments(bundle);
        ft.add(this, "UpdateDialog");
        ft.commitAllowingStateLoss();
    }

    public TextView title, content;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dlg = new Dialog(getActivity(), initDialogStyle());
        dlg.setContentView(initContentView());
        initView(dlg);
        initData();
        dlg.setOnKeyListener(this);
        dlg.setCancelable(true);
        dlg.setCanceledOnTouchOutside(true);
        return dlg;
    }

    protected int initDialogStyle() {
        return R.style.dialog_style;
    }

    protected int initContentView() {
        return R.layout.layout_third_share_dialog;
    }

    protected void initView(Dialog dlg) {
//        title = (TextView) dlg.findViewById(R.id.title_name);
//        content = (TextView) dlg.findViewById(R.id.boldText);
//        buttonPositive = (Button) dlg.findViewById(R.id.ok_button);
//        buttonNegative = (Button) dlg.findViewById(R.id.cancel_button);
//        if (buttonPositive != null)
//        {
//            buttonPositive.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    onPositiveClicked();
//                }
//            });
//        }
//        if (buttonNegative != null)
//        {
//            buttonNegative.setOnClickListener(new OnClickListener()
//            {
//                @Override
//                public void onClick(View v)
//                {
//                    onNegativeClicked();
//                }
//            });
//        }
    }

    protected void initData() {
    }

    public Button buttonPositive, buttonNegative;

//    public void setTitleText(String titleText)
//    {
//        if (title != null)
//        {
//            title.setText(titleText);
//        }
//    }
//
//    public void setContentText(String contentText)
//    {
//        if (content != null)
//        {
//            content.setText(contentText);
//        }
//    }

    protected void onPositiveClicked() {
        dismiss();
    }

    protected void onNegativeClicked() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setOnDismissListener(null);
        }
        super.onDestroyView();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dismiss();
        }
        return false;
    }
}