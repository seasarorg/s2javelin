package org.seasar.javelin;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;
import org.seasar.framework.log.Logger;

/**
 * Javelin���O���o�͂��邽�߂�TraceInterceptor�B
 * 
 * @version 0.2
 * @author On Eriguchi (SMG), Tetsu Hayakawa (SMG)
 */
public class JavelinTraceInterceptor extends AbstractInterceptor
{
    private static final long serialVersionUID = 6661781313519708185L;

    /**
     * ���\�b�h�Ăяo������Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String CALL = "Call  ";

    /**
     * ���\�b�h�߂莞��Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String RETURN = "Return";

    /**
     * ��Othrow����Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String THROW = "Throw ";

    /**
     * ��Ocatch����Javlin���O�̐擪�ɏo�͂��鎯�ʎq�B
     */
    private static final String CATCH = "Catch ";

    
    private static final int CALLER_STACKTRACE_INDEX = 3;

    /**
     * ���s�����B
     */
    private static final String NEW_LINE = System.getProperty("line.separator");

    /**
     * ���O�̋�؂蕶���B
     */
    private static final String DELIM = ",";

    /**
     * �X���b�h���̋�؂蕶��
     */
    private static final String THREAD_DELIM = "@";

    /**
     * ���O�o�͂��鎞���̃t�H�[�}�b�g�B
     */
    private static final String TIME_FORMAT_STR = "yyyy/MM/dd HH:mm:ss.SSS";

    /**
     * Javelin���O�ɏo�͂��鎞���f�[�^���`�p�t�H�[�}�b�^�B
     */
    private ThreadLocal timeFormat_ = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return new SimpleDateFormat(TIME_FORMAT_STR);
        }
    };

    /**
     * ���\�b�h�̌Ăяo�����I�u�W�F�N�g��\��������B
     */
    private ThreadLocal callerLog_ = new ThreadLocal()
    {
        protected synchronized Object initialValue()
        {
            return "<unknown>,<unknown>,0";
        }
    };

    /**
     * Javelin���O�o�͗p���K�[�B
     */
    private static Logger logger_ = Logger
            .getLogger(JavelinTraceInterceptor.class);

    /**
     * �������o�͂��邩�ǂ����B
     * args�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean isLogArgs_ = true;

    /**
     * �߂�l���o�͂��邩�ǂ����B
     * return�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean isLogReturn_ = true;

    /**
     * �X�^�b�N�g���[�X���o�͂��邩�ǂ����B
     * stackTrace�v���p�e�B�̒l���Z�b�g�����B
     */
    private boolean isLogStackTrace_ = false;

    /**
     * ��O�������L�^����e�[�u���B
     * �L�[�ɃX���b�h�̎��ʎq�A�l�ɗ�O�I�u�W�F�N�g������B
     * ��O�����������炱�̃}�b�v�ɂ��̗�O��o�^���A
     * �L���b�`���ꂽ���_�ō폜����B
     */
    static private Map exceptionMap__ = Collections.synchronizedMap(new HashMap());
    
    /**
     * Javelin���O�o�͗p��invoke���\�b�h�B
     * 
     * ���ۂ̃��\�b�h�Ăяo�������s����O��ŁA
     * �Ăяo���ƕԋp�̏ڍ׃��O���AJavelin�`���ŏo�͂���B
     * 
     * ���s���ɗ�O�����������ꍇ�́A���̏ڍׂ����O�o�͂���B
     * 
     * @param invocation
     *            �C���^�[�Z�v�^�ɂ���Ď擾���ꂽ�A�Ăяo�����\�b�h�̏��
     * @return invocation�����s�����Ƃ��̖߂�l
     * @throws Throwable invocation�����s�����Ƃ��ɔ���������O
     */
    public Object invoke(MethodInvocation invocation) throws Throwable
    {
        StringBuffer methodCallBuff = new StringBuffer(256);

        // �Ăяo������擾�B
        String calleeMethodName = invocation.getMethod().getName();
        String calleeClassName = getTargetClass(invocation).getName();
        String objectID = Integer.toHexString(System
                .identityHashCode(invocation.getThis()));
        String modifiers = Modifier.toString(invocation.getMethod()
                .getModifiers());

        // �Ăяo�������擾�B
        String currentCallerLog = (String) this.callerLog_.get();

        Thread currentThread = Thread.currentThread();
        String threadName = currentThread.getName();
        String threadClassName = currentThread.getClass().getName();
        String threadID = Integer.toHexString(System
                .identityHashCode(currentThread));
        String threadInfo = threadName + THREAD_DELIM + threadClassName + THREAD_DELIM + threadID;

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
        StringBuffer callDetailBuff = createCallDetail(methodCallBuff,
                invocation);

        // Call ���O�o�́B
        logger_.debug(callDetailBuff);

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
            // Throw�ڍ׃��O�����B
            StringBuffer throwBuff = createThrowCatchDetail(THROW, calleeMethodName,
                    calleeClassName, objectID, threadInfo, cause);

            // Throw ���O�o�́B
            logger_.debug(throwBuff);

            //��O�������L�^����B
            exceptionMap__.put(threadInfo, cause);
            
            //��O���X���[���A�I������B
            throw cause;
        }
        
        //���̃X���b�h�ŁA���O�ɗ�O���������Ă��������m�F����B
        //�������Ă��Ă����ɂ��ǂ蒅�����̂ł���΁A���̎��_�ŗ�O��
        //catch���ꂽ�Ƃ������ƂɂȂ�̂ŁACatch���O���o�͂���B
        boolean isExceptionThrowd = exceptionMap__.containsKey(threadInfo);
        if(isExceptionThrowd == true) 
        {
            //�������Ă�����O�I�u�W�F�N�g���}�b�v������o���B�i���폜����j
            Throwable exception = (Throwable)exceptionMap__.remove(threadInfo);
            
            // Catch�ڍ׃��O�����B
            StringBuffer throwBuff = createThrowCatchDetail(CATCH, calleeMethodName,
                    calleeClassName, objectID, threadInfo, exception);

            // Catch ���O�o�́B
            logger_.debug(throwBuff);
        }

        // Return�ڍ׃��O�����B
        StringBuffer returnDetailBuff = createReturnDetail(methodCallBuff, ret);

        // Return���O�o�́B
        logger_.debug(returnDetailBuff);

        this.callerLog_.set(currentCallerLog);

        //invocation�����s�����ۂ̖߂�l��Ԃ��B
        return ret;
    }

    /**
     * ���\�b�h�Ăяo���i���ʎqCall�j�̏ڍׂȃ��O�𐶐�����B
     * 
     * @param methodCallBuff ���\�b�h�Ăяo���̕�����
     * @param invocation ���\�b�h�Ăяo���̏��
     * @return ���\�b�h�Ăяo���̏ڍׂȃ��O
     */
    private StringBuffer createCallDetail(StringBuffer methodCallBuff,
            MethodInvocation invocation)
    {
        String timeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
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
    private StringBuffer createReturnDetail(StringBuffer methodCallBuff,
            Object ret)
    {
        StringBuffer returnDetailBuff = new StringBuffer(512);
        String returnTimeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
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
            String calleeClassName, String objectID, String threadInfo,
            Throwable cause)
    {
        String throwTimeStr = ((SimpleDateFormat) this.timeFormat_.get())
                .format(new Date());
        String throwableID = Integer
                .toHexString(System.identityHashCode(cause));
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
     * �����̓��e�����O�o�͂��邩�ǂ����ݒ肷��B
     * @param isLogArgs ���������O�o�͂���Ȃ�true
     */
    public void setLogArgs(boolean isLogArgs)
    {
        this.isLogArgs_ = isLogArgs;
    }

    /**
     * �߂�l�̓��e�����O�o�͂��邩�ǂ����ݒ肷��B
     * @param isLogReturn �߂�l�����O�o�͂���Ȃ�true
     */
    public void setLogReturn(boolean isLogReturn)
    {
        this.isLogReturn_ = isLogReturn;
    }

    /**
     * ���\�b�h�Ăяo���܂ł̃X�^�b�N�g���[�X�����O�o�͂��邩�ݒ肷��B
     * @param isLogStackTrace �X�^�b�N�g���[�X�����O�o�͂���Ȃ�true
     */
    public void setLogStackTrace(boolean isLogStackTrace)
    {
        this.isLogStackTrace_ = isLogStackTrace;
    }
}