(ns violet.warp
  (:refer-clojure :exclude [+ * map not char])
  (:require
    [violet.warp.state :as s]
    [violet.warp.macros :as m]))

(def one
  (m/impl-parse state
    (let [c (s/peek state)]
      (if (nil? c)
        (s/put-error state [:one "expected [one], given [eof]"])
        (-> state
            (s/put-result c)
            (s/pop))))))

(defn match
  [t]
  (m/impl-parse state
    (let [c (s/peek state)]
      (if (or (nil? c)
              (not= t c))
        (s/put-error state [:single {:expected t :given c}])
        (-> state
            (s/put-result c)
            (s/pop))))))

#_
(state/-parse (match-char \t) (state/make "time"))

(def eof
  "Matches end of source."
  (m/impl-parse state
    (let [{:keys [source]} state]
      (if (empty? source)
        (s/put-result state :eof)
        (s/put-error state [:eof "missing"])))))

(def bof
  "Matches beginning of source."
  (m/impl-parse state
    (let [{:keys [offset]} state]
      (if (= 0 offset)
        (s/put-result state :bof)
        (s/put-error state [:bof "missing value"])))))

(defn parse
  "A wrapper -parse"
  [parser source]
  (:result (s/-parse parser (s/make source))))

(defn parse!
  [parser source]
  (let [state (s/-parse parser (s/make source))]
    (if (s/error? state)
      (throw (:error state))
      (:result state))))

(defn info
  "Mainly used to debug the result of a parser."
  [parser source]
  (s/-parse parser (s/make source)))
