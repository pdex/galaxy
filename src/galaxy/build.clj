; universe -> galaxies -> stars
;
; app properties
;  * hit iterations (wtf is this?)
;
; viewport properties
;  * height
;  * width
;  * scale
;  * bounds?
;  * origin?
;
;  universe properties
;  * height?
;  * width?
;  * galaxies

(ns galaxy.build
  (:use (galaxy constant floatmath)))

(defstruct Point :x :y)
(defn point [x y] (struct Point x y))

(defstruct Position :x :y :z)
(defn position [x y z] (struct Position x y z))

(defstruct Velocity :x :y :z)
(defn velocity [x y z] (struct Velocity x y z))

(defstruct Star
  :pos    ; current position in 3D
  :vel    ; current velocity in 3D
  :point) ; current point in 2D
(defn star [pos vel point] (ref (struct Star pos vel point)))

(defstruct Galaxy
  :color  ; color to render this galaxy
  :mass   ; mass of this galaxy
  :pos    ; position of this galaxy
  :vel    ; velocity of this galaxy
  :stars) ; stars in this galaxy
(defn galaxy [color mass pos vel stars] (ref (struct Galaxy color mass pos vel stars)))

(defstruct Universe
  :galaxies ; galaxies in this universe
  )
(defn universe [galaxies] (struct Universe galaxies))

; tweaky math stuff
(defn random-seed-matrix []
  (let [w1 (* 2.0 M_PI (FLOATRAND)) sinw1 (Math/sin w1) cosw1 (Math/cos w1)
        w2 (* 2.0 M_PI (FLOATRAND)) sinw2 (Math/sin w2) cosw2 (Math/cos w2)]
    [[cosw2     (* (- sinw1) sinw2) (* cosw1 sinw2)]
     [0.0       cosw1               sinw1]
     [(- sinw2) (* (- sinw1) cosw2) (* cosw1 cosw2)]]))

(defn random-seed-size []
  (+ (* GALAXYRANGESIZE (FLOATRAND)) GALAXYMINSIZE))

(defn random-galaxy-count []
  (max MINGALAXIES (rand-int MAXGALAXIES)))

(defn random-color []
  (COLOR-PALETTE (rand-int (- (count COLOR-PALETTE) 1))))

(defn random-star-count []
  (max MIN-STARS (rand-int MAX-STARS)))
