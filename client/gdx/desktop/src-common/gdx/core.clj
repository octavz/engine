(ns gdx.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [cheshire.core :refer :all]
            [clojure.walk :refer :all]
            ))

(require '[clojure.string :as str])
(import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
        com.badlogic.gdx.scenes.scene2d.EventListener
        com.badlogic.gdx.scenes.scene2d.ui.Dialog
        [play_clj.entities ActorEntity])

(declare  main-screen ui-screen)

(def skin-path "uiskin.json")

(def server-url "http://localhost:9000/")

(defn sc [dpi] (- 600 (* 80 (-  dpi 0.6 ))))

(defn scale-width [] (let [dpi (graphics! :get-density )
                           width (game :width) 
                           ret (sc dpi)
                           ret1 (sc 3) ]
                       (prn ret)
                       (prn ret1)
                       ret))

(defn request [url method body ] 
  (let [request (com.badlogic.gdx.Net$HttpRequest. method)]
    (.setUrl request url)
    (.setHeader request "Content-Type" "application/json")
    (.setHeader request "Accept" "application/json")
    (.setHeader request "Accept" "application/json")
    (when-not (nil? body)
      (.setContent request body))
    request))

(defn after-login [player] 
  (prn (player :player))
  (prn (player :items))
  )

(defn do-login [screen entities ctrl email pass]
  (do
    (dialog! ctrl :cancel)
    (if (or (str/blank? email) (str/blank? pass))
      "Email or password is empty"
      (let [listener (proxy [com.badlogic.gdx.Net$HttpResponseListener][]
                      (handleHttpResponse [response]
                        (dialog! ctrl :hide);validate that response is ok
                        (after-login (keywordize-keys (parse-string (.getResultAsString response))))))
            request (request (str server-url "login") 
                             "POST"
                             (generate-string {:login email :password pass}))]
        (net! :send-http-request request listener)
        ""))))

(defn user-bar [screen entities ui-skin] (let [lbl-player (label "name" ui-skin)
                                               lbl-money (label "10C" ui-skin)
                                               lbl-sector (label "Solar One" ui-skin)
                                               lbl-position (label "10,100" ui-skin)
                                               bar (horizontal [lbl-player lbl-money lbl-sector lbl-position] 
                                                               :align 16
                                                               :space 10
                                                               :set-width (graphics! :get-width)
                                                               :fill 0
                                                               :set-y (- (graphics! :get-height) 10)
                                                               )
                                               ]
                                           (horizontal! bar :layout)
                                           bar))

(defn login-dialog [screen entities ui-skin] 
  (let  [txt-email (text-field "test" ui-skin)
         txt-pass (text-field "test" ui-skin :set-password-mode true :set-password-character \*)
         lbl-error (label "" ui-skin)
         dlg-ret (ActorEntity. (proxy [Dialog] ["Login" ui-skin]
             (result [stuff] 
               (let [result (do-login screen
                                      entities
                                      this
                                      (text-field! txt-email :get-text)
                                      (text-field! txt-pass :get-text))]
                 (when-not (str/blank? result)
                   (label! lbl-error :set-text result))))))
         content-table (dialog! dlg-ret :get-content-table)]
    (cell! (table! content-table :add (:object (label "Enter login/password" ui-skin))) :colspan 2)
    (table! content-table :row)
    (dialog! dlg-ret :key 66 true)
    (dialog! dlg-ret :button "Login")
    (cell! (table! content-table :add "Login:") :align (align :right))
    (cell! (table! content-table :add (:object txt-email)) :width 300 :height 30)
    (table! content-table :row)
    (cell! (table! content-table :add "Password:") :align (align :right))
    (cell! (table! content-table :add (:object txt-pass)) :width 300 :height 30)
    (table! content-table :row)
    (cell! (table! content-table :add (:object lbl-error)) :colspan 2)
    (dialog! dlg-ret :pack)
    dlg-ret))

(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen 
             :renderer (stage)
             :camera (orthographic))
    (let [ui-skin (skin skin-path)
          dlg (login-dialog screen entities ui-skin)
          bar (user-bar screen entities ui-skin)
          ]
      (dialog! dlg :show (stage))
      [bar dlg]))
    
  :on-resize
  (fn [screen entities]
    (height! screen (scale-width)))

  :on-render
  (fn [screen entities]
    (render! screen entities)))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen 
             :renderer (stage)
             :camera (orthographic))
    (label (str (graphics! :get-density)) (color :green)))
    
  :on-resize
  (fn [screen entities]
    (height! screen (scale-width)))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities)))

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)))))
(defgame gdx-game
  :on-create
  (fn [this]
    (set-screen! this main-screen ui-screen)))


(in-ns 'gdx.core)
(use 'play-clj.repl)
;(gdx.core.desktop-launcher/-main)
(try
  (on-gl (set-screen! gdx-game main-screen ui-screen))
(catch Exception e 
  (.printStackTrace e)))
