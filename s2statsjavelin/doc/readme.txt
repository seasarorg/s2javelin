     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2006/05/14
   _/                       S2StatsJavelin 簡易マニュアル                    _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


１.概要

    S2StatsJavelinは、SeasarのAOP機構を利用し、Seasar上のアプリケーションの動作を
    JMX経由で監視可能にするツールです。

    現在のところ、以下の情報を参照することが可能です。

    1. メソッドの呼び出し回数
    2. メソッドの平均処理時間（ミリ秒単位）
    3. メソッドの最大処理時間（ミリ秒単位）
    4. メソッドの最小処理時間（ミリ秒単位）
    5. メソッドの呼び出し元
    6. 例外の発生回数
    7. 例外の発生履歴

    また、実行中にカウンタをリセットすることも可能です。

    S2StatsJavelinはInterceptorの一種ですので、diconファイルへの追記で、簡単に設
    定できます。
    また、ServletFilterを併用することにより、Webページへのアクセスについても、
    上記の処理時間等の情報を参照することが可能になりました。


２．動作環境

    S2StatsJavelinはJRE 5.0で動作を確認しています。
    HTTPアダプタを使用して、HTTP経由で情報を参照する場合、MX4Jが必要です。


３．インストール方法

    インストールに必要な設定は、
  
        -------------------------
        (1) ライブラリの配置
        (2) diconの設定
        (3) web.xmlの設定
        -------------------------

    の３段階です。


    (1) ライブラリの配置

        S2StatsJavelin.jarを、解析対象アプリケーションのクラスパスに含めます。
        HTTPポート経由で情報を参照したい場合は、MX4Jも追加します。


    (2) diconの設定

        diconにInterceptorの定義を追加します。

        S2StatsJavelinの本体は、"S2StatsJavelinInterceptor"です。
        このS2StatsJavelinInterceptorを監視対象クラスに適用するために、diconへの追
        記が必要になります。


        まず、添付のjavelin.diconをコピーして、アプリケーションに追加してくださ
        い。

        下はjavelin.diconの内容です。javelinTraceInterceptorの宣言を行っています。

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

        S2StatsJavelinInterceptorの５つのプロパティの意味は次の通りです。


               名前        |デフォルト|                 意味
            ---------------+----------+-----------------------------------------
            intervalMax    |    1000  |メソッドの処理時間を記録する回数です。
            ---------------+----------+-----------------------------------------
            throwableMax   |    1000  |例外の発生を記録する回数です。
            ---------------+----------+-----------------------------------------
            recordThreshold|       0  |メソッドの処理時間を記録する閾値をミリ秒
                           |          |単位で指定します。
                           |          |指定した値を下回る呼び出しは記録しません。
                           |          |閾値の判定はメソッドのコールツリーのルート
                           |          |で判断します。
                           |          |閾値を超過した場合、コールツリーに含まれる
                           |          |全ての呼び出しを記録します。
            ---------------+----------+-----------------------------------------
            alarmThreshold |    1000  |メソッドの処理時間超過を通知する閾値を、
                           |          |ミリ秒単位で指定します。
            ---------------+----------+-----------------------------------------
            httpPort       |       0  |情報をHTTP経由で公開する場合に使用するポ
                           |          |ート番号。
                           |          |0の場合はHTTPでの公開を行いません。
            ---------------+----------+-----------------------------------------
            domain         |  default |アプリケーションサーバ上で複数のアプリケ
                           |          |ーションが動作している場合の識別に使用す
                           |          |る文字列。


        さらに、aspectとしてS2StatsJavelinInterceptorをログ対象のクラスに適用しま
        す。
        下はArrayListクラスのメソッド呼び出しをログに出力するための設定例です。

        ========================================================================
        <components>
            <!-- [1] javelin.diconを含ませる -->
            <include path="javelin.dicon"/>

            （略)

            <!-- [2] S2JavelinをArrayListクラスに適用 -->
            <component class="java.util.ArrayList>
                <aspect>javelin.S2StatsJavelinInterceptor</aspect>
            </component>
        ========================================================================


        InterceptorChainが既にログ出力対象クラスへ設定されている場合は、
        S2StatsJavelinの適用も簡単になります。

        "sampleInterceptorChain"というInterceptorChainが定義されていた場合の設定
        例を示します。

        ========================================================================
        <components>
            <!-- [1] javelin.diconを含ませる -->
            <include path="javelin.dicon"/>

            <!-- sampleInterceptorChainの定義 -->
            <component name="sampleInterceptorChain"
                    class="org.seasar.javelin.InterceptorChain">

                <!-- [2] S2JavelinをChainに追加する -->
                <initMethod name="add">
                    <arg>javelin.S2StatsJavelinInterceptor</arg>
                </initMethod>

                （略）
    
            </component>
        ========================================================================

        この追記によって、sampleInterceptorChainが設定されている全クラスについて、
        ログが出力されるようになります。


        また、Seasar Ver2.3で追加されたアスペクトの自動登録機能を用いると、パッ
        ケージ単位などでの、ログ出力対象クラスの一括指定が可能になります。

        下は、examples.di.impl以下の全パッケージについて、名前がImplで終わるクラ
        スを指定した例です。

        ========================================================================
        <components>
            <!-- [1] javelin.diconを含ませる -->
            <include path="javelin.dicon"/>

            <!-- [2] S2JavelinをAspectAutoRegisterに追加する -->
            <component class="org.seasar.framework.container.autoregister.AspectAutoRegister">
                <property name="interceptor">
                    javelin.S2StatsJavelinInterceptor
                </property>
                <!-- [3] ログ出力の対象を指定する -->
                <initMethod name="addClassPattern">
                    <!-- 1つ目はパッケージの指定 -->
                    <arg>"examples.di.impl"</arg>
                    <!-- 2つ目はクラス名の正規表現 -->
                    <arg>".*Impl"</arg>
                </initMethod>

                <!-- 次の指定は、Seasar Ver2.3.0では"registAll"と書くこと。
                     また、Ver2.3.2以降では書く必要はない。 -->
                <initMethod name="registerAll"/>

            </component>
        ========================================================================

        その他にも、登録したくないクラスパターンを指定するなどの、詳細な記述が可
        能です。
        詳しくは、こちらを参照してください。

        「S2Containerリファレンス：アスペクトの自動登録」
            http://s2container.seasar.org/ja/DIContainer.html#AspectAutoRegister


    (2) web.xmlの設定

        web.xmlにServletFilterの設定を追加します。
        まず以下のfilter設定を追加します。
        init-paramはdiconファイルのパラメータと同一です。
        (httpPortはdiconファイルでのみ、使用可能です。)

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

        続いてfilter-mappingを指定します。

        ========================================================================
        <filter-mapping>
            <filter-name>S2StatsJavelinfilter</filter-name>
            <url-pattern>*.html</url-pattern>
        </filter-mapping>
        ========================================================================


４．使用方法

    (1) インストールが完了したら、対象のSeasarアプリケーションを起動します。

    (2) HTTPポートを指定した場合は、ブラウザで参照することが可能です。

    (3) S2StatsJavelinViwerを使用すると、Eclipse上から参照することが可能です。


以上

--------------------------------------------------
This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
