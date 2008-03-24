package org.seasar.javelin;

/**
 * �V�X�e�����O�̃��O���x���B
 * 
 * @author eriguchi
 */
public class LogLevel
{
    /** FATAL���x����int�l�B */
    public static final int      FATAL_INT = 50000;

    /** ERROR���x����int�l�B */
    public static final int      ERROR_INT = 40000;

    /** WARN���x����int�l�B */
    public static final int      WARN_INT  = 30000;

    /** INFO���x����int�l�B */
    public static final int      INFO_INT  = 20000;

    /** DEBUG���x����int�l�B */
    public static final int      DEBUG_INT = 10000;

    /** FATAL���x���B */
    public static final LogLevel FATAL     = new LogLevel(FATAL_INT, "FATAL");

    /** ERROR���x���B */
    public static final LogLevel ERROR     = new LogLevel(ERROR_INT, "ERROR");

    /** WARN���x���B */
    public static final LogLevel WARN     = new LogLevel(WARN_INT, "WARN");

    /** INFO���x���B */
    public static final LogLevel INFO     = new LogLevel(INFO_INT, "INFO");
    
    /** DEBUG���x���B */
    public static final LogLevel DEBUG     = new LogLevel(DEBUG_INT, "DEBUG");

    /** ���O���x����int�l�B */
    private int                  level;

    /** ���O���x���̖��́B */
    private String               levelStr;

    /**
     * �R���X�g���N�^�B
     * 
     * @param level ���O���x����int�l�B
     * @param levelStr ���O���x���̖��́B
     */
    public LogLevel(int level, String levelStr)
    {
        this.level = level;
        this.levelStr = levelStr;
    }

    /**
     * ���O���x���̖��̂��擾����B
     * 
     * @return�@���O���x���̖��́B
     */
    public String getLevelStr()
    {
        return levelStr;
    }

    /**
     * ���O���x����int�l���擾����B
     * 
     * @return�@���O���x����int�l�B
     */
    public int getLevel()
    {
        return level;
    }
}
