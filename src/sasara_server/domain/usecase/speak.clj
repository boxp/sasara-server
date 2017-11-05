(ns sasara-server.domain.usecase.speak
  (:require [clojure.spec.alpha :as s]
            [clojure.core.async :refer [chan put! alts!! timeout]]
            [com.stuartsierra.component :as component]
            [sasara-server.domain.entity.intent :as intent]
            [sasara-server.domain.entity.voice :as voice]
            [sasara-server.infra.repository.intent :as intent-repo]
            [sasara-server.infra.repository.voice :as voice-repo]))

(s/def ::link string?)
(s/def ::channel
  #(instance? (-> (chan) class) %))
(s/def ::speak-usecase-component
  (s/keys :req-un [:intent-repo/intent-repository-component
                   :voice-repo/voice-repository-component]))

(def get-link-timeout 30000)

(s/fdef get-voice
  :args (s/cat :c ::speak-usecase-component
               :message string?)
  :ret (s/nilable ::voice/voice))
(defn get-voice
  [{:keys [intent-repository-component voice-repository-component] :as c}
   message]
  (let [timeout-c (timeout get-link-timeout)
        voice-c (voice-repo/subscribe-voice voice-repository-component message)]
  (do (intent-repo/publish-intent intent-repository-component
                      {:message message})
      (-> (alts!! [timeout-c voice-c])
          first))))

(defrecord SpeakUsecaseComponent []
  component/Lifecycle
  (start [this]
    (println ";; Starting SpeakUsecaseComponent")
    this)
  (stop [this]
    (println ";; Stopping SpeakUsecaseComponent")
    this))

(s/fdef speak-usecase-component
  :args (s/cat)
  :ret ::speak-usecase-component)
(defn speak-usecase-component []
  (map->SpeakUsecaseComponent {}))
