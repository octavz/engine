(ns gdx.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]))
(import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener 
        com.badlogic.gdx.scenes.scene2d.EventListener)

(declare login-screen main-screen)

(def skin-path "uiskin.json")

(defscreen login-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (let [ui-skin (skin skin-path)
          txtEmail (text-field "" ui-skin)
          txtPass (text-field "" ui-skin :set-password-mode true :set-password-character \*)
          cb (proxy [ChangeListener] []
               ;;; for this object, we only care about 'clicked'
               (changed [evt actor] (do 
                                      (println (text-field! txtEmail :get-text))
                                      (println (text-field! txtPass :get-text))
                                      )))
          login-dialog (dialog "Login" ui-skin :button "Login" :key 66 true :add-listener ^EventListener cb) 
          content-table (dialog! login-dialog :get-content-table)
          ] 
      (cell! (table! content-table :add "Email:") :align (align :right))
      (cell! (table! content-table :add (:object txtEmail)) :width 200)
      (table! content-table :row)
      (cell! (table! content-table :add "Password:") :align (align :right)) 
      (cell! (table! content-table :add (:object txtPass)) :width 200)
      (dialog! login-dialog :show (stage))
      login-dialog))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (label "Hello Octav!" (color :green)))
  
  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))

(defgame gdx-game
  :on-create
  (fn [this]
    (set-screen! this main-screen login-screen)))
 
(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! main-screen login-screen)))))

(in-ns 'gdx.core)
(on-gl (set-screen! gdx-game main-screen login-screen))
