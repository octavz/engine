(ns gdx.core
  (:require [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [cheshire.core :refer :all]))

(require '[clojure.string :as str])
(import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
        com.badlogic.gdx.scenes.scene2d.EventListener
        com.badlogic.gdx.scenes.scene2d.ui.Dialog
        [play_clj.entities ActorEntity])

(declare  main-screen)

(def skin-path "uiskin.json")

(def server-url "http://localhost:9000/")

(defn do-login [ctrl email pass]
  (do
    (dialog! ctrl :cancel)
    (if (or (str/blank? email) (str/blank? pass))
      "Email or password is empty"
      (let [listener (proxy [com.badlogic.gdx.Net$HttpResponseListener][]
                      (handleHttpResponse [response]
                        (println (parse-string (.getResultAsString response)))
                        (dialog! ctrl :hide)))
            request (com.badlogic.gdx.Net$HttpRequest. "POST")]
        (.setUrl request (str server-url "login"))
        (.setHeader request "Content-Type" "application/json")
        (.setHeader request "Accept" "application/json")
        (.setContent request (generate-string {:login email :password pass}))
        (net! :send-http-request request listener)
        ""))) )

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (update! screen :renderer (stage))
    (let [ui-skin (skin skin-path)
          lblHeader (label "Enter login/password" ui-skin)
          txtEmail (text-field "test" ui-skin)
          txtPass (text-field "test" ui-skin :set-password-mode true :set-password-character \*)
          lblError (label "" ui-skin)
          login-dialog (ActorEntity. (proxy [Dialog] ["Login" ui-skin]
                                       (result [stuff] (let [result (do-login this
                                                                              (text-field! txtEmail :get-text)
                                                                              (text-field! txtPass :get-text))]
                                                         (when-not (str/blank? result)
                                                           (label! lblError :set-text result))))))
          content-table (dialog! login-dialog :get-content-table)]
      (cell! (table! content-table :add (:object lblHeader)) :colspan 2)
      (table! content-table :row)
      (dialog! login-dialog :key 66 true)
      (dialog! login-dialog :button "Login")
      (cell! (table! content-table :add "Login:") :align (align :right))
      (cell! (table! content-table :add (:object txtEmail)) :width 200)
      (table! content-table :row)
      (cell! (table! content-table :add "Password:") :align (align :right))
      (cell! (table! content-table :add (:object txtPass)) :width 200)
      (table! content-table :row)
      (cell! (table! content-table :add (:object lblError)) :colspan 2)
      (dialog! login-dialog :show (stage))
      (dialog! login-dialog :pack)
      login-dialog))

  :on-render
  (fn [screen entities]
    (clear!)
    (render! screen entities))
  )

(set-screen-wrapper! (fn [screen screen-fn]
                       (try (screen-fn)
                         (catch Exception e
                           (.printStackTrace e)
                           (set-screen! main-screen)))))
(defgame gdx-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))


(in-ns 'gdx.core)
(use 'play-clj.repl)
;(gdx.core.desktop-launcher/-main)
(on-gl (set-screen! gdx-game main-screen))
