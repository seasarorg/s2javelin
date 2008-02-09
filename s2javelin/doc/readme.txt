     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2008/02/09
   _/                       S2Javelin 簡易マニュアル                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


１.概要

    S2Javelinとは、Seasar2のAOP機能を用いて、動作ログ出力機能および性能情報の
    通信機能を組み込むプロダクトです。
　　出力された動作ログはArrowVisionによってシーケンス図表示することができます。
　　またBottleneckEyeによってアプリケーションの性能情報の監視を、
　　クラス図により行うことができます。

    ※１）ArrowVisionはエスエムジー株式会社が公開しているフリーの解析ツールです。
          詳しくは「４．ArrowVisionについて」をご参照下さい。


２．インストール方法

    インストールに必要な設定は、
  
        -------------------------
        (1) S2Javelinの配置
        (2) ライブラリの配置
        (3) JVM起動オプションの変更
        (4) Javelin設定ファイルの変更(javelin.properties)
        (5) diconの設定(S2JavelinInterceptor)
        (6) web.xmlの設定(S2JavelinFilter)
        (7) log4jプロパティの修正
        (8) 適用の確認
        -------------------------

    の8段階です。

    (1) S2Javelinの配置
        zipを展開してできた、以下のディレクトリ階層を、
        インストール先に配置します。

        Javelin
        │  readme.txt
        │  SEASAR-LICENSE.TXT
        │      
        ├─conf
        │      javelin.properties
        │      
        ├─lib
        │      s2javelin.jar
        │      s2javelin_core.jar
        │      
        └─logs
                deleteme.txt

        以下の説明では、C:\Javelin配下に配置したことを前提とします。

    (2) ライブラリの配置

        s2javelin.jar、およびs2javelin_core.jarを、解析対象アプリケーションのクラスパスに含めます。
        Webアプリケーションの場合は、WEB-INF/libディレクトリにs2javelin.jarをコピーしてください。

    (3) JVM起動オプションの変更
        JVM起動オプションに、以下を追加します。
        
        ========================================================================        
        -Djavelin.properties=C:\Javelin\conf\javelin.properties
        ========================================================================

    (4) Javelin設定ファイルの変更(javelin.properties)
        前の項目で指定した、javelin.properties内の設定を、環境に合わせて変更します。
        以下の3つのパラメータは、
        監視結果をArrowVision、BottleneckEyeで確認する際に必要となります。

        ・javelin.javelinFileDir
            S2Javelinの出力する動作ログの出力先ディレクトリ
        ・javelin.error.log
            S2Javelin本体のログの出力ファイル
        ・javelin.acceptPort
            BottleneckEyeとの通信に使用するポート番号

        ========================================================================
            javelin.javelinFileDir=C:/Javelin/logs
            javelin.error.log=C:/Javelin/logs/javelin.log
            javelin.acceptPort=18000
        ========================================================================

    (5) diconの設定(S2JavelinInterceptor)

        S2Javelinは、Interceptorを適用したメソッドを監視対象とするので、
        diconにInterceptorの定義を追加します。
        ※Seasarのバージョンにより、アスペクトの適用方法も変わるため、
          各バージョンに合わせた設定を行ってください。

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

        また、アスペクトの自動登録機能を用いると、パッケージ単位などでの、
        監視対象クラスの一括指定が可能になります。

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

    (6) web.xmlの設定(S2JavelinFilter)
        
        web.xmlのfilter定義部分に、以下の内容を記述します。
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter>
            <filter-name>s2JavelinFilter</filter-name>
            <filter-class>
                org.seasar.javelin.S2JavelinFilter
            </filter-class>
        </filter>
        ========================================================================

        web.xmlのfilter-mapping定義部分に、以下の内容を記述します。
        ※encodingfilterより後に定義する必要があります。
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter-mapping>
            <filter-name>s2JavelinFilter</filter-name>
            <url-pattern>/*</url-pattern>
        </filter-mapping>
        ========================================================================

    (7) log4jプロパティの修正

        log4j.propertiesに、以下の内容を追記します。
        ========================================================================
        # SQL Logger
        log4j.category.org.seasar.extension.jdbc.impl=DEBUG, DaoSql
        log4j.category.org.seasar.dao=DEBUG, DaoSql
        log4j.appender.DaoSql=org.apache.log4j.FileAppender
        #SQL log file SQLログの出力先
        log4j.appender.DaoSql.File=c:\\Javelin\\logs\\s2javelin.jvn
        log4j.appender.DaoSql.layout=org.apache.log4j.PatternLayout
        log4j.appender.DaoSql.layout.ConversionPattern=Write ,%d{yyyy/MM/dd HH:mm:ss.SSS},,DaoSQL,0,%M,%C,0,public,%t%n<<javelin.FieldValue_START>>%n%m%n<<javelin.FieldValue_END>>%n%n
        ========================================================================

        ★ログの出力先★で示した部分に、ログ出力するファイルのフルパスを指定して
        ください。

    (8) 適用の確認
        以上の設定を行った後、アプリケーションを起動して、
        監視対象とした処理が実行される様にアプリケーションを操作すると、
        アプリケーションの標準出力に以下のような内容が出力されることを確認してください。
        ※パラメータの詳細は、javelin.propertiesに設定した内容に依存します。
        
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

３．使用方法(ArrowVision)

    (1) 対象のSeasarアプリケーションを起動し動作させる。
        ⇒「２．インストール」の「Javelin設定ファイルの変更」及び
          「log4jプロパティの修正」で指定した場所に、ログファイルが生成されます。

    (2) (1)で生成されたログファイルを、そのままArrowVisionの画面上にドラッグ＆ド
        ロップしてください。
        ⇒SQLの呼び出しも含めた、動作シーケンスが表示されます。

４．使用方法(BottleneckEye)

    (1) 対象のSeasarアプリケーションを起動し動作させる。

    (2) BottleneckEyeファイル(拡張子".beye"のファイル)を作成する。
        「File」→「New」→「Bottleneck Eye」→「File」を選択し、
        ファイル名を入力する。
    
    (3) BottleneckEyeファイルを開き、S2Javelinに接続する設定を行う。
        「Settings」タブを選択し、以下の内容を入力する。

        Host: SeasarアプリケーションのあるマシンのIP
        Port: javelin.acceptPortで指定したポート番号
        
    (4) S2Javelinに接続する。
        「Start」ボタンをクリックする。
        ⇒アプリケーションの構造が表示されます。

５．ArrowVisionについて

    S2Javelinのログファイルをビジュアルに表示するためには「ArrowVision」という表
    示用ツールが必要になります。

    ArrowVisionは、エスエムジー株式会社のホームページから、無償でダウンロードす
    ることができます。

    「ArrowVision」
        http://www.smg.co.jp/service/products/arrow_vision/index.html

以上



This product includes software developed by the 
Seasar Project (http://www.seasar.org/).
