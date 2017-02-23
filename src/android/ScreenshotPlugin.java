package org.android.screenshot;

        import android.app.Activity;
        import com.github.piasy.rxscreenshotdetector.RxScreenshotDetector;
        import com.trello.rxlifecycle2.LifecycleProvider;
        import com.trello.rxlifecycle2.LifecycleTransformer;
        import com.trello.rxlifecycle2.RxLifecycle;
        import com.trello.rxlifecycle2.android.ActivityEvent;
        import com.trello.rxlifecycle2.android.RxLifecycleAndroid;

        import io.reactivex.Observable;
        import io.reactivex.android.schedulers.AndroidSchedulers;
        import io.reactivex.functions.Consumer;
        import io.reactivex.schedulers.Schedulers;
        import io.reactivex.subjects.BehaviorSubject;

        import org.apache.cordova.*;
        

        import javax.annotation.Nonnull;

        public class ScreenshotPlugin extends CordovaPlugin implements LifecycleProvider<ActivityEvent>  {

            private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();

            public void initialize(CordovaInterface cordova, CordovaWebView webView) {
                super.initialize(cordova, webView);
                Activity activity = this.cordova.getActivity();
                final CordovaWebView webView1 = webView;
                RxScreenshotDetector.start(activity).compose(this.<String>bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        webView1.loadUrl("javascript: alert('Screenshot');");
                    }
                });
            }          
              
            @Nonnull
            @Override
            public Observable<ActivityEvent> lifecycle() {
                return lifecycleSubject.hide();
            }

            @Nonnull
            @Override
            public <T> LifecycleTransformer<T> bindUntilEvent(@Nonnull ActivityEvent event) {
                return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
            }

            @Nonnull
            @Override
            public <T> LifecycleTransformer<T> bindToLifecycle() {
                return RxLifecycleAndroid.bindActivity(lifecycleSubject);
            }  

        }