(ns sasara-server.app.webapp.handler
  (:require [clojure.spec.alpha :as s]
            [com.stuartsierra.component :as component]
            [cheshire.core :refer [generate-string]]
            [sasara-server.domain.usecase.speak :as speak]))

(s/def ::status #{200 404 408 204})
(s/def ::headers (s/map-of string? string?))
(s/def ::body string?)
(s/def ::response
  (s/keys :req-un [::status ::headers ::body]))
(s/def ::webapp-handler-component
  (s/keys :req-un [:speak/speak-usecase-component]))

(s/fdef index
  :args (s/cat :comp ::webapp-handler-component)
  :ret ::response)
(defn index
  [comp]
  {:status 200
   :headers {"Content-Type" "text/html; charset=utf-8"}
   :body "Hello!(｀･ω･´)\n"})

(s/fdef speak
  :args (s/cat :comp ::webapp-handler-component
               :message string?)
  :ret ::response)
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
       :body (-> {:message "Request Timeout."}
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
