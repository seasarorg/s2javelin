     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2005/12/02
   _/                       S2Javelin �ȈՃ}�j���A��                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


�P.�T�v

    S2Javelin�́ASeasar��AOP�@�\�𗘗p���ASeasar��̃A�v���P�[�V�����̓��샍�O��
    �o�͂���c�[���ł��B
    �܂��ASeasar�A�v���P�[�V������Ŕ��s���ꂽSQL�ɂ��Ă��A���l�Ƀ��O���擾��
    �܂��B

    �o�͂������샍�O��SQL���O�́A���̂܂�ArrowVision(���P)��p���ăV�[�P���X�}��
    ���ĕ\�������邱�Ƃ��\�ł��B

    S2Javelin�̎��Ԃ�Seasar2�ɕW���Y�t����Ă���TraceInterceptor�̈��ł��̂ŁA
    dicon�t�@�C���ւ̒ǋL�ŁA�ȒP�ɐݒ�ł��܂��B


    ���P�jArrowVision�̓G�X�G���W�[������Ђ����J���Ă���t���[�̉�̓c�[���ł��B
          �ڂ����́u�S�DArrowVision�ɂ��āv�����Q�Ɖ������B



�Q�D�C���X�g�[�����@

    �C���X�g�[���ɕK�v�Ȑݒ�́A
  
        -------------------------
        (1) ���C�u�����̔z�u
        (2) dicon�̐ݒ�
        (3) log4j�v���p�e�B�̏C��
        -------------------------

    �̂R�i�K�ł��B


    (1) ���C�u�����̔z�u

        s2javelin.jar���A��͑ΏۃA�v���P�[�V�����̃N���X�p�X�Ɋ܂߂܂��B


    (2) dicon�̐ݒ�

        dicon��Interceptor�̒�`��ǉ����܂��B

        S2Javelin�̖{�̂́A���O��Ǝ��̌`���ŏo�͂���Interceptor�ł���
        "JavelinTraceInterceptor"�ł��B
        ����JavelinTraceInterceptor�����O�o�͑ΏۃN���X�ɓK�p���邽�߂ɁAdicon��
        �̒ǋL���K�v�ɂȂ�܂��B


        �܂��A�Y�t��javelin.dicon���R�s�[���āA�A�v���P�[�V�����Ɋ܂܂��Ă�����
        ���B

        ����javelin.dicon�̓��e�ł��BjavelinTraceInterceptor�̐錾���s���Ă��܂��B

        ========================================================================
        <components namespace="javelin">
            <component name="javelinTraceInterceptor"
                class="org.seasar.javelin.JavelinTraceInterceptor">
                <property name="logArgs">true</property>
                <property name="logReturn">true</property>
                <property name="logStackTrace">false</property>
            </component>
        </components>
        ========================================================================

        JavelinTraceInterceptor�̂R�̃v���p�e�B�̈Ӗ��͎��̒ʂ�ł��B
        �i�S��boolean�^�̃v���p�e�B�ł��j


               ���O      |�f�t�H���g|                 �Ӗ�
            -------------+----------+-------------------------------------------
            logArgs      |  true    |true�Ȃ烁�\�b�h�̈����l�����O�ɏo�͂��܂�
            -------------+----------+-------------------------------------------
            logReturn    |  true    |true�Ȃ烁�\�b�h�̖߂�l�����O�ɏo�͂��܂�
            -------------+----------+-------------------------------------------
            logStackTrace|  false   |true�Ȃ烁�\�b�h���Ăяo�����܂ł̃X�^�b
                         |          |�N�g���[�X���A���\�b�h���ƂɃ��O�o�͂��܂��B
                         |          |�����O�̗ʂ��ɒ[�ɑ����Ȃ�܂��̂ŁA���ӂ��K
                         |          |  �v�ł��B


        ���Ƃ́Aaspect�Ƃ���javelinTraceInterceptor�����O�Ώۂ̃N���X�ɓK�p����
        ���B
        ����ArrayList�N���X�̃��\�b�h�Ăяo�������O�ɏo�͂��邽�߂̐ݒ��ł��B

        ========================================================================
        <components>
            <!-- [1] javelin.dicon���܂܂��� -->
            <include path="javelin.dicon"/>

            �i��)

            <!-- [2] S2Javelin��ArrayList�N���X�ɓK�p -->
            <component class="java.util.ArrayList">
                <aspect>javelin.javelinTraceInterceptor</aspect>
            </component>
        ========================================================================


        InterceptorChain�����Ƀ��O�o�͑ΏۃN���X�֐ݒ肳��Ă���ꍇ�́A
        S2Javelin�̓K�p���ȒP�ɂȂ�܂��B

        "sampleInterceptorChain"�Ƃ���InterceptorChain����`����Ă����ꍇ�̐ݒ�
        ��������܂��B

        ========================================================================
        <components>
            <!-- [1] javelin.dicon���܂܂��� -->
            <include path="javelin.dicon"/>

            <!-- sampleInterceptorChain�̒�` -->
            <component name="sampleInterceptorChain"
                    class="org.seasar.javelin.InterceptorChain">

                <!-- [2] S2Javelin��Chain�ɒǉ����� -->
                <initMethod name="add">
                    <arg>javelin.javelinTraceInterceptor</arg>
                </initMethod>

                �i���j
    
            </component>
        ========================================================================

        ���̒ǋL�ɂ���āAsampleInterceptorChain���ݒ肳��Ă���S�N���X�ɂ��āA
        ���O���o�͂����悤�ɂȂ�܂��B


        �܂��ASeasar Ver2.3�Œǉ����ꂽ�A�X�y�N�g�̎����o�^�@�\��p����ƁA�p�b
        �P�[�W�P�ʂȂǂł́A���O�o�͑ΏۃN���X�̈ꊇ�w�肪�\�ɂȂ�܂��B

        ���́Aexamples.di.impl�ȉ��̑S�p�b�P�[�W�ɂ��āA���O��Impl�ŏI���N��
        �X���w�肵����ł��B

        ========================================================================
        <components>
            <!-- [1] javelin.dicon���܂܂��� -->
            <include path="javelin.dicon"/>

            <!-- [2] S2Javelin��AspectAutoRegister�ɒǉ����� -->
            <component class="org.seasar.framework.container.autoregister.AspectAutoRegister">
                <property name="interceptor">
                    javelin.javelinTraceInterceptor
                </property>
                <!-- [3] ���O�o�͂̑Ώۂ��w�肷�� -->
                <initMethod name="addClassPattern">
                    <!-- 1�ڂ̓p�b�P�[�W�̎w�� -->
                    <arg>"examples.di.impl"</arg>
                    <!-- 2�ڂ̓N���X���̐��K�\�� -->
                    <arg>".*Impl"</arg>
                </initMethod>

                <!-- ���̎w��́ASeasar Ver2.3.0�ł�"registAll"�Ə������ƁB
                     �܂��AVer2.3.2�ȍ~�ł͏����K�v�͂Ȃ��B -->
                <initMethod name="registerAll"/>

            </component>
        ========================================================================

        ���̑��ɂ��A�o�^�������Ȃ��N���X�p�^�[�����w�肷��Ȃǂ́A�ڍׂȋL�q����
        �\�ł��B
        �ڂ����́A��������Q�Ƃ��Ă��������B

        �uS2Container���t�@�����X�F�A�X�y�N�g�̎����o�^�v
            http://s2container.seasar.org/ja/DIContainer.html#AspectAutoRegister


    (3) log4j�v���p�e�B�̏C��

        log4j.properties�ɁA����ǋL���܂��B
        (�Y�t��log4j.properties�t�@�C�����A���Ɠ������e�̃t�@�C���ɂȂ��Ă��܂��j
        ========================================================================
        #S2Javelin Logger
        log4j.category.org.seasar.javelin.JavelinTraceInterceptor=DEBUG, Javelin
        log4j.appender.Javelin=org.apache.log4j.FileAppender
        #S2Javelin log file
        log4j.appender.Javelin.File=c:\\temp\\s2javelin.jvn �������O�̏o�͐恚
        log4j.appender.Javelin.layout=org.apache.log4j.PatternLayout
        log4j.appender.Javelin.layout.ConversionPattern=%m%n
        log4j.additivity.org.seasar=false

        # SQL Logger
        log4j.category.org.seasar.extension.jdbc.impl=DEBUG, DaoSql
        log4j.category.org.seasar.dao=DEBUG, DaoSql
        log4j.appender.DaoSql=org.apache.log4j.FileAppender
        #SQL log file
        log4j.appender.DaoSql.File=c:\\temp\\s2javelin.jvn �������O�̏o�͐恚
        log4j.appender.DaoSql.layout=org.apache.log4j.PatternLayout
        log4j.appender.DaoSql.layout.ConversionPattern=Write ,%d{yyyy/MM/dd HH:mm:ss.SSS},,DaoSQL,0,%M,%C,0,public,%t%n<<javelin.FieldValue_START>>%n%m%n<<javelin.FieldValue_END>>%n%n
        ========================================================================

        �����O�̏o�͐恚�Ŏ����������ɁA���O�o�͂���t�@�C���̃t���p�X���w�肵��
        ���������B


�R�D�g�p���@

    (1) �C���X�g�[��������������A�Ώۂ�Seasar�A�v���P�[�V�������N�����܂��B
        �ˁu�Q�D�C���X�g�[���v�́ulog4j�v���p�e�B�̏C���v�Ŏw�肵���ꏊ�ɁA���O
          �t�@�C������������܂��B

    (2) (1)�Ő������ꂽ���O�t�@�C�����A���̂܂�ArrowVision�̉�ʏ�Ƀh���b�O���h
        ���b�v���Ă��������B
        ��SQL�̌Ăяo�����܂߂��A����V�[�P���X���\������܂��B


�S�DArrowVision�ɂ���

    S2Javelin�̃��O�t�@�C�����r�W���A���ɕ\�����邽�߂ɂ́uArrowVision�v�Ƃ����\
    ���p�c�[�����K�v�ɂȂ�܂��B

    ArrowVision�́A�G�X�G���W�[������Ђ̃z�[���y�[�W����A�����Ń_�E�����[�h��
    �邱�Ƃ��ł��܂��B

    �uArrowVision�v
        http://www.smg.co.jp/service/products/arrow_vision/index.html


�ȏ�



This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
