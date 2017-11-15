package me.gavin.icon.material;

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import io.reactivex.Observable;
import me.gavin.icon.material.databinding.ActivityMainBinding;
import me.gavin.svg.parser.SVGParser;
import me.gavin.util.InputUtil;

public class MainActivity extends AppCompatActivity {

    private final int TYPE_SEEK_ICON_SIZE = 0x10;
    private final int TYPE_SEEK_SHADOW_ANGLE = 0x20;
    private final int TYPE_SEEK_SHADOW_LENGTH = 0x21;
    private final int TYPE_SEEK_SHADOW_GRADIENT = 0x22;
    private final int TYPE_SEEK_SHADOW_ALPHA = 0x23;

    ActivityMainBinding mBinding;

    int mSeekBarType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // Observable.just("", "size", "action")
//        Observable.just("gavin/rect.svg")
//        Observable.just("gavin/circle.svg")
        Observable.just("gavin/setting.svg")
//        Observable.just("action")
//                .flatMap(path -> Observable.just(path)
//                        .map(getAssets()::list)
//                        .flatMap(Observable::fromArray)
//                        .filter(s -> s.endsWith(".svg"))
//                        .map(s -> String.format("%s%s",
//                                TextUtils.isEmpty(path) ? "" : path + "/", s)))
//                .take(1)
                .map(getAssets()::open)
                .map(SVGParser::parse)
                .subscribe(mBinding.pre::setSVG);
//                .subscribe(L::e);

        mBinding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (mSeekBarType) {
                    case TYPE_SEEK_ICON_SIZE:
                        mBinding.pre.setIconSize(progress);
                        break;
                    case TYPE_SEEK_SHADOW_ANGLE:
                        break;
                    case TYPE_SEEK_SHADOW_LENGTH:
                        break;
                    case TYPE_SEEK_SHADOW_GRADIENT:
                        mBinding.pre.setShadowGradient(progress);
                        break;
                    case TYPE_SEEK_SHADOW_ALPHA:
                        mBinding.pre.setShadowAlpha(progress);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mSeekBarType = 0;
        mBinding.seekBar.setVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.icon_shape:
                new ChooseIconDialog(this, svg -> {
                    mBinding.pre.setSVG(svg);
                }).show();
                return true;
            case R.id.icon_color:
                showEditDialog(false);
                return true;
            case R.id.icon_size:
                mSeekBarType = TYPE_SEEK_ICON_SIZE;
                mBinding.seekBar.setVisibility(View.VISIBLE);
                return true;
            case R.id.shadow_angle:
                return true;
            case R.id.shadow_length:
                return true;
            case R.id.shadow_gradient:
                mSeekBarType = TYPE_SEEK_SHADOW_GRADIENT;
                mBinding.seekBar.setVisibility(View.VISIBLE);
                return true;
            case R.id.shadow_alpha:
                mSeekBarType = TYPE_SEEK_SHADOW_ALPHA;
                mBinding.seekBar.setVisibility(View.VISIBLE);
                return true;
            case R.id.bg_shape:
                return true;
            case R.id.bg_color:
                showEditDialog(true);
                return true;
            default:
                return false;
        }
    }

    private void showEditDialog(boolean isBg) {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit, null);
        EditText editText = view.findViewById(R.id.editText);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("图标颜色")
                .setView(view)
                .setPositiveButton("确定", (dialog, which) -> {
                    if (isBg) {
                        setBgColor(editText.getText().toString());
                    } else {
                        setIconColor(editText.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        editText.postDelayed(() -> InputUtil.show(this, editText), 100);
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                alertDialog.dismiss();
                if (isBg) {
                    setBgColor(editText.getText().toString());
                } else {
                    setIconColor(editText.getText().toString());
                }
            }
            return true;
        });
    }

    private void setIconColor(String colorStr) {
        try {
            if (TextUtils.isEmpty(colorStr)) {
                mBinding.pre.setIconColor(null);
            } else {
                mBinding.pre.setIconColor(Color.parseColor("#" + colorStr));
            }
        } catch (Exception e) {
            Toast.makeText(this, "格式错误", Toast.LENGTH_LONG).show();
        }
    }

    private void setBgColor(String colorStr) {
        try {
            if (!TextUtils.isEmpty(colorStr)) {
                mBinding.pre.setBgColor(Color.parseColor("#" + colorStr));
            }
        } catch (Exception e) {
            Toast.makeText(this, "格式错误", Toast.LENGTH_LONG).show();
        }
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_TITLE, UUID.randomUUID() + ".jpg");
        startActivityForResult(intent, 99);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && data != null && data.getData() != null) {
            try {
                mBinding.pre.save(getContentResolver().openOutputStream(data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
