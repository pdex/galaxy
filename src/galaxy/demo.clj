(ns galaxy.demo
  (:import (java.awt Color Graphics Dimension))
  (:use (galaxy build render)))

(def demo
  (universe [
    (galaxy Color/red 50 (position 10 20 30) (velocity 1 1 1) [
      (star (position 10 20 30) (velocity 1 1 1) (point 10 20))
    ])
  ]))

(defn run-demo [] (let [animation (build-animation (build-frame (build-render demo)))] (send-off animator animation)))

;(dosync (alter (first (:stars @(first (:galaxies demo)))) assoc-in [:point :x] 40))
