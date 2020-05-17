package com.example.doodlze20;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

public class ColorDialogFragment extends DialogFragment {

    private SeekBar alpha;
    private SeekBar blue;
    private SeekBar green;
    private SeekBar red;

    private View colorView;

    private int color;
    private MainActivityFragment doodleFragment;
    private SeekBar.OnSeekBarChangeListener colorChangeListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        color = Color.argb(alpha.getProgress(),
                                red.getProgress(),
                                green.getProgress(),
                                blue.getProgress());
                        Log.d("VALUE ", "" + alpha.getProgress());
                        colorView.setBackgroundColor(color);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            };

    public ColorDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getDoodleFragment();

        if(fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodleFragment();

        if(fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View colorDialogView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_color,
                null);
        builder.setView(colorDialogView);

        builder.setTitle(R.string.title_color_dialog);

        alpha = (SeekBar) colorDialogView.findViewById(R.id.alphaSeekBar);
        red = (SeekBar) colorDialogView.findViewById(R.id.redSeekBar);
        green = (SeekBar) colorDialogView.findViewById(R.id.greenSeekBar);
        blue = (SeekBar) colorDialogView.findViewById(R.id.blueSeekBar);

        colorView = colorDialogView.findViewById(R.id.colorView);

        alpha.setOnSeekBarChangeListener(colorChangeListener);
        red.setOnSeekBarChangeListener(colorChangeListener);
        green.setOnSeekBarChangeListener(colorChangeListener);
        blue.setOnSeekBarChangeListener(colorChangeListener);

        final DoodleView doodleView = getDoodleFragment().getDoodleView();
        color = doodleView.getDrawingColor();
        alpha.setProgress(Color.alpha(color));
        red.setProgress(Color.red(color));
        green.setProgress(Color.green(color));
        blue.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doodleView.setDrawingColor(color);
            }
        });

        return builder.create();
    }

    public MainActivityFragment getDoodleFragment() {
        return (MainActivityFragment) getFragmentManager()
                .findFragmentById(R.id.doodleFragment);
    }
}
