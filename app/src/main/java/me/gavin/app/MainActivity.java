package me.gavin.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.SeekBar;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.icon.designer.R;
import me.gavin.icon.designer.databinding.ActivityMainBinding;
import me.gavin.icon.designer.databinding.DialogCodeBinding;
import me.gavin.icon.designer.databinding.DialogEditColorBinding;
import me.gavin.icon.designer.databinding.DialogInputNameBinding;
import me.gavin.svg.parser.SVGParser;
import me.gavin.util.AlipayUtil;
import me.gavin.util.CacheHelper;
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

        afterCreate();

        mBinding.menu.setOnMenuItemSelectedListener(menuItem -> {
            mSeekBarType = 0;
            mBinding.seekBar.setVisibility(View.GONE);
            switch (menuItem.getItemId()) {
                case R.id.icon_shape:
                    new ChooseIconDialog(this, mBinding.pre::setSVG).show();
                    break;
                case R.id.icon_color:
                    showEditDialog(false);
                    break;
                case R.id.icon_size:
                    mSeekBarType = TYPE_SEEK_ICON_SIZE;
                    mBinding.seekBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.shadow_angle:
                    break;
                case R.id.shadow_length:
                    break;
                case R.id.shadow_gradient:
                    mSeekBarType = TYPE_SEEK_SHADOW_GRADIENT;
                    mBinding.seekBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.shadow_alpha:
                    mSeekBarType = TYPE_SEEK_SHADOW_ALPHA;
                    mBinding.seekBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.background_shape_rect:
                    mBinding.pre.setBgShape(0);
                    break;
                case R.id.background_shape_circle:
                    mBinding.pre.setBgShape(1);
                    break;
                case R.id.background_color:
                    showEditDialog(true);
                    break;
                case R.id.effect_score:
                    mBinding.pre.toggleEffectScore();
                    break;

                case R.id.save:
                    new RxPermissions(this)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    showSaveDialog();
                                } else {
                                    Snackbar.make(mBinding.pre, "保存图片需要 SDCard 卡读写权限", Snackbar.LENGTH_LONG)
                                            .setAction("设置", v -> {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }).show();
                                }
                            });
                    break;
                case R.id.send:
                    new RxPermissions(this)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    save(null, true);
                                } else {
                                    Snackbar.make(mBinding.pre, "保存图片需要 SDCard 卡读写权限", Snackbar.LENGTH_LONG)
                                            .setAction("设置", v -> {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }).show();
                                }
                            });
                    break;
                case R.id.donate_wechat:
                    new AlertDialog.Builder(this)
                            .setTitle("微信")
                            .setView(DialogCodeBinding.inflate(getLayoutInflater()).getRoot())
                            .setPositiveButton("哦哦", null)
                            .show();
                    break;
                case R.id.donate_alipay:
                    AlipayUtil.alipay(this, AlipayUtil.ALIPAY_CODE);
                    break;
                default:
                    break;
            }
        });
    }

    private void afterCreate() {
        Observable.just("gavin/adb.svg")
                .map(getAssets()::open)
                .map(SVGParser::parse)
                .subscribe(mBinding.pre::setSVG);

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
                        // mBinding.pre.setShadowGradient(progress);
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

    private void showEditDialog(boolean isBg) {
        DialogEditColorBinding binding = DialogEditColorBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("图标颜色")
                .setView(binding.getRoot())
                .setPositiveButton("确定", (dialog, which) -> {
                    if (isBg) {
                        setBgColor(binding.editText.getText().toString());
                    } else {
                        setIconColor(binding.editText.getText().toString());
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        binding.editText.postDelayed(() -> InputUtil.show(this, binding.editText), 100);
        binding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                alertDialog.dismiss();
                if (isBg) {
                    setBgColor(binding.editText.getText().toString());
                } else {
                    setIconColor(binding.editText.getText().toString());
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
            Snackbar.make(mBinding.pre, "格式错误", Snackbar.LENGTH_LONG).show();
        }
    }

    private void setBgColor(String colorStr) {
        try {
            if (!TextUtils.isEmpty(colorStr)) {
                mBinding.pre.setBgColor(Color.parseColor("#" + colorStr));
            }
        } catch (Exception e) {
            Snackbar.make(mBinding.pre, "格式错误", Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSaveDialog() {
        DialogInputNameBinding binding = DialogInputNameBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("保存")
                .setView(binding.getRoot())
                .setPositiveButton("确定", (dialog, which) ->
                        save(binding.editText.getText().toString(), false))
                .setNegativeButton("取消", null)
                .show();
        binding.editText.postDelayed(() -> InputUtil.show(this, binding.editText), 100);
        binding.editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                alertDialog.dismiss();
                save(binding.editText.getText().toString(), false);
            }
            return true;
        });
    }

    private void save(String text, boolean isSend) {
        String name = (TextUtils.isEmpty(text) ? UUID.randomUUID().toString() : text) + "_%sx%s";
        Observable.just(192, 144, 96, 72, 48)
                .map(size -> mBinding.pre.save(String.format(name, size, size), size))
                .map(path -> CacheHelper.file2Uri(this, new File(path)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(uri -> CacheHelper.updateAlbum(this, uri))
                .toList()
                .subscribe(uri -> {
                    if (isSend) {
                        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder
                                .from(this)
                                .setChooserTitle("分享图片")
                                .setType("image/*");
                        for (Uri i : uri) {
                            builder.addStream(i);
                        }
                        builder.startChooser();
                    } else {
                        Snackbar.make(mBinding.pre, "图片已保存", Snackbar.LENGTH_LONG)
                                .setAction("查看", v -> {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri.get(0), "image/*");
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        Snackbar.make(mBinding.pre, "你跟我说打不开？？？", Snackbar.LENGTH_LONG).show();
                                    }
                                }).show();
                    }
                }, throwable -> Snackbar.make(mBinding.pre, throwable.getMessage(), Snackbar.LENGTH_LONG).show());
    }

}
