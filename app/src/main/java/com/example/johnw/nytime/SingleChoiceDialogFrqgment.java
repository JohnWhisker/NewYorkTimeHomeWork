package com.example.johnw.nytime;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import java.util.List;

/**
 * Created by JohnWhisker on 3/20/16.
 */
public class SingleChoiceDialogFrqgment extends DialogFragment {
    public static final String DATA = "items";
    public static final String SELECTED = "selected";
    private SelectionListener listener;

    public interface  SelectionListener{
        public void selectItem(int position);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{
            this.listener = (SelectionListener) activity;
        }catch (ClassCastException oops){
            oops.printStackTrace();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Resources res = getActivity().getResources();
        Bundle bundle = getArguments();
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Please Select");
        dialog.setPositiveButton("Ok",new PositiveButtonClickListener());
        List<String> list = (List<String>) bundle.get(DATA);
        int possition = bundle.getInt(SELECTED);
        CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
        dialog.setSingleChoiceItems(cs,possition,selectItemListener);
        return dialog.create();
    }
    class PositiveButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }
    DialogInterface.OnClickListener selectItemListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(listener!=null){
                listener.selectItem(which);
            }

        }
    };
}
