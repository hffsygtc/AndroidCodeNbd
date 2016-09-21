package cn.com.nbd.nbdmobile.widget;


import cn.com.nbd.nbdmobile.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;

 /**
  * 首页底部主栏目按钮
  * 
  * @author riche
  *
  */
public class MyRadioButton extends RadioButton {  
  
    private int mDrawableHeightSize;// xml文件中设置的图片高度大小  
    private int mDrawableWidthSize;// xml文件中设置的图片宽度大小  
  
    public MyRadioButton(Context context) {  
        this(context, null, 0);  
    }  
  
    public MyRadioButton(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
    }  
  
    public MyRadioButton(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        
        Drawable drawableLeft = null, drawableTop = null, drawableRight = null, drawableBottom = null;  
        TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.MyRadioButton);  
  
        int n = a.getIndexCount();  
        for (int i = 0; i < n; i++) {  
            int attr = a.getIndex(i);  
            Log.i("MyRadioButton", "attr:" + attr);  
            switch (attr) {  
            case R.styleable.MyRadioButton_drawableHeightSize:  
            	mDrawableHeightSize = a.getDimensionPixelSize(R.styleable.MyRadioButton_drawableHeightSize, 50);  
                Log.i("MyRadioButton", "mDrawableSize:" + mDrawableHeightSize);  
                break;  
            case R.styleable.MyRadioButton_drawableWidthSize:  
            	mDrawableWidthSize = a.getDimensionPixelSize(R.styleable.MyRadioButton_drawableWidthSize, 50);  
            	Log.i("MyRadioButton", "mDrawableSize:" + mDrawableWidthSize);  
            	break;  
            case R.styleable.MyRadioButton_drawableTop:  
                drawableTop = a.getDrawable(attr);  
                break;  
            case R.styleable.MyRadioButton_drawableBottom:  
                drawableRight = a.getDrawable(attr);  
                break;  
            case R.styleable.MyRadioButton_drawableRight:  
                drawableBottom = a.getDrawable(attr);  
                break;  
            case R.styleable.MyRadioButton_drawableLeft:  
                drawableLeft = a.getDrawable(attr);  
                break;  
            default :  
                    break;  
            }  
        }  
        a.recycle();  
          
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);  
  
    }  
  
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,  
            Drawable top, Drawable right, Drawable bottom) {  
  
        if (left != null) {  
            left.setBounds(0, 0, mDrawableWidthSize, mDrawableHeightSize);  
        }  
        if (right != null) {  
            right.setBounds(0, 0, mDrawableWidthSize, mDrawableHeightSize);  
        }  
        if (top != null) {  
            top.setBounds(0, 0, mDrawableWidthSize, mDrawableHeightSize);  
        }  
        if (bottom != null) {  
            bottom.setBounds(0, 0, mDrawableWidthSize, mDrawableHeightSize);  
        }  
        setCompoundDrawables(left, top, right, bottom);  
    }  
  
}  
