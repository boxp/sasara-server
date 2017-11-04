(ns sasara-server.infra.repository.voice
  (:import (com.google.pubsub.v1 PubsubMessage))
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :refer [chan put! close!]]
            [cheshire.core :refer [parse-string]]
            [com.stuartsierra.component :as component]
            [sasara-server.infra.datasource.pubsub :as pubsub]
            [sasara-server.domain.entity.voice :as voice]))

(s/def ::message
  #(instance? PubsubMessage %))
(s/def ::channel
  #(instance? (-> (chan) class) %))

(s/def ::voice-repository-component
  (s/keys :req-un [:pubsub/pubsub-subscription-component]))

(def topic-key :sasara-voice)
(def subscription-key :sasara-voice-server)

(s/fdef message->voice
  :args (s/cat :message ::message)
  :ret ::voice/voice)
(defn message->voice
  [message]
  (-> message
      .getData
      .toStringUtf8
      (parse-string true)))

(s/fdef subscribe-voice
  :args (s/cat :c ::voice-repository-component)
  :ret ::channel)
(defn subscribe-voice
  [c]
  (:channel c))

(defrecord VoiceRepositoryComponent []
 component/Lifecycle
 (start [this]
   (let [c (chan 1 (map message->voice))]
    (println ";; Starting VoiceRepositoryComponent")
    (try (pubsub/create-subscription (:pubsub-subscription-component this)
                                     topic-key
                                     subscription-key)
         (catch Exception e
           (println "Info: Already " subscription-key "has exists")))
    (-> this
        (update :pubsub-subscription-component
                #(pubsub/add-subscriber % topic-key subscription-key
                                        (fn [m] (put! c m))))
        (assoc :channel c))))
 (stop [this]
    (println ";; Stopping VoiceRepositoryComponent")
    (when (:channel this) (close! (:channel this)))
    (-> this
        (dissoc :channel))))

(s/fdef voice-repository-component
  :args (s/cat)
  :ret ::voice-repository-component)
(defn voice-repository-component []
  (map->VoiceRepositoryComponent {}))
