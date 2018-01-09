(ns sasara-server.app.webapp.handler-spec
  (:require [clojure.spec.alpha :as s]
            [sasara-server.domain.usecase.speak :as speak]))

(s/def ::status #{200 404 408 204})
(s/def ::headers (s/map-of string? string?))
(s/def ::body string?)
(s/def ::response
  (s/keys :req-un [::status ::headers ::body]))
(s/def ::webapp-handler-component
  (s/keys :req-un [:speak/speak-usecase-component]))
