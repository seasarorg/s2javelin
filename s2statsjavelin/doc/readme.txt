     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2006/05/14
   _/                       S2StatsJavelin �ȈՃ}�j���A��                    _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


�P.�T�v

    S2StatsJavelin�́ASeasar��AOP�@�\�𗘗p���ASeasar��̃A�v���P�[�V�����̓����
    JMX�o�R�ŊĎ��\�ɂ���c�[���ł��B

    ���݂̂Ƃ���A�ȉ��̏����Q�Ƃ��邱�Ƃ��\�ł��B

    1. ���\�b�h�̌Ăяo����
    2. ���\�b�h�̕��Ϗ������ԁi�~���b�P�ʁj
    3. ���\�b�h�̍ő又�����ԁi�~���b�P�ʁj
    4. ���\�b�h�̍ŏ��������ԁi�~���b�P�ʁj
    5. ���\�b�h�̌Ăяo����
    6. ��O�̔�����
    7. ��O�̔�������

    �܂��A���s���ɃJ�E���^�����Z�b�g���邱�Ƃ��\�ł��B

    S2StatsJavelin��Interceptor�̈��ł��̂ŁAdicon�t�@�C���ւ̒ǋL�ŁA�ȒP�ɐ�
    ��ł��܂��B
    �܂��AServletFilter�𕹗p���邱�Ƃɂ��AWeb�y�[�W�ւ̃A�N�Z�X�ɂ��Ă��A
    ��L�̏������ԓ��̏����Q�Ƃ��邱�Ƃ��\�ɂȂ�܂����B


�Q�D�����

    S2StatsJavelin��JRE 5.0�œ�����m�F���Ă��܂��B
    HTTP�A�_�v�^���g�p���āAHTTP�o�R�ŏ����Q�Ƃ���ꍇ�AMX4J���K�v�ł��B


�R�D�C���X�g�[�����@

    �C���X�g�[���ɕK�v�Ȑݒ�́A
  
        -------------------------
        (1) ���C�u�����̔z�u
        (2) dicon�̐ݒ�
        (3) web.xml�̐ݒ�
        -------------------------

    �̂R�i�K�ł��B


    (1) ���C�u�����̔z�u

        S2StatsJavelin.jar���A��͑ΏۃA�v���P�[�V�����̃N���X�p�X�Ɋ܂߂܂��B
        HTTP�|�[�g�o�R�ŏ����Q�Ƃ������ꍇ�́AMX4J���ǉ����܂��B


    (2) dicon�̐ݒ�

        dicon��Interceptor�̒�`��ǉ����܂��B

        S2StatsJavelin�̖{�̂́A"S2StatsJavelinInterceptor"�ł��B
        ����S2StatsJavelinInterceptor���Ď��ΏۃN���X�ɓK�p���邽�߂ɁAdicon�ւ̒�
        �L���K�v�ɂȂ�܂��B


        �܂��A�Y�t��javelin.dicon���R�s�[���āA�A�v���P�[�V�����ɒǉ����Ă�����
        ���B

        ����javelin.dicon�̓��e�ł��BjavelinTraceInterceptor�̐錾���s���Ă��܂��B

        ========================================================================
		<components namespace="javelin">
    		<component name="S2StatsJavelinInterceptor"
        		class="org.seasar.javelin.jmx.S2StatsJavelinInterceptor">
        		<property name="intervalMax">1000</property>
        		<property name="throwableMax">1000</property>
        		<property name="recordThreshold">0</property>
        		<property name="alarmThreshold">1000</property>
        		<property name="httpPort">10000</property>
				<property name="domain">"org.seasar.javelin.jmx.s2jsfexample"</property>
    		</component>
		</components>
        ========================================================================

        S2StatsJavelinInterceptor�̂T�̃v���p�e�B�̈Ӗ��͎��̒ʂ�ł��B


               ���O        |�f�t�H���g|                 �Ӗ�
            ---------------+----------+-----------------------------------------
            intervalMax    |    1000  |���\�b�h�̏������Ԃ��L�^����񐔂ł��B
            ---------------+----------+-----------------------------------------
            throwableMax   |    1000  |��O�̔������L�^����񐔂ł��B
            ---------------+----------+-----------------------------------------
            recordThreshold|       0  |���\�b�h�̏������Ԃ��L�^����臒l���~���b
                           |          |�P�ʂŎw�肵�܂��B
                           |          |�w�肵���l�������Ăяo���͋L�^���܂���B
                           |          |臒l�̔���̓��\�b�h�̃R�[���c���[�̃��[�g
                           |          |�Ŕ��f���܂��B
                           |          |臒l�𒴉߂����ꍇ�A�R�[���c���[�Ɋ܂܂��
                           |          |�S�Ă̌Ăяo�����L�^���܂��B
            ---------------+----------+-----------------------------------------
            alarmThreshold |    1000  |���\�b�h�̏������Ԓ��߂�ʒm����臒l���A
                           |          |�~���b�P�ʂŎw�肵�܂��B
            ---------------+----------+-----------------------------------------
            httpPort       |       0  |����HTTP�o�R�Ō��J����ꍇ�Ɏg�p����|
                           |          |�[�g�ԍ��B
                           |          |0�̏ꍇ��HTTP�ł̌��J���s���܂���B
            ---------------+----------+-----------------------------------------
            domain         |  default |�A�v���P�[�V�����T�[�o��ŕ����̃A�v���P
                           |          |�[�V���������삵�Ă���ꍇ�̎��ʂɎg�p��
                           |          |�镶����B


        ����ɁAaspect�Ƃ���S2StatsJavelinInterceptor�����O�Ώۂ̃N���X�ɓK�p����
        ���B
        ����ArrayList�N���X�̃��\�b�h�Ăяo�������O�ɏo�͂��邽�߂̐ݒ��ł��B

        ========================================================================
        <components>
            <!-- [1] javelin.dicon���܂܂��� -->
            <include path="javelin.dicon"/>

            �i��)

            <!-- [2] S2Javelin��ArrayList�N���X�ɓK�p -->
            <component class="java.util.ArrayList>
                <aspect>javelin.S2StatsJavelinInterceptor</aspect>
            </component>
        ========================================================================


        InterceptorChain�����Ƀ��O�o�͑ΏۃN���X�֐ݒ肳��Ă���ꍇ�́A
        S2StatsJavelin�̓K�p���ȒP�ɂȂ�܂��B

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
                    <arg>javelin.S2StatsJavelinInterceptor</arg>
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
                    javelin.S2StatsJavelinInterceptor
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


    (2) web.xml�̐ݒ�

        web.xml��ServletFilter�̐ݒ��ǉ����܂��B
        �܂��ȉ���filter�ݒ��ǉ����܂��B
        init-param��dicon�t�@�C���̃p�����[�^�Ɠ���ł��B
        (httpPort��dicon�t�@�C���ł̂݁A�g�p�\�ł��B)

        ========================================================================
        <filter>
            <filter-name>S2StatsJavelinfilter</filter-name>
            <filter-class>org.seasar.javelin.jmx.S2StatsJavelinFilter</filter-class>
            <init-param>
                <param-name>domain</param-name>
                <param-value>org.seasar.javelin.jmx.s2jsfexample</param-value>
            </init-param>
            <init-param>
                <param-name>intervalMax</param-name>
                <param-value>1000</param-value>
            </init-param>
            <init-param>
                <param-name>throwableMax</param-name>
                <param-value>1000</param-value>
            </init-param>
            <init-param>
                <param-name>recordThreshold</param-name>
                <param-value>0</param-value>
            </init-param>
            <init-param>
                <param-name>alarmThreshold</param-name>
                <param-value>1000</param-value>
            </init-param>
        </filter>
        ========================================================================

        ������filter-mapping���w�肵�܂��B

        ========================================================================
        <filter-mapping>
            <filter-name>S2StatsJavelinfilter</filter-name>
            <url-pattern>*.html</url-pattern>
        </filter-mapping>
        ========================================================================


�S�D�g�p���@

    (1) �C���X�g�[��������������A�Ώۂ�Seasar�A�v���P�[�V�������N�����܂��B

    (2) HTTP�|�[�g���w�肵���ꍇ�́A�u���E�U�ŎQ�Ƃ��邱�Ƃ��\�ł��B

    (3) S2StatsJavelinViwer���g�p����ƁAEclipse�ォ��Q�Ƃ��邱�Ƃ��\�ł��B


�ȏ�

--------------------------------------------------
This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
