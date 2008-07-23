package org.seasar.javelin;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.log.Logger;

/**
 * Javelin���O���o�͂��AComponent�Ԃ̌Ăяo���֌W��
 * MBean�Ƃ��Č��J���邽�߂�Interceptor�B<br>
 * <br>
 * �܂��A�ȉ��̏����AMBean�o�R�Ŏ擾���邱�Ƃ��\�B
 * <ol>
 * <li>���\�b�h�̌Ăяo����</li>
 * <li>���\�b�h�̕��Ϗ������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̍Œ��������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̍ŒZ�������ԁi�~���b�P�ʁj</li>
 * <li>���\�b�h�̌Ăяo����</li>
 * <li>��O�̔�����</li>
 * <li>��O�̔�������</li>
 * </ol>
 * �܂��A�ȉ��̏�����dicon�t�@�C���ł̃p�����[�^�Ŏw��\�B
 * <ol>
 * <li>httpPort:HTTP�Ō��J����ۂ̃|�[�g�ԍ��B�g�p�ɂ�MX4J���K�v�B</li>
 * <li>intervalMax:���\�b�h�̏������Ԃ����񕪋L�^���邩�B
 *     �f�t�H���g�l��1000�B</li>
 * <li>throwableMax:��O�̔��������񕪋L�^���邩�B
 *     �f�t�H���g�l��1000�B</li>
 * <li>recordThreshold:�������ԋL�^�p��臒l�B
 *     ���̎��Ԃ��z�������\�b�h�Ăяo���̂݋L�^����B
 *     �f�t�H���g�l��0�B</li>
 * <li>alarmThreshold:�������ԋL�^�p��臒l�B
 *     ���̎��Ԃ��z�������\�b�h�Ăяo���̔�����Viwer�ɒʒm����B
 *     �f�t�H���g�l��1000�B</li>
 * <li>domain:MBean��o�^����ۂɎg�p����h���C���B
 *     ���ۂ̃h���C�����́A[domain�p�����[�^] + [Mbean�̎��]�ƂȂ�B
 *     MBean�̎�ނ͈ȉ��̂��̂�����B
 *     <ol>
 *       <li>container:�S�R���|�[�l���g��ObjectName���Ǘ�����B</li>
 *       <li>component:��̃R���|�[�l���g�Ɋւ���������J����MBean�B</li>
 *       <li>invocation:���\�b�h�Ăяo���Ɋւ���������J����MBean�B</li>
 *     </ol>
 *     </li>
 * </ol>
 * 
 * @version 0.2
 * @author On Eriguchi (SMG), Tetsu Hayakawa (SMG)
 */
public class S2JavelinInterceptor extends AbstractInterceptor
{
    private static final long             serialVersionUID        = 6661781313519708185L;

    /**
     * ���\�b�h�Ăяo������Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String           CALL                    = "Call  ";

    /**
     * ���\�b�h�߂莞��Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String           RETURN                  = "Return";

    /**
     * ��Othrow����Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String           THROW                   = "Throw ";

    /**
     * ��Ocatch����Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String           CATCH                   = "Catch ";

    /** �X�^�b�N�g���[�X�̐[�� */
    private static final int              CALLER_STACKTRACE_INDEX = 3;

    /**
     * ���s�����B
     */
    private static final String           NEW_LINE                =
                                                                          System.getProperty("line.separator");

    /**
     * ���O�̋�؂蕶���B
     */
    private static final String           DELIM                   = ",";

    /**
     * �X���b�h���̋�؂蕶��
     */
    private static final String           THREAD_DELIM            = "@";

    /**
     * ���O�o�͂��鎞���̃t�H�[�}�b�g�B
     */
    private static final String           TIME_FORMAT_STR         = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * Javelin���O�ɏo�͂��鎞���f�[�^���`�p�t�H�[�}�b�^�B
     */
    private ThreadLocal<DateFormat>       timeFormat_             = new ThreadLocal<DateFormat>() {
                                                                      protected synchronized DateFormat initialValue()
                                                                      {
                                                                          return new SimpleDateFormat(
                                                                                                      TIME_FORMAT_STR);
                                                                      }
                                                                  };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g��\��������B
     */
    private ThreadLocal<String>           callerLog_              = new ThreadLocal<String>() {
                                                                      protected synchronized String initialValue()
                                                                      {
                                                                          return "<unknown>,<unknown>,0";
                                                                      }
                                                                  };

    /**
     * Javelin���O�o�͗p���K�[�B
     */
    private static Logger                 logger_                 =
                                                                          Logger.getLogger(S2JavelinInterceptor.class);

    /**
     * �������o�͂��邩�ǂ����B
     * args�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean                       isLogArgs_              = true;

    /**
     * �߂�l���o�͂��邩�ǂ����B
     * return�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean                       isLogReturn_            = true;

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ����B
     * stackTrace�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean                       isLogStackTrace_        = false;

    /**
     * ��O�������L�^����e�[�u���B
     * �L�[�ɃX���b�h�̎��ʎq�A�l�ɗ�O�I�u�W�F�N�g������B
     * ��O�����������炱�̃}�b�v�ɂ��̗�O��o�^���A
     * �L���b�`���ꂽ���_�ō폜����B
     */
    static private Map<String, Throwable> exceptionMap__          =
                                                                          Collections.synchronizedMap(new HashMap<String, Throwable>());

    private S2JavelinConfig               config_                 = new S2JavelinConfig();

    /** �ݒ�l��W���o�͂ɏo�͂�����true */
    private boolean                       isPrintConfig_          = false;

    private boolean                       isInitialized_          = false;

    /**
     * Javelin���O�o�͗p��invoke���\�b�h�B
     * 
     * ���ۂ̃��\�b�h�Ăяo�������s����O��ŁA
     * �Ăяo���ƕԋp�̏ڍ׃��O���AJavelin�`���ŏo�͂���B
     * ���s���ɗ�O�����������ꍇ�́A���̏ڍׂ����O�o�͂���B<br>
     * 
     * �܂��A���ۂ̃��\�b�h�Ăяo�������s����O��ŁA
     * ���s�񐔂���s���Ԃ�MBean�ɋL�^����B
     * 
     * ���s���ɗ�O�����������ꍇ�́A
     * ��O�̔����񐔂┭���������L�^����B
     * 
     * @param invocation �C���^�[�Z�v�^�ɂ���Ď擾���ꂽ�A�Ăяo�����\�b�h�̏��
     * @return invocation�����s�����Ƃ��̖߂�l
     * @throws Throwable invocation�����s�����Ƃ��ɔ���������O
     */
    public Object invoke(MethodInvocation invocation)
        throws Throwable
    {
        // �ݒ�l���o�͂��Ă��Ȃ���Ώo�͂���
        synchronized (this)
        {
            if (this.isPrintConfig_ == false)
            {
                this.isPrintConfig_ = true;
                printConfigValue();
            }

            initialize();
        }

        // �Ăяo������擾�B
        String calleeClassName = getTargetClass(invocation).getName();
        String calleeMethodName = invocation.getMethod().getName();

        try
        {
            // �Ăяo������擾�B
            String className = calleeClassName;
            String methodName = calleeMethodName;

            StackTraceElement[] stacktrace = null;
            if (this.config_.isLogStacktrace())
            {
                stacktrace = Thread.currentThread().getStackTrace();
            }

            S2StatsJavelinRecorder.preProcess(className, methodName, invocation.getArguments(),
                                              stacktrace, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        String objectID = "";
        String threadInfo = "";

        StringBuffer methodCallBuff = new StringBuffer(256);
        // �Ăяo�������擾�B
        String currentCallerLog = "";

        if (this.config_.isJavelinEnable())
        {
            currentCallerLog = this.callerLog_.get();

            objectID = Integer.toHexString(System.identityHashCode(invocation.getThis()));
            String modifiers = Modifier.toString(invocation.getMethod().getModifiers());

            Thread currentThread = Thread.currentThread();
            String threadName = currentThread.getName();
            String threadClassName = currentThread.getClass().getName();
            String threadID = Integer.toHexString(System.identityHashCode(currentThread));

            threadInfo = threadName + THREAD_DELIM + threadClassName + THREAD_DELIM + threadID;

            // ���\�b�h�Ăяo�����ʕ��������B
            methodCallBuff.append(calleeMethodName);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(calleeClassName);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(objectID);

            // �Ăяo����̃��O���A���񃍃O�o�͎��̌Ăяo�����Ƃ��Ďg�p���邽�߂ɕۑ�����B
            this.callerLog_.set(methodCallBuff.toString());

            methodCallBuff.append(DELIM);
            methodCallBuff.append(currentCallerLog);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(modifiers);
            methodCallBuff.append(DELIM);
            methodCallBuff.append(threadInfo);
            methodCallBuff.append(NEW_LINE);

            // Call �ڍ׃��O�����B
            StringBuffer callDetailBuff = createCallDetail(methodCallBuff, invocation);

            // Call ���O�o�́B
            logger_.debug(callDetailBuff);
        }

        //���ۂ̃��\�b�h�Ăяo�������s����B
        //���s���ɗ�O������������A���̏ڍ׃��O���o�͂���B
        Object ret = null;
        try
        {
            // ���\�b�h�Ăяo���B
            ret = invocation.proceed();
        }
        catch (Throwable cause)
        {
            S2StatsJavelinRecorder.postProcess(cause, this.config_);

            if (this.config_.isJavelinEnable())
            {
                // Throw�ڍ׃��O�����B
                StringBuffer throwBuff =
                        createThrowCatchDetail(THROW, calleeMethodName, calleeClassName, objectID,
                                               threadInfo, cause);

                // Throw ���O�o�́B
                logger_.debug(throwBuff);

                //��O�������L�^����B
                exceptionMap__.put(threadInfo, cause);
            }

            //��O���X���[���A�I������B
            throw cause;
        }

        if (this.config_.isJavelinEnable())
        {
            //���̃X���b�h�ŁA���O�ɗ�O���������Ă��������m�F����B
            //�������Ă��Ă����ɂ��ǂ蒅�����̂ł���΁A���̎��_�ŗ�O��
            //catch���ꂽ�Ƃ������ƂɂȂ�̂ŁACatch���O���o�͂���B
            boolean isExceptionThrowd = exceptionMap__.containsKey(threadInfo);
            if (isExceptionThrowd == true)
            {
                //�������Ă�����O�I�u�W�F�N�g���}�b�v������o���B�i���폜����j
                Throwable exception = exceptionMap__.remove(threadInfo);

                // Catch�ڍ׃��O�����B
                StringBuffer throwBuff =
                        createThrowCatchDetail(CATCH, calleeMethodName, calleeClassName, objectID,
                                               threadInfo, exception);

                // Catch ���O�o�́B
                logger_.debug(throwBuff);
            }

            // Return�ڍ׃��O�����B
            StringBuffer returnDetailBuff = createReturnDetail(methodCallBuff, ret);

            // Return���O�o�́B
            logger_.debug(returnDetailBuff);

            this.callerLog_.set(currentCallerLog);
        }

        try
        {
            S2StatsJavelinRecorder.postProcess(ret, this.config_);
        }
        catch (Throwable th)
        {
            th.printStackTrace();
        }

        //invocation�����s�����ۂ̖߂�l��Ԃ��B
        return ret;
    }

    /**
     * ����������B
     */
    private void initialize()
    {
        if (this.isInitialized_ == false)
        {
            if (this.config_.getHttpPort() != 0)
            {
                try
                {
                    Mx4JLauncher.execute(this.config_.getHttpPort());
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
            this.isInitialized_ = true;
        }
    }

    /**
     * ���\�b�h�Ăяo���i���ʎqCall�j�̏ڍׂȃ��O�𐶐�����B
     * 
     * @param methodCallBuff ���\�b�h�Ăяo���̕�����
     * @param invocation ���\�b�h�Ăяo���̏��
     * @return ���\�b�h�Ăяo���̏ڍׂȃ��O
     */
    private StringBuffer createCallDetail(StringBuffer methodCallBuff, MethodInvocation invocation)
    {
        String timeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        StringBuffer callDetailBuff = new StringBuffer(512);

        callDetailBuff.append(CALL);
        callDetailBuff.append(DELIM);
        callDetailBuff.append(timeStr);
        callDetailBuff.append(DELIM);
        callDetailBuff.append(methodCallBuff);

        // �����̃��O�𐶐�����B
        if (this.isLogArgs_ == true)
        {
            callDetailBuff.append("<<javelin.Args_START>>" + NEW_LINE);
            Object[] args = invocation.getArguments();
            if (args != null && args.length > 0)
            {
                for (int i = 0; i < args.length; ++i)
                {
                    callDetailBuff.append("    args[");
                    callDetailBuff.append(i);
                    callDetailBuff.append("] = ");
                    callDetailBuff.append(args[i]);
                    callDetailBuff.append(NEW_LINE);
                }
            }
            callDetailBuff.append("<<javelin.Args_END>>" + NEW_LINE);
        }

        // �X�^�b�N�g���[�X�̃��O�𐶐�����B
        if (this.isLogStackTrace_ == true)
        {
            callDetailBuff.append("<<javelin.StackTrace_START>>" + NEW_LINE);
            Throwable th = new Throwable();
            StackTraceElement[] stackTraceElements = th.getStackTrace();
            for (int index = CALLER_STACKTRACE_INDEX; index < stackTraceElements.length; index++)
            {
                callDetailBuff.append("    at ");
                callDetailBuff.append(stackTraceElements[index]);
                callDetailBuff.append(NEW_LINE);
            }

            callDetailBuff.append("<<javelin.StackTrace_END>>" + NEW_LINE);
        }

        if (invocation.getThis() instanceof Throwable)
        {
            callDetailBuff.append("<<javelin.Exception>>" + NEW_LINE);
        }
        return callDetailBuff;
    }

    /**
     * ���\�b�h�̖߂�i���ʎqReturn�j�̏ڍׂȃ��O�𐶐�����B
     * 
     * @param methodCallBuff ���\�b�h�Ăяo���̕�����
     * @param ret ���\�b�h�̖߂�l
     * @return ���\�b�h�̖߂�̏ڍׂȃ��O
     */
    private StringBuffer createReturnDetail(StringBuffer methodCallBuff, Object ret)
    {
        StringBuffer returnDetailBuff = new StringBuffer(512);
        String returnTimeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        returnDetailBuff.append(RETURN);
        returnDetailBuff.append(DELIM);
        returnDetailBuff.append(returnTimeStr);
        returnDetailBuff.append(DELIM);
        returnDetailBuff.append(methodCallBuff);

        // �߂�l�̃��O�𐶐�����B
        if (this.isLogReturn_)
        {
            returnDetailBuff.append("<<javelin.Return_START>>" + NEW_LINE);
            returnDetailBuff.append("    ");
            returnDetailBuff.append(ret);
            returnDetailBuff.append(NEW_LINE);
            returnDetailBuff.append("<<javelin.Return_END>>" + NEW_LINE);
        }
        return returnDetailBuff;
    }

    /**
     * ��O�����i���ʎqThrow�j�A�܂��͗�O�L���b�`�iCatch�j�̏ڍ׃��O�𐶐�����B
     * 
     * @param id ���ʎq�BThrow�܂���Catch
     * @param calleeMethodName �Ăяo���惁�\�b�h��
     * @param calleeClassName �Ăяo����N���X��
     * @param objectID �Ăяo����N���X�̃I�u�W�F�N�gID
     * @param threadInfo �X���b�h���
     * @param cause ����������O
     * @return ��O�����̏ڍׂȃ��O
     */
    private StringBuffer createThrowCatchDetail(String id, String calleeMethodName,
            String calleeClassName, String objectID, String threadInfo, Throwable cause)
    {
        String throwTimeStr = ((SimpleDateFormat)this.timeFormat_.get()).format(new Date());
        String throwableID = Integer.toHexString(System.identityHashCode(cause));
        StringBuffer throwBuff = new StringBuffer(512);
        throwBuff.append(id);
        throwBuff.append(DELIM);
        throwBuff.append(throwTimeStr);
        throwBuff.append(DELIM);
        throwBuff.append(cause.getClass().getName());
        throwBuff.append(DELIM);
        throwBuff.append(throwableID);
        throwBuff.append(DELIM);
        throwBuff.append(calleeMethodName);
        throwBuff.append(DELIM);
        throwBuff.append(calleeClassName);
        throwBuff.append(DELIM);
        throwBuff.append(objectID);
        throwBuff.append(DELIM);
        throwBuff.append(threadInfo);
        return throwBuff;
    }

    /**
     * 
     * @param intervalMax ���ώ��s���Ԃ��v�Z���邽�߂̌��ʂ̍ő吔
     */
    public void setIntervalMax(int intervalMax)
    {
        if (this.config_.isSetIntervalMax() == false)
        {
            this.config_.setIntervalMax(intervalMax);
        }
    }

    /**
     * 
     * @param throwableMax Invocation�ɗ�O��ۑ�����ő吔
     */
    public void setThrowableMax(int throwableMax)
    {
        if (this.config_.isSetThrowableMax() == false)
        {
            this.config_.setThrowableMax(throwableMax);
        }
    }

    /**
     * 
     * @param recordThreshold ���O�o�͂���TAT��臒l
     */
    public void setRecordThreshold(int recordThreshold)
    {
        if (this.config_.isSetRecordThreshold() == false)
        {
            this.config_.setRecordThreshold(recordThreshold);
        }
    }

    /**
     * 
     * @param alarmThreshold �A���[����ʒm����TAT��臒l
     */
    public void setAlarmThreshold(int alarmThreshold)
    {
        if (this.config_.isSetAlarmThreshold() == false)
        {
            this.config_.setAlarmThreshold(alarmThreshold);
        }
    }

    /**
     * 
     * @param domain BottleneckEye�ɗ��p����h���C��
     */
    public void setDomain(String domain)
    {
        if (this.config_.isSetDomain() == false)
        {
            this.config_.setDomain(domain);
        }
    }

    /**
     * StatsJavelin���O���o�͂���f�B���N�g�����w�肷��B
     *
     * @param javelinFileDir �f�B���N�g����
     */
    public void setJavelinFileDir(String javelinFileDir)
    {
        if (this.config_.isSetJavelinFileDir() == false)
        {
            this.config_.setJavelinFileDir(javelinFileDir);
        }
    }

    /**
     * 
     * @param httpPort BottleneckEye�Ƃ̒ʐM�ɗ��p����|�[�g�ԍ�
     */
    public void setHttpPort(int httpPort)
    {
        if (this.config_.isSetHttpPort() == false)
        {
            this.config_.setHttpPort(httpPort);
        }
    }

    /**
     * �������o�͂��邩�ǂ����̐ݒ��ύX����B
     *
     * @param isLogArgs �������o�͂���Ȃ�true
     */
    public void setLogArgs(boolean isLogArgs)
    {
        if (this.config_.isLogArgs() == false)
        {
            this.config_.setLogArgs(isLogArgs);
        }
    }

    /**
     * �߂�l���o�͂��邩�ǂ����̐ݒ��ύX����B
     *
     * @param isLogReturn �߂�l���o�͂���Ȃ�true
     */
    public void setLogReturn(boolean isLogReturn)
    {
        if (this.config_.isLogReturn() == false)
        {
            this.config_.setLogArgs(isLogReturn);
        }
    }

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ����̐ݒ��ύX����B
     *
     * @param isLogStacktrace �X�^�b�N�g���[�X���o�͂���Ȃ�true
     */
    public void setLogStacktrace(boolean isLogStacktrace)
    {
        if (this.config_.isLogStacktrace() == false)
        {
            this.config_.setLogStacktrace(isLogStacktrace);
        }
    }

    /**
     * 
     * @param endCalleeName ���[�̌Ăяo����̖���
     */
    public void setEndCalleeName(String endCalleeName)
    {
        if (this.config_.isSetEndCalleeName() == false)
        {
            this.config_.setEndCalleeName(endCalleeName);
        }
    }

    /**
     * 
     * @param endCallerName ���[�̌Ăяo����̖���
     */
    public void setRootCallerName(String endCallerName)
    {
        if (this.config_.isSetRootCallerName() == false)
        {
            this.config_.setRootCallerName(endCallerName);
        }
    }

    /**
     * 
     * @param threadModel �X���b�h����
     */
    public void setThreadModel(int threadModel)
    {
        if (this.config_.isSetThreadModel() == false)
        {
            this.config_.setThreadModel(threadModel);
        }
    }

    /**
     * �ݒ�l��W���o�͂ɏo�͂���B
     */
    private void printConfigValue()
    {
        PrintStream out = System.out;
        out.println(">>>> Properties related with S2JavelinInterceptor");
        out.println("\tjavelin.intervalMax             : " + this.config_.getIntervalMax());
        out.println("\tjavelin.throwableMax            : " + this.config_.getThrowableMax());
        out.println("\tjavelin.statisticsThreshold     : " + this.config_.getStatisticsThreshold());
        out.println("\tjavelin.recordThreshold         : " + this.config_.getRecordThreshold());
        out.println("\tjavelin.alarmThreshold          : " + this.config_.getAlarmThreshold());
        out.println("\tjavelin.javelinFileDir          : " + this.config_.getJavelinFileDir());
        out.println("\tjavelin.recordException         : " + this.config_.isRecordException());
        out.println("\tjavelin.alarmException          : " + this.config_.isAlarmException());
        out.println("\tjavelin.domain                  : " + this.config_.getDomain());
        out.println("\tjavelin.log.stacktrace          : " + this.config_.isLogStacktrace());
        out.println("\tjavelin.log.args                : " + this.config_.isLogArgs());
        out.println("\tjavelin.log.jmxinfo             : " + this.config_.isLogMBeanInfo());
        out.println("\tjavelin.log.jmxinfo.root        : " + this.config_.isLogMBeanInfoRoot());
        out.println("\tjavelin.log.return              : " + this.config_.isLogReturn());
        out.println("\tjavelin.log.return.detail       : " + this.config_.isReturnDetail());
        out.println("\tjavelin.log.return.detail.depth : " + this.config_.getReturnDetailDepth());
        out.println("\tjavelin.log.args.detail         : " + this.config_.isArgsDetail());
        out.println("\tjavelin.log.args.detail.depth   : " + this.config_.getArgsDetailDepth());
        out.println("\tjavelin.rootCallerName          : " + this.config_.getRootCallerName());
        out.println("\tjavelin.endCalleeName           : " + this.config_.getEndCalleeName());
        out.println("\tjavelin.threadModel             : " + this.config_.getThreadModel());
        out.println("\tjavelin.httpPort                : " + this.config_.getHttpPort());
        out.println("\tjavelin.acceptPort              : " + this.config_.getAcceptPort());
        out.println("\tjavelin.stringLimitLength       : " + this.config_.getStringLimitLength());
        out.println("\tjavelin.system.log              : " + this.config_.getSystemLog());
        out.println("\tjavelin.log.jvn.max             : " + this.config_.getLogJvnMax());
        out.println("\tjavelin.log.zip.max             : " + this.config_.getLogZipMax());
        out.println("\tjavelin.system.log.num.max      : " + this.config_.getSystemLogNumMax());
        out.println("\tjavelin.system.log.size.max     : " + this.config_.getSystemLogSizeMax());
        out.println("\tjavelin.system.log.level        : " + this.config_.getSystemLogLevel());
        out.println("\tjavelin.call.tree.max           : " + this.config_.getCallTreeMax());
        out.println("\tjavelin.record.jmx              : " + this.config_.isRecordJMX());
        out.println("\tjavelin.recordStrategy          : " + this.config_.getRecordStrategy());
        out.println("\tjavelin.alarmListeners          : " + this.config_.getAlarmListeners());
        out.println("\tjavelin.telegramListeners       : " + this.config_.getTelegramListeners());
        out.println("\tjavelin.serializeFile           : " + this.config_.getSerializeFile());
        out.println("<<<<");
    }
}