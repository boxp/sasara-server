(ns sasara-server.system
  (:require [com.stuartsierra.component :as component]
            [environ.core :refer [env]]
            [sasara-server.infra.datasource.example :refer [example-datasource-component]]
            [sasara-server.infra.repository.example :refer [example-repository-component]]
            [sasara-server.domain.usecase.example :refer [example-usecase-component]]
            [sasara-server.app.my-webapp.handler :refer [my-webapp-handler-component]]
            [sasara-server.app.my-webapp.endpoint :refer [my-webapp-endpoint-component]])
  (:gen-class))

(defn sasara-server-system
  [{:keys [sasara-server-example-port
           sasara-server-my-webapp-port] :as conf}]
  (component/system-map
    :example-datasource (example-datasource-component sasara-server-example-port)
    :example-repository (component/using
                          (example-repository-component)
                          [:example-datasource])
    :example-usecase (component/using
                       (example-usecase-component)
                       [:example-repository])
    :my-webapp-handler (component/using
                         (my-webapp-handler-component)
                         [:example-usecase])
    :my-webapp-endpoint (component/using
                          (my-webapp-endpoint-component sasara-server-my-webapp-port)
                          [:my-webapp-handler])))

(defn load-config []
  {:sasara-server-example-port (-> (or (env :sasara-server-example-port) "8000") Integer/parseInt)
   :sasara-server-my-webapp-port (-> (or (env :sasara-server-my-webapp-port) "8080") Integer/parseInt)})

(defn -main []
  (component/start
    (sasara-server-system (load-config))))
