package me.gavin.icon.material;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import io.reactivex.Observable;
import me.gavin.icon.material.databinding.ActivityMainBinding;
import me.gavin.icon.material.util.L;
import me.gavin.svg.parser.SVGParser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mBinding;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bg_shape:
                return true;
            default:
                return false;
        }
    }
}
