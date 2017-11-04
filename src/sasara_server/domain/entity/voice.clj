(ns sasara-server.domain.entity.voice
  (:require [clojure.spec.alpha :as s]))

(s/def ::message string?)
(s/def ::link string?)

(s/def ::voice
  (s/keys ::req-un [::message ::link]))
