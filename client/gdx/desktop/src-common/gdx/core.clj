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

(defn do-login [ctrl email pass]
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


(defscreen ui-screen
  :on-show
  (fn [screen entities]
    (update! screen 
             :renderer (stage)
             :camera (orthographic))
    (let [ui-skin (skin skin-path)
          secCamera (orthographic)
          lblHeader (label "Enter login/password" ui-skin)
          txtEmail (text-field "test" ui-skin)
          txtPass (text-field "test" ui-skin :set-password-mode true :set-password-character \*)
          lblError (label "" ui-skin)
          d (proxy [Dialog] ["Login" ui-skin]
              (result [stuff] (let [result (do-login this
                                                     (text-field! txtEmail :get-text)
                                                     (text-field! txtPass :get-text))]
                                (when-not (str/blank? result)
                                  (label! lblError :set-text result)))))
          login-dialog (ActorEntity. d)
          content-table (dialog! login-dialog :get-content-table)]
      (cell! (table! content-table :add (:object lblHeader)) :colspan 2)
      (table! content-table :row)
      (dialog! login-dialog :key 66 true)
      (dialog! login-dialog :button "Login")
      (cell! (table! content-table :add "Login:") :align (align :right))
      (cell! (table! content-table :add (:object txtEmail)) :width 300 :height 30)
      (table! content-table :row)
      (cell! (table! content-table :add "Password:") :align (align :right))
      (cell! (table! content-table :add (:object txtPass)) :width 300 :height 30)
      (table! content-table :row)
      (cell! (table! content-table :add (:object lblError)) :colspan 2)
      (dialog! login-dialog :show (stage))
      (dialog! login-dialog :pack)
      login-dialog))
    
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
