(ns galaxy.demo
  (:import (java.awt Color Graphics Dimension))
  (:use (galaxy build constant floatmath render)))

(def a-seed-matrix (build-seed-matrix 0.5 0.25))
(defn a-universe [rot_x rot_y]
  {:rot_x rot_x
   :rot_y rot_y
   :cox   (Math/cos rot_y)
   :six   (Math/sin rot_y)
   :cor   (Math/cos rot_x)
   :sir   (Math/sin rot_x)
   :scale 1
   :midx  250
   :midy  250})
(def this-universe (a-universe 0 0))

(defn third [xs] (nth xs 2))
(defn star-position [d h cosw sinw galaxy-position seed-matrix]
  (position
    (+ (* (first  (first seed-matrix)) d cosw) (* (first  (second seed-matrix)) d sinw) (* (first  (third seed-matrix)) h) (:x galaxy-position))
    (+ (* (second (first seed-matrix)) d cosw) (* (second (second seed-matrix)) d sinw) (* (second (third seed-matrix)) h) (:y galaxy-position))
    (+ (* (third  (first seed-matrix)) d cosw) (* (third  (second seed-matrix)) d sinw) (* (third  (third seed-matrix)) h) (:z galaxy-position))))
(defn star-velocity [v cosw sinw galaxy-velocity seed-matrix]
  (velocity
    (* DELTAT (+ (- (* (first  (first seed-matrix)) v sinw)) (* (first  (second seed-matrix)) v cosw) (:x galaxy-velocity)))
    (* DELTAT (+ (- (* (second (first seed-matrix)) v sinw)) (* (second (second seed-matrix)) v cosw) (:y galaxy-velocity)))
    (* DELTAT (+ (- (* (third  (first seed-matrix)) v sinw)) (* (third  (second seed-matrix)) v cosw) (:z galaxy-velocity)))))
(defn project-point [position]
  (let [scale (:scale this-universe)
        cox (:cox this-universe)
        six (:six this-universe)
        cor (:cor this-universe)
        sir (:sir this-universe)
        midx (:midx this-universe)
        midy (:midy this-universe)
        x (+ midx
            (* scale
              (- (* cox (:x position))
                 (* six (:z position)))))
        y (+ midy
            (* scale
              (- (* cor (:y position))
                (* sir (+ (* six (:x position))
                          (* cox (:z position)))))))]
    (point x y)))
(defn a-star [position velocity]
  (star position velocity (project-point position)))
(defn random-star [galaxy-mass galaxy-position galaxy-size galaxy-velocity seed-matrix]
  (let [w (* 2.0 M_PI (FLOATRAND))
        d (* (FLOATRAND) galaxy-size)
        h (* (if (< (FLOATRAND) 0.5) -1 1) (FLOATRAND) (/ (Math/exp (* -2.0 (/ d galaxy-size))) 5.0) galaxy-size)
        v (Math/sqrt (/ (* galaxy-mass QCONS) (Math/sqrt (+ (* d d) (* h h)))))
        cosw (Math/cos w)
        sinw (Math/sin w)]
    (a-star (star-position d h cosw sinw galaxy-position seed-matrix) (star-velocity v cosw sinw galaxy-velocity seed-matrix))))
(defn a-bunch-of-random-stars [mass position size velocity seed-matrix]
  ;(let [mass (:mass galaxy) position (:position galaxy) size (:size galaxy) velocity (:velocity galaxy)])
  (apply vector (doall
    (map (fn [_] (random-star mass position size velocity seed-matrix)) (range 1000)))))
(defn a-galaxy [color mass position velocity] (galaxy color mass position velocity (a-bunch-of-random-stars mass position 100 velocity a-seed-matrix)))

(def demo
  (universe [
    (a-galaxy Color/red 50 (position 10 20 30) (velocity 1 1 1))
;  (galaxy Color/red 50 (position 10 20 30) (velocity 1 1 1) [
;    (star (position 10 20 30) (velocity 1 1 1) (point 10 20))
;  ])
  ]))

(defn run-demo [] (let [animation (build-animation (build-frame (build-render demo)))] (send-off animator animation)))

;(dosync (alter (first (:stars @(first (:galaxies demo)))) assoc-in [:point :x] 40))
