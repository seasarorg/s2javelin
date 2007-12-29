package org.seasar.javelin;

/**
 * Javelinで使用する定数群。
 * 
 * @author eriguchi
 */
public interface JavelinConstants
{
    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Return"のID。 <br>
     */
    public static final int    ID_RETURN       = 1;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Call"のID。 <br>
     */
    public static final int    ID_CALL         = 0;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Read"のID。 <br>
     */
    public static final int    ID_FIELD_READ   = 2;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Write"のID。 <br>
     */
    public static final int    ID_FIELD_WRITE  = 3;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Catch"のID。 <br>
     */
    public static final int    ID_CATCH        = 5;

    /**
     * 内部処理で、動作ログの種類を区別するために使用する"Throw"のID。 <br>
     */
    public static final int    ID_THROW        = 4;

    /**
     * 動作ログに出力する"Return"を表す文字列。<br>
     */
    public static final String MSG_RETURN      = "Return";

    /**
     * 動作ログに出力する"Call"を表す文字列。<br>
     */
    public static final String MSG_CALL        = "Call  ";

    /**
     * 動作ログに出力する"Read"を表す文字列。<br>
     */
    public static final String MSG_FIELD_READ  = "Read  ";

    /**
     * 動作ログに出力する"Write"を表す文字列。<br>
     */
    public static final String MSG_FIELD_WRITE = "Write ";

    /**
     * 動作ログに出力する"Catch"を表す文字列。<br>
     */
    public static final String MSG_CATCH       = "Catch ";

    /**
     * 動作ログに出力する"Throw"を表す文字列。<br>
     */
    public static final String MSG_THROW       = "Throw ";
}