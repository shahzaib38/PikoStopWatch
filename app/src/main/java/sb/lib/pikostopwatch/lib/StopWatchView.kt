package sb.lib.pikostopwatch.lib

import android.content.Context
import android.graphics.*
import android.icu.text.SimpleDateFormat
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toRectF
import java.util.*


class StopWatchView @JvmOverloads constructor(context: Context ,attr:AttributeSet?=null ,defStyle:Int =0)
    : View(context ,attr,defStyle) {


    private var startColor: Int = Color.parseColor("#689F38")
    private var startTextColor = Color.GREEN

    private var buttonSize: Int = 0
    private var mTimeLeftInMillis: Long =  START_INITIAL_TIME
    private lateinit var gradient: LinearGradient

    private var buttomMargin = 100

    private val paint: Paint = Paint().apply {

        this.color = Color.RED
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND

    }

    private val backgroundPaint : Paint = Paint().apply {

        this.color = Color.WHITE
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND

    }



    init {

            init(context,attr,defStyle)
        }

    private fun init(context: Context, attr: AttributeSet?, defStyle: Int) {


    }

    private var defaultSize : Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


        val widthMeasureSize  = MeasureSpec.getSize(widthMeasureSpec)
        val heightMeasureSize  = MeasureSpec.getSize(heightMeasureSpec)


         defaultSize = Math.min(widthMeasureSize,heightMeasureSize)

        buttonSize =  (defaultSize * 20)/100

        val finalSize = buttonSize + defaultSize + buttomMargin

        setMeasuredDimension(defaultSize, finalSize)
    }

    private var rect : Rect = Rect()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {

        val minimumSize = Math.min(width,height)
             val   radius = (minimumSize * 95)/100

        val fivePercent = (minimumSize * 5)/100


        rect.left = fivePercent
        rect.top = fivePercent
        rect.right = radius
        rect.bottom = radius


        gradient =  LinearGradient(width.toFloat(),height.toFloat(),
            0f,0f,
            intArrayOf(Color.parseColor("#FFA500") , Color.parseColor("#FF6D00")),
            floatArrayOf(0f,1f) ,
            Shader.TileMode.CLAMP)


        textPaint.textSize = (width * 22)/100f
        cancelPaint.textSize = (defaultSize * 4)/100f
        startPaint.textSize = (defaultSize * 4)/100f


        isIntitialized = true

    }


    private var isIntitialized =false



     fun setStopWatch(hours :Int  ,minutes :Int ){

         mTimeLeftInMillis = 100000L

    }


    private fun startWatch(){



        object : CountDownTimer(START_INITIAL_TIME  , INTERVAL_PER_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                mTimeLeftInMillis = millisUntilFinished


                invalidate()
            }

            override fun onFinish() {

            }
        }.start()



    }

    enum class StopWatchState {

        START ,PAUSE ,RESUME ,CANCAL

    }


    private var stopWatchState = StopWatchState.START


    override fun onDraw(canvas: Canvas?) {


        if(canvas == null ) return

        if(!isIntitialized) return



        canvas.drawColor(Color.BLACK)
        
        paint.shader = gradient

        val strokeSize =  (defaultSize * 3)/100

        paint.strokeWidth =  strokeSize.toFloat()
        backgroundPaint.strokeWidth = strokeSize.toFloat()



        canvas.drawArc(rect.toRectF(), 0f , 360f ,false ,backgroundPaint)

       val numberOfSeconds = START_INITIAL_TIME / 1000L

        val progressPercentage  = (numberOfSeconds - (mTimeLeftInMillis / 1000))


        val progressRatio = (360/numberOfSeconds) * progressPercentage

        canvas.drawArc(rect.toRectF(), MIN_ANGLE, MAX_ANGLE + progressRatio, false, paint)

            drawTimeText(canvas)

        drawStartAndPauseButton(canvas)

        drawCancelButton(canvas)

    }

    private var cancelPaint = Paint().apply {

        this.style = Paint.Style.STROKE
        this.color = Color.WHITE

    }


    private var startTextPaint = Paint().apply {
        this.color = Color.WHITE
    }

    private fun drawCancelButton(canvas: Canvas) {


        val minimumSize = Math.min(width,height)
        val   radius = (minimumSize * 95)/100

       val x = width - radius
        val xCenter = x + buttonSize/2f

        val yCenter = defaultSize + buttonSize/2f

        cancelPaint.style = Paint.Style.STROKE

        cancelPaint.color = Color.parseColor("#757575")


        canvas.drawCircle(xCenter , yCenter ,buttonSize/2f ,cancelPaint)

        cancelPaint.style = Paint.Style.FILL

        canvas.drawCircle(xCenter ,yCenter ,buttonSize/2.2f ,cancelPaint)


       val width =  cancelPaint.measureText("Cancel")

        val heightText = (cancelPaint.descent() + cancelPaint.ascent())/2f



        cancelPaint.color = Color.WHITE
        canvas.drawText("Cancel" ,xCenter - width/2f  , yCenter - heightText/2, cancelPaint)


    }


    private var startPaint = Paint().apply {

        this.style = Paint.Style.STROKE
        this.color = Color.GREEN

    }



    private fun drawStartAndPauseButton(canvas: Canvas) {

        val minimumSize = Math.min(width,height)
        val radius = (minimumSize * 95)/100
        val xCenter = radius - buttonSize/2f


        val yCenter = defaultSize + buttonSize/2f

        startPaint.style = Paint.Style.STROKE

        startPaint.color = startColor

        canvas.drawCircle(xCenter , yCenter ,buttonSize/2f ,startPaint)

        startPaint.style = Paint.Style.FILL


        canvas.drawCircle(xCenter ,yCenter ,buttonSize/2.2f ,startPaint)

        startPaint.color = startTextColor





        when(stopWatchState) {


            StopWatchState.START -> {


                val width =  startPaint.measureText("Start")

                val heightText = (startPaint.descent() + startPaint.ascent())/2f

                canvas.drawText("Start", xCenter - width / 2, yCenter - heightText / 2, startPaint)

            }
            StopWatchState.PAUSE ->{



                val width =  startPaint.measureText("Pause")

                val heightText = (startPaint.descent() + cancelPaint.ascent())/2f


                canvas.drawText("Pause", xCenter - width / 2, yCenter - heightText / 2, startPaint)
            }


            StopWatchState.RESUME ->{


                val width =  startPaint.measureText("Resume")

                val heightText = (startPaint.descent() + cancelPaint.ascent())/2f

                canvas.drawText("Resume", xCenter - width / 2, yCenter - heightText / 2, startPaint)


            }
            else -> {}
        }

    }


    private var textPaint = Paint().apply {

        this.color = Color.WHITE


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event ==null)return false

        when(event.action){


            MotionEvent.ACTION_DOWN ->{

                val minimumSize = Math.min(width,height)
                val radius = (minimumSize * 95)/100
                val xCenter = radius - buttonSize/2f


                val yCenter = defaultSize + buttonSize/2f

                val startX = xCenter - buttonSize/2
                val endX= xCenter + buttonSize/2


                val startY= yCenter - buttonSize/2
                val endY= yCenter + buttonSize/2




                if(event.x >=startX &&  event.x <=endX  && event.y >= startY && event.y<=endY ){

                    println("Clicked ")

                    startWatch()


                    stopWatchState = StopWatchState.PAUSE
                    startColor = Color.parseColor("#E65100")
                    startTextColor = Color.parseColor("#FFD180")


                }







            }

        }




        return true
    }



    private fun drawTimeText(canvas: Canvas) {

        val minutes = (mTimeLeftInMillis / 1000) as Long / 60
        val seconds = (mTimeLeftInMillis / 1000) as Long % 60

        val timeLeftFormatted: String =
            java.lang.String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

        val textWidth = textPaint.measureText(timeLeftFormatted)


        val textHeight = ( textPaint.ascent() +textPaint.descent())/2

        canvas.drawText(timeLeftFormatted ,defaultSize/2f -(textWidth/2) , defaultSize/2 - textHeight/2 ,textPaint )

    }

    companion object {

        private const val MIN_ANGLE = -90f
        private const val MAX_ANGLE = -360f
        private const val START_INITIAL_TIME = 80000L
        private const val INTERVAL_PER_SECOND = 1000L

    }


}