package com.squareup.picasso;

import android.graphics.Bitmap;
import com.squareup.picasso.Picasso;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FetchAction extends Action<Object> {
    private Callback callback;
    private final Object target;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FetchAction(Picasso picasso, Request request, int i, int i2, Object obj, String str, Callback callback) {
        super(picasso, null, request, i, i2, 0, null, str, obj, false);
        this.target = new Object();
        this.callback = callback;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.squareup.picasso.Action
    public void complete(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
        Callback callback = this.callback;
        if (callback != null) {
            callback.onSuccess();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.squareup.picasso.Action
    public void error() {
        Callback callback = this.callback;
        if (callback != null) {
            callback.onError();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.squareup.picasso.Action
    public void cancel() {
        super.cancel();
        this.callback = null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.squareup.picasso.Action
    public Object getTarget() {
        return this.target;
    }
}
