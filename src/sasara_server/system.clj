(ns sasara-server.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [sasara-server.infra.datasource.pubsub :refer [pubsub-publisher-component pubsub-subscription-component]]
            [sasara-server.infra.repository.intent :refer [intent-repository-component]]
            [sasara-server.infra.repository.voice :refer [voice-repository-component]]
            [sasara-server.domain.usecase.speak :refer [speak-usecase-component]]
            [sasara-server.app.webapp.handler :refer [webapp-handler-component]]
            [sasara-server.app.webapp.endpoint :refer [webapp-endpoint-component]])
  (:gen-class))

(defn sasara-server-system
  [{:keys [sasara-server-webapp-port] :as conf}]
  (component/system-map
    :pubsub-publisher-component (pubsub-publisher-component)
    :pubsub-subscription-component (pubsub-subscription-component)
    :intent-repository-component (component/using (intent-repository-component)
                                                  [:pubsub-publisher-component])

    :voice-repository-component (component/using (voice-repository-component)
                                                 [:pubsub-subscription-component])
    :speak-usecase-component (component/using (speak-usecase-component)
                                              [:intent-repository-component
                                               :voice-repository-component])
    :webapp-handler-component (component/using (webapp-handler-component)
                                               [:speak-usecase-component])
    :webapp-endpoint-component (component/using (webapp-endpoint-component sasara-server-webapp-port)
                                                [:webapp-handler-component])))

(defn load-config []
  {:sasara-server-webapp-port (-> (or (env :sasara-server-webapp-port) "8080") Integer/parseInt)})

(defn -main []
  (component/start
    (sasara-server-system (load-config))))
