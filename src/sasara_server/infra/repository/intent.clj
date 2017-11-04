(ns sasara-server.infra.repository.intent
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :refer [chan put!]]
            [cheshire.core :refer [generate-string]]
            [com.stuartsierra.component :as component]
            [sasara-server.infra.datasource.pubsub :as pubsub]
            [sasara-server.domain.entity.intent :as intent]))

(s/def ::channel
  #(instance? (-> (chan) class) %))

(s/def ::intent-repository-component
  (s/keys :req-un [:pubsub/pubsub-publisher-component]))

(def topic-key :sasara-intent)

(s/fdef publish-intent
  :args (s/cat :comp ::intent-repository-component
               :intent ::intent/intent)
  :ret ::channel)
(defn publish-intent
  [{:keys [pubsub-publisher-component] :as comp}
   intent]
  (let [c (chan)]
    (pubsub/publish pubsub-publisher-component
                    topic-key
                    (generate-string intent)
                    #(put! c %)
                    #(put! c (ex-info "Publish intent failed."
                                      {:intent intent
                                       :error-message (.getMessage %)})))
    c))

(defrecord IntentRepositoryComponent []
  component/Lifecycle
  (start [this]
    (println ";; Starting IntentRepositoryComponent")
    (-> this
        (update :pubsub-publisher-component
                #(pubsub/create-publisher % topic-key))))
  (stop [this]
    (println ";; Stopping IntentRepositoryComponent")
    this))

(s/fdef intent-repository-component
  :args (s/cat)
  :ret ::intent-repository-component)
(defn intent-repository-component
  []
  (map->IntentRepositoryComponent {}))
