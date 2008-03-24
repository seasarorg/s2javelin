package org.seasar.javelin;

/**
 * システムログのログレベル。
 * 
 * @author eriguchi
 */
public class LogLevel
{
    /** FATALレベルのint値。 */
    public static final int      FATAL_INT = 50000;

    /** ERRORレベルのint値。 */
    public static final int      ERROR_INT = 40000;

    /** WARNレベルのint値。 */
    public static final int      WARN_INT  = 30000;

    /** INFOレベルのint値。 */
    public static final int      INFO_INT  = 20000;

    /** DEBUGレベルのint値。 */
    public static final int      DEBUG_INT = 10000;

    /** FATALレベル。 */
    public static final LogLevel FATAL     = new LogLevel(FATAL_INT, "FATAL");

    /** ERRORレベル。 */
    public static final LogLevel ERROR     = new LogLevel(ERROR_INT, "ERROR");

    /** WARNレベル。 */
    public static final LogLevel WARN     = new LogLevel(WARN_INT, "WARN");

    /** INFOレベル。 */
    public static final LogLevel INFO     = new LogLevel(INFO_INT, "INFO");
    
    /** DEBUGレベル。 */
    public static final LogLevel DEBUG     = new LogLevel(DEBUG_INT, "DEBUG");

    /** ログレベルのint値。 */
    private int                  level;

    /** ログレベルの名称。 */
    private String               levelStr;

    /**
     * コンストラクタ。
     * 
     * @param level ログレベルのint値。
     * @param levelStr ログレベルの名称。
     */
    public LogLevel(int level, String levelStr)
    {
        this.level = level;
        this.levelStr = levelStr;
    }

    /**
     * ログレベルの名称を取得する。
     * 
     * @return　ログレベルの名称。
     */
    public String getLevelStr()
    {
        return levelStr;
    }

    /**
     * ログレベルのint値を取得する。
     * 
     * @return　ログレベルのint値。
     */
    public int getLevel()
    {
        return level;
    }
}
