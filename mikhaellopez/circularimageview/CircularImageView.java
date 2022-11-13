package com.mikhaellopez.circularimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
/* loaded from: classes.dex */
public class CircularImageView extends ImageView {
    private static final float DEFAULT_BORDER_WIDTH = 4.0f;
    private static final float DEFAULT_SHADOW_RADIUS = 8.0f;
    private static final ImageView.ScaleType SCALE_TYPE = ImageView.ScaleType.CENTER_CROP;
    private float borderWidth;
    private int canvasSize;
    private Drawable drawable;
    private Bitmap image;
    private Paint paint;
    private Paint paintBorder;
    private int shadowColor;
    private float shadowRadius;

    public CircularImageView(Context context) {
        this(context, null);
    }

    public CircularImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircularImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.shadowColor = ViewCompat.MEASURED_STATE_MASK;
        init(context, attributeSet, i);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        Paint paint = new Paint();
        this.paint = paint;
        paint.setAntiAlias(true);
        Paint paint2 = new Paint();
        this.paintBorder = paint2;
        paint2.setAntiAlias(true);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.CircularImageView, i, 0);
        if (obtainStyledAttributes.getBoolean(R.styleable.CircularImageView_civ_border, true)) {
            setBorderWidth(obtainStyledAttributes.getDimension(R.styleable.CircularImageView_civ_border_width, getContext().getResources().getDisplayMetrics().density * DEFAULT_BORDER_WIDTH));
            setBorderColor(obtainStyledAttributes.getColor(R.styleable.CircularImageView_civ_border_color, -1));
        }
        if (obtainStyledAttributes.getBoolean(R.styleable.CircularImageView_civ_shadow, false)) {
            this.shadowRadius = DEFAULT_SHADOW_RADIUS;
            drawShadow(obtainStyledAttributes.getFloat(R.styleable.CircularImageView_civ_shadow_radius, this.shadowRadius), obtainStyledAttributes.getColor(R.styleable.CircularImageView_civ_shadow_color, this.shadowColor));
        }
    }

    public void setBorderWidth(float f) {
        this.borderWidth = f;
        requestLayout();
        invalidate();
    }

    public void setBorderColor(int i) {
        Paint paint = this.paintBorder;
        if (paint != null) {
            paint.setColor(i);
        }
        invalidate();
    }

    public void addShadow() {
        if (this.shadowRadius == 0.0f) {
            this.shadowRadius = DEFAULT_SHADOW_RADIUS;
        }
        drawShadow(this.shadowRadius, this.shadowColor);
        invalidate();
    }

    public void setShadowRadius(float f) {
        drawShadow(f, this.shadowColor);
        invalidate();
    }

    public void setShadowColor(int i) {
        drawShadow(this.shadowRadius, i);
        invalidate();
    }

    @Override // android.widget.ImageView
    public ImageView.ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override // android.widget.ImageView
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported. ScaleType.CENTER_CROP is used by default. So you don't need to use ScaleType.", scaleType));
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        loadBitmap();
        if (this.image == null) {
            return;
        }
        if (!isInEditMode()) {
            this.canvasSize = canvas.getWidth();
            if (canvas.getHeight() < this.canvasSize) {
                this.canvasSize = canvas.getHeight();
            }
        }
        float f = this.borderWidth;
        float f2 = ((int) (this.canvasSize - (f * 2.0f))) / 2;
        float f3 = this.shadowRadius;
        canvas.drawCircle(f2 + f, f2 + f, (f + f2) - (f3 + (f3 / 2.0f)), this.paintBorder);
        float f4 = this.borderWidth;
        float f5 = this.shadowRadius;
        canvas.drawCircle(f2 + f4, f4 + f2, f2 - (f5 + (f5 / 2.0f)), this.paint);
    }

    private void loadBitmap() {
        if (this.drawable == getDrawable()) {
            return;
        }
        Drawable drawable = getDrawable();
        this.drawable = drawable;
        this.image = drawableToBitmap(drawable);
        updateShader();
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.canvasSize = i;
        if (i2 < i) {
            this.canvasSize = i2;
        }
        if (this.image != null) {
            updateShader();
        }
    }

    private void drawShadow(float f, int i) {
        this.shadowRadius = f;
        this.shadowColor = i;
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(1, this.paintBorder);
        }
        this.paintBorder.setShadowLayer(f, 0.0f, f / 2.0f, i);
    }

    private void updateShader() {
        Bitmap bitmap = this.image;
        if (bitmap == null) {
            return;
        }
        this.image = cropBitmap(bitmap);
        BitmapShader bitmapShader = new BitmapShader(this.image, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Matrix matrix = new Matrix();
        matrix.setScale(this.canvasSize / this.image.getWidth(), this.canvasSize / this.image.getHeight());
        bitmapShader.setLocalMatrix(matrix);
        this.paint.setShader(bitmapShader);
    }

    private Bitmap cropBitmap(Bitmap bitmap) {
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            return Bitmap.createBitmap(bitmap, (bitmap.getWidth() / 2) - (bitmap.getHeight() / 2), 0, bitmap.getHeight(), bitmap.getHeight());
        }
        return Bitmap.createBitmap(bitmap, 0, (bitmap.getHeight() / 2) - (bitmap.getWidth() / 2), bitmap.getWidth(), bitmap.getWidth());
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth > 0 && intrinsicHeight > 0) {
            try {
                Bitmap createBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(createBitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return createBitmap;
            } catch (OutOfMemoryError unused) {
                Log.e(getClass().toString(), "Encountered OutOfMemoryError while generating bitmap!");
            }
        }
        return null;
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(measureWidth(i), measureHeight(i2));
    }

    private int measureWidth(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        return (mode == 1073741824 || mode == Integer.MIN_VALUE) ? size : this.canvasSize;
    }

    private int measureHeight(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode != 1073741824 && mode != Integer.MIN_VALUE) {
            size = this.canvasSize;
        }
        return size + 2;
    }
}
