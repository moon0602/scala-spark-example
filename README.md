# Overview
Scala SparkSQL Example APP

## Required middleware
- Scala
- Apache Spark
- SBT
- JDK

## Setting

#### 前提条件

- JDKがinstall済みであること
- java コマンドに環境変数Pathが通っていること

#### sbt インストール

http://www.scala-sbt.org/

- Mac

   MacPorts だとバージョンが古いので homebrew を使用

    ``brew install sbt``

- Windows

    http://www.scala-sbt.org/download.html から msi をDLして実行

- Linux

    http://www.scala-sbt.org/download.htmlからsbt-0.13.5.tgz をダウンロードして、適当な場所(hoge)に配置。hoge/sbt/binにPATHを通しておく。

    ``chmod u+x hoge/bin/sbt`` を実行


#### IntelliJ IDEA インストール

http://www.jetbrains.com/idea/download/index.html
    
公式からインストーラをDLして実行


#### IntelliJ IDEA に Scala Plugin をインストール

1. メニューの「File」 -> 「Settings」を選択
2. 「Plugins」 -> 「Browse Repositories」ボタンを押下して
   「Browse Repositories」ダイアログを開く
3. 右上の検索フォームに「scala」と入力して検索
4. 「Scala」と「SBT」をし、コンテキストメニューから「Download and Install」を実行
5. 「OK」を押下のち再び「OK」を押下
6. 再起動を要求されるので再起動


#### IntelliJ IDEA に JavaSDKのパスを指定

1. メニューの「File」->「Other Settings」-> 「Template Project Structure...」 を選択
2. 「Project Settings」 -> 「Project」 -> 「Project SDK」 の 「New」ボタンを押下
3. インストール済みJDKのパスを選択
4. 「OK」を押下


#### conscript インストール

- Windows

1. https://github.com/n8han/conscript から conscript runnable jar を DL
2. ``java -jar`` コマンドで DL した jar を実行
3. splash screen 下部のメッセージ部に 「Installed: ${インストールされたPath}」が表示されるまで待つ
4. splash screen を閉じる
5. ${インストールされたPath} に環境変数Pathを通す

- Linux, Mac

1. Terminal で ``curl https://raw.github.com/n8han/conscript/master/setup.sh | sh`` を実行 ::
2. $HOME/bin に環境変数Pathを通す


#### giter8 インストール

1. Terminal(コマンドプロンプト)で ``cs n8han/giter8`` を実行 ::


#### giter8 にてプロジェクトテンプレートを導入

1. Terminal(コマンドプロンプト)でプロジェクトフォルダを作成したいパスに移動
2. g8コマンドで任意のテンプレートを取得
   (handsonでは chrislewis/basic-project を使用) ::

       g8 chrislewis/basic-project

3. テンプレートに設定されているパラメータを入力


#### プロジェクトの plugins.sbt に sbt-idea plugin の設定を記述

1. プロジェクトフォルダ/project/plugins.sbt というファイルを作成し、下記の内容を記述 ::

       resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
       
       addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

2. **(Optional)** プロジェクトフォルダ/.gitignore というファイルを作成し、下記の内容を記述 ::

       .DS_Store
       .idea
       target

3. Terminal(コマンドプロンプト)でカレントディレクトリをプロジェクトフォルダに移動
4. ``sbt`` 実行
5. ``gen-idea`` 実行


#### IntelliJ IDEA でプロジェクトを開く

1. メニューの「File」 -> 「Open Project...」を選択
2. プロジェクトフォルダを選択


#### IntelliJ IDEA の SBT Plugin に sbt のパスを設定

1. メニューの「File」 -> 「Settings」を選択
2. 「SBT」 -> 「SBT Launcher JAR file」でインストール済みの sbt-launch.jar を指定
3. 「OK」を押下


#### IntelliJ IDEA に Scala Compiler を指定

1. メニューの「File」-> 「Settings」を選択
2. 「Project Settings」 -> 「Compiler」 -> 「Scala Compiler」を選択
3. 「Compiler library」に ``sscala-2.9.1`` を指定
4. 「OK」を押下


#### sbt-idea プラグインの不具合？対応

1. メニューの「File」 -> 「Project Structure...」を選択
2. 「Project Settings」 -> 「Modules」 -> 「default-XXXXXX」 -> 「Dependencies」タブ選択
3. ``org.scala-lang_scala-library_2.9.1`` の Scope が 「Test」 になっているのを 「Compile」 に変更
4. 「OK」を押下