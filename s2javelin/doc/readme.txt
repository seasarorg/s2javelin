     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2008/02/09
   _/                       S2Javelin �ȈՃ}�j���A��                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


�P.�T�v

    S2Javelin�Ƃ́ASeasar2��AOP�@�\��p���āA���샍�O�o�͋@�\����ѐ��\����
    �ʐM�@�\��g�ݍ��ރv���_�N�g�ł��B
�@�@�o�͂��ꂽ���샍�O��ArrowVision�ɂ���ăV�[�P���X�}�\�����邱�Ƃ��ł��܂��B
�@�@�܂�BottleneckEye�ɂ���ăA�v���P�[�V�����̐��\���̊Ď����A
�@�@�N���X�}�ɂ��s�����Ƃ��ł��܂��B

    ���P�jArrowVision�̓G�X�G���W�[������Ђ����J���Ă���t���[�̉�̓c�[���ł��B
          �ڂ����́u�S�DArrowVision�ɂ��āv�����Q�Ɖ������B


�Q�D�C���X�g�[�����@

    �C���X�g�[���ɕK�v�Ȑݒ�́A
  
        -------------------------
        (1) S2Javelin�̔z�u
        (2) ���C�u�����̔z�u
        (3) JVM�N���I�v�V�����̕ύX
        (4) Javelin�ݒ�t�@�C���̕ύX(javelin.properties)
        (5) dicon�̐ݒ�(S2JavelinInterceptor)
        (6) web.xml�̐ݒ�(S2JavelinFilter)
        (7) log4j�v���p�e�B�̏C��
        (8) �K�p�̊m�F
        -------------------------

    ��8�i�K�ł��B

    (1) S2Javelin�̔z�u
        zip��W�J���Ăł����A�ȉ��̃f�B���N�g���K�w���A
        �C���X�g�[����ɔz�u���܂��B

        Javelin
        ��  readme.txt
        ��  SEASAR-LICENSE.TXT
        ��      
        ����conf
        ��      javelin.properties
        ��      
        ����lib
        ��      s2javelin.jar
        ��      s2javelin_core.jar
        ��      
        ����logs
                deleteme.txt

        �ȉ��̐����ł́AC:\Javelin�z���ɔz�u�������Ƃ�O��Ƃ��܂��B

    (2) ���C�u�����̔z�u

        s2javelin.jar�A�����s2javelin_core.jar���A��͑ΏۃA�v���P�[�V�����̃N���X�p�X�Ɋ܂߂܂��B
        Web�A�v���P�[�V�����̏ꍇ�́AWEB-INF/lib�f�B���N�g����s2javelin.jar���R�s�[���Ă��������B

    (3) JVM�N���I�v�V�����̕ύX
        JVM�N���I�v�V�����ɁA�ȉ���ǉ����܂��B
        
        ========================================================================        
        -Djavelin.properties=C:\Javelin\conf\javelin.properties
        ========================================================================

    (4) Javelin�ݒ�t�@�C���̕ύX(javelin.properties)
        �O�̍��ڂŎw�肵���Ajavelin.properties���̐ݒ���A���ɍ��킹�ĕύX���܂��B
        �ȉ���3�̃p�����[�^�́A
        �Ď����ʂ�ArrowVision�ABottleneckEye�Ŋm�F����ۂɕK�v�ƂȂ�܂��B

        �Ejavelin.javelinFileDir
            S2Javelin�̏o�͂��铮�샍�O�̏o�͐�f�B���N�g��
        �Ejavelin.error.log
            S2Javelin�{�̂̃��O�̏o�̓t�@�C��
        �Ejavelin.acceptPort
            BottleneckEye�Ƃ̒ʐM�Ɏg�p����|�[�g�ԍ�

        ========================================================================
            javelin.javelinFileDir=C:/Javelin/logs
            javelin.error.log=C:/Javelin/logs/javelin.log
            javelin.acceptPort=18000
        ========================================================================

    (5) dicon�̐ݒ�(S2JavelinInterceptor)

        S2Javelin�́AInterceptor��K�p�������\�b�h���Ď��ΏۂƂ���̂ŁA
        dicon��Interceptor�̒�`��ǉ����܂��B
        ��Seasar�̃o�[�W�����ɂ��A�A�X�y�N�g�̓K�p���@���ς�邽�߁A
          �e�o�[�W�����ɍ��킹���ݒ���s���Ă��������B

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

        �܂��A�A�X�y�N�g�̎����o�^�@�\��p����ƁA�p�b�P�[�W�P�ʂȂǂł́A
        �Ď��ΏۃN���X�̈ꊇ�w�肪�\�ɂȂ�܂��B

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

    (6) web.xml�̐ݒ�(S2JavelinFilter)
        
        web.xml��filter��`�����ɁA�ȉ��̓��e���L�q���܂��B
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter>
            <filter-name>s2JavelinFilter</filter-name>
            <filter-class>
                org.seasar.javelin.S2JavelinFilter
            </filter-class>
        </filter>
        ========================================================================

        web.xml��filter-mapping��`�����ɁA�ȉ��̓��e���L�q���܂��B
        ��encodingfilter����ɒ�`����K�v������܂��B
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter-mapping>
            <filter-name>s2JavelinFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        ========================================================================

    (7) log4j�v���p�e�B�̏C��

        log4j.properties�ɁA�ȉ��̓��e��ǋL���܂��B
        ========================================================================
        # SQL Logger
        log4j.category.org.seasar.extension.jdbc.impl=DEBUG, DaoSql
        log4j.category.org.seasar.dao=DEBUG, DaoSql
        log4j.appender.DaoSql=org.apache.log4j.FileAppender
        #SQL log file SQL���O�̏o�͐�
        log4j.appender.DaoSql.File=c:\\Javelin\\logs\\s2javelin.jvn
        log4j.appender.DaoSql.layout=org.apache.log4j.PatternLayout
        log4j.appender.DaoSql.layout.ConversionPattern=Write ,%d{yyyy/MM/dd HH:mm:ss.SSS},,DaoSQL,0,%M,%C,0,public,%t%n<<javelin.FieldValue_START>>%n%m%n<<javelin.FieldValue_END>>%n%n
        ========================================================================

        �����O�̏o�͐恚�Ŏ����������ɁA���O�o�͂���t�@�C���̃t���p�X���w�肵��
        ���������B

    (8) �K�p�̊m�F
        �ȏ�̐ݒ���s������A�A�v���P�[�V�������N�����āA
        �Ď��ΏۂƂ������������s�����l�ɃA�v���P�[�V�����𑀍삷��ƁA
        �A�v���P�[�V�����̕W���o�͂Ɉȉ��̂悤�ȓ��e���o�͂���邱�Ƃ��m�F���Ă��������B
        ���p�����[�^�̏ڍׂ́Ajavelin.properties�ɐݒ肵�����e�Ɉˑ����܂��B
        
        >>>> Properties related with S2JavelinFilter
            javelin.intervalMax             : 500
            javelin.throwableMax            : 500
            javelin.javelinFileDir          : C:/Javelin/logs
            javelin.acceptPort              : 18000
            javelin.javelinEnable           : false
            javelin.stringLimitLength       : 1024
            javelin.error.log               : C:/Javelin/logs/javelin.log
            javelin.record.jmx              : true
        <<<<

        >>>> Properties related with S2JavelinInterceptor
            javelin.javelinEnable   : false
            javelin.intervalMax     : 500
            javelin.throwableMax    : 500
            javelin.recordThreshold : 0
            javelin.alarmThreshold  : 0
            javelin.domain          : default
            javelin.httpPort        : 0
        <<<<    

�R�D�g�p���@(ArrowVision)

    (1) �Ώۂ�Seasar�A�v���P�[�V�������N�������삳����B
        �ˁu�Q�D�C���X�g�[���v�́uJavelin�ݒ�t�@�C���̕ύX�v�y��
          �ulog4j�v���p�e�B�̏C���v�Ŏw�肵���ꏊ�ɁA���O�t�@�C������������܂��B

    (2) (1)�Ő������ꂽ���O�t�@�C�����A���̂܂�ArrowVision�̉�ʏ�Ƀh���b�O���h
        ���b�v���Ă��������B
        ��SQL�̌Ăяo�����܂߂��A����V�[�P���X���\������܂��B

�S�D�g�p���@(BottleneckEye)

    (1) �Ώۂ�Seasar�A�v���P�[�V�������N�������삳����B

    (2) BottleneckEye�t�@�C��(�g���q".beye"�̃t�@�C��)���쐬����B
        �uFile�v���uNew�v���uBottleneck Eye�v���uFile�v��I�����A
        �t�@�C��������͂���B
    
    (3) BottleneckEye�t�@�C�����J���AS2Javelin�ɐڑ�����ݒ���s���B
        �uSettings�v�^�u��I�����A�ȉ��̓��e����͂���B

        Host: Seasar�A�v���P�[�V�����̂���}�V����IP
        Port: javelin.acceptPort�Ŏw�肵���|�[�g�ԍ�
        
    (4) S2Javelin�ɐڑ�����B
        �uStart�v�{�^�����N���b�N����B
        �˃A�v���P�[�V�����̍\�����\������܂��B

�T�DArrowVision�ɂ���

    S2Javelin�̃��O�t�@�C�����r�W���A���ɕ\�����邽�߂ɂ́uArrowVision�v�Ƃ����\
    ���p�c�[�����K�v�ɂȂ�܂��B

    ArrowVision�́A�G�X�G���W�[������Ђ̃z�[���y�[�W����A�����Ń_�E�����[�h��
    �邱�Ƃ��ł��܂��B

    �uArrowVision�v
        http://www.smg.co.jp/service/products/arrow_vision/index.html

�ȏ�



This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
