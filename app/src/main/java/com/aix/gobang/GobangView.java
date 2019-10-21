package com.aix.gobang;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class GobangView extends View {
    /**
     * 网格线画笔
     */
    private Paint mPaintLine;
    /**
     * 棋盘路径
     */
    private Path mPathLine;
    /**
     * 黑色棋子
     */
    private Path mPathDrawBlack;
    /**
     * 黑色棋子画笔
     */
    private Paint mPaintBlack;
    /**
     * 红色棋子
     */
    private Path mPathDrawRed;
    /**
     * 红色棋子画笔
     */
    private Paint mPaintRed;

    /**
     * 存储棋子
     */
    private int[][] chessPieces;

    /**
     * 尚无棋子
     */
    private static final int NULL_CHESS = 0;
    /**
     * 黑色棋子
     */
    private static final int BLACK_CHESS = 1;
    /**
     * 红色棋子
     */
    private static final int RED_CHESS = 2;

    /**
     * 棋盘间距
     */
    private int chessSpace = 0;
    /**
     * 距确定绘制起点
     */
    private int leftStart = 0;
    private int topStart = 0;
    /**
     * 棋盘格数
     */
    private int chessNum = 13;
    /**
     * 当前是否为黑色棋子
     */
    public boolean isBlack = true;


    public GobangView(Context context) {
        this(context,null);
    }

    public GobangView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GobangView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化画笔
     */
    private void init() {
        mPathLine = new Path();
        mPathDrawBlack = new Path();
        mPathDrawRed = new Path();

        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(3);
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStyle(Paint.Style.STROKE);

        mPaintBlack = new Paint();
        mPaintBlack.setStrokeWidth(3);
        mPaintBlack.setColor(Color.BLACK);
        mPaintBlack.setStyle(Paint.Style.FILL);

        mPaintRed = new Paint();
        mPaintRed.setStrokeWidth(3);
        mPaintRed.setColor(Color.RED);
        mPaintRed.setStyle(Paint.Style.FILL);

        chessPieces = new int[chessNum][chessNum];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //最小长度
        int minLength;
        if (w<=h){
            minLength = w;
        }else{
            minLength = h;
        }
        chessSpace = minLength/ (chessNum+1);
        //计算起始点 (左右各空一个)
        if (w<h) {//垂直
            leftStart = chessSpace;
            topStart = (h - minLength - 2 * chessSpace) / 2;
        }else{//水平
            leftStart = (w - minLength - 2 * chessSpace) / 2;
            topStart = chessSpace;
        }
        //计算画线的终点
        int end = minLength - 2 * chessSpace;
        for (int i = 0; i<chessNum; i++){
            mPathLine.moveTo(i* chessSpace,0);
            mPathLine.lineTo(i* chessSpace,end);

            mPathLine.moveTo(0,i* chessSpace);
            mPathLine.lineTo(end,i* chessSpace);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //移动到绘制起点
        canvas.translate(leftStart, topStart);
        //画棋盘
        canvas.drawPath(mPathLine,mPaintLine);
        //画黑色棋子
        canvas.drawPath(mPathDrawBlack, mPaintBlack);
        //画红色棋子
        canvas.drawPath(mPathDrawRed, mPaintRed);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                drawChessPieces(event.getX(),event.getY());
                break;
        }
        return true;
    }

    //确定圆心 画棋子
    private void drawChessPieces(float x, float y){
        float x1 = (x- leftStart)/ chessSpace;
        float y1 = (y- topStart)/ chessSpace;
        //棋子行列
        int posx = Math.round(x1);
        int posy = Math.round(y1);
        //棋子圆心坐标
        float chessPiecesX = posx * chessSpace;
        float chessPiecesY = posy * chessSpace;

        //确定是否在范围内
        if (posx>=0 && posx<chessNum && posy>=0 && posy<chessNum){
            //判断当前点是否没有棋子
            if (chessPieces[posx][posy] == NULL_CHESS){
                if (isBlack) {//是黑棋
                    chessPieces[posx][posy] = BLACK_CHESS;
                    mPathDrawBlack.addCircle(chessPiecesX, chessPiecesY, chessSpace / 3, Path.Direction.CCW);
                    isBlack = false;
                }else{
                    chessPieces[posx][posy] = RED_CHESS;
                    mPathDrawRed.addCircle(chessPiecesX, chessPiecesY, chessSpace / 3, Path.Direction.CCW);
                    isBlack = true;
                }
                //可以在此处做胜利判断 (未作)
                invalidate();
            }
        }
    }

}
