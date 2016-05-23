(ns gdx.core.desktop-launcher
  (:require [gdx.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. gdx-game "gdx" 1024 768)
  (Keyboard/enableRepeatEvents true)
  )
