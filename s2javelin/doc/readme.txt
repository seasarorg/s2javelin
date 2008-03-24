     _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/
                                                                2008/03/24
   _/                       S2Javelin 簡易マニュアル                       _/
 
 _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/ _/


１.概要

    S2Javelinとは、性能情報の解析機能および動作ログ出力機能を組み込むプロダクト
    です。S2Javelinは、Seasar2のAOP機能を用いて、アプリケーションのソースコード
    に変更を加える事無く、これらを実現します。

    性能情報は、BottleneckEyeによってクラス図形式や表形式で参照する事ができます。
    動作ログは、ArrowVisionによってシーケンス図形式で表示することができます。

    ※１）BottleneckEyeはエスエムジー株式会社が公開しているフリーの解析ツールです。
          詳しくは「３．使用方法(BottleneckEye)」をご参照下さい。
    ※２）ArrowVisionはエスエムジー株式会社が公開しているフリーの解析ツールです。
          詳しくは「４．使用方法(ArrowVision)」および「５．ArrowVisionについて」
          をご参照下さい。


２．S2Javelinインストール方法

    S2Javelinインストールに必要な設定は、
  
        -------------------------
        (1) S2Javelinの配置
        (2) ライブラリの配置
        (3) JVM起動オプションの変更
        (4) Javelin設定ファイルの変更(javelin.properties)
        (5) diconの設定(S2JavelinInterceptor)
        (6) web.xmlの設定(S2JavelinFilter)
        (7) 適用の確認
        -------------------------

    の9段階です。

    (1) S2Javelinの配置

        S2Javelin-V1.1.0.zipを展開してできた、以下のディレクトリ階層を、
        インストール先に配置します。

        .
        │  readme.txt(本ファイル)
        │  SEASAR-LICENSE.TXT
        │
        ├─conf
        │      javelin.properties
        │
        ├─data
        │      deleteme.txt
        │
        ├─lib
        │      s2javelin.jar
        │      s2javelin_core.jar
        │
        ├─logs
        │      deleteme.txt
        │
        └─traces
                deleteme.txt

        以下の説明では、C:\Javelin配下に配置したことを前提とします。

    (2) ライブラリの配置

        s2javelin.jar、およびs2javelin_core.jarを、解析対象アプリケーションのク
        ラスパスに追加します。
        Webアプリケーションの場合は、WEB-INF/libディレクトリにs2javelin_core.jar
        およびs2javelin.jarをコピーしてください。

    (3) JVM起動オプションの変更
        JVM起動オプションに、以下を追加します。
        
        ========================================================================
        -Djavelin.property=C:/Javelin/conf/javelin.properties
        ========================================================================

    (4) Javelin設定ファイルの変更(javelin.properties)
        前の項目で指定した、javelin.properties内の設定を、環境に合わせて変更しま
        す。詳しくは、javelin.propertiesファイルのコメントを参照ください。

    (5) diconの設定(S2JavelinInterceptor)
        S2Javelinは、Interceptorを適用したメソッドを監視対象とするので、diconに
        Interceptorの定義を追加します。
        ※Seasarのバージョンにより、アスペクトの適用方法も変わるため、
          各バージョンに合わせた設定を行ってください。

        以降、S2JSF Example 1.1.2に対して、S2Javelinを適用する際の手順を示します。

        src/main/resources/examples/jsf/dicon/allaop.diconに対して、以下の変更を
        加えます。
        ========================================================================
            (略)
            <include path="aop.dicon"/>
            <include path="dao.dicon"/>
            <include path="j2ee.dicon"/>
            <include path="javelin.dicon"/>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(追加)

        <component
            name="actionInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(追加)
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>actionThrowsInterceptor</arg></initMethod>
        </component>

        <component
            name="logicInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(追加)
            <initMethod name="add"><arg>aop.traceThrowsInterceptor</arg></initMethod>
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>j2ee.requiredTx</arg></initMethod>
        </component>

        <component
            name="daoInterceptorChain"
            class="org.seasar.framework.aop.interceptors.InterceptorChain"
        >
            <initMethod name="add"><arg>s2JavelinInterceptor</arg></initMethod>
            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^(追加)
            <initMethod name="add"><arg>aop.traceInterceptor</arg></initMethod>
            <initMethod name="add"><arg>dao.interceptor</arg></initMethod>
        </component>

        ========================================================================

        この追記によって、Action、Logic、Daoの全コンポーネントクラスに対してログ
        が出力されるようになります。

    (6) web.xmlの設定(S2JavelinFilter)
        
        web.xmlのfilter定義部分に、以下の内容を記述します。
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter>
            <filter-name>s2javelinfilter</filter-name>
            <filter-class>org.seasar.javelin.S2JavelinFilter</filter-class>
        </filter>
        ========================================================================

        web.xmlのfilter-mapping定義部分に、以下の内容を記述します。
        ※encodingfilterより後に定義する必要があります。
        ========================================================================
        <!-- S2JavelinFilter -->
        <filter-mapping>
            <filter-name>s2JavelinFilter</filter-name>
            <url-pattern>*.html</url-pattern>
        </filter-mapping>
        ========================================================================

    (7) 適用の確認
        以上の設定を行った後、アプリケーション・サーバを起動して、Webアプリケー
        ションが正常に動作する事を確認してください。

        監視対象とした処理が実行される様にアプリケーションを操作すると、アプリケ
        ーションの標準出力に以下のような内容が出力されます。
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


３．使用方法(BottleneckEye)
    BottleneckEyeは、Eclipseプラグインです。
    Eclipseのpluginsディレクトリに配置してください。

    (1) 対象のSeasarアプリケーションを起動し動作させる。

    (2) BottleneckEyeファイル(拡張子".beye"のファイル)を作成する。
        「File」→「New」→「Bottleneck Eye」→「File」を選択し、
        ファイル名を入力する。
    
    (3) S2Javelinに接続する設定を行う。
        「Settings」タブを選択し、以下の内容を入力します。

        Host   : SeasarアプリケーションのあるマシンのIP
        Port   : javelin.acceptPortで指定したポート番号
        Domain : 通信にJMXを使用するさいに必要なドメインを指定します。
                 (JMXのサポートは廃止予定です。値は変更しないで下さい。)
        Warning: クラス図上で、黄字で表示するメソッドの閾値を指定します。
        Alarm  : クラス図上で、赤字で表示するメソッドの閾値を指定します。
        Mode   : 通信方式に、TCP/JMXのいずれを使用するか指定します。
                 (JMXのサポートは廃止予定です。TCPを使用して下さい。)
        Style  : クラス間のリレーションシップの線分描画アルゴリズムを指定します。
                 (以下より、最も適したものを選択して下さい。)
                 NORMAL    - 最短の直線で描画します。
                 SHORTEST  - クラスを横切りません。
                 FAN       - 線分が重なった際に重なりを回避します。
                 MANHATTAN - 直角に曲がる線で描画します。

    (4) BottleneckEyeファイルを開き、S2Javelinに接続する。
        ファイルを開くと同時に接続を開始します。
        ⇒未接続の場合は背景はグレーです。接続に成功した場合、白色になります。


４．使用方法(ArrowVision)
    ArrowVisionは、Eclipseプラグインです。
    媒体を展開してできたディレクトリをEclipseのpluginsディレクトリに配置してください。

    (1) 対象のSeasarアプリケーションを起動し動作させる。
        ⇒「２．インストール」の「Javelin設定ファイルの変更」で指定した場所に、
          ログファイルが生成されます。

    (2) (1)で生成されたログファイルを、そのままArrowVisionの画面上にドラッグ＆ド
        ロップしてください。
        ⇒動作シーケンスが表示されます。


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
