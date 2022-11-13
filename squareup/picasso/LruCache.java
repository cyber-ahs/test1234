package com.squareup.picasso;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class LruCache implements Cache {
    private int evictionCount;
    private int hitCount;
    final LinkedHashMap<String, Bitmap> map;
    private final int maxSize;
    private int missCount;
    private int putCount;
    private int size;

    public LruCache(Context context) {
        this(Utils.calculateMemoryCacheSize(context));
    }

    public LruCache(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Max size must be positive.");
        }
        this.maxSize = i;
        this.map = new LinkedHashMap<>(0, 0.75f, true);
    }

    @Override // com.squareup.picasso.Cache
    public Bitmap get(String str) {
        if (str == null) {
            throw new NullPointerException("key == null");
        }
        synchronized (this) {
            Bitmap bitmap = this.map.get(str);
            if (bitmap != null) {
                this.hitCount++;
                return bitmap;
            }
            this.missCount++;
            return null;
        }
    }

    @Override // com.squareup.picasso.Cache
    public void set(String str, Bitmap bitmap) {
        if (str == null || bitmap == null) {
            throw new NullPointerException("key == null || bitmap == null");
        }
        synchronized (this) {
            this.putCount++;
            this.size += Utils.getBitmapBytes(bitmap);
            Bitmap put = this.map.put(str, bitmap);
            if (put != null) {
                this.size -= Utils.getBitmapBytes(put);
            }
        }
        trimToSize(this.maxSize);
    }

    /* JADX WARN: Code restructure failed: missing block: B:20:0x0070, code lost:
        throw new java.lang.IllegalStateException(getClass().getName() + ".sizeOf() is reporting inconsistent results!");
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void trimToSize(int i) {
        while (true) {
            synchronized (this) {
                if (this.size >= 0 && (!this.map.isEmpty() || this.size == 0)) {
                    if (this.size <= i || this.map.isEmpty()) {
                        break;
                    }
                    Map.Entry<String, Bitmap> next = this.map.entrySet().iterator().next();
                    this.map.remove(next.getKey());
                    this.size -= Utils.getBitmapBytes(next.getValue());
                    this.evictionCount++;
                } else {
                    break;
                }
            }
        }
    }

    public final void evictAll() {
        trimToSize(-1);
    }

    @Override // com.squareup.picasso.Cache
    public final synchronized int size() {
        return this.size;
    }

    @Override // com.squareup.picasso.Cache
    public final synchronized int maxSize() {
        return this.maxSize;
    }

    @Override // com.squareup.picasso.Cache
    public final synchronized void clear() {
        evictAll();
    }

    @Override // com.squareup.picasso.Cache
    public final synchronized void clearKeyUri(String str) {
        int length = str.length();
        Iterator<Map.Entry<String, Bitmap>> it = this.map.entrySet().iterator();
        boolean z = false;
        while (it.hasNext()) {
            Map.Entry<String, Bitmap> next = it.next();
            String key = next.getKey();
            Bitmap value = next.getValue();
            int indexOf = key.indexOf(10);
            if (indexOf == length && key.substring(0, indexOf).equals(str)) {
                it.remove();
                this.size -= Utils.getBitmapBytes(value);
                z = true;
            }
        }
        if (z) {
            trimToSize(this.maxSize);
        }
    }

    public final synchronized int hitCount() {
        return this.hitCount;
    }

    public final synchronized int missCount() {
        return this.missCount;
    }

    public final synchronized int putCount() {
        return this.putCount;
    }

    public final synchronized int evictionCount() {
        return this.evictionCount;
    }
}
