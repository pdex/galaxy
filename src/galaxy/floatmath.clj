(ns galaxy.floatmath
  (:use (galaxy constant)))

(def random (java.util.Random.))
(defn LRAND [] (bit-and (. random nextLong) 0x7fffffff))
(defn NRAND [n] (mod (LRAND) n))
(defn FLOATRAND [] (/ (LRAND) MAXRAND))
