
package com.example.draw;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class MainActivity extends AppCompatActivity
        implements View.OnTouchListener, View.OnClickListener {

    private int currentBackgroundColor = 0xffffffff;
    private static final String TAG = "MainActivity";
    private boolean mIsDrawingEnabled = false;
    private RelativeLayout mColorPickerContainer, mUndoContainer, mPenSizeContainer;
    private  LinearLayout linear;
    private DrawableImageView mImageView;
    private ImageView mColorPicker;
    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mColorPickerContainer = findViewById(R.id.color_plate_container);
        mUndoContainer = findViewById(R.id.undo_container);
        mPenSizeContainer = findViewById(R.id.pen_size_container);
        mImageView = findViewById(R.id.stillshot_imageview);
        mColorPicker = findViewById(R.id.btn_dialog);
        findViewById(R.id.init_draw_icon).setOnClickListener(this);
        findViewById(R.id.pen_size).setOnClickListener(this);
        findViewById(R.id.undo_icon).setOnClickListener(this);
        findViewById(R.id.redo_icon).setOnClickListener(this);

        mImageView.setOnTouchListener(this);
        mColorPicker.setOnClickListener(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (mIsDrawingEnabled) {
            Log.d(TAG, "onTouch: sending touch event to DrawableImageView");
            Log.d(TAG, "onClick: 2222");
            return mImageView.touchEvent(motionEvent);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.init_draw_icon: {
                Log.d(TAG, "onClick: 11111");
                toggleColorPickerVisibility();
                break;
            }
            case R.id.undo_icon: {
                undoAction();
                break;
            }
            case R.id.redo_icon: {
                redoAction();
                break;
            }
            case R.id.btn_dialog: {
                colorPicker();
                break;
            }
            case R.id.pen_size: {
                sizePicker();
                break;
            }
        }
    }

    private void sizePicker() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Size");
        alert.setMessage("Choose Pen Size");

        linear = new LinearLayout(this);
        linear.setOrientation(LinearLayout.VERTICAL);

        final SeekBar seek = new SeekBar(this);
        mText = new TextView(this);
        mText.setPadding(10, 10, 10, 10);
        linear.addView(mText);
        linear.addView(seek);
        alert.setView(linear);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mText.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }});

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(seek.getProgress() < 8) seek.setProgress(8);
                mImageView.setPenSize((float) seek.getProgress());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mImageView.setPenSize(8f); }
        }).show();
    }

    private void toggleColorPickerVisibility() {
        if (mColorPickerContainer.getVisibility() == View.VISIBLE) {
            mColorPickerContainer.setVisibility(View.INVISIBLE);
            mUndoContainer.setVisibility(View.INVISIBLE);
            mPenSizeContainer.setVisibility(View.INVISIBLE);

            mIsDrawingEnabled = false;
        } else if (mColorPickerContainer.getVisibility() == View.INVISIBLE) {
            mColorPickerContainer.setVisibility(View.VISIBLE);
            mUndoContainer.setVisibility(View.VISIBLE);
            mPenSizeContainer.setVisibility(View.VISIBLE);

            if (mImageView.getBrushColor() == 0) {
                mImageView.setBrushColor(Color.BLACK);
            }

            mIsDrawingEnabled = true;
        }
        mImageView.setDrawingIsEnabled(mIsDrawingEnabled);
    }

    private void undoAction() {
        if (mIsDrawingEnabled) {
            mImageView.removeLastPath();
        }
    }

    private void redoAction() {
        if (mIsDrawingEnabled) {
            mImageView.readdLastPath();
        }
    }

    private void colorPicker() {
        final Context context = MainActivity.this;

        ColorPickerDialogBuilder
                .with(context)
                .setTitle(R.string.color_dialog_title)
                .initialColor(currentBackgroundColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorChangedListener(new OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int selectedColor) {
                        // Handle on color change
                        Log.d("ColorPicker", "onColorChanged: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        if (allColors != null) {
                            StringBuilder sb = null;

                            for (Integer color : allColors) {
                                if (color == null)
                                    continue;
                                if (sb == null)
                                    sb = new StringBuilder("Color List:");
                                sb.append("\r\n#" + Integer.toHexString(color).toUpperCase());
                            }

                            if (sb != null)
                                Toast.makeText(getApplicationContext(), sb.toString(), Toast.LENGTH_SHORT).show();
                            Drawable mDrawable = getResources().getDrawable(R.drawable.ic_color_plate_icon);
                            mDrawable.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN);
                            mColorPicker.setImageDrawable(mDrawable);
                            mImageView.setBrushColor(selectedColor);
                        }
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .showColorEdit(true)
                .setColorEditTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_blue_bright))
                .build()
                .show();
    }
}


