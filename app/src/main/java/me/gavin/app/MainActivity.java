package me.gavin.app;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.app.app.ChooseAppDialog;
import me.gavin.icon.designer.R;
import me.gavin.icon.designer.databinding.ActivityMainBinding;
import me.gavin.icon.designer.databinding.DialogCodeBinding;
import me.gavin.icon.designer.databinding.DialogInputNameBinding;
import me.gavin.svg.parser.SVGParser;
import me.gavin.util.AlipayUtil;
import me.gavin.util.CacheHelper;
import me.gavin.util.InputUtil;
import me.gavin.util.ShortcutUtil;
import me.gavin.widget.color.picker.ColorPickerDialogBuilder;

public class MainActivity extends AppCompatActivity {

    private final int TYPE_SEEK_ICON_SIZE = 0x10;
    private final int TYPE_SEEK_BG_CORNER = 0x20;
    private final int TYPE_SEEK_SHADOW_ANGLE = 0x30;
    private final int TYPE_SEEK_SHADOW_LENGTH = 0x31;
    private final int TYPE_SEEK_SHADOW_GRADIENT = 0x32;
    private final int TYPE_SEEK_SHADOW_ALPHA = 0x33;

    ActivityMainBinding mBinding;

    int mSeekBarType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

//        mBinding.elevation.setOnClickListener(v -> {
//            try {
//                v = mBinding.getRoot();
//                Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bitmap);
//                canvas.drawColor(ContextCompat.getColor(this, R.color.colorPrimary));
//                v.draw(canvas);
//                CacheHelper.saveBitmap(bitmap, "Elevation");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        afterCreate();

        mBinding.menu.setOnMenuItemSelectedListener(menuItem -> {
            mSeekBarType = 0;
            mBinding.seekBar.setVisibility(View.GONE);
            switch (menuItem.getItemId()) {
                case R.id.icon_shape_md:
                    new ChooseIconDialog(this, mBinding.pre::setSVG).show();
                    break;
                case R.id.icon_shape_app:
                    new ChooseAppDialog(this, appInfo
                            -> mBinding.pre.setDrawable(appInfo.drawable)).show();
                    break;
                case R.id.icon_shape_image:
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 0);
                    break;
                case R.id.icon_shape_text:
                    DialogInputNameBinding binding = DialogInputNameBinding.inflate(getLayoutInflater());
                    AlertDialog alertDialog = new AlertDialog.Builder(this)
                            .setTitle(R.string.input)
                            .setView(binding.getRoot())
                            .setPositiveButton(android.R.string.ok, (dialog, which) ->
                                    mBinding.pre.setText(binding.editText.getText().toString()))
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    binding.editText.postDelayed(() -> InputUtil.show(this, binding.editText), 100);
                    binding.editText.setOnEditorActionListener((v, actionId, event) -> {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            alertDialog.dismiss();
                            mBinding.pre.setText(binding.editText.getText().toString());
                        }
                        return true;
                    });
                    break;
                case R.id.icon_color:
                    ColorPickerDialogBuilder.with(this)
                            .setTitle(R.string.choose_color)
                            .withAlpha(true)
                            .setColor(mBinding.pre.getIconColor())
                            .setInputButton(R.string.input, (dialog, color)
                                    -> setIconColor(color))
                            .setPositiveButton(android.R.string.ok, (dialog, color)
                                    -> mBinding.pre.setIconColor(color))
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                case R.id.icon_size:
                    mSeekBarType = TYPE_SEEK_ICON_SIZE;
                    mBinding.seekBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.background_shape_rect:
                    mBinding.pre.setBgShape(0);
                    break;
                case R.id.background_shape_circle:
                    mBinding.pre.setBgShape(1);
                    break;
                case R.id.background_shape_rect_v:
                    mBinding.pre.setBgShape(2);
                    break;
                case R.id.background_shape_rect_h:
                    mBinding.pre.setBgShape(3);
                    break;
                case R.id.background_corner:
                    mSeekBarType = TYPE_SEEK_BG_CORNER;
                    mBinding.seekBar.setVisibility(View.VISIBLE);
                    break;
                case R.id.background_color:
                    ColorPickerDialogBuilder.with(this)
                            .setTitle(R.string.choose_color)
                            .withAlpha(true)
                            .setColor(mBinding.pre.getBgColor())
                            .setInputButton(R.string.input, (dialog, color)
                                    -> setBgColor(color))
                            .setPositiveButton(android.R.string.ok, (dialog, color)
                                    -> mBinding.pre.setBgColor(color))
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
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
                case R.id.effect_score:
                    mBinding.pre.toggleEffectScore();
                    break;
                case R.id.effect_lines:
                    mBinding.pre.toggleEffectLines();
                    break;

                case R.id.attach:
                    new ChooseAppDialog(this, appInfo -> {
                        boolean supports = ShortcutUtil.addShortcut(this,
                                appInfo.getIntent(), appInfo.label, mBinding.pre.getBitmap(192));
                        Snackbar.make(mBinding.pre, supports ? R.string.attach_success : R.string.attach_not_support, Snackbar.LENGTH_LONG).show();
                    }).show();
                    break;
                case R.id.save:
                    new RxPermissions(this)
                            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe(granted -> {
                                if (granted) {
                                    showSaveDialog();
                                } else {
                                    Snackbar.make(mBinding.pre, R.string.permission_denied, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.settings, v -> {
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
                                    Snackbar.make(mBinding.pre, R.string.permission_denied, Snackbar.LENGTH_LONG)
                                            .setAction(R.string.settings, v -> {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                intent.setData(Uri.parse("package:" + getPackageName()));
                                                startActivity(intent);
                                            }).show();
                                }
                            });
                    break;
                case R.id.donate_wechat:
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.donate_wechat)
                            .setView(DialogCodeBinding.inflate(getLayoutInflater()).getRoot())
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                    break;
                case R.id.donate_alipay:
                    AlipayUtil.alipay(this, AlipayUtil.ALIPAY_CODE);
                    break;
                case R.id.test:

                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.pre.save();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                mBinding.pre.setBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void afterCreate() {
        Observable.just("design/notification/ic_adb_24px.svg")
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
                    case TYPE_SEEK_BG_CORNER:
                        mBinding.pre.setBgCorner(progress);
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

    private void setIconColor(String colorStr) {
        try {
            if (TextUtils.isEmpty(colorStr)) {
                mBinding.pre.setIconColor(null);
            } else {
                mBinding.pre.setIconColor(Color.parseColor(colorStr));
            }
        } catch (Exception e) {
            Snackbar.make(mBinding.pre, R.string.wrong_format, Snackbar.LENGTH_LONG).show();
        }
    }

    private void setBgColor(String colorStr) {
        try {
            if (!TextUtils.isEmpty(colorStr)) {
                mBinding.pre.setBgColor(Color.parseColor(colorStr));
            }
        } catch (Exception e) {
            Snackbar.make(mBinding.pre, R.string.wrong_format, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showSaveDialog() {
        DialogInputNameBinding binding = DialogInputNameBinding.inflate(getLayoutInflater());
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.save)
                .setView(binding.getRoot())
                .setPositiveButton(android.R.string.ok, (dialog, which) ->
                        save(binding.editText.getText().toString(), false))
                .setNegativeButton(android.R.string.cancel, null)
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
        Observable.just(512)
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
                                .setChooserTitle(R.string.send)
                                .setType("image/*");
                        for (Uri i : uri) {
                            builder.addStream(i);
                        }
                        builder.startChooser();
                    } else {
                        Snackbar.make(mBinding.pre, R.string.image_saved, Snackbar.LENGTH_LONG)
                                .setAction(R.string.view, v -> {
                                    try {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setDataAndType(uri.get(0), "image/*");
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                        Snackbar.make(mBinding.pre, R.string.cant_open, Snackbar.LENGTH_LONG).show();
                                    }
                                }).show();
                    }
                }, throwable -> Snackbar.make(mBinding.pre, throwable.getMessage(), Snackbar.LENGTH_LONG).show());
    }

}
