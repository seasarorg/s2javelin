     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2005/12/02
   _/                       S2Javelin 簡易マニュアル                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


１.概要

    S2Javelinは、SeasarのAOP機構を利用し、Seasar上のアプリケーションの動作ログを
    出力するツールです。
    また、Seasarアプリケーション上で発行されたSQLについても、同様にログを取得し
    ます。

    出力した動作ログとSQLログは、そのままArrowVision(※１)を用いてシーケンス図と
    して表示させることが可能です。

    S2Javelinの実態はSeasar2に標準添付されているTraceInterceptorの一種ですので、
    diconファイルへの追記で、簡単に設定できます。


    ※１）ArrowVisionはエスエムジー株式会社が公開しているフリーの解析ツールです。
          詳しくは「４．ArrowVisionについて」をご参照下さい。



２．インストール方法

    インストールに必要な設定は、
  
        -------------------------
        (1) ライブラリの配置
        (2) diconの設定
        (3) log4jプロパティの修正
        -------------------------

    の３段階です。


    (1) ライブラリの配置

        s2javelin.jarを、解析対象アプリケーションのクラスパスに含めます。


    (2) diconの設定

        diconにInterceptorの定義を追加します。

        S2Javelinの本体は、ログを独自の形式で出力するInterceptorである
        "JavelinTraceInterceptor"です。
        このJavelinTraceInterceptorをログ出力対象クラスに適用するために、diconへ
        の追記が必要になります。


        まず、添付のjavelin.diconをコピーして、アプリケーションに含ませてくださ
        い。

        下はjavelin.diconの内容です。javelinTraceInterceptorの宣言を行っています。

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

        JavelinTraceInterceptorの３つのプロパティの意味は次の通りです。
        （全てboolean型のプロパティです）


               名前      |デフォルト|                 意味
            -------------+----------+-------------------------------------------
            logArgs      |  true    |trueならメソッドの引数値をログに出力します
            -------------+----------+-------------------------------------------
            logReturn    |  true    |trueならメソッドの戻り値をログに出力します
            -------------+----------+-------------------------------------------
            logStackTrace|  false   |trueならメソッドが呼び出されるまでのスタッ
                         |          |クトレースを、メソッドごとにログ出力します。
                         |          |※ログの量が極端に多くなりますので、注意が必
                         |          |  要です。


        あとは、aspectとしてjavelinTraceInterceptorをログ対象のクラスに適用しま
        す。
        下はArrayListクラスのメソッド呼び出しをログに出力するための設定例です。

        ========================================================================
        <components>
            <!-- [1] javelin.diconを含ませる -->
            <include path="javelin.dicon"/>

            （略)

            <!-- [2] S2JavelinをArrayListクラスに適用 -->
            <component class="java.util.ArrayList">
                <aspect>javelin.javelinTraceInterceptor</aspect>
            </component>
        ========================================================================


        InterceptorChainが既にログ出力対象クラスへ設定されている場合は、
        S2Javelinの適用も簡単になります。

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
                    <arg>javelin.javelinTraceInterceptor</arg>
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
                    javelin.javelinTraceInterceptor
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


    (3) log4jプロパティの修正

        log4j.propertiesに、次を追記します。
        (添付のlog4j.propertiesファイルが、下と同じ内容のファイルになっています）
        ========================================================================
        #S2Javelin Logger
        log4j.category.org.seasar.javelin.JavelinTraceInterceptor=DEBUG, Javelin
        log4j.appender.Javelin=org.apache.log4j.FileAppender
        #S2Javelin log file
        log4j.appender.Javelin.File=c:\\temp\\s2javelin.jvn ←★ログの出力先★
        log4j.appender.Javelin.layout=org.apache.log4j.PatternLayout
        log4j.appender.Javelin.layout.ConversionPattern=%m%n
        log4j.additivity.org.seasar=false

        # SQL Logger
        log4j.category.org.seasar.extension.jdbc.impl=DEBUG, DaoSql
        log4j.category.org.seasar.dao=DEBUG, DaoSql
        log4j.appender.DaoSql=org.apache.log4j.FileAppender
        #SQL log file
        log4j.appender.DaoSql.File=c:\\temp\\s2javelin.jvn ←★ログの出力先★
        log4j.appender.DaoSql.layout=org.apache.log4j.PatternLayout
        log4j.appender.DaoSql.layout.ConversionPattern=Write ,%d{yyyy/MM/dd HH:mm:ss.SSS},,DaoSQL,0,%M,%C,0,public,%t%n<<javelin.FieldValue_START>>%n%m%n<<javelin.FieldValue_END>>%n%n
        ========================================================================

        ★ログの出力先★で示した部分に、ログ出力するファイルのフルパスを指定して
        ください。


３．使用方法

    (1) インストールが完了したら、対象のSeasarアプリケーションを起動します。
        ⇒「２．インストール」の「log4jプロパティの修正」で指定した場所に、ログ
          ファイルが生成されます。

    (2) (1)で生成されたログファイルを、そのままArrowVisionの画面上にドラッグ＆ド
        ロップしてください。
        ⇒SQLの呼び出しも含めた、動作シーケンスが表示されます。


４．ArrowVisionについて

    S2Javelinのログファイルをビジュアルに表示するためには「ArrowVision」という表
    示用ツールが必要になります。

    ArrowVisionは、エスエムジー株式会社のホームページから、無償でダウンロードす
    ることができます。

    「ArrowVision」
        http://www.smg.co.jp/service/products/arrow_vision/index.html


以上



This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
