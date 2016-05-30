(defproject gdx "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  
  :dependencies [[com.badlogicgames.gdx/gdx "1.9.3"]
                 [com.badlogicgames.gdx/gdx-backend-lwjgl "1.9.3"]
                 [com.badlogicgames.gdx/gdx-platform "1.9.3"
                  :classifier "natives-desktop"]
                 [org.clojure/clojure "1.8.0"]
                 [cheshire "5.6.1"]
                 [play-clj "0.4.7"] ]
  
  :source-paths ["src" "src-common"]
  :javac-options ["-target" "1.6" "-source" "1.6" "-Xlint:-options"]
  :aot [gdx.core.desktop-launcher]
  :main gdx.core.desktop-launcher
  )
