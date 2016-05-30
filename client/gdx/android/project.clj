(defproject gdx "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  
  :dependencies [[com.badlogicgames.gdx/gdx "1.9.3" :use-resources true]
                 [com.badlogicgames.gdx/gdx-backend-android "1.9.3"]
                 [neko/neko "3.2.0"]
                 [org.clojure-android/clojure "1.7.0-r2" :use-resources true]
                 [cheshire "5.6.1"]
                 [play-clj "0.4.7"]]
  :plugins [[lein-droid "0.4.4"]]
  :profiles {:dev {:dependencies [[org.clojure-android/tools.nrepl "0.2.6-lollipop"]]
                   :android {:aot :all-with-unused}}
             :release {:android
                       {;; Specify the path to your private
                        ;; keystore and the the alias of the
                        ;; key you want to sign APKs with.
                        ;; :keystore-path "/home/user/.android/private.keystore"
                        ;; :key-alias "mykeyalias"
                        :aot :all}}}
  :repositories [["sonatype snapshots" "https://oss.sonatype.org/content/repositories/snapshots/"]]
  :android {;; Specify the path to the Android SDK directory either
            ;; here or in your ~/.lein/profiles.clj file.
            :sdk-path "/opt/android-sdk/"
            
            ;; Uncomment this if dexer fails with OutOfMemoryException
            ;; :force-dex-optimize true
            
            :assets-paths ["../desktop/resources"]
            :native-libraries-paths ["libs"]
            :target-version "16"
            :aot-exclude-ns ["clojure.parallel" "clojure.core.reducers" "clojure.repl" ]
            :dex-opts ["-JXmx4096M"]}
  
  :source-paths ["src/clojure" "../desktop/src-common"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"])
