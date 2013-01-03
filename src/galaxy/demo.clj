(ns galaxy.demo
  (:import (java.awt Color Graphics Dimension))
  (:use (galaxy build render)))

(def demo (universe [(galaxy Color/red [(star (position 10 20 30) (velocity 1 1 1) (point 10 20))])]))
