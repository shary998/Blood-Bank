package com.example.blooddonar.custom.textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import com.example.blooddonar.constants.Constants


/**
 * Created by muneeb on 06,September,2020
 */
class RegularTextView : androidx.appcompat.widget.AppCompatTextView {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        if (attrs != null) {

            val myTypeface = Typeface.createFromAsset(
                getContext().assets,
                Constants.REGULAR
            )
            typeface = myTypeface
        }
    }
}