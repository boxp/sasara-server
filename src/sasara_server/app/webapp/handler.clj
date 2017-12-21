(ns sasara-server.app.webapp.handler
  (:require [clojure.spec.alpha :as s]
            [com.stuartsierra.component :as component]
            [cheshire.core :refer [generate-string]]
            [sasara-server.domain.usecase.speak :as speak]
            [sasara-server.app.webapp.handler-spec :as spec]))

(s/fdef index
  :args (s/cat :comp ::spec/webapp-handler-component)
  :ret ::spec/response)
(defn index
  [comp]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body "Hello!World!\n"})

(s/fdef speak
  :args (s/cat :comp ::spec/webapp-handler-component
               :message string?)
  :ret ::spec/response)
(defn speak
  [{:keys [speak-usecase-component] :as comp}
   message]
  (let [voice (speak/get-voice speak-usecase-component message)]
    (if voice
      {:status 200
       :headers {"Content-Type" "application/json; charset=utf-8"}
       :body (-> voice
                 generate-string)}
      {:status 408
       :headers {"Content-Type" "application/json; charset=utf-8"}
       :body (-> {:message "タイムアウトしました。もう一度お試しください。"}
                 generate-string)})))

(defrecord WebappHandlerComponent []
  component/Lifecycle
  (start [this]
    (println ";; Starting WebappHandlerComponent")
    this)
  (stop [this]
    (println ";; Stopping WebappHandlerComponent")
    this))

(defn webapp-handler-component []
  (map->WebappHandlerComponent {}))
