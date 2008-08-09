package org.seasar.javelin.util;

import org.seasar.javelin.CallTreeNode;
import org.seasar.javelin.SystemLogger;
import org.seasar.javelin.bean.InvocationInterval;

public class StatsUtil
{

    /**
     * 実処理時間を取得する。
     * @param node CallTreeNode
     * @return 実処理時間
     */
    public static InvocationInterval getElapsedTime(CallTreeNode node)
    {
        InvocationInterval interval = new InvocationInterval();

        long elapsedTime = node.getAccumulatedTime();
        long elapsedCpuTime = node.getCpuTime();
        long elapsedUserTime = node.getUserTime();
        for (int index = 0; index < node.getChildren().size(); index++)
        {
            CallTreeNode child = node.getChildren().get(index);
            elapsedTime = elapsedTime - child.getAccumulatedTime();
            elapsedCpuTime = elapsedCpuTime - child.getCpuTime();
            elapsedUserTime = elapsedUserTime - child.getUserTime();
        }

        interval.setInterval(elapsedTime);
        interval.setCpuInterval(elapsedCpuTime);
        interval.setUserInterval(elapsedUserTime);

        return interval;
    }

    /**
     * スレッドを識別するための文字列を出力する。 
     * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
     * 
     * @return スレッドを識別するための文字列
     */
    public static String createThreadIDText(Thread currentThread)
    {
        StringBuilder threadId = new StringBuilder();
        threadId.append(currentThread.getName());
        threadId.append("@");
        threadId.append(ThreadUtil.getThreadId());
        threadId.append("(" + currentThread.getClass().getName());
        threadId.append("@");
        threadId.append(StatsUtil.getObjectID(currentThread));
        threadId.append(")");

        return threadId.toString();
    }
    
    /**
     * スレッドを識別するための文字列を出力する。 
     * フォーマット：スレッド名@スレッドクラス名@スレッドオブジェクトのID
     * 
     * @return スレッドを識別するための文字列
     */
    public static String createThreadIDText()
    {
        Thread currentThread = Thread.currentThread();
        return createThreadIDText(currentThread);
    }

    /**
     * オブジェクトIDを16進形式の文字列として取得する。
     * 
     * @param object オブジェクトIDを取得オブジェクト。
     * @return オブジェクトID。
     */
    public static String getObjectID(Object object)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        return Integer.toHexString(System.identityHashCode(object));
    }

    /**
     * objectをtoStringで文字列に変換する。
     * 
     * toStringで例外が発生した場合は、
     * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
     * クラス名@オブジェクトIDを返す。
     * 
     * @param object オブジェクト
     * @return toStringでobjectを文字列化したもの。
     */
    public static String toStr(Object object)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        String result;
        try
        {
            result = object.toString();
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().debug(
                                             "Javelin Exception" + object.getClass().toString()
                                                     + "#toString(): ", th);
            result = object.getClass().toString() + "@" + StatsUtil.getObjectID(object);
        }
        return result;
    }

    /**
     * objectをtoStringで文字列に変換、指定長で切る。
     * 
     * toStringで例外が発生した場合は、
     * 標準エラー出力にobjectのクラス名とスタックトレースを出力し、
     * クラス名@オブジェクトIDを返す。
     * 指定長を超えている場合は指定長で切り、"..."を付与する。
     * 
     * @param object 文字列化対象オブジェクト
     * @param length 文字列指定長
     * @return toStringで文字列に変換し、指定長で切ったもの。
     */
    public static String toStr(Object object, int length)
    {
        // 引数がnullの場合は"null"を返す。
        if (object == null)
        {
            return "null";
        }

        String result;
        try
        {
            result = object.toString();
            if (length == 0)
            {
                result = "";
            }
            else if (result.length() > length)
            {
                result = result.substring(0, length) + "...";
            }
        }
        catch (Throwable th)
        {
            SystemLogger.getInstance().debug(
                                             "Javelin Exception" + object.getClass().toString()
                                                     + "#toString(): ", th);
            result = object.getClass().toString() + "@" + StatsUtil.getObjectID(object);
        }

        return result;
    }

    /**
     * バイト列をbyte[length]:FFFF...形式に変換、指定長で切る。
     * 
     * @param binary バイナリ
     * @return バイト列をbyte[length]:FFFF...形式に変換、指定長で切ったもの。
     */
    public static String toStr(byte binary)
    {
        String hex = Integer.toHexString(((int)binary) & 0xFF).toUpperCase();
        String result = "byte[1]:" + "00".substring(hex.length()) + hex;
        return result;
    }

    /**
     * バイト列をbyte[length]:FFFF...形式に変換(最大で先頭8バイトを16進出力)。
     * 
     * @param binary バイナリ
     * @return バイト列をbyte[length]:FFFF...形式に変換(最大で先頭8バイトを16進出力)したもの。
     */
    public static String toStr(byte[] binary)
    {

        if (binary.length == 0)
        {
            return "byte[0]";
        }

        StringBuffer result = new StringBuffer("byte[");
        result.append(binary.length);
        result.append("]:");
        for (int count = 0; count < 8 && count < binary.length; count++)
        {
            String hex = Integer.toHexString(((int)binary[count]) & 0xFF).toUpperCase();
            result.append("00".substring(hex.length()) + hex);
        }
        if (binary.length > 8)
        {
            result.append("...");
        }
        return result.toString();
    }

    /**
     * Objectの情報出力を行う
     * 出力深度にあわせ、フィールドを辿るかその場で出力するか判定する
     * 
     * @param object       出力対象オブジェクト
     * @param detailDepth  出力深度
     * @return             出力結果
     */
    public static String buildDetailString(Object object, int detailDepth)
    {
        return DetailStringBuilder.buildDetailString(object, detailDepth);
    }

    /**
     * ToStringの結果を返す
     * 
     * @param object 変換対象
     * @return       ToStringの結果
     */
    public static String buildString(Object object)
    {
        //toStringは例外を発生させることがあるため、発生時は
        //"????"という文字列を返すようにする。
        return DetailStringBuilder.buildString(object);
    }
}
